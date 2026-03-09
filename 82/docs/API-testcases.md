## API Testcases (excluding UserController)

Base URL: `http://localhost:8081`

Example IDs used below:
- workerId: `68c52af392e99917d149612a`
- patientId: `68c52b0b92e99917d149612b`

Replace `{id}` placeholders with actual values as needed.

---

### FileController `/api/files`

Upload file metadata
```bash
curl -X POST http://localhost:8081/api/files \
  -H "Content-Type: application/json" \
  -d '{
    "name":"Examination Report.pdf",
    "category":"Worker Upload",
    "uploadedBy":"C1",
    "fileUrl":"https://example.com/files/exam-report.pdf",
    "contentType":"application/pdf",
    "size": 240123,
    "comment":"Initial upload"
  }'
```

List files
```bash
curl http://localhost:8081/api/files
curl "http://localhost:8081/api/files?category=Worker%20Upload"
curl "http://localhost:8081/api/files?uploadedBy=C1"
```

Get file by id / update comment / delete
```bash
curl http://localhost:8081/api/files/{id}
curl -X PUT http://localhost:8081/api/files/{id}/comment -H "Content-Type: application/json" -d '{"comment":"Reviewed"}'
curl -X DELETE http://localhost:8081/api/files/{id}
```

---

### TaskController `/api/tasks`

Create task
```bash
curl -X POST http://localhost:8081/api/tasks/create-for-patient \
  -H "Content-Type: application/json" \
  -d '{"title":"Morning vitals","description":"BP & Temp","priority":"HIGH","dueDate":"2025-10-08","assignedWorkerId":"68c52af392e99917d149612a","patientId":"68c52b0b92e99917d149612b"}'
```

Create recurring template
```bash
curl -X POST http://localhost:8081/api/tasks/recurring \
  -H "Content-Type: application/json" \
  -d '{"name":"Daily vitals","frequency":"DAILY","timeOfDay":"08:00","patientId":"68c52b0b92e99917d149612b","assignedWorkerId":"68c52af392e99917d149612a","startDate":"2025-10-08","endDate":null,"daysOfWeek":["MON","TUE","WED","THU","FRI","SAT","SUN"]}'
```

List recurring templates
```bash
curl http://localhost:8081/api/tasks/recurring
```

Update recurring template
```bash
curl -X PUT http://localhost:8081/api/tasks/recurring/{id} \
  -H "Content-Type: application/json" \
  -d '{"name":"Daily vitals (upd)","frequency":"DAILY","timeOfDay":"09:00","patientId":"68c52b0b92e99917d149612b","assignedWorkerId":"68c52af392e99917d149612a","startDate":"2025-10-08","endDate":null,"daysOfWeek":["MON","WED","FRI"]}'
```

Delete recurring template
```bash
curl -X DELETE http://localhost:8081/api/tasks/recurring/{id}
```

Toggle recurring template
```bash
curl -X POST http://localhost:8081/api/tasks/recurring/{id}/toggle
```

Generate tasks by date
```bash
curl -X POST "http://localhost:8081/api/tasks/recurring/generate?date=2025-10-08"
```

Task CRUD
```bash
curl http://localhost:8081/api/tasks/{id}
curl -X PUT http://localhost:8081/api/tasks/{id} -H "Content-Type: application/json" -d '{"title":"Update","status":"IN_PROGRESS"}'
curl -X DELETE http://localhost:8081/api/tasks/{id}
```

Task filters & stats
```bash
curl http://localhost:8081/api/tasks/worker/68c52af392e99917d149612a
curl http://localhost:8081/api/tasks/worker-name/Alice
curl http://localhost:8081/api/tasks/status/COMPLETED
curl http://localhost:8081/api/tasks/due-date/2025-10-08
curl http://localhost:8081/api/tasks/priority/HIGH
curl http://localhost:8081/api/tasks/today
curl http://localhost:8081/api/tasks/today/worker/68c52af392e99917d149612a
curl http://localhost:8081/api/tasks/pending-approval
curl http://localhost:8081/api/tasks/completed
curl http://localhost:8081/api/tasks/in-progress
curl http://localhost:8081/api/tasks/rejected
curl http://localhost:8081/api/tasks/stats
curl http://localhost:8081/api/tasks/stats/worker/68c52af392e99917d149612a
```

Complete/approve/reject/update status
```bash
curl -X POST http://localhost:8081/api/tasks/{id}/worker-complete
curl -X POST http://localhost:8081/api/tasks/{id}/approve -H "Content-Type: application/json" -d '{"note":"OK"}'
curl -X POST http://localhost:8081/api/tasks/{id}/reject  -H "Content-Type: application/json" -d '{"reason":"Incomplete"}'
curl -X PUT  http://localhost:8081/api/tasks/{id}/status  -H "Content-Type: application/json" -d '{"status":"COMPLETED"}'
```

---

### WorkerController `/api/workers`

Get / Update / Delete worker
```bash
curl http://localhost:8081/api/workers/68c52af392e99917d149612a
curl -X PUT http://localhost:8081/api/workers/68c52af392e99917d149612a -H "Content-Type: application/json" -d '{"name":"Alice Worker","status":"ACTIVE","organizationId":"org-001"}'
curl -X DELETE http://localhost:8081/api/workers/{id}
```

Activate / Deactivate
```bash
curl -X POST http://localhost:8081/api/workers/68c52af392e99917d149612a/activate
curl -X POST http://localhost:8081/api/workers/68c52af392e99917d149612a/deactivate
```

Allocate shift & update shift status
```bash
curl -X POST http://localhost:8081/api/workers/68c52af392e99917d149612a/allocate-shift -H "Content-Type: application/json" -d '{"date":"2025-10-08","time":"08:00-12:00","location":"Ward A","shiftType":"MORNING"}'
curl -X PUT  http://localhost:8081/api/workers/68c52af392e99917d149612a/shift-status -H "Content-Type: application/json" -d '{"status":"ON_DUTY"}'
```

Shift queries / remove allocation
```bash
curl      http://localhost:8081/api/workers/68c52af392e99917d149612a/shifts/2025-10-08
curl      http://localhost:8081/api/workers/organization/org-001/shifts/2025-10-08
curl -X DELETE http://localhost:8081/api/workers/68c52af392e99917d149612a/shifts/2025-10-08/08:00-12:00
```

Organization scope & daily schedule
```bash
curl http://localhost:8081/api/workers/organization/org-001
curl http://localhost:8081/api/workers/organization/org-001/available
curl -X POST   http://localhost:8081/api/workers/daily-schedule -H "Content-Type: application/json" -d '{"organizationId":"org-001","date":"2025-10-08"}'
curl          http://localhost:8081/api/workers/organization/org-001/daily-schedule/2025-10-08
curl -X DELETE http://localhost:8081/api/workers/organization/org-001/daily-schedule/2025-10-08
```

Upload worker photo
```bash
curl -X POST http://localhost:8081/api/workers/upload-photo -H "Content-Type: application/json" -d '{"workerId":"68c52af392e99917d149612a","photoUrl":"https://example.com/photo.jpg"}'
```

---

### ScheduleController `/api/schedules`

Schedule CRUD
```bash
curl http://localhost:8081/api/schedules/{id}
curl -X PUT http://localhost:8081/api/schedules/{id} -H "Content-Type: application/json" -d '{"workerId":"68c52af392e99917d149612a","date":"2025-10-08","status":"SCHEDULED","shiftType":"MORNING","notes":"Bring BP cuff"}'
curl -X DELETE http://localhost:8081/api/schedules/{id}
```

Queries & stats
```bash
curl http://localhost:8081/api/schedules/worker/68c52af392e99917d149612a
curl http://localhost:8081/api/schedules/date/2025-10-08
curl http://localhost:8081/api/schedules/worker/68c52af392e99917d149612a/date/2025-10-08
curl http://localhost:8081/api/schedules/organization/org-001
curl http://localhost:8081/api/schedules/organization/org-001/date/2025-10-08
curl http://localhost:8081/api/schedules/manager/mgr-001
curl http://localhost:8081/api/schedules/status/SCHEDULED
curl http://localhost:8081/api/schedules/shift-type/MORNING
curl "http://localhost:8081/api/schedules/date-range?startDate=2025-10-01&endDate=2025-10-31"
curl "http://localhost:8081/api/schedules/worker/68c52af392e99917d149612a/has-schedule/2025-10-08"
curl "http://localhost:8081/api/schedules/stats/org-001?date=2025-10-08"
```

Photo & status
```bash
curl -X POST http://localhost:8081/api/schedules/{id}/upload-photo -H "Content-Type: application/json" -d '{"photoUrl":"https://example.com/snap.jpg"}'
curl -X PUT  http://localhost:8081/api/schedules/{id}/status       -H "Content-Type: application/json" -d '{"status":"COMPLETED"}'
```

---

### PatientController `/api/patients`

Patient CRUD & queries
```bash
curl http://localhost:8081/api/patients/{id}
curl -X PUT http://localhost:8081/api/patients/{id} -H "Content-Type: application/json" -d '{"firstName":"Bob","lastName":"Chen","dob":"1950-01-01","medicalRecordNumber":"MRN-0001"}'
curl -X DELETE http://localhost:8081/api/patients/{id}
curl http://localhost:8081/api/patients/family-member/family-001
curl http://localhost:8081/api/patients/poa/poa-001
curl http://localhost:8081/api/patients/medical-record/MRN-0001
```

Assignments
```bash
curl -X POST http://localhost:8081/api/patients/{id}/assign-family-member -H "Content-Type: application/json" -d '{"familyMemberId":"family-001"}'
curl -X POST http://localhost:8081/api/patients/{id}/assign-poa           -H "Content-Type: application/json" -d '{"poaId":"poa-001"}'
```

---

### BudgetController `/api/budget`

Budget ops & stats
```bash
curl http://localhost:8081/api/budget/patient/68c52b0b92e99917d149612b
curl -X DELETE http://localhost:8081/api/budget/patient/68c52b0b92e99917d149612b
curl -X POST http://localhost:8081/api/budget/adjust-total -H "Content-Type: application/json" -d '{"patientId":"68c52b0b92e99917d149612b","amount":5000}'
curl -X POST http://localhost:8081/api/budget/category     -H "Content-Type: application/json" -d '{"patientId":"68c52b0b92e99917d149612b","name":"Supplies","limit":1200}'
curl -X POST http://localhost:8081/api/budget/sub-element   -H "Content-Type: application/json" -d '{"patientId":"68c52b0b92e99917d149612b","categoryId":"cat-001","name":"Gloves","limit":200}'
curl -X POST http://localhost:8081/api/budget/monthly-usage -H "Content-Type: application/json" -d '{"patientId":"68c52b0b92e99917d149612b","categoryId":"cat-001","subElementId":"sub-001","month":"2025-10","used":50}'
curl http://localhost:8081/api/budget/warnings/68c52b0b92e99917d149612b
curl http://localhost:8081/api/budget/statistics/68c52b0b92e99917d149612b
curl -X POST http://localhost:8081/api/budget/validate -H "Content-Type: application/json" -d '{"patientId":"68c52b0b92e99917d149612b"}'
```

---

### AuthorizationController `/api/authorization`

Grant / revoke
```bash
# Grant access (MANAGER or WORKER) — all fields required
curl -X POST http://localhost:8081/api/authorization/grant \
  -H "Content-Type: application/json" \
  -d '{
    "patientId":"68c52b0b92e99917d149612b",
    "authorizedBy":"family-001",
    "authorizedTo":"mgr-001",
    "authorizationType":"MANAGER",
    "organizationId":"org-001"
  }'

# Revoke access (must be revoked by the original authorizer)
curl -X POST http://localhost:8081/api/authorization/revoke \
  -H "Content-Type: application/json" \
  -d '{
    "patientId":"68c52b0b92e99917d149612b",
    "authorizedBy":"family-001",
    "authorizedTo":"mgr-001"
  }'
```

Queries & bulk revoke
```bash
# List authorizations for a patient / for a user
curl "http://localhost:8081/api/authorization/patient/68c52b0b92e99917d149612b"
curl "http://localhost:8081/api/authorization/user/mgr-001"

# Check whether a user currently has access to a patient
curl "http://localhost:8081/api/authorization/check?userId=mgr-001&patientId=68c52b0b92e99917d149612b"

# List all accessible patient IDs for a user
curl "http://localhost:8081/api/authorization/accessible-patients/mgr-001"

# Revoke all access for a user (also revokes workers under a manager)
curl -X POST "http://localhost:8081/api/authorization/revoke-all/mgr-001"

# Revoke all access granted by a specific authorizer (FM/POA)
curl -X POST "http://localhost:8081/api/authorization/revoke-by-authorizer/family-001"

# Organizations accessible by a Family/POA id
curl "http://localhost:8081/api/authorization/accessible-orgs/family-001"
```

Example success response shape
```json
{
  "code": "0",
  "msg": "Access granted successfully!",
  "data": "Access granted!"
}
```

---

### InviteCodeController `/api/invite`
```bash
curl -X POST http://localhost:8081/api/invite/generate -H "Content-Type: application/json" -d '{"creatorId":"family-001"}'
curl -X POST http://localhost:8081/api/invite/use      -H "Content-Type: application/json" -d '{"code":"ABCD-1234"}'
curl        "http://localhost:8081/api/invite/my-codes?creatorId=family-001"
curl -X DELETE http://localhost:8081/api/invite/{codeId}
curl        "http://localhost:8081/api/invite/patient/68c52b0b92e99917d149612b"
```

---

### Health `/api/health`
```bash
curl http://localhost:8081/api/health
```



