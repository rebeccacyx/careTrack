package com.careapp.controller;

import com.careapp.domain.Message;
import com.careapp.service.MessageService;
import com.careapp.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/messages")
public class CommunicationController {

    @Autowired
    private MessageService messageService;

    /**
     * Get all messages for current user
     * GET /api/messages
     */
    @GetMapping
    public Result<List<Message>> getAllMessages(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        try {
            String currentUserId = userId != null ? userId : "default-user-001";
            List<Message> messages = messageService.getMessagesByUser(currentUserId);
            return Result.success(messages, "Messages retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve messages: " + e.getMessage());
        }
    }
    
    /**
     * Get messages by recipient
     * GET /api/messages/inbox
     */
    @GetMapping("/inbox")
    public Result<List<Message>> getInboxMessages(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        try {
            String currentUserId = userId != null ? userId : "default-user-001";
            List<Message> messages = messageService.getMessagesByRecipient(currentUserId);
            return Result.success(messages, "Inbox messages retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve inbox messages: " + e.getMessage());
        }
    }
    
    /**
     * Get sent messages
     * GET /api/messages/sent
     */
    @GetMapping("/sent")
    public Result<List<Message>> getSentMessages(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        try {
            String currentUserId = userId != null ? userId : "default-user-001";
            List<Message> messages = messageService.getMessagesBySender(currentUserId);
            return Result.success(messages, "Sent messages retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve sent messages: " + e.getMessage());
        }
    }
    
    /**
     * Get unread messages
     * GET /api/messages/unread
     */
    @GetMapping("/unread")
    public Result<List<Message>> getUnreadMessages(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        try {
            String currentUserId = userId != null ? userId : "default-user-001";
            List<Message> messages = messageService.getUnreadMessages(currentUserId);
            return Result.success(messages, "Unread messages retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve unread messages: " + e.getMessage());
        }
    }
    
    /**
     * Get unread message count
     * GET /api/messages/unread/count
     */
    @GetMapping("/unread/count")
    public Result<Long> getUnreadCount(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        try {
            String currentUserId = userId != null ? userId : "default-user-001";
            long count = messageService.getUnreadCount(currentUserId);
            return Result.success(count, "Unread count retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve unread count: " + e.getMessage());
        }
    }

    /**
     * Send a new message
     * POST /api/messages
     */
    @PostMapping
    public Result<Message> sendMessage(
            @RequestBody Map<String, String> messageData,
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestHeader(value = "X-Organization-Id", required = false) String organizationId) {
        try {
            String subject = messageData.get("subject");
            String content = messageData.get("content");
            String toUserId = messageData.get("toUserId");
            String toUserName = messageData.get("toUserName");
            String category = messageData.get("category");

            if (subject == null || content == null) {
                return Result.error("400", "Subject and content are required!");
            }
            if (toUserId == null) {
                return Result.error("400", "Recipient (toUserId) is required!");
            }

            String fromUserId = userId != null ? userId : "default-user-001";
            String fromUserName = messageData.get("fromUserName");
            
            Message message = new Message();
            message.setSubject(subject);
            message.setContent(content);
            message.setFromUserId(fromUserId);
            message.setFromUserName(fromUserName != null ? fromUserName : "Current User");
            message.setToUserId(toUserId);
            message.setToUserName(toUserName);
            message.setCategory(category != null ? category : "general");
            message.setOrganizationId(organizationId != null ? organizationId : "org-001");

            Message savedMessage = messageService.sendMessage(message);
            return Result.success(savedMessage, "Message sent successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to send message: " + e.getMessage());
        }
    }

    /**
     * Reply to a message
     * POST /api/messages/{messageId}/reply
     */
    @PostMapping("/{messageId}/reply")
    public Result<Message> replyToMessage(
            @PathVariable String messageId,
            @RequestBody Map<String, String> replyData,
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestHeader(value = "X-Organization-Id", required = false) String organizationId) {
        try {
            String content = replyData.get("content");
            String subject = replyData.get("subject");

            if (content == null || content.isEmpty()) {
                return Result.error("400", "Content is required!");
            }

            String fromUserId = userId != null ? userId : "default-user-001";
            String fromUserName = replyData.get("fromUserName");
            
            // Get original message to determine recipient
            Optional<Message> originalOpt = messageService.getMessageById(messageId);
            if (!originalOpt.isPresent()) {
                return Result.error("404", "Original message not found!");
            }
            
            Message original = originalOpt.get();
            
            Message reply = new Message();
            reply.setSubject(subject);
            reply.setContent(content);
            reply.setFromUserId(fromUserId);
            reply.setFromUserName(fromUserName != null ? fromUserName : "Current User");
            reply.setToUserId(original.getFromUserId());
            reply.setToUserName(original.getFromUserName());
            reply.setOrganizationId(organizationId != null ? organizationId : original.getOrganizationId());

            Message savedReply = messageService.replyToMessage(messageId, reply);
            if (savedReply != null) {
                return Result.success(savedReply, "Reply sent successfully!");
            } else {
                return Result.error("404", "Original message not found!");
            }
        } catch (Exception e) {
            return Result.error("500", "Failed to send reply: " + e.getMessage());
        }
    }

    /**
     * Mark message as read
     * PUT /api/messages/{messageId}/read
     */
    @PutMapping("/{messageId}/read")
    public Result<Message> markMessageAsRead(@PathVariable String messageId) {
        try {
            Message message = messageService.markAsRead(messageId);
            if (message != null) {
                return Result.success(message, "Message marked as read!");
            } else {
                return Result.error("404", "Message not found!");
            }
        } catch (Exception e) {
            return Result.error("500", "Failed to mark message as read: " + e.getMessage());
        }
    }

    /**
     * Delete a message (mark as deleted)
     * DELETE /api/messages/{messageId}
     */
    @DeleteMapping("/{messageId}")
    public Result<Boolean> deleteMessage(@PathVariable String messageId) {
        try {
            boolean deleted = messageService.deleteMessage(messageId);
            if (deleted) {
                return Result.success(true, "Message deleted successfully!");
            } else {
                return Result.error("404", "Message not found!");
            }
        } catch (Exception e) {
            return Result.error("500", "Failed to delete message: " + e.getMessage());
        }
    }
    
    /**
     * Permanently delete a message
     * DELETE /api/messages/{messageId}/permanent
     */
    @DeleteMapping("/{messageId}/permanent")
    public Result<Boolean> permanentlyDeleteMessage(@PathVariable String messageId) {
        try {
            boolean deleted = messageService.permanentlyDeleteMessage(messageId);
            if (deleted) {
                return Result.success(true, "Message permanently deleted!");
            } else {
                return Result.error("404", "Message not found!");
            }
        } catch (Exception e) {
            return Result.error("500", "Failed to permanently delete message: " + e.getMessage());
        }
    }

    /**
     * Get message by ID
     * GET /api/messages/{messageId}
     */
    @GetMapping("/{messageId}")
    public Result<Message> getMessageById(@PathVariable String messageId) {
        try {
            Optional<Message> message = messageService.getMessageById(messageId);
            if (message.isPresent()) {
                return Result.success(message.get(), "Message retrieved successfully!");
            } else {
                return Result.error("404", "Message not found!");
            }
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve message: " + e.getMessage());
        }
    }
    
    /**
     * Archive a message
     * PUT /api/messages/{messageId}/archive
     */
    @PutMapping("/{messageId}/archive")
    public Result<Message> archiveMessage(@PathVariable String messageId) {
        try {
            Message message = messageService.archiveMessage(messageId);
            if (message != null) {
                return Result.success(message, "Message archived successfully!");
            } else {
                return Result.error("404", "Message not found!");
            }
        } catch (Exception e) {
            return Result.error("500", "Failed to archive message: " + e.getMessage());
        }
    }
    
    /**
     * Get conversation between current user and another user
     * GET /api/messages/conversation/{userId}
     */
    @GetMapping("/conversation/{userId}")
    public Result<List<Message>> getConversation(
            @PathVariable String userId,
            @RequestHeader(value = "X-User-Id", required = false) String currentUserId) {
        try {
            String fromUserId = currentUserId != null ? currentUserId : "default-user-001";
            List<Message> messages = messageService.getConversation(fromUserId, userId);
            return Result.success(messages, "Conversation retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve conversation: " + e.getMessage());
        }
    }
    
    /**
     * Get replies to a message
     * GET /api/messages/{messageId}/replies
     */
    @GetMapping("/{messageId}/replies")
    public Result<List<Message>> getReplies(@PathVariable String messageId) {
        try {
            List<Message> replies = messageService.getReplies(messageId);
            return Result.success(replies, "Replies retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve replies: " + e.getMessage());
        }
    }
    
    /**
     * Get messages by category
     * GET /api/messages/category/{category}
     */
    @GetMapping("/category/{category}")
    public Result<List<Message>> getMessagesByCategory(
            @PathVariable String category,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        try {
            String currentUserId = userId != null ? userId : "default-user-001";
            List<Message> messages = messageService.getMessagesByCategory(currentUserId, category);
            return Result.success(messages, "Messages retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve messages: " + e.getMessage());
        }
    }
}
