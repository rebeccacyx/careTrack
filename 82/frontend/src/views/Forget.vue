<template>
  <div class="forget-container">
    <!-- Background with animated gradient -->
    <div class="bg-gradient">
      <div class="gradient-circle circle-1"></div>
      <div class="gradient-circle circle-2"></div>
      <div class="gradient-circle circle-3"></div>
    </div>

    <!-- Content -->
    <div class="forget-content">
      <!-- Logo Section -->
      <div class="logo-section">
        <div class="logo-icon">
          <HeartOutlined />
        </div>
        <h1 class="logo-text">CareTrack</h1>
        <p class="logo-subtitle">Recover your account password</p>
      </div>

      <!-- Forget Password Form -->
      <div class="form-wrapper">
        <a-form
          :model="formState"
          name="forgot"
          autocomplete="off"
          @finish="onFinish"
          @finishFailed="onFinishFailed"
          :disabled="loading"
          class="forget-form"
        >
          <div class="icon-wrapper">
            <LockOutlined class="lock-icon" />
          </div>

          <h2 class="form-title">Forgot Password?</h2>
          <p class="form-description">
            No worries! Enter your email address and we'll send you a password reset token.
          </p>

          <!-- Email -->
          <a-form-item
            name="email"
            :rules="[{ required: true, type:'email', message: 'Enter valid email' }]"
          >
            <a-input 
              v-model:value="formState.email" 
              placeholder="Enter your email address"
              size="large"
              class="form-input"
            >
              <template #prefix><MailOutlined class="input-icon" /></template>
            </a-input>
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
              <template #icon><SendOutlined /></template>
              Send Reset Link
            </a-button>
          </a-form-item>

          <!-- Back to Sign In -->
          <div class="back-link-section">
            <a class="back-link-text" @click="onBack">
              <ArrowLeftOutlined /> Back to Sign In
            </a>
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
  MailOutlined,
  LockOutlined,
  HeartOutlined,
  SendOutlined,
  ArrowLeftOutlined
} from '@ant-design/icons-vue';
import { forgotPassword } from '@/services/userService';
import { useRouter } from 'vue-router';

const router = useRouter();

const formState = reactive({ email: '' });
const loading = ref(false);

const onFinish = async (values) => {
  loading.value = true;
  try {
    const result = await forgotPassword(values.email);
    
    if (result.data) {
      message.success('Password reset token has been sent! Please check your email or contact administrator for the reset token.');
      formState.email = '';
    }
  } catch (e) {
    console.error('Forgot password error:', e);
    message.error(e?.message || 'Failed to send recovery link');
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
.forget-container {
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

.forget-content {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 480px;
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

.icon-wrapper {
  display: flex;
  justify-content: center;
  margin-bottom: 24px;
}

.lock-icon {
  font-size: 64px;
  color: #667eea;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
  width: 120px;
  height: 120px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto;
}

.form-title {
  font-size: 28px;
  font-weight: 700;
  color: #1a1a1a;
  text-align: center;
  margin: 0 0 12px;
}

.form-description {
  font-size: 15px;
  color: #666;
  text-align: center;
  margin: 0 0 32px;
  line-height: 1.6;
}

.forget-form :deep(.ant-form-item) {
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

.back-link-section {
  text-align: center;
  margin-top: 24px;
}

.back-link-text {
  color: #667eea;
  font-weight: 500;
  cursor: pointer;
  font-size: 15px;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s ease;
}

.back-link-text:hover {
  color: #764ba2;
  transform: translateX(-4px);
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

  .lock-icon {
    font-size: 48px;
    width: 100px;
    height: 100px;
  }

  .logo-text {
    font-size: 28px;
  }
}
</style>
