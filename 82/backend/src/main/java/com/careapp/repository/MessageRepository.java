package com.careapp.repository;

import com.careapp.domain.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageRepository extends MongoRepository<Message, String> {
    
    // Find messages by sender
    List<Message> findByFromUserId(String fromUserId);
    
    // Find messages by recipient
    List<Message> findByToUserId(String toUserId);
    
    // Find messages by recipient ordered by created date (descending)
    List<Message> findByToUserIdOrderByCreatedAtDesc(String toUserId);
    
    // Find messages by sender ordered by created date (descending)
    List<Message> findByFromUserIdOrderByCreatedAtDesc(String fromUserId);
    
    // Find messages by status
    List<Message> findByStatus(String status);
    
    // Find messages by recipient and status
    List<Message> findByToUserIdAndStatus(String toUserId, String status);
    
    // Find messages by recipient and status ordered by created date (descending)
    List<Message> findByToUserIdAndStatusOrderByCreatedAtDesc(String toUserId, String status);
    
    // Find messages by category
    List<Message> findByCategory(String category);
    
    // Find messages by recipient and category
    List<Message> findByToUserIdAndCategory(String toUserId, String category);
    
    // Find messages by organization
    List<Message> findByOrganizationId(String organizationId);
    
    // Find messages between two users
    List<Message> findByFromUserIdAndToUserIdOrToUserIdAndFromUserId(
        String fromUserId1, String toUserId1, String toUserId2, String fromUserId2);
    
    // Find replies to a message
    List<Message> findByOriginalMessageId(String originalMessageId);
    
    // Find messages created after a specific time
    List<Message> findByCreatedAtAfter(LocalDateTime createdAt);
    
    // Find unread messages by recipient
    List<Message> findByToUserIdAndReadAtIsNull(String toUserId);
    
    // Count unread messages by recipient
    long countByToUserIdAndReadAtIsNull(String toUserId);
    
    // Count messages by sender
    long countByFromUserId(String fromUserId);
    
    // Count messages by recipient
    long countByToUserId(String toUserId);
    
    // Find messages by sender or recipient (user's all messages)
    List<Message> findByFromUserIdOrToUserIdOrderByCreatedAtDesc(String fromUserId, String toUserId);
}



