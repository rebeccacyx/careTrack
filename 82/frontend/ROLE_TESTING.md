# Role Testing Guide

## How to Modify User Roles for Testing

### Method 1: Modify userService.js (Recommended)
Open the `src/services/userService.js` file and find the following two locations:

1. **Line 14** - In the login function:
```javascript
role: "manager", // Change to 'poa', 'manager', or 'worker'
```

2. **Line 31** - In the getMe function:
```javascript
role: "manager", // Change to 'poa', 'manager', or 'worker'
```

### Method 2: Modify router/index.js (Alternative)
Open the `src/router/index.js` file and find line 18:
```javascript
return { role: 'manager' } // Change to 'poa', 'manager', or 'worker'
```

## Role Descriptions

- **poa**: POA/FM role - Shows complete menu (Home, Tasks, Carer Team, Budget, Upload, Setting)
- **manager**: Manager role - Shows management menu (Home, Worker Management, Budget, Tasks, Upload, Communication, Setting)
- **worker**: Worker role - Shows basic menu (Home, Tasks, Upload, Setting)

## Invite Code Testing

You can also modify the invite code status in `userService.js`:
```javascript
// Lines 19 and 43
valid: false, // Change to true to skip invite code flow
```

## Testing Process

1. Modify role settings
2. Save files
3. Refresh browser
4. Re-login to test

## Important Notes

- Save files and refresh browser after making changes
- Ensure role settings are consistent in both files
- Manager and worker roles will trigger invite code flow (if valid: false)
