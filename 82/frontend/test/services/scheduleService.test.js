import { describe, it, expect, vi, beforeEach } from 'vitest'
import * as scheduleService from '@/services/scheduleService'
import api from '@/services/api'

vi.mock('@/services/api')

describe('scheduleService.js', () => {
    beforeEach(() => {
        vi.clearAllMocks()
    })

    // ---------- getAllSchedules ----------
    it('should get all schedules successfully', async () => {
        api.get.mockResolvedValueOnce({ data: { code: '0', data: [{ id: 's1', task: 'Visit' }] } })
        const result = await scheduleService.getAllSchedules()
        expect(api.get).toHaveBeenCalledWith('/schedules')
        expect(result.data[0].task).toBe('Visit')
    })

    it('should throw error when getAllSchedules fails', async () => {
        api.get.mockRejectedValueOnce({ response: { data: { msg: 'Server error' } } })
        await expect(scheduleService.getAllSchedules()).rejects.toThrow('Server error')
    })

    // ---------- getScheduleById ----------
    it('should get schedule by ID successfully', async () => {
        api.get.mockResolvedValueOnce({ data: { code: '0', data: { id: 's1', task: 'Checkup' } } })
        const result = await scheduleService.getScheduleById('s1')
        expect(api.get).toHaveBeenCalledWith('/schedules/s1')
        expect(result.data.task).toBe('Checkup')
    })

    it('should throw error when getScheduleById fails', async () => {
        api.get.mockRejectedValueOnce({ response: { data: { msg: 'Not found' } } })
        await expect(scheduleService.getScheduleById('bad')).rejects.toThrow('Not found')
    })

    // ---------- createSchedule ----------
    it('should create schedule successfully', async () => {
        api.post.mockResolvedValueOnce({ data: { code: '0', data: { id: 's2', task: 'New Task' } } })
        const result = await scheduleService.createSchedule({ task: 'New Task' })
        expect(api.post).toHaveBeenCalledWith('/schedules', { task: 'New Task' })
        expect(result.data.id).toBe('s2')
    })

    it('should throw error when createSchedule fails', async () => {
        api.post.mockRejectedValueOnce({ response: { data: { msg: 'Invalid data' } } })
        await expect(scheduleService.createSchedule({})).rejects.toThrow('Invalid data')
    })

    // ---------- updateSchedule ----------
    it('should update schedule successfully', async () => {
        api.put.mockResolvedValueOnce({ data: { code: '0', data: { id: 's1', updated: true } } })
        const result = await scheduleService.updateSchedule('s1', { task: 'Updated' })
        expect(api.put).toHaveBeenCalledWith('/schedules/s1', { task: 'Updated' })
        expect(result.data.updated).toBe(true)
    })

    it('should throw error when updateSchedule fails', async () => {
        api.put.mockRejectedValueOnce({ response: { data: { msg: 'Update failed' } } })
        await expect(scheduleService.updateSchedule('s1', {})).rejects.toThrow('Update failed')
    })

    // ---------- deleteSchedule ----------
    it('should delete schedule successfully', async () => {
        api.delete.mockResolvedValueOnce({ data: { code: '0', data: { success: true } } })
        const result = await scheduleService.deleteSchedule('s3')
        expect(api.delete).toHaveBeenCalledWith('/schedules/s3')
        expect(result.data.success).toBe(true)
    })

    it('should throw error when deleteSchedule fails', async () => {
        api.delete.mockRejectedValueOnce({ response: { data: { msg: 'Delete failed' } } })
        await expect(scheduleService.deleteSchedule('bad')).rejects.toThrow('Delete failed')
    })

    // ---------- getSchedulesByPatient ----------
    it('should get schedules by patient successfully', async () => {
        api.get.mockResolvedValueOnce({ data: { code: '0', data: [{ id: 's4' }] } })
        const result = await scheduleService.getSchedulesByPatient('p1')
        expect(api.get).toHaveBeenCalledWith('/schedules/patient/p1')
        expect(result.data[0].id).toBe('s4')
    })

    it('should throw error when getSchedulesByPatient fails', async () => {
        api.get.mockRejectedValueOnce({ response: { data: { msg: 'Patient not found' } } })
        await expect(scheduleService.getSchedulesByPatient('bad')).rejects.toThrow('Patient not found')
    })

    // ---------- getSchedulesByWorker ----------
    it('should get schedules by worker successfully', async () => {
        api.get.mockResolvedValueOnce({ data: { code: '0', data: [{ id: 's5' }] } })
        const result = await scheduleService.getSchedulesByWorker('w1')
        expect(api.get).toHaveBeenCalledWith('/schedules/worker/w1')
        expect(result.data[0].id).toBe('s5')
    })

    it('should throw error when getSchedulesByWorker fails', async () => {
        api.get.mockRejectedValueOnce({ response: { data: { msg: 'Worker not found' } } })
        await expect(scheduleService.getSchedulesByWorker('bad')).rejects.toThrow('Worker not found')
    })

    // ---------- getSchedulesByDateRange ----------
    it('should get schedules by date range successfully', async () => {
        api.get.mockResolvedValueOnce({ data: { code: '0', data: [{ id: 's6' }] } })
        const result = await scheduleService.getSchedulesByDateRange('2025-10-01', '2025-10-10')
        expect(api.get).toHaveBeenCalledWith('/schedules/date-range?startDate=2025-10-01&endDate=2025-10-10')
        expect(result.data[0].id).toBe('s6')
    })

    it('should throw error when getSchedulesByDateRange fails', async () => {
        api.get.mockRejectedValueOnce({ response: { data: { msg: 'Invalid range' } } })
        await expect(scheduleService.getSchedulesByDateRange('bad', 'bad')).rejects.toThrow('Invalid range')
    })
})