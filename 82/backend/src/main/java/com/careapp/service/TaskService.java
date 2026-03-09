package com.careapp.service;

import com.careapp.domain.Task;
import com.careapp.repository.TaskRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    
    @Resource
    private TaskRepository taskRepository;
    
    // Create a new task
    public Task createTask(Task task) {
        if (task.getCreatedAt() == null) {
            task.setCreatedAt(LocalDateTime.now());
        }
        if (task.getUpdatedAt() == null) {
            task.setUpdatedAt(LocalDateTime.now());
        }
        if (task.getStatus() == null || task.getStatus().isEmpty()) {
            task.setStatus("In Progress");
        }
        return taskRepository.save(task);
    }
    
    // Get task by ID
    public Optional<Task> getTaskById(String id) {
        return taskRepository.findById(id);
    }
    
    // Get all tasks
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }
    
    // Get tasks by assigned worker
    public List<Task> getTasksByAssignedWorker(String assignedToId) {
        return taskRepository.findByAssignedToId(assignedToId);
    }
    
    // Get tasks by assigned worker name
    public List<Task> getTasksByAssignedWorkerName(String assignedTo) {
        return taskRepository.findByAssignedTo(assignedTo);
    }
    
    // Get tasks by status
    public List<Task> getTasksByStatus(String status) {
        return taskRepository.findByStatus(status);
    }
    
    // Get tasks by due date
    public List<Task> getTasksByDueDate(LocalDate dueDate) {
        return taskRepository.findByDueDate(dueDate);
    }
    
    
    // Get tasks by priority
    public List<Task> getTasksByPriority(String priority) {
        return taskRepository.findByPriority(priority);
    }
    
    // Get tasks by created by
    public List<Task> getTasksByCreatedBy(String createdBy) {
        return taskRepository.findByCreatedBy(createdBy);
    }
    
    // Get tasks by patient
    public List<Task> getTasksByPatient(String patientId) {
        return taskRepository.findByPatientId(patientId);
    }
    
    // Get tasks by organization
    public List<Task> getTasksByOrganization(String organizationId) {
        return taskRepository.findByOrganizationId(organizationId);
    }
    
    // Get tasks by assigned worker and status
    public List<Task> getTasksByAssignedWorkerAndStatus(String assignedToId, String status) {
        return taskRepository.findByAssignedToIdAndStatus(assignedToId, status);
    }
    
    // Get tasks by assigned worker and due date
    public List<Task> getTasksByAssignedWorkerAndDueDate(String assignedToId, LocalDate dueDate) {
        return taskRepository.findByAssignedToIdAndDueDate(assignedToId, dueDate);
    }
    
    // Get tasks by status and due date
    public List<Task> getTasksByStatusAndDueDate(String status, LocalDate dueDate) {
        return taskRepository.findByStatusAndDueDate(status, dueDate);
    }
    
    // Get tasks by multiple statuses
    public List<Task> getTasksByStatuses(List<String> statuses) {
        return taskRepository.findByStatusIn(statuses);
    }
    
    // Get tasks by assigned worker and multiple statuses
    public List<Task> getTasksByAssignedWorkerAndStatuses(String assignedToId, List<String> statuses) {
        return taskRepository.findByAssignedToIdAndStatusIn(assignedToId, statuses);
    }
    
    // Get tasks by due date and multiple statuses
    public List<Task> getTasksByDueDateAndStatuses(LocalDate dueDate, List<String> statuses) {
        return taskRepository.findByDueDateAndStatusIn(dueDate, statuses);
    }
    
    // Get tasks by priority and status
    public List<Task> getTasksByPriorityAndStatus(String priority, String status) {
        return taskRepository.findByPriorityAndStatus(priority, status);
    }
    
    // Update task
    public Task updateTask(Task task) {
        task.setUpdatedAt(LocalDateTime.now());
        return taskRepository.save(task);
    }
    
    // Update task status
    public Task updateTaskStatus(String taskId, String status) {
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        if (taskOpt.isPresent()) {
            Task task = taskOpt.get();
            task.setStatus(status);
            task.setUpdatedAt(LocalDateTime.now());
            return taskRepository.save(task);
        }
        return null;
    }
    
    // Worker marks task as completed
    public Task workerCompleteTask(String taskId) {
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        if (taskOpt.isPresent()) {
            Task task = taskOpt.get();
            task.markAsWorkerCompleted();
            return taskRepository.save(task);
        }
        return null;
    }
    
    // POA approves task completion
    public Task approveTaskCompletion(String taskId, String approvalReason) {
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        if (taskOpt.isPresent()) {
            Task task = taskOpt.get();
            task.approveCompletion(approvalReason);
            return taskRepository.save(task);
        }
        return null;
    }
    
    // POA rejects task completion
    public Task rejectTaskCompletion(String taskId, String rejectionReason) {
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        if (taskOpt.isPresent()) {
            Task task = taskOpt.get();
            task.rejectCompletion(rejectionReason);
            return taskRepository.save(task);
        }
        return null;
    }
    
    // Delete task
    public boolean deleteTask(String taskId) {
        if (taskRepository.existsById(taskId)) {
            taskRepository.deleteById(taskId);
            return true;
        }
        return false;
    }
    
    // Get task statistics
    public long getTaskCountByStatus(String status) {
        return taskRepository.countByStatus(status);
    }
    
    public long getTaskCountByAssignedWorker(String assignedToId) {
        return taskRepository.countByAssignedToId(assignedToId);
    }
    
    public long getTaskCountByAssignedWorkerAndStatus(String assignedToId, String status) {
        return taskRepository.countByAssignedToIdAndStatus(assignedToId, status);
    }
    
    public long getTaskCountByDueDate(LocalDate dueDate) {
        return taskRepository.countByDueDate(dueDate);
    }
    
    public long getTaskCountByDueDateAndStatus(LocalDate dueDate, String status) {
        return taskRepository.countByDueDateAndStatus(dueDate, status);
    }
    
    // Get today's tasks for a worker
    public List<Task> getTodayTasksForWorker(String assignedToId) {
        return getTasksByAssignedWorkerAndDueDate(assignedToId, LocalDate.now());
    }
    
    // Get today's tasks for all workers
    public List<Task> getTodayTasks() {
        return getTasksByDueDate(LocalDate.now());
    }
    
    // Get pending tasks (waiting for POA approval)
    public List<Task> getPendingApprovalTasks() {
        return getTasksByStatus("Worker Completed");
    }
    
    // Get completed tasks
    public List<Task> getCompletedTasks() {
        return getTasksByStatus("Completed");
    }
    
    // Get in-progress tasks
    public List<Task> getInProgressTasks() {
        return getTasksByStatus("In Progress");
    }
    
    // Get rejected tasks
    public List<Task> getRejectedTasks() {
        return getTasksByStatus("Rejected");
    }
}
