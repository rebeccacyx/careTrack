package com.careapp.controller;

import com.careapp.domain.Schedule;
import com.careapp.dto.DailyScheduleRequest;
import com.careapp.service.ScheduleService;
import com.careapp.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/schedules")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    /**
     * Create a new schedule
     * @param schedule The schedule object to create
     * @return The created schedule
     */
    @PostMapping
    public Result<Schedule> createSchedule(@RequestBody Schedule schedule) {
        try {
            Schedule createdSchedule = scheduleService.createSchedule(schedule);
            return Result.success(createdSchedule, "Schedule created successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to create schedule: " + e.getMessage());
        }
    }

    /**
     * Get all schedules
     * @return A list of all schedules
     */
    @GetMapping
    public Result<List<Schedule>> getAllSchedules() {
        try {
            List<Schedule> schedules = scheduleService.getAllSchedules();
            return Result.success(schedules, "Schedules retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve schedules: " + e.getMessage());
        }
    }

    /**
     * Get schedule by ID
     * @param id The ID of the schedule
     * @return The schedule if found, otherwise 404
     */
    @GetMapping("/{id}")
    public Result<Schedule> getScheduleById(@PathVariable String id) {
        try {
            Optional<Schedule> schedule = scheduleService.getScheduleById(id);
            if (schedule.isPresent()) {
                return Result.success(schedule.get(), "Schedule retrieved successfully!");
            } else {
                return Result.error("404", "Schedule not found!");
            }
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve schedule: " + e.getMessage());
        }
    }

    /**
     * Update an existing schedule
     * @param id The ID of the schedule to update
     * @param scheduleDetails The schedule object with updated details
     * @return The updated schedule, or 404 if not found
     */
    @PutMapping("/{id}")
    public Result<Schedule> updateSchedule(@PathVariable String id, @RequestBody Schedule scheduleDetails) {
        try {
            Schedule updatedSchedule = scheduleService.updateSchedule(id, scheduleDetails);
            if (updatedSchedule != null) {
                return Result.success(updatedSchedule, "Schedule updated successfully!");
            } else {
                return Result.error("404", "Schedule not found!");
            }
        } catch (Exception e) {
            return Result.error("500", "Failed to update schedule: " + e.getMessage());
        }
    }

    /**
     * Delete a schedule by ID
     * @param id The ID of the schedule to delete
     * @return True if deleted, false otherwise
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteSchedule(@PathVariable String id) {
        try {
            boolean deleted = scheduleService.deleteSchedule(id);
            if (deleted) {
                return Result.success(true, "Schedule deleted successfully!");
            } else {
                return Result.error("404", "Schedule not found!");
            }
        } catch (Exception e) {
            return Result.error("500", "Failed to delete schedule: " + e.getMessage());
        }
    }

    /**
     * Get schedules by worker ID
     * @param workerId The worker ID
     * @return A list of schedules for the worker
     */
    @GetMapping("/worker/{workerId}")
    public Result<List<Schedule>> getSchedulesByWorkerId(@PathVariable String workerId) {
        try {
            List<Schedule> schedules = scheduleService.getSchedulesByWorkerId(workerId);
            return Result.success(schedules, "Worker schedules retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve worker schedules: " + e.getMessage());
        }
    }

    /**
     * Get schedules by date
     * @param date The schedule date (YYYY-MM-DD format)
     * @return A list of schedules for the date
     */
    @GetMapping("/date/{date}")
    public Result<List<Schedule>> getSchedulesByDate(@PathVariable String date) {
        try {
            LocalDate scheduleDate = LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
            List<Schedule> schedules = scheduleService.getSchedulesByDate(scheduleDate);
            return Result.success(schedules, "Schedules retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve schedules: " + e.getMessage());
        }
    }

    /**
     * Get schedules by worker ID and date
     * @param workerId The worker ID
     * @param date The schedule date (YYYY-MM-DD format)
     * @return A list of schedules for the worker on the date
     */
    @GetMapping("/worker/{workerId}/date/{date}")
    public Result<List<Schedule>> getSchedulesByWorkerIdAndDate(@PathVariable String workerId, @PathVariable String date) {
        try {
            LocalDate scheduleDate = LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
            List<Schedule> schedules = scheduleService.getSchedulesByWorkerIdAndDate(workerId, scheduleDate);
            return Result.success(schedules, "Worker schedules retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve worker schedules: " + e.getMessage());
        }
    }

    /**
     * Get schedules by organization ID
     * @param organizationId The organization ID
     * @return A list of schedules for the organization
     */
    @GetMapping("/organization/{organizationId}")
    public Result<List<Schedule>> getSchedulesByOrganizationId(@PathVariable String organizationId) {
        try {
            List<Schedule> schedules = scheduleService.getSchedulesByOrganizationId(organizationId);
            return Result.success(schedules, "Organization schedules retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve organization schedules: " + e.getMessage());
        }
    }

    /**
     * Get schedules by organization ID and date
     * @param organizationId The organization ID
     * @param date The schedule date (YYYY-MM-DD format)
     * @return A list of schedules for the organization on the date
     */
    @GetMapping("/organization/{organizationId}/date/{date}")
    public Result<List<Schedule>> getSchedulesByOrganizationIdAndDate(@PathVariable String organizationId, @PathVariable String date) {
        try {
            LocalDate scheduleDate = LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
            List<Schedule> schedules = scheduleService.getSchedulesByOrganizationIdAndDate(organizationId, scheduleDate);
            return Result.success(schedules, "Organization schedules retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve organization schedules: " + e.getMessage());
        }
    }

    /**
     * Get schedules by manager ID
     * @param managerId The manager ID
     * @return A list of schedules created by the manager
     */
    @GetMapping("/manager/{managerId}")
    public Result<List<Schedule>> getSchedulesByManagerId(@PathVariable String managerId) {
        try {
            List<Schedule> schedules = scheduleService.getSchedulesByManagerId(managerId);
            return Result.success(schedules, "Manager schedules retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve manager schedules: " + e.getMessage());
        }
    }

    /**
     * Get schedules by status
     * @param status The schedule status
     * @return A list of schedules with the specified status
     */
    @GetMapping("/status/{status}")
    public Result<List<Schedule>> getSchedulesByStatus(@PathVariable String status) {
        try {
            List<Schedule> schedules = scheduleService.getSchedulesByStatus(status);
            return Result.success(schedules, "Schedules retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve schedules: " + e.getMessage());
        }
    }

    /**
     * Get schedules by shift type
     * @param shiftType The shift type
     * @return A list of schedules with the specified shift type
     */
    @GetMapping("/shift-type/{shiftType}")
    public Result<List<Schedule>> getSchedulesByShiftType(@PathVariable String shiftType) {
        try {
            List<Schedule> schedules = scheduleService.getSchedulesByShiftType(shiftType);
            return Result.success(schedules, "Schedules retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve schedules: " + e.getMessage());
        }
    }

    /**
     * Get schedules by date range
     * @param startDate The start date (YYYY-MM-DD format)
     * @param endDate The end date (YYYY-MM-DD format)
     * @return A list of schedules within the date range
     */
    @GetMapping("/date-range")
    public Result<List<Schedule>> getSchedulesByDateRange(@RequestParam String startDate, @RequestParam String endDate) {
        try {
            LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
            LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);
            List<Schedule> schedules = scheduleService.getSchedulesByDateRange(start, end);
            return Result.success(schedules, "Schedules retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve schedules: " + e.getMessage());
        }
    }

    /**
     * Upload worker photo FILE for a schedule (actual file upload - Swagger will show Choose File button)
     * POST /api/schedules/{id}/upload-photo-file
     * @param id The schedule ID
     * @param file Photo file
     * @return The updated schedule, or 404 if not found
     */
    @PostMapping(value = "/{id}/upload-photo-file", consumes = "multipart/form-data")
    public Result<Schedule> uploadWorkerPhotoFile(
            @PathVariable String id, 
            @RequestPart("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return Result.error("400", "Please select a file to upload!");
            }
            
            // Validate file type
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return Result.error("400", "Only image files are allowed!");
            }
            
            // Create upload directory
            String uploadDir = "uploads/schedule-photos/";
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                boolean created = directory.mkdirs();
                if (!created) {
                    return Result.error("500", "Failed to create upload directory: " + directory.getAbsolutePath());
                }
                System.out.println("📁 Created upload directory: " + directory.getAbsolutePath());
            }
            
            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String newFilename = "schedule_" + id + "_" + UUID.randomUUID().toString() + fileExtension;
            
            // Save file
            Path filePath = Paths.get(uploadDir + newFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            // 验证文件是否保存成功
            File savedFile = filePath.toFile();
            if (!savedFile.exists() || !savedFile.isFile()) {
                return Result.error("500", "File was not saved correctly. Path: " + filePath.toAbsolutePath());
            }
            System.out.println("✅ Schedule photo saved successfully: " + savedFile.getAbsolutePath() + " (Size: " + savedFile.length() + " bytes)");
            
            // 生成访问URL
            String photoUrl = "/uploads/schedule-photos/" + newFilename;
            
            // Update database
            Schedule updatedSchedule = scheduleService.uploadWorkerPhoto(id, photoUrl);
            if (updatedSchedule != null) {
                return Result.success(updatedSchedule, "Worker photo uploaded successfully! URL: " + photoUrl);
            } else {
                return Result.error("404", "Schedule not found!");
            }
        } catch (IOException e) {
            return Result.error("500", "Failed to save file: " + e.getMessage());
        } catch (Exception e) {
            return Result.error("500", "Failed to upload worker photo: " + e.getMessage());
        }
    }
    
    /**
     * Upload worker photo URL for a schedule (upload via URL - receives JSON)
     * POST /api/schedules/{id}/upload-photo
     * @param id The schedule ID
     * @param body A map containing the photo URL
     * @return The updated schedule, or 404 if not found
     */
    @PostMapping("/{id}/upload-photo")
    public Result<Schedule> uploadWorkerPhoto(@PathVariable String id, @RequestBody Map<String, String> body) {
        try {
            String photoUrl = body.get("photoUrl");
            Schedule updatedSchedule = scheduleService.uploadWorkerPhoto(id, photoUrl);
            if (updatedSchedule != null) {
                return Result.success(updatedSchedule, "Worker photo URL updated successfully!");
            } else {
                return Result.error("404", "Schedule not found!");
            }
        } catch (Exception e) {
            return Result.error("500", "Failed to upload worker photo: " + e.getMessage());
        }
    }

    /**
     * Update schedule status
     * @param id The schedule ID
     * @param body A map containing the new status
     * @return The updated schedule, or 404 if not found
     */
    @PutMapping("/{id}/status")
    public Result<Schedule> updateScheduleStatus(@PathVariable String id, @RequestBody Map<String, String> body) {
        try {
            String status = body.get("status");
            Schedule updatedSchedule = scheduleService.updateScheduleStatus(id, status);
            if (updatedSchedule != null) {
                return Result.success(updatedSchedule, "Schedule status updated successfully!");
            } else {
                return Result.error("404", "Schedule not found!");
            }
        } catch (Exception e) {
            return Result.error("500", "Failed to update schedule status: " + e.getMessage());
        }
    }

    /**
     * Check if worker has schedule on specific date
     * @param workerId The worker ID
     * @param date The schedule date (YYYY-MM-DD format)
     * @return True if worker has schedule on the date, false otherwise
     */
    @GetMapping("/worker/{workerId}/has-schedule/{date}")
    public Result<Boolean> hasScheduleOnDate(@PathVariable String workerId, @PathVariable String date) {
        try {
            LocalDate scheduleDate = LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
            boolean hasSchedule = scheduleService.hasScheduleOnDate(workerId, scheduleDate);
            return Result.success(hasSchedule, "Schedule check completed successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to check schedule: " + e.getMessage());
        }
    }

    /**
     * Get schedule statistics
     * @param organizationId The organization ID
     * @param date The date to get statistics for (YYYY-MM-DD format)
     * @return A map containing schedule statistics
     */
    @GetMapping("/stats/{organizationId}")
    public Result<Map<String, Long>> getScheduleStatistics(@PathVariable String organizationId, @RequestParam(required = false) String date) {
        try {
            LocalDate scheduleDate = (date != null && !date.isEmpty()) ? 
                LocalDate.parse(date, DateTimeFormatter.ISO_DATE) : LocalDate.now();
            Map<String, Long> stats = scheduleService.getScheduleStatistics(organizationId, scheduleDate);
            return Result.success(stats, "Schedule statistics retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve schedule statistics: " + e.getMessage());
        }
    }
    
    /**
     * Batch create daily schedules for multiple workers
     * POST /api/schedules/batch-create
     * @param request The daily schedule request
     * @param organizationId Organization ID from header
     * @param managerId Manager ID from header
     * @return A list of created schedules
     */
    @PostMapping("/batch-create")
    public Result<List<Schedule>> batchCreateDailySchedules(
            @RequestBody DailyScheduleRequest request,
            @RequestHeader(value = "X-Organization-Id", required = false) String organizationId,
            @RequestHeader(value = "X-User-Id", required = false) String managerId) {
        try {
            // Use default values if headers not provided
            String orgId = organizationId != null ? organizationId : "org-001";
            String mgrId = managerId != null ? managerId : "manager-001";
            
            List<Schedule> createdSchedules = scheduleService.batchCreateDailySchedules(request, orgId, mgrId);
            return Result.success(createdSchedules, "Schedules created successfully for " + createdSchedules.size() + " workers!");
        } catch (Exception e) {
            return Result.error("500", "Failed to batch create schedules: " + e.getMessage());
        }
    }
    
    /**
     * Batch update schedule statuses
     * PUT /api/schedules/batch-update-status
     * @param body Map containing scheduleIds (List<String>) and status (String)
     * @return A list of updated schedules
     */
    @PutMapping("/batch-update-status")
    public Result<List<Schedule>> batchUpdateScheduleStatus(@RequestBody Map<String, Object> body) {
        try {
            @SuppressWarnings("unchecked")
            List<String> scheduleIds = (List<String>) body.get("scheduleIds");
            String status = (String) body.get("status");
            
            if (scheduleIds == null || scheduleIds.isEmpty()) {
                return Result.error("400", "scheduleIds are required!");
            }
            if (status == null || status.isEmpty()) {
                return Result.error("400", "status is required!");
            }
            
            List<Schedule> updatedSchedules = scheduleService.batchUpdateScheduleStatus(scheduleIds, status);
            return Result.success(updatedSchedules, "Successfully updated " + updatedSchedules.size() + " schedules!");
        } catch (Exception e) {
            return Result.error("500", "Failed to batch update schedules: " + e.getMessage());
        }
    }
    
    /**
     * Batch delete schedules by IDs
     * DELETE /api/schedules/batch-delete
     * @param body Map containing scheduleIds (List<String>)
     * @return Number of deleted schedules
     */
    @DeleteMapping("/batch-delete")
    public Result<Integer> batchDeleteSchedules(@RequestBody Map<String, Object> body) {
        try {
            @SuppressWarnings("unchecked")
            List<String> scheduleIds = (List<String>) body.get("scheduleIds");
            
            if (scheduleIds == null || scheduleIds.isEmpty()) {
                return Result.error("400", "scheduleIds are required!");
            }
            
            int deletedCount = scheduleService.batchDeleteSchedules(scheduleIds);
            return Result.success(deletedCount, "Successfully deleted " + deletedCount + " schedules!");
        } catch (Exception e) {
            return Result.error("500", "Failed to batch delete schedules: " + e.getMessage());
        }
    }
    
    /**
     * Delete all schedules for a specific date
     * DELETE /api/schedules/date/{date}
     * @param date The schedule date (YYYY-MM-DD format)
     * @param organizationId Organization ID (optional)
     * @return Number of deleted schedules
     */
    @DeleteMapping("/date/{date}")
    public Result<Integer> deleteSchedulesByDate(
            @PathVariable String date,
            @RequestParam(required = false) String organizationId) {
        try {
            LocalDate scheduleDate = LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
            int deletedCount = scheduleService.deleteSchedulesByDate(scheduleDate, organizationId);
            return Result.success(deletedCount, "Successfully deleted " + deletedCount + " schedules for " + date);
        } catch (Exception e) {
            return Result.error("500", "Failed to delete schedules by date: " + e.getMessage());
        }
    }
    
    /**
     * Copy schedules from one date to another
     * POST /api/schedules/copy
     * @param body Map containing sourceDate, targetDate, organizationId, managerId
     * @return A list of newly created schedules
     */
    @PostMapping("/copy")
    public Result<List<Schedule>> copySchedules(
            @RequestBody Map<String, String> body,
            @RequestHeader(value = "X-Organization-Id", required = false) String headerOrgId,
            @RequestHeader(value = "X-User-Id", required = false) String headerMgrId) {
        try {
            String sourceDate = body.get("sourceDate");
            String targetDate = body.get("targetDate");
            String organizationId = body.getOrDefault("organizationId", headerOrgId != null ? headerOrgId : "org-001");
            String managerId = body.getOrDefault("managerId", headerMgrId != null ? headerMgrId : "manager-001");
            
            if (sourceDate == null || targetDate == null) {
                return Result.error("400", "sourceDate and targetDate are required!");
            }
            
            LocalDate source = LocalDate.parse(sourceDate, DateTimeFormatter.ISO_DATE);
            LocalDate target = LocalDate.parse(targetDate, DateTimeFormatter.ISO_DATE);
            
            List<Schedule> copiedSchedules = scheduleService.copySchedules(source, target, organizationId, managerId);
            return Result.success(copiedSchedules, "Successfully copied " + copiedSchedules.size() + " schedules from " + sourceDate + " to " + targetDate);
        } catch (Exception e) {
            return Result.error("500", "Failed to copy schedules: " + e.getMessage());
        }
    }
    
    /**
     * Get weekly schedule overview
     * GET /api/schedules/weekly
     * @param startDate The start date of the week (YYYY-MM-DD format)
     * @param organizationId Organization ID
     * @return A list of schedules for the week
     */
    @GetMapping("/weekly")
    public Result<List<Schedule>> getWeeklySchedule(
            @RequestParam String startDate,
            @RequestParam(required = false) String organizationId,
            @RequestHeader(value = "X-Organization-Id", required = false) String headerOrgId) {
        try {
            LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
            String orgId = organizationId != null ? organizationId : (headerOrgId != null ? headerOrgId : "org-001");
            
            List<Schedule> schedules = scheduleService.getWeeklySchedule(start, orgId);
            return Result.success(schedules, "Weekly schedule retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve weekly schedule: " + e.getMessage());
        }
    }
    
    /**
     * Validate if schedule can be created (check for conflicts)
     * GET /api/schedules/validate
     * @param workerId Worker ID
     * @param date Schedule date (YYYY-MM-DD format)
     * @param shiftType Shift type (morning/evening/full-day)
     * @return True if valid, false if conflict exists
     */
    @GetMapping("/validate")
    public Result<Boolean> validateSchedule(
            @RequestParam String workerId,
            @RequestParam String date,
            @RequestParam String shiftType) {
        try {
            LocalDate scheduleDate = LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
            boolean isValid = scheduleService.validateSchedule(workerId, scheduleDate, shiftType);
            
            if (isValid) {
                return Result.success(true, "No conflicts found. Schedule can be created.");
            } else {
                return Result.success(false, "Conflict found. Worker already has a schedule for this shift.");
            }
        } catch (Exception e) {
            return Result.error("500", "Failed to validate schedule: " + e.getMessage());
        }
    }
}
