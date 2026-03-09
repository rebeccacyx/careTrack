# API Test Cases Documentation

## Access Swagger UI
After starting the backend service, visit:
```
http://localhost:8080/swagger-ui.html
```

---

## 1. Daily Schedule Management Feature Tests

### 1.1 Batch Create Schedules
**Endpoint**: `POST /api/schedules/batch-create`

**Headers**:
```json
X-Organization-Id: org-001
X-User-Id: manager-001
```

**Request Body**:
```json
{
  "scheduleDate": "2025-01-20",
  "morningShiftWorkerIds": ["W001", "W002"],
  "eveningShiftWorkerIds": ["W003", "W004"],
  "scheduleNotes": "Regular schedule"
}
```

**Expected Result**: Returns 4 created schedule records

---

### 1.2 Batch Update Schedule Status
**Endpoint**: `PUT /api/schedules/batch-update-status`

**Request Body**:
```json
{
  "scheduleIds": ["schedule_id_1", "schedule_id_2"],
  "status": "confirmed"
}
```

**Expected Result**: Returns updated schedule list

---

### 1.3 Copy Schedule
**Endpoint**: `POST /api/schedules/copy`

**Headers**:
```json
X-Organization-Id: org-001
X-User-Id: manager-001
```

**Request Body**:
```json
{
  "sourceDate": "2025-01-20",
  "targetDate": "2025-01-21",
  "organizationId": "org-001",
  "managerId": "manager-001"
}
```

**Expected Result**: Copies schedule from 2025-01-20 to 2025-01-21

---

### 1.4 Get Weekly Schedule
**Endpoint**: `GET /api/schedules/weekly?startDate=2025-01-20&organizationId=org-001`

**Expected Result**: Returns weekly schedule starting from 2025-01-20

---

### 1.5 Validate Schedule Conflict
**Endpoint**: `GET /api/schedules/validate?workerId=W001&date=2025-01-20&shiftType=morning`

**Expected Result**: Returns true (no conflict) or false (conflict exists)

---

### 1.6 Delete All Schedules for a Date
**Endpoint**: `DELETE /api/schedules/date/2025-01-20?organizationId=org-001`

**Expected Result**: Returns number of deleted schedules

---

## 2. Worker Photo Upload Feature Tests

### 2.1 Upload Photo Endpoint
**Endpoint**: `POST /api/workers/{workerId}/photo`

**Path Parameter**: `workerId = W001`

**Request Body**:
```json
{
  "photoUrl": "https://example.com/photos/worker1.jpg"
}
```

**Expected Result**: Returns updated worker information including photo URL

---

### 2.2 Batch Upload Photos
**Endpoint**: `POST /api/workers/batch-upload-photos`

**Request Body**:
```json
{
  "W001": "https://example.com/photos/worker1.jpg",
  "W002": "https://example.com/photos/worker2.jpg",
  "W003": "https://example.com/photos/worker3.jpg"
}
```

**Expected Result**: Returns 3 updated worker records

---

### 2.3 Delete Worker Photo
**Endpoint**: `DELETE /api/workers/W001/photo`

**Expected Result**: Returns updated worker information with photoUrl set to null

---

### 2.4 Get Workers Without Photos
**Endpoint**: `GET /api/workers/organization/org-001/without-photos`

**Expected Result**: Returns list of workers who have not uploaded photos

---

## 3. Notification Feature Tests

### 3.1 Get My Notifications
**Endpoint**: `GET /api/notifications/my`

**Headers**:
```json
X-User-Id: user-001
```

**Expected Result**: Returns all notifications for the current user

---

### 3.2 Get Unread Notifications
**Endpoint**: `GET /api/notifications/unread`

**Headers**:
```json
X-User-Id: user-001
```

**Expected Result**: Returns list of unread notifications

---

### 3.3 Get Unread Notification Count
**Endpoint**: `GET /api/notifications/unread/count`

**Headers**:
```json
X-User-Id: user-001
```

**Expected Result**: Returns count of unread notifications

---

### 3.4 Mark Notification as Read
**Endpoint**: `PUT /api/notifications/{notificationId}/read`

**Expected Result**: Returns updated notification with isRead=true

---

### 3.5 Mark All Notifications as Read
**Endpoint**: `PUT /api/notifications/read-all`

**Headers**:
```json
X-User-Id: user-001
```

**Expected Result**: Returns number of notifications marked as read

---

### 3.6 Create Task Assignment Notification
**Endpoint**: `POST /api/notifications/task-assigned`

**Request Body**:
```json
{
  "workerId": "W001",
  "taskId": "task-001",
  "taskTitle": "Assist patient with meal",
  "assignedBy": "manager-001"
}
```

**Expected Result**: Creates a task assignment notification

---

### 3.7 Create Task Completion Notification
**Endpoint**: `POST /api/notifications/task-completed`

**Request Body**:
```json
{
  "managerId": "manager-001",
  "taskId": "task-001",
  "taskTitle": "Assist patient with meal",
  "completedBy": "W001"
}
```

**Expected Result**: Creates a task completion notification

---

### 3.8 Create Schedule Update Notification
**Endpoint**: `POST /api/notifications/schedule-updated`

**Request Body**:
```json
{
  "workerId": "W001",
  "scheduleDate": "2025-01-20",
  "shiftType": "morning",
  "updatedBy": "manager-001"
}
```

**Expected Result**: Creates a schedule update notification

---

### 3.9 Delete Notification
**Endpoint**: `DELETE /api/notifications/{notificationId}`

**Expected Result**: Notification is deleted

---

### 3.10 Get Notifications by Category
**Endpoint**: `GET /api/notifications/category/task`

**Headers**:
```json
X-User-Id: user-001
```

**Expected Result**: Returns notifications of task category

---

## 4. Communication Feature Tests

### 4.1 Send Message
**Endpoint**: `POST /api/messages`

**Headers**:
```json
X-User-Id: user-001
X-Organization-Id: org-001
```

**Request Body**:
```json
{
  "subject": "About Patient P1's Care Plan",
  "content": "Hello, I would like to discuss adjustments to P1's care plan.",
  "toUserId": "user-002",
  "toUserName": "Dr. Smith",
  "fromUserName": "Nurse Johnson",
  "category": "general"
}
```

**Expected Result**: Message sent successfully, notification created for recipient

---

### 4.2 Get Inbox
**Endpoint**: `GET /api/messages/inbox`

**Headers**:
```json
X-User-Id: user-001
```

**Expected Result**: Returns all messages received by the user

---

### 4.3 Get Sent Messages
**Endpoint**: `GET /api/messages/sent`

**Headers**:
```json
X-User-Id: user-001
```

**Expected Result**: Returns all messages sent by the user

---

### 4.4 Get Unread Messages
**Endpoint**: `GET /api/messages/unread`

**Headers**:
```json
X-User-Id: user-001
```

**Expected Result**: Returns list of unread messages

---

### 4.5 Get Unread Message Count
**Endpoint**: `GET /api/messages/unread/count`

**Headers**:
```json
X-User-Id: user-001
```

**Expected Result**: Returns count of unread messages

---

### 4.6 Reply to Message
**Endpoint**: `POST /api/messages/{messageId}/reply`

**Headers**:
```json
X-User-Id: user-002
X-Organization-Id: org-001
```

**Request Body**:
```json
{
  "content": "OK, we can discuss this issue tomorrow at 10 AM.",
  "fromUserName": "Dr. Smith"
}
```

**Expected Result**: Reply sent successfully, subject automatically prefixed with "Re:"

---

### 4.7 Mark Message as Read
**Endpoint**: `PUT /api/messages/{messageId}/read`

**Expected Result**: Message marked as read, status updated to "read"

---

### 4.8 Get Message Details
**Endpoint**: `GET /api/messages/{messageId}`

**Expected Result**: Returns complete message information

---

### 4.9 Archive Message
**Endpoint**: `PUT /api/messages/{messageId}/archive`

**Expected Result**: Message status updated to "archived"

---

### 4.10 Delete Message
**Endpoint**: `DELETE /api/messages/{messageId}`

**Expected Result**: Message marked as deleted (soft delete)

---

### 4.11 Permanently Delete Message
**Endpoint**: `DELETE /api/messages/{messageId}/permanent`

**Expected Result**: Message permanently deleted from database

---

### 4.12 Get Conversation
**Endpoint**: `GET /api/messages/conversation/user-002`

**Headers**:
```json
X-User-Id: user-001
```

**Expected Result**: Returns all messages between user-001 and user-002

---

### 4.13 Get Message Replies
**Endpoint**: `GET /api/messages/{messageId}/replies`

**Expected Result**: Returns all replies to the message

---

### 4.14 Get Messages by Category
**Endpoint**: `GET /api/messages/category/urgent`

**Headers**:
```json
X-User-Id: user-001
```

**Expected Result**: Returns messages of urgent category

---

## 5. Recommended Test Workflows

### 5.1 Complete Schedule Management Workflow
1. Create workers (if not already done)
2. Batch create schedules → Validate schedule conflicts
3. Get weekly schedule to view results
4. Batch update schedule status
5. Copy schedule to next day
6. Delete schedules for specific date

### 5.2 Complete Worker Photo Management Workflow
1. Get list of workers without photos
2. Upload photo for single worker
3. Batch upload photos for multiple workers
4. Verify photos have been uploaded
5. Delete photo for a worker

### 5.3 Complete Notification Feature Workflow
1. Create task assignment notification
2. Get unread notification count
3. Get unread notification list
4. Mark single notification as read
5. Create other notification types (schedule update, task completion, etc.)
6. Query notifications by category
7. Mark all notifications as read
8. Delete notification

### 5.4 Complete Communication Feature Workflow
1. User A sends message to User B
2. User B gets unread message count
3. User B views inbox
4. User B reads message (marks as read)
5. User B replies to message
6. User A gets conversation history
7. View all replies to message
8. Archive or delete message

---

## 6. Common Test Scenarios

### Scenario 1: Administrator Creates Weekly Schedule
```
1. POST /api/schedules/batch-create (Create Monday schedule)
2. POST /api/schedules/copy (Copy to Tuesday)
3. POST /api/schedules/copy (Copy to Wednesday)
4. ... Repeat until Sunday
5. GET /api/schedules/weekly (View entire week schedule)
```

### Scenario 2: Worker Receives and Completes Task
```
1. Manager: Create task (automatically triggers task assignment notification)
2. Worker: GET /api/notifications/unread (View unread notifications)
3. Worker: PUT /api/notifications/{id}/read (Mark notification as read)
4. Worker: Complete task
5. Worker: POST /api/notifications/task-completed (Notify manager)
6. Manager: Receives task completion notification
```

### Scenario 3: Message Exchange Between Users
```
1. User A: POST /api/messages (Send message to User B)
2. User B: GET /api/messages/unread/count (View unread count)
3. User B: GET /api/messages/inbox (View inbox)
4. User B: PUT /api/messages/{id}/read (Mark as read)
5. User B: POST /api/messages/{id}/reply (Reply to message)
6. User A: GET /api/messages/conversation/{userB} (View conversation)
```

---

## 7. Notes

1. **Headers**: Most endpoints require `X-User-Id` and/or `X-Organization-Id` headers
2. **Date Format**: Use `yyyy-MM-dd` format (e.g., 2025-01-20)
3. **ID Format**: Worker IDs use format like `W001`, `W002`, etc.
4. **Default Values**: If headers are not provided, system uses default values (e.g., org-001, default-user-001)
5. **Related Features**: Sending messages automatically creates notifications

---

## 8. Quick Test Data

### Sample Worker IDs
```
W001, W002, W003, W004, W005
```

### Sample User IDs
```
manager-001, user-001, user-002, worker-001
```

### Organization ID
```
org-001
```

### Sample Dates
```
2025-01-20, 2025-01-21, 2025-01-22
```

---

Happy Testing! 🚀



