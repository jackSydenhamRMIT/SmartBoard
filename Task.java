package com.example.smartboardjacksydenham;
import java.time.LocalDate;

/**
 * This class is used as the model for the Task object used throughout the program.
 */

public class Task {

    private String taskName;
    private String description;
    private Boolean dateBoolean;
    private LocalDate dueDate;
    private Boolean markAsCompleted;
    private String projectName;
    private String columnName;
    private String user;


    public Task() {
    }

    public Task(String taskName, String description, Boolean dateBoolean, LocalDate dueDate, Boolean markAsCompleted, String projectName, String columnName, String user) {
        this.taskName = taskName;
        this.dateBoolean = dateBoolean;
        this.dueDate = dueDate;
        this.markAsCompleted = markAsCompleted;
        this.description = description;
        this.projectName = projectName;
        this.columnName = columnName;
        this.user = user;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getDateBoolean() {
        return dateBoolean;
    }

    public void setDateBoolean(Boolean dateBoolean) {
        this.dateBoolean = dateBoolean;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public Boolean getMarkAsCompleted() {
        return markAsCompleted;
    }

    public void setMarkAsCompleted(Boolean markAsCompleted) {
        this.markAsCompleted = markAsCompleted;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Task{" +
                "taskName='" + taskName + '\'' +
                ", description='" + description + '\'' +
                ", dateBoolean=" + dateBoolean +
                ", dueDate=" + dueDate +
                ", markAsCompleted=" + markAsCompleted +
                ", projectName='" + projectName + '\'' +
                ", columnName='" + columnName + '\'' +
                ", user='" + user + '\'' +
                '}';
    }
}
