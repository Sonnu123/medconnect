# MedConnect - Patient Appointment System

A full-stack web application for managing patient appointments and health records built with Spring Boot and React.

## Features

- User authentication (Patients & Doctors)
- Book and manage appointments
- Upload and view health records
- Role-based dashboards
- Secure JWT authentication

## Tech Stack

**Backend:**
- Java 17
- Spring Boot 3.2.0
- PostgreSQL
- Spring Security + JWT
- Maven

**Frontend:**
- React 18
- React Router
- Axios
- CSS3

## Project Structure

```
medconnect-simple/
├── backend/
│   ├── src/main/
│   │   ├── java/com/medconnect/     (9 Java files - flat structure)
│   │   └── resources/
│   │       └── application.properties
│   ├── pom.xml
│   ├── mvnw / mvnw.cmd              (Maven wrapper)
│   └── .mvn/
├── frontend/
│   ├── public/
│   │   └── index.html
│   ├── src/                         (7 files - flat structure)
│   │   ├── index.js
│   │   ├── App.js
│   │   ├── Auth.js
│   │   ├── Dashboard.js
│   │   ├── Navbar.js
│   │   ├── api.js
│   │   └── index.css
│   └── package.json
└── README.md
```

---

# 🚀 SETUP & LAUNCH GUIDE

## Prerequisites

Install these before starting:

1. **Java 17+**
   - Download: https://adoptium.net/
   - Verify: `java -version`

2. **Node.js 16+**
   - Download: https://nodejs.org/
   - Verify: `node -v`

3. **PostgreSQL 14+**
   - Download: https://www.postgresql.org/download/
   - Verify: `psql --version`

---

## Step 1: Database Setup

Open your terminal and run:

```bash
# Start PostgreSQL CLI
psql postgres

# Or on Windows:
psql -U postgres
```

Then paste these commands:

```sql
CREATE DATABASE medconnect;
CREATE USER medconnect_user WITH PASSWORD 'medconnect_pass';
GRANT ALL PRIVILEGES ON DATABASE medconnect TO medconnect_user;
\c medconnect
GRANT ALL ON SCHEMA public TO medconnect_user;
\q
```

**Test connection:**
```bash
psql -U medconnect_user -d medconnect -h localhost
# If it works, you're good! Type \q to exit
```

---

## Step 2: Backend Setup

```bash
cd medconnect-simple/backend

# First time only - build the project
./mvnw clean install           # Mac/Linux
mvnw.cmd clean install         # Windows

# This will:
# - Download all dependencies (~5 minutes first time)
# - Compile the code
# - Should see "BUILD SUCCESS"
```

**If Maven wrapper fails:**
Download this file: https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar
Save to: `backend/.mvn/wrapper/maven-wrapper.jar`

---

## Step 3: Frontend Setup

```bash
cd ../frontend

# Install dependencies
npm install

# This will:
# - Download all packages (~2-3 minutes)
# - Should see "added XXX packages"
```

---

## Step 4: Launch the Application

You need **TWO terminal windows** open:

### Terminal 1 - Backend (Port 8080)

```bash
cd medconnect-simple/backend

./mvnw spring-boot:run         # Mac/Linux
mvnw.cmd spring-boot:run       # Windows
```

**Wait for this message:**
```
Started MedConnectApplication in X.XXX seconds
```

**Backend is now running on:** http://localhost:8080

### Terminal 2 - Frontend (Port 3000)

```bash
cd medconnect-simple/frontend

npm start
```

**Your browser will automatically open to:** http://localhost:3000

---

## Step 5: Login & Test

**Default accounts:**

**Doctor:**
- Email: `doctor@medconnect.com`
- Password: `doctor123`

**Patient:**
- Email: `patient@medconnect.com`
- Password: `patient123`

**Try this flow:**
1. Login as patient
2. Click "Book New Appointment"
3. Select doctor, date/time, add reason
4. Book appointment
5. Logout
6. Login as doctor
7. See the appointment in your queue
8. Mark it as completed

---

# 📦 GIT SETUP & GITHUB PUSH

## Initialize Git Repository

```bash
cd medconnect-simple

# Initialize git
git init

# Add all files
git add .

# Make first commit
git commit -m "Initial commit: MedConnect appointment system"

# Set main branch
git branch -M main
```

## Push to GitHub

### Option A: New Repository (Recommended)

1. Go to https://github.com/new
2. Name: `medconnect` (or whatever you want)
3. **Don't** initialize with README
4. Click "Create repository"

Then in your terminal:

```bash
# Add your GitHub repo as remote (replace YOUR_USERNAME)
git remote add origin https://github.com/YOUR_USERNAME/medconnect.git

# Push to GitHub
git push -u origin main
```

### Option B: Use GitHub CLI

```bash
# If you have gh CLI installed
gh repo create medconnect --public --source=. --remote=origin --push
```

## Making Changes Later

```bash
# After making changes to code
git add .
git commit -m "Describe what you changed"
git push
```

---

# 🛠️ TROUBLESHOOTING

## Port Already in Use

**Port 8080 (Backend):**
```bash
# Find process
lsof -i :8080              # Mac/Linux
netstat -ano | findstr :8080    # Windows

# Kill it
kill -9 <PID>              # Mac/Linux
taskkill /PID <PID> /F     # Windows
```

**Port 3000 (Frontend):**
```bash
# Find process
lsof -i :3000              # Mac/Linux
netstat -ano | findstr :3000    # Windows

# Kill it
kill -9 <PID>              # Mac/Linux
taskkill /PID <PID> /F     # Windows
```

## Database Connection Failed

1. Check PostgreSQL is running:
   ```bash
   # Mac
   brew services list
   
   # Ubuntu
   sudo service postgresql status
   
   # Windows
   # Check Services app → PostgreSQL
   ```

2. Verify credentials in `backend/src/main/resources/application.properties`

3. Test connection:
   ```bash
   psql -U medconnect_user -d medconnect -h localhost
   ```

## Backend Build Errors

```bash
cd backend

# Clear everything and rebuild
rm -rf target
./mvnw clean install

# If still fails, check Java version
java -version    # Must be 17+
```

## Frontend Errors

```bash
cd frontend

# Clear and reinstall
rm -rf node_modules package-lock.json
npm install
npm start
```

## Can't Login

1. Check backend console for errors
2. Make sure backend shows "Started MedConnectApplication"
3. Try registering a new account
4. Check browser console (F12) for errors

---

# 📊 API ENDPOINTS

Base URL: `http://localhost:8080/api`

## Public Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/auth/register` | Register new user |
| POST | `/auth/login` | Login user |

## Protected Endpoints (Require JWT Token)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/appointments` | Create appointment |
| GET | `/appointments/patient` | Get patient appointments |
| GET | `/appointments/doctor` | Get doctor appointments |
| PUT | `/appointments/{id}/status` | Update appointment status |
| GET | `/appointments/doctors` | List all doctors |
| POST | `/health-records` | Upload health record |
| GET | `/health-records` | Get user's records |
| GET | `/health-records/{id}` | Get specific record |

---

# 🎯 WHAT TO DO NEXT

## Test All Features

**As Patient:**
- [x] Register new account
- [x] Login
- [x] Book appointment
- [x] View appointment history
- [x] Upload health record
- [x] View health records

**As Doctor:**
- [x] Login
- [x] View appointments
- [x] Complete appointment with notes
- [x] Cancel appointment

## Customize It

- Change the color scheme in `frontend/src/index.css`
- Add new appointment types
- Add profile editing
- Add appointment search/filter
- Add email notifications

## Deploy It

Free options:
- **Railway** (recommended): https://railway.app
- **Render**: https://render.com
- **Heroku**: https://heroku.com

---

# 📝 PROJECT FILES

## Backend (9 files)
1. `MedConnectApplication.java` - Main entry point
2. `SecurityConfig.java` - Security configuration
3. `DataInitializer.java` - Seed default users
4. `Models.java` - Database entities (User, Appointment, HealthRecord)
5. `DTOs.java` - Request/response objects
6. `Repositories.java` - Database access
7. `Controllers.java` - REST API endpoints
8. `Services.java` - Business logic
9. `SecurityComponents.java` - JWT authentication

## Frontend (7 files)
1. `index.js` - React entry point
2. `App.js` - Routing & auth state
3. `Auth.js` - Login & register forms
4. `Dashboard.js` - Patient & doctor views
5. `Navbar.js` - Navigation bar
6. `api.js` - Backend API calls
7. `index.css` - All styles

---

# 📞 QUICK REFERENCE

**Start Backend:**
```bash
cd backend && ./mvnw spring-boot:run
```

**Start Frontend:**
```bash
cd frontend && npm start
```

**Stop Both:**
Press `Ctrl+C` in both terminal windows

**Access App:**
http://localhost:3000

**Backend API:**
http://localhost:8080/api

**Login:**
- doctor@medconnect.com / doctor123
- patient@medconnect.com / patient123

---

Built with ❤️ using Spring Boot & React
// API docs
