import { describe, it, expect, vi, beforeEach } from 'vitest'
import * as workerService from '@/services/workerService'
import api from '@/services/api'

vi.mock('@/services/api')

describe('workerService.js', () => {
    beforeEach(() => {
        vi.clearAllMocks()
    })

    // ---------- getAllWorkers ----------
    it('should get all workers successfully', async () => {
        api.get.mockResolvedValueOnce({ data: { code: '0', data: [{ id: 'w1', name: 'Alice' }] } })
        const result = await workerService.getAllWorkers()
        expect(api.get).toHaveBeenCalledWith('/workers')
        expect(result.data[0].name).toBe('Alice')
    })

    it('should throw error when getAllWorkers fails', async () => {
        api.get.mockRejectedValueOnce({ response: { data: { msg: 'Server error' } } })
        await expect(workerService.getAllWorkers()).rejects.toThrow('Server error')
    })

    // ---------- getWorkersByOrganization ----------
    it('should get workers by organization successfully', async () => {
        api.get.mockResolvedValueOnce({ data: { code: '0', data: [{ id: 'w2' }] } })
        const result = await workerService.getWorkersByOrganization('org1')
        expect(api.get).toHaveBeenCalledWith('/workers/organization/org1')
        expect(result.data[0].id).toBe('w2')
    })

    it('should throw error when getWorkersByOrganization fails', async () => {
        api.get.mockRejectedValueOnce({ response: { data: { msg: 'Org not found' } } })
        await expect(workerService.getWorkersByOrganization('bad')).rejects.toThrow('Org not found')
    })

    // ---------- createWorker ----------
    it('should create worker successfully', async () => {
        api.post.mockResolvedValueOnce({ data: { code: '0', data: { id: 'w3' } } })
        const result = await workerService.createWorker({ name: 'New Worker' })
        expect(api.post).toHaveBeenCalledWith('/workers', { name: 'New Worker' })
        expect(result.data.id).toBe('w3')
    })

    it('should throw error when createWorker fails', async () => {
        api.post.mockRejectedValueOnce({ response: { data: { msg: 'Invalid data' } } })
        await expect(workerService.createWorker({})).rejects.toThrow('Invalid data')
    })

    // ---------- updateWorker ----------
    it('should update worker successfully', async () => {
        api.put.mockResolvedValueOnce({ data: { code: '0', data: { updated: true } } })
        const result = await workerService.updateWorker('w1', { name: 'Updated' })
        expect(api.put).toHaveBeenCalledWith('/workers/w1', { name: 'Updated' })
        expect(result.data.updated).toBe(true)
    })

    it('should throw error when updateWorker fails', async () => {
        api.put.mockRejectedValueOnce({ response: { data: { msg: 'Update failed' } } })
        await expect(workerService.updateWorker('w1', {})).rejects.toThrow('Update failed')
    })

    // ---------- deleteWorker ----------
    it('should delete worker successfully', async () => {
        api.delete.mockResolvedValueOnce({ data: { code: '0', data: { deleted: true } } })
        const result = await workerService.deleteWorker('w1')
        expect(api.delete).toHaveBeenCalledWith('/workers/w1')
        expect(result.data.deleted).toBe(true)
    })

    it('should throw error when deleteWorker fails', async () => {
        api.delete.mockRejectedValueOnce({ response: { data: { msg: 'Delete failed' } } })
        await expect(workerService.deleteWorker('bad')).rejects.toThrow('Delete failed')
    })

    // ---------- activateWorker ----------
    it('should activate worker successfully', async () => {
        api.post.mockResolvedValueOnce({ data: { code: '0', data: { active: true } } })
        const result = await workerService.activateWorker('w1')
        expect(api.post).toHaveBeenCalledWith('/workers/w1/activate')
        expect(result.data.active).toBe(true)
    })

    it('should throw error when activateWorker fails', async () => {
        api.post.mockRejectedValueOnce({ response: { data: { msg: 'Activation failed' } } })
        await expect(workerService.activateWorker('w1')).rejects.toThrow('Activation failed')
    })

    // ---------- deactivateWorker ----------
    it('should deactivate worker successfully', async () => {
        api.post.mockResolvedValueOnce({ data: { code: '0', data: { active: false } } })
        const result = await workerService.deactivateWorker('w1')
        expect(api.post).toHaveBeenCalledWith('/workers/w1/deactivate')
        expect(result.data.active).toBe(false)
    })

    it('should throw error when deactivateWorker fails', async () => {
        api.post.mockRejectedValueOnce({ response: { data: { msg: 'Deactivation failed' } } })
        await expect(workerService.deactivateWorker('w1')).rejects.toThrow('Deactivation failed')
    })

    // ---------- getAvailableWorkers ----------
    it('should get available workers successfully', async () => {
        api.get.mockResolvedValueOnce({ data: { code: '0', data: [{ id: 'w5' }] } })
        const result = await workerService.getAvailableWorkers('org1')
        expect(api.get).toHaveBeenCalledWith('/workers/organization/org1/available')
        expect(result.data[0].id).toBe('w5')
    })

    it('should throw error when getAvailableWorkers fails', async () => {
        api.get.mockRejectedValueOnce({ response: { data: { msg: 'Unavailable' } } })
        await expect(workerService.getAvailableWorkers('bad')).rejects.toThrow('Unavailable')
    })

    // ---------- createDailySchedule ----------
    it('should create daily schedule successfully', async () => {
        api.post.mockResolvedValueOnce({ data: { code: '0', data: { scheduled: true } } })
        const result = await workerService.createDailySchedule({ date: '2025-10-19' }, 'm1')
        expect(api.post).toHaveBeenCalledWith('/workers/daily-schedule', { date: '2025-10-19' }, {
            headers: { 'X-Manager-Id': 'm1' }
        })
        expect(result.data.scheduled).toBe(true)
    })

    it('should throw error when createDailySchedule fails', async () => {
        api.post.mockRejectedValueOnce({ response: { data: { msg: 'Schedule failed' } } })
        await expect(workerService.createDailySchedule({}, 'm1')).rejects.toThrow('Schedule failed')
    })

    // ---------- getDailySchedule ----------
    it('should get daily schedule successfully', async () => {
        api.get.mockResolvedValueOnce({ data: { code: '0', data: [{ id: 's1' }] } })
        const result = await workerService.getDailySchedule('org1', '2025-10-19')
        expect(api.get).toHaveBeenCalledWith('/workers/organization/org1/daily-schedule/2025-10-19')
        expect(result.data[0].id).toBe('s1')
    })

    it('should throw error when getDailySchedule fails', async () => {
        api.get.mockRejectedValueOnce({ response: { data: { msg: 'Fetch failed' } } })
        await expect(workerService.getDailySchedule('bad', '2025-10-19')).rejects.toThrow('Fetch failed')
    })

    // ---------- clearDailySchedule ----------
    it('should clear daily schedule successfully', async () => {
        api.delete.mockResolvedValueOnce({ data: { code: '0', data: { cleared: true } } })
        const result = await workerService.clearDailySchedule('org1', '2025-10-19')
        expect(api.delete).toHaveBeenCalledWith('/workers/organization/org1/daily-schedule/2025-10-19')
        expect(result.data.cleared).toBe(true)
    })

    it('should throw error when clearDailySchedule fails', async () => {
        api.delete.mockRejectedValueOnce({ response: { data: { msg: 'Clear failed' } } })
        await expect(workerService.clearDailySchedule('bad', '2025-10-19')).rejects.toThrow('Clear failed')
    })

    // ---------- uploadWorkerPhoto ----------
    it('should upload worker photo successfully', async () => {
        const mockPhoto = new Blob(['fake'], { type: 'image/png' })
        api.post.mockResolvedValueOnce({ data: { code: '0', data: { uploaded: true } } })
        const result = await workerService.uploadWorkerPhoto('w1', mockPhoto)
        expect(api.post).toHaveBeenCalled()
        expect(result.data.uploaded).toBe(true)
    })

    it('should throw error when uploadWorkerPhoto fails', async () => {
        api.post.mockRejectedValueOnce({ response: { data: { msg: 'Upload failed' } } })
        await expect(workerService.uploadWorkerPhoto('w1', 'bad')).rejects.toThrow('Upload failed')
    })
})