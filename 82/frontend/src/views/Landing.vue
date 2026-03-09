<template>
  <div class="landing-container">
    <!-- Background with animated elements -->
    <div class="bg-animation">
      <div class="gradient-orb orb-1"></div>
      <div class="gradient-orb orb-2"></div>
      <div class="gradient-orb orb-3"></div>
    </div>

    <!-- Main Content -->
    <div class="landing-content">
      <!-- Header -->
      <header class="landing-header">
        <div class="logo-section">
          <div class="logo-icon">
            <HeartOutlined />
          </div>
          <h1 class="logo-text">CareTrack</h1>
        </div>
        <div class="header-actions">
          <a-button type="text" class="header-btn" @click="goToLogin">
            Sign In
          </a-button>
          <a-button type="primary" class="header-btn header-btn-primary" @click="goToRegister">
            Get Started
          </a-button>
        </div>
      </header>

      <!-- Hero Section -->
      <section class="hero-section">
        <div class="hero-content">
          <h1 class="hero-title">
            Care Management
            <span class="gradient-text">Made Simple</span>
          </h1>
          <p class="hero-subtitle">
            A unified care management platform for families, caregivers, and organizations. 
            Coordinate care, manage tasks, track budgets, and monitor health — all in one place.
          </p>
          <div class="hero-actions">
            <a-button type="primary" size="large" class="cta-button" @click="goToRegister">
              <template #icon><UserAddOutlined /></template>
              Start Free Trial
            </a-button>
            <a-button size="large" class="cta-button-secondary" @click="goToLogin">
              Sign In
            </a-button>
          </div>
        </div>
      </section>

      <!-- Features Section -->
      <section class="features-section">
        <div class="feature-grid">
          <div class="feature-card">
            <div class="feature-icon">
              <TeamOutlined />
            </div>
            <h3 class="feature-title">Team Coordination</h3>
            <p class="feature-description">
              Seamlessly coordinate between managers, caregivers, and families
            </p>
          </div>

          <div class="feature-card">
            <div class="feature-icon">
              <CheckSquareOutlined />
            </div>
            <h3 class="feature-title">Task Management</h3>
            <p class="feature-description">
              Assign, track, and monitor caregiving tasks in real-time
            </p>
          </div>

          <div class="feature-card">
            <div class="feature-icon">
              <DollarOutlined />
            </div>
            <h3 class="feature-title">Budget Tracking</h3>
            <p class="feature-description">
              Monitor care budgets with smart alerts and detailed reports
            </p>
          </div>

          <div class="feature-card">
            <div class="feature-icon">
              <MessageOutlined />
            </div>
            <h3 class="feature-title">Communication</h3>
            <p class="feature-description">
              Stay connected with built-in messaging and notifications
            </p>
          </div>

          <div class="feature-card">
            <div class="feature-icon">
              <FileTextOutlined />
            </div>
            <h3 class="feature-title">Document Management</h3>
            <p class="feature-description">
              Upload, organize, and access important documents securely
            </p>
          </div>

          <div class="feature-card">
            <div class="feature-icon">
              <SafetyOutlined />
            </div>
            <h3 class="feature-title">Secure & Private</h3>
            <p class="feature-description">
              Enterprise-grade security to protect sensitive care information
            </p>
          </div>
        </div>
      </section>

      <!-- CTA Section -->
      <section class="cta-section">
        <div class="cta-content">
          <h2 class="cta-title">Ready to Transform Care Management?</h2>
          <p class="cta-subtitle">Join thousands of families and caregivers using CareTrack</p>
          <a-button type="primary" size="large" class="cta-button-large" @click="goToRegister">
            <template #icon><RocketOutlined /></template>
            Get Started Today
          </a-button>
        </div>
      </section>

      <!-- Footer -->
      <footer class="landing-footer">
        <p>&copy; 2024 CareTrack. All rights reserved.</p>
        <div class="footer-links">
          <a @click="goToLogin">Sign In</a>
          <a @click="goToRegister">Register</a>
        </div>
      </footer>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import {
  HeartOutlined,
  UserAddOutlined,
  TeamOutlined,
  CheckSquareOutlined,
  DollarOutlined,
  MessageOutlined,
  FileTextOutlined,
  SafetyOutlined,
  RocketOutlined
} from '@ant-design/icons-vue'

const router = useRouter()

const goToLogin = () => {
  router.push('/login')
}

const goToRegister = () => {
  router.push('/register')
}
</script>

<style scoped>
.landing-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
  position: relative;
  overflow: hidden;
}

.bg-animation {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  overflow: hidden;
  z-index: 0;
}

.gradient-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.3;
  animation: float 20s infinite ease-in-out;
}

.orb-1 {
  width: 400px;
  height: 400px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  top: -100px;
  left: -100px;
  animation-delay: 0s;
}

.orb-2 {
  width: 500px;
  height: 500px;
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  bottom: -150px;
  right: -150px;
  animation-delay: 5s;
}

.orb-3 {
  width: 350px;
  height: 350px;
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  animation-delay: 10s;
}

@keyframes float {
  0%, 100% {
    transform: translate(0, 0) scale(1);
  }
  25% {
    transform: translate(50px, -50px) scale(1.1);
  }
  50% {
    transform: translate(-30px, 30px) scale(0.9);
  }
  75% {
    transform: translate(40px, 40px) scale(1.05);
  }
}

.landing-content {
  position: relative;
  z-index: 1;
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 24px;
}

/* Header */
.landing-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 32px 0;
}

.logo-section {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-icon {
  width: 48px;
  height: 48px;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(20px);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.logo-text {
  font-size: 32px;
  font-weight: 700;
  color: white;
  margin: 0;
  text-shadow: 0 2px 20px rgba(0, 0, 0, 0.2);
}

.header-actions {
  display: flex;
  gap: 16px;
  align-items: center;
}

.header-btn {
  color: white;
  font-weight: 600;
  height: 44px;
  padding: 0 24px;
  border-radius: 12px;
  transition: all 0.3s ease;
}

.header-btn:hover {
  background: rgba(255, 255, 255, 0.1);
  color: white;
}

.header-btn-primary {
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.header-btn-primary:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);
}

/* Hero Section */
.hero-section {
  padding: 120px 0 80px;
  text-align: center;
}

.hero-content {
  max-width: 800px;
  margin: 0 auto;
}

.hero-title {
  font-size: 64px;
  font-weight: 800;
  color: white;
  margin: 0 0 24px;
  line-height: 1.2;
  text-shadow: 0 4px 30px rgba(0, 0, 0, 0.2);
}

.gradient-text {
  background: linear-gradient(135deg, #fff 0%, #f0f0f0 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.hero-subtitle {
  font-size: 20px;
  color: rgba(255, 255, 255, 0.9);
  margin: 0 0 48px;
  line-height: 1.6;
}

.hero-actions {
  display: flex;
  gap: 16px;
  justify-content: center;
  flex-wrap: wrap;
}

.cta-button {
  height: 56px;
  padding: 0 40px;
  font-size: 18px;
  font-weight: 600;
  border-radius: 16px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);
  transition: all 0.3s ease;
}

.cta-button:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.3);
}

.cta-button-secondary {
  height: 56px;
  padding: 0 40px;
  font-size: 18px;
  font-weight: 600;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(20px);
  border: 2px solid rgba(255, 255, 255, 0.3);
  color: white;
  transition: all 0.3s ease;
}

.cta-button-secondary:hover {
  background: rgba(255, 255, 255, 0.25);
  border-color: rgba(255, 255, 255, 0.5);
  transform: translateY(-4px);
  color: white;
}

/* Features Section */
.features-section {
  padding: 100px 0;
}

.feature-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 32px;
  max-width: 1200px;
  margin: 0 auto;
}

.feature-card {
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 24px;
  padding: 40px 32px;
  text-align: center;
  transition: all 0.3s ease;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.feature-card:hover {
  transform: translateY(-8px);
  background: rgba(255, 255, 255, 0.2);
  box-shadow: 0 16px 48px rgba(0, 0, 0, 0.2);
}

.feature-icon {
  width: 80px;
  height: 80px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 24px;
  font-size: 36px;
  color: white;
  transition: all 0.3s ease;
}

.feature-card:hover .feature-icon {
  transform: scale(1.1) rotate(5deg);
  background: rgba(255, 255, 255, 0.3);
}

.feature-title {
  font-size: 22px;
  font-weight: 700;
  color: white;
  margin: 0 0 16px;
}

.feature-description {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.85);
  line-height: 1.6;
  margin: 0;
}

/* CTA Section */
.cta-section {
  padding: 100px 0;
  text-align: center;
}

.cta-content {
  max-width: 600px;
  margin: 0 auto;
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 32px;
  padding: 64px 48px;
  box-shadow: 0 16px 48px rgba(0, 0, 0, 0.2);
}

.cta-title {
  font-size: 42px;
  font-weight: 700;
  color: white;
  margin: 0 0 16px;
}

.cta-subtitle {
  font-size: 18px;
  color: rgba(255, 255, 255, 0.9);
  margin: 0 0 32px;
}

.cta-button-large {
  height: 60px;
  padding: 0 48px;
  font-size: 20px;
  font-weight: 600;
  border-radius: 16px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);
  transition: all 0.3s ease;
}

.cta-button-large:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.3);
}

/* Footer */
.landing-footer {
  padding: 48px 0;
  border-top: 1px solid rgba(255, 255, 255, 0.2);
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: rgba(255, 255, 255, 0.8);
}

.footer-links {
  display: flex;
  gap: 24px;
}

.footer-links a {
  color: rgba(255, 255, 255, 0.8);
  cursor: pointer;
  transition: color 0.3s ease;
  font-weight: 500;
}

.footer-links a:hover {
  color: white;
}

/* Responsive */
@media (max-width: 768px) {
  .hero-title {
    font-size: 40px;
  }

  .hero-subtitle {
    font-size: 18px;
  }

  .feature-grid {
    grid-template-columns: 1fr;
  }

  .cta-title {
    font-size: 32px;
  }

  .landing-footer {
    flex-direction: column;
    gap: 16px;
  }
}
</style>




