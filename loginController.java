package com.example.smartboardjacksydenham;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.ResourceBundle;

public class loginController implements Initializable {

    DatabaseController DC = new DatabaseController();

    //maps and lists created
    HashMap<String, User> usersMap = new HashMap<>();
    ArrayList<Project> projectsList = new ArrayList<>();
    ArrayList<Column> columnsList = new ArrayList<>();
    ArrayList<Task> tasksList = new ArrayList<>();


    /**
     * At the beginning of all controller files, the contents (controls) of the associated fxml file are declared for
     * use (distinguished by the @FXML tag). They are named after the given fxid of said control within the fxml file.
     * This allows for methods to act upon the declared controls.
     */

    @FXML
    public Button LoginButton;
    @FXML
    public Hyperlink createProfile;
    @FXML
    private Label errorMsg, userWarning, passWarning;
    @FXML
    private TextField userField;
    @FXML
    private PasswordField passField;

    private String motivationalMessage;


    /**
     * since class instances are scene exclusive in javafx, all created maps and lists will be reset to the declared
     * settings upon changing scenes. i.e. if a user is created and added to the usersMap in the profile creator scene,
     * the map will again be empty when returning to the login page, as said user was never created on the login
     * controllers instance of the map. to work around this, data must be passed between scenes. in this case, the
     * profile creator passes the current instance of both it projectsList and usersMap to the login controller upon
     * changing scenes to the login. This passed data then overwrites the recipient classes map/list instances with the
     * passed data. This method is always used when moving between scenes to keep all information across the program when it runs.
     *
     * @param projectList is the passed projectsList ArrayList instance and overwrites this classes projectsList
     * @param userMap     is the passed usersMap HashMap instance and overwrites this classes usersMap
     *                    <p>
     *                    important to note that on almost all other passes, a string of the logged-in user's username is passed to keep
     *                    track of who is logged in, along with the motivational message to keep track of one singular motivational quote.
     *                    Of course that is not needed here as no one is logged-in after creating a new user as it returns the user to the
     *                    login page.
     */
    public void passData(ArrayList<Task> taskList, ArrayList<Column> columnList, ArrayList<Project> projectList, HashMap<String, User> userMap) {
        projectsList = projectList;
        columnsList = columnList;
        tasksList = taskList;
        usersMap = userMap;
        System.out.println(projectsList);
        System.out.println(usersMap);
        System.out.println(columnsList);
        System.out.println(tasksList);

    }

    //method for logging into the workspace
    public void LoginButtonClick() throws IOException {
//      L.passData(tasksList, columnsList, projectsList, usersMap);

        // warnings displayed if TextFields are empty on clicking the login button
        if (userField.getText().trim().isEmpty() && passField.getText().trim().isEmpty()) {
            userWarning.setText("Username field is empty");
            passWarning.setText("Password field is empty");
        } else if (userField.getText().trim().isEmpty()) {
            userWarning.setText("Username field is empty");
        } else if (passField.getText().trim().isEmpty()) {
            passWarning.setText("Password field is empty");
        }

        // for loop cycles through a list of the values in the usersMap (User objects) and checks to see that the inputted
        // username and password are found on a user object. If it successfully finds a match, the changeSceneToWorkspace
        // method runs. If the input doesn't match any user details, the error msg will display said problem.
        for (User user : usersMap.values()) {
            if (user.getUsername().equals(userField.getText()) && user.getPassword().equals(passField.getText())) {
                changeSceneToWorkspace();
            }
        }
        errorMsg.setText("Invalid Login details...");
    }

    //changing scene to workspace. this will be run if login is successful
    public void changeSceneToWorkspace() throws IOException {
        //declaring for passing
        HashMap<String, User> userMap = this.usersMap;
        ArrayList<Project> projectList = this.projectsList;
        ArrayList<Column> columnList = this.columnsList;
        ArrayList<Task> taskList = this.tasksList;
        String loggedIn = userField.getText(); //the loggedIn String is set straight from the inputted username, since by this point the login has been declared successful

        // random number generator used to randomly select a motivational message to display while the user is logged in.
        // of course, this cannot be run again from inside the workspace, so the message will stay the same until the user logs out
        Random RNG = new Random();
        int rngNumber = RNG.nextInt(5);
        switch (rngNumber) {
            case 0 -> motivationalMessage = "\"You don't have to have a great start, but you have to start to be great.\" - Zig Ziglar";
            case 1 -> motivationalMessage = "\"When something is important enough, you do it even if the odds are not in your favour.\" - Elon Musk";
            case 2 -> motivationalMessage = "\"It's okay to struggle. It's not okay to give up.\" - Gabe Grunewald";
            case 3 -> motivationalMessage = "\"If you dream it, you can do it.\" - Walt Disney";
            case 4 -> motivationalMessage = "\"We choose to go to the moon not because it is easy, but because it is hard.\" - J.F.Kennedy";
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("Workspace.fxml"));
        Parent root = loader.load();

        // calling the method to set the motivational message, and passing data
        workspaceController WorkspaceController = loader.getController();
        WorkspaceController.passData(taskList, columnList, projectList, userMap, loggedIn, motivationalMessage);

        Stage stage = (Stage) LoginButton.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Workspace");
        stage.show();
    }


    // keyRelease methods declared on username TextField and password TextField to hide warning Labels within the GUI
    public void usernameKeyReleased() {
        userWarning.setText("");
        errorMsg.setText("");
    }

    public void passwordKeyReleased() {
        passWarning.setText("");
        errorMsg.setText("");
    }

    //changes scene to profileCreator and passes current instance of the usersMap and projectsList
    public void createProfileClick() throws IOException {
        HashMap<String, User> userMap = this.usersMap;
        ArrayList<Column> columnList = this.columnsList;
        ArrayList<Task> taskList = this.tasksList;
        ArrayList<Project> projectList = this.projectsList;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("profileCreator.fxml"));
        Parent root = loader.load();

        profileCreatorController creatorController = loader.getController();
        creatorController.passData(taskList, columnList, projectList, userMap);

        Stage stage = (Stage) LoginButton.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Profile Creator");
        stage.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // as soon as the application is launched and the login page is opened, all data is drawn from the database and
        // used to set up the objects used by the program.
        try {

            Connection connection = DriverManager.getConnection(DC.sqlURL, DC.sqlUSER, DC.sqlPASS);
            Statement statement = connection.createStatement();
            ResultSet userResultSet = statement.executeQuery("select * from users");
            final File defaultPfp = new File("src/main/resources/com/example/smartboardjacksydenham/images/default.png"); //creating default profile picture File object
            while (userResultSet.next()) {
                usersMap.put(userResultSet.getString("username"), new User((userResultSet.getString("firstname")), (userResultSet.getString("lastname")),
                        (userResultSet.getString("username")), (userResultSet.getString("password")), (new Image(defaultPfp.toURI().toString()))));
            }

            ResultSet projectResultSet = statement.executeQuery("select * from projects");
            while (projectResultSet.next()){
                projectsList.add(new Project(projectResultSet.getString("projectName"), (projectResultSet.getString("user")), (projectResultSet.getBoolean("defaultProject"))));
            }

            ResultSet columnResultSet = statement.executeQuery("select * from columns");
            while (columnResultSet.next()){
                columnsList.add(new Column(columnResultSet.getString("columnName"), (columnResultSet.getString("project")), (columnResultSet.getString("user"))));
            }

            ResultSet taskResultSet = statement.executeQuery("select * from tasks");
            while (taskResultSet.next()) {
                if (taskResultSet.getBoolean("dateBoolean")) {
                    tasksList.add(new Task(taskResultSet.getString("taskName"), taskResultSet.getString("description"),
                            taskResultSet.getBoolean("dateBoolean"), taskResultSet.getDate("dueDate").toLocalDate(),
                            taskResultSet.getBoolean("markAsComplete"), taskResultSet.getString("project"),
                            taskResultSet.getString("columnName"), taskResultSet.getString("user")));
                } else {
                    tasksList.add(new Task(taskResultSet.getString("taskName"), taskResultSet.getString("description"),
                            taskResultSet.getBoolean("dateBoolean"), null,
                            taskResultSet.getBoolean("markAsComplete"), taskResultSet.getString("project"),
                            taskResultSet.getString("columnName"), taskResultSet.getString("user")));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(usersMap);
        System.out.println(projectsList);
        System.out.println(columnsList);
        System.out.println(tasksList);
    }
}