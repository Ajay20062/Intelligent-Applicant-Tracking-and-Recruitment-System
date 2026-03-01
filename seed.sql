INSERT INTO Recruiters (full_name, email, company) VALUES
('John Smith', 'john.smith@techcorp.com', 'TechCorp'),
('Sarah Johnson', 'sarah.johnson@innovate.com', 'Innovate Solutions'),
('Michael Chen', 'michael.chen@dataworks.com', 'DataWorks Inc'),
('Emily Rodriguez', 'emily.rodriguez@cloudify.com', 'Cloudify'),
('David Kim', 'david.kim@startupx.com', 'StartupX');

INSERT INTO Jobs (recruiter_id, title, department, location, status) VALUES
(1, 'Senior Python Developer', 'Engineering', 'San Francisco, CA', 'Open'),
(2, 'Data Scientist', 'Data Analytics', 'New York, NY', 'Open'),
(3, 'Frontend Engineer', 'Engineering', 'Remote', 'Open'),
(4, 'DevOps Engineer', 'Infrastructure', 'Austin, TX', 'Paused'),
(5, 'Product Manager', 'Product', 'Seattle, WA', 'Open');

INSERT INTO Candidates (full_name, email, phone, resume_url) VALUES
('Alice Williams', 'alice.williams@email.com', '+1-555-0101', 'https://resume.com/alice'),
('Bob Martinez', 'bob.martinez@email.com', '+1-555-0102', 'https://resume.com/bob'),
('Carol Davis', 'carol.davis@email.com', '+1-555-0103', 'https://resume.com/carol'),
('Daniel Brown', 'daniel.brown@email.com', '+1-555-0104', 'https://resume.com/daniel'),
('Eva Taylor', 'eva.taylor@email.com', '+1-555-0105', 'https://resume.com/eva');

INSERT INTO Applications (job_id, candidate_id, status) VALUES
(1, 1, 'Applied'),
(2, 2, 'Screening'),
(3, 3, 'Interviewing'),
(1, 4, 'Rejected'),
(5, 5, 'Hired');
