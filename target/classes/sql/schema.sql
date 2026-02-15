CREATE DATABASE IF NOT EXISTS iats_db;
USE iats_db;

CREATE TABLE Recruiters (
    recruiter_id INT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL,
    company VARCHAR(150),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE Jobs (
    job_id INT PRIMARY KEY AUTO_INCREMENT,
    recruiter_id INT NOT NULL,
    title VARCHAR(150) NOT NULL,
    department VARCHAR(100),
    location VARCHAR(100),
    required_skills TEXT,
    status ENUM('Open', 'Closed', 'Paused') NOT NULL DEFAULT 'Open',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_jobs_recruiter
        FOREIGN KEY (recruiter_id)
        REFERENCES Recruiters(recruiter_id)
        ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE Candidates (
    candidate_id INT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    phone VARCHAR(30),
    resume_url VARCHAR(255),
    resume_text TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE Applications (
    application_id INT PRIMARY KEY AUTO_INCREMENT,
    job_id INT NOT NULL,
    candidate_id INT NOT NULL,
    status ENUM('Applied', 'Screening', 'Interviewing', 'Rejected', 'Hired') NOT NULL DEFAULT 'Applied',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_applications_job
        FOREIGN KEY (job_id)
        REFERENCES Jobs(job_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_applications_candidate
        FOREIGN KEY (candidate_id)
        REFERENCES Candidates(candidate_id)
        ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE ApplicationScreenings (
    screening_id INT PRIMARY KEY AUTO_INCREMENT,
    application_id INT NOT NULL UNIQUE,
    score DECIMAL(5,2) NOT NULL DEFAULT 0.00,
    matched_skills TEXT,
    missing_skills TEXT,
    recommendation ENUM('Strong Match', 'Moderate Match', 'Low Match') NOT NULL DEFAULT 'Low Match',
    screened_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_screening_application
        FOREIGN KEY (application_id)
        REFERENCES Applications(application_id)
        ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE Interviews (
    interview_id INT PRIMARY KEY AUTO_INCREMENT,
    application_id INT NOT NULL,
    scheduled_at DATETIME NOT NULL,
    interview_type ENUM('Phone', 'Video', 'Onsite') NOT NULL,
    status ENUM('Scheduled', 'Completed', 'No-Show', 'Cancelled') NOT NULL DEFAULT 'Scheduled',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_interviews_application
        FOREIGN KEY (application_id)
        REFERENCES Applications(application_id)
        ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    username VARCHAR(60) NOT NULL UNIQUE,
    role ENUM('Admin', 'Recruiter') NOT NULL DEFAULT 'Recruiter',
    password_hash VARCHAR(128) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

ALTER TABLE Jobs
    ADD COLUMN IF NOT EXISTS required_skills TEXT;

ALTER TABLE Candidates
    ADD COLUMN IF NOT EXISTS resume_text TEXT;

ALTER TABLE users
    ADD COLUMN IF NOT EXISTS role ENUM('Admin', 'Recruiter') NOT NULL DEFAULT 'Recruiter';

ALTER TABLE ApplicationScreenings
    ADD COLUMN IF NOT EXISTS missing_skills TEXT;
