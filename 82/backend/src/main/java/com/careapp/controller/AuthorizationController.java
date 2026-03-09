package com.careapp.controller;

import com.careapp.domain.Authorization;
import com.careapp.service.AuthorizationService;
import com.careapp.utils.Result;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/authorization")
public class AuthorizationController {

    @Resource
    private AuthorizationService authorizationService;

    // Grant access
    @PostMapping("/grant")
    public Result<String> grantAccess(@RequestBody Map<String, String> request) {
        String patientId = request.get("patientId");
        String authorizedBy = request.get("authorizedBy");
        String authorizedTo = request.get("authorizedTo");
        String authorizationType = request.get("authorizationType");
        String organizationId = request.get("organizationId");
        
        if (patientId == null || authorizedBy == null || authorizedTo == null || authorizationType == null || organizationId == null) {
            return Result.<String>error("400", "Missing required fields!");
        }
        
        boolean success = authorizationService.grantAccess(patientId, authorizedBy, authorizedTo, authorizationType, organizationId);
        if (success) {
            return Result.<String>success("Access granted!", "Access granted successfully!");
        } else {
            return Result.<String>error("400", "Failed to grant access!");
        }
    }

    // Revoke access
    @PostMapping("/revoke")
    public Result<String> revokeAccess(@RequestBody Map<String, String> request) {
        String patientId = request.get("patientId");
        String authorizedBy = request.get("authorizedBy");
        String authorizedTo = request.get("authorizedTo");
        
        if (patientId == null || authorizedBy == null || authorizedTo == null) {
            return Result.<String>error("400", "Missing required fields!");
        }
        
        boolean success = authorizationService.revokeAccess(patientId, authorizedBy, authorizedTo);
        if (success) {
            return Result.<String>success("Access revoked!", "Access revoked successfully!");
        } else {
            return Result.<String>error("400", "Failed to revoke access!");
        }
    }

    // Get authorizations by patient
    @GetMapping("/patient/{patientId}")
    public Result<List<Authorization>> getAuthorizationsByPatient(@PathVariable String patientId) {
        List<Authorization> authorizations = authorizationService.getAuthorizationsByPatient(patientId);
        return Result.<List<Authorization>>success(authorizations, "Authorizations retrieved successfully!");
    }

    // Get authorizations by user
    @GetMapping("/user/{userId}")
    public Result<List<Authorization>> getAuthorizationsByUser(@PathVariable String userId) {
        List<Authorization> authorizations = authorizationService.getAuthorizationsByUser(userId);
        return Result.<List<Authorization>>success(authorizations, "Authorizations retrieved successfully!");
    }

    // Check access
    @GetMapping("/check")
    public Result<Boolean> checkAccess(@RequestParam String userId, @RequestParam String patientId) {
        boolean hasAccess = authorizationService.hasAccess(userId, patientId);
        return Result.<Boolean>success(hasAccess, "Access check completed!");
    }

    // Get accessible patient IDs
    @GetMapping("/accessible-patients/{userId}")
    public Result<List<String>> getAccessiblePatientIds(@PathVariable String userId) {
        List<String> patientIds = authorizationService.getAccessiblePatientIds(userId);
        return Result.<List<String>>success(patientIds, "Accessible patient IDs retrieved successfully!");
    }

    // Revoke all access for user
    @PostMapping("/revoke-all/{userId}")
    public Result<String> revokeAllAccessForUser(@PathVariable String userId) {
        boolean success = authorizationService.revokeAllAccessForUser(userId);
        if (success) {
            return Result.<String>success("All access revoked!", "All access revoked successfully!");
        } else {
            return Result.<String>error("400", "Failed to revoke all access!");
        }
    }

    // Revoke all access by authorizer
    @PostMapping("/revoke-by-authorizer/{authorizerId}")
    public Result<String> revokeAllAccessByAuthorizer(@PathVariable String authorizerId) {
        boolean success = authorizationService.revokeAllAccessByAuthorizer(authorizerId);
        if (success) {
            return Result.<String>success("All access revoked!", "All access revoked successfully!");
        } else {
            return Result.<String>error("400", "Failed to revoke all access!");
        }
    }

    // Revoke all access for organization (Carer Team functionality)
    @PostMapping("/revoke-organization/{organizationId}")
    public Result<String> revokeAllAccessForOrganization(@PathVariable String organizationId) {
        boolean success = authorizationService.revokeAllAccessForOrganization(organizationId);
        if (success) {
            return Result.<String>success("Organization access revoked!", "All organization access revoked successfully!");
        } else {
            return Result.<String>error("400", "Failed to revoke organization access!");
        }
    }
    
    // Get organizations accessible by Family/POA
    @GetMapping("/accessible-orgs/{familyId}")
    public Result<List<String>> getAccessibleOrganizations(@PathVariable String familyId) {
        List<String> orgIds = authorizationService.getAccessibleOrganizations(familyId);
        return Result.<List<String>>success(orgIds, "Accessible organizations retrieved successfully!");
    }


}
