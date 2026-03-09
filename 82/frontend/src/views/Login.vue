<template>
  <div class="login-container">
    <!-- Background with animated gradient -->
    <div class="bg-gradient">
      <div class="gradient-circle circle-1"></div>
      <div class="gradient-circle circle-2"></div>
      <div class="gradient-circle circle-3"></div>
    </div>

    <!-- Content -->
    <div class="login-content">
      <!-- Logo Section -->
      <div class="logo-section">
        <div class="logo-icon">
          <HeartOutlined />
        </div>
        <h1 class="logo-text">CareTrack</h1>
        <p class="logo-subtitle">Welcome back! Sign in to continue</p>
      </div>

      <!-- Login Form -->
      <div class="form-wrapper">
        <a-form
          :model="formState"
          name="Login"
          autocomplete="off"
          @finish="onFinish"
          @finishFailed="onFinishFailed"
          :disabled="loading"
          class="login-form"
        >
          <h2 class="form-title">Sign In</h2>

          <!-- Email -->
          <a-form-item
            name="email"
            :rules="[
              { required: true, message: 'Please input your Email!' },
              { type: 'email', message: 'Email format is invalid' }
            ]"
          >
            <a-input 
              v-model:value="formState.email" 
              placeholder="Enter your email"
              size="large"
              class="form-input"
            >
              <template #prefix><MailOutlined class="input-icon" /></template>
            </a-input>
          </a-form-item>

          <!-- Password -->
          <a-form-item
            name="password"
            :rules="[{ required: true, message: 'Please input your password!' }]"
          >
            <a-input-password 
              v-model:value="formState.password" 
              placeholder="Enter your password"
              size="large"
              class="form-input"
            >
              <template #prefix><LockOutlined class="input-icon" /></template>
            </a-input-password>
          </a-form-item>

          <!-- Remember + Forgot -->
          <a-form-item class="remember-forgot">
            <div class="row-between">
              <a-checkbox v-model:checked="formState.remember" class="remember-checkbox">
                Remember me
              </a-checkbox>
              <a class="forgot-link" @click="onForgot">Forgot password?</a>
            </div>
          </a-form-item>

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
              Sign In
            </a-button>
          </a-form-item>

          <!-- Divider -->
          <div class="divider">
            <span>or</span>
          </div>

          <!-- Sign up -->
          <div class="signup-link">
            Don't have an account?
            <a class="link-text" @click="onRegister">Create Account</a>
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
import { reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import { MailOutlined, LockOutlined, HeartOutlined } from '@ant-design/icons-vue'
import { useRouter, useRoute } from 'vue-router'
import { login } from '@/services/userService'

const router = useRouter()
const route = useRoute()

const formState = reactive({
  email: '',
  password: '',
  remember: true,
})

const loading = ref(false)

const onFinish = async () => {
  loading.value = true
  try {
    const res = await login({
      email: formState.email,
      password: formState.password,
    })

    const token = res?.data?.token
    if (token) {
      sessionStorage.setItem('token', token)
    }
    message.success('Signed in successfully!')

    if (window.clearRouterCache) {
      window.clearRouterCache()
    }

    try {
      const userInfo = res?.data?.user || { role: 'user' }
      const role = userInfo.role
      
      if (role === 'worker' || role === 'manager') {
        const inviteStatus = res?.data?.inviteStatus || { valid: false }
        
        if (!inviteStatus.valid && inviteStatus.reason === 'missing') {
          router.replace('/invitecode')
          return
        }
      }
      
      const redirect = route.query.redirect || '/app/menu'
      router.replace(String(redirect))
    } catch (e) {
      const redirect = route.query.redirect || '/app/menu'
      router.replace(String(redirect))
    }
  } catch (e) {
    const msg = e?.response?.data?.message || e?.message || 'Login failed'
    message.error(msg)
  } finally {
    loading.value = false
  }
}

const onFinishFailed = () => {
  message.error('Please check the form')
}

const onForgot = () => {
  router.push('/forgot-password')
}

const onRegister = () => {
  router.push('/register')
}

const goToLanding = () => {
  router.push('/')
}
</script>

<style scoped>
.login-container {
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

.login-content {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 480px;
}

.logo-section {
  text-align: center;
  margin-bottom: 40px;
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

.login-form :deep(.ant-form-item) {
  margin-bottom: 24px;
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

.remember-forgot {
  margin-bottom: 8px;
}

.row-between {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.remember-checkbox {
  color: #666;
}

.forgot-link {
  color: #667eea;
  font-weight: 500;
  cursor: pointer;
  transition: color 0.3s ease;
}

.forgot-link:hover {
  color: #764ba2;
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

.signup-link {
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

  .logo-text {
    font-size: 28px;
  }
}
</style>
