package com.example.smartboardjacksydenham;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * This class acts as a controller for the profileEditor.fxml file, allowing the user to edit their user profile's
 * details. They can change their firstname, lastname, and profile picture from here, or even delete their account entirely
 */

public class profileEditorController implements Initializable {

    DatabaseController DC = new DatabaseController();
    User U = new User();

    HashMap<String, User> usersMap = new HashMap<>();
    ArrayList<Project> projectsList = new ArrayList<>();
    ArrayList<Column> columnsList = new ArrayList<>();
    ArrayList<Task> tasksList = new ArrayList<>();

    @FXML
    private Label warningMsg;
    @FXML
    private TextField firstField, lastField, userField, passField;
    @FXML
    private Button saveButton, deleteUser, cancelButton;
    @FXML
    private Hyperlink pfpButton;
    @FXML
    private ImageView pfp;

    private String loggedInUser;
    private String motivationalMessage;

    final FileChooser fc = new FileChooser(); //creating fileChooser object

    // after the data is received here, the displayed firstname, lastname, username, and password tabs are all
    // automatically filled with the logged-in user's corresponding data, along with their profile picture taking the
    // space of the ImageView.
    // also note that the username and password cannot be changed after creation, so while they are visible, both
    // TextFields are at a lower opacity and cannot be edited
    public void passData(ArrayList<Task> taskList, ArrayList<Column> columnList, ArrayList<Project> projectList, HashMap<String, User> userMap, String user, String motivation) {
        usersMap = userMap;
        projectsList = projectList;
        columnsList = columnList;
        tasksList = taskList;
        loggedInUser = user;
        motivationalMessage = motivation;
        pfp.setImage(usersMap.get(loggedInUser).getPfp());
        U.setPfp(usersMap.get(loggedInUser).getPfp());
        firstField.setText(usersMap.get(loggedInUser).getFirstname());
        lastField.setText(usersMap.get(loggedInUser).getLastname());
        userField.setText(usersMap.get(loggedInUser).getUsername());
        passField.setText(usersMap.get(loggedInUser).getPassword());
    }

    // username cannot be changed here so checking to see if it is already in use like we do in the profileCreator is
    // not necessary. As long as there are no fields empty, the inputted data will be added to the usersMap. The system
    // will attempt to create a new user object, but since the username cannot be changed, it will just overwrite the
    // previous data attached to that username key (that user) in the usersMap
    public void saveButtonClick() throws IOException {
        HashMap<String, User> userMap = this.usersMap;

        if (firstField.getText().trim().isEmpty() || lastField.getText().trim().isEmpty()) {
            warningMsg.setText("Field(s) empty");
        } else {

            U.setFirstname(firstField.getText());
            U.setLastname(lastField.getText());
            U.setUsername(userField.getText());
            U.setPassword(passField.getText());
            U.setPfp(U.getPfp());

            // the user's data is overwritten, and the usersMap is saved to the database
            usersMap.put(U.getUsername(), new User(U.getFirstname(), U.getLastname(), U.getUsername(), U.getPassword(), U.getPfp()));
            DC.saveUsers(userMap);


            returnToWorkspace(); //once the changes have been made, much like the user creator, a separate method will be run to return to the workspace
        }
    }

    // returns to workspace and passes projectsList and usersMap. The cancel button will just run this method, rather
    // than applying changes like the saveButtonClick method does.
    public void returnToWorkspace() throws IOException{
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

    // this method is called when the user selects the delete user option. It displays a confirmation box to confirm
    // that user wants to delete the current user, and if they select 'OK', it removes the user object of the logged-in
    // user from the usersMap. It also removes all projects associated with that user, before returning to the login page
    public void deleteUserClick() throws IOException{
        ArrayList<Project> projectList = this.projectsList;
        ArrayList<Column> columnList = this.columnsList;
        ArrayList<Task> taskList = this.tasksList;
        HashMap<String, User> userMap = this.usersMap;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Delete User");
        alert.setContentText("Are you sure you want to delete the current user?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){

            // the user and all their data is removed, and the Map/Lists are saved to the database
            usersMap.remove(loggedInUser);
            projectsList.removeIf(Projects -> Objects.equals(Projects.getUser(), loggedInUser));
            columnsList.removeIf(Column -> Objects.equals(Column.getUser(), loggedInUser));
            tasksList.removeIf(Task -> Objects.equals(Task.getUser(), loggedInUser));
            DC.saveUsers(userMap);
            DC.saveProjects(projectList);
            DC.saveColumns(columnList);
            DC.saveTasks(taskList);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Parent root = loader.load();

            loginController LoginController = loader.getController();
            LoginController.passData(taskList, columnList, projectList, userMap);

            Stage stage = (Stage) deleteUser.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Login");
            stage.show();
        }
    }

    // identical to the method used to select the profile picture in the profile creator
    public void pfpButtonClick(){
        fc.setTitle("Image File Chooser");
        fc.setInitialDirectory(new File(System.getProperty("user.home")));
        fc.getExtensionFilters().clear();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));
        File file = fc.showOpenDialog(null);

        if(file != null) {
            U.setPfp(new Image(file.toURI().toString()));
            pfp.setImage(U.getPfp());
        } else {
            System.out.println("Invalid File");
        }
    }

    // selects all text when the firstname and lastname TextFields are selected for easier editing
    public void firstFieldClicked(){
        firstField.selectAll();
    }
    public void lastFieldClicked(){
        lastField.selectAll();
    }

    // hides the warningMsg Label when the user types in either TextField
    public void firstKeyPressed(){
        warningMsg.setText("");
    }
    public void lastKeyPressed(){
        warningMsg.setText("");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }
}
