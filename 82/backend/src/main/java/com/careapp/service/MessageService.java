package com.careapp.service;

import com.careapp.domain.Message;
import com.careapp.domain.User;
import com.careapp.repository.MessageRepository;
import com.careapp.repository.UserRepository;
import com.careapp.service.impl.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MessageService {
    
    @Autowired
    private MessageRepository messageRepository;
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private EmailService emailService;
    
    /**
     * Send a new message
     * @param message The message to send
     * @return The created message
     */
    public Message sendMessage(Message message) {
        if (message.getCreatedAt() == null) {
            message.setCreatedAt(LocalDateTime.now());
        }
        if (message.getStatus() == null || message.getStatus().isEmpty()) {
            message.setStatus("sent");
        }
        if (message.getIsReply() == null) {
            message.setIsReply(false);
        }
        if (message.getReplyCount() == null) {
            message.setReplyCount(0);
        }
        
        Message savedMessage = messageRepository.save(message);
        
        // Create notification for recipient
        try {
            notificationService.createMessageReceivedNotification(
                message.getToUserId(),
                savedMessage.getId(),
                message.getFromUserName() != null ? message.getFromUserName() : "User",
                message.getSubject()
            );
        } catch (Exception e) {
            // Log error but don't fail the message sending
            System.err.println("Failed to create notification: " + e.getMessage());
        }
        
        // Send email notification to recipient
        try {
            // Get recipient user information
            Optional<User> recipientOpt = userRepository.findById(message.getToUserId());
            if (recipientOpt.isPresent()) {
                User recipient = recipientOpt.get();
                if (StringUtils.hasText(recipient.getEmail())) {
                    String subject = "📧 CareTrack - New Message: " + message.getSubject();
                    String senderName = message.getFromUserName() != null ? message.getFromUserName() : "User";
                    String emailContent = String.format(
                        "Hello %s,\n\n" +
                        "You have received a new message from %s on CareTrack.\n\n" +
                        "Subject: %s\n\n" +
                        "Message:\n%s\n\n" +
                        "Please log in to CareTrack to view and reply to this message.\n\n" +
                        "Best regards,\n" +
                        "CareTrack Team",
                        recipient.getFirstName() != null ? recipient.getFirstName() : recipient.getEmail(),
                        senderName,
                        message.getSubject(),
                        message.getContent()
                    );
                    
                    emailService.sendText(recipient.getEmail(), subject, emailContent);
                    System.out.println("✅ Message notification email sent to " + recipient.getEmail());
                }
            }
        } catch (IllegalStateException e) {
            // Email service not configured
            System.err.println("⚠️ Email service not configured: " + e.getMessage());
            System.err.println("💡 To enable email notifications, set SENDGRID_API_KEY environment variable");
        } catch (Exception e) {
            // Other email sending errors
            System.err.println("❌ Failed to send message notification email: " + e.getMessage());
            // Log error but don't fail message sending
            e.printStackTrace();
        }
        
        return savedMessage;
    }
    
    /**
     * Reply to a message
     * @param originalMessageId The ID of the message being replied to
     * @param replyMessage The reply message
     * @return The created reply message
     */
    public Message replyToMessage(String originalMessageId, Message replyMessage) {
        Optional<Message> originalOpt = messageRepository.findById(originalMessageId);
        if (originalOpt.isPresent()) {
            Message original = originalOpt.get();
            
            // Set reply properties
            replyMessage.setIsReply(true);
            replyMessage.setOriginalMessageId(originalMessageId);
            
            // If subject not set, use "Re: " prefix
            if (replyMessage.getSubject() == null || replyMessage.getSubject().isEmpty()) {
                replyMessage.setSubject("Re: " + original.getSubject());
            }
            
            // Increment reply count on original message
            original.setReplyCount(original.getReplyCount() + 1);
            messageRepository.save(original);
            
            return sendMessage(replyMessage);
        }
        return null;
    }
    
    /**
     * Get message by ID
     * @param id The message ID
     * @return Optional containing the message if found
     */
    public Optional<Message> getMessageById(String id) {
        return messageRepository.findById(id);
    }
    
    /**
     * Get all messages
     * @return List of all messages
     */
    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }
    
    /**
     * Get messages by recipient
     * @param toUserId The recipient user ID
     * @return List of messages for the recipient
     */
    public List<Message> getMessagesByRecipient(String toUserId) {
        return messageRepository.findByToUserIdOrderByCreatedAtDesc(toUserId);
    }
    
    /**
     * Get messages by sender
     * @param fromUserId The sender user ID
     * @return List of messages from the sender
     */
    public List<Message> getMessagesBySender(String fromUserId) {
        return messageRepository.findByFromUserIdOrderByCreatedAtDesc(fromUserId);
    }
    
    /**
     * Get all messages for a user (sent and received)
     * @param userId The user ID
     * @return List of all messages for the user
     */
    public List<Message> getMessagesByUser(String userId) {
        return messageRepository.findByFromUserIdOrToUserIdOrderByCreatedAtDesc(userId, userId);
    }
    
    /**
     * Get unread messages by recipient
     * @param toUserId The recipient user ID
     * @return List of unread messages
     */
    public List<Message> getUnreadMessages(String toUserId) {
        return messageRepository.findByToUserIdAndReadAtIsNull(toUserId);
    }
    
    /**
     * Get messages by recipient and status
     * @param toUserId The recipient user ID
     * @param status The message status
     * @return List of messages
     */
    public List<Message> getMessagesByRecipientAndStatus(String toUserId, String status) {
        return messageRepository.findByToUserIdAndStatusOrderByCreatedAtDesc(toUserId, status);
    }
    
    /**
     * Get messages by category
     * @param toUserId The recipient user ID
     * @param category The message category
     * @return List of messages
     */
    public List<Message> getMessagesByCategory(String toUserId, String category) {
        return messageRepository.findByToUserIdAndCategory(toUserId, category);
    }
    
    /**
     * Get conversation between two users
     * @param userId1 First user ID
     * @param userId2 Second user ID
     * @return List of messages in the conversation
     */
    public List<Message> getConversation(String userId1, String userId2) {
        return messageRepository.findByFromUserIdAndToUserIdOrToUserIdAndFromUserId(
            userId1, userId2, userId1, userId2);
    }
    
    /**
     * Get replies to a message
     * @param originalMessageId The original message ID
     * @return List of reply messages
     */
    public List<Message> getReplies(String originalMessageId) {
        return messageRepository.findByOriginalMessageId(originalMessageId);
    }
    
    /**
     * Mark message as read
     * @param messageId The message ID
     * @return The updated message, or null if not found
     */
    public Message markAsRead(String messageId) {
        Optional<Message> optionalMessage = messageRepository.findById(messageId);
        if (optionalMessage.isPresent()) {
            Message message = optionalMessage.get();
            if (message.getReadAt() == null) {
                message.setReadAt(LocalDateTime.now());
                message.setStatus("read");
                return messageRepository.save(message);
            }
            return message;
        }
        return null;
    }
    
    /**
     * Archive message
     * @param messageId The message ID
     * @return The updated message, or null if not found
     */
    public Message archiveMessage(String messageId) {
        Optional<Message> optionalMessage = messageRepository.findById(messageId);
        if (optionalMessage.isPresent()) {
            Message message = optionalMessage.get();
            message.setStatus("archived");
            return messageRepository.save(message);
        }
        return null;
    }
    
    /**
     * Delete message
     * @param messageId The message ID
     * @return True if deleted successfully, false otherwise
     */
    public boolean deleteMessage(String messageId) {
        Optional<Message> optionalMessage = messageRepository.findById(messageId);
        if (optionalMessage.isPresent()) {
            Message message = optionalMessage.get();
            message.setStatus("deleted");
            messageRepository.save(message);
            return true;
        }
        return false;
    }
    
    /**
     * Permanently delete message
     * @param messageId The message ID
     * @return True if deleted successfully, false otherwise
     */
    public boolean permanentlyDeleteMessage(String messageId) {
        if (messageRepository.existsById(messageId)) {
            messageRepository.deleteById(messageId);
            return true;
        }
        return false;
    }
    
    /**
     * Get unread message count
     * @param toUserId The recipient user ID
     * @return Count of unread messages
     */
    public long getUnreadCount(String toUserId) {
        return messageRepository.countByToUserIdAndReadAtIsNull(toUserId);
    }
    
    /**
     * Get sent message count
     * @param fromUserId The sender user ID
     * @return Count of sent messages
     */
    public long getSentCount(String fromUserId) {
        return messageRepository.countByFromUserId(fromUserId);
    }
    
    /**
     * Get received message count
     * @param toUserId The recipient user ID
     * @return Count of received messages
     */
    public long getReceivedCount(String toUserId) {
        return messageRepository.countByToUserId(toUserId);
    }
    
    /**
     * Get messages by organization
     * @param organizationId The organization ID
     * @return List of messages
     */
    public List<Message> getMessagesByOrganization(String organizationId) {
        return messageRepository.findByOrganizationId(organizationId);
    }
}



