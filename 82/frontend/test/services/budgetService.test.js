import { describe, it, expect, vi, beforeEach } from 'vitest'
import * as budgetService from '@/services/budgetService'
import api from '@/services/api'

vi.mock('@/services/api')

describe('budgetService.js', () => {
    beforeEach(() => {
        vi.clearAllMocks()
    })

    // ----------- Basic CRUD -----------

    it('should get budget by patient id', async () => {
        api.get.mockResolvedValueOnce({ data: { code: '0', data: { total: 1000 } } })
        const result = await budgetService.getBudgetByPatient('p1')
        expect(api.get).toHaveBeenCalledWith('/budget/patient/p1')
        expect(result.data.total).toBe(1000)
    })

    it('should throw error when get budget fails', async () => {
        api.get.mockResolvedValueOnce({ data: { code: '1', msg: 'Failed' } })
        await expect(budgetService.getBudgetByPatient('p1')).rejects.toThrow('Failed')
    })

    it('should create budget successfully', async () => {
        api.post.mockResolvedValueOnce({ data: { code: '0', data: { id: 'b1' } } })
        const result = await budgetService.createBudget({ name: 'test' })
        expect(api.post).toHaveBeenCalledWith('/budget', { name: 'test' })
        expect(result.data.id).toBe('b1')
    })

    it('should throw error when create budget fails', async () => {
        api.post.mockResolvedValueOnce({ data: { code: '1', msg: 'Invalid data' } })
        await expect(budgetService.createBudget({})).rejects.toThrow('Invalid data')
    })

    it('should update budget successfully', async () => {
        api.put.mockResolvedValueOnce({ data: { code: '0', data: { id: 'b2' } } })
        const result = await budgetService.updateBudget({ id: 'b2', name: 'updated' })
        expect(api.put).toHaveBeenCalledWith('/budget', { id: 'b2', name: 'updated' })
        expect(result.data.id).toBe('b2')
    })

    it('should delete budget successfully', async () => {
        api.delete.mockResolvedValueOnce({ data: { code: '0', data: true } })
        const result = await budgetService.deleteBudget('b1')
        expect(api.delete).toHaveBeenCalledWith('/budget/b1')
        expect(result.data).toBe(true)
    })

    // ----------- Category -----------

    it('should get budget categories', async () => {
        api.get.mockResolvedValueOnce({ data: { code: '0', data: [{ id: 'c1' }] } })
        const result = await budgetService.getBudgetCategories('p1')
        expect(api.get).toHaveBeenCalledWith('/budget/patient/p1/categories')
        expect(result.data[0].id).toBe('c1')
    })

    it('should create budget category', async () => {
        api.post.mockResolvedValueOnce({ data: { code: '0', data: { id: 'c2' } } })
        const result = await budgetService.createBudgetCategory('p1', { name: 'Food' })
        expect(api.post).toHaveBeenCalledWith('/budget/patient/p1/categories', { name: 'Food' })
        expect(result.data.id).toBe('c2')
    })

    it('should update budget category', async () => {
        api.put.mockResolvedValueOnce({ data: { code: '0', data: { id: 'c3' } } })
        const result = await budgetService.updateBudgetCategory('p1', 'c3', { name: 'Updated' })
        expect(api.put).toHaveBeenCalledWith('/budget/patient/p1/categories/c3', { name: 'Updated' })
        expect(result.data.id).toBe('c3')
    })

    it('should delete budget category', async () => {
        api.delete.mockResolvedValueOnce({ data: { code: '0', data: true } })
        const result = await budgetService.deleteBudgetCategory('p1', 'c1')
        expect(api.delete).toHaveBeenCalledWith('/budget/patient/p1/categories/c1')
        expect(result.data).toBe(true)
    })

    // ----------- Sub-elements -----------

    it('should get budget sub-elements', async () => {
        api.get.mockResolvedValueOnce({ data: { code: '0', data: [{ id: 's1' }] } })
        const result = await budgetService.getBudgetSubElements('p1', 'c1')
        expect(api.get).toHaveBeenCalledWith('/budget/patient/p1/categories/c1/sub-elements')
        expect(result.data[0].id).toBe('s1')
    })

    it('should create budget sub-element', async () => {
        api.post.mockResolvedValueOnce({ data: { code: '0', data: { id: 's2' } } })
        const result = await budgetService.createBudgetSubElement('p1', 'c1', { name: 'test' })
        expect(api.post).toHaveBeenCalledWith('/budget/patient/p1/categories/c1/sub-elements', { name: 'test' })
        expect(result.data.id).toBe('s2')
    })

    it('should update budget sub-element', async () => {
        api.put.mockResolvedValueOnce({ data: { code: '0', data: { id: 's3' } } })
        const result = await budgetService.updateBudgetSubElement('p1', 'c1', 's3', { name: 'updated' })
        expect(api.put).toHaveBeenCalledWith('/budget/patient/p1/categories/c1/sub-elements/s3', { name: 'updated' })
        expect(result.data.id).toBe('s3')
    })

    it('should delete budget sub-element', async () => {
        api.delete.mockResolvedValueOnce({ data: { code: '0', data: true } })
        const result = await budgetService.deleteBudgetSubElement('p1', 'c1', 's1')
        expect(api.delete).toHaveBeenCalledWith('/budget/patient/p1/categories/c1/sub-elements/s1')
        expect(result.data).toBe(true)
    })

    // ----------- Calculations & Reports -----------

    it('should get budget calculations', async () => {
        api.get.mockResolvedValueOnce({ data: { code: '0', data: { total: 200 } } })
        const result = await budgetService.getBudgetCalculations('p1')
        expect(api.get).toHaveBeenCalledWith('/budget/patient/p1/calculations')
        expect(result.data.total).toBe(200)
    })

    it('should throw error when budget calculation fails', async () => {
        api.get.mockResolvedValueOnce({ data: { code: '1', msg: 'Server error' } })
        await expect(budgetService.getBudgetCalculations('p1')).rejects.toThrow('Server error')
    })

    it('should get budget reports', async () => {
        api.get.mockResolvedValueOnce({ data: { code: '0', data: { report: 'ok' } } })
        const result = await budgetService.getBudgetReports('p1', 'monthly', '2025-01-01', '2025-01-31')
        expect(api.get).toHaveBeenCalledWith('/budget/patient/p1/reports?type=monthly&startDate=2025-01-01&endDate=2025-01-31')
        expect(result.data.report).toBe('ok')
    })

    it('should throw error when report fetch fails', async () => {
        api.get.mockResolvedValueOnce({ data: { code: '1', msg: 'Report not found' } })
        await expect(budgetService.getBudgetReports('p1')).rejects.toThrow('Report not found')
    })
})