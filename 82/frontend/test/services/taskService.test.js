import { describe, it, expect, vi, beforeEach } from 'vitest'
import * as taskService from '@/services/taskService'
import api from '@/services/api'

vi.mock('@/services/api')

describe('taskService.js', () => {
    beforeEach(() => {
        vi.clearAllMocks()
    })

    // ---------- getAllTasks ----------
    it('should get all tasks successfully', async () => {
        api.get.mockResolvedValueOnce({ data: { code: '0', data: [{ id: 't1', name: 'Test Task' }] } })
        const result = await taskService.getAllTasks()
        expect(api.get).toHaveBeenCalledWith('/tasks')
        expect(result.data[0].name).toBe('Test Task')
    })

    it('should throw error when getAllTasks fails', async () => {
        api.get.mockRejectedValueOnce({ response: { data: { msg: 'Server error' } } })
        await expect(taskService.getAllTasks()).rejects.toThrow('Server error')
    })

    // ---------- getTasksByPatient ----------
    it('should get tasks by patient successfully', async () => {
        api.get.mockResolvedValueOnce({ data: { code: '0', data: [{ id: 't2' }] } })
        const result = await taskService.getTasksByPatient('p1')
        expect(api.get).toHaveBeenCalledWith('/tasks/patient/p1')
        expect(result.data[0].id).toBe('t2')
    })

    it('should throw error when getTasksByPatient fails', async () => {
        api.get.mockRejectedValueOnce({ response: { data: { msg: 'Patient not found' } } })
        await expect(taskService.getTasksByPatient('bad')).rejects.toThrow('Patient not found')
    })

    // ---------- getTasksByWorker ----------
    it('should get tasks by worker successfully', async () => {
        api.get.mockResolvedValueOnce({ data: { code: '0', data: [{ id: 't3' }] } })
        const result = await taskService.getTasksByWorker('w1')
        expect(api.get).toHaveBeenCalledWith('/tasks/worker/w1')
        expect(result.data[0].id).toBe('t3')
    })

    it('should throw error when getTasksByWorker fails', async () => {
        api.get.mockRejectedValueOnce({ response: { data: { msg: 'Worker not found' } } })
        await expect(taskService.getTasksByWorker('bad')).rejects.toThrow('Worker not found')
    })

    // ---------- createTask ----------
    it('should create task successfully', async () => {
        api.post.mockResolvedValueOnce({ data: { code: '0', data: { id: 't4' } } })
        const result = await taskService.createTask({ name: 'New Task' })
        expect(api.post).toHaveBeenCalledWith('/tasks', { name: 'New Task' })
        expect(result.data.id).toBe('t4')
    })

    it('should throw error when createTask fails', async () => {
        api.post.mockRejectedValueOnce({ response: { data: { msg: 'Invalid data' } } })
        await expect(taskService.createTask({})).rejects.toThrow('Invalid data')
    })

    // ---------- updateTask ----------
    it('should update task successfully', async () => {
        api.put.mockResolvedValueOnce({ data: { code: '0', data: { updated: true } } })
        const result = await taskService.updateTask('t1', { name: 'Updated' })
        expect(api.put).toHaveBeenCalledWith('/tasks/t1', { name: 'Updated' })
        expect(result.data.updated).toBe(true)
    })

    it('should throw error when updateTask fails', async () => {
        api.put.mockRejectedValueOnce({ response: { data: { msg: 'Update failed' } } })
        await expect(taskService.updateTask('t1', {})).rejects.toThrow('Update failed')
    })

    // ---------- deleteTask ----------
    it('should delete task successfully', async () => {
        api.delete.mockResolvedValueOnce({ data: { code: '0', data: { deleted: true } } })
        const result = await taskService.deleteTask('t1')
        expect(api.delete).toHaveBeenCalledWith('/tasks/t1')
        expect(result.data.deleted).toBe(true)
    })

    it('should throw error when deleteTask fails', async () => {
        api.delete.mockRejectedValueOnce({ response: { data: { msg: 'Delete failed' } } })
        await expect(taskService.deleteTask('bad')).rejects.toThrow('Delete failed')
    })

    // ---------- assignTask ----------
    it('should assign task successfully', async () => {
        api.post.mockResolvedValueOnce({ data: { code: '0', data: { assigned: true } } })
        const result = await taskService.assignTask('t1', 'w1')
        expect(api.post).toHaveBeenCalledWith('/tasks/t1/assign', { workerId: 'w1' })
        expect(result.data.assigned).toBe(true)
    })

    it('should throw error when assignTask fails', async () => {
        api.post.mockRejectedValueOnce({ response: { data: { msg: 'Assign failed' } } })
        await expect(taskService.assignTask('t1', 'bad')).rejects.toThrow('Assign failed')
    })

    // ---------- completeTask ----------
    it('should complete task successfully', async () => {
        api.post.mockResolvedValueOnce({ data: { code: '0', data: { completed: true } } })
        const result = await taskService.completeTask('t1', { notes: 'Done' })
        expect(api.post).toHaveBeenCalledWith('/tasks/t1/complete', { notes: 'Done' })
        expect(result.data.completed).toBe(true)
    })

    it('should throw error when completeTask fails', async () => {
        api.post.mockRejectedValueOnce({ response: { data: { msg: 'Complete failed' } } })
        await expect(taskService.completeTask('t1', {})).rejects.toThrow('Complete failed')
    })

    // ---------- approveTask ----------
    it('should approve task successfully', async () => {
        api.post.mockResolvedValueOnce({ data: { code: '0', data: { approved: true } } })
        const result = await taskService.approveTask('t1', { approvedBy: 'manager' })
        expect(api.post).toHaveBeenCalledWith('/tasks/t1/approve', { approvedBy: 'manager' })
        expect(result.data.approved).toBe(true)
    })

    it('should throw error when approveTask fails', async () => {
        api.post.mockRejectedValueOnce({ response: { data: { msg: 'Approval failed' } } })
        await expect(taskService.approveTask('t1', {})).rejects.toThrow('Approval failed')
    })

    // ---------- rejectTask ----------
    it('should reject task successfully', async () => {
        api.post.mockResolvedValueOnce({ data: { code: '0', data: { rejected: true } } })
        const result = await taskService.rejectTask('t1', { reason: 'Bad quality' })
        expect(api.post).toHaveBeenCalledWith('/tasks/t1/reject', { reason: 'Bad quality' })
        expect(result.data.rejected).toBe(true)
    })

    it('should throw error when rejectTask fails', async () => {
        api.post.mockRejectedValueOnce({ response: { data: { msg: 'Reject failed' } } })
        await expect(taskService.rejectTask('t1', {})).rejects.toThrow('Reject failed')
    })

    // ---------- getRecurringTasks ----------
    it('should get recurring tasks successfully', async () => {
        api.get.mockResolvedValueOnce({ data: { code: '0', data: [{ id: 'r1' }] } })
        const result = await taskService.getRecurringTasks()
        expect(api.get).toHaveBeenCalledWith('/tasks/recurring')
        expect(result.data[0].id).toBe('r1')
    })

    it('should throw error when getRecurringTasks fails', async () => {
        api.get.mockRejectedValueOnce({ response: { data: { msg: 'Recurring fetch failed' } } })
        await expect(taskService.getRecurringTasks()).rejects.toThrow('Recurring fetch failed')
    })

    // ---------- createRecurringTask ----------
    it('should create recurring task successfully', async () => {
        api.post.mockResolvedValueOnce({ data: { code: '0', data: { id: 'r2' } } })
        const result = await taskService.createRecurringTask({ name: 'Weekly task' })
        expect(api.post).toHaveBeenCalledWith('/tasks/recurring', { name: 'Weekly task' })
        expect(result.data.id).toBe('r2')
    })

    it('should throw error when createRecurringTask fails', async () => {
        api.post.mockRejectedValueOnce({ response: { data: { msg: 'Recurring create failed' } } })
        await expect(taskService.createRecurringTask({})).rejects.toThrow('Recurring create failed')
    })

    // ---------- updateRecurringTask ----------
    it('should update recurring task successfully', async () => {
        api.put.mockResolvedValueOnce({ data: { code: '0', data: { updated: true } } })
        const result = await taskService.updateRecurringTask('r1', { name: 'Updated' })
        expect(api.put).toHaveBeenCalledWith('/tasks/recurring/r1', { name: 'Updated' })
        expect(result.data.updated).toBe(true)
    })

    it('should throw error when updateRecurringTask fails', async () => {
        api.put.mockRejectedValueOnce({ response: { data: { msg: 'Recurring update failed' } } })
        await expect(taskService.updateRecurringTask('r1', {})).rejects.toThrow('Recurring update failed')
    })

    // ---------- deleteRecurringTask ----------
    it('should delete recurring task successfully', async () => {
        api.delete.mockResolvedValueOnce({ data: { code: '0', data: { deleted: true } } })
        const result = await taskService.deleteRecurringTask('r1')
        expect(api.delete).toHaveBeenCalledWith('/tasks/recurring/r1')
        expect(result.data.deleted).toBe(true)
    })

    it('should throw error when deleteRecurringTask fails', async () => {
        api.delete.mockRejectedValueOnce({ response: { data: { msg: 'Recurring delete failed' } } })
        await expect(taskService.deleteRecurringTask('bad')).rejects.toThrow('Recurring delete failed')
    })
})