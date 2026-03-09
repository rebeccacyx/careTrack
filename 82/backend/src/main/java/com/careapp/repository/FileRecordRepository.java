package com.careapp.repository;

import com.careapp.domain.FileRecord;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRecordRepository extends MongoRepository<FileRecord, String> {
    List<FileRecord> findAllByCategoryOrderByCreatedAtDesc(String category);
    List<FileRecord> findAllByUploadedByOrderByCreatedAtDesc(String uploadedBy);
}


