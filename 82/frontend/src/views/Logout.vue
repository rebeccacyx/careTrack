<template>
  <div class="logout-container">
    <a-card style="max-width: 500px; margin: 0 auto; text-align: center;">
      <template #title>
        <div style="display: flex; align-items: center; justify-content: center; gap: 12px;">
          <LogoutOutlined style="font-size: 24px; color: #ff4d4f;" />
          <span style="font-size: 20px; font-weight: 600;">Logout</span>
        </div>
      </template>
      
      <div style="padding: 20px 0;">
        <div style="margin-bottom: 24px;">
          <a-avatar :size="80" style="background-color: #f5f5f5; margin-bottom: 16px;">
            <template #icon><UserOutlined /></template>
          </a-avatar>
          <div style="font-size: 16px; color: #666; margin-bottom: 8px;">
            {{ userName }}
          </div>
          <a-tag :color="getRoleColor()" style="margin-bottom: 16px;">
            {{ getRoleDisplayName() }}
          </a-tag>
        </div>
        
        <a-typography-paragraph style="font-size: 16px; color: #333; margin-bottom: 24px;">
          Are you sure you want to logout from the system?
        </a-typography-paragraph>
        
        <a-alert
          message="Logout Notice"
          description="You will be redirected to the login page and all your session data will be cleared."
          type="info"
          show-icon
          style="margin-bottom: 24px; text-align: left;"
        />
        
        <div style="display: flex; gap: 12px; justify-content: center;">
          <a-button size="large" @click="goBack">
            Cancel
          </a-button>
          <a-button type="primary" danger size="large" @click="confirmLogout" :loading="loggingOut">
            <template #icon><LogoutOutlined /></template>
            Logout
          </a-button>
        </div>
      </div>
    </a-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { LogoutOutlined, UserOutlined } from '@ant-design/icons-vue'
import { logout, getMe } from '@/services/userService'
import { message } from 'ant-design-vue'

const router = useRouter()
const loggingOut = ref(false)
const userName = ref('User')
const userRole = ref('user')

// Get role display name
const getRoleDisplayName = () => {
  switch (userRole.value) {
    case 'poa':
      return 'Power of Attorney / Family Member'
    case 'manager':
      return 'Manager'
    case 'worker':
      return 'Worker'
    default:
      return userRole.value
  }
}

// Get role color for tag
const getRoleColor = () => {
  switch (userRole.value) {
    case 'poa':
      return 'purple'
    case 'manager':
      return 'blue'
    case 'worker':
      return 'green'
    default:
      return 'default'
  }
}

// Go back to previous page
const goBack = () => {
  router.go(-1)
}

// Confirm logout
const confirmLogout = async () => {
  loggingOut.value = true
  
  try {
    // Add a small delay for better UX
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    // Call logout function
    logout()
    
  } catch (error) {
    console.error('Logout error:', error)
    message.error('Logout failed. Please try again.')
    loggingOut.value = false
  }
}

// Load user information
onMounted(async () => {
  try {
    const userInfo = await getMe()
    userName.value = userInfo?.data?.name || 'User'
    userRole.value = userInfo?.data?.role || 'user'
  } catch (error) {
    console.error('Failed to load user info:', error)
  }
})
</script>

<style scoped>
.logout-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.ant-card {
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border-radius: 12px;
}

.ant-card-head {
  border-bottom: 1px solid #f0f0f0;
  padding: 20px 24px;
}

.ant-card-body {
  padding: 24px;
}
</style>
