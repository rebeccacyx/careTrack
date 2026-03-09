import { createRouter, createWebHistory } from 'vue-router'
import { getMe, getInviteStatus } from '../services/userService'

import Landing from '../views/Landing.vue'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import ForgotPassword from '../views/Forget.vue'
import InviteCode from '../views/InviteCode.vue'

// Simple authentication check function
function isAuthed() {
  return !!sessionStorage.getItem('token')
}

// Now using real API from userService


const routes = [
  { path: '/', component: Landing },
  { path: '/login', component: Login },
  { path: '/register', component: Register },
  { path: '/forgot-password', component: ForgotPassword },
  { path: '/invitecode', component: InviteCode },
  // Routes that require authentication with layout
  { 
    path: '/app', 
    component: () => import('../views/Layout.vue'), 
    meta: { requiresAuth: true },
    children: [
      { path: '', redirect: '/app/menu' },
      { path: 'menu', component: () => import('../views/Home.vue') },
      { path: 'tasks', component: () => import('../views/Tasks.vue') },
      { path: 'carer-team', component: () => import('../views/CarerTeam.vue') },
      { path: 'budget', component: () => import('../views/Budget.vue') },
      { path: 'upload', component: () => import('../views/Upload.vue') },
      { path: 'setting', component: () => import('../views/Setting.vue') },
      { path: 'logout', component: () => import('../views/Logout.vue') },
      { path: 'worker-management', component: () => import('../views/WorkerManagement.vue') },
      { path: 'communication', component: () => import('../views/Communication.vue') }
    ]
  },
  // Legacy routes for backward compatibility
  { path: '/menu', redirect: '/app/menu' },
  { path: '/tasks', redirect: '/app/tasks' },
  { path: '/carer-team', redirect: '/app/carer-team' },
  { path: '/budget', redirect: '/app/budget' },
  { path: '/upload', redirect: '/app/upload' },
  { path: '/setting', redirect: '/app/setting' },
  { path: '/worker-management', redirect: '/app/worker-management' },
  { path: '/communication', redirect: '/app/communication' }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

function needsInvite(role) {
  return role === 'worker' || role === 'manager'
}

let meCached = null
let inviteCached = null

// Clear cache function
function clearCache() {
  meCached = null
  inviteCached = null
}

router.beforeEach(async (to, from, next) => {
  // Routes that require authentication
  if (to.meta?.requiresAuth) {
    if (!isAuthed()) {
      return next({ path: '/login', query: { redirect: to.fullPath } })
    }

    try {
      meCached = meCached || await getMe()
      const role = meCached?.role

      if (!needsInvite(role)) {
        return next()
      }

      if (to.path === '/invitecode') {
        return next()
      }

      inviteCached = inviteCached || await getInviteStatus()
      console.log('Router guard - invite status:', inviteCached)
      
      // Only redirect to invite code if user hasn't used any invite code yet
      if (!inviteCached?.valid && inviteCached?.reason === 'missing') {
        console.log('Router guard - redirecting to invite code page')
        return next({
          path: '/invitecode',
          query: { redirect: to.fullPath }
        })
      } else if (inviteCached?.valid || inviteCached?.reason === 'already_used') {
        console.log('Router guard - user has used invite code, proceeding')
      }

      return next()
    } catch (e) {
      return next({ path: '/login', query: { redirect: to.fullPath } })
    }
  }

  return next()
})

// Export clear cache function for global access
window.clearRouterCache = clearCache

export default router
