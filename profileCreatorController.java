package com.example.smartboardjacksydenham;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

/**
 * The purpose of this class is to be a controller for the profileCreator.fxml file, allowing the user to create a user
 * profile that can grant them access to their personal workspace
 */

public class profileCreatorController extends User implements Initializable, Serializable {

    DatabaseController DC = new DatabaseController();
    Launcher L = new Launcher();
    User U = new User();

    HashMap<String, User> usersMap = new HashMap<>();
    ArrayList<Project> projectsList = new ArrayList<>();
    ArrayList<Column> columnsList = new ArrayList<>();
    ArrayList<Task> tasksList = new ArrayList<>();

    @FXML
    private Label warningMsg, usernameTaken;
    @FXML
    private TextField firstField, lastField, userField, passField;
    @FXML
    private Button createButton, cancelButton;
    @FXML
    private Hyperlink pfpButton;
    @FXML
    private ImageView pfp;

    final File defaultPfp = new File("src/main/resources/com/example/smartboardjacksydenham/images/default.png"); //creating default profile picture File object
    final FileChooser fc = new FileChooser(); //creating fileChooser object


    // after data is received, the profile picture Image on the user class is immediately set to the string of the given
    // defaultPfp. this means that if no picture is selected, the user will automatically receive the default profile
    // picture. the ImageView is also set to the current user profile picture, which has now been set as the default.
    public void passData(ArrayList<Task> taskList, ArrayList<Column> columnList, ArrayList<Project> projectList, HashMap<String, User> userMap) {
        projectsList = projectList;
        columnsList = columnList;
        tasksList = taskList;
        usersMap = userMap;
        U.setPfp(new Image(defaultPfp.toURI().toString()));
        pfp.setImage(U.getPfp());
    }

    public void createButtonClick() throws IOException {
        HashMap<String, User> userMap = this.usersMap;

        // similarly to the login page, a warning is displayed if any fields are left empty on clicking the create new user button
        if (firstField.getText().trim().isEmpty() || lastField.getText().trim().isEmpty() ||
                userField.getText().trim().isEmpty() || passField.getText().trim().isEmpty()) {
            warningMsg.setText("Field(s) empty");
        }

        // since the usersMap is made up of (Username String, associated user object), checking to see that the inputted
        // username is contained in the usersMap keySet allows the system to deny duplicate usernames, avoiding any complications
        // with things like projects being assigned to incorrect users.
        else if (usersMap.containsKey(userField.getText())) {
            usernameTaken.setText("Username already in use...");
        }

        // if all fields are filled and the inputted username is accepted, the Fields will be set as their corresponding
        // User object attribute.
        else {
            U.setFirstname(firstField.getText());
            U.setLastname(lastField.getText());
            U.setUsername(userField.getText());
            U.setPassword(passField.getText());

            // user object created and saved to the usersMap, the users are then saved to the database
            usersMap.put(U.getUsername(), new User(U.getFirstname(), U.getLastname(), U.getUsername(), U.getPassword(), U.getPfp()));
            DC.saveUsers(userMap);

            changeSceneToLogin(); //method called to return to login page
        }
    }

    // if the user selects cancel, the method is called to return to login page, again passing the relevant data but without changes made
    public void cancelButtonClick() throws IOException {
        changeSceneToLogin();
    }

    public void changeSceneToLogin() throws IOException{ //changing scene

        // declaring current instances for passing
        HashMap<String, User> userMap = this.usersMap;
        ArrayList<Project> projectList = this.projectsList;
        ArrayList<Column> columnList = this.columnsList;
        ArrayList<Task> taskList = this.tasksList;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
        Parent root = loader.load();

        // As written in the login controller, while changing scenes this method calls the loginController class and runs
        // the passData method, passing the current instances of the projectsList and the (now updated) usersMap.
        loginController LoginController = loader.getController();
        LoginController.passData(taskList, columnList, projectList, userMap);

        Stage stage = (Stage) createButton.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Login");
        stage.show();

    }

    // on clicking the 'Click to change' hyperlink, the File Chooser will open on the users home page. The user is able
    // to selected png, jpg, and gif files. once an image has been selected. the string value of the image is set as
    // the Image on the user class as well as the ImageView, the same way the default profile pic is set at launch.
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

    // keyPressed methods to hide the warning Labels when the user begins typing again
    public void firstKeyPressed(){
        warningMsg.setText("");
        usernameTaken.setText("");
    }

    public void lastKeyPressed(){
        warningMsg.setText("");
        usernameTaken.setText("");
    }

    public void userKeyPressed(){
        warningMsg.setText("");
        usernameTaken.setText("");
    }

    public void passKeyPressed(){
        warningMsg.setText("");
        usernameTaken.setText("");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }
}
