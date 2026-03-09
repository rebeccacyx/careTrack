package com.careapp.repository;

import com.careapp.domain.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {
    
    // Find notifications by recipient ID
    List<Notification> findByRecipientId(String recipientId);
    
    // Find unread notifications by recipient ID
    List<Notification> findByRecipientIdAndIsRead(String recipientId, Boolean isRead);
    
    // Find notifications by recipient ID and type
    List<Notification> findByRecipientIdAndType(String recipientId, String type);
    
    // Find notifications by recipient ID and category
    List<Notification> findByRecipientIdAndCategory(String recipientId, String category);
    
    // Find notifications by recipient ID and priority
    List<Notification> findByRecipientIdAndPriority(String recipientId, String priority);
    
    // Find notifications by organization ID
    List<Notification> findByOrganizationId(String organizationId);
    
    // Find notifications by sender ID
    List<Notification> findBySenderId(String senderId);
    
    // Find notifications by related entity
    List<Notification> findByRelatedEntityTypeAndRelatedEntityId(String relatedEntityType, String relatedEntityId);
    
    // Find notifications created after a specific time
    List<Notification> findByRecipientIdAndCreatedAtAfter(String recipientId, LocalDateTime createdAt);
    
    // Find notifications that expire before a specific time
    List<Notification> findByExpiresAtBefore(LocalDateTime expiresAt);
    
    // Count unread notifications by recipient
    long countByRecipientIdAndIsRead(String recipientId, Boolean isRead);
    
    // Count notifications by recipient and priority
    long countByRecipientIdAndPriority(String recipientId, String priority);
    
    // Delete notifications that expired before a specific time
    void deleteByExpiresAtBefore(LocalDateTime expiresAt);
    
    // Find notifications by recipient role
    List<Notification> findByRecipientRole(String recipientRole);
    
    // Find notifications by recipient ID ordered by created date (descending)
    List<Notification> findByRecipientIdOrderByCreatedAtDesc(String recipientId);
    
    // Find unread notifications by recipient ID ordered by priority and created date
    List<Notification> findByRecipientIdAndIsReadOrderByPriorityDescCreatedAtDesc(String recipientId, Boolean isRead);
}



