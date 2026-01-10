## MedConnect Backend – Explain Like I’m 5 (But I Know Basic Java)

This guide is your **step‑by‑step script** for the interview.

- It will **tell you exactly which file to open**.
- It will explain **every important part** of the backend: web server, database, security, and APIs.
- It assumes you know **basic Java** but **no Spring Boot**.

Follow it **in order** the first time. Later you can jump around.

---

## 0. Where the backend lives

- Open the **`backend`** folder in the project.
- All main backend Java code is in:
  - `backend/src/main/java/com/medconnect`
  - `backend/src/main/resources`

We’ll walk top‑down:

1. How the app starts and what dependencies it uses.
2. How the database is configured.
3. How data models (tables) are defined.
4. How we talk to the database.
5. How business logic is written.
6. How HTTP endpoints are defined.
7. How security (JWT) works.
8. How initial data is created.

---

## 1. How the backend starts (and what libraries it uses)

### 1.1 Open `backend/pom.xml`

Think of `pom.xml` like a **shopping list of libraries** plus some project info.

Important parts:

- **Parent**: Spring Boot starter
  - Tells Maven: “Use Spring Boot 3.2.0 with its sensible defaults.”
- **Dependencies** we care about:
  - `spring-boot-starter-web` → build HTTP APIs (controllers, JSON, embedded web server).
  - `spring-boot-starter-data-jpa` → talk to the database using Java classes instead of raw SQL.
  - `spring-boot-starter-security` → authentication and authorization.
  - `spring-boot-starter-validation` → (optional) validating inputs.
  - `postgresql` → the actual PostgreSQL database driver.
  - `jjwt-*` (three dependencies) → JSON Web Token (JWT) library.
  - `lombok` → generates getters/setters/constructors so we write less boilerplate.

**ELI5 view**

- Imagine building a Lego set.
- `pom.xml` says:
  - “I’m using the **Spring Boot** Lego base.”
  - “I also need **Web**, **Database**, **Security**, **JWT**, and **Lombok** Lego kits.”

In the interview, you don’t need to recite Maven details, but you should know:

> “I used Spring Boot starters for web, data‑JPA, and security, plus the PostgreSQL driver and the `jjwt` library for JWT tokens.”

---

### 1.2 Open `backend/src/main/java/com/medconnect/MedConnectApplication.java`

This is the **entry point** – like `public static void main` in any Java console app.

What you’ll see:

- `@SpringBootApplication` on the class.
- A `main` method that calls `SpringApplication.run(...)`.

**What this does ELI5**

- In plain Java, to build a web server, you would:
  - Start Tomcat by hand.
  - Register servlets.
  - Wire up everything manually.
- With Spring Boot:
  - You just put `@SpringBootApplication` on this class.
  - You call `SpringApplication.run(...)`.
  - Spring:
    - Starts an embedded web server on port 8080.
    - Scans the `com.medconnect` package for special annotations like `@RestController`, `@Service`, `@Repository`, `@Component`.
    - Automatically wires these classes together (Dependency Injection).
    - Sets up database and security according to configuration.

**One sentence to say**

> “`MedConnectApplication` is my Spring Boot entry point; running it starts the web server and bootstraps all controllers, services, repositories, and security.”

---

## 2. How the database and JWT are configured

### 2.1 Open `backend/src/main/resources/application.properties`

This file is like a **settings sheet** for the backend.

Important lines:

- **Database**
  - `spring.datasource.url=jdbc:postgresql://localhost:5432/medconnect`
  - `spring.datasource.username=medconnect_user`
  - `spring.datasource.password=medconnect_pass`
  - `spring.jpa.hibernate.ddl-auto=update`
  - `spring.jpa.show-sql=true`

ELI5:

- “Connect to a PostgreSQL database called `medconnect` on my machine with that username/password.”
- `ddl-auto=update` means:
  - “Look at my Java entity classes and make sure the database tables match them; create or update them automatically.”
- `show-sql=true`:
  - “Print SQL queries to the console so I can see what Hibernate is doing.”

- **JWT**
  - `jwt.secret=...` → secret key used to sign JWT tokens.
  - `jwt.expiration=86400000` → how long tokens are valid (here: 24 hours).

- **File upload limits**
  - `spring.servlet.multipart.max-file-size=10MB`
  - `spring.servlet.multipart.max-request-size=10MB`

- **Server port**
  - `server.port=8080` → backend listens on port 8080.

**One sentence**

> “`application.properties` configures database connection, how JPA updates tables, JWT settings, file upload size, and the port my backend runs on.”

---

## 3. How data is represented (Entities = database tables)

### 3.1 Open `backend/src/main/java/com/medconnect/Models.java`

This file defines **three entities**: `User`, `Appointment`, and `HealthRecord`.

When you see:

- `@Entity` → “This Java class is a database table.”
- `@Table(name = "...")` → “Use this table name.”
- `@Id`, `@GeneratedValue` → “This is the primary key with auto‑increment.”
- `@ManyToOne`, `@JoinColumn` → “This column links to another table (foreign key).”
- `@Enumerated(EnumType.STRING)` → “Store enums as readable strings.”
- `@Data` (from Lombok) → “Generate getters, setters, toString, equals, hashCode.”

#### 3.1.1 `User` – open the `User` class inside `Models.java`

What it models:

- A person in the system:
  - Patient
  - Doctor
  - Admin (unused in UI today, but reserved)

Important bits:

- `@Data` – no manual getters/setters.
- `@Entity`, `@Table(name = "users")` – maps to `users` table.
- `@Id`, `@GeneratedValue(strategy = GenerationType.IDENTITY)` – `id` auto‑generated.
- `@Column(unique = true, nullable = false)` on `email` – emails must be unique and present.
- `@Enumerated(EnumType.STRING)` on `role` – store `"PATIENT"`, `"DOCTOR"`, not numbers.
- `createdAt` default set to `LocalDateTime.now()` when a new User is created.

Fields to mention:

- `id`, `email`, `password` (hashed), `fullName`, `phone`
- `role` (enum: `PATIENT`, `DOCTOR`, `ADMIN`)
- `specialization`, `licenseNumber` (for doctors)

**Analogy**

- Think of `User` as a **row** in the `users` Excel sheet with columns for each field.

#### 3.1.2 `Appointment` – open the `Appointment` class

What it models:

- A **booking** between a patient and a doctor at a specific time.

Key points:

- `@ManyToOne` to `User` for both `patient` and `doctor`.
  - Means: many appointments can share the same patient/doctor.
- `@JoinColumn(name = "patient_id")` / `doctor_id`:
  - Actual foreign key columns in the DB.
- `Status` enum: `SCHEDULED`, `COMPLETED`, `CANCELLED`.
  - Stored as string.

Fields:

- `id`
- `patient` (User)
- `doctor` (User)
- `appointmentTime` (LocalDateTime)
- `status`
- `reason` (why they booked)
- `notes` (doctor’s notes)
- `createdAt`

**Analogy**

- Think of an appointment row that **points to** two users: the patient and the doctor.

#### 3.1.3 `HealthRecord` – open the `HealthRecord` class

What it models:

- A **medical record entry** (like a lab result or prescription).

Key points:

- `@ManyToOne` to `patient` (required) and `doctor` (optional).
- `RecordType` enum: `LAB_RESULT`, `PRESCRIPTION`, `XRAY`, `REPORT`, `OTHER`.
- `fileName` and `filePath`:
  - Just metadata; the actual file is stored on disk.

Fields:

- `id`
- `patient`, `doctor`
- `title`, `description`
- `type` (enum)
- `fileName`, `filePath`
- `recordDate`, `createdAt`

**One sentence**

> “`Models.java` defines the 3 main tables of my system – users, appointments, and health records – using JPA annotations so Spring/Hibernate can create and map the database automatically.”

---

## 4. How data moves in and out of the API (DTOs)

### 4.1 Open `backend/src/main/java/com/medconnect/DTOs.java`

Here we define **simple data classes** used in HTTP requests and responses.

You’ll see:

- `LoginRequest` → what the frontend sends to log in.
  - Fields: `email`, `password`.
- `RegisterRequest` → what the frontend sends to register.
  - Fields: `email`, `password`, `fullName`, `phone`, `role`, `specialization`, `licenseNumber`.
- `AppointmentRequest` → data to create an appointment.
  - Fields: `doctorId`, `appointmentTime`, `reason`.
- `AuthResponse` → what we send back after login/register.
  - Fields: `token`, `userId`, `email`, `fullName`, `role`.

**Why not use entities here?**

- For login, we don’t need all `User` fields, just `email` and `password`.
- We also don’t want to accidentally expose internal fields.

**Analogy**

- Think of DTOs as the **envelopes** for sending data over HTTP.
- Entities are the **rows** in your internal database.

---

## 5. How we access the database (Repositories)

### 5.1 Open `backend/src/main/java/com/medconnect/Repositories.java`

This file defines **three interfaces** that extend `JpaRepository`.

ELI5:

- `JpaRepository<Entity, IdType>` is like a magical helper that says:
  - “I know how to do CRUD for this entity. Just tell me which one.”
- You **don’t** implement these interfaces; Spring generates code at runtime.

#### 5.1.1 `UserRepository`

- `extends JpaRepository<User, Long>`
  - Tells Spring: “I store `User` entities with `Long` IDs.”

Custom methods:

- `Optional<User> findByEmail(String email)`
- `List<User> findByRole(Role role)`

Spring reads these names and generates SQL like:

- `SELECT * FROM users WHERE email = ?`
- `SELECT * FROM users WHERE role = ?`

#### 5.1.2 `AppointmentRepository`

Methods:

- `findByPatientOrderByAppointmentTimeDesc(User patient)`
  - “All appointments for this patient, newest first.”
- `findByDoctorOrderByAppointmentTimeDesc(User doctor)`
- `findByDoctorAndAppointmentTimeBetween(User doctor, LocalDateTime start, LocalDateTime end)`

These method names become SQL with `WHERE` and `ORDER BY` under the hood.

#### 5.1.3 `HealthRecordRepository`

- `findByPatientOrderByRecordDateDesc(User patient)`

**One sentence**

> “My repositories are interfaces that extend `JpaRepository`; I just declare methods like `findByEmail`, and Spring Data automatically creates the SQL and implementation.”

---

## 6. How business logic is written (Services)

### 6.1 Open `backend/src/main/java/com/medconnect/Services.java`

You’ll see **three @Service classes**:

- `AuthService`
- `AppointmentService`
- `HealthRecordService`

Idea:

- **Controllers** should be thin: they just handle HTTP details.
- **Services** contain the **real rules** of the app.

#### 6.1.1 `AuthService` – login and registration

Dependencies (via `@Autowired`):

- `UserRepository` – find/create users.
- `PasswordEncoder` – to hash passwords (BCrypt).
- `JwtUtil` – to generate tokens.
- `AuthenticationManager` – to let Spring Security verify credentials.

**Method: `register(RegisterRequest request)`**

Step‑by‑step:

1. Check if email is already used with `userRepository.findByEmail(...)`.
2. Create a new `User`:
   - Copy fields from `request`.
   - Hash the password with `passwordEncoder.encode(...)`.
3. Save user to DB: `userRepository.save(user)`.
4. Generate JWT token from user’s email: `jwtUtil.generateToken(user.getEmail())`.
5. Return an `AuthResponse` with token + user info.

**Method: `login(LoginRequest request)`**

Steps:

1. Call `authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password))`.
   - This triggers Spring Security:
     - Loads user by email (`CustomUserDetailsService`).
     - Compares raw password with the stored hash using the same encoder.
2. If auth passes:
   - Find user from DB (`userRepository.findByEmail`).
   - Generate JWT token with `JwtUtil`.
   - Return `AuthResponse`.

ELI5:

- `AuthService` is the **bouncer at the club**:
  - Registration: adds new people to the list.
  - Login: checks if you’re on the list and gives you a wristband (JWT).

#### 6.1.2 `AppointmentService` – booking and listing appointments

Dependencies:

- `AppointmentRepository`
- `UserRepository`

Key methods:

- `createAppointment(AppointmentRequest request, String patientEmail)`
  - Look up the `User` who is the patient (by email).
  - Look up the doctor (by ID).
  - Create `Appointment`, set:
    - `patient`, `doctor`, `appointmentTime`, `reason`, `status = SCHEDULED`.
  - Save and return.

- `getPatientAppointments(String patientEmail)`
  - Find patient by email → call `appointmentRepository.findByPatientOrderByAppointmentTimeDesc`.

- `getDoctorAppointments(String doctorEmail)`
  - Same idea but for doctor.

- `updateAppointmentStatus(Long id, Status status, String notes)`
  - Find appointment by `id`.
  - Update `status`.
  - If `notes` is not null, set them.
  - Save and return.

- `getAllDoctors()`
  - Return `userRepository.findByRole(Role.DOCTOR)`.

ELI5:

- `AppointmentService` is the **scheduler**:
  - It knows how to create appointments, list them for patients/doctors, and update their status.

#### 6.1.3 `HealthRecordService` – health records and file storage

Dependencies:

- `HealthRecordRepository`
- `UserRepository`

Constructor:

- Checks if `uploads/health-records/` folder exists; if not, creates it.

Key methods:

- `createRecord(patientEmail, title, description, type, MultipartFile file)`
  - Find patient `User` by email.
  - Create new `HealthRecord` with those details.
  - If `file` is provided and not empty:
    - Generate unique file name: random UUID + original filename.
    - Save bytes to disk at `uploads/health-records/...`.
    - Store original filename and path in the entity.
  - Save via `healthRecordRepository.save(record)`.

- `getPatientRecords(patientEmail)`
  - Find patient and list records ordered by `recordDate`.

- `getRecordById(Long id)`
  - Return one record or throw if not found.

ELI5:

- `HealthRecordService` is like the **medical records clerk**:
  - Files documents, labels them, and lets you fetch them later.

---

## 7. How HTTP endpoints are defined (Controllers)

### 7.1 Open `backend/src/main/java/com/medconnect/Controllers.java`

Here you’ll find three `@RestController` classes:

- `AuthController` – `/api/auth/...`
- `AppointmentController` – `/api/appointments/...`
- `HealthRecordController` – `/api/health-records/...`

`@RestController` ELI5:

- “This class answers HTTP requests and sends back JSON.”

#### 7.1.1 `AuthController`

Annotations:

- `@RestController`
- `@RequestMapping("/api/auth")`

Methods:

- `@PostMapping("/register")`
  - Path: `POST /api/auth/register`
  - Parameter: `@RequestBody RegisterRequest request`
  - Returns: `ResponseEntity<AuthResponse>`
  - Behavior: calls `authService.register(request)`.

- `@PostMapping("/login")`
  - Path: `POST /api/auth/login`
  - Parameter: `@RequestBody LoginRequest request`
  - Returns: `ResponseEntity<AuthResponse>`
  - Behavior: calls `authService.login(request)`.

ELI5:

- `AuthController` is the **front desk** for auth:
  - It takes login/register forms from the internet and passes them to `AuthService`.

#### 7.1.2 `AppointmentController`

Class annotations:

- `@RestController`
- `@RequestMapping("/api/appointments")`

Key methods:

- `@PostMapping`
  - Path: `POST /api/appointments`
  - Parameters:
    - `@RequestBody AppointmentRequest request`
    - `Authentication authentication` (injected by Spring Security)
  - `authentication.getName()` gives the logged‑in user’s email (from JWT).
  - Calls `appointmentService.createAppointment(request, email)`.

- `@GetMapping("/patient")`
  - `GET /api/appointments/patient`
  - Uses `authentication.getName()` to figure out which patient.
  - Returns that patient’s appointments.

- `@GetMapping("/doctor")`
  - `GET /api/appointments/doctor`
  - Works similarly for doctors.

- `@PutMapping("/{id}/status")`
  - `PUT /api/appointments/{id}/status`
  - `@PathVariable Long id`
  - `@RequestBody Map<String, String> body` – expects `"status"` and optional `"notes"`.
  - Converts string to enum: `Status.valueOf(body.get("status"))`.
  - Calls `appointmentService.updateAppointmentStatus`.

- `@GetMapping("/doctors")`
  - `GET /api/appointments/doctors`
  - Returns list of `User`s who are doctors.

ELI5:

- `AppointmentController` is the **receptionist**:
  - “Book me an appointment,” “Show me my appointments,” “Update this appointment’s status.”

#### 7.1.3 `HealthRecordController`

Class annotations:

- `@RestController`
- `@RequestMapping("/api/health-records")`

Key methods:

- `@PostMapping`
  - Path: `POST /api/health-records`
  - Uses `@RequestParam` for text fields:
    - `title`, `description`, `type`
  - Uses `@RequestParam(value = "file", required = false) MultipartFile file` for uploads.
  - Uses `Authentication authentication` for patient email.
  - Calls `healthRecordService.createRecord(...)`.

- `@GetMapping`
  - `GET /api/health-records`
  - Returns all records for the authenticated patient.

- `@GetMapping("/{id}")`
  - `GET /api/health-records/{id}`
  - Returns a single record.

ELI5:

- `HealthRecordController` is the **records window**:
  - Handles “add a record” and “show my records” HTTP requests.

---

## 8. How security and JWT work

This has **two main files**:

1. `SecurityComponents.java` – utilities and filter.
2. `SecurityConfig.java` – the security rules.

### 8.1 Open `backend/src/main/java/com/medconnect/SecurityComponents.java`

You’ll see three classes:

1. `JwtUtil`
2. `CustomUserDetailsService`
3. `JwtAuthFilter`

#### 8.1.1 `JwtUtil` – making and checking tokens

Reads these from `application.properties`:

- `jwt.secret` – secret key to sign tokens.
- `jwt.expiration` – how long tokens last.

Key methods:

- `generateToken(String email)` – creates a JWT:
  - Subject = email.
  - Adds issued date and expiration.
  - Signs with HS256 using the secret.

- `extractEmail(String token)` – gets the subject (email) back out.

- `validateToken(String token, String email)`:
  - Checks:
    - token’s email == expected email.
    - token not expired.

ELI5:

- JWT is like a **tamper‑proof badge**:
  - `JwtUtil` prints the badge and checks if it’s still valid.

#### 8.1.2 `CustomUserDetailsService` – giving Spring Security user details

Implements `UserDetailsService`.

Method:

- `loadUserByUsername(String email)`:
  - Loads `User` from DB via `UserRepository`.
  - Wraps it into `org.springframework.security.core.userdetails.User`:
    - username = email
    - password = hashed password
    - authorities = `ROLE_` + `user.getRole().name()`, e.g. `ROLE_PATIENT`.

Used when:

- User logs in.
- Token is being validated (to load user details).

ELI5:

- This class translates your own `User` into a format Spring Security understands.

#### 8.1.3 `JwtAuthFilter` – checking tokens on every request

Extends `OncePerRequestFilter`, meaning:

- Runs at most once for each HTTP request.

Algorithm:

1. Read `Authorization` header.
2. If it starts with `"Bearer "`:
   - Strip it to get the token.
   - Use `JwtUtil.extractEmail` to get email.
3. If email is not null AND there is no authentication yet:
   - Load `UserDetails` with `CustomUserDetailsService`.
   - Validate token with `JwtUtil.validateToken`.
   - If valid:
     - Create `UsernamePasswordAuthenticationToken` with user details and authorities.
     - Attach request details.
     - Put it in `SecurityContextHolder`.
4. Call `filterChain.doFilter(request, response)` to continue.

ELI5:

- `JwtAuthFilter` is the **security guard at the door**:
  - Checks if your badge (JWT) is present and valid.
  - If yes, it tells the system “this user is logged in as X.”

---

### 8.2 Open `backend/src/main/java/com/medconnect/SecurityConfig.java`

This class configures Spring Security behavior.

Key annotations:

- `@Configuration` – this class defines beans/config.
- `@EnableWebSecurity` – turn on web security.

Important bean: `securityFilterChain(HttpSecurity http)`

What it configures:

- `csrf.disable()`:
  - CSRF is useful for cookie‑based auth.
  - You’re using JWT in headers, so you disable CSRF.

- `.cors(cors -> cors.configurationSource(corsConfigurationSource()))`:
  - Tells Spring to use your custom CORS settings (next bean).

- `authorizeHttpRequests`:
  - `.requestMatchers("/api/auth/**").permitAll()`:
    - Login and registration are public.
  - `.anyRequest().authenticated()`:
    - Everything else needs a valid JWT.

- `sessionManagement(STATELESS)`:
  - Server does **not** keep sessions.
  - Every request must bring a token.

- `.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)`:
  - Run `JwtAuthFilter` **before** the standard username/password filter.

Other beans:

- `corsConfigurationSource()`:
  - Allows requests from `http://localhost:3000`.
  - Allows methods GET/POST/PUT/DELETE/OPTIONS.
  - Allows all headers.
  - `setAllowCredentials(true)` to send cookies/headers if needed.

- `passwordEncoder()`:
  - `new BCryptPasswordEncoder()` – same encoder used for hashing and verifying passwords.

- `authenticationManager(AuthenticationConfiguration config)`:
  - Gets the `AuthenticationManager` configured by Spring (uses your `CustomUserDetailsService` and `PasswordEncoder`).

ELI5:

- `SecurityConfig` is the **rule book**:
  - Which URLs are public.
  - Which URLs require login.
  - That we are using JWT tokens instead of sessions.
  - That it’s okay for the React app on localhost:3000 to call the backend.

---

## 9. How initial users are created (seed data)

### 9.1 Open `backend/src/main/java/com/medconnect/DataInitializer.java`

This class is annotated with `@Component` and implements `CommandLineRunner`.

ELI5:

- `CommandLineRunner.run(...)` is called **when the app starts**, after Spring Boot is ready.

Logic:

1. Inject `UserRepository` and `PasswordEncoder`.
2. In `run(...)`:
   - If `userRepository.count() == 0`:
     - Create a doctor `User` with:
       - email: `doctor@medconnect.com`
       - password: `passwordEncoder.encode("doctor123")`
       - role: `DOCTOR`, plus specialization and license number.
     - Save it.
     - Create a patient `User` with:
       - email: `patient@medconnect.com`
       - password: `passwordEncoder.encode("patient123")`
       - role: `PATIENT`.
     - Save it.
     - Print `"✓ Default users created"` to console.

Why this is useful:

- On a **fresh database**, you don’t have to register users manually before demoing.
- You can immediately log in as:
  - Patient: `patient@medconnect.com / patient123`
  - Doctor: `doctor@medconnect.com / doctor123`

---

## 10. Putting it all together – the story to tell

Here’s the short **end‑to‑end story** using what you just read:

1. **App startup**
   - I run `MedConnectApplication`.
   - Spring Boot reads `application.properties`, connects to PostgreSQL, sets up JPA and security, scans all my `@RestController`, `@Service`, `@Repository`, `@Component` classes.
   - `DataInitializer` runs once and creates a default doctor and patient if the DB is empty.

2. **Login**
   - Frontend sends `POST /api/auth/login` with `LoginRequest`.
   - `AuthController` receives it and calls `AuthService.login`.
   - `AuthService` uses Spring Security’s `AuthenticationManager`, which calls `CustomUserDetailsService` and `PasswordEncoder` to verify credentials.
   - If valid, `JwtUtil` generates a JWT token with the user’s email.
   - `AuthController` returns `AuthResponse` with the token and user info.

3. **Authenticated request (e.g., book appointment)**
   - Frontend calls `POST /api/appointments` with `AppointmentRequest`, adding `Authorization: Bearer <token>` header.
   - Request hits `JwtAuthFilter`:
     - Extracts email from token using `JwtUtil`.
     - Loads user with `CustomUserDetailsService`.
     - Validates token and sets `Authentication` in `SecurityContext`.
   - `AppointmentController.createAppointment` is then called **with** an `Authentication` object.
   - It calls `appointmentService.createAppointment`, passing the patient’s email and request data.
   - `AppointmentService`:
     - Loads patient and doctor (`UserRepository`).
     - Creates an `Appointment` entity and saves it via `AppointmentRepository`.
   - Saved appointment is returned as JSON.

4. **Viewing health records**
   - Frontend calls `GET /api/health-records` with the token.
   - `JwtAuthFilter` authenticates.
   - `HealthRecordController.getRecords` uses `authentication.getName()` to know which patient.
   - Calls `healthRecordService.getPatientRecords`, which uses `HealthRecordRepository`.
   - Results come back as JSON.

That’s the **full backend picture**: from HTTP requests, through controllers and services, down to repositories and entities, plus how JWT security and database configuration work.

If you can walk through these files in order and explain them in your own words, you’ll be in **great shape** for the interview.

