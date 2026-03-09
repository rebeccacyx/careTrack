<template>
    <a-layout style="min-height: 100vh; background: #f5f7fa;">
      <!-- left menu -->
      <a-layout-sider :width="280" theme="light" class="custom-sider">
        <!-- User Info Section -->
        <div class="user-info-section">
          <div class="user-info-content">
            <a-avatar :size="64" class="user-avatar" :style="{ background: getRoleGradient() }">
              <template #icon><UserOutlined style="font-size: 32px;" /></template>
            </a-avatar>
            <div class="user-name">{{ userName }}</div>
            <a-tag :color="getRoleColor()" class="user-role-tag">
              {{ getRoleDisplayName() }}
            </a-tag>
          </div>
        </div>
        
        <a-menu
          v-model:selectedKeys="selectedKeys"
          mode="inline"
          @click="onMenuClick"
          class="custom-menu"
        >
          <a-menu-item key="home" class="menu-item">
            <template #icon><HomeOutlined /></template>
            <span>Home</span>
            <a-tooltip title="Return to main page, view system overview and quick actions" placement="right">
              <QuestionCircleOutlined class="menu-help-icon" />
            </a-tooltip>
          </a-menu-item>
          
          <!-- Manager and Worker Management -->
          <a-menu-item v-if="userRole === 'manager'" key="worker-management" class="menu-item">
            <template #icon><AppstoreOutlined /></template>
            <span>Worker Management</span>
            <a-tooltip title="Manage care staff, including inviting Workers to join the team" placement="right">
              <QuestionCircleOutlined class="menu-help-icon" />
            </a-tooltip>
          </a-menu-item>
          
          <!-- Manager and Power of Attorney/Family Member Budget -->
          <a-menu-item v-if="userRole === 'manager' || userRole === 'poa'" key="budget" class="menu-item">
            <template #icon><BarChartOutlined /></template>
            <span>Budget</span>
            <a-tooltip :title="getBudgetTooltip()" placement="right">
              <QuestionCircleOutlined class="menu-help-icon" />
            </a-tooltip>
          </a-menu-item>
          
          <!-- All roles Tasks -->
          <a-menu-item key="tasks" class="menu-item">
            <template #icon><CheckSquareOutlined /></template>
            <span>Tasks</span>
            <a-tooltip :title="getTasksTooltip()" placement="right">
              <QuestionCircleOutlined class="menu-help-icon" />
            </a-tooltip>
          </a-menu-item>
          
          <!-- All roles Upload -->
          <a-menu-item key="upload" class="menu-item">
            <template #icon><SettingOutlined /></template>
            <span>Upload</span>
            <a-tooltip title="Upload and manage files, including documents, images and other materials" placement="right">
              <QuestionCircleOutlined class="menu-help-icon" />
            </a-tooltip>
          </a-menu-item>
          
          <!-- Manager and Power of Attorney/Family Member Communication -->
          <a-menu-item v-if="userRole === 'manager' || userRole === 'poa'" key="communication" class="menu-item">
            <template #icon><MessageOutlined /></template>
            <span>Communication</span>
            <a-tooltip :title="getCommunicationTooltip()" placement="right">
              <QuestionCircleOutlined class="menu-help-icon" />
            </a-tooltip>
          </a-menu-item>
          
          <!-- Power of Attorney/Family Member Carer Team -->
          <a-menu-item v-if="userRole === 'poa'" key="carer-team" class="menu-item">
            <template #icon><AppstoreOutlined /></template>
            <span>Carer Team</span>
            <a-tooltip title="View and manage care team information, invite organizations to join" placement="right">
              <QuestionCircleOutlined class="menu-help-icon" />
            </a-tooltip>
          </a-menu-item>
          
          <!-- All roles Setting -->
          <a-menu-item key="setting" class="menu-item">
            <template #icon><SettingOutlined /></template>
            <span>Setting</span>
            <a-tooltip :title="getSettingTooltip()" placement="right">
              <QuestionCircleOutlined class="menu-help-icon" />
            </a-tooltip>
          </a-menu-item>
          
          <!-- Logout -->
          <a-menu-item key="logout" class="menu-item logout-item">
            <template #icon><LogoutOutlined /></template>
            <span>Logout</span>
            <a-tooltip title="Logout from the system" placement="right">
              <QuestionCircleOutlined class="menu-help-icon" />
            </a-tooltip>
          </a-menu-item>
        </a-menu>
      </a-layout-sider>
  
      <!-- right content -->
      <a-layout>
        <!-- Header with help button, notifications and logout -->
        <a-layout-header class="custom-header">
          <!-- Left side - User info -->
          <div class="header-left">
            <div class="welcome-text">
              Welcome, <span class="user-name-highlight">{{ userName }}</span>
            </div>
            <a-tag :color="getRoleColor()" class="header-role-tag">
              {{ getRoleDisplayName() }}
            </a-tag>
          </div>
          
          <!-- Right side - Actions -->
          <div class="header-actions">
            <!-- Notification Bell -->
            <a-badge 
              :count="unreadNotificationsCount" 
              :offset="[10, 0]"
              :show-zero="false"
            >
              <a-tooltip title="View notifications and alerts">
                <a-button type="text" @click="showNotificationModal" class="header-action-btn">
                  <template #icon><BellOutlined /></template>
                </a-button>
              </a-tooltip>
            </a-badge>
            
            <!-- Guide Button -->
            <a-tooltip title="View setup guide for your role">
              <a-button type="text" @click="showGuide" class="header-action-btn">
                <template #icon><QuestionCircleOutlined /></template>
                Guide
              </a-button>
            </a-tooltip>
            
            <!-- Help Button (original help modal) -->
            <a-tooltip title="View system help information">
              <a-button type="text" @click="showHelpModal" class="header-action-btn">
                <template #icon><InfoCircleOutlined /></template>
                Help
              </a-button>
            </a-tooltip>
            
            <!-- Logout Button -->
            <a-tooltip title="Logout from the system">
              <a-button type="text" @click="handleLogout" class="header-action-btn logout-btn">
                <template #icon><LogoutOutlined /></template>
                Logout
              </a-button>
            </a-tooltip>
          </div>
        </a-layout-header>
        
        <a-layout-content class="custom-content">
          <div class="content-wrapper">
            <router-view />
          </div>
        </a-layout-content>
      </a-layout>
    </a-layout>

    <!-- Help Modal -->
    <a-modal
      v-model:open="helpModalVisible"
      title="Help Information"
      :footer="null"
      width="600px"
    >
      <div style="line-height: 1.6;">
        <h4>System Feature Introduction:</h4>
        <ul>
          <li><strong>Home:</strong> Return to main page, view system overview and quick actions</li>
          <li v-if="userRole === 'manager'"><strong>Worker Management:</strong> Manage care staff, including inviting Workers to join the team</li>
          <li v-if="userRole === 'manager' || userRole === 'poa'"><strong>Budget:</strong> {{ getBudgetTooltip() }}</li>
          <li><strong>Tasks:</strong> {{ getTasksTooltip() }}</li>
          <li><strong>Upload:</strong> Upload and manage files, including documents, images and other materials</li>
          <li v-if="userRole === 'manager' || userRole === 'poa'"><strong>Communication:</strong> {{ getCommunicationTooltip() }}</li>
          <li v-if="userRole === 'poa'"><strong>Carer Team:</strong> View and manage care team information, invite organizations to join</li>
          <li><strong>Setting:</strong> {{ getSettingTooltip() }}</li>
          <li><strong>Logout:</strong> Logout from the system and return to login page</li>
        </ul>
        <p style="margin-top: 16px; color: #666;">
          <strong>Tip:</strong> Hover over the question mark icon on menu items to view detailed descriptions.
        </p>
      </div>
    </a-modal>

    <!-- Guide Component -->
    <Guide ref="guideRef" />

    <!-- Notification Modal -->
    <a-modal
      v-model:open="notificationModalVisible"
      title="Notifications & Alerts"
      width="600px"
      :footer="null"
    >
      <div style="max-height: 500px; overflow-y: auto;">
        <div v-if="notifications.length === 0" style="text-align: center; padding: 40px; color: #999;">
          <BellOutlined style="font-size: 48px; margin-bottom: 16px; color: #d9d9d9;" />
          <p>No notifications</p>
        </div>
        
        <div v-else>
          <div v-for="notification in notifications" :key="notification.id" 
               style="margin-bottom: 16px; padding: 12px; border-radius: 6px; border-left: 4px solid;"
               :style="{ 
                 backgroundColor: getNotificationBgColor(notification.type),
                 borderLeftColor: getNotificationColor(notification.type)
               }">
            <div style="display: flex; justify-content: space-between; align-items: flex-start;">
              <div style="flex: 1;">
                <div style="display: flex; align-items: center; gap: 8px; margin-bottom: 4px;">
                  <span :style="{ color: getNotificationColor(notification.type) }">
                    {{ getNotificationIcon(notification.type) }}
                  </span>
                  <span style="font-weight: 600; font-size: 14px;">
                    {{ notification.title }}
                  </span>
                  <a-tag :color="getNotificationTagColor(notification.type)" size="small">
                    {{ notification.type.toUpperCase() }}
                  </a-tag>
                </div>
                <p style="margin: 0; color: #666; font-size: 13px; line-height: 1.4;">
                  {{ notification.message }}
                </p>
                <div style="margin-top: 8px; font-size: 12px; color: #999;">
                  {{ formatNotificationTime(notification.createdAt || notification.timestamp) }}
                </div>
              </div>
              <a-button 
                v-if="!notification.isRead" 
                type="text" 
                size="small" 
                @click="markAsRead(notification.id)"
                style="color: #1890ff;"
              >
                Mark as read
              </a-button>
            </div>
          </div>
        </div>
      </div>
      
      <div v-if="notifications.length > 0" style="margin-top: 16px; text-align: center; border-top: 1px solid #f0f0f0; padding-top: 16px;">
        <a-button type="primary" @click="markAllAsRead" :disabled="unreadNotificationsCount === 0">
          Mark All as Read
        </a-button>
      </div>
    </a-modal>
  </template>
  
  <script setup>
  import { ref, onMounted, watchEffect, computed } from 'vue'
  import { useRouter, useRoute } from 'vue-router'
  import {
    HomeOutlined, CheckSquareOutlined, BarChartOutlined,
    SettingOutlined, MessageOutlined, AppstoreOutlined, UserOutlined,
    QuestionCircleOutlined, BellOutlined, LogoutOutlined, InfoCircleOutlined
  } from '@ant-design/icons-vue'
  import { message } from 'ant-design-vue'
  import { getMe, logout } from '@/services/userService'
  import { 
    getMyNotifications, 
    markAsRead as markNotificationAsRead, 
    markAllAsRead as markAllNotificationsAsRead 
  } from '@/services/notificationService'
  import Guide from './Guide.vue'
  
  const selectedKeys = ref(['home'])
  const userRole = ref('worker')
  const userName = ref('Test User')
  const helpModalVisible = ref(false)
  const firstVisitModalVisible = ref(false)
  const notificationModalVisible = ref(false)
  const guideRef = ref(null)
  
  onMounted(async () => {
    try {
      console.log('Layout component mounted, fetching user info...')
      const userInfo = await getMe()
      console.log('User info received:', userInfo)
      userRole.value = userInfo?.data?.role || 'worker'
      userName.value = userInfo?.data?.name || 'Test User'
      console.log('Set userRole to:', userRole.value)
      console.log('Set userName to:', userName.value)
      
      // Check if this is the first visit and show guide (pass userInfo to avoid duplicate call)
      checkFirstVisit(userInfo)
      
      // Load notifications from backend API
      await loadNotifications()
    } catch (e) {
      console.error('Failed to get user info:', e)
    }
  })
  
  // Check if this is the user's first visit
  const checkFirstVisit = async (userInfo) => {
    try {
      // Use provided userInfo or fetch if not provided
      const userData = userInfo || await getMe()
      const userRoleFromAPI = userData?.data?.role
      
      if (!userRoleFromAPI) {
        console.log('No user role found, skipping guide')
        return
      }
      
      // Check if user has seen guide for their role
      const seenGuides = JSON.parse(localStorage.getItem('user_guide_seen') || '{}')
      const hasSeenGuide = seenGuides[userRoleFromAPI]
      
      console.log('Guide check - Role:', userRoleFromAPI, 'Has seen:', hasSeenGuide)
      
      // Only show guide once - on the first visit for this role
      if (!hasSeenGuide) {
        // First time seeing guide for this role - auto show
        // Use longer delay to ensure component is fully mounted and UI is ready
        setTimeout(() => {
          if (guideRef.value) {
            console.log('First visit detected, showing guide for role:', userRoleFromAPI)
            guideRef.value.show(true) // true = autoShow
          } else {
            // If ref is not ready yet, try again after a short delay
            console.log('Guide ref not ready, retrying...')
            setTimeout(() => {
              if (guideRef.value) {
                console.log('Retrying to show guide for role:', userRoleFromAPI)
                guideRef.value.show(true)
              } else {
                console.warn('Guide ref still not available after retry')
              }
            }, 500)
          }
        }, 1000) // Increased delay to 1000ms for better reliability
      } else {
        console.log('User has already seen guide for role:', userRoleFromAPI)
      }
    } catch (error) {
      console.error('Failed to check first visit:', error)
      // On error, don't show the modal to avoid annoying users
    }
  }

  // Show guide manually (from Guide button)
  const showGuide = () => {
    if (guideRef.value) {
      guideRef.value.show()
    }
  }
  
  // Show help modal
  const showHelpModal = () => {
    helpModalVisible.value = true
  }
  
  // Handle logout
  const handleLogout = () => {
    console.log('Logout button clicked');
    logout()
  }
  
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

  const getRoleGradient = () => {
    switch (userRole.value) {
      case 'poa':
        return 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
      case 'manager':
        return 'linear-gradient(135deg, #1890ff 0%, #096dd9 100%)'
      case 'worker':
        return 'linear-gradient(135deg, #52c41a 0%, #389e0d 100%)'
      default:
        return 'linear-gradient(135deg, #8c8c8c 0%, #595959 100%)'
    }
  }
  
  // Get dynamic tooltips based on user role
  const getTasksTooltip = () => {
    switch (userRole.value) {
      case 'manager':
        return 'Task management: Assign tasks to care staff, monitor task execution'
      case 'worker':
        return 'Task management: View assigned tasks and execute them'
      case 'poa':
        return 'Task management: View all task execution status, approve important tasks'
      default:
        return 'Task management page'
    }
  }
  
  const getSettingTooltip = () => {
    switch (userRole.value) {
      case 'poa':
        return 'Settings page: Fill in patient information and special requirements, manage personal basic information and preferences'
      case 'worker':
      case 'manager':
        return 'Settings page: View patient information and special requirements, manage personal basic information and preferences'
      default:
        return 'Settings page: Manage personal basic information and preferences'
    }
  }
  
  const getCommunicationTooltip = () => {
    switch (userRole.value) {
      case 'manager':
        return 'Communication with patient agents/family members, send messages and notifications'
      case 'poa':
        return 'Communication with managers, receive messages and notifications'
      default:
        return 'Communication page'
    }
  }
  
  const getBudgetTooltip = () => {
    switch (userRole.value) {
      case 'manager':
        return 'View and manage budget information, including expense statistics and financial reports'
      case 'poa':
        return 'Power of Attorney/Family Members can modify and manage budgets'
      default:
        return 'Budget management page'
    }
  }
  
  const router = useRouter()
  const route = useRoute()
  
  // Route mapping
  const keyToPath = {
    home: '/app/menu',
    tasks: '/app/tasks',
    'carer-team': '/app/carer-team',
    budget: '/app/budget',
    upload: '/app/upload',
    setting: '/app/setting',
    logout: '/app/logout',
    'worker-management': '/app/worker-management',
    communication: '/app/communication',
  }
  
  // Auto highlight when route changes
  watchEffect(() => {
    const pathToKey = Object.fromEntries(Object.entries(keyToPath).map(([k, v]) => [v, k]))
    const k = pathToKey[route.path] || 'home'
    selectedKeys.value = [k]
  })
  
  const onMenuClick = ({ key }) => {
    const path = keyToPath[key]
    if (path) router.push(path)
  }

  // Notification data loaded from backend API
  const allNotifications = ref([])

  // Get notifications - now loaded from API
  const notifications = computed(() => {
    return allNotifications.value || []
  })

  // Computed property for unread notifications count
  const unreadNotificationsCount = computed(() => {
    const count = notifications.value.filter(n => !n.isRead).length
    console.log('Unread notifications count:', count)
    return count
  })

  // Notification functions
  const showNotificationModal = () => {
    notificationModalVisible.value = true
  }

  // Load notifications from backend API
  const loadNotifications = async () => {
    try {
      const response = await getMyNotifications()
      if (response?.data) {
        allNotifications.value = response.data
        console.log('Loaded notifications from backend:', allNotifications.value.length)
      }
    } catch (error) {
      console.error('Failed to load notifications:', error)
      // Set empty array on error
      allNotifications.value = []
    }
  }

  const markAsRead = async (notificationId) => {
    try {
      const response = await markNotificationAsRead(notificationId)
      if (response?.data) {
        // Update local notification state
        const notification = allNotifications.value.find(n => n.id === notificationId)
        if (notification) {
          notification.isRead = true
        }
        console.log('Marked notification as read:', notificationId)
        message.success('Notification marked as read')
      }
    } catch (error) {
      console.error('Failed to mark notification as read:', error)
      message.error('Failed to mark notification as read')
    }
  }

  const markAllAsRead = async () => {
    try {
      const response = await markAllNotificationsAsRead()
      if (response?.data !== undefined) {
        const markedCount = response.data
        console.log('Marked all notifications as read, count:', markedCount)
        
        // Update all notifications to read
        allNotifications.value.forEach(n => n.isRead = true)
        
        // Show success message
        if (markedCount > 0) {
          message.success(`All ${markedCount} notifications marked as read`)
        } else {
          message.info('No unread notifications to mark')
        }
      }
    } catch (error) {
      console.error('Failed to mark all notifications as read:', error)
      message.error('Failed to mark all notifications as read')
    }
  }

  const getNotificationColor = (type) => {
    switch (type) {
      case 'error': return '#ff4d4f'
      case 'warning': return '#fa8c16'
      case 'info': return '#1890ff'
      case 'success': return '#52c41a'
      default: return '#666'
    }
  }

  const getNotificationBgColor = (type) => {
    switch (type) {
      case 'error': return '#fff2f0'
      case 'warning': return '#fff7e6'
      case 'info': return '#e6f7ff'
      case 'success': return '#f6ffed'
      default: return '#fafafa'
    }
  }

  const getNotificationTagColor = (type) => {
    switch (type) {
      case 'error': return 'red'
      case 'warning': return 'orange'
      case 'info': return 'blue'
      case 'success': return 'green'
      default: return 'default'
    }
  }

  const getNotificationIcon = (type) => {
    switch (type) {
      case 'error': return '🚨'
      case 'warning': return '⚠️'
      case 'info': return 'ℹ️'
      case 'success': return '✅'
      default: return '📢'
    }
  }

  const formatNotificationTime = (timestamp) => {
    if (!timestamp) return 'Unknown'
    
    const now = new Date()
    // Handle both Date objects and string timestamps
    const notificationDate = timestamp instanceof Date ? timestamp : new Date(timestamp)
    const diff = now - notificationDate
    const hours = Math.floor(diff / (1000 * 60 * 60))
    const minutes = Math.floor(diff / (1000 * 60))
    
    if (hours > 24) {
      return `${Math.floor(hours / 24)} days ago`
    } else if (hours > 0) {
      return `${hours} hours ago`
    } else if (minutes > 0) {
      return `${minutes} minutes ago`
    } else {
      return 'Just now'
    }
  }
  </script>

<style scoped>
/* Custom Sider - Premium */
.custom-sider {
  box-shadow: 4px 0 20px rgba(0, 0, 0, 0.08), -2px 0 10px rgba(102, 126, 234, 0.1);
  background: rgba(255, 255, 255, 0.95) !important;
  backdrop-filter: blur(20px) saturate(180%);
  -webkit-backdrop-filter: blur(20px) saturate(180%);
  border-right: 1px solid rgba(226, 232, 240, 0.8);
  position: relative;
  z-index: 100;
}

.custom-sider::after {
  content: '';
  position: absolute;
  top: 0;
  right: 0;
  width: 1px;
  height: 100%;
  background: linear-gradient(180deg, transparent 0%, rgba(102, 126, 234, 0.3) 50%, transparent 100%);
}

/* User Info Section - Premium */
.user-info-section {
  padding: 32px 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-bottom: none;
  position: relative;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(102, 126, 234, 0.3);
}

.user-info-section::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.15) 0%, transparent 70%);
  animation: pulse 6s ease-in-out infinite;
}

.user-info-section::after {
  content: '';
  position: absolute;
  bottom: -30%;
  left: -30%;
  width: 150%;
  height: 150%;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.1) 0%, transparent 70%);
  animation: pulse 8s ease-in-out infinite reverse;
}

@keyframes pulse {
  0%, 100% { 
    transform: scale(1) rotate(0deg); 
    opacity: 0.4; 
  }
  50% { 
    transform: scale(1.2) rotate(180deg); 
    opacity: 0.8; 
  }
}

.user-info-content {
  text-align: center;
  position: relative;
  z-index: 1;
}

.user-avatar {
  margin-bottom: 20px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.3), 0 0 20px rgba(255, 255, 255, 0.3);
  border: 4px solid rgba(255, 255, 255, 0.4);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
}

.user-avatar::before {
  content: '';
  position: absolute;
  top: -4px;
  left: -4px;
  right: -4px;
  bottom: -4px;
  border-radius: 50%;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.5), transparent);
  z-index: -1;
  animation: rotate 3s linear infinite;
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.user-name {
  font-size: 20px;
  font-weight: 700;
  color: #ffffff;
  margin-bottom: 12px;
  text-shadow: 0 4px 8px rgba(0, 0, 0, 0.3), 0 0 20px rgba(255, 255, 255, 0.2);
  letter-spacing: 0.5px;
}

.user-role-tag {
  background: rgba(255, 255, 255, 0.25);
  backdrop-filter: blur(15px) saturate(180%);
  -webkit-backdrop-filter: blur(15px) saturate(180%);
  border: 1px solid rgba(255, 255, 255, 0.4);
  color: #ffffff;
  font-weight: 600;
  padding: 6px 16px;
  border-radius: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  text-transform: uppercase;
  letter-spacing: 0.5px;
  font-size: 11px;
}

/* Custom Menu - Premium */
.custom-menu {
  border: none;
  background: transparent;
  padding: 20px 12px;
}

.custom-menu :deep(.ant-menu-item) {
  border-radius: 12px;
  margin: 6px 0;
  height: 52px;
  line-height: 52px;
  padding-left: 18px !important;
  transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.custom-menu :deep(.ant-menu-item)::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 0;
  background: linear-gradient(180deg, #667eea 0%, #764ba2 100%);
  border-radius: 0 4px 4px 0;
  transition: height 0.3s ease;
}

.custom-menu :deep(.ant-menu-item:hover) {
  background: linear-gradient(90deg, rgba(102, 126, 234, 0.08) 0%, rgba(118, 75, 162, 0.05) 100%);
  transform: translateX(6px);
  box-shadow: 0 2px 12px rgba(102, 126, 234, 0.1);
}

.custom-menu :deep(.ant-menu-item:hover)::before {
  height: 60%;
}

.custom-menu :deep(.ant-menu-item-selected) {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.15) 0%, rgba(118, 75, 162, 0.1) 100%);
  color: #667eea;
  font-weight: 600;
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.2);
  transform: translateX(4px);
}

.custom-menu :deep(.ant-menu-item-selected)::before {
  height: 80%;
}

.custom-menu :deep(.ant-menu-item-selected::after) {
  display: none;
}

.custom-menu :deep(.ant-menu-item-icon) {
  font-size: 18px;
  transition: all 0.3s ease;
}

.custom-menu :deep(.ant-menu-item:hover .ant-menu-item-icon) {
  transform: scale(1.15);
  color: #667eea;
}

.menu-item {
  color: #262626;
  font-size: 14px;
  display: flex;
  align-items: center;
}

.menu-help-icon {
  margin-left: 8px;
  color: #bfbfbf;
  cursor: help;
  transition: color 0.3s;
}

.menu-item:hover .menu-help-icon {
  color: #1890ff;
}

.logout-item {
  color: #ff4d4f;
  margin-top: 8px;
}

.logout-item:hover {
  background: #fff1f0 !important;
  color: #ff7875 !important;
}

/* Custom Header - Premium */
.custom-header {
  background: rgba(255, 255, 255, 0.95) !important;
  backdrop-filter: blur(20px) saturate(180%);
  -webkit-backdrop-filter: blur(20px) saturate(180%);
  padding: 0 40px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid rgba(226, 232, 240, 0.8);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06), 0 0 1px rgba(102, 126, 234, 0.1);
  position: sticky;
  top: 0;
  z-index: 100;
  transition: all 0.3s ease;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.welcome-text {
  font-size: 16px;
  font-weight: 500;
  color: #595959;
}

.user-name-highlight {
  color: #667eea;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: 0.3px;
}

.header-role-tag {
  margin: 0;
  font-weight: 500;
  border-radius: 12px;
  padding: 2px 12px;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.header-action-btn {
  color: #475569;
  border-radius: 10px;
  transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);
  height: 44px;
  padding: 0 18px;
  font-weight: 600;
  position: relative;
  overflow: hidden;
}

.header-action-btn::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  border-radius: 50%;
  background: rgba(102, 126, 234, 0.1);
  transform: translate(-50%, -50%);
  transition: width 0.4s, height 0.4s;
}

.header-action-btn:hover {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.05) 100%);
  color: #667eea;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.2);
}

.header-action-btn:hover::before {
  width: 200px;
  height: 200px;
}

.header-action-btn.logout-btn:hover {
  background: linear-gradient(135deg, rgba(255, 77, 79, 0.1) 0%, rgba(250, 173, 20, 0.05) 100%);
  color: #ff4d4f;
}

.header-action-btn.logout-btn:hover::before {
  background: rgba(255, 77, 79, 0.1);
}

/* Custom Content */
.custom-content {
  margin: 24px;
  padding: 0;
  position: relative;
}

.content-wrapper {
  padding: 40px;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.98) 0%, rgba(255, 255, 255, 0.95) 100%);
  backdrop-filter: blur(25px) saturate(180%);
  -webkit-backdrop-filter: blur(25px) saturate(180%);
  min-height: calc(100vh - 120px);
  border-radius: 24px;
  box-shadow: 
    0 20px 60px rgba(0, 0, 0, 0.08),
    0 8px 32px rgba(102, 126, 234, 0.06),
    inset 0 1px 0 rgba(255, 255, 255, 0.95),
    inset 0 -1px 0 rgba(255, 255, 255, 0.8);
  border: 1px solid rgba(226, 232, 240, 0.9);
  transition: all 0.5s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: visible;
  animation: contentFadeIn 0.6s ease-out;
}

@keyframes contentFadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* Top gradient border - subtle */
.content-wrapper::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, 
    transparent 0%, 
    rgba(102, 126, 234, 0.4) 25%,
    rgba(118, 75, 162, 0.5) 50%,
    rgba(102, 126, 234, 0.4) 75%,
    transparent 100%);
  background-size: 200% 100%;
  opacity: 0.7;
  animation: shimmerBorder 4s infinite;
  border-radius: 24px 24px 0 0;
  z-index: 1;
}

@keyframes shimmerBorder {
  0% {
    background-position: -200% 0;
  }
  100% {
    background-position: 200% 0;
  }
}

/* Subtle background glow effect - behind content */
.content-wrapper::after {
  content: '';
  position: absolute;
  top: -20%;
  right: -10%;
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, rgba(102, 126, 234, 0.06) 0%, transparent 70%);
  border-radius: 50%;
  animation: gentlePulse 12s ease-in-out infinite;
  pointer-events: none;
  z-index: 0;
}

@keyframes gentlePulse {
  0%, 100% {
    transform: scale(1) translate(0, 0);
    opacity: 0.4;
  }
  50% {
    transform: scale(1.1) translate(20px, -20px);
    opacity: 0.6;
  }
}

.content-wrapper:hover {
  box-shadow: 
    0 24px 72px rgba(0, 0, 0, 0.1),
    0 12px 40px rgba(102, 126, 234, 0.08),
    inset 0 1px 0 rgba(255, 255, 255, 0.98),
    inset 0 -1px 0 rgba(255, 255, 255, 0.9);
  transform: translateY(-1px);
  border-color: rgba(102, 126, 234, 0.25);
}

.content-wrapper:hover::before {
  opacity: 1;
  animation-duration: 2s;
}

/* Ensure all content is above decoration layers */
.content-wrapper > * {
  position: relative;
  z-index: 10;
}

/* Enhanced styling for cards inside content */
.content-wrapper :deep(.ant-card) {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(15px);
  border-radius: 16px;
  box-shadow: 
    0 4px 20px rgba(0, 0, 0, 0.06),
    0 2px 8px rgba(102, 126, 234, 0.04);
  border: 1px solid rgba(226, 232, 240, 0.8);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  margin-bottom: 24px;
  position: relative;
  z-index: 10;
}

.content-wrapper :deep(.ant-card:hover) {
  box-shadow: 
    0 8px 32px rgba(0, 0, 0, 0.1),
    0 4px 16px rgba(102, 126, 234, 0.08);
  transform: translateY(-2px);
  border-color: rgba(102, 126, 234, 0.25);
}

.content-wrapper :deep(.ant-card-head) {
  background: linear-gradient(180deg, 
    rgba(255, 255, 255, 0.9) 0%, 
    rgba(248, 250, 252, 0.7) 100%);
  border-bottom: 1px solid rgba(226, 232, 240, 0.8);
  padding: 20px 24px;
  border-radius: 16px 16px 0 0;
}

.content-wrapper :deep(.ant-card-head-title) {
  font-size: 18px;
  font-weight: 700;
  color: #1a1a1a;
  letter-spacing: 0.3px;
}

.content-wrapper :deep(.ant-card-body) {
  padding: 24px;
  background: rgba(255, 255, 255, 0.8);
}

/* Enhanced table styling */
.content-wrapper :deep(.ant-table) {
  border-radius: 12px;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.9);
}

.content-wrapper :deep(.ant-table-thead > tr > th) {
  background: linear-gradient(180deg, 
    rgba(248, 250, 252, 0.95) 0%, 
    rgba(241, 245, 249, 0.9) 100%);
  font-weight: 700;
  border-bottom: 2px solid rgba(226, 232, 240, 0.8);
  color: #1e293b;
  padding: 16px;
}

.content-wrapper :deep(.ant-table-tbody > tr:hover > td) {
  background: linear-gradient(135deg, 
    rgba(102, 126, 234, 0.05) 0%, 
    rgba(118, 75, 162, 0.03) 100%);
}

/* Enhanced buttons */
.content-wrapper :deep(.ant-btn-primary) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.25);
  transition: all 0.3s ease;
}

.content-wrapper :deep(.ant-btn-primary:hover) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.35);
  filter: brightness(1.05);
}

/* Enhanced tags */
.content-wrapper :deep(.ant-tag) {
  border-radius: 12px;
  padding: 4px 12px;
  font-weight: 500;
  border: none;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;
}

.content-wrapper :deep(.ant-tag:hover) {
  transform: translateY(-1px);
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

/* Enhanced inputs */
.content-wrapper :deep(.ant-input),
.content-wrapper :deep(.ant-input-password),
.content-wrapper :deep(.ant-select-selector) {
  border-radius: 10px;
  border: 2px solid rgba(226, 232, 240, 0.8);
  transition: all 0.3s ease;
}

.content-wrapper :deep(.ant-input):focus,
.content-wrapper :deep(.ant-input-password):focus,
.content-wrapper :deep(.ant-select-focused .ant-select-selector) {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

/* Enhanced lists */
.content-wrapper :deep(.ant-list-item) {
  padding: 16px 20px;
  border-radius: 12px;
  transition: all 0.3s ease;
  margin-bottom: 8px;
  border: 1px solid transparent;
}

.content-wrapper :deep(.ant-list-item):hover {
  background: linear-gradient(135deg, 
    rgba(102, 126, 234, 0.06) 0%, 
    rgba(118, 75, 162, 0.04) 100%);
  transform: translateX(4px);
  border-color: rgba(102, 126, 234, 0.15);
}

/* Enhanced progress bars */
.content-wrapper :deep(.ant-progress-bg) {
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.08);
}

/* Responsive */
@media (max-width: 768px) {
  .custom-sider {
    width: 200px !important;
  }

  .custom-header {
    padding: 0 16px;
  }

  .content-wrapper {
    padding: 16px;
    margin: 0;
    border-radius: 0;
  }

  .header-actions {
    gap: 4px;
  }

  .header-action-btn {
    padding: 0 8px;
    font-size: 12px;
  }
}
</style>
  