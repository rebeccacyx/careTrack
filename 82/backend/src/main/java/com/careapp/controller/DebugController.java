package com.careapp.controller;

import com.careapp.domain.User;
import com.careapp.domain.Patient;
import com.careapp.domain.Task;
import com.careapp.domain.Authorization;
import com.careapp.service.UserService;
import com.careapp.service.PatientService;
import com.careapp.service.TaskService;
import com.careapp.service.AuthorizationService;
import com.careapp.utils.Result;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/debug")
public class DebugController {
    
    @Resource
    private UserService userService;
    
    @Resource
    private PatientService patientService;
    
    @Resource
    private TaskService taskService;
    
    @Resource
    private AuthorizationService authorizationService;
    
    // View all user data
    @GetMapping("/users")
    public Result<List<User>> getAllUsers() {
        try {
            // Need to add a getAllUsers method to UserService
            // Temporarily return empty list
            return Result.success(List.of(), "Users retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve users: " + e.getMessage());
        }
    }
    
    // View all patient data
    @GetMapping("/patients")
    public Result<List<Patient>> getAllPatients() {
        try {
            List<Patient> patients = patientService.getAllPatients();
            return Result.success(patients, "Patients retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve patients: " + e.getMessage());
        }
    }
    
    // View all task data
    @GetMapping("/tasks")
    public Result<List<Task>> getAllTasks() {
        try {
            List<Task> tasks = taskService.getAllTasks();
            return Result.success(tasks, "Tasks retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve tasks: " + e.getMessage());
        }
    }
    
    // View all authorization relationships
    @GetMapping("/authorizations")
    public Result<List<Authorization>> getAllAuthorizations() {
        try {
            // Need to add a getAllAuthorizations method to AuthorizationService
            // Temporarily return empty list
            return Result.success(List.of(), "Authorizations retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve authorizations: " + e.getMessage());
        }
    }
    
    // View database overview
    @GetMapping("/overview")
    public Result<Map<String, Object>> getDatabaseOverview() {
        try {
            Map<String, Object> overview = new HashMap<>();
            
            // Patient count
            List<Patient> patients = patientService.getAllPatients();
            overview.put("patientCount", patients.size());
            
            // Task count
            List<Task> tasks = taskService.getAllTasks();
            overview.put("taskCount", tasks.size());
            
            // Patients grouped by organization
            Map<String, Long> patientsByOrg = new HashMap<>();
            for (Patient patient : patients) {
                String orgId = patient.getOrganizationId() != null ? patient.getOrganizationId() : "null";
                patientsByOrg.put(orgId, patientsByOrg.getOrDefault(orgId, 0L) + 1);
            }
            overview.put("patientsByOrganization", patientsByOrg);
            
            // Tasks grouped by organization
            Map<String, Long> tasksByOrg = new HashMap<>();
            for (Task task : tasks) {
                String orgId = task.getOrganizationId() != null ? task.getOrganizationId() : "null";
                tasksByOrg.put(orgId, tasksByOrg.getOrDefault(orgId, 0L) + 1);
            }
            overview.put("tasksByOrganization", tasksByOrg);
            
            // Tasks grouped by assignment status
            Map<String, Long> tasksByAssignment = new HashMap<>();
            for (Task task : tasks) {
                String assignment = task.getAssignedToId() != null ? "assigned" : "unassigned";
                tasksByAssignment.put(assignment, tasksByAssignment.getOrDefault(assignment, 0L) + 1);
            }
            overview.put("tasksByAssignment", tasksByAssignment);
            
            return Result.success(overview, "Database overview retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve database overview: " + e.getMessage());
        }
    }
}

