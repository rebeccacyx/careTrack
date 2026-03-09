import { describe, it, expect, vi, beforeEach } from 'vitest'
import * as userService from '@/services/userService'
import api from '@/services/api'

vi.mock('@/services/api')

// Mock sessionStorage
const mockSessionStorage = (() => {
    let store = {}
    return {
        getItem: vi.fn((key) => store[key]),
        setItem: vi.fn((key, value) => { store[key] = value }),
        removeItem: vi.fn((key) => delete store[key]),
        clear: vi.fn(() => { store = {} }),
    }
})()
Object.defineProperty(window, 'sessionStorage', { value: mockSessionStorage })

// Mock window.location
delete window.location
window.location = { href: '' }

describe('userService.js', () => {
    beforeEach(() => {
        vi.clearAllMocks()
        sessionStorage.clear()
    })

    it('should login successfully and return user info + token', async () => {
        api.post.mockResolvedValueOnce({
            data: {
                code: '0',
                data: { id: 'u123', email: 'test@example.com', firstName: 'Alice', lastName: 'Lee' }
            }
        })
        api.get.mockResolvedValueOnce({
            data: { code: '0', data: { valid: true, reason: 'active' } }
        })
        const result = await userService.login({ email: 'test@example.com', password: '123456' })
        expect(api.post).toHaveBeenCalledWith('/auth/login-email', { email: 'test@example.com', password: '123456' })
        expect(api.get).toHaveBeenCalledWith('/auth/invite-status?userId=u123')
        expect(result.data.user.email).toBe('test@example.com')
        expect(result.data.token).toMatch(/^token_/)
        expect(result.data.inviteStatus.valid).toBe(true)
        expect(sessionStorage.setItem).toHaveBeenCalledWith('userId', 'u123')
    })

    it('should throw error if user is removed', async () => {
        userService.addRemovedWorker({ email: 'removed@example.com', name: 'Removed User' })
        await expect(userService.login({ email: 'removed@example.com', password: '123' }))
            .rejects.toThrow('Access denied')
    })

    it('should return guest info if no userId', async () => {
        sessionStorage.getItem.mockReturnValueOnce(null)
        const result = await userService.getMe()
        expect(result.data.role).toBe('guest')
    })

    it('should return user info if userId exists', async () => {
        sessionStorage.getItem.mockReturnValueOnce('u123')
        api.get.mockResolvedValueOnce({
            data: { code: '0', data: { id: 'u123', email: 'test@example.com', userType: 'MANAGER' } }
        })
        const result = await userService.getMe()
        expect(api.get).toHaveBeenCalledWith('/auth/me?userId=u123')
        expect(result.data.email).toBe('test@example.com')
    })

    it('should get invite status from backend', async () => {
        sessionStorage.getItem.mockReturnValueOnce('u456')
        api.get.mockResolvedValueOnce({
            data: { code: '0', data: { valid: true, reason: 'ok' } }
        })
        const result = await userService.getInviteStatus()
        expect(api.get).toHaveBeenCalledWith('/auth/invite-status?userId=u456')
        expect(result.data.valid).toBe(true)
    })

    it('should submit invite code successfully', async () => {
        api.post.mockResolvedValueOnce({ data: { code: '0', data: { success: true } } })
        const result = await userService.submitInviteCode('INV123')
        expect(api.post).toHaveBeenCalledWith('/auth/submit-invite-code', { inviteCode: 'INV123' })
        expect(result.data.success).toBe(true)
    })

    it('should clear sessionStorage and redirect to login', () => {
        sessionStorage.setItem('userId', 'abc')
        userService.logout()
        expect(sessionStorage.removeItem).toHaveBeenCalledWith('userId')
        expect(window.location.href).toBe('/login')
    })
})