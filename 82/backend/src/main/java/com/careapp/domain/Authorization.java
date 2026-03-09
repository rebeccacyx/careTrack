package com.careapp.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "authorizations")
public class Authorization {
    @Id
    private String id;
    
    private String patientId; // patient ID
    private String authorizedBy; // authorizer ID (FM/POA)
    private String authorizedTo; // authorized user ID (Manager/Worker)
    private String authorizationType; // "MANAGER", "WORKER"
    private LocalDateTime grantedAt;
    private LocalDateTime revokedAt;
    private boolean isActive;
    private String organizationId; // organization ID
    
    // getters & setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getAuthorizedBy() { return authorizedBy; }
    public void setAuthorizedBy(String authorizedBy) { this.authorizedBy = authorizedBy; }

    public String getAuthorizedTo() { return authorizedTo; }
    public void setAuthorizedTo(String authorizedTo) { this.authorizedTo = authorizedTo; }

    public String getAuthorizationType() { return authorizationType; }
    public void setAuthorizationType(String authorizationType) { this.authorizationType = authorizationType; }

    public LocalDateTime getGrantedAt() { return grantedAt; }
    public void setGrantedAt(LocalDateTime grantedAt) { this.grantedAt = grantedAt; }

    public LocalDateTime getRevokedAt() { return revokedAt; }
    public void setRevokedAt(LocalDateTime revokedAt) { this.revokedAt = revokedAt; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public String getOrganizationId() { return organizationId; }
    public void setOrganizationId(String organizationId) { this.organizationId = organizationId; }
}
