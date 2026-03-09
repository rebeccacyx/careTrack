package com.careapp.controller;

import com.careapp.domain.Task;
import com.careapp.domain.RecurringTask;
import com.careapp.domain.TaskRequest;
import com.careapp.dto.CreateTaskRequest;
import com.careapp.dto.CreateRecurringTaskRequest;
import com.careapp.service.TaskService;
import com.careapp.service.RecurringTaskService;
import com.careapp.service.TaskRequestService;
import com.careapp.utils.Result;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    
    @Resource
    private TaskService taskService;
    
    @Resource
    private RecurringTaskService recurringTaskService;
    
    @Resource
    private TaskRequestService taskRequestService;
    
    // Create a new task
    @PostMapping
    public Result<Task> createTask(@RequestBody Task task) {
        try {
            Task createdTask = taskService.createTask(task);
            return Result.success(createdTask, "Task created successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to create task: " + e.getMessage());
        }
    }
    
    // Create a new task for the default patient (simplified for Manager)
    @PostMapping("/create-for-patient")
    public Result<Task> createTaskForPatient(
            @RequestBody CreateTaskRequest request,
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestHeader(value = "X-Organization-Id", required = false) String organizationId,
            @RequestHeader(value = "X-Patient-Id", required = false) String patientId) {
        try {
            // Validate required fields
            if (request.getTitle() == null || request.getTitle().isEmpty()) {
                return Result.error("400", "title is required");
            }
            
            // Create Task object with automatic default values
            Task task = new Task();
            task.setTitle(request.getTitle());
            if (request.getDescription() != null) {
                task.setDescription(request.getDescription());
            }
            if (request.getAssignedTo() != null) {
                task.setAssignedTo(request.getAssignedTo());
            }
            if (request.getPriority() != null) {
                task.setPriority(request.getPriority());
            }
            if (request.getDueDate() != null) {
                task.setDueDate(request.getDueDate());
            }
            
            // Get user information from request headers, use defaults if not provided (backward compatibility)
            task.setPatientId(patientId != null ? patientId : "default-patient-001");
            task.setCreatedBy(userId != null ? userId : "manager-001");
            task.setOrganizationId(organizationId != null ? organizationId : "org-001");
            task.setStatus("In Progress");             // Default status
            
            // Set assignedToId - require explicit worker ID to avoid name ambiguity
            if (request.getAssignedToId() != null && !request.getAssignedToId().isEmpty()) {
                task.setAssignedToId(request.getAssignedToId());
            } else if (request.getAssignedTo() != null && !request.getAssignedTo().isEmpty()) {
                // Reject ambiguous name-only assignment to prevent misrouting
                return Result.error("400", "assignedToId is required when assigning a worker. Do not use name-only assignment.");
            } else {
                // Task is unassigned - no worker assigned
                task.setAssignedToId(null);
            }
            
            Task createdTask = taskService.createTask(task);
            return Result.success(createdTask, "Task created successfully for patient!");
        } catch (Exception e) {
            return Result.error("500", "Failed to create task: " + e.getMessage());
        }
    }
    
    // Get all tasks
    @GetMapping
    public Result<List<Task>> getAllTasks() {
        try {
            List<Task> tasks = taskService.getAllTasks();
            return Result.success(tasks, "Tasks retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve tasks: " + e.getMessage());
        }
    }
    
    // ==================== Recurring Task Endpoints ====================
    
    // Create recurring task template
    @PostMapping("/recurring")
    public Result<RecurringTask> createRecurringTask(
            @RequestBody CreateRecurringTaskRequest request,
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestHeader(value = "X-Organization-Id", required = false) String organizationId,
            @RequestHeader(value = "X-Patient-Id", required = false) String patientId) {
        try {
            // Set user information from headers if provided
            if (userId != null) {
                request.setCreatedBy(userId);
            }
            if (organizationId != null) {
                request.setOrganizationId(organizationId);
            }
            if (patientId != null) {
                request.setPatientId(patientId);
            }
            
            RecurringTask createdTask = recurringTaskService.createRecurringTask(request);
            return Result.success(createdTask, "Recurring task template created successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to create recurring task template: " + e.getMessage());
        }
    }
    
    // Get all recurring task templates
    @GetMapping("/recurring")
    public Result<List<RecurringTask>> getAllRecurringTasks() {
        try {
            List<RecurringTask> tasks = recurringTaskService.getAllRecurringTasks();
            return Result.success(tasks, "Recurring task templates retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve recurring task templates: " + e.getMessage());
        }
    }
    
    // Update recurring task template
    @PutMapping("/recurring/{id}")
    public Result<RecurringTask> updateRecurringTask(@PathVariable String id, @RequestBody CreateRecurringTaskRequest request) {
        try {
            RecurringTask updatedTask = recurringTaskService.updateRecurringTask(id, request);
            if (updatedTask != null) {
                return Result.success(updatedTask, "Recurring task template updated successfully!");
            } else {
                return Result.error("404", "Recurring task template not found!");
            }
        } catch (Exception e) {
            return Result.error("500", "Failed to update recurring task template: " + e.getMessage());
        }
    }
    
    // Delete recurring task template
    @DeleteMapping("/recurring/{id}")
    public Result<String> deleteRecurringTask(@PathVariable String id) {
        try {
            boolean deleted = recurringTaskService.deleteRecurringTask(id);
            if (deleted) {
                return Result.success("Recurring task template deleted successfully!");
            } else {
                return Result.error("404", "Recurring task template not found!");
            }
        } catch (Exception e) {
            return Result.error("500", "Failed to delete recurring task template: " + e.getMessage());
        }
    }
    
    // Toggle recurring task template status
    @PostMapping("/recurring/{id}/toggle")
    public Result<RecurringTask> toggleRecurringTaskStatus(@PathVariable String id) {
        try {
            RecurringTask updatedTask = recurringTaskService.toggleRecurringTaskStatus(id);
            if (updatedTask != null) {
                String status = updatedTask.getIsActive() ? "activated" : "deactivated";
                return Result.success(updatedTask, "Recurring task template " + status + " successfully!");
            } else {
                return Result.error("404", "Recurring task template not found!");
            }
        } catch (Exception e) {
            return Result.error("500", "Failed to toggle recurring task template status: " + e.getMessage());
        }
    }
    
    // Generate tasks from templates
    @PostMapping("/recurring/generate")
    public Result<List<Task>> generateTasksFromTemplates(@RequestParam String date) {
        try {
            LocalDate targetDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            List<Task> generatedTasks = recurringTaskService.generateTasksFromTemplates(targetDate);
            return Result.success(generatedTasks, "Tasks generated from recurring templates successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to generate tasks from recurring templates: " + e.getMessage());
        }
    }
    
    // Get task by ID
    @GetMapping("/{id}")
    public Result<Task> getTaskById(@PathVariable String id) {
        try {
            Optional<Task> task = taskService.getTaskById(id);
            if (task.isPresent()) {
                return Result.success(task.get(), "Task retrieved successfully!");
            } else {
                return Result.error("404", "Task not found!");
            }
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve task: " + e.getMessage());
        }
    }
    
    // Update task
    @PutMapping("/{id}")
    public Result<Task> updateTask(@PathVariable String id, @RequestBody Task task) {
        try {
            Optional<Task> existingTask = taskService.getTaskById(id);
            if (existingTask.isPresent()) {
                Task taskToUpdate = existingTask.get();
                
                // Update only provided fields, preserve existing ones
                if (task.getTitle() != null && !task.getTitle().isEmpty()) {
                    taskToUpdate.setTitle(task.getTitle());
                }
                if (task.getDescription() != null) {
                    taskToUpdate.setDescription(task.getDescription());
                }
                if (task.getAssignedTo() != null) {
                    taskToUpdate.setAssignedTo(task.getAssignedTo());
                }
                if (task.getAssignedToId() != null) {
                    taskToUpdate.setAssignedToId(task.getAssignedToId());
                }
                if (task.getPriority() != null && !task.getPriority().isEmpty()) {
                    taskToUpdate.setPriority(task.getPriority());
                }
                if (task.getStatus() != null && !task.getStatus().isEmpty()) {
                    taskToUpdate.setStatus(task.getStatus());
                }
                if (task.getDueDate() != null) {
                    taskToUpdate.setDueDate(task.getDueDate());
                }
                if (task.getApprovalReason() != null) {
                    taskToUpdate.setApprovalReason(task.getApprovalReason());
                }
                if (task.getRejectionReason() != null) {
                    taskToUpdate.setRejectionReason(task.getRejectionReason());
                }
                if (task.getPatientId() != null) {
                    taskToUpdate.setPatientId(task.getPatientId());
                }
                if (task.getOrganizationId() != null) {
                    taskToUpdate.setOrganizationId(task.getOrganizationId());
                }
                
                // Save to database
                Task updatedTask = taskService.updateTask(taskToUpdate);
                return Result.success(updatedTask, "Task updated successfully!");
            } else {
                return Result.error("404", "Task not found!");
            }
        } catch (Exception e) {
            return Result.error("500", "Failed to update task: " + e.getMessage());
        }
    }
    
    // Delete task
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteTask(@PathVariable String id) {
        try {
            boolean deleted = taskService.deleteTask(id);
            if (deleted) {
                return Result.success(true, "Task deleted successfully!");
            } else {
                return Result.error("404", "Task not found!");
            }
        } catch (Exception e) {
            return Result.error("500", "Failed to delete task: " + e.getMessage());
        }
    }
    
    // Get tasks by assigned worker
    @GetMapping("/worker/{workerId}")
    public Result<List<Task>> getTasksByWorker(@PathVariable String workerId) {
        try {
            List<Task> tasks = taskService.getTasksByAssignedWorker(workerId);
            return Result.success(tasks, "Worker tasks retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve worker tasks: " + e.getMessage());
        }
    }
    
    // Get tasks by assigned worker name
    @GetMapping("/worker-name/{workerName}")
    public Result<List<Task>> getTasksByWorkerName(@PathVariable String workerName) {
        try {
            List<Task> tasks = taskService.getTasksByAssignedWorkerName(workerName);
            return Result.success(tasks, "Worker tasks retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve worker tasks: " + e.getMessage());
        }
    }
    
    // Get tasks by status
    @GetMapping("/status/{status}")
    public Result<List<Task>> getTasksByStatus(@PathVariable String status) {
        try {
            List<Task> tasks = taskService.getTasksByStatus(status);
            return Result.success(tasks, "Tasks retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve tasks: " + e.getMessage());
        }
    }
    
    // Get tasks by due date
    @GetMapping("/due-date/{dueDate}")
    public Result<List<Task>> getTasksByDueDate(@PathVariable String dueDate) {
        try {
            LocalDate date = LocalDate.parse(dueDate, DateTimeFormatter.ISO_LOCAL_DATE);
            List<Task> tasks = taskService.getTasksByDueDate(date);
            return Result.success(tasks, "Tasks retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve tasks: " + e.getMessage());
        }
    }
    
    
    // Get tasks by priority
    @GetMapping("/priority/{priority}")
    public Result<List<Task>> getTasksByPriority(@PathVariable String priority) {
        try {
            List<Task> tasks = taskService.getTasksByPriority(priority);
            return Result.success(tasks, "Tasks retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve tasks: " + e.getMessage());
        }
    }
    
    // Get today's tasks
    @GetMapping("/today")
    public Result<List<Task>> getTodayTasks() {
        try {
            List<Task> tasks = taskService.getTodayTasks();
            return Result.success(tasks, "Today's tasks retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve today's tasks: " + e.getMessage());
        }
    }
    
    // Get today's tasks for a specific worker
    @GetMapping("/today/worker/{workerId}")
    public Result<List<Task>> getTodayTasksForWorker(@PathVariable String workerId) {
        try {
            List<Task> tasks = taskService.getTodayTasksForWorker(workerId);
            return Result.success(tasks, "Today's tasks for worker retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve today's tasks: " + e.getMessage());
        }
    }
    
    // Get pending approval tasks
    @GetMapping("/pending-approval")
    public Result<List<Task>> getPendingApprovalTasks() {
        try {
            List<Task> tasks = taskService.getPendingApprovalTasks();
            return Result.success(tasks, "Pending approval tasks retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve pending approval tasks: " + e.getMessage());
        }
    }
    
    // Get completed tasks
    @GetMapping("/completed")
    public Result<List<Task>> getCompletedTasks() {
        try {
            List<Task> tasks = taskService.getCompletedTasks();
            return Result.success(tasks, "Completed tasks retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve completed tasks: " + e.getMessage());
        }
    }
    
    // Get in-progress tasks
    @GetMapping("/in-progress")
    public Result<List<Task>> getInProgressTasks() {
        try {
            List<Task> tasks = taskService.getInProgressTasks();
            return Result.success(tasks, "In-progress tasks retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve in-progress tasks: " + e.getMessage());
        }
    }
    
    // Get rejected tasks
    @GetMapping("/rejected")
    public Result<List<Task>> getRejectedTasks() {
        try {
            List<Task> tasks = taskService.getRejectedTasks();
            return Result.success(tasks, "Rejected tasks retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve rejected tasks: " + e.getMessage());
        }
    }
    
    // Worker marks task as completed
    @PostMapping("/{id}/worker-complete")
    public Result<Task> workerCompleteTask(@PathVariable String id) {
        try {
            Task task = taskService.workerCompleteTask(id);
            if (task != null) {
                return Result.success(task, "Task marked as completed by worker!");
            } else {
                return Result.error("404", "Task not found!");
            }
        } catch (Exception e) {
            return Result.error("500", "Failed to complete task: " + e.getMessage());
        }
    }
    
    // POA/FM/Manager approves task completion
    @PostMapping("/{id}/approve")
    public Result<Task> approveTaskCompletion(@PathVariable String id, @RequestBody Map<String, String> body) {
        try {
            String approvalReason = body.get("approvalReason");
            Task task = taskService.approveTaskCompletion(id, approvalReason);
            if (task != null) {
                return Result.success(task, "Task completion approved!");
            } else {
                return Result.error("404", "Task not found!");
            }
        } catch (Exception e) {
            return Result.error("500", "Failed to approve task: " + e.getMessage());
        }
    }
    
    // POA/FM/Manager rejects task completion
    @PostMapping("/{id}/reject")
    public Result<Task> rejectTaskCompletion(@PathVariable String id, @RequestBody Map<String, String> body) {
        try {
            String rejectionReason = body.get("rejectionReason");
            Task task = taskService.rejectTaskCompletion(id, rejectionReason);
            if (task != null) {
                return Result.success(task, "Task completion rejected!");
            } else {
                return Result.error("404", "Task not found!");
            }
        } catch (Exception e) {
            return Result.error("500", "Failed to reject task: " + e.getMessage());
        }
    }
    
    // Update task status
    @PutMapping("/{id}/status")
    public Result<Task> updateTaskStatus(@PathVariable String id, @RequestBody Map<String, String> body) {
        try {
            String status = body.get("status");
            Task task = taskService.updateTaskStatus(id, status);
            if (task != null) {
                return Result.success(task, "Task status updated successfully!");
            } else {
                return Result.error("404", "Task not found!");
            }
        } catch (Exception e) {
            return Result.error("500", "Failed to update task status: " + e.getMessage());
        }
    }
    
    // Get task statistics
    @GetMapping("/stats")
    public Result<Map<String, Object>> getTaskStats() {
        try {
            Map<String, Object> stats = Map.of(
                "total", taskService.getAllTasks().size(),
                "completed", taskService.getTaskCountByStatus("Completed"),
                "inProgress", taskService.getTaskCountByStatus("In Progress"),
                "workerCompleted", taskService.getTaskCountByStatus("Worker Completed"),
                "rejected", taskService.getTaskCountByStatus("Rejected"),
                "today", taskService.getTaskCountByDueDate(LocalDate.now())
            );
            return Result.success(stats, "Task statistics retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve task statistics: " + e.getMessage());
        }
    }
    
    // Get task statistics for a specific worker
    @GetMapping("/stats/worker/{workerId}")
    public Result<Map<String, Object>> getTaskStatsForWorker(@PathVariable String workerId) {
        try {
            Map<String, Object> stats = Map.of(
                "total", taskService.getTaskCountByAssignedWorker(workerId),
                "completed", taskService.getTaskCountByAssignedWorkerAndStatus(workerId, "Completed"),
                "inProgress", taskService.getTaskCountByAssignedWorkerAndStatus(workerId, "In Progress"),
                "workerCompleted", taskService.getTaskCountByAssignedWorkerAndStatus(workerId, "Worker Completed"),
                "rejected", taskService.getTaskCountByAssignedWorkerAndStatus(workerId, "Rejected")
            );
            return Result.success(stats, "Worker task statistics retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve worker task statistics: " + e.getMessage());
        }
    }
    
    // Name-to-ID hard mapping removed intentionally to avoid ambiguity.
    
    // ========== Additional API endpoints for frontend compatibility ==========
    
    /**
     * Get tasks by patient ID
     * GET /api/tasks/patient/{patientId}
     */
    @GetMapping("/patient/{patientId}")
    public Result<List<Task>> getTasksByPatient(@PathVariable String patientId) {
        try {
            List<Task> tasks = taskService.getTasksByPatient(patientId);
            return Result.success(tasks, "Patient tasks retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve patient tasks: " + e.getMessage());
        }
    }
    
    /**
     * Assign task to worker
     * POST /api/tasks/{id}/assign
     */
    @PostMapping("/{id}/assign")
    public Result<Task> assignTask(@PathVariable String id, @RequestBody Map<String, String> body) {
        try {
            String workerId = body.get("workerId");
            if (workerId == null || workerId.isEmpty()) {
                return Result.error("400", "Worker ID is required!");
            }
            
            Optional<Task> taskOpt = taskService.getTaskById(id);
            if (taskOpt.isPresent()) {
                Task task = taskOpt.get();
                task.setAssignedToId(workerId);
                task.setStatus("In Progress");
                
                Task updatedTask = taskService.updateTask(task);
                return Result.success(updatedTask, "Task assigned successfully!");
            } else {
                return Result.error("404", "Task not found!");
            }
        } catch (Exception e) {
            return Result.error("500", "Failed to assign task: " + e.getMessage());
        }
    }
    
    /**
     * Complete task
     * POST /api/tasks/{id}/complete
     */
    @PostMapping("/{id}/complete")
    public Result<Task> completeTask(@PathVariable String id, @RequestBody Map<String, String> body) {
        try {
            String completionNotes = body.get("completionNotes");
            
            Optional<Task> taskOpt = taskService.getTaskById(id);
            if (taskOpt.isPresent()) {
                Task task = taskOpt.get();
                task.setStatus("Worker Completed");
                if (completionNotes != null) {
                    task.setDescription(task.getDescription() + "\n\nCompletion Notes: " + completionNotes);
                }
                
                Task updatedTask = taskService.updateTask(task);
                return Result.success(updatedTask, "Task completed successfully!");
            } else {
                return Result.error("404", "Task not found!");
            }
        } catch (Exception e) {
            return Result.error("500", "Failed to complete task: " + e.getMessage());
        }
    }
    
    /**
     * Get tasks by patient ID (alternative endpoint)
     * GET /api/tasks/patient/{patientId}/all
     */
    @GetMapping("/patient/{patientId}/all")
    public Result<List<Task>> getAllTasksByPatient(@PathVariable String patientId) {
        try {
            List<Task> tasks = taskService.getTasksByPatient(patientId);
            return Result.success(tasks, "All patient tasks retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve all patient tasks: " + e.getMessage());
        }
    }
    
    // ==================== Task Request Endpoints ====================
    
    // Create a new task request (POA submits request)
    @PostMapping("/requests")
    public Result<TaskRequest> createTaskRequest(
            @RequestBody TaskRequest request,
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestHeader(value = "X-Organization-Id", required = false) String organizationId,
            @RequestHeader(value = "X-Patient-Id", required = false) String patientId) {
        try {
            // Set user information from headers
            if (userId != null) {
                request.setRequesterId(userId);
            }
            if (organizationId != null) {
                request.setOrganizationId(organizationId);
            }
            if (patientId != null) {
                request.setPatientId(patientId);
            }
            
            TaskRequest createdRequest = taskRequestService.createTaskRequest(request);
            return Result.success(createdRequest, "Task request submitted successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to submit task request: " + e.getMessage());
        }
    }
    
    // Get pending task requests for an organization (Manager view)
    @GetMapping("/requests/pending")
    public Result<List<TaskRequest>> getPendingRequests(
            @RequestHeader(value = "X-Organization-Id", required = false) String organizationId) {
        try {
            if (organizationId == null || organizationId.isEmpty()) {
                return Result.error("400", "Organization ID is required!");
            }
            List<TaskRequest> requests = taskRequestService.getPendingRequestsByOrganization(organizationId);
            return Result.success(requests, "Pending requests retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve pending requests: " + e.getMessage());
        }
    }
    
    // Get all task requests by requester (POA view)
    @GetMapping("/requests/my-requests")
    public Result<List<TaskRequest>> getMyRequests(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        try {
            if (userId == null || userId.isEmpty()) {
                return Result.error("400", "User ID is required!");
            }
            List<TaskRequest> requests = taskRequestService.getRequestsByRequester(userId);
            return Result.success(requests, "My requests retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve my requests: " + e.getMessage());
        }
    }
    
    // Get all task requests by organization (Manager view all)
    @GetMapping("/requests")
    public Result<List<TaskRequest>> getAllRequests(
            @RequestHeader(value = "X-Organization-Id", required = false) String organizationId) {
        try {
            if (organizationId == null || organizationId.isEmpty()) {
                return Result.error("400", "Organization ID is required!");
            }
            List<TaskRequest> requests = taskRequestService.getAllRequestsByOrganization(organizationId);
            return Result.success(requests, "All requests retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve requests: " + e.getMessage());
        }
    }
    
    // Approve a task request (Manager action)
    @PostMapping("/requests/{requestId}/approve")
    public Result<TaskRequest> approveRequest(
            @PathVariable String requestId,
            @RequestBody Map<String, String> body,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        try {
            if (userId == null || userId.isEmpty()) {
                return Result.error("400", "User ID is required!");
            }
            String approvalReason = body.get("approvalReason");
            TaskRequest request = taskRequestService.approveRequest(requestId, userId, approvalReason);
            if (request != null) {
                return Result.success(request, "Request approved successfully!");
            } else {
                return Result.error("404", "Request not found!");
            }
        } catch (Exception e) {
            return Result.error("500", "Failed to approve request: " + e.getMessage());
        }
    }
    
    // Reject a task request (Manager action)
    @PostMapping("/requests/{requestId}/reject")
    public Result<TaskRequest> rejectRequest(
            @PathVariable String requestId,
            @RequestBody Map<String, String> body,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        try {
            if (userId == null || userId.isEmpty()) {
                return Result.error("400", "User ID is required!");
            }
            String rejectionReason = body.get("rejectionReason");
            TaskRequest request = taskRequestService.rejectRequest(requestId, userId, rejectionReason);
            if (request != null) {
                return Result.success(request, "Request rejected successfully!");
            } else {
                return Result.error("404", "Request not found!");
            }
        } catch (Exception e) {
            return Result.error("500", "Failed to reject request: " + e.getMessage());
        }
    }
    
    // Get task request by ID
    @GetMapping("/requests/{requestId}")
    public Result<TaskRequest> getTaskRequest(@PathVariable String requestId) {
        try {
            Optional<TaskRequest> request = taskRequestService.getTaskRequestById(requestId);
            if (request.isPresent()) {
                return Result.success(request.get(), "Request retrieved successfully!");
            } else {
                return Result.error("404", "Request not found!");
            }
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve request: " + e.getMessage());
        }
    }
}
