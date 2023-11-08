package com.example.smartboardjacksydenham;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is used to operate on the database. The URL, username, and password used by java to connect to mySQL is
 * found here, along with the methods used to perform actions on the database.
 */

public class DatabaseController {

    // ** SET YOUR SQL DATA HERE **
    //----------------------------------------------------------------------------------------
    public String sqlURL = "jdbc:mysql://localhost:3306/smartTest"; // schema connection URL
    public String sqlUSER = "root"; // connection username
    public String sqlPASS = "Macksta1#"; // connection password
    //----------------------------------------------------------------------------------------

    HashMap<String, User> usersMap = new HashMap<>();
    ArrayList<Project> projectsList = new ArrayList<>();
    ArrayList<Column> columnsList = new ArrayList<>();
    ArrayList<Task> tasksList = new ArrayList<>();

    // Method for creating the tables if they don't already exist. This is called when the program launches
    public void createTables() {

        try {
            Connection connection = DriverManager.getConnection(sqlURL, sqlUSER, sqlPASS);
            Statement statement = connection.createStatement();

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS users (firstname VARCHAR(45), " +
                    "lastname VARCHAR(45), username VARCHAR(45) NOT NULL, password VARCHAR(45));");

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS projects (projectName VARCHAR(45) NOT NULL, " +
                   "user VARCHAR(45), defaultProject BOOLEAN NOT NULL);");

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS columns (columnName VARCHAR(45) NOT NULL, " +
                   "project VARCHAR(45), user VARCHAR(45));");

         statement.executeUpdate("CREATE TABLE IF NOT EXISTS tasks (taskName VARCHAR(45) NOT NULL, " +
                    "description VARCHAR(500), dateBoolean BOOLEAN, dueDate DATE, markAsComplete BOOLEAN, " +
                   "project VARCHAR(45), columnName VARCHAR(45), user VARCHAR(45));");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Methods for saving data to the database. These methods are called whenever a change is made to the data in the
     * program. Of course, not all are called; only the related method. For example if a user chooses to edit his profile,
     * when they select 'save changes', and the usersMap is updated, the saveUsers() method below is also called, and passed
     * the current instance of the usersMap. it is then saved to the database, so if the system crashes, or the user
     * force closes the application, the updated data will still be there upon re-opening
     */
    public void saveUsers(HashMap<String, User> userMap){
        usersMap = userMap;
        try {

            Connection connection = DriverManager.getConnection(sqlURL, sqlUSER, sqlPASS);
            Statement statement = connection.createStatement();

            statement.execute("delete from users");

            try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO users VALUES (?, ?, ?, ?);")) {

                for (Map.Entry<String, User> entry : usersMap.entrySet()) {
                    preparedStatement.setString(1, entry.getValue().getFirstname());
                    preparedStatement.setString(2, entry.getValue().getLastname());
                    preparedStatement.setString(3, entry.getValue().getUsername());
                    preparedStatement.setString(4, entry.getValue().getPassword());
                    preparedStatement.executeUpdate();

                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    public void saveProjects(ArrayList<Project> projectList){
        projectsList = projectList;
        try {

            Connection connection = DriverManager.getConnection(sqlURL, sqlUSER, sqlPASS);
            Statement statement = connection.createStatement();

            statement.execute("delete from projects");

            try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO projects VALUES (?, ?, ?);")) {

                for (Project project : projectsList) {
                    preparedStatement.setString(1, project.getProjectName());
                    preparedStatement.setString(2, project.getUser());
                    preparedStatement.setBoolean(3, project.getDefaultProject());
                    preparedStatement.executeUpdate();

                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void saveColumns(ArrayList<Column> columnList){
        columnsList = columnList;
        try {

            Connection connection = DriverManager.getConnection(sqlURL, sqlUSER, sqlPASS);
            Statement statement = connection.createStatement();

            statement.execute("delete from columns");

            try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO columns VALUES (?, ?, ?);")) {

                for (Column column : columnsList) {
                    preparedStatement.setString(1, column.getColumnName());
                    preparedStatement.setString(2, column.getProject());
                    preparedStatement.setString(3, column.getUser());
                    preparedStatement.executeUpdate();

                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public void saveTasks(ArrayList<Task> taskList){
        tasksList = taskList;
        try {

            Connection connection = DriverManager.getConnection(sqlURL, sqlUSER, sqlPASS);
            Statement statement = connection.createStatement();

            statement.execute("delete from tasks");

            try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO tasks VALUES (?, ?, ?, ?, ?, ?, ?, ?);")) {

                // saving tasks is a little different, as it checks if the dateBoolean of the Task object is true, and
                // if it is, saves the date. If it isn't the date will be set as null to avoid errors
                for (Task task : tasksList) {
                    preparedStatement.setString(1, task.getTaskName());
                    preparedStatement.setString(2, task.getDescription());
                    preparedStatement.setBoolean(3, task.getDateBoolean());
                    if (task.getDateBoolean().equals(true)) {
                        preparedStatement.setDate(4, Date.valueOf(task.getDueDate()));
                    } else  preparedStatement.setDate(4, null);
                    preparedStatement.setBoolean(5, task.getMarkAsCompleted());
                    preparedStatement.setString(6,task.getProjectName());
                    preparedStatement.setString(7, task.getColumnName());
                    preparedStatement.setString(8, task.getUser());
                    preparedStatement.executeUpdate();

                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
