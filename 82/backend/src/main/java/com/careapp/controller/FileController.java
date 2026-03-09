package com.careapp.controller;

import com.careapp.domain.FileRecord;
import com.careapp.service.FileService;
import com.careapp.utils.Result;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Resource
    private FileService fileService;

    /**
     * Upload actual file (multipart/form-data)
     * POST /api/files/upload
     * @param file The file to upload
     * @param category Optional category (e.g., "Worker Upload", "Admin Upload")
     * @param uploadedBy Optional user ID who uploaded the file
     * @param comment Optional comment
     * @return FileRecord with file URL
     */
    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public Result<FileRecord> uploadFileMultipart(
            @RequestPart("file") MultipartFile file,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String uploadedBy,
            @RequestParam(required = false) String comment) {
        try {
            System.out.println("uploadedBy received: " + uploadedBy);
            if (file == null || file.isEmpty()) {
                return Result.error("400", "Please select a file to upload!");
            }

            // Create upload directory
            String uploadDir = "uploads/files/";
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
            String newFilename = UUID.randomUUID().toString() + fileExtension;

            // Save file
            Path filePath = Paths.get(uploadDir + newFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            // Verify file was saved
            File savedFile = filePath.toFile();
            if (!savedFile.exists() || !savedFile.isFile()) {
                return Result.error("500", "File was not saved correctly. Path: " + filePath.toAbsolutePath());
            }
            System.out.println("✅ File saved successfully: " + savedFile.getAbsolutePath() + " (Size: " + savedFile.length() + " bytes)");

            // Generate access URL
            String fileUrl = "/uploads/files/" + newFilename;

            // Create FileRecord
            FileRecord record = new FileRecord();
            record.setName(originalFilename != null ? originalFilename : "uploaded_file" + fileExtension);
            record.setCategory(category != null ? category : "General Upload");
            record.setUploadedBy(uploadedBy != null ? uploadedBy : "unknown");
            record.setFileUrl(fileUrl);
            record.setContentType(file.getContentType());
            record.setSize(file.getSize());
            record.setComment(comment != null ? comment : "");

            FileRecord saved = fileService.save(record);
            return Result.success(saved, "File uploaded successfully!");
        } catch (IOException e) {
            return Result.error("500", "Failed to save file: " + e.getMessage());
        } catch (Exception e) {
            return Result.error("500", "Failed to upload file: " + e.getMessage());
        }
    }

    // Upload metadata (front-end already generates preview URL locally; here we accept URL and metadata)
    @PostMapping
    public Result<FileRecord> uploadFile(@RequestBody Map<String, Object> body) {
        try {
            String name = (String) body.get("name");
            String category = (String) body.get("category");
            String uploadedBy = (String) body.get("uploadedBy");
            String fileUrl = (String) body.get("fileUrl");
            String contentType = (String) body.get("contentType");
            Long size = body.get("size") == null ? null : Long.valueOf(body.get("size").toString());
            String comment = (String) body.getOrDefault("comment", "");

            if (name == null || fileUrl == null) {
                return Result.error("400", "name and fileUrl are required");
            }

            FileRecord record = new FileRecord();
            record.setName(name);
            record.setCategory(category);
            record.setUploadedBy(uploadedBy);
            record.setFileUrl(fileUrl);
            record.setContentType(contentType);
            record.setSize(size);
            record.setComment(comment);

            FileRecord saved = fileService.save(record);
            return Result.success(saved, "File metadata saved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to save file metadata: " + e.getMessage());
        }
    }

    // List files (optional filters)
    @GetMapping
    public Result<List<FileRecord>> listFiles(@RequestParam(required = false) String category,
                                              @RequestParam(required = false) String uploadedBy) {
        try {
            if (category != null && !category.isEmpty()) {
                return Result.success(fileService.listByCategory(category), "Files retrieved successfully!");
            }
            if (uploadedBy != null && !uploadedBy.isEmpty()) {
                return Result.success(fileService.listByUploader(uploadedBy), "Files retrieved successfully!");
            }
            return Result.success(fileService.listAll(), "Files retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve files: " + e.getMessage());
        }
    }

    // Get by ID
    @GetMapping("/{id}")
    public Result<FileRecord> getFile(@PathVariable String id) {
        try {
            Optional<FileRecord> record = fileService.getById(id);
            if (record.isPresent()) {
                return Result.success(record.get(), "File retrieved successfully!");
            }
            return Result.error("404", "File not found!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve file: " + e.getMessage());
        }
    }

    // Update comment
    @PutMapping("/{id}/comment")
    public Result<FileRecord> updateComment(@PathVariable String id, @RequestBody Map<String, String> body) {
        try {
            String comment = body.get("comment");
            FileRecord updated = fileService.updateComment(id, comment == null ? "" : comment);
            if (updated == null) {
                return Result.error("404", "File not found!");
            }
            return Result.success(updated, "Comment updated successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to update comment: " + e.getMessage());
        }
    }

    // Delete file metadata
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable String id) {
        try {
            boolean ok = fileService.deleteById(id);
            if (ok) {
                return Result.success(true, "File deleted successfully!");
            }
            return Result.error("404", "File not found!");
        } catch (Exception e) {
            return Result.error("500", "Failed to delete file: " + e.getMessage());
        }
    }

    /**
     * Serve file by URL path (fallback for static resource serving)
     * GET /api/files/serve?path=/uploads/files/filename.pdf
     */
    @GetMapping("/serve")
    public ResponseEntity<org.springframework.core.io.Resource> serveFile(@RequestParam String path) {
        try {
            // Remove leading slash if present and sanitize path
            String cleanPath = path.startsWith("/") ? path.substring(1) : path;
            
            // Security check: ensure path is within uploads directory
            if (!cleanPath.startsWith("uploads/")) {
                return ResponseEntity.badRequest().build();
            }
            
            File file = new File(cleanPath);
            if (!file.exists() || !file.isFile()) {
                System.out.println("❌ File not found: " + file.getAbsolutePath());
                return ResponseEntity.notFound().build();
            }
            
            FileSystemResource resource = new FileSystemResource(file);
            
            // Determine content type
            String contentType = Files.probeContentType(file.toPath());
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getName() + "\"")
                    .body(resource);
                    
        } catch (Exception e) {
            System.err.println("❌ Error serving file: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}


