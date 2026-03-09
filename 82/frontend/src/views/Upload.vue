<template>
  <div class="upload-page">
    <a-card>
      <template #title>
        <div style="display: flex; align-items: center; gap: 8px;">
          <h2>All Uploaded Files</h2>
          <a-tooltip title="File upload page for uploading and managing documents, images and other materials" placement="top">
            <QuestionCircleOutlined style="color: #999; cursor: help;" />
          </a-tooltip>
        </div>
      </template>

      <!-- Upload button -->
      <div style="text-align: right; margin-bottom: 16px;">
        <a-upload
          :show-upload-list="false"
          :before-upload="handleBeforeUpload"
        >
          <a-button type="primary">+ Upload File</a-button>
        </a-upload>
      </div>

      <!-- File table -->
      <a-table :columns="columns" :data-source="files" row-key="id" bordered>
        <template #bodyCell="{ column, record }">
          <!-- Action buttons -->
          <template v-if="column.dataIndex === 'actions'">
            <a-space>
              <a-button size="small" @click="openCommentModal(record)">comment</a-button>
              <a-button size="small" @click="viewFile(record)">View</a-button>
            </a-space>
          </template>
          <!-- Comment -->
          <template v-else-if="column.dataIndex === 'comment'">
            <span>{{ record.comment || '-' }}</span>
          </template>
        </template>
      </a-table>
    </a-card>

    <!-- Comment modal -->
    <a-modal
      v-model:open="isCommentModalOpen"
      title="Add Comment"
      @ok="saveComment"
      @cancel="cancelComment"
    >
      <a-textarea
        v-model:value="currentComment"
        placeholder="Write your comment here..."
        rows="4"
      />
    </a-modal>

    <!-- File view modal -->
    <a-modal
      v-model:open="isViewModalOpen"
      :title="currentViewFile?.name || 'View File'"
      width="90%"
      :footer="null"
    >
      <div v-if="currentViewFile">
        <!-- 图片预览 -->
        <div v-if="isImageFile(currentViewFile.name)" style="text-align: center;">
          <img 
            :src="currentViewFile.fileUrl" 
            style="max-width: 100%; max-height: 70vh; border-radius: 8px;"
            alt="Preview"
          />
        </div>
        <!-- PDF预览 -->
        <div v-else-if="isPdfFile(currentViewFile.name)" style="width: 100%; height: 70vh;">
          <iframe
            :src="currentViewFile.fileUrl"
            style="width: 100%; height: 100%; border: none; border-radius: 8px;"
            type="application/pdf"
          ></iframe>
        </div>
        <!-- 其他文件类型 -->
        <div v-else style="padding: 40px; text-align: center;">
          <div style="font-size: 48px; color: #1890ff; margin-bottom: 16px;">📄</div>
          <h3>{{ currentViewFile.name }}</h3>
          <p style="color: #666; margin: 16px 0;">This file type cannot be previewed in the browser.</p>
          <a-button type="primary" @click="downloadFile(currentViewFile)" style="margin-top: 16px;">
            <template #icon>
              <DownloadOutlined />
            </template>
            Download File
          </a-button>
        </div>
        
        <!-- 文件信息和操作按钮 -->
        <div style="margin-top: 20px; padding-top: 20px; border-top: 1px solid #f0f0f0; display: flex; justify-content: space-between; align-items: center;">
          <div style="color: #666; font-size: 14px;">
            <span v-if="currentViewFile.size">
              Size: {{ formatFileSize(currentViewFile.size) }}
            </span>
            <span v-if="currentViewFile.contentType" style="margin-left: 16px;">
              Type: {{ currentViewFile.contentType }}
            </span>
          </div>
          <a-space>
            <a-button @click="downloadFile(currentViewFile)">
              <template #icon>
                <DownloadOutlined />
              </template>
              Download
            </a-button>
            <a-button type="primary" @click="openInNewTab(currentViewFile)">
              <template #icon>
                <ExportOutlined />
              </template>
              Open in New Tab
            </a-button>
          </a-space>
        </div>
      </div>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { QuestionCircleOutlined, DownloadOutlined, ExportOutlined } from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'
import { getMe } from '@/services/userService'
import { uploadFile, getAllFiles, updateFileComment } from '@/services/fileService'
import { getBaseUrl } from '@/services/api'

// Files list - loaded from API
const files = ref([])

const currentUser = ref(null)

// Load user info and files on mount
onMounted(async () => {
  try {
    const userInfo = await getMe()
    currentUser.value = userInfo?.data
    // Load files from API
    await loadFiles()
  } catch (error) {
    console.error('Failed to get user info:', error)
  }
})

// Helper function to get full file URL
// If static resources fail, use the API endpoint as fallback
const getFullFileUrl = (fileUrl) => {
  if (!fileUrl) return ''
  // If already a full URL, return as is
  if (fileUrl.startsWith('http://') || fileUrl.startsWith('https://')) {
    return fileUrl
  }
  
  const API_BASE_URL = getBaseUrl()
  
  // Use API endpoint for serving files (more reliable than static resources)
  // This ensures files are accessible even if static resource mapping fails
  if (fileUrl.startsWith('/uploads/')) {
    return `${API_BASE_URL}/api/files/serve?path=${encodeURIComponent(fileUrl)}`
  }
  
  // Fallback to direct URL
  return `${API_BASE_URL}${fileUrl}`
}

// Load files from API
const loadFiles = async () => {
  try {
    const response = await getAllFiles()
    if (response?.data) {
      // Map backend FileRecord to frontend format
      files.value = response.data.map(file => ({
        id: file.id,
        name: file.name,
        category: file.category || 'General Upload',
        uploadedBy: file.uploadedBy || 'Unknown',
        time: file.createdAt ? new Date(file.createdAt).toLocaleDateString() : '',
        comment: file.comment || '',
        fileUrl: getFullFileUrl(file.fileUrl),
        contentType: file.contentType,
        size: file.size
      }))
    }
  } catch (error) {
    console.error('Failed to load files:', error)
  }
}

// Table
const columns = [
  { title: 'File name', dataIndex: 'name' },
  { title: 'Category', dataIndex: 'category' },
  { title: 'Uploaded By', dataIndex: 'uploadedBy' },
  { title: 'Time', dataIndex: 'time' },
  { title: 'Comment', dataIndex: 'comment' },
  { title: 'Actions', dataIndex: 'actions' },
]

// Upload file
const handleBeforeUpload = async (file) => {
  try {
    // Get current user info
    const userInfo = await getMe()
    if (!userInfo?.data?.id) {
      message.error('User not authenticated')
      return false
    }
    
    // Determine category based on user role
    let category = 'General Upload'
    if (userInfo.data.role === 'worker') {
      category = 'Worker Upload'
    } else if (userInfo.data.role === 'manager') {
      category = 'Manager Upload'
    } else if (userInfo.data.role === 'poa') {
      category = 'POA Upload'
    }
    
    // Upload file via API
    const uploadResponse = await uploadFile(file, {
      category: category,
      uploadedBy: userInfo.data.name || userInfo.data.email || 'Unknown',
      comment: ''
    })
    
    if (uploadResponse.data) {
      message.success('File uploaded successfully')
      // Reload files list to show the new file
      await loadFiles()
    }
  } catch (error) {
    console.error('Failed to upload file:', error)
    message.error(error.message || 'Failed to upload file')
  }
  
  return false // Prevent default upload
}

// Comment modal logic
const isCommentModalOpen = ref(false)
const currentFile = ref(null)
const currentComment = ref('')

const openCommentModal = (record) => {
  currentFile.value = record
  currentComment.value = record.comment || ''
  isCommentModalOpen.value = true
}

const saveComment = async () => {
  if (currentFile.value) {
    try {
      await updateFileComment(currentFile.value.id, currentComment.value)
      currentFile.value.comment = currentComment.value
      message.success('Comment updated successfully')
      // Reload files to get updated data
      await loadFiles()
    } catch (error) {
      console.error('Failed to update comment:', error)
      message.error(error.message || 'Failed to update comment')
    }
  }
  isCommentModalOpen.value = false
}

const cancelComment = () => {
  isCommentModalOpen.value = false
}

// File view modal logic
const isViewModalOpen = ref(false)
const currentViewFile = ref(null)

// View file
const viewFile = (record) => {
  currentViewFile.value = record
  isViewModalOpen.value = true
}

// Check if file is an image
const isImageFile = (filename) => {
  const imageExtensions = ['.jpg', '.jpeg', '.png', '.gif', '.bmp', '.webp', '.svg']
  const extension = filename.toLowerCase().substring(filename.lastIndexOf('.'))
  return imageExtensions.includes(extension)
}

// 判断是否为PDF文件
const isPdfFile = (filename) => {
  const extension = filename.toLowerCase().substring(filename.lastIndexOf('.'))
  return extension === '.pdf'
}

// 下载文件
const downloadFile = (file) => {
  try {
    const link = document.createElement('a')
    link.href = file.fileUrl
    link.download = file.name
    link.target = '_blank'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    message.success('File download started')
  } catch (error) {
    console.error('Failed to download file:', error)
    message.error('Failed to download file')
  }
}

// 在新标签页打开文件
const openInNewTab = (file) => {
  window.open(file.fileUrl, '_blank')
}

// 格式化文件大小
const formatFileSize = (bytes) => {
  if (!bytes || bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i]
}
</script>

<style scoped>
.upload-page {
  max-width: 1200px;
  margin: 0 auto;
}
</style>
