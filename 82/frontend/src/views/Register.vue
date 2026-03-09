<template>
  <div class="register-container">
    <!-- Background with animated gradient -->
    <div class="bg-gradient">
      <div class="gradient-circle circle-1"></div>
      <div class="gradient-circle circle-2"></div>
      <div class="gradient-circle circle-3"></div>
    </div>

    <!-- Content -->
    <div class="register-content">
      <!-- Logo Section -->
      <div class="logo-section">
        <div class="logo-icon">
          <HeartOutlined />
        </div>
        <h1 class="logo-text">CareTrack</h1>
        <p class="logo-subtitle">Create your account to get started</p>
      </div>

      <!-- Register Form -->
      <div class="form-wrapper">
        <a-form
          :model="formState"
          name="register"
          autocomplete="off"
          @finish="onFinish"
          @finishFailed="onFinishFailed"
          :disabled="loading"
          class="register-form"
        >
          <h2 class="form-title">Create Account</h2>

          <!-- Name Fields Row -->
          <div class="name-row">
            <a-form-item
              name="firstName"
              :rules="[{ required: true, message: 'Enter first name' }]"
              class="name-field"
            >
              <a-input 
                v-model:value="formState.firstName" 
                placeholder="First name"
                size="large"
                class="form-input"
              >
                <template #prefix><UserOutlined class="input-icon" /></template>
              </a-input>
            </a-form-item>

            <a-form-item
              name="lastName"
              :rules="[{ required: true, message: 'Enter last name' }]"
              class="name-field"
            >
              <a-input 
                v-model:value="formState.lastName" 
                placeholder="Last name"
                size="large"
                class="form-input"
              >
                <template #prefix><UserOutlined class="input-icon" /></template>
              </a-input>
            </a-form-item>
          </div>

          <!-- Middle Name -->
          <a-form-item name="middleName">
            <a-input 
              v-model:value="formState.middleName" 
              placeholder="Middle name (optional)"
              size="large"
              class="form-input"
            >
              <template #prefix><UserOutlined class="input-icon" /></template>
            </a-input>
          </a-form-item>

          <!-- Email -->
          <a-form-item
            name="email"
            :rules="[{ required: true, type:'email', message: 'Enter valid email' }]"
          >
            <a-input 
              v-model:value="formState.email" 
              placeholder="Email address"
              size="large"
              class="form-input"
            >
              <template #prefix><MailOutlined class="input-icon" /></template>
            </a-input>
          </a-form-item>

          <!-- Role -->
          <a-form-item
            name="role"
            :rules="[{ required: true, message: 'Please select a role' }]"
          >
            <a-radio-group v-model:value="formState.role" size="large" class="role-group">
              <a-radio-button value="poa" class="role-button">
                <UserOutlined /> POA
              </a-radio-button>
              <a-radio-button value="worker" class="role-button">
                <SolutionOutlined /> Worker
              </a-radio-button>
              <a-radio-button value="manager" class="role-button">
                <TeamOutlined /> Manager
              </a-radio-button>
            </a-radio-group>
          </a-form-item>

          <!-- Password -->
          <a-form-item
            name="password"
            :rules="[{ required: true, message: 'Enter password' }]"
          >
            <a-input-password 
              v-model:value="formState.password" 
              placeholder="Password"
              size="large"
              class="form-input"
            >
              <template #prefix><LockOutlined class="input-icon" /></template>
            </a-input-password>
          </a-form-item>

          <!-- Confirm Password -->
          <a-form-item
            name="confirm"
            :rules="[{ required: true, message: 'Confirm password' }]"
          >
            <a-input-password 
              v-model:value="formState.confirm" 
              placeholder="Confirm password"
              size="large"
              class="form-input"
            >
              <template #prefix><LockOutlined class="input-icon" /></template>
            </a-input-password>
          </a-form-item>

          <!-- Required Note -->
          <div class="required-note">
            <span class="asterisk">*</span> Required fields
          </div>

          <!-- Submit -->
          <a-form-item>
            <a-button 
              type="primary" 
              html-type="submit" 
              :loading="loading" 
              block
              size="large"
              class="submit-button"
            >
              Create Account
            </a-button>
          </a-form-item>

          <!-- Divider -->
          <div class="divider">
            <span>or</span>
          </div>

          <!-- Sign in -->
          <div class="signin-link">
            Already have an account?
            <a class="link-text" @click="onBack">Sign In</a>
          </div>
        </a-form>
      </div>

      <!-- Footer -->
      <div class="footer-text">
        <a @click="goToLanding" class="back-link">← Back to Home</a>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue';
import { message } from 'ant-design-vue';
import { 
  UserOutlined, 
  MailOutlined, 
  LockOutlined, 
  HeartOutlined,
  SolutionOutlined,
  TeamOutlined
} from '@ant-design/icons-vue';
import { register } from '@/services/userService';
import { useRouter } from 'vue-router';

const router = useRouter();

const formState = reactive({
  firstName: '',
  middleName: '',
  lastName: '',
  email: '',
  password: '',
  confirm: '',
  role: ''
});

const loading = ref(false);

const onFinish = async (values) => {
  loading.value = true;
  try {
    if (values.password !== values.confirm) {
      throw new Error('Passwords do not match');
    }
    
    const roleMapping = {
      'poa': 'POA',
      'worker': 'WORKER', 
      'manager': 'MANAGER'
    };
    
    const userData = {
      firstName: values.firstName,
      middleName: values.middleName,
      lastName: values.lastName,
      email: values.email,
      password: values.password,
      role: values.role,
      userType: roleMapping[values.role] || 'WORKER'
    };
    
    const result = await register(userData);
    
    if (result.data) {
      message.success('Registration successful! Please login with your credentials.');
      router.push('/login');
    }
  } catch (e) {
    console.error('Registration error:', e);
    message.error(e?.message || 'Registration failed');
  } finally {
    loading.value = false;
  }
}

const onFinishFailed = () => message.error('Please check the form');

const onBack = () => {
  router.push('/login');
}

const goToLanding = () => {
  router.push('/');
}
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  position: relative;
  overflow: hidden;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.bg-gradient {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  overflow: hidden;
  z-index: 0;
}

.gradient-circle {
  position: absolute;
  border-radius: 50%;
  filter: blur(60px);
  opacity: 0.4;
  animation: float 15s infinite ease-in-out;
}

.circle-1 {
  width: 300px;
  height: 300px;
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  top: -100px;
  right: -100px;
  animation-delay: 0s;
}

.circle-2 {
  width: 400px;
  height: 400px;
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  bottom: -150px;
  left: -150px;
  animation-delay: 5s;
}

.circle-3 {
  width: 250px;
  height: 250px;
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  animation-delay: 10s;
}

@keyframes float {
  0%, 100% {
    transform: translate(0, 0) scale(1);
  }
  50% {
    transform: translate(30px, -30px) scale(1.1);
  }
}

.register-content {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 600px;
}

.logo-section {
  text-align: center;
  margin-bottom: 32px;
}

.logo-icon {
  width: 64px;
  height: 64px;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(20px);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 16px;
  font-size: 32px;
  color: white;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.logo-text {
  font-size: 36px;
  font-weight: 700;
  color: white;
  margin: 0 0 8px;
  text-shadow: 0 2px 20px rgba(0, 0, 0, 0.2);
}

.logo-subtitle {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.9);
  margin: 0;
}

.form-wrapper {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 24px;
  padding: 48px 40px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.form-title {
  font-size: 28px;
  font-weight: 700;
  color: #1a1a1a;
  text-align: center;
  margin: 0 0 32px;
}

.register-form :deep(.ant-form-item) {
  margin-bottom: 20px;
}

.name-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.name-field {
  margin-bottom: 20px;
}

.form-input {
  border-radius: 12px;
  padding: 12px 16px;
  font-size: 16px;
  border: 2px solid #e0e0e0;
  transition: all 0.3s ease;
}

.form-input:hover {
  border-color: #667eea;
}

.form-input:focus,
.form-input-focused {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.input-icon {
  color: #999;
  font-size: 16px;
}

.role-group {
  width: 100%;
  display: flex;
  gap: 8px;
}

.role-group :deep(.ant-radio-button-wrapper) {
  flex: 1;
  text-align: center;
  height: 48px;
  line-height: 48px;
  border-radius: 12px;
  border: 2px solid #e0e0e0;
  transition: all 0.3s ease;
  font-weight: 500;
}

.role-group :deep(.ant-radio-button-wrapper:hover) {
  border-color: #667eea;
}

.role-group :deep(.ant-radio-button-wrapper-checked) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-color: #667eea;
  color: white;
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.3);
}

.role-button {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.required-note {
  text-align: center;
  color: #999;
  font-size: 13px;
  margin: -12px 0 20px;
}

.asterisk {
  color: #ff4d4f;
  margin-right: 4px;
}

.submit-button {
  height: 52px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.3);
  transition: all 0.3s ease;
}

.submit-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

.divider {
  text-align: center;
  margin: 32px 0;
  position: relative;
}

.divider::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 0;
  right: 0;
  height: 1px;
  background: #e0e0e0;
}

.divider span {
  background: rgba(255, 255, 255, 0.95);
  padding: 0 16px;
  color: #999;
  position: relative;
}

.signin-link {
  text-align: center;
  color: #666;
  font-size: 15px;
}

.link-text {
  color: #667eea;
  font-weight: 600;
  cursor: pointer;
  margin-left: 8px;
  transition: color 0.3s ease;
}

.link-text:hover {
  color: #764ba2;
}

.footer-text {
  text-align: center;
  margin-top: 24px;
}

.back-link {
  color: rgba(255, 255, 255, 0.9);
  font-size: 14px;
  cursor: pointer;
  transition: color 0.3s ease;
  text-decoration: none;
}

.back-link:hover {
  color: white;
}

/* Responsive */
@media (max-width: 768px) {
  .form-wrapper {
    padding: 32px 24px;
  }

  .form-title {
    font-size: 24px;
  }

  .name-row {
    grid-template-columns: 1fr;
  }

  .role-group {
    flex-direction: column;
  }

  .logo-text {
    font-size: 28px;
  }
}
</style>
