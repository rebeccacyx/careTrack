# 🩺 CareTrack — Scheduling for Care
*A unified care management platform for families, caregivers, and organizations.*

---

## Overview
CareTrack is a cloud-based care management system designed for **People with Special Needs (PWSN)**.  
It enables **managers, caregivers, and families** to coordinate care, manage tasks, track budgets, and monitor health — all in one platform.

---

## Dependencies
Before running this project, make sure you have:
- [Java 21 (JDK)](https://www.oracle.com/java/technologies/downloads/)
- [Maven](https://maven.apache.org/download.cgi)
- [Node.js 18+ & npm](https://nodejs.org/)
- [Git](https://git-scm.com/)
- [Heroku CLI](https://devcenter.heroku.com/articles/heroku-cli)
- (Optional) [MongoDB Community Server](https://www.mongodb.com/try/download/community)

---

## Getting Started

1. **Clone the repository**
   ```bash
   git clone https://github.com/Eighty-two-82/Eighty-two.git
   cd Eighty-two
   ```
2. **Run the backend**
   ```bash
   cd backend
   ./mvnw spring-boot:run
   ```
   Runs at: http://localhost:8081
3. **Run the frontend**
   ```bash
   cd frontend
   npm install
   npm run dev
   ```
   Runs at: http://localhost:5173
4. **Connect to MongoDB**
   ```bash 
   Local: mongodb://localhost:27017/eighty-two
   Production: export SPRING_DATA_MONGODB_URI=<your_mongodb_uri>  # or MONGODB_URI
   ```
---

### Environment Variables：

**Backend SendGrid Configuration:**
To enable email sending (for forget password and budget alerts), you need to set the SendGrid API key:
```bash
export SENDGRID_API_KEY=SG.your_actual_api_key_here
```

Or add it to your shell profile (`~/.bashrc`, `~/.zshrc`, etc.):
```bash
echo 'export SENDGRID_API_KEY=SG.your_actual_api_key_here' >> ~/.bashrc
source ~/.bashrc
```

**Email Features:**
- **Forget Password**: When a user requests password reset, an email with reset token will be sent to their registered email address.
- **Budget Alerts**: When budget usage reaches 80% (warning) or 100% (critical), an email alert will be sent to the POA (Power of Attorney) user.

**Test Email Configuration:**
- Check email service status: `GET http://localhost:8081/api/mail/status`
- Send test email: `POST http://localhost:8081/api/mail/test?to=your-email@example.com`

**Frontend .env.example**
VITE_API_BASE_URL=http://localhost:8081
**.env is ignored in .gitignore and should not be committed**
**Modify `.env` locally to point to different backend endpoints.**

---
## ☁️ Deployment

This project uses **Heroku CI/CD Pipelines** for continuous integration and automatic deployment.

Every push to the **`main`** branch automatically triggers a full build and deployment of both backend and frontend applications.

| Component | URL | Description |
|------------|-----|-------------|
| **Frontend (Vue 3)** | 🌐 [https://care-track-e2ca875a8e53.herokuapp.com/login](https://care-track-e2ca875a8e53.herokuapp.com/login) | Public user interface |
| **Backend (Spring Boot)** | 🌐 [https://care-scheduling-app-e8951cd9f9c6.herokuapp.com](https://care-scheduling-app-e8951cd9f9c6.herokuapp.com) | RESTful API for CareTrack |

**Deployment Flow**
1. Developer commits changes to `main` branch.  
2. Heroku pipeline automatically builds and redeploys both frontend and backend.  
3. Environment variables are securely injected during deployment.

**Environment Variables**

SPRING_DATA_MONGODB_URI=

SENDGRID_API_KEY=

SPRING_PROFILES_ACTIVE=prod
> These variables are used to configure database access, email service, and runtime profile during deployment.
---

## 💡 Features

| Feature | Description |
|----------|-------------|
| 🔐 **Authentication** | Secure login, registration, and password reset |
| 📆 **Task Scheduling** | Assign and track caregiving tasks |
| 💸 **Budget Management** | Monitor care budgets and receive alerts |
| ❤️ **Health Monitoring** | Record and view patient data |
| ✉️ **Email Notifications** | Automated system messages via SendGrid |
| ☁️ **Cloud Storage** | Real-time persistence on MongoDB Atlas |

---

## 🎨 Frontend Structure

```bash
frontend/
 ├── src/
 │   ├── assets/        # Static resources (icons, images, styles)
 │   ├── components/    # Reusable UI components (buttons, cards, etc.)
 │   ├── router/        # Vue Router configuration
 │   ├── services/      # API request modules (Axios calls)
 │   ├── views/         # Page-level views (Dashboard, Budget, Tasks, etc.)
 │   ├── App.vue        # Root component
 │   ├── main.js        # App entry point
 │   └── style.css      # Global CSS or Tailwind entry
 ├── public/            # Public assets served directly
 ├── test/              # Frontend unit test files (Vitest)
 ├── vite.config.js     # Vite configuration
 ├── package.json       # Dependencies and scripts
 └── README.md          # Project setup and documentation
```
---

## Backend Structure

```bash
backend/
 ├── src/main/java/com/careapp/
 │   ├── config/        # Configuration (email, security, MongoDB setup)
 │   ├── controller/    # REST API controllers
 │   ├── domain/        # Entity/data model classes (MongoDB documents)
 │   ├── dto/           # Data Transfer Objects for API requests/responses
 │   ├── repository/    # MongoDB repositories (extends MongoRepository)
 │   ├── service/       # Business logic layer
 │   ├── utils/         # Utility classes (helpers, formatters, etc.)
 │   └── CareAppApplication.java  # Spring Boot main entry point
 ├── src/test/java/com/careapp/controller/   # Backend unit tests
 ├── uploads/          # Uploaded files directory
 ├── pom.xml           # Maven configuration
 ├── Procfile          # Heroku deployment configuration
 ├── app.json          # Heroku app definition
 ├── init.sql          # DB initialization script (if used)
 ├── start-backend.sh  # Local start script
 └── start-with-email.sh # Start script including email setup
```

## 🧪 Testing

CareTrack integrates **manual**, **unit**, and **integration testing** to ensure code stability and functionality across both backend and frontend components.

| Type | Tool | Scope |
|------|------|--------|
| 🧩 **Unit Testing** | JUnit | Backend service and controller logic |
| 🔗 **Integration Testing** | Manual (Postman, Browser) | End-to-end flow between modules |
| 🖥️ **Frontend Testing** | Vitest | Component and UI interaction testing |

---

### 🧠 Run Backend Tests
```bash
cd backend
./mvnw test
```
### 🧠 Run Frontend Tests
```bash
cd frontend
npm run test
```
> Both backend and frontend tests can be executed locally before deployment to ensure functionality and integration stability.
---
## 👥 Contributors

| Name | Role | Email |
|------|------|--------|
| **Yuxuan Chen** | Product Owner & Frontend Development | yuxuchen8@student.unimelb.edu.au |
| **Yuanmeng Yi** | Scrum Master & Full Stack Development | yuanmeng.yi@student.unimelb.edu.au |
| **Yutong He** | Backend Developer | yutongh8@student.unimelb.edu.au |
| **Songzhu Li** | Backend Developer | songzhul@student.unimelb.edu.au |
| **Tongyu Yang** | Frontend Developer | tongyu.yang1@student.unimelb.edu.au |

---

## 📜 License

 

---

## 🔗 Links

- 🌐 **Live App**  
  [Frontend](https://care-track-e2ca875a8e53.herokuapp.com/login) | [Backend](https://care-scheduling-app-e8951cd9f9c6.herokuapp.com)

- 🧭 **Documentation (Confluence)** – [project workspace](https://eighty-two.atlassian.net/wiki/spaces/EIGHTYTWOS/folder/38338574?atlOrigin=eyJpIjoiYmU5MjZkMjhkMzM4NDgzNGFmY2Y2N2U0N2ZhMDNkNTciLCJwIjoiYyJ9)

- 💾 **Source Code**  
  [GitHub Repository](https://github.com/Eighty-two-82/Eighty-two)

