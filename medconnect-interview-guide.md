# MedConnect - Complete Beginner's Guide
## From Zero to Interview Ready

**For someone who only knows basic Java**

---

# TABLE OF CONTENTS

1. [How Web Applications Work - The Foundation](#part-1-how-web-applications-work)
2. [What is Spring Boot?](#part-2-what-is-spring-boot)
3. [What is React?](#part-3-what-is-react)
4. [JavaScript Crash Course](#part-4-javascript-crash-course)
5. [How Frontend and Backend Talk](#part-5-how-frontend-and-backend-talk)
6. [Your Code - Line by Line](#part-6-your-code-explained)
7. [Interview Preparation](#part-7-interview-preparation)

---

# PART 1: How Web Applications Work

## The Restaurant Analogy

Imagine a restaurant:
- **Customer** = User with a web browser
- **Waiter** = Frontend (React app)
- **Kitchen** = Backend (Spring Boot)
- **Pantry** = Database (PostgreSQL)

**How it works:**
1. Customer tells waiter what they want (clicks "Book Appointment")
2. Waiter writes it down and gives order to kitchen (React sends request to Spring Boot)
3. Kitchen checks pantry for ingredients (Spring Boot queries database)
4. Kitchen prepares food (Spring Boot processes data)
5. Waiter brings food to customer (React displays the result)

## Client-Server Model

```
┌─────────────┐                    ┌─────────────┐
│   Browser   │ ←── Internet ───→  │   Server    │
│  (Chrome)   │                    │  (Backend)  │
│             │                    │             │
│  Frontend   │                    │ Spring Boot │
│   React     │                    │    +        │
│             │                    │  Database   │
└─────────────┘                    └─────────────┘
```

**Your computer (localhost):**
- **Frontend** runs on http://localhost:3000
- **Backend** runs on http://localhost:8080
- They're both on YOUR machine, but pretending to be separate servers

## What is HTTP?

**HTTP = HyperText Transfer Protocol** (how computers talk on the web)

It's like sending letters with specific formats:

**Request (from frontend to backend):**
```
POST /api/auth/login
Headers: Content-Type: application/json
Body: {
  "email": "patient@medconnect.com",
  "password": "patient123"
}
```

**Response (from backend to frontend):**
```
Status: 200 OK
Body: {
  "token": "abc123...",
  "userId": 2,
  "role": "PATIENT"
}
```

**Common HTTP Methods:**
- **GET** = "Give me data" (like reading a book)
- **POST** = "Create new data" (like writing a new page)
- **PUT** = "Update existing data" (like editing a page)
- **DELETE** = "Remove data" (like ripping out a page)

---

# PART 2: What is Spring Boot?

## What Problem Does It Solve?

**Old Java Web Development (painful):**
```
1. Configure Tomcat server (50 XML files)
2. Set up database connections manually
3. Write tons of boilerplate code
4. Handle HTTP requests manually
5. Manage security yourself
6. Configure everything by hand
```

**Spring Boot (easy):**
```
1. Add dependencies to pom.xml
2. Write your actual business logic
3. Everything else is automatic!
```

## What IS Spring Boot?

**Spring Boot is a Java framework that:**
- Creates a web server for you (Tomcat is built-in)
- Handles HTTP requests/responses automatically
- Connects to databases for you
- Manages security
- Converts JSON ↔ Java objects automatically
- Does 90% of the boring setup work

**Think of it as:**
"Java on steroids for building web servers"

## Key Spring Boot Concepts

### 1. **Annotations** (the @ things)

Annotations tell Spring Boot what to do automatically.

```java
@RestController  // "This class handles web requests"
@Service         // "This class contains business logic"
@Repository      // "This class talks to the database"
@Autowired       // "Spring, please give me an instance of this"
```

**Example:**
```java
@RestController
public class HelloController {
    @GetMapping("/hello")
    public String sayHello() {
        return "Hello!";
    }
}
```

What happens:
1. Spring Boot sees `@RestController`
2. It says "Oh, I need to watch this class for web requests"
3. It sees `@GetMapping("/hello")`
4. It says "When someone visits /hello, call this method"
5. User visits http://localhost:8080/hello
6. Spring Boot automatically calls `sayHello()`
7. Returns "Hello!" to the browser

**You didn't write ANY code to:**
- Start a server
- Listen on a port
- Parse the HTTP request
- Send the HTTP response
- Spring Boot did it ALL automatically!

### 2. **Dependency Injection** (@Autowired)

**Without Spring:**
```java
public class AppointmentService {
    private AppointmentRepository repo;
    
    public AppointmentService() {
        this.repo = new AppointmentRepository(); // You create it
    }
}
```

**With Spring:**
```java
@Service
public class AppointmentService {
    @Autowired
    private AppointmentRepository repo; // Spring creates it for you!
    
    // No constructor needed!
}
```

**Why is this better?**
- Spring manages all object creation
- You get the SAME instance everywhere (singleton pattern)
- Easy to test (can swap real database with fake one)
- Less code to write

### 3. **MVC Pattern** (Model-View-Controller)

Spring Boot organizes code into layers:

```
Controller  →  Service  →  Repository  →  Database
   ↓            ↓            ↓
"Handle      "Business    "Talk to
requests"     logic"      database"
```

**Example flow - Booking an appointment:**

1. **Controller** receives HTTP request
   ```java
   @PostMapping("/appointments")
   public Appointment create(@RequestBody AppointmentRequest request) {
       return appointmentService.createAppointment(request);
   }
   ```

2. **Service** validates and processes
   ```java
   public Appointment createAppointment(AppointmentRequest request) {
       // Check if doctor exists
       // Check if time slot is available
       // Create appointment
       return appointmentRepository.save(appointment);
   }
   ```

3. **Repository** saves to database
   ```java
   public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
       // Spring Boot writes the SQL for you!
   }
   ```

### 4. **JPA/Hibernate** (Database Magic)

**You write Java classes:**
```java
@Entity
@Table(name = "users")
class User {
    @Id
    @GeneratedValue
    private Long id;
    
    private String email;
    private String password;
}
```

**Spring Boot automatically:**
- Creates the database table
- Generates SQL queries
- Converts rows ↔ Java objects

**You write:**
```java
User user = userRepository.findByEmail("patient@medconnect.com");
```

**Spring Boot executes:**
```sql
SELECT * FROM users WHERE email = 'patient@medconnect.com';
```

You never wrote SQL! Spring Boot did it!

---

# PART 3: What is React?

## What Problem Does It Solve?

**Old way (Vanilla JavaScript + HTML):**
```html
<div id="appointment-list"></div>

<script>
// Fetch data
fetch('/api/appointments')
  .then(res => res.json())
  .then(data => {
    // Manually create HTML for each appointment
    let html = '';
    data.forEach(apt => {
      html += '<div>' + apt.patientName + '</div>';
    });
    document.getElementById('appointment-list').innerHTML = html;
  });
</script>
```

**Problems:**
- Mixing HTML and JavaScript is messy
- Hard to reuse code
- Manually updating the page is tedious
- Gets complex FAST

**React way:**
```javascript
function AppointmentList() {
  const [appointments, setAppointments] = useState([]);
  
  useEffect(() => {
    fetch('/api/appointments')
      .then(res => res.json())
      .then(data => setAppointments(data));
  }, []);
  
  return (
    <div>
      {appointments.map(apt => (
        <div key={apt.id}>{apt.patientName}</div>
      ))}
    </div>
  );
}
```

**Benefits:**
- Cleaner code
- Automatic page updates when data changes
- Reusable components
- Easier to understand

## What IS React?

**React is a JavaScript library for building user interfaces.**

Think of it like LEGO blocks:
- Each component is a LEGO piece
- You combine small pieces to make bigger things
- Pieces are reusable

**Example - Building a page:**
```javascript
<App>
  <Navbar />
  <Dashboard>
    <AppointmentList />
    <HealthRecords />
  </Dashboard>
</App>
```

Each piece is a separate component you can build and test independently.

## Key React Concepts

### 1. **Components** (Reusable UI pieces)

```javascript
function Navbar({ user, onLogout }) {
  return (
    <div className="navbar">
      <h1>MedConnect</h1>
      <span>Welcome, {user.fullName}</span>
      <button onClick={onLogout}>Logout</button>
    </div>
  );
}
```

This is a function that returns HTML-like code (it's called JSX).

You can use it anywhere:
```javascript
<Navbar user={currentUser} onLogout={handleLogout} />
```

### 2. **JSX** (HTML in JavaScript)

```javascript
const name = "John";
const element = <h1>Hello, {name}!</h1>;
```

**It looks like HTML but it's JavaScript!**

- Curly braces `{}` let you insert JavaScript
- You can use variables, functions, loops, etc.

```javascript
<div>
  {appointments.map(apt => (
    <div key={apt.id}>
      <h3>{apt.doctor.fullName}</h3>
      <p>{apt.reason}</p>
    </div>
  ))}
</div>
```

### 3. **State** (Data that can change)

```javascript
const [count, setCount] = useState(0);

// count = current value (0)
// setCount = function to change it

<button onClick={() => setCount(count + 1)}>
  Clicked {count} times
</button>
```

**How it works:**
1. Initial value: `count = 0`
2. User clicks button
3. `setCount(count + 1)` is called → `count = 1`
4. **React automatically re-renders the component**
5. Button now shows "Clicked 1 times"

**In your app:**
```javascript
const [appointments, setAppointments] = useState([]);

// Load appointments
fetch('/api/appointments')
  .then(res => res.json())
  .then(data => setAppointments(data));  // This updates the page!
```

### 4. **Effects** (Side effects like API calls)

```javascript
useEffect(() => {
  // This runs when component loads
  loadAppointments();
}, []); // Empty array = run only once
```

**Common uses:**
- Fetch data from API when page loads
- Set up timers
- Subscribe to events

### 5. **Props** (Passing data to components)

```javascript
// Parent component
<Navbar user={currentUser} onLogout={handleLogout} />

// Navbar component receives props
function Navbar({ user, onLogout }) {
  return (
    <div>
      <span>{user.fullName}</span>
      <button onClick={onLogout}>Logout</button>
    </div>
  );
}
```

Props = parameters for components

---

# PART 4: JavaScript Crash Course

You know Java, so I'll teach JavaScript through comparisons.

## Variables

**Java:**
```java
String name = "John";
int age = 25;
final String CONSTANT = "value";
```

**JavaScript:**
```javascript
let name = "John";        // Can change (like normal variable)
const age = 25;           // Cannot change (like final)
var oldWay = "avoid";     // Old way, don't use

// JavaScript is dynamically typed
let x = 5;      // x is a number
x = "hello";    // Now x is a string - this is OK!
```

## Functions

**Java:**
```java
public String greet(String name) {
    return "Hello, " + name;
}
```

**JavaScript (3 ways):**

```javascript
// 1. Classic function
function greet(name) {
    return "Hello, " + name;
}

// 2. Arrow function (modern)
const greet = (name) => {
    return "Hello, " + name;
};

// 3. Arrow function (shortened)
const greet = (name) => "Hello, " + name;
```

All three do the same thing!

## Objects

**Java:**
```java
class User {
    private String email;
    private String name;
    
    public User(String email, String name) {
        this.email = email;
        this.name = name;
    }
    
    public String getEmail() { return email; }
}

User user = new User("test@test.com", "John");
System.out.println(user.getEmail());
```

**JavaScript:**
```javascript
// Objects are just key-value pairs
const user = {
    email: "test@test.com",
    name: "John"
};

console.log(user.email);  // Access with dot
console.log(user['email']); // Or brackets
```

## Arrays

**Java:**
```java
String[] names = {"John", "Jane", "Bob"};

for (String name : names) {
    System.out.println(name);
}
```

**JavaScript:**
```javascript
const names = ["John", "Jane", "Bob"];

// For loop
for (const name of names) {
    console.log(name);
}

// Map (like Java streams)
names.map(name => console.log(name));

// Filter
const jNames = names.filter(name => name.startsWith('J'));
// Result: ["John", "Jane"]
```

## Promises & Async (Important!)

This is how JavaScript handles waiting for things (like API calls).

**The problem:**
```javascript
// This DOESN'T work!
const data = fetch('/api/appointments');
console.log(data); // ❌ Prints "Promise", not the actual data
```

**Why?** `fetch()` takes time. JavaScript doesn't wait!

**Solution 1: .then()**
```javascript
fetch('/api/appointments')
  .then(response => response.json())
  .then(data => {
    console.log(data); // ✅ Now we have the data!
  });
```

**Solution 2: async/await (cleaner)**
```javascript
async function loadAppointments() {
  const response = await fetch('/api/appointments');
  const data = await response.json();
  console.log(data); // ✅ Data is here!
}
```

`await` = "Wait for this to finish before continuing"

## Destructuring (Shortcut)

**Java:**
```java
User user = getUser();
String email = user.getEmail();
String name = user.getName();
```

**JavaScript:**
```javascript
const user = getUser();
const { email, name } = user; // Extract both at once!

// Same as:
// const email = user.email;
// const name = user.name;
```

**In function parameters:**
```javascript
function Navbar({ user, onLogout }) {
  // Instead of props.user and props.onLogout
}
```

## Template Strings

**Java:**
```java
String message = "Hello, " + name + "! You have " + count + " messages.";
```

**JavaScript:**
```javascript
const message = `Hello, ${name}! You have ${count} messages.`;
```

Use backticks `` ` `` and `${}` for variables.

---

# PART 5: How Frontend and Backend Talk

## The Complete Flow

Let's trace what happens when you book an appointment:

### Step 1: User Clicks "Book Appointment"

**Dashboard.js (Frontend):**
```javascript
const handleAppointmentSubmit = async (e) => {
  e.preventDefault();  // Don't refresh page
  
  // Call API
  await appointmentService.create(appointmentForm);
};
```

### Step 2: Frontend Sends HTTP Request

**api.js (Frontend):**
```javascript
export const appointmentService = {
  create: (appointmentData) => api.post('/appointments', appointmentData)
};

// This sends:
POST http://localhost:8080/api/appointments
Headers: {
  Authorization: Bearer eyJhbGc...  // JWT token
  Content-Type: application/json
}
Body: {
  "doctorId": 1,
  "appointmentTime": "2026-01-10T10:00:00",
  "reason": "Annual checkup"
}
```

### Step 3: Backend Receives Request

**Controllers.java (Backend):**
```java
@PostMapping("/api/appointments")
public ResponseEntity<Appointment> createAppointment(
    @RequestBody AppointmentRequest request,
    Authentication authentication) {
    
    String email = authentication.getName(); // Get email from JWT
    return ResponseEntity.ok(appointmentService.createAppointment(request, email));
}
```

**What happens:**
1. Spring Security checks JWT token (is it valid?)
2. Extracts user email from token
3. Converts JSON → `AppointmentRequest` object automatically
4. Calls the service

### Step 4: Service Processes Request

**Services.java (Backend):**
```java
public Appointment createAppointment(AppointmentRequest request, String patientEmail) {
    // Find patient by email
    User patient = userRepository.findByEmail(patientEmail)
        .orElseThrow(() -> new RuntimeException("Patient not found"));
    
    // Find doctor by ID
    User doctor = userRepository.findById(request.getDoctorId())
        .orElseThrow(() -> new RuntimeException("Doctor not found"));
    
    // Create appointment object
    Appointment appointment = new Appointment();
    appointment.setPatient(patient);
    appointment.setDoctor(doctor);
    appointment.setAppointmentTime(request.getAppointmentTime());
    appointment.setReason(request.getReason());
    appointment.setStatus(Status.SCHEDULED);
    
    // Save to database
    return appointmentRepository.save(appointment);
}
```

### Step 5: Repository Saves to Database

**Repositories.java (Backend):**
```java
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    // Spring Boot generates this automatically:
    // INSERT INTO appointments (patient_id, doctor_id, appointment_time, ...)
    // VALUES (?, ?, ?, ...)
}
```

### Step 6: Backend Sends Response

**Spring Boot automatically converts Java object → JSON:**
```json
{
  "id": 3,
  "patient": {
    "id": 2,
    "email": "patient@medconnect.com",
    "fullName": "John Smith"
  },
  "doctor": {
    "id": 1,
    "email": "doctor@medconnect.com",
    "fullName": "Dr. Sarah Johnson",
    "specialization": "General Medicine"
  },
  "appointmentTime": "2026-01-10T10:00:00",
  "status": "SCHEDULED",
  "reason": "Annual checkup"
}
```

### Step 7: Frontend Receives Response

**Dashboard.js (Frontend):**
```javascript
const handleAppointmentSubmit = async (e) => {
  e.preventDefault();
  
  try {
    await appointmentService.create(appointmentForm);
    setMessage({ type: 'success', text: 'Appointment booked!' });
    loadAppointments(); // Refresh the list
  } catch (err) {
    setMessage({ type: 'error', text: 'Failed to book' });
  }
};
```

### Step 8: Page Updates Automatically

React sees state changed (`setMessage`, `loadAppointments`) and re-renders the component to show the new appointment!

## CORS (Why localhost:3000 can talk to localhost:8080)

**Problem:** Browsers block requests from one domain to another (security).

**Solution in SecurityConfig.java:**
```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
    // "I allow requests from localhost:3000"
    
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
    // "These HTTP methods are OK"
    
    return source;
}
```

This tells the backend: "It's OK if localhost:3000 sends requests to me."

---

# PART 6: Your Code Explained

Now let's go through YOUR actual code files, line by line.

## Backend Files

### 1. MedConnectApplication.java

```java
package com.medconnect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MedConnectApplication {
    public static void main(String[] args) {
        SpringApplication.run(MedConnectApplication.class, args);
    }
}
```

**Line by line:**

**Line 1:** `package com.medconnect;`
- All your backend code is in the "com.medconnect" package

**Line 3-4:** Imports
- `SpringApplication` = Main Spring Boot class
- `SpringBootApplication` = Annotation that enables auto-configuration

**Line 6:** `@SpringBootApplication`
- This ONE annotation does:
  1. `@Configuration` - This class has configuration
  2. `@EnableAutoConfiguration` - Spring Boot auto-configures everything
  3. `@ComponentScan` - Look for other classes with annotations

**Line 7-11:** Main method
- `SpringApplication.run()` starts the entire application
- It:
  1. Starts Tomcat web server on port 8080
  2. Scans for all classes with annotations (@Controller, @Service, etc.)
  3. Sets up database connection
  4. Configures security
  5. Makes your app ready to receive HTTP requests

**When you run this, you see:**
```
Started MedConnectApplication in 2.003 seconds
✓ Default users created
```

---

### 2. Models.java (Database Entities)

```java
package com.medconnect;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

// User Entity
@Data
@Entity
@Table(name = "users")
class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false)
    private String fullName;
    
    @Column(nullable = false)
    private String phone;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
    
    private String specialization;
    private String licenseNumber;
    
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    public enum Role {
        PATIENT, DOCTOR, ADMIN
    }
}
```

**Key annotations:**

**`@Data`** (Lombok)
- Automatically generates:
  - `getId()`, `setId()`, `getEmail()`, `setEmail()`, etc.
  - `equals()`, `hashCode()`, `toString()`
- You don't have to write these!

**`@Entity`**
- "This class represents a database table"

**`@Table(name = "users")`**
- "The table is called 'users'"

**`@Id`**
- "This field is the primary key"

**`@GeneratedValue(strategy = GenerationType.IDENTITY)`**
- "Database auto-generates this value (auto-increment)"

**`@Column(unique = true, nullable = false)`**
- `unique = true`: No two users can have same email
- `nullable = false`: Email is required (can't be null)

**`@Enumerated(EnumType.STRING)`**
- Store enum as string in database ("PATIENT", "DOCTOR")
- Without this, it would store as numbers (0, 1)

**When Spring Boot starts, it creates this SQL:**
```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    phone VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL,
    specialization VARCHAR(255),
    license_number VARCHAR(255),
    created_at TIMESTAMP NOT NULL
);
```

You never wrote this SQL - Spring Boot did it automatically!

**Appointment Entity:**

```java
@Data
@Entity
@Table(name = "appointments")
class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private User patient;
    
    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private User doctor;
    
    // ... other fields
}
```

**`@ManyToOne`**
- "Many appointments can have ONE patient"
- "Many appointments can have ONE doctor"
- This creates a foreign key relationship

**`@JoinColumn(name = "patient_id")`**
- "Store the user's ID in a column called patient_id"

**What this means:**
```
appointments table:
id | patient_id | doctor_id | ...
1  | 2          | 1         | ...
   ↓            ↓
   points to    points to
   user.id=2    user.id=1
```

When you do `appointment.getPatient()`, Spring Boot automatically fetches the full User object from the database!

---

### 3. DTOs.java (Data Transfer Objects)

```java
@Data
class LoginRequest {
    private String email;
    private String password;
}
```

**What's a DTO?**
- DTO = Data Transfer Object
- Simple container for moving data between frontend and backend
- No business logic, just data

**Why separate from Entity?**

**Bad (using Entity directly):**
```java
@PostMapping("/login")
public User login(@RequestBody User user) {
    // Problem: User has password, role, id, etc.
    // We only need email and password for login!
}
```

**Good (using DTO):**
```java
@PostMapping("/login")
public AuthResponse login(@RequestBody LoginRequest request) {
    // Clear: Only email and password expected
}
```

**Benefits:**
1. **Security** - Don't expose all User fields
2. **Clarity** - Shows exactly what data is needed
3. **Flexibility** - Can change Entity without breaking API

**AuthResponse:**
```java
@Data
@AllArgsConstructor
class AuthResponse {
    private String token;
    private Long userId;
    private String email;
    private String fullName;
    private Role role;
}
```

`@AllArgsConstructor` generates:
```java
public AuthResponse(String token, Long userId, String email, 
                    String fullName, Role role) {
    this.token = token;
    this.userId = userId;
    // ... etc
}
```

---

### 4. Repositories.java (Database Access)

```java
@Repository
interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findByRole(Role role);
}
```

**What's happening:**

**`extends JpaRepository<User, Long>`**
- `User` = Entity type
- `Long` = ID type
- Gives you FREE methods:
  - `save(user)` - INSERT or UPDATE
  - `findById(id)` - SELECT by ID
  - `findAll()` - SELECT all
  - `deleteById(id)` - DELETE

**`Optional<User> findByEmail(String email)`**
- You just DECLARE this method
- Spring Boot IMPLEMENTS it automatically!
- Generates:
  ```sql
  SELECT * FROM users WHERE email = ?
  ```

**Magic naming convention:**
- `findBy{FieldName}` → WHERE field_name = ?
- `findByEmailAndRole` → WHERE email = ? AND role = ?
- `findByCreatedAtBefore` → WHERE created_at < ?

**Optional<User> - What's this?**

**Without Optional:**
```java
User user = userRepository.findById(1);
if (user == null) {  // NullPointerException risk!
    throw new RuntimeException("Not found");
}
```

**With Optional:**
```java
User user = userRepository.findById(1)
    .orElseThrow(() -> new RuntimeException("Not found"));
```

Optional = container that might or might not have a value

**Appointment Repository:**
```java
@Repository
interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatientOrderByAppointmentTimeDesc(User patient);
}
```

**This generates:**
```sql
SELECT * FROM appointments 
WHERE patient_id = ? 
ORDER BY appointment_time DESC
```

Spring Boot reads the method name and figures it out!

---

### 5. Services.java (Business Logic)

```java
@Service
class AuthService {
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private AuthenticationManager authenticationManager;
    
    public AuthResponse register(RegisterRequest request) {
        // 1. Check if email exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }
        
        // 2. Create new user
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setRole(request.getRole());
        user.setSpecialization(request.getSpecialization());
        user.setLicenseNumber(request.getLicenseNumber());
        
        // 3. Save to database
        user = userRepository.save(user);
        
        // 4. Generate JWT token
        String token = jwtUtil.generateToken(user.getEmail());
        
        // 5. Return response
        return new AuthResponse(token, user.getId(), user.getEmail(), 
                               user.getFullName(), user.getRole());
    }
}
```

**Line by line:**

**`@Service`**
- Marks this as a service layer class
- Spring Boot creates ONE instance (singleton)

**`@Autowired`**
- "Spring, give me instances of these"
- Dependency Injection - Spring manages these objects

**`passwordEncoder.encode(request.getPassword())`**
- Takes plain password: "patient123"
- Returns encrypted: "$2a$10$N9qo8uLOickgx2Z..."
- Uses BCrypt algorithm
- One-way encryption (can't reverse it!)

**Why encrypt?**
```
Bad: password = "patient123"
     → If database is stolen, hacker sees password

Good: password = "$2a$10$encrypted..."
     → Hacker can't figure out original password
```

**How login works with encrypted passwords:**
```java
public AuthResponse login(LoginRequest request) {
    // 1. Spring Security checks:
    //    - Find user by email
    //    - Compare BCrypt hash of typed password with stored hash
    //    - If match → success, else → fail
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.getEmail(), 
            request.getPassword()
        )
    );
    
    // 2. Get user from database
    User user = userRepository.findByEmail(request.getEmail())
        .orElseThrow(() -> new RuntimeException("User not found"));
    
    // 3. Generate token
    String token = jwtUtil.generateToken(user.getEmail());
    
    // 4. Return response
    return new AuthResponse(token, user.getId(), user.getEmail(), 
                           user.getFullName(), user.getRole());
}
```

**AppointmentService:**

```java
public Appointment createAppointment(AppointmentRequest request, String patientEmail) {
    // 1. Find patient
    User patient = userRepository.findByEmail(patientEmail)
        .orElseThrow(() -> new RuntimeException("Patient not found"));
    
    // 2. Find doctor
    User doctor = userRepository.findById(request.getDoctorId())
        .orElseThrow(() -> new RuntimeException("Doctor not found"));
    
    // 3. Create appointment
    Appointment appointment = new Appointment();
    appointment.setPatient(patient);
    appointment.setDoctor(doctor);
    appointment.setAppointmentTime(request.getAppointmentTime());
    appointment.setReason(request.getReason());
    appointment.setStatus(Status.SCHEDULED);
    
    // 4. Save
    return appointmentRepository.save(appointment);
}
```

**Why not let Controller do this?**

**Bad:**
```java
@PostMapping("/appointments")
public Appointment create(@RequestBody AppointmentRequest request) {
    User patient = userRepository.findByEmail(...);
    User doctor = userRepository.findById(...);
    Appointment apt = new Appointment();
    // ... 10 more lines
    return appointmentRepository.save(apt);
}
```

All business logic in Controller = messy!

**Good:**
```java
@PostMapping("/appointments")
public Appointment create(@RequestBody AppointmentRequest request, 
                         Authentication auth) {
    return appointmentService.createAppointment(request, auth.getName());
}
```

Controller stays clean, Service has all the logic.

---

### 6. Controllers.java (REST Endpoints)

```java
@RestController
@RequestMapping("/api/auth")
class AuthController {
    @Autowired private AuthService authService;
    
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
```

**`@RestController`**
- "This class handles HTTP requests and returns JSON"
- Combination of `@Controller` + `@ResponseBody`

**`@RequestMapping("/api/auth")`**
- "All methods in this class start with /api/auth"

**`@PostMapping("/register")`**
- "Handle POST requests to /api/auth/register"

**`@RequestBody RegisterRequest request`**
- "Convert JSON from request body → RegisterRequest object"

**Example:**
```
Frontend sends:
POST /api/auth/register
Body: { "email": "test@test.com", "password": "pass123", ... }

Spring Boot automatically:
1. Receives HTTP request
2. Reads JSON from body
3. Creates RegisterRequest object
4. Fills in fields from JSON
5. Calls register(request)
```

**`ResponseEntity.ok(...)`**
- Returns HTTP 200 (OK) status
- Converts AuthResponse object → JSON

**Response:**
```
HTTP/1.1 200 OK
Content-Type: application/json

{
  "token": "eyJhbGc...",
  "userId": 3,
  "email": "test@test.com",
  "fullName": "Test User",
  "role": "PATIENT"
}
```

**AppointmentController:**

```java
@RestController
@RequestMapping("/api/appointments")
class AppointmentController {
    @Autowired private AppointmentService appointmentService;
    
    @PostMapping
    public ResponseEntity<Appointment> createAppointment(
            @RequestBody AppointmentRequest request,
            Authentication authentication) {
        
        String email = authentication.getName();
        return ResponseEntity.ok(
            appointmentService.createAppointment(request, email)
        );
    }
}
```

**`Authentication authentication`**
- Spring Security automatically provides this
- Contains info about logged-in user
- `authentication.getName()` returns email from JWT token

**How does it know who's logged in?**

1. Frontend sends:
   ```
   POST /api/appointments
   Authorization: Bearer eyJhbGc...
   ```

2. JwtAuthFilter (we'll see next) extracts email from token

3. Spring Security creates Authentication object

4. Controller receives it as parameter

---

### 7. SecurityComponents.java (JWT & Security)

This file has 3 classes. Let's break them down:

#### **JwtUtil - Creates and validates tokens**

```java
@Component
class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.expiration}")
    private Long expiration;
    
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
    
    public String generateToken(String email) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, email);
    }
    
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
}
```

**What's a JWT?**

JWT = JSON Web Token

Structure: `header.payload.signature`

Example:
```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.
eyJzdWIiOiJwYXRpZW50QG1lZGNvbm5lY3QuY29tIiwiaWF0IjoxNjQwOTk1MjAwLCJleHAiOjE2NDA5OTUyMDB9.
SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c
```

Decoded:
```json
Header: {
  "alg": "HS256",
  "typ": "JWT"
}

Payload: {
  "sub": "patient@medconnect.com",  // Subject (user email)
  "iat": 1640995200,                 // Issued at
  "exp": 1641081600                  // Expires at
}

Signature: Created using secret key
```

**How it works:**

**Creating token:**
1. Take header + payload
2. Sign with secret key → creates signature
3. Combine: `header.payload.signature`

**Validating token:**
1. Split token into parts
2. Recreate signature using header + payload + secret key
3. Compare with provided signature
4. If match → token is valid
5. If different → token was tampered with!

**Why is this secure?**
- Without secret key, you can't create valid signature
- If someone changes payload, signature won't match
- Like a tamper-proof seal

**`@Value("${jwt.secret}")`**
- Reads from `application.properties`:
  ```
  jwt.secret=medconnect-secret-key-...
  jwt.expiration=86400000
  ```
- 86400000 ms = 24 hours

#### **CustomUserDetailsService - Loads user for Spring Security**

```java
@Service
class CustomUserDetailsService implements UserDetailsService {
    @Autowired private UserRepository userRepository;
    
    @Override
    public UserDetails loadUserByUsername(String email) 
            throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(
                    new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
                )
        );
    }
}
```

**What's UserDetailsService?**
- Interface Spring Security requires
- Tells Spring Security how to load user from database

**When is this called?**
- During login
- When validating JWT token

**What it returns:**
```java
UserDetails = {
    username: "patient@medconnect.com",
    password: "$2a$10$encrypted...",
    authorities: ["ROLE_PATIENT"]
}
```

**Authorities = Roles**
- `ROLE_PATIENT` → can access patient endpoints
- `ROLE_DOCTOR` → can access doctor endpoints

#### **JwtAuthFilter - Checks token on every request**

```java
@Component
class JwtAuthFilter extends OncePerRequestFilter {
    @Autowired private JwtUtil jwtUtil;
    @Autowired private CustomUserDetailsService userDetailsService;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   FilterChain filterChain)
            throws ServletException, IOException {
        
        // 1. Get Authorization header
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String email = null;
        
        // 2. Extract token
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);  // Remove "Bearer "
            email = jwtUtil.extractEmail(token);
        }
        
        // 3. Validate token
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            
            if (jwtUtil.validateToken(token, userDetails.getUsername())) {
                // 4. Set authentication
                UsernamePasswordAuthenticationToken authToken = 
                    new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource()
                                         .buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        
        // 5. Continue to next filter/controller
        filterChain.doFilter(request, response);
    }
}
```

**What's a Filter?**
- Runs BEFORE every request reaches Controller
- Like a security checkpoint at airport

**Request flow:**
```
Browser → JwtAuthFilter → SecurityConfig → Controller
```

**What this filter does:**

1. **Get token from header**
   ```
   Authorization: Bearer eyJhbGc...
   ```

2. **Extract email from token**
   - Decode JWT
   - Read "sub" field

3. **Validate token**
   - Check signature
   - Check expiration
   - Check email matches

4. **Set authentication**
   - Tell Spring Security "This user is logged in"
   - Now Controller can access `Authentication` parameter

5. **Continue request**
   - Pass to next filter or controller

**If token is invalid:**
- Don't set authentication
- Controller sees unauthenticated request
- Spring Security blocks it (401 Unauthorized)

---

### 8. SecurityConfig.java (Security Rules)

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Autowired
    private JwtAuthFilter jwtAuthFilter;
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) 
            throws Exception {
        http.csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}
```

**Line by line:**

**`@Configuration`**
- "This class configures Spring Boot"

**`@EnableWebSecurity`**
- "Enable Spring Security"

**`csrf.disable()`**
- CSRF = Cross-Site Request Forgery protection
- We disable because we use JWT tokens
- JWT is already secure against CSRF

**`cors.configurationSource(...)`**
- Enables CORS (Cross-Origin Resource Sharing)
- Allows localhost:3000 to call localhost:8080

**`.requestMatchers("/api/auth/**").permitAll()`**
- "/api/auth/login" → Allow without login
- "/api/auth/register" → Allow without login
- Makes sense - can't login if you need to be logged in first!

**`.anyRequest().authenticated()`**
- All OTHER requests require authentication
- "/api/appointments" → need JWT token
- "/api/health-records" → need JWT token

**`.sessionManagement(STATELESS)`**
- Don't use sessions (cookies)
- Use JWT tokens instead
- Each request is independent

**`.addFilterBefore(jwtAuthFilter, ...)`**
- Run JwtAuthFilter BEFORE other filters
- Check token before anything else

**CORS Configuration:**

```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
    configuration.setAllowedHeaders(Arrays.asList("*"));
    configuration.setAllowCredentials(true);
    
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
}
```

**What's CORS?**

**Without CORS:**
```
Frontend (localhost:3000): "Can I get /api/appointments?"
Backend (localhost:8080): "No! You're from a different origin!"
```

**With CORS:**
```
Frontend: "Can I get /api/appointments?"
Backend: "Let me check my allowed origins... localhost:3000 is OK! Here you go."
```

**Why needed?**
- Browser security blocks cross-origin requests by default
- localhost:3000 ≠ localhost:8080 (different ports)
- CORS tells browser "It's OK, I allow this"

**Password Encoder:**

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

Creates a BCrypt encoder that Spring Security uses to:
1. Hash passwords when registering
2. Verify passwords when logging in

---

### 9. DataInitializer.java (Seed Data)

```java
@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            User doctor = new User();
            doctor.setEmail("doctor@medconnect.com");
            doctor.setPassword(passwordEncoder.encode("doctor123"));
            doctor.setFullName("Dr. Sarah Johnson");
            doctor.setPhone("555-0101");
            doctor.setRole(Role.DOCTOR);
            doctor.setSpecialization("General Medicine");
            doctor.setLicenseNumber("MD123456");
            userRepository.save(doctor);
            
            User patient = new User();
            patient.setEmail("patient@medconnect.com");
            patient.setPassword(passwordEncoder.encode("patient123"));
            patient.setFullName("John Smith");
            patient.setPhone("555-0102");
            patient.setRole(Role.PATIENT);
            userRepository.save(patient);
            
            System.out.println("✓ Default users created");
        }
    }
}
```

**`implements CommandLineRunner`**
- Interface that says "Run this when app starts"
- `run()` method executes after Spring Boot finishes starting

**What it does:**
1. Check if database is empty (`count() == 0`)
2. If empty, create 2 default users
3. Print confirmation message

**When you see this in console:**
```
✓ Default users created
```

It means this ran successfully!

**Why useful?**
- Fresh database? Automatically has test users
- Don't need to register manually
- Can test app immediately

---

## Frontend Files

### 1. index.js (Entry Point)

```javascript
import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import App from './App';

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);
```

**What this does:**
1. Find `<div id="root"></div>` in index.html
2. Render App component inside it
3. Everything starts from `<App />`

**`<React.StrictMode>`**
- Development mode checks
- Warns about potential problems
- Doesn't affect production

---

### 2. App.js (Routing & Auth State)

```javascript
import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Auth from './Auth';
import Dashboard from './Dashboard';
import Navbar from './Navbar';

function App() {
  const [user, setUser] = useState(null);

  useEffect(() => {
    const token = localStorage.getItem('token');
    const userData = localStorage.getItem('user');
    if (token && userData) {
      setUser(JSON.parse(userData));
    }
  }, []);

  const handleLogin = (userData) => {
    setUser(userData);
    localStorage.setItem('user', JSON.stringify(userData));
  };

  const handleLogout = () => {
    setUser(null);
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  };

  return (
    <Router>
      <div className="App">
        {user && <Navbar user={user} onLogout={handleLogout} />}
        <Routes>
          <Route
            path="/auth"
            element={!user ? <Auth onLogin={handleLogin} /> : <Navigate to="/" />}
          />
          <Route
            path="/"
            element={user ? <Dashboard user={user} /> : <Navigate to="/auth" />}
          />
        </Routes>
      </div>
    </Router>
  );
}
```

**Line by line:**

**`const [user, setUser] = useState(null);`**
- `user` = current user data (null if not logged in)
- `setUser` = function to change user
- `useState(null)` = initial value is null

**`useEffect(() => {...}, [])`**
- Runs once when app loads
- Empty array `[]` = "run only once"
- Checks localStorage for saved login

**localStorage?**
- Browser storage that persists even after closing browser
- Like saving to a file
- Key-value pairs

**Why check localStorage?**
```
1. User logs in → save to localStorage
2. User refreshes page → app reloads
3. Without localStorage → user is logged out!
4. With localStorage → load saved data, stay logged in
```

**`<Router>`**
- Enables routing (different pages)
- `/auth` = login page
- `/` = dashboard

**`<Routes>`**
- Defines which component shows for which URL

**`<Route path="/auth" element={...} />`**
- When URL is /auth, show Auth component
- But only if user is NOT logged in
- If logged in, redirect to `/`

**`!user ? <Auth /> : <Navigate to="/" />`**
- Ternary operator (like if-else)
- If user is null → show Auth
- If user exists → redirect to home

**Protection:**
```
Logged out + visit /     → Redirects to /auth
Logged in  + visit /auth → Redirects to /
```

You can't access dashboard without logging in!

---

### 3. api.js (Backend Communication)

```javascript
import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);
```

**Axios?**
- JavaScript library for HTTP requests
- Easier than native `fetch()`

**`api.create({...})`**
- Creates reusable HTTP client
- All requests go to `http://localhost:8080/api`

**`interceptors.request.use`**
- Runs BEFORE every request
- Adds Authorization header automatically

**How it works:**
```javascript
// You write:
api.get('/appointments')

// Interceptor adds token:
GET http://localhost:8080/api/appointments
Headers: {
  Content-Type: application/json,
  Authorization: Bearer eyJhbGc...
}
```

**Service functions:**

```javascript
export const authService = {
  login: (email, password) => api.post('/auth/login', { email, password }),
  register: (userData) => api.post('/auth/register', userData),
};
```

**Usage:**
```javascript
const response = await authService.login('patient@medconnect.com', 'patient123');
// Sends POST to /api/auth/login
```

Clean and reusable!

---

### 4. Auth.js (Login & Register)

```javascript
function Auth({ onLogin }) {
  const [isLogin, setIsLogin] = useState(true);
  const [formData, setFormData] = useState({
    email: '',
    password: '',
    fullName: '',
    // ... other fields
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
```

**State variables:**
- `isLogin` - true = show login, false = show register
- `formData` - all form fields
- `error` - error message to display
- `loading` - true while waiting for response

**Form handling:**

```javascript
const handleChange = (e) => {
  setFormData({
    ...formData,
    [e.target.name]: e.target.value,
  });
};
```

**`...formData`** = spread operator
- Copies all existing fields
- Then overwrites one field

Example:
```javascript
formData = { email: 'test@test.com', password: '' }

User types in password field:
handleChange({ target: { name: 'password', value: 'pass123' }})

Result:
formData = { email: 'test@test.com', password: 'pass123' }
```

**Submit handling:**

```javascript
const handleSubmit = async (e) => {
  e.preventDefault();  // Don't refresh page!
  setError('');
  setLoading(true);

  try {
    const response = isLogin
      ? await authService.login(formData.email, formData.password)
      : await authService.register(formData);
    
    localStorage.setItem('token', response.data.token);
    onLogin(response.data);
  } catch (err) {
    setError(err.response?.data?.message || 'Failed');
  } finally {
    setLoading(false);
  }
};
```

**`e.preventDefault()`**
- Stops default form behavior (page refresh)
- Lets us handle it with JavaScript

**`try-catch-finally`**
```javascript
try {
  // Try this code
} catch (err) {
  // If error, run this
} finally {
  // Always run this (even if error)
}
```

**`response.data`**
```javascript
response = {
  status: 200,
  data: {
    token: "abc123...",
    userId: 2,
    email: "patient@medconnect.com",
    // ...
  }
}

// Access with: response.data.token
```

**Conditional rendering:**

```javascript
{!isLogin && (
  <div className="form-group">
    <label>Full Name</label>
    <input ... />
  </div>
)}
```

**`&&` operator:**
- If `!isLogin` is false → don't render anything
- If `!isLogin` is true → render the div

---

### 5. Dashboard.js (Patient & Doctor Views)

This is the biggest file. Let's break it down:

**State:**

```javascript
const isPatient = user.role === 'PATIENT';
const [activeTab, setActiveTab] = useState('appointments');
const [appointments, setAppointments] = useState([]);
const [healthRecords, setHealthRecords] = useState([]);
const [doctors, setDoctors] = useState([]);
const [loading, setLoading] = useState(false);
const [showModal, setShowModal] = useState(null);
const [message, setMessage] = useState({ type: '', text: '' });
```

**Loading data on mount:**

```javascript
useEffect(() => {
  loadAppointments();
  if (isPatient) {
    loadHealthRecords();
    loadDoctors();
  }
}, []);
```

**`[]` dependency array:**
- Empty = run once when component mounts
- With dependencies: run when those change

**Loading appointments:**

```javascript
const loadAppointments = async () => {
  try {
    const response = isPatient
      ? await appointmentService.getPatientAppointments()
      : await appointmentService.getDoctorAppointments();
    setAppointments(response.data);
  } catch (err) {
    console.error('Error loading appointments:', err);
  }
};
```

**Flow:**
1. Call API
2. Get response
3. Update state with `setAppointments`
4. React automatically re-renders component
5. New appointments appear!

**Creating appointment:**

```javascript
const handleAppointmentSubmit = async (e) => {
  e.preventDefault();
  setLoading(true);
  
  try {
    await appointmentService.create(appointmentForm);
    setMessage({ type: 'success', text: 'Appointment booked!' });
    setShowModal(null);
    setAppointmentForm({ doctorId: '', appointmentTime: '', reason: '' });
    loadAppointments();
  } catch (err) {
    setMessage({ type: 'error', text: 'Failed to book' });
  } finally {
    setLoading(false);
  }
};
```

**Rendering appointments:**

```javascript
{appointments.map(apt => (
  <div key={apt.id} className="appointment-item">
    <h4>Dr. {apt.doctor.fullName}</h4>
    <p><strong>Date & Time:</strong> {formatDate(apt.appointmentTime)}</p>
    <p><strong>Reason:</strong> {apt.reason}</p>
    <p>
      <span className={`status-badge status-${apt.status.toLowerCase()}`}>
        {apt.status}
      </span>
    </p>
  </div>
))}
```

**`.map()` function:**
```javascript
[1, 2, 3].map(x => x * 2)  // Result: [2, 4, 6]

appointments = [apt1, apt2, apt3]
appointments.map(apt => <div>...</div>)
// Result: [<div>apt1</div>, <div>apt2</div>, <div>apt3</div>]
```

React renders all the divs!

**`key={apt.id}`**
- Required for lists
- Helps React track which items changed
- Use unique ID

**Conditional rendering:**

```javascript
{isPatient ? (
  <PatientView />
) : (
  <DoctorView />
)}
```

Shows different UI based on role!

**Modal for booking:**

```javascript
{showModal === 'appointment' && (
  <div className="modal">
    <div className="modal-content">
      <h3>Book Appointment</h3>
      <form onSubmit={handleAppointmentSubmit}>
        {/* Form fields */}
      </form>
    </div>
  </div>
)}
```

**`showModal === 'appointment'`:**
- If true → show modal
- If false/null → hide modal

---

### 6. Navbar.js (Simple Component)

```javascript
function Navbar({ user, onLogout }) {
  return (
    <div className="navbar">
      <h1>MedConnect</h1>
      <div className="navbar-right">
        <span>Welcome, {user.fullName}</span>
        <button onClick={onLogout} className="btn btn-secondary">
          Logout
        </button>
      </div>
    </div>
  );
}
```

**Destructuring props:**
```javascript
// Instead of:
function Navbar(props) {
  return <span>{props.user.fullName}</span>;
}

// We use:
function Navbar({ user, onLogout }) {
  return <span>{user.fullName}</span>;
}
```

Cleaner!

**`onClick={onLogout}`**
- When button clicked, call `onLogout` function
- `onLogout` comes from App.js as a prop
- Clears localStorage and sets user to null

---

### 7. index.css (Styles)

CSS classes used throughout:

```css
.btn-primary {
  background-color: #2563eb;
  color: white;
}

.btn-primary:hover {
  background-color: #1d4ed8;
}
```

**`:hover`** = when mouse over button

**Class names match component usage:**
```javascript
<button className="btn btn-primary">Book</button>
```

Applies both `.btn` and `.btn-primary` styles.

---

# PART 7: Interview Preparation

## The 30-Second Pitch

"I built MedConnect, a full-stack web application for managing patient appointments and health records. Patients can book appointments with doctors, upload medical documents, and track their health history. Doctors can view their schedule, complete appointments with clinical notes, and access patient information.

I used Spring Boot for the backend to handle REST APIs, database operations, and JWT authentication. The frontend is built with React for a responsive user interface. PostgreSQL stores all the data with proper relationships between users, appointments, and health records.

The app demonstrates my understanding of full-stack development, including security best practices like password encryption and token-based authentication, RESTful API design, and modern frontend development with React hooks and state management."

## Common Questions & Answers

### Q: "Walk me through how the login process works."

**Answer:**
"When a user enters their email and password on the frontend, React sends a POST request to /api/auth/login with the credentials in JSON format. 

On the backend, Spring Security's AuthenticationManager validates the credentials by checking the encrypted password in the database using BCrypt. If valid, my AuthService generates a JWT token containing the user's email and role.

The backend returns this token along with user details to the frontend. React stores the token in localStorage and includes it in the Authorization header of all subsequent requests.

For protected endpoints, my JwtAuthFilter intercepts every request, validates the token's signature and expiration, and sets the authentication context so Spring Security knows who's logged in. This stateless approach means the server doesn't need to maintain sessions."

### Q: "Why did you use JWT instead of sessions?"

**Answer:**
"I chose JWT for several reasons:

First, it's stateless - the backend doesn't need to store session data, which makes it easier to scale horizontally if needed.

Second, JWTs are self-contained - all the information needed to verify the user is in the token itself, so I don't need to query the database on every request.

Third, JWTs work well for APIs and can be used across different domains if needed, unlike cookies which have same-origin restrictions.

The tradeoff is that you can't easily revoke a JWT before it expires, but for this application where tokens expire in 24 hours, that's acceptable."

### Q: "How does your database schema work?"

**Answer:**
"I have three main tables: users, appointments, and health_records.

The users table stores both patients and doctors, distinguished by a 'role' field. This simplified the auth logic rather than having separate tables.

The appointments table has foreign keys to the users table - one for patient_id and one for doctor_id. This creates a many-to-one relationship where one user can have multiple appointments as either a patient or doctor.

The health_records table links to patients through patient_id. I stored file metadata like filename and path rather than the actual file in the database for better performance.

I used JPA annotations like @ManyToOne and @JoinColumn to define these relationships, and Spring Boot's Hibernate automatically creates the proper foreign key constraints and joins."

### Q: "What's the difference between @Service and @Controller?"

**Answer:**
"They're both Spring stereotypes but serve different purposes in the MVC pattern.

@Controller classes handle HTTP requests - they receive requests, call services, and return responses. They focus on web layer concerns like parsing request bodies and setting HTTP status codes.

@Service classes contain business logic - validation, data transformation, and coordinating between repositories. They're independent of the web layer, which makes them easier to test and reuse.

For example, my AppointmentController receives a POST request and passes data to AppointmentService. The service validates that the doctor exists, checks availability, creates the appointment object, and saves it. This separation makes the code more maintainable and follows SOLID principles."

### Q: "How did you handle security?"

**Answer:**
"I implemented multiple security layers:

First, passwords are hashed using BCrypt before storage, so even if the database is compromised, passwords can't be retrieved.

Second, I use JWT tokens for authentication. The token is signed with a secret key, so it can't be tampered with.

Third, I configured Spring Security to protect all endpoints except login and registration. The JwtAuthFilter validates tokens on every request.

Fourth, I enabled CORS only for localhost:3000 to prevent unauthorized domains from calling the API.

Fifth, I use role-based access control - patients can only see their own data, and doctors have different permissions.

For a production app, I'd add rate limiting, HTTPS, and more robust error handling."

### Q: "What would you improve if you had more time?"

**Answer:**
"Several things:

First, I'd add email notifications for appointment reminders and confirmations using something like SendGrid.

Second, I'd implement more robust error handling with custom exception classes and global exception handlers instead of generic RuntimeExceptions.

Third, I'd add proper logging with SLF4J to track issues and monitor usage.

Fourth, I'd add unit tests for services and integration tests for endpoints using JUnit and Mockito.

Fifth, I'd move the JWT secret and database credentials to environment variables instead of application.properties.

Finally, I'd add pagination for the appointments list and implement proper file download functionality for health records rather than just storing the path."

### Q: "How did you handle file uploads?"

**Answer:**
"I used Spring Boot's MultipartFile for file uploads. When a patient uploads a health record, the frontend sends a multipart/form-data request instead of JSON.

On the backend, the HealthRecordService receives the file, generates a unique filename using UUID to prevent collisions, and saves it to a local 'uploads' directory.

I store the filename and filepath in the database but not the actual file content. This keeps the database performant and makes it easy to move files to cloud storage later like AWS S3.

For a production app, I'd add file type validation, virus scanning, and use cloud storage instead of local filesystem."

### Q: "Explain how React's useState works."

**Answer:**
"useState is a React Hook that lets you add state to functional components.

When you call useState with an initial value, it returns an array with two elements: the current state value and a function to update it.

For example: const [count, setCount] = useState(0)

The key thing is that when you call setCount with a new value, React doesn't just update the variable - it triggers a re-render of the component. React compares the new virtual DOM with the old one and updates only what changed in the actual DOM.

This is why in my Dashboard, when I call setAppointments with new data after fetching from the API, the appointment list automatically updates on screen. I don't have to manually manipulate the DOM."

### Q: "What's the difference between useEffect with empty array vs with dependencies?"

**Answer:**
"useEffect with an empty dependency array runs once when the component mounts, similar to componentDidMount in class components.

useEffect with dependencies runs whenever any of those dependencies change. For example:

useEffect(() => {
  loadData();
}, [userId])

This would run on mount AND whenever userId changes.

In my app, I use an empty array in Dashboard to load appointments when the page first loads. If I wanted to reload appointments when a filter changes, I'd add that filter to the dependency array.

Without any array at all, useEffect runs after every render, which usually causes infinite loops and should be avoided."

### Q: "How would you add a new feature - appointment search?"

**Answer:**
"I'd approach it systematically:

First, on the backend, I'd add a new method to AppointmentRepository:
List<Appointment> findByPatientAndReasonContainingIgnoreCase(User patient, String search)

Spring Data JPA would generate the query automatically.

Then I'd add a method to AppointmentService that calls this repository method.

In AppointmentController, I'd add a GET endpoint:
@GetMapping('/search')
public List<Appointment> search(@RequestParam String query)

On the frontend, I'd add a search input in Dashboard that updates state on change. I'd use useEffect to call the API when the search term changes, with debouncing to avoid too many requests.

Finally, I'd filter the displayed appointments based on the search results.

Total time: probably 30-45 minutes for a basic implementation."

## Technical Terms to Know

**Spring Boot:**
- Framework for building Java web applications
- Auto-configures most boilerplate

**JPA (Java Persistence API):**
- Specification for working with databases
- Hibernate is the implementation we use

**REST (Representational State Transfer):**
- Architectural style for APIs
- Uses HTTP methods (GET, POST, PUT, DELETE)

**JWT (JSON Web Token):**
- Self-contained authentication token
- Contains user info and signature

**BCrypt:**
- Password hashing algorithm
- One-way encryption (can't reverse)

**CORS (Cross-Origin Resource Sharing):**
- Security feature that restricts which domains can call your API

**React:**
- JavaScript library for building UIs
- Component-based architecture

**JSX:**
- Syntax extension that looks like HTML in JavaScript

**State:**
- Data that can change over time in React components

**Props:**
- Data passed from parent to child components

**Hook:**
- Function that lets you use React features (useState, useEffect)

**Axios:**
- JavaScript HTTP client library

**localStorage:**
- Browser storage that persists across sessions

## Red Flags to Avoid

**Don't say:**
- "I followed a tutorial" (even if you did, explain you understood and modified it)
- "I don't know why it works, it just does"
- "Spring Boot does everything automatically" (know what it does)
- "I didn't write the repository methods" (explain Spring Data JPA)

**Do say:**
- "I researched different approaches and chose X because..."
- "I understand it works this way... [explain]"
- "Spring Boot auto-configures X, which saves time on Y"
- "Spring Data JPA generates these methods based on naming conventions"

## What Interviewers Want to Hear

1. **You understand the fundamentals** - not just that it works
2. **You can explain tradeoffs** - why JWT vs sessions, etc.
3. **You know what you'd improve** - shows growth mindset
4. **You can debug issues** - walk through how you'd find a bug
5. **You understand the whole stack** - how frontend and backend connect

## Practice Explaining

Pick a random feature and practice explaining:
1. What it does (user perspective)
2. How it works (technical)
3. Why you built it that way (decisions)

Example: "Appointment booking"
1. Users select a doctor, pick a time, and add a reason
2. Frontend validates, sends POST request, backend checks doctor exists, creates appointment in database, returns confirmation
3. I used a modal for better UX, separated service logic from controller for maintainability, and added status field for future appointment cancellation

---

# FINAL TIPS

## Before the Interview

1. **Run the app** - make sure it works
2. **Create an appointment** - walk through the flow
3. **Look at the code** - refresh your memory
4. **Practice explaining** - out loud, to a friend

## During the Interview

1. **Start with the pitch** - 30 seconds, what it is
2. **Offer to demo** - show it working
3. **Explain architecture** - frontend, backend, database
4. **Dive into details** - when asked
5. **Be honest** - "I'd need to look that up" is OK

## Questions to Ask Them

1. "What tech stack does your team use?"
2. "How do you handle authentication in your apps?"
3. "What's your deployment process?"
4. "Do you use microservices or monolithic architecture?"

Shows you're thinking beyond just coding.

---

# YOU GOT THIS! 🚀

You built a real, working full-stack application. That's more than most students can say. Know your project, explain it confidently, and show that you understand not just WHAT you built, but WHY and HOW.

Good luck!
