package com.careapp.controller;

import com.careapp.domain.Worker;
import com.careapp.dto.DailyScheduleRequest;
import com.careapp.dto.WorkerPhotoUploadRequest;
import com.careapp.service.WorkerService;
import com.careapp.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/workers")
public class WorkerController {

    @Autowired
    private WorkerService workerService;

    // Create a new worker (for management to create worker records)
    @PostMapping
    public Result<Worker> createWorker(@RequestBody Worker worker) {
        try {
            Worker createdWorker = workerService.createWorker(worker);
            return Result.success(createdWorker, "Worker created successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to create worker: " + e.getMessage());
        }
    }

    // Get all workers
    @GetMapping
    public Result<List<Worker>> getAllWorkers() {
        try {
            List<Worker> workers = workerService.getAllWorkers();
            return Result.success(workers, "Workers retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve workers: " + e.getMessage());
        }
    }

    /**
     * Get workers by manager ID
     * GET /api/workers/manager/{managerId}
     * @param managerId Manager ID
     * @return List of workers managed by the manager
     */
    @GetMapping("/manager/{managerId}")
    public Result<List<Worker>> getWorkersByManagerId(@PathVariable String managerId) {
        try {
            List<Worker> workers = workerService.getWorkersByManagerId(managerId);
            return Result.success(workers, "Workers retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve workers: " + e.getMessage());
        }
    }

    // Get worker by ID
    @GetMapping("/{id}")
    public Result<Worker> getWorkerById(@PathVariable String id) {
        try {
            Optional<Worker> worker = workerService.getWorkerById(id);
            if (worker.isPresent()) {
                return Result.success(worker.get(), "Worker retrieved successfully!");
            } else {
                return Result.error("404", "Worker not found!");
            }
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve worker: " + e.getMessage());
        }
    }

    // Get workers by organization
    @GetMapping("/organization/{organizationId}")
    public Result<List<Worker>> getWorkersByOrganization(@PathVariable String organizationId) {
        try {
            List<Worker> workers = workerService.getWorkersByOrganization(organizationId);
            return Result.success(workers, "Workers retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve workers: " + e.getMessage());
        }
    }

    // Update worker
    @PutMapping("/{id}")
    public Result<Worker> updateWorker(@PathVariable String id, @RequestBody Worker workerDetails) {
        try {
            Worker updatedWorker = workerService.updateWorker(id, workerDetails);
            if (updatedWorker != null) {
                return Result.success(updatedWorker, "Worker updated successfully!");
            } else {
                return Result.error("404", "Worker not found!");
            }
        } catch (Exception e) {
            return Result.error("500", "Failed to update worker: " + e.getMessage());
        }
    }

    // Delete worker
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteWorker(@PathVariable String id) {
        try {
            boolean deleted = workerService.deleteWorker(id);
            if (deleted) {
                return Result.success(true, "Worker deleted successfully!");
            } else {
                return Result.error("404", "Worker not found!");
            }
        } catch (Exception e) {
            return Result.error("500", "Failed to delete worker: " + e.getMessage());
        }
    }


    // Activate worker
    @PostMapping("/{id}/activate")
    public Result<Worker> activateWorker(@PathVariable String id) {
        try {
            Worker worker = workerService.activateWorker(id);
            if (worker != null) {
                return Result.success(worker, "Worker activated successfully!");
            } else {
                return Result.error("404", "Worker not found!");
            }
        } catch (Exception e) {
            return Result.error("500", "Failed to activate worker: " + e.getMessage());
        }
    }

    // Deactivate worker
    @PostMapping("/{id}/deactivate")
    public Result<Worker> deactivateWorker(@PathVariable String id) {
        try {
            Worker worker = workerService.deactivateWorker(id);
            if (worker != null) {
                return Result.success(worker, "Worker deactivated successfully!");
            } else {
                return Result.error("404", "Worker not found!");
            }
        } catch (Exception e) {
            return Result.error("500", "Failed to deactivate worker: " + e.getMessage());
        }
    }


    // Note: Removed assign-patient and remove-patient endpoints
    // because Workers should access Patient data through Task assignments, not direct assignment

    // Allocate worker to a shift (management uploads staff ID for shift)
    @PostMapping("/{id}/allocate-shift")
    public Result<Worker> allocateShift(@PathVariable String id, @RequestBody Worker.ShiftAllocation shiftAllocation) {
        try {
            Worker worker = workerService.allocateShift(id, shiftAllocation);
            if (worker != null) {
                return Result.success(worker, "Shift allocated successfully!");
            } else {
                return Result.error("404", "Worker not found!");
            }
        } catch (Exception e) {
            return Result.error("500", "Failed to allocate shift: " + e.getMessage());
        }
    }

    // Update shift status
    @PutMapping("/{id}/shift-status")
    public Result<Worker> updateShiftStatus(@PathVariable String id, @RequestBody Map<String, String> body) {
        try {
            String shiftDate = body.get("shiftDate");
            String shiftTime = body.get("shiftTime");
            String newStatus = body.get("status");
            
            if (shiftDate == null || shiftTime == null || newStatus == null) {
                return Result.error("400", "Shift date, time, and status are required!");
            }
            
            Worker worker = workerService.updateShiftStatus(id, shiftDate, shiftTime, newStatus);
            if (worker != null) {
                return Result.success(worker, "Shift status updated successfully!");
            } else {
                return Result.error("404", "Worker or shift not found!");
            }
        } catch (Exception e) {
            return Result.error("500", "Failed to update shift status: " + e.getMessage());
        }
    }

    // Get worker's shifts for a specific date
    @GetMapping("/{id}/shifts/{date}")
    public Result<List<Worker.ShiftAllocation>> getWorkerShiftsForDate(@PathVariable String id, @PathVariable String date) {
        try {
            List<Worker.ShiftAllocation> shifts = workerService.getWorkerShiftsForDate(id, date);
            return Result.success(shifts, "Worker shifts retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve worker shifts: " + e.getMessage());
        }
    }

    // Get all workers with shifts for a specific date
    @GetMapping("/organization/{organizationId}/shifts/{date}")
    public Result<List<Worker>> getWorkersWithShiftsForDate(@PathVariable String organizationId, @PathVariable String date) {
        try {
            List<Worker> workers = workerService.getWorkersWithShiftsForDate(organizationId, date);
            return Result.success(workers, "Workers with shifts retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve workers with shifts: " + e.getMessage());
        }
    }

    // Remove shift allocation
    @DeleteMapping("/{id}/shifts/{date}/{time}")
    public Result<Worker> removeShiftAllocation(@PathVariable String id, @PathVariable String date, @PathVariable String time) {
        try {
            Worker worker = workerService.removeShiftAllocation(id, date, time);
            if (worker != null) {
                return Result.success(worker, "Shift allocation removed successfully!");
            } else {
                return Result.error("404", "Worker or shift not found!");
            }
        } catch (Exception e) {
            return Result.error("500", "Failed to remove shift allocation: " + e.getMessage());
        }
    }

    // ========== Daily Management API Endpoints ==========

    // Get available workers for scheduling
    @GetMapping("/organization/{organizationId}/available")
    public Result<List<Worker>> getAvailableWorkers(@PathVariable String organizationId) {
        try {
            List<Worker> workers = workerService.getAvailableWorkers(organizationId);
            return Result.success(workers, "Available workers retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve available workers: " + e.getMessage());
        }
    }

    // Create daily schedule
    @PostMapping("/daily-schedule")
    public Result<List<Worker>> createDailySchedule(@RequestBody DailyScheduleRequest scheduleRequest, 
                                                   @RequestHeader("X-Manager-Id") String managerId) {
        try {
            if (scheduleRequest.getScheduleDate() == null || scheduleRequest.getScheduleDate().isEmpty()) {
                return Result.error("400", "Schedule date is required!");
            }
            
            List<Worker> updatedWorkers = workerService.createDailySchedule(scheduleRequest, managerId);
            return Result.success(updatedWorkers, "Daily schedule created successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to create daily schedule: " + e.getMessage());
        }
    }

    // Get daily schedule for a specific date
    @GetMapping("/organization/{organizationId}/daily-schedule/{date}")
    public Result<List<Worker>> getDailySchedule(@PathVariable String organizationId, @PathVariable String date) {
        try {
            List<Worker> workers = workerService.getDailySchedule(organizationId, date);
            return Result.success(workers, "Daily schedule retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve daily schedule: " + e.getMessage());
        }
    }

    // Clear daily schedule for a specific date
    @DeleteMapping("/organization/{organizationId}/daily-schedule/{date}")
    public Result<String> clearDailySchedule(@PathVariable String organizationId, @PathVariable String date) {
        try {
            int updatedCount = workerService.clearDailySchedule(organizationId, date);
            return Result.success("Cleared " + updatedCount + " worker schedules", 
                                "Daily schedule cleared successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to clear daily schedule: " + e.getMessage());
        }
    }

    // Upload worker photo
    @PostMapping("/upload-photo")
    public Result<Worker> uploadWorkerPhoto(@RequestBody WorkerPhotoUploadRequest photoRequest) {
        try {
            if (photoRequest.getWorkerId() == null || photoRequest.getWorkerId().isEmpty()) {
                return Result.error("400", "Worker ID is required!");
            }
            if (photoRequest.getPhotoUrl() == null || photoRequest.getPhotoUrl().isEmpty()) {
                return Result.error("400", "Photo URL is required!");
            }
            
            Worker worker = workerService.uploadWorkerPhoto(photoRequest);
            if (worker != null) {
                return Result.success(worker, "Worker photo uploaded successfully!");
            } else {
                return Result.error("404", "Worker not found!");
            }
        } catch (Exception e) {
            return Result.error("500", "Failed to upload worker photo: " + e.getMessage());
        }
    }
    
    /**
     * Upload worker file (actual file upload - Swagger will show Choose File button)
     * POST /api/workers/{id}/photo-file
     * @param id Worker ID
     * @param file File (supports images, PDF, Excel, Word documents)
     * @return Updated worker
     */
    @PostMapping(value = "/{id}/photo-file", consumes = "multipart/form-data")
    public Result<Worker> uploadWorkerPhotoFile(
            @PathVariable String id, 
            @RequestPart("file") MultipartFile file) {
        try {
            Worker worker = workerService.saveWorkerFile(id, file, false);
            if (worker != null) {
                return Result.success(worker, "Worker file uploaded successfully!");
            } else {
                return Result.error("404", "Worker not found!");
            }
        } catch (IllegalArgumentException e) {
            return Result.error("400", e.getMessage());
        } catch (IOException e) {
            return Result.error("500", "Failed to save file: " + e.getMessage());
        } catch (Exception e) {
            return Result.error("500", "Failed to upload worker photo: " + e.getMessage());
        }
    }
    
    /**
     * Upload worker photo URL 
     * 
     * POST /api/workers/{id}/photo-url
     * @param id Worker ID
     * @param body Map containing photoUrl
     * @return Updated worker
     */
    @PostMapping("/{id}/photo-url")
    public Result<Worker> uploadWorkerPhotoUrl(@PathVariable String id, @RequestBody Map<String, String> body) {
        try {
            String photoUrl = body.get("photoUrl");
            
            if (photoUrl == null || photoUrl.isEmpty()) {
                return Result.error("400", "Photo URL is required!");
            }
            
            Worker worker = workerService.uploadWorkerPhotoSimple(id, photoUrl);
            if (worker != null) {
                return Result.success(worker, "Worker photo URL updated successfully!");
            } else {
                return Result.error("404", "Worker not found!");
            }
        } catch (Exception e) {
            return Result.error("500", "Failed to upload worker photo: " + e.getMessage());
        }
    }
    
    /**
     * Upload worker photo (compatible with old version - receives JSON)
     * POST /api/workers/{id}/photo
     * @param id Worker ID
     * @param body Map containing photoUrl
     * @return Updated worker
     */
    @PostMapping("/{id}/photo")
    public Result<Worker> uploadWorkerPhotoSimple(@PathVariable String id, @RequestBody Map<String, String> body) {
        try {
            String photoUrl = body.get("photoUrl");
            
            if (photoUrl == null || photoUrl.isEmpty()) {
                return Result.error("400", "Photo URL is required!");
            }
            
            Worker worker = workerService.uploadWorkerPhotoSimple(id, photoUrl);
            if (worker != null) {
                return Result.success(worker, "Worker photo uploaded successfully!");
            } else {
                return Result.error("404", "Worker not found!");
            }
        } catch (Exception e) {
            return Result.error("500", "Failed to upload worker photo: " + e.getMessage());
        }
    }

    /**
     * Upload worker photo (multipart form-data, part name 'photo')
     * POST /api/workers/{id}/photo
     * @param id Worker ID
     * @param photo Multipart file with form field name 'photo'
     * @return Updated worker
     */
    @PostMapping(value = "/{id}/photo", consumes = "multipart/form-data")
    public Result<Worker> uploadWorkerPhotoMultipart(
            @PathVariable String id,
            @RequestPart("photo") MultipartFile photo) {
        try {
            Worker worker = workerService.saveWorkerFile(id, photo, true);
            if (worker != null) {
                return Result.success(worker, "Worker photo uploaded successfully!");
            } else {
                return Result.error("404", "Worker not found!");
            }
        } catch (IllegalArgumentException e) {
            return Result.error("400", e.getMessage());
        } catch (IOException e) {
            return Result.error("500", "Failed to save file: " + e.getMessage());
        } catch (Exception e) {
            return Result.error("500", "Failed to upload worker photo: " + e.getMessage());
        }
    }
    
    /**
     * Batch upload worker photos
     * POST /api/workers/batch-upload-photos
     * @param body Map of worker ID to photo URL
     * @return List of updated workers
     */
    @PostMapping("/batch-upload-photos")
    public Result<List<Worker>> batchUploadWorkerPhotos(@RequestBody Map<String, String> body) {
        try {
            if (body == null || body.isEmpty()) {
                return Result.error("400", "Photo upload data is required!");
            }
            
            List<Worker> updatedWorkers = workerService.batchUploadWorkerPhotos(body);
            return Result.success(updatedWorkers, "Successfully uploaded photos for " + updatedWorkers.size() + " workers!");
        } catch (Exception e) {
            return Result.error("500", "Failed to batch upload worker photos: " + e.getMessage());
        }
    }
    
    /**
     * Delete worker photo
     * DELETE /api/workers/{id}/photo
     * @param id Worker ID
     * @return Updated worker
     */
    @DeleteMapping("/{id}/photo")
    public Result<Worker> deleteWorkerPhoto(@PathVariable String id) {
        try {
            Worker worker = workerService.deleteWorkerPhoto(id);
            if (worker != null) {
                return Result.success(worker, "Worker photo deleted successfully!");
            } else {
                return Result.error("404", "Worker not found!");
            }
        } catch (Exception e) {
            return Result.error("500", "Failed to delete worker photo: " + e.getMessage());
        }
    }
    
    /**
     * Get workers without photos
     * GET /api/workers/organization/{organizationId}/without-photos
     * @param organizationId Organization ID
     * @return List of workers without photos
     */
    @GetMapping("/organization/{organizationId}/without-photos")
    public Result<List<Worker>> getWorkersWithoutPhotos(@PathVariable String organizationId) {
        try {
            List<Worker> workers = workerService.getWorkersWithoutPhotos(organizationId);
            return Result.success(workers, "Retrieved " + workers.size() + " workers without photos!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve workers without photos: " + e.getMessage());
        }
    }

    /**
     * Bind worker to manager
     * POST /api/workers/{id}/bind-manager/{managerId}
     * @param id Worker ID
     * @param managerId Manager ID
     * @return Updated worker
     */
    @PostMapping("/{id}/bind-manager/{managerId}")
    public Result<Worker> bindWorkerToManager(@PathVariable String id, @PathVariable String managerId) {
        try {
            Worker worker = workerService.bindWorkerToManager(id, managerId);
            if (worker != null) {
                return Result.success(worker, "Worker bound to manager successfully!");
            } else {
                return Result.error("404", "Worker not found!");
            }
        } catch (Exception e) {
            return Result.error("500", "Failed to bind worker to manager: " + e.getMessage());
        }
    }

    /**
     * Unbind worker from manager
     * POST /api/workers/{id}/unbind-manager
     * @param id Worker ID
     * @return Updated worker
     */
    @PostMapping("/{id}/unbind-manager")
    public Result<Worker> unbindWorkerFromManager(@PathVariable String id) {
        try {
            Worker worker = workerService.unbindWorkerFromManager(id);
            if (worker != null) {
                return Result.success(worker, "Worker unbound from manager successfully!");
            } else {
                return Result.error("404", "Worker not found!");
            }
        } catch (Exception e) {
            return Result.error("500", "Failed to unbind worker from manager: " + e.getMessage());
        }
    }

}