package com.example.smartboardjacksydenham;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Similar to the createTaskController, this class acts as a controller for the editTask.fxml file, allowing users to
 * edit their pre-existing tasks. The changes made are limitless, and users are able to add/remove due dates, and change
 * the name and/or description of the desired task.
 */

public class editTaskController {

    DatabaseController DC = new DatabaseController();
    Task T = new Task();

    HashMap<String, User> usersMap = new HashMap<>();
    ArrayList<Project> projectsList = new ArrayList<>();
    ArrayList<Column> columnsList = new ArrayList<>();
    ArrayList<Task> tasksList = new ArrayList<>();

    private Parent root;
    private Scene scene;
    private Stage stage;

    @FXML
    public VBox innerVBox;
    @FXML
    public Label headerMsg, warningMsg;
    @FXML
    public TextField nameField;
    @FXML
    public Hyperlink addDateHyperlink;
    @FXML
    public TextArea descField;
    @FXML
    public Button okButton, cancelButton;

    private String loggedInUser;
    private String projectName;
    private String columnName;
    private String currentTask;
    private String motivationalMessage;

    DatePicker datePicker = new DatePicker();
    CheckBox checkBox = new CheckBox();

    // dissimilar to the passData() method in the createTaskController, this method receives the same data, along with
    // a currentTask String. This of course, is needed so that the correct task can be modified.
    public void passData(ArrayList<Task> taskList, ArrayList<Column> columnList, ArrayList<Project> projectList, HashMap<String, User> userMap, String user, String project, String column, String task, String motivation) {
        projectsList = projectList;
        columnsList = columnList;
        tasksList = taskList;
        usersMap = userMap;
        loggedInUser = user;
        projectName = project;
        columnName = column;
        currentTask = task;
        motivationalMessage = motivation;

        // the task that is being edited is displayed at the top of the scene
        headerMsg.setText("Currently Editing '" + currentTask + "' under '" + columnName + "' on the '" + projectName + "' project.");

        // many for-loops are utilized in this class. This allows the program to iterate through the tasksList, select a
        // specific task object and make changes to it directly using the getters/setters in the Task class. This small
        // for-loop fills all the fields in the scene with the associated data of the task that is being edited, along
        // with displaying the date inputs with their options if that tasks duDateBoolean is true (there is a due data)
        for (Task task1 : tasksList) {
            if (task1.getTaskName().equals(currentTask)) {
                nameField.setText(task1.getTaskName());
                descField.setText(task1.getDescription());
                if (task1.getDateBoolean().equals(true)) {
                    addDateClick();
                    datePicker.setValue(task1.getDueDate());
                    checkBox.setSelected(task1.getMarkAsCompleted());
                }
            }
        }
        // if there is no dueDate on the task that is being edited, the DueDateBoolean is set to false on the Task class
        // essentially initialising the 'addDueDate' hyperlink
        T.setDateBoolean(false);
    }

    public void addDateClick() {
        // this time, rather than just setting the dateBoolean to true on the Task class when the 'addDate' link is
        // clicked the boolean is also set to true on the current task using another for-loop
        T.setDateBoolean(true);
        for (Task task : tasksList) {
            if (task.getTaskName().equals(currentTask)) {
                task.setDateBoolean(true);
            }
        }

        innerVBox.getChildren().remove(addDateHyperlink);
        HBox inputHBox = new HBox();
        HBox dateHBox = new HBox();
        dateHBox.setSpacing(20);
        dateHBox.setAlignment(Pos.CENTER_LEFT);
        inputHBox.setSpacing(10);
        inputHBox.setAlignment(Pos.CENTER_LEFT);

        Label status = new Label();
        status.setText("");

        Hyperlink deleteDate = new Hyperlink();
        deleteDate.setText("Delete");

        // a for-loop is used on the 'deleteDate' link to set the dateBoolean to false on the current task; not just the
        // Task class
        deleteDate.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                T.setDateBoolean(false);
                for (Task task : tasksList) {
                    if (task.getTaskName().equals(currentTask)) {
                        task.setDateBoolean(false);
                    }
                }
                datePicker.setValue(null);
                checkBox.setSelected(false);
                innerVBox.getChildren().removeAll(dateHBox, inputHBox);
                innerVBox.getChildren().add(addDateHyperlink);
            }
        });

        innerVBox.getChildren().addAll(dateHBox, inputHBox);
        dateHBox.getChildren().addAll(new Label("Due Date"), deleteDate);
        inputHBox.getChildren().addAll(datePicker, status, checkBox, new Label("Mark as Complete"));
    }

    // same checks are run as in the createTaskController
    public void okButtonClick() throws IOException {
        ArrayList<Task> taskList = this.tasksList;

        if (nameField.getText().isEmpty() || descField.getText().isEmpty()) {
            warningMsg.setText("Field(s) Empty");
        } else if (T.getDateBoolean().equals(true) && datePicker.getValue() == null) {
            warningMsg.setText("Invalid Date");
        } else {

            // upon checking to see if the task name is already in use within the column,the current task name is NOT
            // added to the 'taskNames' list, allowing the user to keep the same name when editing a task
            ArrayList<String> taskNames = new ArrayList<>();
            for (Task task1 : tasksList) {
                if (task1.getUser().equals(loggedInUser) && task1.getProjectName().equals(projectName) && (!(task1.getTaskName().equals(currentTask)))) {
                    taskNames.add(task1.getTaskName());
                }
            }
            if (taskNames.contains(nameField.getText())) {
                warningMsg.setText("Task name already in use within column...");
            } else {

                // In this class, instead of creating a new Task object, the current task is modified directly, retaining
                // the user, projectName, and columnName values, and in turn retaining the same position on the workspace
                for (Task task2 : tasksList) {
                    if (task2.getTaskName().equals(currentTask)) {
                        task2.setTaskName(nameField.getText());
                        task2.setDescription(descField.getText());
                        task2.setDueDate(datePicker.getValue());
                        task2.setMarkAsCompleted(checkBox.isSelected());
                    }
                }
                DC.saveTasks(taskList);
                returnToWorkspace();
            }
        }
    }

    public void returnToWorkspace() throws IOException {
        ArrayList<Project> projectList = this.projectsList;
        ArrayList<Column> columnList = this.columnsList;
        ArrayList<Task> taskList = this.tasksList;
        HashMap<String, User> userMap = this.usersMap;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("Workspace.fxml"));
        Parent root = loader.load();

        workspaceController WorkspaceController = loader.getController();
        WorkspaceController.passData(taskList, columnList, projectList, userMap, loggedInUser, motivationalMessage);

        Stage stage = (Stage) cancelButton.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Workspace");
        stage.show();
    }

    public void nameKeyPressed(){
        warningMsg.setText("");
    }
    public void descKeyPressed(){
        warningMsg.setText("");
    }

}
