package com.careapp.service;

import com.careapp.domain.RecurringTask;
import com.careapp.domain.Task;
import com.careapp.dto.CreateRecurringTaskRequest;
import com.careapp.repository.RecurringTaskRepository;
import com.careapp.repository.TaskRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RecurringTaskService {
    
    @Resource
    private RecurringTaskRepository recurringTaskRepository;
    
    @Resource
    private TaskRepository taskRepository;
    
    // Create a new recurring task template
    public RecurringTask createRecurringTask(CreateRecurringTaskRequest request) {
        RecurringTask recurringTask = new RecurringTask();
        recurringTask.setTitle(request.getTitle());
        recurringTask.setDescription(request.getDescription());
        recurringTask.setAssignedTo(request.getAssignedTo());
        recurringTask.setAssignedToId(request.getAssignedToId());
        recurringTask.setFrequency(request.getFrequency());
        recurringTask.setFrequencyNumber(request.getFrequencyNumber() != null ? request.getFrequencyNumber() : 1);
        recurringTask.setTimeOfDay(request.getTimeOfDay());
        recurringTask.setDayOfWeek(request.getDayOfWeek());
        recurringTask.setDayOfMonth(request.getDayOfMonth());
        recurringTask.setStartDate(request.getStartDate());
        recurringTask.setEndDate(request.getEndDate());
        recurringTask.setPatientId(request.getPatientId());
        recurringTask.setOrganizationId(request.getOrganizationId());
        recurringTask.setCreatedBy(request.getCreatedBy() != null ? request.getCreatedBy() : "manager-001");
        recurringTask.setUpdatedAt(LocalDateTime.now());
        
        return recurringTaskRepository.save(recurringTask);
    }
    
    // Get all recurring task templates
    public List<RecurringTask> getAllRecurringTasks() {
        return recurringTaskRepository.findAll();
    }
    
    // Get recurring task by ID
    public Optional<RecurringTask> getRecurringTaskById(String id) {
        return recurringTaskRepository.findById(id);
    }
    
    // Update recurring task template
    public RecurringTask updateRecurringTask(String id, CreateRecurringTaskRequest request) {
        Optional<RecurringTask> existingTask = recurringTaskRepository.findById(id);
        if (existingTask.isPresent()) {
            RecurringTask task = existingTask.get();
            task.setTitle(request.getTitle());
            task.setDescription(request.getDescription());
            task.setAssignedTo(request.getAssignedTo());
            task.setAssignedToId(request.getAssignedToId());
            task.setFrequency(request.getFrequency());
            task.setFrequencyNumber(request.getFrequencyNumber());
            task.setTimeOfDay(request.getTimeOfDay());
            task.setDayOfWeek(request.getDayOfWeek());
            task.setDayOfMonth(request.getDayOfMonth());
            task.setStartDate(request.getStartDate());
            task.setEndDate(request.getEndDate());
            task.setPatientId(request.getPatientId());
            task.setOrganizationId(request.getOrganizationId());
            task.setUpdatedAt(LocalDateTime.now());
            
            return recurringTaskRepository.save(task);
        }
        return null;
    }
    
    // Delete recurring task template
    public boolean deleteRecurringTask(String id) {
        if (recurringTaskRepository.existsById(id)) {
            recurringTaskRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    // Toggle recurring task status (active/inactive)
    public RecurringTask toggleRecurringTaskStatus(String id) {
        Optional<RecurringTask> existingTask = recurringTaskRepository.findById(id);
        if (existingTask.isPresent()) {
            RecurringTask task = existingTask.get();
            task.setIsActive(!task.getIsActive());
            task.setUpdatedAt(LocalDateTime.now());
            return recurringTaskRepository.save(task);
        }
        return null;
    }
    
    // Generate tasks from templates for a specific date
    public List<Task> generateTasksFromTemplates(LocalDate date) {
        List<RecurringTask> activeTemplates = recurringTaskRepository.findByIsActiveTrue();
        List<Task> generatedTasks = new ArrayList<>();
        
        for (RecurringTask template : activeTemplates) {
            if (shouldGenerateTask(template, date)) {
                Task task = createTaskFromTemplate(template, date);
                if (task != null) {
                    generatedTasks.add(task);
                }
            }
        }
        
        return generatedTasks;
    }
    
    // Check if a task should be generated for the given date
    private boolean shouldGenerateTask(RecurringTask template, LocalDate date) {
        // Check if date is within the template's date range
        if (template.getStartDate() != null && date.isBefore(template.getStartDate())) {
            return false;
        }
        if (template.getEndDate() != null && date.isAfter(template.getEndDate())) {
            return false;
        }
        
        // Check frequency
        switch (template.getFrequency().toLowerCase()) {
            case "daily":
                return true;
            case "weekly":
                if (template.getDayOfWeek() != null) {
                    String dayOfWeek = date.getDayOfWeek().name().toLowerCase();
                    return dayOfWeek.equals(template.getDayOfWeek().toLowerCase());
                }
                return true;
            case "monthly":
                if (template.getDayOfMonth() != null) {
                    return date.getDayOfMonth() == template.getDayOfMonth();
                }
                return true;
            default:
                return false;
        }
    }
    
    // Create a task from a recurring template
    private Task createTaskFromTemplate(RecurringTask template, LocalDate date) {
        // Enforce global uniqueness by (title + date) across all tasks, regardless of assignment.
        // If any task with the same title already exists for the given date, skip generation.
        boolean taskExists = taskRepository.findByDueDate(date).stream()
            .anyMatch(t -> t.getTitle().equals(template.getTitle()));

        if (taskExists) {
            return null; // A task with the same title already exists for this date
        }
        
        // Create new task
        Task task = new Task();
        task.setTitle(template.getTitle());
        task.setDescription(template.getDescription());
        task.setAssignedTo(template.getAssignedTo());
        task.setAssignedToId(template.getAssignedToId());
        task.setPriority("normal"); // Default priority for recurring tasks
        task.setStatus("In Progress");
        task.setDueDate(date);
        task.setCreatedBy(template.getCreatedBy());
        task.setPatientId(template.getPatientId());
        task.setOrganizationId(template.getOrganizationId());
        task.setIsRecurring(true);
        task.setRecurringTemplateId(template.getId());
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        
        return taskRepository.save(task);
    }
}
