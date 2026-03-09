<template>
  <div class="home-page">
    <!-- Welcome Banner -->
    <div class="welcome-banner">
      <div class="banner-content">
        <div class="banner-left">
          <h1 class="welcome-title">
            Welcome back, <span class="user-name">{{ userName || 'User' }}</span>!
          </h1>
          <p class="welcome-subtitle">Here's your dashboard overview</p>
        </div>
        <div class="banner-right">
          <div class="role-badge" :class="`role-${userRole}`">
            <component :is="getRoleIcon()" class="role-icon" />
            <span>{{ roleDisplay }}</span>
          </div>
        </div>
      </div>
      <div class="banner-decoration">
        <div class="decoration-circle circle-1"></div>
        <div class="decoration-circle circle-2"></div>
        <div class="decoration-circle circle-3"></div>
      </div>
    </div>

    <!-- Stats Cards (for non-worker) -->
    <a-row v-if="userRole !== 'worker'" :gutter="16" class="stats-row">
      <a-col :xs="24" :sm="12" :md="8">
        <div class="stat-card stat-budget">
          <div class="stat-icon">
            <DollarOutlined />
          </div>
          <div class="stat-content">
            <div class="stat-label">Total Budget</div>
            <div class="stat-value">${{ totalBudget.toLocaleString() }}</div>
          </div>
        </div>
      </a-col>
      <a-col :xs="24" :sm="12" :md="8">
        <div class="stat-card stat-used">
          <div class="stat-icon">
            <ShoppingCartOutlined />
          </div>
          <div class="stat-content">
            <div class="stat-label">Total Used</div>
            <div class="stat-value">${{ totalUsed.toLocaleString() }}</div>
            <div class="stat-percentage">{{ totalUsagePct }}%</div>
          </div>
        </div>
      </a-col>
      <a-col :xs="24" :sm="12" :md="8">
        <div class="stat-card stat-left" :class="{ 'stat-negative': totalLeft < 0 }">
          <div class="stat-icon">
            <WalletOutlined />
          </div>
          <div class="stat-content">
            <div class="stat-label">Money Left</div>
            <div class="stat-value">${{ totalLeft.toLocaleString() }}</div>
          </div>
        </div>
      </a-col>
    </a-row>

    <!-- Worker Stats -->
    <a-row v-if="userRole === 'worker'" :gutter="16" class="stats-row">
      <a-col :xs="24" :sm="12" :md="8">
        <div class="stat-card stat-tasks">
          <div class="stat-icon">
            <CheckSquareOutlined />
          </div>
          <div class="stat-content">
            <div class="stat-label">Total Tasks</div>
            <div class="stat-value">{{ taskPreview.length }}</div>
          </div>
        </div>
      </a-col>
      <a-col :xs="24" :sm="12" :md="8">
        <div class="stat-card stat-schedule">
          <div class="stat-icon">
            <CalendarOutlined />
          </div>
          <div class="stat-content">
            <div class="stat-label">Upcoming Shifts</div>
            <div class="stat-value">{{ workerSchedule.length }}</div>
          </div>
        </div>
      </a-col>
      <a-col :xs="24" :sm="12" :md="8">
        <div class="stat-card stat-upload">
          <div class="stat-icon">
            <FileTextOutlined />
          </div>
          <div class="stat-content">
            <div class="stat-label">Recent Files</div>
            <div class="stat-value">{{ uploadPreview.length }}</div>
          </div>
        </div>
      </a-col>
    </a-row>

    <!-- Budget Preview (Non-Worker) -->
    <a-row v-if="userRole !== 'worker'" :gutter="16" class="content-row">
      <a-col :xs="24" :lg="14">
        <div class="preview-card budget-card">
          <div class="card-header">
            <div class="header-left">
              <div class="card-icon budget-icon">
                <BarChartOutlined />
              </div>
              <div>
                <h3 class="card-title">Budget Overview</h3>
                <p class="card-subtitle">Track your spending and budget usage</p>
              </div>
            </div>
            <router-link to="/app/budget">
              <a-button type="primary" ghost class="view-all-btn">
                View Details <RightOutlined />
              </a-button>
            </router-link>
          </div>

          <div class="budget-progress">
            <div class="progress-header">
              <span>Overall Budget Usage</span>
              <a-tag :color="budgetUsageColor" class="usage-tag">{{ totalUsagePct }}% used</a-tag>
            </div>
            <a-progress
              :percent="totalUsagePct"
              :stroke-color="budgetUsageColor === 'red' ? '#ff4d4f' : budgetUsageColor === 'orange' ? '#fa8c16' : '#52c41a'"
              :show-info="false"
              :stroke-width="12"
              class="usage-progress"
            />
            <div class="progress-stats">
              <div class="stat-item">
                <span class="stat-label-small">Available</span>
                <span class="stat-value-small" :class="{ 'text-danger': totalLeft < 0 }">
                  ${{ totalLeft.toLocaleString() }}
                </span>
              </div>
              <div class="stat-item">
                <span class="stat-label-small">Used</span>
                <span class="stat-value-small">${{ totalUsed.toLocaleString() }}</span>
              </div>
              <div class="stat-item">
                <span class="stat-label-small">Total</span>
                <span class="stat-value-small">${{ totalBudget.toLocaleString() }}</span>
              </div>
            </div>
          </div>

          <!-- Top Categories -->
          <div v-if="topCategoryWarnings.length > 0" class="top-categories">
            <h4 class="section-title">
              <ExclamationCircleOutlined /> Top Categories Near/Over Budget
            </h4>
            <div class="category-list">
              <div
                v-for="(category, index) in topCategoryWarnings"
                :key="index"
                class="category-item"
              >
                <div class="category-info">
                  <span class="category-name">{{ category.category }}</span>
                  <span class="category-usage">
                    ${{ category.used.toLocaleString() }} / ${{ category.budget.toLocaleString() }}
                  </span>
                </div>
                <div class="category-progress-wrapper">
                  <a-progress
                    :percent="category.pct"
                    :stroke-color="category.pct >= 100 ? '#ff4d4f' : category.pct >= 80 ? '#fa8c16' : '#52c41a'"
                    :show-info="false"
                    :stroke-width="6"
                  />
                  <a-tag
                    :color="category.pct >= 100 ? 'red' : category.pct >= 80 ? 'orange' : 'green'"
                    class="category-tag"
                  >
                    {{ category.pct }}%
                  </a-tag>
                </div>
              </div>
            </div>
          </div>
          <div v-else class="empty-state">
            <BarChartOutlined class="empty-icon" />
            <p>No budget data available</p>
          </div>
        </div>
      </a-col>

      <!-- Tasks Preview -->
      <a-col :xs="24" :lg="10">
        <div class="preview-card tasks-card">
          <div class="card-header">
            <div class="header-left">
              <div class="card-icon tasks-icon">
                <CheckSquareOutlined />
              </div>
              <div>
                <h3 class="card-title">Recent Tasks</h3>
                <p class="card-subtitle">Your latest tasks and updates</p>
              </div>
            </div>
            <router-link to="/app/tasks">
              <a-button type="link" class="view-link">View all</a-button>
            </router-link>
          </div>

          <div v-if="taskPreview.length > 0" class="task-list">
            <div
              v-for="task in taskPreview"
              :key="task.id"
              class="task-item"
              @click="$router.push('/app/tasks')"
            >
              <div class="task-main">
                <div class="task-title-wrapper">
                  <span class="task-title">{{ task.title }}</span>
                  <a-tag :color="getPriorityColor(task.priority)" class="task-priority">
                    {{ getPriorityText(task.priority) }}
                  </a-tag>
                </div>
                <div class="task-meta">
                  <span class="task-due">Due: {{ task.dueDate || 'N/A' }}</span>
                  <a-tag :color="getStatusColor(task.status)">{{ task.status }}</a-tag>
                </div>
              </div>
            </div>
          </div>
          <div v-else class="empty-state">
            <CheckSquareOutlined class="empty-icon" />
            <p>No tasks available</p>
          </div>
        </div>
      </a-col>
    </a-row>

    <!-- Worker: Schedule + Tasks -->
    <a-row v-if="userRole === 'worker'" :gutter="16" class="content-row">
      <!-- Schedule Preview -->
      <a-col :xs="24" :lg="12">
        <div class="preview-card schedule-card">
          <div class="card-header">
            <div class="header-left">
              <div class="card-icon schedule-icon">
                <ClockCircleOutlined />
              </div>
              <div>
                <h3 class="card-title">My Schedule</h3>
                <p class="card-subtitle">Upcoming shifts and assignments</p>
              </div>
            </div>
            <a-button type="link" @click="showScheduleModal" class="view-link">View all</a-button>
          </div>

          <div v-if="workerSchedule.length > 0" class="schedule-list">
            <div
              v-for="(item, index) in workerSchedule"
              :key="index"
              class="schedule-item"
            >
              <div class="schedule-date">
                <div class="date-day">{{ getDayFromDate(item.date) }}</div>
                <div class="date-month">{{ getMonthFromDate(item.date) }}</div>
              </div>
              <div class="schedule-info">
                <div class="schedule-shift">
                  <a-tag :color="getShiftColor(item.shift)" class="shift-tag">
                    {{ item.shift }}
                  </a-tag>
                </div>
                <div class="schedule-time">{{ item.time }}</div>
              </div>
            </div>
          </div>
          <div v-else class="empty-state">
            <ClockCircleOutlined class="empty-icon" />
            <p>No schedule assigned</p>
            <p class="empty-hint">Your manager will assign shifts soon</p>
          </div>
        </div>
      </a-col>

      <!-- Tasks Preview -->
      <a-col :xs="24" :lg="12">
        <div class="preview-card tasks-card">
          <div class="card-header">
            <div class="header-left">
              <div class="card-icon tasks-icon">
                <CheckSquareOutlined />
              </div>
              <div>
                <h3 class="card-title">My Tasks</h3>
                <p class="card-subtitle">Tasks assigned to you</p>
              </div>
            </div>
            <router-link to="/app/tasks">
              <a-button type="link" class="view-link">View all</a-button>
            </router-link>
          </div>

          <div v-if="taskPreview.length > 0" class="task-list">
            <div
              v-for="task in taskPreview"
              :key="task.id"
              class="task-item"
              @click="$router.push('/app/tasks')"
            >
              <div class="task-main">
                <div class="task-title-wrapper">
                  <span class="task-title">{{ task.title }}</span>
                  <a-tag :color="getPriorityColor(task.priority)" class="task-priority">
                    {{ getPriorityText(task.priority) }}
                  </a-tag>
                </div>
                <div class="task-meta">
                  <span class="task-due">Due: {{ task.dueDate || 'N/A' }}</span>
                  <a-tag :color="getStatusColor(task.status)">{{ task.status }}</a-tag>
                </div>
              </div>
            </div>
          </div>
          <div v-else class="empty-state">
            <CheckSquareOutlined class="empty-icon" />
            <p>No tasks assigned</p>
          </div>
        </div>
      </a-col>
    </a-row>

    <!-- Communication Preview (Non-Worker) -->
    <div v-if="userRole !== 'worker'" class="preview-card communication-card">
      <div class="card-header">
        <div class="header-left">
          <div class="card-icon communication-icon">
            <MessageOutlined />
          </div>
          <div>
            <h3 class="card-title">Recent Communications</h3>
            <p class="card-subtitle">Latest messages and notifications</p>
          </div>
        </div>
        <router-link to="/app/communication">
          <a-button type="primary" ghost class="view-all-btn">
            Open <RightOutlined />
          </a-button>
        </router-link>
      </div>

      <div v-if="communicationPreview.length > 0" class="communication-list">
        <div
          v-for="item in communicationPreview"
          :key="item.id"
          class="communication-item"
        >
          <div class="comm-icon" :class="{ 'unread': item.unread }">
            <MessageOutlined />
          </div>
          <div class="comm-content">
            <div class="comm-subject">{{ item.subject }}</div>
            <div class="comm-preview">{{ item.preview }}</div>
          </div>
          <div class="comm-meta">
            <a-tag :color="item.unread ? 'blue' : 'default'">{{ item.unread ? 'Unread' : 'Read' }}</a-tag>
            <span class="comm-time">{{ item.time }}</span>
          </div>
        </div>
      </div>
      <div v-else class="empty-state">
        <MessageOutlined class="empty-icon" />
        <p>No communications</p>
      </div>
    </div>

    <!-- Worker Schedule Modal -->
    <a-modal
      v-model:open="scheduleModalVisible"
      title="My Schedule"
      width="800px"
      :footer="null"
      @cancel="scheduleModalVisible = false"
      class="schedule-modal"
    >
      <div v-if="userRole === 'worker'">
        <div style="margin-bottom: 16px;">
          <a-date-picker
            v-model:value="selectedScheduleDate"
            @change="onScheduleDateChange"
            style="width: 200px;"
            placeholder="Select date to view schedule"
          />
        </div>
        
        <div v-if="selectedScheduleDate">
          <a-card :title="`Schedule for ${selectedScheduleDate.format('YYYY-MM-DD')}`" size="small">
            <div v-if="getScheduleForDate(selectedScheduleDate).length > 0">
              <a-list :data-source="getScheduleForDate(selectedScheduleDate)" bordered>
                <template #renderItem="{ item }">
                  <a-list-item>
                    <a-list-item-meta>
                      <template #title>
                        <div style="display: flex; align-items: center; gap: 12px;">
                          <a-tag :color="getShiftColor(item.shift)" style="font-size: 14px; padding: 4px 8px;">
                            {{ item.shift }}
                          </a-tag>
                          <span style="font-weight: 500;">{{ item.time }}</span>
                        </div>
                      </template>
                      <template #description>
                        <div style="color: #666;">
                          <div>Date: {{ item.date }}</div>
                          <div>Duration: {{ item.duration }}</div>
                          <div v-if="item.notes">Notes: {{ item.notes }}</div>
                        </div>
                      </template>
                    </a-list-item-meta>
                  </a-list-item>
                </template>
              </a-list>
            </div>
            <div v-else style="text-align: center; padding: 40px; color: #999;">
              <ClockCircleOutlined style="font-size: 48px; margin-bottom: 16px;" />
              <div style="font-size: 16px; margin-bottom: 8px;">No schedule for this date</div>
              <div style="font-size: 12px;">You are not scheduled to work on {{ selectedScheduleDate.format('YYYY-MM-DD') }}</div>
            </div>
          </a-card>
        </div>
        
        <div v-else style="text-align: center; padding: 40px; color: #999;">
          <CalendarOutlined style="font-size: 48px; margin-bottom: 16px;" />
          <div style="font-size: 16px; margin-bottom: 8px;">Select a date to view your schedule</div>
          <div style="font-size: 12px;">Choose a date from the date picker above to see your work schedule</div>
        </div>
      </div>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import dayjs from 'dayjs'
import {
  CheckSquareOutlined,
  BarChartOutlined,
  MessageOutlined,
  ClockCircleOutlined,
  CalendarOutlined,
  DollarOutlined,
  ShoppingCartOutlined,
  WalletOutlined,
  FileTextOutlined,
  ExclamationCircleOutlined,
  RightOutlined,
  UserOutlined,
  TeamOutlined,
  SolutionOutlined
} from '@ant-design/icons-vue'
import { getMe } from '@/services/userService'
import { getBudgetByPatient, getBudgetCalculations, getBudgetCategories } from '@/services/budgetService'
import { getTasksByPatient, getTasksByWorker, getAllTasks } from '@/services/taskService'
import { getSchedulesByWorker } from '@/services/scheduleService'

const userRole = ref('worker')
const userName = ref('')

// Schedule related data
const scheduleModalVisible = ref(false)
const selectedScheduleDate = ref(null)
const workerSchedule = ref([])
const allWorkerSchedules = ref([])

onMounted(async () => {
  try {
    const me = await getMe()
    userRole.value = me?.data?.role || 'worker'
    userName.value = me?.data?.name || 'User'
    
    await loadHomeData()
  } catch (e) {
    // keep default
  }
})

// Load home page data from API
const loadHomeData = async () => {
  try {
    const userInfo = await getMe()
    if (!userInfo?.data) return
    
    // Load budget data for non-worker roles
    if (userRole.value !== 'worker' && userInfo.data.patientId) {
      try {
        const calculationsResponse = await getBudgetCalculations(userInfo.data.patientId)
        if (calculationsResponse?.data) {
          budgetData.value = {
            ...budgetData.value,
            ...calculationsResponse.data
          }
        }
        
        const budgetResponse = await getBudgetByPatient(userInfo.data.patientId)
        if (budgetResponse?.data) {
          budgetData.value.totalBudget = budgetResponse.data.totalBudget || budgetData.value.totalBudget || 0
        }
        
        const categoriesResponse = await getBudgetCategories(userInfo.data.patientId)
        if (categoriesResponse?.data) {
          budgetCategories.value = categoriesResponse.data
          budgetData.value.categories = categoriesResponse.data
        }
      } catch (error) {
        console.error('Failed to load budget data:', error)
        budgetCategories.value = []
        budgetData.value = { totalBudget: 0, categories: [] }
      }
    }
    
    // Load task data
    try {
      let taskResponse
      if (userRole.value === 'worker') {
        taskResponse = await getTasksByWorker(userInfo.data.id)
      } else if (userRole.value === 'poa') {
        taskResponse = await getTasksByPatient(userInfo.data.patientId)
      } else if (userRole.value === 'manager') {
        taskResponse = await getAllTasks()
      }
      
      if (taskResponse?.data) {
        taskPreview.value = taskResponse.data.slice(0, 5).map(task => ({
          id: task.id,
          title: task.title,
          priority: task.priority,
          dueDate: task.dueDate,
          status: task.status
        }))
      } else {
        taskPreview.value = []
      }
    } catch (error) {
      console.error('Failed to load task data:', error)
      taskPreview.value = []
    }
    
    // Load worker schedule data
    if (userRole.value === 'worker') {
      try {
        await loadWorkerSchedule(userInfo.data.id)
      } catch (error) {
        console.error('Failed to load worker schedule:', error)
        workerSchedule.value = []
        allWorkerSchedules.value = []
      }
    }
    
  } catch (error) {
    console.error('Failed to load home data:', error)
    budgetCategories.value = []
    budgetData.value = { totalBudget: 0, categories: [] }
    taskPreview.value = []
  }
}

const roleDisplay = computed(() => {
  switch (userRole.value) {
    case 'poa': return 'Power of Attorney'
    case 'manager': return 'Manager'
    case 'worker': return 'Worker'
    default: return userRole.value
  }
})

const getRoleIcon = () => {
  switch (userRole.value) {
    case 'poa': return UserOutlined
    case 'manager': return TeamOutlined
    case 'worker': return SolutionOutlined
    default: return UserOutlined
  }
}

// --- Budget Preview ---
const budgetCategories = ref([])
const budgetData = ref({
  totalBudget: 0,
  categories: []
})

const getTotalUsed = () => {
  if (!budgetData.value?.categories || !Array.isArray(budgetData.value.categories)) {
    return 0
  }
  
  return budgetData.value.categories.reduce((total, category) => {
    if (!category?.subElements || !Array.isArray(category.subElements)) {
      return total
    }
    
    return total + category.subElements.reduce((categoryTotal, subElement) => {
      return categoryTotal + (subElement?.totalUtilised || 0)
    }, 0)
  }, 0)
}

const totalBudget = computed(() => budgetData.value.totalBudget || 0)
const totalUsed = computed(() => getTotalUsed())
const totalLeft = computed(() => totalBudget.value - totalUsed.value)
const totalUsagePct = computed(() => {
  if (totalBudget.value === 0) {
    return 0
  }
  return Math.round((totalUsed.value / totalBudget.value) * 100)
})
const budgetUsageColor = computed(() => totalUsagePct.value >= 100 ? 'red' : totalUsagePct.value >= 80 ? 'orange' : 'green')

const topCategoryWarnings = computed(() => {
  const categories = budgetData.value?.categories || budgetCategories.value || []
  const arr = categories
    .map(c => {
      const used = c.subElements && c.subElements.length > 0 
        ? c.subElements.reduce((sum, se) => sum + (se.totalUtilised || 0), 0)
        : (c.used || 0)
      const budget = c.categoryBudget || c.budget || 0
      return {
        category: c.name,
        pct: budget > 0 ? Math.round((used / budget) * 100) : 0,
        used,
        budget
      }
    })
    .sort((a, b) => b.pct - a.pct)
    .slice(0, 3)
  return arr
})

// --- Tasks Preview ---
const taskPreview = ref([])

const getPriorityColor = (priority) => {
  switch (priority) {
    case 'normal': return 'green'
    case 'urgent': return 'orange'
    case 'very-urgent': return 'red'
    default: return 'default'
  }
}

const getPriorityText = (priority) => {
  switch (priority) {
    case 'normal': return 'Normal'
    case 'urgent': return 'Urgent'
    case 'very-urgent': return 'Very Urgent'
    default: return priority
  }
}

const getStatusColor = (status) => {
  switch (status) {
    case 'Pending': return 'orange'
    case 'In Progress': return 'blue'
    case 'Completed': return 'green'
    case 'Worker Completed': return 'gold'
    case 'Rejected': return 'red'
    case 'Cancelled': return 'red'
    default: return 'default'
  }
}

// --- Upload Preview ---
const uploadPreview = ref([
  { id: 'f1', name: 'receipt_2025-09-21.pdf', size: '234 KB', time: '1h ago' },
  { id: 'f2', name: 'care_plan_update.docx', size: '88 KB', time: 'Yesterday' },
  { id: 'f3', name: 'medication_photo.jpg', size: '1.2 MB', time: '2 days ago' }
])

// --- Communication Preview ---
const communicationPreview = ref([
  { id: 101, subject: 'Budget alert acknowledged', preview: 'Thanks, we will adjust the category next week.', unread: true, time: '2h ago' },
  { id: 102, subject: 'New task request', preview: 'POA requested: Add Physical Therapy Session.', unread: true, time: '5h ago' },
  { id: 103, subject: 'System maintenance notice', preview: 'System maintenance tonight from 2:00 AM to 4:00 AM.', unread: false, time: 'Yesterday' }
])

// --- Worker Schedule Functions ---
const loadWorkerSchedule = async (workerId) => {
  try {
    const response = await getSchedulesByWorker(workerId)
    
    if (response?.data && Array.isArray(response.data)) {
      const transformedSchedules = response.data.map(schedule => {
        let shift = schedule.shiftType
        if (shift === 'morning') {
          shift = 'Morning Shift'
        } else if (shift === 'evening' || shift === 'afternoon') {
          shift = 'Afternoon Shift'
        } else if (shift === 'night') {
          shift = 'Night Shift'
        } else if (shift === 'full-day') {
          shift = 'Full Day Shift'
        }
        
        const date = schedule.scheduleDate || schedule.date || ''
        const time = schedule.shiftStartTime && schedule.shiftEndTime 
          ? `${schedule.shiftStartTime} - ${schedule.shiftEndTime}`
          : (schedule.time || '')
        const duration = schedule.duration || '8 hours'
        
        return {
          id: schedule.id,
          date: date,
          shift: shift,
          time: time,
          duration: duration,
          notes: schedule.notes || ''
        }
      })
      
      transformedSchedules.sort((a, b) => {
        if (a.date < b.date) return -1
        if (a.date > b.date) return 1
        return 0
      })
      
      allWorkerSchedules.value = transformedSchedules
      workerSchedule.value = transformedSchedules.slice(0, 5)
    } else {
      allWorkerSchedules.value = []
      workerSchedule.value = []
    }
  } catch (error) {
    console.error('Failed to load worker schedule:', error)
    allWorkerSchedules.value = []
    workerSchedule.value = []
  }
}

const showScheduleModal = () => {
  scheduleModalVisible.value = true
  selectedScheduleDate.value = dayjs()
}

const onScheduleDateChange = (date) => {
  selectedScheduleDate.value = date
}

const getScheduleForDate = (date) => {
  if (!date || !allWorkerSchedules.value.length) return []
  
  const dateStr = date.format('YYYY-MM-DD')
  return allWorkerSchedules.value.filter(schedule => schedule.date === dateStr)
}

const getShiftColor = (shift) => {
  switch (shift) {
    case 'Morning Shift': return 'green'
    case 'Afternoon Shift': return 'blue'
    case 'Night Shift': return 'purple'
    default: return 'default'
  }
}

const getDayFromDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.getDate()
}

const getMonthFromDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec']
  return months[date.getMonth()]
}
</script>

<style scoped>
.home-page {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0;
  animation: fadeInUp 0.6s ease-out;
}

/* Welcome Banner */
.welcome-banner {
  position: relative;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 24px;
  padding: 48px 40px;
  margin-bottom: 32px;
  overflow: hidden;
  box-shadow: 0 12px 40px rgba(102, 126, 234, 0.3);
}

.banner-content {
  position: relative;
  z-index: 2;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 24px;
}

.banner-left {
  flex: 1;
  min-width: 300px;
}

.welcome-title {
  font-size: 36px;
  font-weight: 700;
  color: white;
  margin: 0 0 8px;
  text-shadow: 0 2px 20px rgba(0, 0, 0, 0.2);
}

.user-name {
  background: linear-gradient(135deg, #fff 0%, #f0f0f0 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.welcome-subtitle {
  font-size: 18px;
  color: rgba(255, 255, 255, 0.9);
  margin: 0;
}

.role-badge {
  display: flex;
  align-items: center;
  gap: 12px;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(20px);
  padding: 16px 24px;
  border-radius: 16px;
  color: white;
  font-weight: 600;
  font-size: 16px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);
}

.role-icon {
  font-size: 24px;
}

.banner-decoration {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 1;
}

.decoration-circle {
  position: absolute;
  border-radius: 50%;
  filter: blur(60px);
  opacity: 0.3;
}

.circle-1 {
  width: 200px;
  height: 200px;
  background: rgba(255, 255, 255, 0.4);
  top: -50px;
  right: 100px;
}

.circle-2 {
  width: 150px;
  height: 150px;
  background: rgba(255, 255, 255, 0.3);
  bottom: -30px;
  left: 50px;
}

.circle-3 {
  width: 100px;
  height: 100px;
  background: rgba(255, 255, 255, 0.2);
  top: 50%;
  left: 50%;
}

/* Stats Row */
.stats-row {
  margin-bottom: 32px;
}

.stat-card {
  background: white;
  border-radius: 20px;
  padding: 32px 24px;
  display: flex;
  align-items: center;
  gap: 20px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(226, 232, 240, 0.8);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 32px rgba(102, 126, 234, 0.15);
}

.stat-icon {
  width: 64px;
  height: 64px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  flex-shrink: 0;
}

.stat-budget .stat-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.stat-used .stat-icon {
  background: linear-gradient(135deg, #fa8c16 0%, #faad14 100%);
  color: white;
}

.stat-left .stat-icon {
  background: linear-gradient(135deg, #52c41a 0%, #73d13d 100%);
  color: white;
}

.stat-left.stat-negative .stat-icon {
  background: linear-gradient(135deg, #ff4d4f 0%, #ff7875 100%);
}

.stat-tasks .stat-icon {
  background: linear-gradient(135deg, #1890ff 0%, #40a9ff 100%);
  color: white;
}

.stat-schedule .stat-icon {
  background: linear-gradient(135deg, #722ed1 0%, #9254de 100%);
  color: white;
}

.stat-upload .stat-icon {
  background: linear-gradient(135deg, #13c2c2 0%, #36cfc9 100%);
  color: white;
}

.stat-content {
  flex: 1;
}

.stat-label {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #1a1a1a;
  line-height: 1.2;
}

.stat-percentage {
  font-size: 14px;
  color: #999;
  margin-top: 4px;
}

/* Preview Cards */
.content-row {
  margin-bottom: 32px;
}

.preview-card {
  background: white;
  border-radius: 20px;
  padding: 32px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(226, 232, 240, 0.8);
  transition: all 0.3s ease;
  height: 100%;
}

.preview-card:hover {
  box-shadow: 0 8px 32px rgba(102, 126, 234, 0.12);
  transform: translateY(-2px);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
  flex-wrap: wrap;
  gap: 16px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.card-icon {
  width: 56px;
  height: 56px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  flex-shrink: 0;
}

.budget-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.tasks-icon {
  background: linear-gradient(135deg, #1890ff 0%, #40a9ff 100%);
  color: white;
}

.schedule-icon {
  background: linear-gradient(135deg, #722ed1 0%, #9254de 100%);
  color: white;
}

.communication-icon {
  background: linear-gradient(135deg, #13c2c2 0%, #36cfc9 100%);
  color: white;
}

.card-title {
  font-size: 20px;
  font-weight: 700;
  color: #1a1a1a;
  margin: 0 0 4px;
}

.card-subtitle {
  font-size: 14px;
  color: #666;
  margin: 0;
}

.view-all-btn {
  border-radius: 12px;
  height: 40px;
  padding: 0 20px;
  font-weight: 600;
}

.view-link {
  color: #667eea;
  font-weight: 600;
  padding: 0;
}

/* Budget Progress */
.budget-progress {
  margin-bottom: 32px;
}

.progress-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.usage-progress {
  margin-bottom: 16px;
}

.usage-progress :deep(.ant-progress-bg) {
  border-radius: 8px;
}

.progress-stats {
  display: flex;
  gap: 24px;
  flex-wrap: wrap;
}

.stat-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.stat-label-small {
  font-size: 12px;
  color: #999;
}

.stat-value-small {
  font-size: 16px;
  font-weight: 600;
  color: #1a1a1a;
}

.text-danger {
  color: #ff4d4f;
}

/* Top Categories */
.top-categories {
  margin-top: 32px;
  padding-top: 32px;
  border-top: 1px solid #f0f0f0;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0 0 20px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.category-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.category-item {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.category-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.category-name {
  font-weight: 600;
  color: #1a1a1a;
}

.category-usage {
  font-size: 14px;
  color: #666;
}

.category-progress-wrapper {
  display: flex;
  align-items: center;
  gap: 12px;
}

.category-progress-wrapper :deep(.ant-progress) {
  flex: 1;
}

.category-progress-wrapper :deep(.ant-progress-bg) {
  border-radius: 4px;
}

.category-tag {
  min-width: 50px;
  text-align: center;
  border-radius: 8px;
}

/* Task List */
.task-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.task-item {
  padding: 20px;
  background: #f8f9fa;
  border-radius: 12px;
  border: 1px solid #e9ecef;
  cursor: pointer;
  transition: all 0.3s ease;
}

.task-item:hover {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.08) 0%, rgba(118, 75, 162, 0.05) 100%);
  border-color: rgba(102, 126, 234, 0.3);
  transform: translateX(4px);
}

.task-main {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.task-title-wrapper {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

.task-title {
  font-weight: 600;
  color: #1a1a1a;
  flex: 1;
}

.task-priority {
  flex-shrink: 0;
  border-radius: 8px;
}

.task-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

.task-due {
  font-size: 13px;
  color: #666;
}

/* Schedule List */
.schedule-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.schedule-item {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 12px;
  border: 1px solid #e9ecef;
  transition: all 0.3s ease;
}

.schedule-item:hover {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.08) 0%, rgba(118, 75, 162, 0.05) 100%);
  border-color: rgba(102, 126, 234, 0.3);
  transform: translateX(4px);
}

.schedule-date {
  width: 64px;
  height: 64px;
  background: linear-gradient(135deg, #722ed1 0%, #9254de 100%);
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: white;
  flex-shrink: 0;
}

.date-day {
  font-size: 24px;
  font-weight: 700;
  line-height: 1;
}

.date-month {
  font-size: 12px;
  opacity: 0.9;
}

.schedule-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.schedule-shift {
  display: flex;
  align-items: center;
}

.shift-tag {
  border-radius: 8px;
  font-weight: 600;
}

.schedule-time {
  font-size: 14px;
  color: #666;
}

/* Communication List */
.communication-card {
  margin-bottom: 32px;
}

.communication-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.communication-item {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 12px;
  border: 1px solid #e9ecef;
  transition: all 0.3s ease;
}

.communication-item:hover {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.08) 0%, rgba(118, 75, 162, 0.05) 100%);
  border-color: rgba(102, 126, 234, 0.3);
  transform: translateX(4px);
}

.comm-icon {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, #13c2c2 0%, #36cfc9 100%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 20px;
  flex-shrink: 0;
}

.comm-icon.unread {
  background: linear-gradient(135deg, #1890ff 0%, #40a9ff 100%);
  box-shadow: 0 0 0 3px rgba(24, 144, 255, 0.2);
}

.comm-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.comm-subject {
  font-weight: 600;
  color: #1a1a1a;
  font-size: 15px;
}

.comm-preview {
  font-size: 13px;
  color: #666;
  line-height: 1.5;
}

.comm-meta {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 8px;
  flex-shrink: 0;
}

.comm-time {
  font-size: 12px;
  color: #999;
}

/* Empty State */
.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #999;
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 16px;
  color: #d9d9d9;
}

.empty-state p {
  font-size: 16px;
  margin: 0;
}

.empty-hint {
  font-size: 13px;
  color: #bbb;
  margin-top: 8px;
}

/* Animations */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* Responsive */
@media (max-width: 768px) {
  .welcome-banner {
    padding: 32px 24px;
  }

  .welcome-title {
    font-size: 28px;
  }

  .banner-content {
    flex-direction: column;
    align-items: flex-start;
  }

  .stat-card {
    padding: 24px 20px;
  }

  .preview-card {
    padding: 24px 20px;
  }

  .card-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .progress-stats {
    flex-direction: column;
    gap: 16px;
  }
}
</style>
