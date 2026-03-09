# Password Reset Email Feature Test

## Feature Description
The password reset feature has been updated. When a user requests a password reset, they will receive an email containing the token, instead of the token being returned directly.

## Test Steps

### 1. Request Password Reset
**Endpoint**: `POST /api/auth/forgot-password`

**Request Body**:
```json
{
  "identifier": "user@example.com"
}
```

**Expected Response**:
```json
{
  "code": "200",
  "msg": "Password reset email sent successfully!",
  "data": "Email sent"
}
```

### 2. Check Email
The user should receive an email from `caretrack3@gmail.com` containing:
- Subject: "CareTrack - Password Reset Request"
- Content includes password reset token
- Token validity: 15 minutes

### 3. Reset Password Using Token
**Endpoint**: `POST /api/auth/reset-password`

**Request Body**:
```json
{
  "token": "token_copied_from_email",
  "newPassword": "newpassword123"
}
```

**Expected Response**:
```json
{
  "code": "200",
  "msg": "Password reset successfully!",
  "data": true
}
```

## Notes
- A valid `SENDGRID_API_KEY` environment variable must be set
- Sender email `caretrack3@gmail.com` must be verified in SendGrid
- Token validity is 15 minutes
- If user does not exist, returns 404 error
- If email sending fails, returns 404 error (but token is still generated)

## Test Cases

### Test Case 1: Normal Flow
1. Request reset using an existing user email
2. Check email inbox for received email
3. Use token from email to reset password
4. Verify new password can be used to log in

### Test Case 2: User Does Not Exist
1. Request reset using non-existent email
2. Should return 404 error

### Test Case 3: Token Expired
1. Request password reset
2. Wait 15 minutes, then use token
3. Should return 400 error

### Test Case 4: Invalid Token
1. Use invalid token to reset password
2. Should return 400 error
