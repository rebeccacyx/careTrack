import { describe, it, expect, vi, beforeEach } from 'vitest'
import * as patientService from '@/services/patientService'
import api from '@/services/api'

vi.mock('@/services/api')

describe('patientService.js', () => {
    beforeEach(() => {
        vi.clearAllMocks()
    })

    // ---------- getAllPatients ----------
    it('should get all patients successfully', async () => {
        api.get.mockResolvedValueOnce({ data: { code: '0', data: [{ id: 1, name: 'John' }] } })
        const result = await patientService.getAllPatients()
        expect(api.get).toHaveBeenCalledWith('/patients')
        expect(result.data[0].name).toBe('John')
    })

    it('should throw error when getAllPatients fails', async () => {
        api.get.mockRejectedValueOnce({ response: { data: { msg: 'Server error' } } })
        await expect(patientService.getAllPatients()).rejects.toThrow('Server error')
    })

    // ---------- getPatientById ----------
    it('should get patient by ID successfully', async () => {
        api.get.mockResolvedValueOnce({ data: { code: '0', data: { id: 'p1', name: 'Alice' } } })
        const result = await patientService.getPatientById('p1')
        expect(api.get).toHaveBeenCalledWith('/patients/p1')
        expect(result.data.name).toBe('Alice')
    })

    it('should throw error when getPatientById fails', async () => {
        api.get.mockRejectedValueOnce({ response: { data: { msg: 'Not found' } } })
        await expect(patientService.getPatientById('pX')).rejects.toThrow('Not found')
    })

    // ---------- createPatient ----------
    it('should create patient successfully', async () => {
        api.post.mockResolvedValueOnce({ data: { code: '0', data: { id: 'p2', name: 'New' } } })
        const result = await patientService.createPatient({ name: 'New' })
        expect(api.post).toHaveBeenCalledWith('/patients', { name: 'New' })
        expect(result.data.id).toBe('p2')
    })

    it('should throw error when createPatient fails', async () => {
        api.post.mockRejectedValueOnce({ response: { data: { msg: 'Invalid data' } } })
        await expect(patientService.createPatient({})).rejects.toThrow('Invalid data')
    })

    // ---------- updatePatient ----------
    it('should update patient successfully', async () => {
        api.put.mockResolvedValueOnce({ data: { code: '0', data: { id: 'p1', name: 'Updated' } } })
        const result = await patientService.updatePatient('p1', { name: 'Updated' })
        expect(api.put).toHaveBeenCalledWith('/patients/p1', { name: 'Updated' })
        expect(result.data.name).toBe('Updated')
    })

    it('should throw error when updatePatient fails', async () => {
        api.put.mockRejectedValueOnce({ response: { data: { msg: 'Update failed' } } })
        await expect(patientService.updatePatient('p1', {})).rejects.toThrow('Update failed')
    })

    // ---------- deletePatient ----------
    it('should delete patient successfully', async () => {
        api.delete.mockResolvedValueOnce({ data: { code: '0', data: { success: true } } })
        const result = await patientService.deletePatient('p3')
        expect(api.delete).toHaveBeenCalledWith('/patients/p3')
        expect(result.data.success).toBe(true)
    })

    it('should throw error when deletePatient fails', async () => {
        api.delete.mockRejectedValueOnce({ response: { data: { msg: 'Delete error' } } })
        await expect(patientService.deletePatient('pX')).rejects.toThrow('Delete error')
    })

    // ---------- getPatientsByOrganization ----------
    it('should get patients by organization successfully', async () => {
        api.get.mockResolvedValueOnce({ data: { code: '0', data: [{ id: 'p1' }] } })
        const result = await patientService.getPatientsByOrganization('org1')
        expect(api.get).toHaveBeenCalledWith('/patients/organization/org1')
        expect(result.data[0].id).toBe('p1')
    })

    it('should throw error when getPatientsByOrganization fails', async () => {
        api.get.mockRejectedValueOnce({ response: { data: { msg: 'Org not found' } } })
        await expect(patientService.getPatientsByOrganization('bad')).rejects.toThrow('Org not found')
    })

    // ---------- getPatientsByFamilyMember ----------
    it('should get patients by family member successfully', async () => {
        api.get.mockResolvedValueOnce({ data: { code: '0', data: [{ id: 'p4' }] } })
        const result = await patientService.getPatientsByFamilyMember('f1')
        expect(api.get).toHaveBeenCalledWith('/patients/family-member/f1')
        expect(result.data[0].id).toBe('p4')
    })

    it('should throw error when getPatientsByFamilyMember fails', async () => {
        api.get.mockRejectedValueOnce({ response: { data: { msg: 'Family not found' } } })
        await expect(patientService.getPatientsByFamilyMember('fX')).rejects.toThrow('Family not found')
    })

    // ---------- getPatientByClientId ----------
    it('should get patient by client ID successfully', async () => {
        api.get.mockResolvedValueOnce({ data: { code: '0', data: { id: 'p5' } } })
        const result = await patientService.getPatientByClientId('c1')
        expect(api.get).toHaveBeenCalledWith('/patients/client/c1')
        expect(result.data.id).toBe('p5')
    })

    it('should throw error when getPatientByClientId fails', async () => {
        api.get.mockRejectedValueOnce({ response: { data: { msg: 'Client not found' } } })
        await expect(patientService.getPatientByClientId('bad')).rejects.toThrow('Client not found')
    })

    // ---------- removeOrganizationFromPatient ----------
    it('should remove organization from patient successfully', async () => {
        api.post.mockResolvedValueOnce({ data: { code: '0', data: { removed: true } } })
        const result = await patientService.removeOrganizationFromPatient('p6', { reason: 'Transfer' })
        expect(api.post).toHaveBeenCalledWith('/patients/p6/remove-organization', { reason: 'Transfer' })
        expect(result.data.removed).toBe(true)
    })

    it('should throw error when removeOrganizationFromPatient fails', async () => {
        api.post.mockRejectedValueOnce({ response: { data: { msg: 'Failed to remove' } } })
        await expect(patientService.removeOrganizationFromPatient('pX', {})).rejects.toThrow('Failed to remove')
    })
})