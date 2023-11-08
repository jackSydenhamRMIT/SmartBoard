package com.example.smartboardjacksydenham;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

/**
 * The purpose of this class is to allow users to create projects which will appear on their workspace
 */
public class projectCreatorController implements Initializable {

    ArrayList<Project> projectsList = new ArrayList<>();
    ArrayList<Column> columnsList = new ArrayList<>();
    ArrayList<Task> tasksList = new ArrayList<>();
    HashMap<String, User> usersMap = new HashMap<>();

    DatabaseController DC = new DatabaseController();
    Launcher L = new Launcher();
    Project P = new Project();

    @FXML
    private TextField nameField;
    @FXML
    private Label warningMsg;
    @FXML
    private Button okButton, cancelButton;

    private String loggedInUser;
    private String motivationalMessage;

    // receives data
    public void passData(ArrayList<Task> taskList, ArrayList<Column> columnList, ArrayList<Project> projectList, HashMap<String, User> userMap, String user, String motivation) {
        projectsList = projectList;
        columnsList = columnList;
        tasksList = taskList;
        usersMap = userMap;
        loggedInUser = user;
        motivationalMessage = motivation;

    }

    // this method first creates an ArrayList of all project names of projects created by the current user. if the
    // inputted project name is nothing, the warningMsg will display informing the user the field is empty. If the
    // inputted name isn't already in use, a project will be created with the that name. If the name is already in use
    // the warningMsg Label will display informing the user that it is. if the name passes both the conditions, a
    // new project object will be created on the projectsList with the given name.
    public void okButtonClick() throws IOException {
        ArrayList<Project> projectList = this.projectsList;

        if (nameField.getText().isEmpty()) {
            warningMsg.setText("Field empty");
        } else {
            ArrayList<String> projectNames = new ArrayList<>();
            for (Project project : projectsList) {
                if (project.getUser().equals(loggedInUser)) {
                    projectNames.add(project.getProjectName());
                }
            }

            if (projectNames.contains(nameField.getText())) {
                warningMsg.setText("Project name already in use...");
            } else {
                P.setProjectName(nameField.getText());
                P.setUser(loggedInUser);

                // if the containsUser() method returns false, meaning the user has no projects and this created one
                // will be their first, defaultProject is set to true. if it returns true, meaning the user already
                // has a project on their account, defaultProject is set to false
                if (!(containsUser(projectsList))) {
                    P.setDefaultProject(true);
                } else {
                    P.setDefaultProject(false);
                }

                projectsList.add(new Project(P.getProjectName(), P.getUser(), P.getDefaultProject()));
                // projectsList is saved to the database before returning to the workspace
                DC.saveProjects(projectList);
                cancelButtonClick();
            }
        }
    }

    // iterating through all Projects to see if any Project is assigned to the current user, if there is, true is
    // returned. Otherwise false
    public boolean containsUser(final ArrayList<Project> list){
        for (Project project : projectsList){
            if(project.getUser().equals(loggedInUser)){
                return true;
            }
        }
        return false;
    }

    // returns to workplace without making any changes
    public void cancelButtonClick() throws IOException {
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

    // hides the inUseMsg Label when user begins typing
    public void nameKeyPressed(){
        warningMsg.setText("");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

}
