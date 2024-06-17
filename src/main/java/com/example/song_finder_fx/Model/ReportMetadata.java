package com.example.song_finder_fx.Model;

import com.example.song_finder_fx.DatabasePostgres;

import java.io.File;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class ReportMetadata {
    private int id;
    // private String reportName;
    private int reportMonth;
    private int reportYear;
    private LocalDateTime createdAt;
    private File csvFile; // Field to store the CSV file

    // Constructors
    public ReportMetadata() {}

    public ReportMetadata(int reportMonth, int reportYear, File csvFile) {
        // this.reportName = reportName;
        this.reportMonth = reportMonth;
        this.reportYear = reportYear;
        this.createdAt = LocalDateTime.now();
        this.csvFile = csvFile;
    }

    public ReportMetadata(int id, int month, int year, LocalDateTime createdDate) {
        this.id = id;
        this.reportMonth = month;
        this.reportYear = year;
        this.createdAt = createdDate;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /*public String getReportName() {
        return reportName;
    }*/

    /*public void setReportName(String reportName) {
        this.reportName = reportName;
    }*/

    public int getReportMonth() {
        return reportMonth;
    }

    public void setReportMonth(int reportMonth) {
        this.reportMonth = reportMonth;
    }

    public int getReportYear() {
        return reportYear;
    }

    public void setReportYear(int reportYear) {
        this.reportYear = reportYear;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public File getCsvFile() {
        return csvFile;
    }

    public void setCsvFile(File csvFile) {
        this.csvFile = csvFile;
    }

    public boolean remove() throws SQLException {
        return DatabasePostgres.removeReport(id);
    }
}