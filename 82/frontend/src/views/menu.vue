<template>
    <div style="width:100%; height:100%">
      <!-- User Info Section -->
      <div style="padding: 16px; border-bottom: 1px solid #f0f0f0; background: #fafafa;">
        <div style="text-align: center;">
          <a-avatar :size="48" style="margin-bottom: 8px;">
            <template #icon><UserOutlined /></template>
          </a-avatar>
          <div style="font-size: 16px; font-weight: 600; color: #1890ff; margin-bottom: 4px;">
            {{ userName }}
          </div>
          <div style="font-size: 12px; color: #666; text-transform: uppercase;">
            {{ userRole }}
          </div>
        </div>
      </div>
      
      <!-- Menu Section -->
      <a-menu
        v-model:selectedKeys="selectedKeys"
        mode="inline"
        @click="handleClick"
        style="border: none; background: white;"
      >
        <a-menu-item key="home" style="color: #000; font-size: 14px;">
          <template #icon><HomeOutlined /></template>
          Home
        </a-menu-item>
        
        <a-menu-item key="worker-management" style="color: #000; font-size: 14px;">
          <template #icon><AppstoreOutlined /></template>
          Worker Management
        </a-menu-item>
        
        <a-menu-item key="budget" style="color: #000; font-size: 14px;">
          <template #icon><BarChartOutlined /></template>
          Budget
        </a-menu-item>
        
        <a-menu-item key="tasks" style="color: #000; font-size: 14px;">
          <template #icon><CheckSquareOutlined /></template>
          Tasks
        </a-menu-item>
        
        <a-menu-item key="upload" style="color: #000; font-size: 14px;">
          <template #icon><SettingOutlined /></template>
          Upload
        </a-menu-item>
        
        <a-menu-item key="communication" style="color: #000; font-size: 14px;">
          <template #icon><MessageOutlined /></template>
          Communication
        </a-menu-item>
        
        <a-menu-item key="carer-team" style="color: #000; font-size: 14px;">
          <template #icon><AppstoreOutlined /></template>
          Carer Team
        </a-menu-item>
        
        <a-menu-item key="setting" style="color: #000; font-size: 14px;">
          <template #icon><SettingOutlined /></template>
          Setting
        </a-menu-item>
      </a-menu>
    </div>
  </template>
  
  <script setup>
  import { ref, computed, h, watchEffect, onMounted } from 'vue'
  import { useRouter, useRoute } from 'vue-router'
  import {
    MailOutlined, AppstoreOutlined, SettingOutlined,
    HomeOutlined, CheckSquareOutlined, CalendarOutlined, BarChartOutlined,
    HeartOutlined, MessageOutlined, UserOutlined, QuestionCircleOutlined
  } from '@ant-design/icons-vue'
  import { getMe } from '@/services/userService'

  const props = defineProps({
    role: { type: String, default: 'worker' } 
  })
  
  const userRole = ref('worker')
  const userName = ref('Test User')
  
  onMounted(async () => {
    try {
      console.log('Menu component mounted, fetching user info...')
      const userInfo = await getMe()
      console.log('User info received:', userInfo)
      userRole.value = userInfo?.data?.role || 'worker'
      userName.value = userInfo?.data?.name || 'Test User'
      console.log('Set userRole to:', userRole.value)
      console.log('Set userName to:', userName.value)
    } catch (e) {
      console.error('Failed to get user info:', e)
    }
  })
  
  const router = useRouter()
  const route = useRoute()
  
  
  const selectedKeys = ref([])
  
  
  // Menu items are now defined directly in the template
  
  // —— Route mapping ——
  // key -> path
  const keyToPath = {
    home: '/app/menu',
    tasks: '/app/tasks',
    'carer-team': '/app/carer-team',
    budget: '/app/budget',
    upload: '/app/upload',
    setting: '/app/setting',
    'worker-management': '/app/worker-management',
    communication: '/app/communication',
  }
  // Reverse: path -> key (for highlighting)
  const pathToKey = Object.fromEntries(Object.entries(keyToPath).map(([k, v]) => [v, k]))
  
  // Auto highlight when route changes
  watchEffect(() => {
    const k = pathToKey[route.path] || 'home'
    selectedKeys.value = [k]
  })
  
  // Click to navigate
  function handleClick({ key }) {
    const path = keyToPath[key]
    if (path) router.push(path)
  }
  
  </script>
  