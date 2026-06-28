# Resilient Hybrid File Storage System

A modern, high-availability file management application built with Spring Boot and React. This system ensures 100% data availability by implementing a resilient hybrid storage approach: it primarily uploads files to AWS S3, but automatically falls back to local file storage if the cloud service is unavailable, utilizing a Circuit Breaker pattern.

## Screenshots
<img width="1908" height="910" alt="image" src="https://github.com/user-attachments/assets/f5825993-d91f-49c7-842b-cdd5f99b93ef" />

## Key Features

* **Hybrid Storage Architecture:** Seamlessly switches between AWS S3 (Cloud) and Local Filesystem.
* **Circuit Breaker Integration:** Uses Resilience4j to detect S3 failures and gracefully degrade to local storage.
* **Unified Metadata Management:** Tracks file metadata and storage location (`s3://` or `local://`) in a PostgreSQL database.
* **Responsive Frontend:** A React-based UI that clearly indicates whether a file is stored "Online" (S3) or "Offline" (Local).
* **Dockerized Setup:** Easy deployment of the database and backend services using Docker Compose.

## Technologies Used

**Backend (`localstorage`):**
* Java 17+
* Spring Boot
* Spring Data JPA
* Resilience4j (Circuit Breaker)
* AWS SDK v2
* PostgreSQL
* Docker & Docker Compose

**Frontend (`frontend`):**
* React.js (Vite)
* Tailwind CSS
* Lucide-React
* Axios

## Prerequisites

Before you begin, ensure you have the following installed:
* [Java Development Kit (JDK) 17+](https://adoptium.net/)
* [Node.js (v18+) & npm](https://nodejs.org/)
* [Docker Desktop](https://www.docker.com/products/docker-desktop)
* [Git](https://git-scm.com/)

## Installation & Setup

### 1. Clone the Repository
```bash
git clone <your-repository-url>
cd storage
```

### 2. AWS Configuration (Optional but Recommended)
To fully utilize the S3 functionality, update the `application.properties` in the backend (`localstorage/src/main/resources/application.properties`) with your actual AWS bucket details, or pass them as environment variables in the `docker-compose.yml`.

### 3. Start the Backend & Database (Docker)
Navigate to the backend directory and spin up the containers:
```bash
cd localstorage
docker-compose up --build -d
```
*   **PostgreSQL** will start on port `5433` (mapped from 5432 internally).
*   **Spring Boot Application** will start on port `8080`.

### 4. Start the Frontend
Open a new terminal window, navigate to the frontend directory, and start the development server:
```bash
cd frontend
npm install
npm run dev
```
The React app will typically be accessible at `http://localhost:5173`.

## Checking the Database (pgAdmin)

To inspect the file metadata stored in PostgreSQL, connect using pgAdmin or any SQL client with these credentials:

*   **Host**: `localhost`
*   **Port**: `5433`
*   **Database**: `local_storage`
*   **Username**: `postgres`
*   **Password**: `2111Abc@`

Run the following query to see your files and their storage paths (`s3://` vs `local://`):
```sql
SELECT * FROM stored_file;
```

## API Endpoints

The backend exposes the following RESTful endpoints at `http://localhost:8080/files`:

*   `POST /upload`: Uploads a `multipart/form-data` file.
*   `GET /list`: Retrieves metadata of all stored files.
*   `GET /download?filename={name}`: Downloads a specific file.
*   `DELETE /delete/{filename}`: Deletes a file from storage and the database.

## How the Fallback Works

1.  A user uploads a file via the React UI.
2.  The Spring Boot service attempts to upload the file to the configured AWS S3 bucket.
3.  If S3 is unreachable or fails repeatedly (reaching the Circuit Breaker threshold), the `s3CircuitBreaker` opens.
4.  The system automatically falls back to saving the file in the local `./uploads` directory.
5.  The database records the path as `local://<filename>`. The frontend reads this and tags the file as "Offline".


