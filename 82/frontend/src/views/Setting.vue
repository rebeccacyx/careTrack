<template>
  <div class="setting-page">
    <a-row :gutter="[24, 24]">
      <!-- Patient Information Section -->
      <a-col :span="24">
        <a-card>
          <template #title>
            <div style="display: flex; align-items: center; gap: 8px;">
              <UserOutlined />
              <span>Client Information</span>
              <a-tooltip 
                :title="userRole === 'worker' ? 'View client information shared by your manager' : 'Manage client basic information and special requirements'" 
                placement="top"
              >
                <QuestionCircleOutlined style="color: #999; cursor: help;" />
              </a-tooltip>
            </div>
          </template>
          
          <!-- Worker info alert -->
          <a-alert 
            v-if="userRole === 'worker' && clientInfo"
            message="Client Information from Manager"
            description="This client information is shared by your manager and synchronized from the main patient record."
            type="info"
            show-icon
            style="margin-bottom: 16px;"
          />
          
          <a-form
            :model="patientForm"
            :label-col="{ span: 4 }"
            :wrapper-col="{ span: 20 }"
            :disabled="!canEditPatient"
            @finish="onPatientFormFinish"
          >
            <a-form-item
              label="Client Name"
              name="name"
              :rules="[{ required: true, message: 'Please input client name!' }]"
            >
              <a-input v-model:value="patientForm.name" placeholder="Enter client name" />
            </a-form-item>

            <a-form-item
              label="Age"
              name="age"
              :rules="[{ required: true, message: 'Please input client age!' }]"
            >
              <a-input-number 
                v-model:value="patientForm.age" 
                placeholder="Enter age"
                :min="0"
                :max="150"
                style="width: 100%"
              />
            </a-form-item>

            <a-form-item
              label="Medical Conditions"
              name="medicalConditions"
            >
              <a-textarea
                v-model:value="patientForm.medicalConditions"
                placeholder="Enter medical conditions, allergies, medications, etc."
                :rows="3"
              />
            </a-form-item>

            <a-form-item
              label="Special Requirements"
              name="specialRequirements"
            >
              <a-textarea
                v-model:value="patientForm.specialRequirements"
                placeholder="Enter special requirements (e.g., daily coffee, specific meal times, etc.)"
                :rows="3"
              />
            </a-form-item>

            <!-- Display Client ID if generated -->
            <a-form-item v-if="generatedClientId" label="Client ID">
              <a-input 
                :value="generatedClientId" 
                readonly 
                disabled
                style="background-color: #f5f5f5; color: #999; font-family: monospace;"
              />
            </a-form-item>

            <!-- Display Organization Name if bound -->
            <a-form-item v-if="organizationName" label="Bound Organization">
              <a-input 
                :value="organizationName" 
                readonly 
                disabled
                style="background-color: #f5f5f5; color: #999;"
              />
            </a-form-item>


            <a-form-item v-if="canEditPatient" :wrapper-col="{ offset: 4, span: 20 }">
              <a-button type="primary" html-type="submit" :loading="patientLoading">
                Save Client Information & Generate Client ID
              </a-button>
            </a-form-item>

            <!-- Manager: View Client Information -->
            <template v-if="userRole === 'manager'">
              <a-divider>Client Information</a-divider>
              
              <!-- Auto-loaded client info for bound managers -->
              <div v-if="clientInfo" style="background: #f6ffed; border: 1px solid #b7eb8f; padding: 16px; border-radius: 6px; margin-top: 16px;">
                <div style="display: flex; align-items: center; gap: 8px; margin-bottom: 12px;">
                  <InfoCircleOutlined style="color: #52c41a;" />
                  <h4 style="margin: 0; color: #52c41a;">Bound Client Information</h4>
                </div>
                <p><strong>Name:</strong> {{ clientInfo.firstName }} {{ clientInfo.lastName }}</p>
                <p><strong>Age:</strong> {{ clientInfo.age }}</p>
                <p><strong>Client ID:</strong> {{ clientInfo.clientId }}</p>
                <p><strong>Medical Conditions:</strong> {{ clientInfo.medicalConditions || 'None specified' }}</p>
                <p><strong>Special Requirements:</strong> {{ clientInfo.specialRequirements || 'None specified' }}</p>
                <p><strong>Created By:</strong> {{ clientInfo.createdBy }}</p>
                <p><strong>Created At:</strong> {{ clientInfo.createdAt }}</p>
                <div style="margin-top: 12px; padding: 8px; background: #f0f9ff; border-radius: 4px; font-size: 12px; color: #1890ff;">
                  <InfoCircleOutlined style="margin-right: 4px;" />
                  This client information is automatically loaded through your invite token binding. You have access to manage this client's care team.
                </div>
              </div>
              
              <!-- Manual client lookup for unbound managers -->
              <div v-else style="background: #fff7e6; border: 1px solid #ffd591; padding: 16px; border-radius: 6px; margin-top: 16px;">
                <div style="display: flex; align-items: center; gap: 8px; margin-bottom: 12px;">
                  <ExclamationCircleOutlined style="color: #faad14;" />
                  <h4 style="margin: 0; color: #faad14;">No Client Bound</h4>
                </div>
                <p style="margin-bottom: 16px;">You are not currently bound to any client. To access client information, you need to use an invite token provided by a POA.</p>
                
                <a-form-item label="Client ID (Manual Lookup)">
                  <a-input 
                    v-model:value="clientIdInput" 
                    placeholder="Enter Client ID to view client information"
                    style="margin-bottom: 16px;"
                  >
                    <template #suffix>
                      <a-button 
                        type="primary" 
                        @click="loadClientInfo"
                        :loading="clientInfoLoading"
                      >
                        View Client
                      </a-button>
                    </template>
                  </a-input>
                </a-form-item>
              </div>
            </template>
          </a-form>

          <!-- Display existing token if available -->
          <div v-if="generatedToken && userRole === 'poa'" style="background: #f0f9ff; padding: 16px; border-radius: 6px; border: 1px solid #bae6fd; margin-top: 16px;">
            <h4 style="color: #0369a1; margin-bottom: 12px;">Generated Token</h4>
            <p style="margin-bottom: 8px;"><strong>Token:</strong></p>
            <a-input-group compact>
              <a-input 
                :value="generatedToken" 
                readonly 
                style="background-color: #f5f5f5; color: #999; font-family: monospace;"
              />
              <a-button 
                type="primary" 
                @click="copyToken"
                style="background-color: #1890ff; border-color: #1890ff;"
              >
                <template #icon>
                  <CopyOutlined />
                </template>
                Copy
              </a-button>
            </a-input-group>
            <p style="color: #666; font-size: 12px; margin-bottom: 8px; margin-top: 12px;">
              <strong>Organization:</strong> {{ organizationName }}
            </p>
            <p style="color: #666; font-size: 12px; margin-bottom: 8px;">
              <strong>Expires:</strong> {{ tokenExpiration }}
            </p>
            <p style="color: #666; font-size: 12px;">
              <strong>Status:</strong> <span style="color: #52c41a;">Valid for 7 days</span>
            </p>
          </div>
        </a-card>
      </a-col>

      <!-- Profile Settings Section -->
      <a-col :span="24">
        <a-card>
          <template #title>
            <div style="display: flex; align-items: center; gap: 8px;">
              <SettingOutlined />
              <span>Profile Settings</span>
              <a-tooltip title="Manage your personal information and account settings" placement="top">
                <QuestionCircleOutlined style="color: #999; cursor: help;" />
              </a-tooltip>
            </div>
          </template>
          
          <a-form
            :model="profileForm"
            :label-col="{ span: 3 }"
            :wrapper-col="{ span: 21 }"
            @finish="onProfileFormFinish"
          >
            <a-form-item
              label="Full Name"
              name="fullName"
            >
              <a-input v-model:value="profileForm.fullName" disabled />
            </a-form-item>

            <a-form-item
              label="Email"
              name="email"
              :rules="[
                { required: true, message: 'Please input your email!' },
                { type: 'email', message: 'Please enter a valid email!' }
              ]"
            >
              <a-input v-model:value="profileForm.email" placeholder="Enter your email" />
            </a-form-item>

            <a-form-item
              v-if="userRole === 'worker'"
              label="Hobbies"
              name="hobbies"
            >
              <a-textarea
                v-model:value="profileForm.hobbies"
                placeholder="Enter your hobbies and interests (optional)"
                :rows="3"
              />
              <div style="font-size: 12px; color: #999; margin-top: 4px;">
                Share your hobbies and interests to help team members get to know you better
              </div>
            </a-form-item>

            <a-form-item :wrapper-col="{ offset: 3, span: 21 }">
              <a-button type="primary" html-type="submit" :loading="profileLoading">
                Update Profile
              </a-button>
            </a-form-item>
          </a-form>
        </a-card>
      </a-col>

      <!-- Notification Settings Section -->
      <a-col :span="24">
        <a-card>
          <template #title>
            <div style="display: flex; align-items: center; gap: 8px;">
              <BellOutlined />
              <span>Notification Settings</span>
              <a-tooltip title="Configure your notification preferences" placement="top">
                <QuestionCircleOutlined style="color: #999; cursor: help;" />
              </a-tooltip>
            </div>
          </template>
          
          <!-- Only render form after settings are loaded to prevent default values overriding backend values -->
          <a-spin :spinning="!notificationSettingsLoaded" tip="Loading notification settings...">
            <a-form
              v-if="notificationSettingsLoaded"
              :model="notificationForm"
              :label-col="{ span: 6 }"
              :wrapper-col="{ span: 18 }"
              @finish="onNotificationFormFinish"
            >
            <a-form-item label="Task Reminders">
              <a-switch 
                v-model:checked="notificationForm.taskReminders"
                checked-children="ON"
                un-checked-children="OFF"
              />
              <span style="margin-left: 8px; color: #666;">
                Receive notifications for task deadlines and updates
              </span>
            </a-form-item>

            <a-form-item label="Approval Notifications" v-if="userRole === 'poa' || userRole === 'manager'">
              <a-switch 
                v-model:checked="notificationForm.approvalNotifications"
                checked-children="ON"
                un-checked-children="OFF"
              />
              <span style="margin-left: 8px; color: #666;">
                Receive notifications when approval is required
              </span>
            </a-form-item>

            <a-form-item label="Budget Warning" v-if="userRole === 'poa' || userRole === 'manager'">
              <a-switch 
                v-model:checked="notificationForm.budgetWarning"
                checked-children="ON"
                un-checked-children="OFF"
              />
              <span style="margin-left: 8px; color: #666;">
                Receive notifications for budget alerts and warnings
              </span>
            </a-form-item>

            <a-form-item label="Email Notifications">
              <a-switch 
                v-model:checked="notificationForm.emailNotifications"
                checked-children="ON"
                un-checked-children="OFF"
              />
              <span style="margin-left: 8px; color: #666;">
                Receive notifications via email
              </span>
            </a-form-item>

            <a-form-item :wrapper-col="{ offset: 6, span: 18 }">
              <a-button type="primary" html-type="submit" :loading="notificationLoading">
                Save Notification Settings
              </a-button>
            </a-form-item>
            </a-form>
          </a-spin>
        </a-card>
      </a-col>
    </a-row>

  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { message } from 'ant-design-vue'
import { 
  QuestionCircleOutlined, 
  UserOutlined, 
  SettingOutlined, 
  BellOutlined,
  CopyOutlined,
  InfoCircleOutlined,
  ExclamationCircleOutlined
} from '@ant-design/icons-vue'
import { getMe, updateUserPatientId, updateUserProfile, updateUserNotificationSettings } from '@/services/userService'
import { getPatientById, updatePatient, getPatientByClientId, createPatient } from '@/services/patientService'

const userRole = ref('worker')
const patientLoading = ref(false)
const profileLoading = ref(false)
const notificationLoading = ref(false)
const notificationSettingsLoaded = ref(false) // Track if notification settings have been loaded
const generatedClientId = ref('')
const organizationName = ref('')
const clientIdInput = ref('')
const clientInfoLoading = ref(false)
const clientInfo = ref(null)

// Token display variables
const generatedToken = ref('')
const tokenExpiration = ref('')

// Copy token to clipboard
const copyToken = async () => {
  try {
    await navigator.clipboard.writeText(generatedToken.value)
    message.success('Token copied to clipboard!')
  } catch (error) {
    console.error('Failed to copy token:', error)
    message.error('Failed to copy token')
  }
}

// Patient Information Form
const patientForm = reactive({
  name: '',
  age: null,
  medicalConditions: '',
  specialRequirements: ''
})

// Profile Form
const profileForm = reactive({
  fullName: '',
  email: '',
  hobbies: ''
})

// Notification Form - initialized with default values, will be updated from backend
const notificationForm = reactive({
  taskReminders: false, // Will be loaded from backend
  approvalNotifications: false, // Will be loaded from backend
  budgetWarning: false, // Will be loaded from backend
  emailNotifications: false // Will be loaded from backend
})

// Computed property to check if user can edit patient information
const canEditPatient = computed(() => {
  return userRole.value === 'poa'
})

onMounted(async () => {
  try {
    const userInfo = await getMe()
    userRole.value = userInfo?.data?.role || 'worker'
    
    // Load user profile data
    profileForm.fullName = userInfo?.data?.name || 'Test User'
    profileForm.email = userInfo?.data?.email || 'test@example.com'
    
    // Only load hobbies for worker users
    if (userRole.value === 'worker') {
      profileForm.hobbies = userInfo?.data?.hobbies || ''
    }
    
    // Load patient information if user is POA, Manager, or Worker
    if (userRole.value === 'poa' || userRole.value === 'manager' || userRole.value === 'worker') {
      await loadPatientInfo()
    } else {
      // Load mock patient data for other roles
      loadPatientData()
    }
    
    await loadNotificationSettings()
  } catch (e) {
    console.error('Failed to get user info:', e)
  }
})

// Load patient information from API
const loadPatientInfo = async () => {
  try {
    const userInfo = await getMe()
    console.log('🔍 Setting.vue - User info received:', userInfo)
    console.log('🔍 Setting.vue - PatientId:', userInfo?.data?.patientId)
    console.log('🔍 Setting.vue - User role:', userInfo?.data?.role)
    
    if (userInfo?.data?.patientId) {
      console.log('✅ Setting.vue - Found patientId, loading patient data')
      const response = await getPatientById(userInfo.data.patientId)
      if (response?.data) {
        // Handle both old format (name) and new format (firstName + lastName)
        if (response.data.name) {
          patientForm.name = response.data.name
        } else if (response.data.firstName || response.data.lastName) {
          patientForm.name = `${response.data.firstName || ''} ${response.data.lastName || ''}`.trim()
        }
        
        patientForm.age = response.data.age || null
        patientForm.medicalConditions = response.data.medicalConditions || ''
        patientForm.specialRequirements = response.data.specialRequirements || ''
        
        // Load generated Client ID if it exists
        if (response.data.clientId) {
          generatedClientId.value = response.data.clientId
        }
        
        // Load organization name if it exists
        if (response.data.organizationName) {
          organizationName.value = response.data.organizationName
        }
        
        // Load token information if it exists (separate from Client ID)
        if (response.data.inviteToken && response.data.tokenExpiration) {
          generatedToken.value = response.data.inviteToken
          tokenExpiration.value = new Date(response.data.tokenExpiration).toLocaleDateString()
        }
        
        // For managers and workers, automatically load client info if bound to patient
        if (userInfo?.data?.role === 'manager' || userInfo?.data?.role === 'worker') {
          console.log(`✅ ${userInfo.data.role} is bound to patient, client info loaded automatically`)
          console.log('🔍 Setting.vue - Client info data:', response.data)
          clientInfo.value = response.data
          console.log('🔍 Setting.vue - Client info set to:', clientInfo.value)
          
          // For workers, show additional message about client info source
          if (userInfo?.data?.role === 'worker') {
            console.log('📋 Worker accessing client info from manager via shared patient record')
          }
        }
      }
    } else {
      // No patient ID found, start with empty form
      console.log('No patient ID found, starting with empty form')
      
      // For managers and workers without patient binding, show message
      if (userInfo?.data?.role === 'manager') {
        console.log('Manager not bound to any patient yet')
      } else if (userInfo?.data?.role === 'worker') {
        console.log('Worker not bound to any patient yet - no client info available')
      }
    }
  } catch (error) {
    console.error('Failed to load patient info:', error)
    // Fallback to mock data
    loadPatientData()
  }
}

// Load patient data - now handled by loadPatientInfo API function
const loadPatientData = () => {
  // This function is kept for backward compatibility but now uses API
  // The actual loading is done in loadPatientInfo function
}

// Load notification settings from backend
const loadNotificationSettings = async () => {
  notificationSettingsLoaded.value = false
  try {
    const userInfo = await getMe()
    console.log('🔍 Setting.vue - loadNotificationSettings - userInfo:', userInfo)
    console.log('🔍 Setting.vue - loadNotificationSettings - userInfo.data:', userInfo?.data)
    
    if (userInfo?.data) {
      // Debug: log all notification settings from backend
      console.log('🔔 Setting.vue - Notification settings from backend:', {
        taskReminders: userInfo.data.taskReminders,
        approvalNotifications: userInfo.data.approvalNotifications,
        budgetWarning: userInfo.data.budgetWarning,
        emailNotifications: userInfo.data.emailNotifications
      })
      
      // Use ?? operator for cleaner default value handling
      // Load notification settings from user data, with proper fallbacks
      notificationForm.taskReminders = userInfo.data.taskReminders ?? true
      notificationForm.emailNotifications = userInfo.data.emailNotifications ?? true
      
      // Role-specific defaults for approval notifications and budget warnings
      const hasApprovalAccess = userRole.value === 'poa' || userRole.value === 'manager'
      notificationForm.approvalNotifications = userInfo.data.approvalNotifications ?? hasApprovalAccess
      notificationForm.budgetWarning = userInfo.data.budgetWarning ?? hasApprovalAccess
      
      console.log('✅ Setting.vue - Notification form after loading:', {
        taskReminders: notificationForm.taskReminders,
        approvalNotifications: notificationForm.approvalNotifications,
        budgetWarning: notificationForm.budgetWarning,
        emailNotifications: notificationForm.emailNotifications
      })
    } else {
      // Fallback to default values if no user data
      const hasApprovalAccess = userRole.value === 'poa' || userRole.value === 'manager'
      notificationForm.taskReminders = true
      notificationForm.approvalNotifications = hasApprovalAccess
      notificationForm.budgetWarning = hasApprovalAccess
      notificationForm.emailNotifications = true
    }
  } catch (error) {
    console.error('Failed to load notification settings:', error)
    // Fallback to default values on error
    const hasApprovalAccess = userRole.value === 'poa' || userRole.value === 'manager'
    notificationForm.taskReminders = true
    notificationForm.approvalNotifications = hasApprovalAccess
    notificationForm.budgetWarning = hasApprovalAccess
    notificationForm.emailNotifications = true
  } finally {
    // Mark as loaded after setting values (regardless of success or failure)
    notificationSettingsLoaded.value = true
  }
}


// Generate unique Client ID based on name and age
const generateClientId = (name, age) => {
  const timestamp = Date.now().toString(36)
  const nameCode = name.replace(/\s+/g, '').toUpperCase().substring(0, 3)
  const ageCode = age ? age.toString().padStart(2, '0') : '00'
  const randomCode = Math.random().toString(36).substring(2, 6).toUpperCase()
  
  return `CLI-${nameCode}-${ageCode}-${randomCode}-${timestamp}`
}


// Load client information by Client ID (for Manager)
const loadClientInfo = async () => {
  if (!clientIdInput.value.trim()) {
    message.warning('Please enter a Client ID')
    return
  }
  
  clientInfoLoading.value = true
  try {
    const response = await getPatientByClientId(clientIdInput.value.trim())
    if (response?.data) {
      clientInfo.value = response.data
      message.success('Client information loaded successfully!')
    } else {
      message.error('Client not found with this Client ID')
      clientInfo.value = null
    }
  } catch (error) {
    console.error('Failed to load client info:', error)
    message.error('Failed to load client information')
    clientInfo.value = null
  } finally {
    clientInfoLoading.value = false
  }
}

// Handle patient form submission
const onPatientFormFinish = async () => {
  if (!canEditPatient.value) {
    message.warning('You do not have permission to edit client information')
    return
  }
  
  patientLoading.value = true
  try {
    const userInfo = await getMe()
    
    // Generate unique Client ID
    const clientId = generateClientId(patientForm.name, patientForm.age)
    
    const patientData = {
      firstName: patientForm.name.split(' ')[0] || patientForm.name,
      lastName: patientForm.name.split(' ').slice(1).join(' ') || '',
      age: patientForm.age,
      medicalConditions: patientForm.medicalConditions,
      specialRequirements: patientForm.specialRequirements,
      clientId: clientId,
      createdBy: userInfo.data.id || 'poa',
      createdAt: new Date().toISOString(),
      poaId: userInfo.data.id || 'poa',
      organizationName: userInfo.data.organizationName || 'Default Organization'
    }
    
    let savedPatient
    if (userInfo?.data?.patientId) {
      // Update existing patient
      savedPatient = await updatePatient(userInfo.data.patientId, patientData)
    } else {
      // Create new patient
      savedPatient = await createPatient(patientData)
      
      // Update user's patientId in backend database
      if (savedPatient?.data?.id) {
        try {
          await updateUserPatientId(userInfo.data.id, savedPatient.data.id)
          console.log('Updated user patientId in backend database:', savedPatient.data.id)
        } catch (updateError) {
          console.error('Failed to update user patientId in backend:', updateError)
          // Don't fail the whole operation if backend update fails
        }
      }
    }
    
    // Set generated values for display
    generatedClientId.value = clientId
    organizationName.value = patientData.organizationName
    
    message.success('Client information saved successfully! Client ID: ' + clientId)
  } catch (e) {
    console.error('Failed to save patient info:', e)
    message.error(e.message || 'Failed to save client information')
  } finally {
    patientLoading.value = false
  }
}

// Handle profile form submission
const onProfileFormFinish = async () => {
  profileLoading.value = true
  try {
    const userInfo = await getMe()
    if (!userInfo?.data?.id) {
      throw new Error('User not authenticated')
    }
    
    const profileData = {
      firstName: profileForm.fullName.split(' ')[0] || '',
      middleName: '',
      lastName: profileForm.fullName.split(' ').slice(1).join(' ') || '',
      email: profileForm.email
    }
    
    // Only include hobbies for worker users
    if (userRole.value === 'worker') {
      profileData.hobbies = profileForm.hobbies
    }
    
    await updateUserProfile(userInfo.data.id, profileData)
    message.success('Profile updated successfully!')
  } catch (e) {
    console.error('Failed to update profile:', e)
    message.error(e.message || 'Failed to update profile')
  } finally {
    profileLoading.value = false
  }
}

// Handle notification form submission
const onNotificationFormFinish = async () => {
  notificationLoading.value = true
  try {
    const userInfo = await getMe()
    if (!userInfo?.data?.id) {
      throw new Error('User not authenticated')
    }
    
    const notificationData = {
      taskReminders: notificationForm.taskReminders,
      approvalNotifications: notificationForm.approvalNotifications,
      budgetWarning: notificationForm.budgetWarning,
      emailNotifications: notificationForm.emailNotifications
    }
    
    await updateUserNotificationSettings(userInfo.data.id, notificationData)
    message.success('Notification settings updated successfully!')
  } catch (e) {
    console.error('Failed to update notification settings:', e)
    message.error(e.message || 'Failed to update notification settings')
  } finally {
    notificationLoading.value = false
  }
}
</script>

<style scoped>
.setting-page {
  max-width: 1200px;
  margin: 0 auto;
}

.ant-card {
  margin-bottom: 24px;
}

.ant-form-item {
  margin-bottom: 16px;
}
</style>
