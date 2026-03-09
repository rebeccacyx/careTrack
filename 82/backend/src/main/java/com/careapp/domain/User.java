package com.careapp.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
public class User {
    @Id
    private String id;
    
    // Frontend required fields
    private String firstName;    // first name
    private String middleName;   // middle name
    private String lastName;     // last name
    private String email;        // email
    private String role;         // role: POA, Worker, Admin
    private String password;     // password
    private String status  = "PENDING";

    // Authorization system fields
    private String userType;     // FM, POA, MANAGER, WORKER
    private String organizationId; // organization ID
    private String patientId;    // associated patient ID (for FM/POA)
    private String managerId;    // manager ID (for WORKER userType)
    
    // Temporary field for registration (not persisted to database)
    @Transient
    private String organizationName; // organization name (used during registration to generate organizationId)
    
    // keep uname field for backward compatibility
    private String uname;

    // Password reset fields
    private String passwordResetToken;   // reset token
    private Long passwordResetExpires;   // epoch millis when token expires
    
    // Invite code fields
    private boolean hasUsedInviteCode = false;  // whether user has used an invite code
    
    // Notification settings
    private boolean taskReminders = true;
    private boolean approvalNotifications = true;
    private boolean budgetWarning = true;
    private boolean emailNotifications = true;
    
    // Shift time settings (for managers)
    private String morningShiftStart = "08:00";
    private String morningShiftEnd = "16:00";
    private String afternoonShiftStart = "12:00";
    private String afternoonShiftEnd = "20:00";
    private String eveningShiftStart = "16:00";
    private String eveningShiftEnd = "24:00";

    // getters & setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getMiddleName() { return middleName; }
    public void setMiddleName(String middleName) { this.middleName = middleName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    // Authorization system getters & setters
    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }

    public String getOrganizationId() { return organizationId; }
    public void setOrganizationId(String organizationId) { this.organizationId = organizationId; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getManagerId() { return managerId; }
    public void setManagerId(String managerId) { this.managerId = managerId; }

    // Backward compatibility uname field
    public String getUname() { return uname; }
    public void setUname(String uname) { this.uname = uname; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPasswordResetToken() { return passwordResetToken; }
    public void setPasswordResetToken(String passwordResetToken) { this.passwordResetToken = passwordResetToken; }

    public Long getPasswordResetExpires() { return passwordResetExpires; }
    public void setPasswordResetExpires(Long passwordResetExpires) { this.passwordResetExpires = passwordResetExpires; }

    public boolean isHasUsedInviteCode() { return hasUsedInviteCode; }
    public void setHasUsedInviteCode(boolean hasUsedInviteCode) { this.hasUsedInviteCode = hasUsedInviteCode; }

    // Notification settings getters & setters
    public boolean isTaskReminders() { return taskReminders; }
    public void setTaskReminders(boolean taskReminders) { this.taskReminders = taskReminders; }

    public boolean isApprovalNotifications() { return approvalNotifications; }
    public void setApprovalNotifications(boolean approvalNotifications) { this.approvalNotifications = approvalNotifications; }

    public boolean isBudgetWarning() { return budgetWarning; }
    public void setBudgetWarning(boolean budgetWarning) { this.budgetWarning = budgetWarning; }

    public boolean isEmailNotifications() { return emailNotifications; }
    public void setEmailNotifications(boolean emailNotifications) { this.emailNotifications = emailNotifications; }

    // Shift time settings getters & setters
    public String getMorningShiftStart() { return morningShiftStart; }
    public void setMorningShiftStart(String morningShiftStart) { this.morningShiftStart = morningShiftStart; }

    public String getMorningShiftEnd() { return morningShiftEnd; }
    public void setMorningShiftEnd(String morningShiftEnd) { this.morningShiftEnd = morningShiftEnd; }

    public String getAfternoonShiftStart() { return afternoonShiftStart; }
    public void setAfternoonShiftStart(String afternoonShiftStart) { this.afternoonShiftStart = afternoonShiftStart; }

    public String getAfternoonShiftEnd() { return afternoonShiftEnd; }
    public void setAfternoonShiftEnd(String afternoonShiftEnd) { this.afternoonShiftEnd = afternoonShiftEnd; }

    public String getEveningShiftStart() { return eveningShiftStart; }
    public void setEveningShiftStart(String eveningShiftStart) { this.eveningShiftStart = eveningShiftStart; }

    public String getEveningShiftEnd() { return eveningShiftEnd; }
    public void setEveningShiftEnd(String eveningShiftEnd) { this.eveningShiftEnd = eveningShiftEnd; }
    
    // Organization name getter & setter (transient field)
    public String getOrganizationName() { return organizationName; }
    public void setOrganizationName(String organizationName) { this.organizationName = organizationName; }
}
