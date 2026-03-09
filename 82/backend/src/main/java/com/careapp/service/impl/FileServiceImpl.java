package com.careapp.service.impl;

import com.careapp.domain.FileRecord;
import com.careapp.repository.FileRecordRepository;
import com.careapp.service.FileService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

@Service
public class FileServiceImpl implements FileService {

    @Resource
    private FileRecordRepository fileRecordRepository;

    @Override
    public FileRecord save(FileRecord fileRecord) {
        return fileRecordRepository.save(fileRecord);
    }

    @Override
    public Optional<FileRecord> getById(String id) {
        return fileRecordRepository.findById(id);
    }

    @Override
    public List<FileRecord> listAll() {
        return fileRecordRepository.findAll();
    }

    @Override
    public List<FileRecord> listByCategory(String category) {
        return fileRecordRepository.findAllByCategoryOrderByCreatedAtDesc(category);
    }

    @Override
    public List<FileRecord> listByUploader(String uploadedBy) {
        return fileRecordRepository.findAllByUploadedByOrderByCreatedAtDesc(uploadedBy);
    }

    @Override
    public boolean deleteById(String id) {
        if (fileRecordRepository.existsById(id)) {
            fileRecordRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public FileRecord updateComment(String id, String comment) {
        Optional<FileRecord> optional = fileRecordRepository.findById(id);
        if (optional.isEmpty()) {
            return null;
        }
        FileRecord record = optional.get();
        record.setComment(comment);
        return fileRecordRepository.save(record);
    }
}


