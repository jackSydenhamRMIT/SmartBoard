package com.example.smartboardjacksydenham;

import java.util.ArrayList;

/**
 * This class is used as the model for the Project object used throughout the program.
 */

public class Project {

    ArrayList<Project> projectsList = new ArrayList<>();

    private String projectName;
    private String user;
    private Boolean defaultProject;

    public Project() {
    }

    public Project(String projectName, String user, Boolean defaultProject) {
        this.projectName = projectName;
        this.user = user;
        this.defaultProject = defaultProject;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Boolean getDefaultProject() {
        return defaultProject;
    }

    public void setDefaultProject(Boolean defaultProject) {
        this.defaultProject = defaultProject;
    }

    @Override
    public String toString() {
        return "Project{" +
                "projectsList=" + projectsList +
                ", projectName='" + projectName + '\'' +
                ", user='" + user + '\'' +
                ", defaultProject=" + defaultProject +
                '}';
    }
}
