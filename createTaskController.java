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
 * This class acts as a controller for the createTask.fxml file, allowing the user to add new tasks to a desired column.
 * these created tasks will then be displayed on the workspace using the writeColumns() method on the workspace controller.
 */

public class createTaskController {

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
    private String motivationalMessage;

    // creating objects that will be used by the addDateClick() method while the program is running
    DatePicker datePicker = new DatePicker();
    CheckBox checkBox = new CheckBox();

    // receiving data, this time the usual loggedInUser String, usersMap and various Lists are received, however the
    // current project and current column are also passed so that the task is assigned the correct project/column values
    // on creation
    public void passData(ArrayList<Task> taskList, ArrayList<Column> columnList, ArrayList<Project> projectList, HashMap<String, User> userMap, String user, String project, String column, String motivation) {
        projectsList = projectList;
        columnsList = columnList;
        tasksList = taskList;
        usersMap = userMap;
        loggedInUser = user;
        projectName = project;
        columnName = column;
        motivationalMessage = motivation;

        // text displays where the created task is going to be placed
        headerMsg.setText("Currently creating task under '"+columnName+"' on the '"+projectName+"' project.");

        T.setDateBoolean(false);
    }

    // the addDate hyperlink adds an available input to accept a 'Due Date' and a checkbox for setting the 'Mark as Complete'
    // status using the objects created at the top of this class. The 'Add Due date' button itself is removed and replaced
    // with an option for deleting the due date. The program tracks whether there is a Due Date on the task using a
    // 'dueDateBoolean' Boolean on the Task Class. This makes displaying the tasks in the columns much easier.
    public void addDateClick(){
        // as soon as it is clicked, the dueDateBoolean is set to true, the needed panes are created for holding the
        // date options.
        T.setDateBoolean(true);
        innerVBox.getChildren().remove(addDateHyperlink);
        HBox inputHBox = new HBox();
        HBox dateHBox = new HBox();
        dateHBox.setSpacing(20);
        dateHBox.setAlignment(Pos.CENTER_LEFT);
        inputHBox.setSpacing(10);
        inputHBox.setAlignment(Pos.CENTER_LEFT);

        // since the addDate hyperlink creates a new 'deleteDate' hyperlink every time it is clicked, the ActionEvent
        // Handler must be assigned within this method. This link essentially does the opposite of the addDate link,
        // setting the dueDateBoolean to false, clearing the date options' values and removing them from the scene,
        // before finally reinstating the original 'addDate' link
        Hyperlink deleteDate = new Hyperlink();
        deleteDate.setText("Delete");
        deleteDate.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                T.setDateBoolean(false);
                datePicker.setValue(null);
                checkBox.setSelected(false);
                innerVBox.getChildren().removeAll(dateHBox, inputHBox);
                innerVBox.getChildren().add(addDateHyperlink);
            }
        });

        // once the 'addDate' link has been removed, and all the required nodes are created, they are added to the
        // children set of the scenes VBox. Note that adding children to a VBox appends them to the bottom, so the scene
        // is made up of two VBoxes, and the date is added to the top one (just above the description TextArea)
        innerVBox.getChildren().addAll(dateHBox, inputHBox);
        dateHBox.getChildren().addAll(new Label("Due Date"), deleteDate);
        inputHBox.getChildren().addAll(datePicker, checkBox, new Label("Mark as Complete"));
    }

    // Upon clicking the ok button, three things are checked: (separate warnings are displayed for each)
    // 1) that the Name and Description Fields are not empty
    // 2) that if the dueDateBoolean is true (the due date inputs are available), the date is acceptable
    // 3) that the inputted taskName is not already being uses within the same column.
    // If these checks pass, the Task class is adjusted to have the info provided by the user. Then the instance of the
    // class is saved to the tasksList. Finally, the returnToWorkspace() method is called to go back with all the relevant
    // data including the updated tasksList
    public void okButtonClick() throws IOException {
        ArrayList<Task> taskList = this.tasksList;

        if (nameField.getText().isEmpty() || descField.getText().isEmpty()) {
            warningMsg.setText("Field(s) Empty");
        }
         else if (   T.getDateBoolean().equals(true) && datePicker.getValue() == null){
             warningMsg.setText("Invalid Date");
        } else {

             // all taskNames of the relevant column are added to a list. this list is then check to see that it doesn't
            // contain the inputted taskName
            ArrayList<String> taskNames = new ArrayList<>();
            for (Task task : tasksList) {
                if (task.getUser().equals(loggedInUser) && task.getProjectName().equals(projectName) && task.getColumnName().equals(columnName)) {
                    taskNames.add(task.getTaskName());
                }
            }
            if (taskNames.contains(nameField.getText())) {
                warningMsg.setText("Task name already in use within column...");
            } else {
                T.setTaskName(nameField.getText());
                T.setDescription(descField.getText());
                T.setDueDate(datePicker.getValue());
                T.setMarkAsCompleted(checkBox.isSelected());
                T.setUser(loggedInUser);
                T.setProjectName(projectName);
                T.setColumnName(columnName);

                // task object is created, added to the tasksList, and the list is saved in the database
                tasksList.add(new Task(T.getTaskName(), T.getDescription(), T.getDateBoolean(), T.getDueDate(), T.getMarkAsCompleted(), T.getProjectName(),T.getColumnName(), T.getUser()));
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

    // resetting the warningMsg when the user types in either TextField
    public void nameKeyPressed(){
        warningMsg.setText("");
    }
    public void descKeyPressed(){
        warningMsg.setText("");
    }

}
