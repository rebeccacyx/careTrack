<template>
  <div class="guide-container">
    <a-modal
      v-model:open="visible"
      :title="null"
      :footer="null"
      :closable="false"
      :maskClosable="false"
      width="800px"
      class="guide-modal"
    >
      <div class="guide-content">
        <!-- Role Selection Step -->
        <div v-if="currentStep === 'role-selection'" class="step-content">
          <div class="step-header">
            <h2>Welcome! Let's Get Started</h2>
            <p class="subtitle">Please select your role to see the setup guide</p>
          </div>
          
          <div class="role-options">
            <a-card
              :class="['role-card', { active: selectedRole === 'poa' }]"
              hoverable
              @click="selectedRole = 'poa'"
            >
              <template #title>
                <div class="role-title">
                  <UserOutlined style="font-size: 24px; color: #1890ff;" />
                  <span>POA (Power of Attorney)</span>
                </div>
              </template>
              <p class="role-description">
                I am a Power of Attorney managing care for a client
              </p>
            </a-card>
            
            <a-card
              :class="['role-card', { active: selectedRole === 'manager' }]"
              hoverable
              @click="selectedRole = 'manager'"
            >
              <template #title>
                <div class="role-title">
                  <TeamOutlined style="font-size: 24px; color: #52c41a;" />
                  <span>Manager</span>
                </div>
              </template>
              <p class="role-description">
                I manage workers and schedules
              </p>
            </a-card>
            
            <a-card
              :class="['role-card', { active: selectedRole === 'worker' }]"
              hoverable
              @click="selectedRole = 'worker'"
            >
              <template #title>
                <div class="role-title">
                  <SolutionOutlined style="font-size: 24px; color: #faad14;" />
                  <span>Worker</span>
                </div>
              </template>
              <p class="role-description">
                I am a care worker providing services
              </p>
            </a-card>
          </div>
          
          <div class="step-actions">
            <a-button 
              type="primary" 
              size="large"
              :disabled="!selectedRole"
              @click="showGuide"
            >
              Continue
            </a-button>
          </div>
        </div>

        <!-- Guide Steps for Each Role -->
        <div v-else class="step-content">
          <div class="step-header">
            <h2>{{ guideTitle }}</h2>
            <p class="subtitle">Follow these steps to get started</p>
          </div>

          <a-steps :current="currentGuideStep" direction="vertical" size="small">
            <a-step 
              v-for="(step, index) in guideSteps" 
              :key="index"
            >
              <template #title>
                <div>
                  <span 
                    v-if="step.route"
                    class="step-title-link"
                    @click.stop="navigateToStep(step.route)"
                  >
                    {{ step.title }}
                    <LinkOutlined style="margin-left: 4px; color: #1890ff; font-size: 12px;" />
                  </span>
                  <span v-else class="step-title">{{ step.title }}</span>
                </div>
              </template>
              <template #description>
                <div class="step-description" v-html="step.description"></div>
                <div v-if="step.route" class="step-link-hint">
                  <a-button 
                    type="link" 
                    size="small" 
                    @click.stop="navigateToStep(step.route)"
                    style="padding: 0; height: auto; margin-top: 8px;"
                  >
                    <LinkOutlined style="margin-right: 4px;" />
                    Click here to go to this page
                  </a-button>
                </div>
              </template>
            </a-step>
          </a-steps>

          <div class="step-actions">
            <a-space>
              <a-button @click="goBack">Back</a-button>
              <a-button 
                type="primary" 
                size="large"
                @click="handleComplete"
              >
                I Understand, Got It!
              </a-button>
            </a-space>
          </div>
        </div>
      </div>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { UserOutlined, TeamOutlined, SolutionOutlined, LinkOutlined } from '@ant-design/icons-vue'
import { getMe } from '@/services/userService'

const router = useRouter()
const visible = ref(false)
const currentStep = ref('role-selection') // 'role-selection' or 'guide'
const selectedRole = ref(null)
const currentGuideStep = ref(0)

// Check if user has already seen the guide
const GUIDE_KEY = 'user_guide_seen'

const guideTitle = computed(() => {
  switch (selectedRole.value) {
    case 'poa':
      return 'POA Setup Guide'
    case 'manager':
      return 'Manager Setup Guide'
    case 'worker':
      return 'Worker Setup Guide'
    default:
      return 'Setup Guide'
  }
})

const guideSteps = computed(() => {
  switch (selectedRole.value) {
    case 'poa':
      return [
        {
          title: 'Step 1: Fill Client Information',
          description: 'Go to <strong>Setting</strong> page and fill in your client information.',
          route: '/app/setting'
        },
        {
          title: 'Step 2: Generate Invite Token',
          description: 'Go to <strong>Care Team</strong> page and generate an invite token for managers.',
          route: '/app/carer-team'
        },
        {
          title: 'Step 3: Share Token with Manager',
          description: 'Share the generated token with your manager so they can register and set up the team.',
          route: null
        }
      ]
    case 'manager':
      return [
        {
          title: 'Step 1: Generate Worker Token',
          description: 'Go to <strong>Worker Management</strong> page and generate invite tokens for workers.',
          route: '/app/worker-management'
        },
        {
          title: 'Step 2: Share Tokens',
          description: 'Share the generated tokens with workers so they can register and join your team.',
          route: null
        }
      ]
    case 'worker':
      return [
        {
          title: 'Step 1: Enter Invite Token',
          description: 'Enter the invite token provided by your manager to join the team and start working.',
          route: '/invitecode'
        }
      ]
    default:
      return []
  }
})

const showGuide = () => {
  if (!selectedRole.value) {
    message.warning('Please select a role first')
    return
  }
  currentStep.value = 'guide'
  currentGuideStep.value = 0
}

const goBack = () => {
  currentStep.value = 'role-selection'
  selectedRole.value = null
}

// Navigate to step route
const navigateToStep = (route) => {
  if (!route) {
    return
  }
  
  // Close guide modal first
  visible.value = false
  
  // Small delay to ensure modal closes before navigation
  setTimeout(() => {
    // Navigate to the route
    router.push(route).then(() => {
      message.success('Navigated to the page')
    }).catch((error) => {
      console.error('Navigation error:', error)
      // If navigation fails, it might be because user is not authenticated
      // Try navigating anyway
      window.location.href = route
    })
  }, 100)
}

const handleComplete = () => {
  // Mark guide as seen for this role
  const seenGuides = JSON.parse(localStorage.getItem(GUIDE_KEY) || '{}')
  seenGuides[selectedRole.value] = true
  localStorage.setItem(GUIDE_KEY, JSON.stringify(seenGuides))
  
  visible.value = false
  message.success('Guide completed! You can always access help from the menu.')
  
  // Reset for next time
  currentStep.value = 'role-selection'
  selectedRole.value = null
}

// Check if user needs to see the guide
const shouldShowGuide = async () => {
  try {
    // Check if user is logged in
    const token = sessionStorage.getItem('token')
    if (!token) {
      return false
    }

    // Get user info
    const userInfo = await getMe()
    const userRole = userInfo?.data?.role
    
    if (!userRole) {
      return false
    }

    // Check if user has seen the guide for their role
    const seenGuides = JSON.parse(localStorage.getItem(GUIDE_KEY) || '{}')
    if (seenGuides[userRole]) {
      return false
    }

    // Auto-select role based on user's actual role
    selectedRole.value = userRole
    currentStep.value = 'guide'
    
    return true
  } catch (error) {
    console.error('Failed to check guide status:', error)
    return false
  }
}

// Expose method to show guide manually
const show = async (autoShow = false) => {
  // Try to get user role first
  try {
    const userInfo = await getMe()
    const userRole = userInfo?.data?.role
    
    if (userRole && ['poa', 'manager', 'worker'].includes(userRole)) {
      // User has a role
      const seenGuides = JSON.parse(localStorage.getItem(GUIDE_KEY) || '{}')
      
      if (autoShow && seenGuides[userRole]) {
        // Auto-show but user has already seen guide, don't show
        console.log('Guide already seen for role:', userRole, '- skipping auto-show')
        visible.value = false
        return
      }
      
      if (autoShow && !seenGuides[userRole]) {
        // Auto-show for first time, auto-select role and show guide
        console.log('First time showing guide for role:', userRole)
        selectedRole.value = userRole
        currentStep.value = 'guide'
        visible.value = true
      } else if (!autoShow) {
        // Manual show (from Guide button), check if they've seen the guide
        if (seenGuides[userRole]) {
          // User has seen guide for their role, show role selection (let them choose)
          currentStep.value = 'role-selection'
          selectedRole.value = null
        } else {
          // User hasn't seen guide for their role, auto-select and show guide
          selectedRole.value = userRole
          currentStep.value = 'guide'
        }
        visible.value = true
      }
    } else {
      // No role or unknown role, show role selection
      console.log('No valid role found, showing role selection')
      currentStep.value = 'role-selection'
      selectedRole.value = null
      visible.value = true
    }
  } catch (error) {
    console.error('Error in show guide:', error)
    // On error, show role selection
    currentStep.value = 'role-selection'
    selectedRole.value = null
    visible.value = true
  }
}

// Show guide on mount if needed (for automatic display)
onMounted(async () => {
  // Don't auto-show on mount - let Layout control when to show
  // const shouldShow = await shouldShowGuide()
  // if (shouldShow) {
  //   visible.value = true
  // }
})

defineExpose({ show })
</script>

<style scoped>
.guide-container {
  position: relative;
}

.guide-modal :deep(.ant-modal-content) {
  border-radius: 12px;
  overflow: hidden;
}

.guide-content {
  padding: 24px;
}

.step-header {
  text-align: center;
  margin-bottom: 32px;
}

.step-header h2 {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 600;
  color: #262626;
}

.subtitle {
  margin: 0;
  color: #8c8c8c;
  font-size: 14px;
}

.role-options {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 32px;
}

.role-card {
  cursor: pointer;
  transition: all 0.3s;
  border: 2px solid #f0f0f0;
}

.role-card:hover {
  border-color: #1890ff;
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.15);
}

.role-card.active {
  border-color: #1890ff;
  background: #f0f7ff;
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.2);
}

.role-title {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 18px;
  font-weight: 600;
}

.role-description {
  margin: 8px 0 0 0;
  color: #595959;
  font-size: 14px;
}

.step-actions {
  display: flex;
  justify-content: center;
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid #f0f0f0;
}

:deep(.ant-steps-item-title) {
  font-size: 16px;
  font-weight: 500;
}

.step-title {
  font-weight: 500;
  color: #262626;
}

.step-description {
  color: #595959;
  line-height: 1.8;
  margin-top: 8px;
  font-size: 14px;
}

:deep(.ant-steps-item-description) {
  margin-top: 8px;
  font-size: 14px;
}

:deep(.ant-steps-item-title) {
  font-size: 16px;
  font-weight: 500;
  color: #262626;
}

.step-title-link {
  cursor: pointer;
  color: #1890ff;
  font-weight: 500;
  transition: all 0.3s;
  display: inline-flex;
  align-items: center;
}

.step-title-link:hover {
  color: #40a9ff;
  text-decoration: underline;
}

.step-title {
  color: #262626;
  font-weight: 500;
}

.step-link-hint {
  margin-top: 8px;
}

.step-link-hint :deep(.ant-btn-link) {
  color: #1890ff;
  font-size: 12px;
  padding: 0;
  height: auto;
}

.step-link-hint :deep(.ant-btn-link:hover) {
  color: #40a9ff;
}

@media (max-width: 768px) {
  .guide-content {
    padding: 16px;
  }

  .role-options {
    gap: 12px;
  }
}
</style>
