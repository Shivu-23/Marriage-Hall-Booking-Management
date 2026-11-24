# Marriage Hall Booking Management System

A complete college project based on the supplied specification. It contains a Spring Boot REST API, Spring Data JPA/MySQL persistence, and an HTML + Bootstrap + JavaScript frontend.

## 1. Requirements

- JDK 17 or later
- MySQL Server 8.x (or compatible)
- Maven 3.6+ (or use the included Maven wrapper if you add one)
- Internet connection when opening the frontend for Bootstrap CDN assets

## 2. Database setup

1. Start MySQL Server.
2. Open MySQL Workbench or MySQL Command Line.
3. Run the SQL in `database/marriage_hall.sql`.
4. Open `backend/src/main/resources/application.properties`.
5. Set `spring.datasource.username` and `spring.datasource.password` to your MySQL credentials.

The default configuration uses:
- Database: `marriage_hall_db`
- Username: `root`
- Password: blank

Change these values if your MySQL installation uses a password.

## 3. Run the backend

Open a terminal in the project `backend` folder (the folder containing `pom.xml`) and run:

```bash
mvn spring-boot:run
```

Or in Spring Tools / Spring Tool Suite:
1. File -> Import -> Existing Maven Projects.
2. Select the `backend` folder.
3. Wait for Maven dependencies to download.
4. Right-click `MarriageHallApplication.java`.
5. Run As -> Java Application (or Spring Boot App).

Backend base URL: `http://localhost:8080`

## 4. Run the frontend

After the backend starts:
1. Open the `frontend` folder.
2. Double-click `index.html`, or open it with VS Code Live Server.
3. The browser connects to `http://localhost:8080/api/v1/hall-bookings`.

Bootstrap is loaded from a CDN, so an internet connection is required for styling.

## 5. API endpoints

| Method | URL | Purpose |
|---|---|---|
| POST | `/api/v1/hall-bookings` | Add a booking (201) |
| GET | `/api/v1/hall-bookings` | List all bookings (200) |
| GET | `/api/v1/hall-bookings/{id}` | Get one booking (200) |
| PUT | `/api/v1/hall-bookings/{id}` | Update a booking (200) |
| DELETE | `/api/v1/hall-bookings/{id}` | Cancel a booking (204) |
| GET | `/api/v1/hall-bookings/reports/dates?start=2027-01-01&end=2027-01-31` | Date range report |
| GET | `/api/v1/hall-bookings/reports/capacity?minCapacity=200` | Minimum capacity report |
| GET | `/api/v1/hall-bookings/reports/status?status=CONFIRMED` | Status report |

### Sample POST / PUT JSON

Use a date that is in the future:

```json
{
  "customerName": "Rahul Kumar",
  "hallName": "Grand Palace Hall",
  "eventDate": "2027-01-15",
  "hallCapacity": 500,
  "rentAmount": 75000.00,
  "bookingStatus": "CONFIRMED"
}
```

## 6. Business rules implemented

- Event date must be in the future.
- Hall capacity must be greater than 50.
- Rent amount must be greater than zero.
- Status must be `CONFIRMED`, `PENDING`, or `CANCELLED`.
- An active booking (`CONFIRMED` or `PENDING`) prevents another active booking for the same hall on the same date.
- Cancelling uses a soft delete: the booking row is retained with status `CANCELLED`, and the endpoint responds with HTTP 204.
- Date, capacity and status report endpoints use the repository query methods required by the specification:
  - `findByEventDateBetween(LocalDate start, LocalDate end)`
  - `findByHallCapacityGreaterThanEqual(Integer capacity)`
  - `findByBookingStatusIgnoreCase(String status)`

## 7. Postman testing

Set `Content-Type: application/json` for POST and PUT requests. Start with the sample POST above, then test GET, GET by ID, PUT, DELETE and all three report endpoints. A duplicate active hall/date request should return HTTP 409. Invalid input should return HTTP 400. A missing booking ID should return HTTP 404.

## 8. Troubleshooting

- **Access denied for user:** update username/password in `application.properties`.
- **Unknown database:** run `database/marriage_hall.sql`.
- **Port 8080 already in use:** stop the other application using port 8080, or change `server.port`.
- **Frontend cannot connect:** confirm the backend is running and visit `http://localhost:8080/api/v1/hall-bookings`.
- **Maven command not found:** install Maven, or import the project as an existing Maven project in Spring Tools.

## Project structure

```text
MarriageHallBookingManagement/
├── backend/
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/marriagehall/
│       │   ├── controller/
│       │   ├── dto/
│       │   ├── entity/
│       │   ├── exception/
│       │   ├── repository/
│       │   ├── service/
│       │   └── MarriageHallApplication.java
│       └── resources/application.properties
├── frontend/
│   ├── index.html
│   ├── css/style.css
│   └── js/script.js
├── database/marriage_hall.sql
└── README.md
```
