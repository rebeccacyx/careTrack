package com.careapp.controller;

import com.careapp.domain.InviteCode;
import com.careapp.service.InviteCodeService;
import com.careapp.utils.Result;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/invite")
public class InviteCodeController {

    @Resource
    private InviteCodeService inviteCodeService;

    // Generate invite code
    @PostMapping("/generate")
    public Result<String> generateInviteCode(@RequestBody Map<String, String> request) {
        String createdBy = request.get("createdBy");
        String createdByType = request.get("createdByType");
        String targetType = request.get("targetType");
        String patientId = request.get("patientId");
        String organizationId = request.get("organizationId");
        
        if (createdBy == null || createdByType == null || targetType == null || patientId == null || organizationId == null) {
            return Result.<String>error("400", "Missing required fields!");
        }
        
        // Validate user types
        if (!"FM".equals(createdByType) && !"POA".equals(createdByType) && !"MANAGER".equals(createdByType)) {
            return Result.<String>error("400", "Invalid creator type! Must be FM, POA, or MANAGER");
        }
        
        if (!"MANAGER".equals(targetType) && !"WORKER".equals(targetType)) {
            return Result.<String>error("400", "Invalid target type! Must be MANAGER or WORKER");
        }
        
        String code = inviteCodeService.generateInviteCode(createdBy, createdByType, targetType, patientId, organizationId);
        return Result.<String>success(code, "Invite code generated successfully!");
    }

    // Use invite code
    @PostMapping("/use")
    public Result<String> useInviteCode(@RequestBody Map<String, String> request) {
        String code = request.get("code");
        String usedBy = request.get("usedBy");
        
        System.out.println("🔍 Backend - useInviteCode called with code: " + code + ", usedBy: " + usedBy);
        
        if (code == null || usedBy == null) {
            System.out.println("❌ Backend - Missing required parameters");
            return Result.<String>error("400", "Code and usedBy are required!");
        }
        
        if (!inviteCodeService.validateInviteCode(code)) {
            System.out.println("❌ Backend - Invite code validation failed for code: " + code);
            return Result.<String>error("400", "Invalid or expired invite code!");
        }
        
        boolean success = inviteCodeService.useInviteCode(code, usedBy);
        if (success) {
            System.out.println("✅ Backend - Invite code used successfully");
            return Result.<String>success("Access granted!", "Invite code used successfully!");
        } else {
            System.out.println("❌ Backend - Failed to use invite code");
            return Result.<String>error("400", "Failed to use invite code! The token type may not match your user role, or the token may be invalid.");
        }
    }

    // Get my invite codes
    @GetMapping("/my-codes")
    public Result<List<InviteCode>> getMyInviteCodes(@RequestParam String creatorId) {
        List<InviteCode> codes = inviteCodeService.getInviteCodesByCreator(creatorId);
        return Result.<List<InviteCode>>success(codes, "Invite codes retrieved successfully!");
    }

    // Revoke invite code
    @DeleteMapping("/{codeId}")
    public Result<String> revokeInviteCode(@PathVariable String codeId) {
        boolean success = inviteCodeService.revokeInviteCode(codeId);
        if (success) {
            return Result.<String>success("Invite code revoked!", "Invite code revoked successfully!");
        } else {
            return Result.<String>error("400", "Failed to revoke invite code!");
        }
    }

    // Get active invite codes for patient
    @GetMapping("/patient/{patientId}")
    public Result<List<InviteCode>> getActiveInviteCodesForPatient(@PathVariable String patientId) {
        List<InviteCode> codes = inviteCodeService.getActiveInviteCodesForPatient(patientId);
        return Result.<List<InviteCode>>success(codes, "Active invite codes retrieved successfully!");
    }
}
