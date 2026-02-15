package com.iats.app.ui;

import com.iats.app.model.Application;
import com.iats.app.model.ApplicationReportRow;
import com.iats.app.model.ApplicationScreening;
import com.iats.app.model.CandidateRankingRow;
import com.iats.app.model.DashboardStats;
import com.iats.app.model.Recruiter;
import com.iats.app.service.RecruitmentService;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

public class MainFrame extends JFrame {
    private static final String PAGE_DASHBOARD = "dashboard";
    private static final String PAGE_RECRUITER = "recruiter";
    private static final String PAGE_JOB = "job";
    private static final String PAGE_CANDIDATE = "candidate";
    private static final String PAGE_APPLICATION = "application";
    private static final String PAGE_INTERVIEW = "interview";
    private static final String PAGE_REPORT = "report";

    private final RecruitmentService service;
    private final String loggedInUser;
    private final String loggedInRole;
    private final CardLayout cardLayout;
    private final JPanel contentPanel;
    private final JTextArea outputArea;

    private final JLabel recruitersCountLabel;
    private final JLabel jobsCountLabel;
    private final JLabel candidatesCountLabel;
    private final JLabel applicationsCountLabel;
    private final JLabel interviewsCountLabel;

    private final JTextField recruiterNameField;
    private final JTextField recruiterEmailField;
    private final JTextField recruiterCompanyField;

    private final JTextField jobRecruiterIdField;
    private final JTextField jobTitleField;
    private final JTextField jobDepartmentField;
    private final JTextField jobLocationField;
    private final JTextField jobRequiredSkillsField;

    private final JTextField candidateNameField;
    private final JTextField candidateEmailField;
    private final JTextField candidatePhoneField;
    private final JTextField candidateResumeField;
    private final JTextArea candidateResumeTextArea;

    private final JTextField applicationJobIdField;
    private final JTextField applicationCandidateIdField;
    private final JTextField applicationIdForStatusField;
    private final JTextField applicationStatusField;
    private final JTextField applicationIdForScreenField;

    private final JTextField interviewApplicationIdField;
    private final JTextField interviewScheduledAtField;
    private final JTextField interviewTypeField;
    private final JTextField reportJobIdField;

    public MainFrame() {
        this("User", "Recruiter");
    }

    public MainFrame(String loggedInUser, String loggedInRole) {
        super("IATS Dashboard");
        this.service = new RecruitmentService();
        this.loggedInUser = loggedInUser;
        this.loggedInRole = loggedInRole;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(new Dimension(1220, 760));
        setMinimumSize(new Dimension(1040, 680));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        recruitersCountLabel = createStatValueLabel();
        jobsCountLabel = createStatValueLabel();
        candidatesCountLabel = createStatValueLabel();
        applicationsCountLabel = createStatValueLabel();
        interviewsCountLabel = createStatValueLabel();

        recruiterNameField = new JTextField();
        recruiterEmailField = new JTextField();
        recruiterCompanyField = new JTextField();

        jobRecruiterIdField = new JTextField();
        jobTitleField = new JTextField();
        jobDepartmentField = new JTextField();
        jobLocationField = new JTextField();
        jobRequiredSkillsField = new JTextField();

        candidateNameField = new JTextField();
        candidateEmailField = new JTextField();
        candidatePhoneField = new JTextField();
        candidateResumeField = new JTextField();
        candidateResumeTextArea = new JTextArea(5, 20);
        candidateResumeTextArea.setLineWrap(true);
        candidateResumeTextArea.setWrapStyleWord(true);

        applicationJobIdField = new JTextField();
        applicationCandidateIdField = new JTextField();
        applicationIdForStatusField = new JTextField();
        applicationStatusField = new JTextField();
        applicationIdForScreenField = new JTextField();

        interviewApplicationIdField = new JTextField();
        interviewScheduledAtField = new JTextField();
        interviewTypeField = new JTextField();
        reportJobIdField = new JTextField();

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        outputArea.setFont(new Font("Consolas", Font.PLAIN, 13));

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        add(buildSidebar(), BorderLayout.WEST);
        add(buildMainArea(), BorderLayout.CENTER);

        showPage(PAGE_DASHBOARD);
        refreshDashboard();
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(230, 0));
        sidebar.setBackground(new Color(25, 42, 66));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));

        JLabel brand = new JLabel("IATS");
        brand.setForeground(Color.WHITE);
        brand.setFont(new Font("Segoe UI", Font.BOLD, 28));
        brand.setAlignmentX(LEFT_ALIGNMENT);
        sidebar.add(brand);

        JLabel subtitle = new JLabel("Recruitment Dashboard");
        subtitle.setForeground(new Color(201, 215, 233));
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setAlignmentX(LEFT_ALIGNMENT);
        sidebar.add(subtitle);

        sidebar.add(Box.createRigidArea(new Dimension(0, 24)));
        sidebar.add(createNavButton("Dashboard", PAGE_DASHBOARD));
        sidebar.add(createNavButton("Recruiters", PAGE_RECRUITER));
        sidebar.add(createNavButton("Jobs", PAGE_JOB));
        sidebar.add(createNavButton("Candidates", PAGE_CANDIDATE));
        sidebar.add(createNavButton("Applications", PAGE_APPLICATION));
        sidebar.add(createNavButton("Interviews", PAGE_INTERVIEW));
        sidebar.add(createNavButton("Reports", PAGE_REPORT));
        sidebar.add(Box.createVerticalGlue());

        JButton logoutButton = new JButton("Logout");
        logoutButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        logoutButton.setAlignmentX(LEFT_ALIGNMENT);
        logoutButton.setFocusPainted(false);
        logoutButton.addActionListener(e -> onLogout());
        sidebar.add(logoutButton);

        return sidebar;
    }

    private JPanel buildMainArea() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        contentPanel.add(buildDashboardPage(), PAGE_DASHBOARD);
        contentPanel.add(wrapPage("Recruiter Management", buildRecruiterForm()), PAGE_RECRUITER);
        contentPanel.add(wrapPage("Job Posting Management", buildJobForm()), PAGE_JOB);
        contentPanel.add(wrapPage("Applicant Management", buildCandidateForm()), PAGE_CANDIDATE);
        contentPanel.add(wrapPage("Application Tracking & Screening", buildApplicationForm()), PAGE_APPLICATION);
        contentPanel.add(wrapPage("Interview Scheduling", buildInterviewForm()), PAGE_INTERVIEW);
        contentPanel.add(wrapPage("Reports", buildReportPage()), PAGE_REPORT);

        JScrollPane outputScroll = new JScrollPane(outputArea);
        outputScroll.setPreferredSize(new Dimension(0, 190));
        outputScroll.setBorder(BorderFactory.createTitledBorder("Activity Log"));

        main.add(contentPanel, BorderLayout.CENTER);
        main.add(outputScroll, BorderLayout.SOUTH);
        return main;
    }

    private JPanel buildDashboardPage() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JLabel heading = new JLabel("Overview");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 24));

        JLabel userLabel = new JLabel("Signed in as: " + loggedInUser + " (" + loggedInRole + ")");
        userLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        userLabel.setForeground(new Color(84, 98, 114));

        JPanel top = new JPanel(new BorderLayout());
        top.add(heading, BorderLayout.WEST);
        top.add(userLabel, BorderLayout.EAST);
        panel.add(top, BorderLayout.NORTH);

        JPanel statsGrid = new JPanel(new GridLayout(2, 3, 12, 12));
        statsGrid.add(createStatCard("Recruiters", recruitersCountLabel));
        statsGrid.add(createStatCard("Jobs", jobsCountLabel));
        statsGrid.add(createStatCard("Candidates", candidatesCountLabel));
        statsGrid.add(createStatCard("Applications", applicationsCountLabel));
        statsGrid.add(createStatCard("Interviews", interviewsCountLabel));
        panel.add(statsGrid, BorderLayout.CENTER);

        JPanel actions = new JPanel(new BorderLayout());
        actions.setBorder(BorderFactory.createTitledBorder("Actions"));

        JButton refreshButton = new JButton("Refresh Dashboard");
        refreshButton.addActionListener(e -> refreshDashboard());

        JLabel help = new JLabel("Manage ATS workflows from the left menu.");
        help.setHorizontalAlignment(SwingConstants.LEFT);

        actions.add(help, BorderLayout.CENTER);
        actions.add(refreshButton, BorderLayout.EAST);
        panel.add(actions, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel wrapPage(String title, JPanel body) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBorder(BorderFactory.createTitledBorder(title));
        wrapper.add(body, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel buildRecruiterForm() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(new JLabel("Full Name"));
        panel.add(recruiterNameField);
        panel.add(new JLabel("Email"));
        panel.add(recruiterEmailField);
        panel.add(new JLabel("Company"));
        panel.add(recruiterCompanyField);

        JButton addButton = new JButton("Create Recruiter");
        addButton.addActionListener(e -> onCreateRecruiter());
        panel.add(addButton);

        JButton listButton = new JButton("List Recruiters");
        listButton.addActionListener(e -> onListRecruiters());
        panel.add(listButton);

        return panel;
    }

    private JPanel buildJobForm() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(new JLabel("Recruiter ID"));
        panel.add(jobRecruiterIdField);
        panel.add(new JLabel("Title"));
        panel.add(jobTitleField);
        panel.add(new JLabel("Department"));
        panel.add(jobDepartmentField);
        panel.add(new JLabel("Location"));
        panel.add(jobLocationField);
        panel.add(new JLabel("Required Skills (comma separated)"));
        panel.add(jobRequiredSkillsField);

        JButton addButton = new JButton("Create Job Posting");
        addButton.addActionListener(e -> onCreateJob());
        panel.add(addButton);
        panel.add(new JLabel());

        return panel;
    }

    private JPanel buildCandidateForm() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
        form.add(new JLabel("Full Name"));
        form.add(candidateNameField);
        form.add(new JLabel("Email"));
        form.add(candidateEmailField);
        form.add(new JLabel("Phone"));
        form.add(candidatePhoneField);
        form.add(new JLabel("Resume File/URL"));
        form.add(candidateResumeField);

        panel.add(form, BorderLayout.NORTH);

        JPanel resumeTextPanel = new JPanel(new BorderLayout());
        resumeTextPanel.setBorder(BorderFactory.createTitledBorder("Resume Text (skills and experience)"));
        resumeTextPanel.add(new JScrollPane(candidateResumeTextArea), BorderLayout.CENTER);
        panel.add(resumeTextPanel, BorderLayout.CENTER);

        JButton addButton = new JButton("Create Applicant");
        addButton.addActionListener(e -> onCreateCandidate());
        panel.add(addButton, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel buildApplicationForm() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(new JLabel("Job ID"));
        panel.add(applicationJobIdField);
        panel.add(new JLabel("Candidate ID"));
        panel.add(applicationCandidateIdField);

        JButton addButton = new JButton("Create Application + Auto Screen");
        addButton.addActionListener(e -> onCreateApplication());
        panel.add(addButton);
        panel.add(new JLabel());

        panel.add(new JLabel("Application ID (for status update)"));
        panel.add(applicationIdForStatusField);
        panel.add(new JLabel("New Status (Applied/Screening/Interviewing/Rejected/Hired)"));
        panel.add(applicationStatusField);

        JButton updateStatusButton = new JButton("Update Application Status");
        updateStatusButton.addActionListener(e -> onUpdateApplicationStatus());
        panel.add(updateStatusButton);

        JButton listButton = new JButton("List Applications");
        listButton.addActionListener(e -> onListApplications());
        panel.add(listButton);

        panel.add(new JLabel("Application ID (manual re-screen)"));
        panel.add(applicationIdForScreenField);

        JButton screenButton = new JButton("Run Intelligent Screening");
        screenButton.addActionListener(e -> onScreenApplication());
        panel.add(screenButton);
        panel.add(new JLabel());

        return panel;
    }

    private JPanel buildInterviewForm() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(new JLabel("Application ID"));
        panel.add(interviewApplicationIdField);
        panel.add(new JLabel("Scheduled At (yyyy-MM-dd HH:mm)"));
        panel.add(interviewScheduledAtField);
        panel.add(new JLabel("Interview Type (Phone/Video/Onsite)"));
        panel.add(interviewTypeField);

        JButton addButton = new JButton("Schedule Interview");
        addButton.addActionListener(e -> onScheduleInterview());
        panel.add(addButton);
        panel.add(new JLabel());

        return panel;
    }

    private JPanel buildReportPage() {
        JPanel panel = new JPanel(new GridLayout(0, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton summaryButton = new JButton("Generate Summary Report");
        summaryButton.addActionListener(e -> onGenerateSummaryReport());
        panel.add(summaryButton);

        JButton detailButton = new JButton("Generate Detailed Application Report");
        detailButton.addActionListener(e -> onGenerateDetailedReport());
        panel.add(detailButton);

        panel.add(new JLabel("Job ID for Top Candidate Ranking"));
        panel.add(reportJobIdField);

        JButton rankingButton = new JButton("Show Top 10 Candidates For Job");
        rankingButton.addActionListener(e -> onTopCandidateRanking());
        panel.add(rankingButton);

        JButton exportButton = new JButton("Export CSV Report");
        exportButton.addActionListener(e -> onExportCsvReport());
        exportButton.setEnabled("Admin".equals(loggedInRole));
        panel.add(exportButton);

        if (!"Admin".equals(loggedInRole)) {
            JLabel note = new JLabel("CSV export is restricted to Admin role.");
            note.setForeground(new Color(140, 90, 20));
            panel.add(note);
        }
        return panel;
    }

    private JButton createNavButton(String label, String page) {
        JButton button = new JButton(label);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        button.setAlignmentX(LEFT_ALIGNMENT);
        button.setFocusPainted(false);
        button.addActionListener(e -> showPage(page));
        return button;
    }

    private JPanel createStatCard(String label, JLabel value) {
        JPanel card = new JPanel(new BorderLayout(8, 8));
        card.setBorder(BorderFactory.createLineBorder(new Color(201, 212, 226)));

        JLabel title = new JLabel(label);
        title.setBorder(BorderFactory.createEmptyBorder(8, 8, 0, 8));
        title.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        value.setBorder(BorderFactory.createEmptyBorder(0, 8, 8, 8));
        card.add(title, BorderLayout.NORTH);
        card.add(value, BorderLayout.CENTER);
        return card;
    }

    private JLabel createStatValueLabel() {
        JLabel label = new JLabel("0");
        label.setFont(new Font("Segoe UI", Font.BOLD, 28));
        return label;
    }

    private void showPage(String page) {
        cardLayout.show(contentPanel, page);
        if (PAGE_DASHBOARD.equals(page)) {
            refreshDashboard();
        }
    }

    private void refreshDashboard() {
        try {
            DashboardStats stats = service.getDashboardStats();
            recruitersCountLabel.setText(String.valueOf(stats.getRecruiters()));
            jobsCountLabel.setText(String.valueOf(stats.getJobs()));
            candidatesCountLabel.setText(String.valueOf(stats.getCandidates()));
            applicationsCountLabel.setText(String.valueOf(stats.getApplications()));
            interviewsCountLabel.setText(String.valueOf(stats.getInterviews()));
            append("Dashboard refreshed.");
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void onCreateRecruiter() {
        try {
            int id = service.addRecruiter(
                    recruiterNameField.getText().trim(),
                    recruiterEmailField.getText().trim(),
                    recruiterCompanyField.getText().trim()
            );
            append("Recruiter created. ID = " + id);
            recruiterNameField.setText("");
            recruiterEmailField.setText("");
            recruiterCompanyField.setText("");
            refreshDashboard();
        } catch (IllegalArgumentException ex) {
            showValidation(ex.getMessage());
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void onListRecruiters() {
        try {
            List<Recruiter> recruiters = service.listRecruiters();
            if (recruiters.isEmpty()) {
                append("No recruiters found.");
                return;
            }
            append("Recruiters:");
            for (Recruiter recruiter : recruiters) {
                append("ID=" + recruiter.getRecruiterId()
                        + ", Name=" + recruiter.getFullName()
                        + ", Email=" + recruiter.getEmail()
                        + ", Company=" + recruiter.getCompany());
            }
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void onCreateJob() {
        try {
            int recruiterId = Integer.parseInt(jobRecruiterIdField.getText().trim());
            int id = service.addJob(
                    recruiterId,
                    jobTitleField.getText().trim(),
                    jobDepartmentField.getText().trim(),
                    jobLocationField.getText().trim(),
                    jobRequiredSkillsField.getText().trim()
            );
            append("Job posted. ID = " + id);
            jobRecruiterIdField.setText("");
            jobTitleField.setText("");
            jobDepartmentField.setText("");
            jobLocationField.setText("");
            jobRequiredSkillsField.setText("");
            refreshDashboard();
        } catch (NumberFormatException ex) {
            showValidation("Recruiter ID must be a number.");
        } catch (IllegalArgumentException ex) {
            showValidation(ex.getMessage());
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void onCreateCandidate() {
        try {
            int id = service.addCandidate(
                    candidateNameField.getText().trim(),
                    candidateEmailField.getText().trim(),
                    candidatePhoneField.getText().trim(),
                    candidateResumeField.getText().trim(),
                    candidateResumeTextArea.getText().trim()
            );
            append("Candidate created. ID = " + id);
            candidateNameField.setText("");
            candidateEmailField.setText("");
            candidatePhoneField.setText("");
            candidateResumeField.setText("");
            candidateResumeTextArea.setText("");
            refreshDashboard();
        } catch (IllegalArgumentException ex) {
            showValidation(ex.getMessage());
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void onCreateApplication() {
        try {
            int jobId = Integer.parseInt(applicationJobIdField.getText().trim());
            int candidateId = Integer.parseInt(applicationCandidateIdField.getText().trim());
            int id = service.applyToJob(jobId, candidateId);
            append("Application created. ID = " + id);

            Optional<ApplicationScreening> screeningOptional = service.getScreeningResult(id);
            if (screeningOptional.isPresent()) {
                ApplicationScreening screening = screeningOptional.get();
                append("Screening result -> Score: " + screening.getScore()
                        + "%, Recommendation: " + screening.getRecommendation()
                        + ", Matched Skills: " + screening.getMatchedSkills()
                        + ", Skill Gaps: " + screening.getMissingSkills());
            }

            applicationJobIdField.setText("");
            applicationCandidateIdField.setText("");
            refreshDashboard();
        } catch (NumberFormatException ex) {
            showValidation("Job ID and Candidate ID must be numbers.");
        } catch (IllegalArgumentException ex) {
            showValidation(ex.getMessage());
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void onUpdateApplicationStatus() {
        try {
            int applicationId = Integer.parseInt(applicationIdForStatusField.getText().trim());
            boolean updated = service.updateApplicationStatus(applicationId, applicationStatusField.getText().trim());
            if (!updated) {
                showValidation("Application ID not found.");
                return;
            }
            append("Application " + applicationId + " status updated to " + applicationStatusField.getText().trim());
            applicationIdForStatusField.setText("");
            applicationStatusField.setText("");
            refreshDashboard();
        } catch (NumberFormatException ex) {
            showValidation("Application ID must be a number.");
        } catch (IllegalArgumentException ex) {
            showValidation(ex.getMessage());
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void onListApplications() {
        try {
            List<Application> applications = service.listApplications();
            if (applications.isEmpty()) {
                append("No applications found.");
                return;
            }
            append("Applications:");
            for (Application application : applications) {
                append("ID=" + application.getApplicationId()
                        + ", Job=" + application.getJobId()
                        + ", Candidate=" + application.getCandidateId()
                        + ", Status=" + application.getStatus());
            }
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void onScreenApplication() {
        try {
            int applicationId = Integer.parseInt(applicationIdForScreenField.getText().trim());
            ApplicationScreening screening = service.screenApplication(applicationId);
            append("Manual screening completed for application " + applicationId
                    + " -> Score: " + screening.getScore()
                    + "%, Recommendation: " + screening.getRecommendation()
                    + ", Matched Skills: " + screening.getMatchedSkills()
                    + ", Skill Gaps: " + screening.getMissingSkills());
            applicationIdForScreenField.setText("");
        } catch (NumberFormatException ex) {
            showValidation("Application ID must be a number.");
        } catch (IllegalArgumentException ex) {
            showValidation(ex.getMessage());
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void onScheduleInterview() {
        try {
            int applicationId = Integer.parseInt(interviewApplicationIdField.getText().trim());
            String dateTimeInput = interviewScheduledAtField.getText().trim();
            String interviewType = interviewTypeField.getText().trim();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime scheduledAt = LocalDateTime.parse(dateTimeInput, formatter);
            int id = service.scheduleInterview(applicationId, scheduledAt, interviewType);

            append("Interview scheduled. ID = " + id);
            interviewApplicationIdField.setText("");
            interviewScheduledAtField.setText("");
            interviewTypeField.setText("");
            refreshDashboard();
        } catch (NumberFormatException ex) {
            showValidation("Application ID must be a number.");
        } catch (DateTimeParseException ex) {
            showValidation("Use datetime format yyyy-MM-dd HH:mm");
        } catch (IllegalArgumentException ex) {
            showValidation(ex.getMessage());
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void onGenerateSummaryReport() {
        try {
            append(service.generateSummaryReport());
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void onGenerateDetailedReport() {
        try {
            List<ApplicationReportRow> rows = service.getApplicationReport();
            if (rows.isEmpty()) {
                append("No report data available.");
                return;
            }
            append("Detailed Application Report:");
            for (ApplicationReportRow row : rows) {
                append("Application=" + row.getApplicationId()
                        + ", Candidate=" + row.getCandidateName()
                        + ", Job=" + row.getJobTitle()
                        + ", Status=" + row.getStatus()
                        + ", Score=" + row.getScore()
                        + ", Recommendation=" + row.getRecommendation());
            }
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void onExportCsvReport() {
        if (!"Admin".equals(loggedInRole)) {
            showValidation("Only Admin users can export reports.");
            return;
        }
        try {
            String fileName = "application_report_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv";
            Path reportPath = service.exportApplicationReportCsv(fileName);
            append("CSV report exported: " + reportPath.toAbsolutePath());
        } catch (SQLException ex) {
            showError(ex);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onTopCandidateRanking() {
        try {
            int jobId = Integer.parseInt(reportJobIdField.getText().trim());
            List<CandidateRankingRow> rows = service.getTopCandidatesForJob(jobId, 10);
            if (rows.isEmpty()) {
                append("No candidates found for job ID " + jobId + ".");
                return;
            }
            append("Top candidates for Job ID " + jobId + ":");
            int rank = 1;
            for (CandidateRankingRow row : rows) {
                append("#" + rank
                        + " CandidateID=" + row.getCandidateId()
                        + ", Name=" + row.getCandidateName()
                        + ", ApplicationID=" + row.getApplicationId()
                        + ", Score=" + row.getScore()
                        + ", Recommendation=" + row.getRecommendation()
                        + ", Matched=" + row.getMatchedSkills()
                        + ", Gaps=" + row.getMissingSkills());
                rank++;
            }
        } catch (NumberFormatException ex) {
            showValidation("Job ID must be a number.");
        } catch (IllegalArgumentException ex) {
            showValidation(ex.getMessage());
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void onLogout() {
        int option = JOptionPane.showConfirmDialog(
                this,
                "Do you want to logout?",
                "Logout",
                JOptionPane.YES_NO_OPTION
        );
        if (option == JOptionPane.YES_OPTION) {
            WelcomeFrame welcomeFrame = new WelcomeFrame();
            welcomeFrame.setVisible(true);
            dispose();
        }
    }

    private void append(String message) {
        outputArea.append(message + "\n");
    }

    private void showError(SQLException ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showValidation(String message) {
        JOptionPane.showMessageDialog(this, message, "Validation Error", JOptionPane.WARNING_MESSAGE);
    }
}
