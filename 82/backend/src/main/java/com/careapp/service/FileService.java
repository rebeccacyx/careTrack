package com.careapp.service;

import com.careapp.domain.FileRecord;

import java.util.List;
import java.util.Optional;

public interface FileService {
    FileRecord save(FileRecord fileRecord);
    Optional<FileRecord> getById(String id);
    List<FileRecord> listAll();
    List<FileRecord> listByCategory(String category);
    List<FileRecord> listByUploader(String uploadedBy);
    boolean deleteById(String id);
    FileRecord updateComment(String id, String comment);
}


