import { describe, it, expect, vi, beforeEach } from 'vitest'
import * as inviteCodeService from '@/services/inviteCodeService'
import api from '@/services/api'

vi.mock('@/services/api')

describe('inviteCodeService.js', () => {
    beforeEach(() => {
        vi.clearAllMocks()
    })

    // ---------- generateInviteCode ----------
    it('should generate invite code successfully', async () => {
        api.post.mockResolvedValueOnce({
            data: { code: '0', data: { code: 'ABC123', creatorId: 'u001' } }
        })

        const result = await inviteCodeService.generateInviteCode({ creatorId: 'u001' })
        expect(api.post).toHaveBeenCalledWith('/invite/generate', { creatorId: 'u001' })
        expect(result.data.code).toBe('ABC123')
    })

    it('should throw error when generateInviteCode fails', async () => {
        api.post.mockRejectedValueOnce({ response: { data: { msg: 'Network error' } } })
        await expect(inviteCodeService.generateInviteCode({})).rejects.toThrow('Network error')
    })

    // ---------- useInviteCode ----------
    it('should use invite code successfully', async () => {
        api.post.mockResolvedValueOnce({
            data: { code: '0', data: { success: true, usedBy: 'userX' } }
        })

        const result = await inviteCodeService.useInviteCode('ABC123', 'userX')
        expect(api.post).toHaveBeenCalledWith('/invite/use', { code: 'ABC123', usedBy: 'userX' })
        expect(result.data.success).toBe(true)
    })

    it('should throw error when useInviteCode fails', async () => {
        api.post.mockRejectedValueOnce({ response: { data: { msg: 'Invalid code' } } })
        await expect(inviteCodeService.useInviteCode('BAD', 'userX')).rejects.toThrow('Invalid code')
    })

    // ---------- getMyInviteCodes ----------
    it('should get my invite codes successfully', async () => {
        api.get.mockResolvedValueOnce({
            data: { code: '0', data: [{ code: 'ABC123' }, { code: 'XYZ789' }] }
        })

        const result = await inviteCodeService.getMyInviteCodes('u001')
        expect(api.get).toHaveBeenCalledWith('/invite/my-codes?creatorId=u001')
        expect(result.data).toHaveLength(2)
    })

    it('should throw error when getMyInviteCodes fails', async () => {
        api.get.mockRejectedValueOnce({ response: { data: { msg: 'Server error' } } })
        await expect(inviteCodeService.getMyInviteCodes('u001')).rejects.toThrow('Server error')
    })

    // ---------- revokeInviteCode ----------
    it('should revoke invite code successfully', async () => {
        api.delete.mockResolvedValueOnce({
            data: { code: '0', data: { revoked: true } }
        })

        const result = await inviteCodeService.revokeInviteCode('c123')
        expect(api.delete).toHaveBeenCalledWith('/invite/c123')
        expect(result.data.revoked).toBe(true)
    })

    it('should throw error when revokeInviteCode fails', async () => {
        api.delete.mockRejectedValueOnce({ response: { data: { msg: 'Not found' } } })
        await expect(inviteCodeService.revokeInviteCode('x999')).rejects.toThrow('Not found')
    })

    // ---------- getActiveInviteCodesForPatient ----------
    it('should get active invite codes for patient', async () => {
        api.get.mockResolvedValueOnce({
            data: { code: '0', data: [{ code: 'ABC123' }] }
        })

        const result = await inviteCodeService.getActiveInviteCodesForPatient('p001')
        expect(api.get).toHaveBeenCalledWith('/invite/patient/p001')
        expect(result.data[0].code).toBe('ABC123')
    })

    it('should throw error when getActiveInviteCodesForPatient fails', async () => {
        api.get.mockRejectedValueOnce({ response: { data: { msg: 'Patient not found' } } })
        await expect(inviteCodeService.getActiveInviteCodesForPatient('p999')).rejects.toThrow('Patient not found')
    })
})