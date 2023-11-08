package com.example.smartboardjacksydenham;

/**
 * This class is used as the model for the Column object used throughout the program.
 */
public class Column {

    private String columnName;
    private String project;
    private String user;

    public Column(){
    }

    public Column(String columnName, String project, String user){
        this.columnName = columnName;
        this.project = project;
        this.user = user;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Column{" +
                "columnName='" + columnName + '\'' +
                ", project='" + project + '\'' +
                ", user='" + user + '\'' +
                '}';
    }
}
