package com.careapp.repository;

import com.careapp.domain.Schedule;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ScheduleRepository extends MongoRepository<Schedule, String> {
    
    // Find schedules by worker ID
    List<Schedule> findByWorkerId(String workerId);
    
    // Find schedules by date
    List<Schedule> findByScheduleDate(LocalDate scheduleDate);
    
    // Find schedules by worker ID and date
    List<Schedule> findByWorkerIdAndScheduleDate(String workerId, LocalDate scheduleDate);
    
    // Find schedules by organization ID
    List<Schedule> findByOrganizationId(String organizationId);
    
    // Find schedules by organization ID and date
    List<Schedule> findByOrganizationIdAndScheduleDate(String organizationId, LocalDate scheduleDate);
    
    // Find schedules by manager ID
    List<Schedule> findByManagerId(String managerId);
    
    // Find schedules by status
    List<Schedule> findByStatus(String status);
    
    // Find schedules by shift type
    List<Schedule> findByShiftType(String shiftType);
    
    // Find schedules by date range
    List<Schedule> findByScheduleDateBetween(LocalDate startDate, LocalDate endDate);
    
    // Find schedules by worker ID and date range
    List<Schedule> findByWorkerIdAndScheduleDateBetween(String workerId, LocalDate startDate, LocalDate endDate);
    
    // Find schedules by organization ID and date range
    List<Schedule> findByOrganizationIdAndScheduleDateBetween(String organizationId, LocalDate startDate, LocalDate endDate);
    
    // Check if worker has schedule on specific date
    boolean existsByWorkerIdAndScheduleDate(String workerId, LocalDate scheduleDate);
    
    // Count schedules by worker ID
    long countByWorkerId(String workerId);
    
    // Count schedules by organization ID
    long countByOrganizationId(String organizationId);
    
    // Count schedules by status
    long countByStatus(String status);
    
    // Count schedules by date
    long countByScheduleDate(LocalDate scheduleDate);
}
