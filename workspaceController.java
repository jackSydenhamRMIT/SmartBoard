package com.example.smartboardjacksydenham;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class workspaceController {

    DatabaseController DC = new DatabaseController();
    Column C = new Column();

    HashMap<String, User> usersMap = new HashMap<>();
    ArrayList<Project> projectsList = new ArrayList<>();
    ArrayList<Column> columnsList = new ArrayList<>();
    ArrayList<Task> tasksList = new ArrayList<>();

    private Parent root;
    private Scene scene;

    @FXML
    public Button editProfile, logoutButton;
    @FXML
    private Label nowLoggedIn, helloMsg, motivationMsg;
    @FXML
    private ImageView pfpPreview;
    @FXML
    private Menu projectMenu;
    @FXML
    private MenuItem newProjectButton, editProjectButton, deleteProjectButton, setDefaultButton, removeDefault, newColumn;
    @FXML
    protected TabPane tabPane;

    private String motivationalMessage; //String of message displayed to motivate the user to work on the project

    private String loggedInUser;
    private String currentColumn;

    //receives data and uses it upon loading the scene
    public void passData(ArrayList<Task> taskList, ArrayList<Column> columnList, ArrayList<Project> projectList, HashMap<String, User> userMap, String user, String motivation) {
        tasksList = taskList;
        columnsList = columnList;
        projectsList = projectList;
        usersMap = userMap;
        loggedInUser = user;
        motivationalMessage = motivation;
        motivationMsg.setText(motivationalMessage);

        nowLoggedIn.setText("Logged in as: '" + usersMap.get(loggedInUser).getUsername() + "'"); //displays the username of the logged-in user, using the nowLoggedIn Label
        helloMsg.setText("Hello " + usersMap.get(loggedInUser).getFirstname()); //displays the firstname of the logged-in user, using the helloMsg Label
        pfpPreview.setImage(usersMap.get(loggedInUser).getPfp()); //displays the profile picture of the logged-in user, using the pfpPreview ImageView

        // as soon as the scene opens the writeTabs() method is called to display the users project tabs (if there are any)
        writeTabs();

        // once the tabs have been displayed, the program checks to see if the user has any columns attached to any of
        // their projects. If they do, the writeColumns() method is called to display the columns and their tasks
        ArrayList<String> columns = new ArrayList<>();
        for (Column column : columnsList) {
            if (column.getUser().equals(loggedInUser)) {
                columns.add(column.getColumnName());
            }
        }
        if (!(columns.isEmpty())) {
            writeColumns();
        }

    }

    public void writeTabs() {
        // as long as a project has been created on the loggedInUser, tabs are created for each project in the
        // projectsList that has the attributed username of the logged-in user's username.
        // if there is a default project, that project's tab will be created first, and given a distinctive orange colour,
        // followed by the others. since the default is the first tab, it will automatically be selected
        for (int i = 0; i < projectsList.size(); i++) {
                if (projectsList.get(i).getUser().equals(loggedInUser) && projectsList.get(i).getDefaultProject().equals(true)) {
                    Tab project = new Tab(projectsList.get(i).getProjectName());
                    tabPane.getTabs().add(project);
                    Label tip = new Label("Click the 'Project' button to add columns.");
                    tip.setFont(Font.font("System", FontWeight.BOLD, 12));
                    tip.setPadding(new Insets(8));
                    project.setContent(tip);
                    project.setStyle("-fx-background-color: ORANGE");
                }
            }
        for (int i = 0; i < projectsList.size(); i++) {
                 if (projectsList.get(i).getUser().equals(loggedInUser) && projectsList.get(i).getDefaultProject().equals(false)) {
                    Tab project = new Tab(projectsList.get(i).getProjectName());
                    tabPane.getTabs().add(project);
                    Label tip = new Label("Click the 'Project' button to add columns.");
                    tip.setFont(Font.font("System", FontWeight.BOLD, 12));
                    tip.setPadding(new Insets(8));
                    project.setContent(tip);
                }
            }
        if (tabPane.getTabs().isEmpty()) {
            projectMenu.setDisable(true);
        }
        }


    // when a tab is clicked on, the associated columns are added to a new instance of FlowPane, which is placed in a
    // ScrollPane
    public void writeColumns() {
        ArrayList<Column> columnList = this.columnsList;
        ArrayList<Task> taskList = this.tasksList;

        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
        String selectedTabString = selectedTab.getText();
        ScrollPane scrPane = new ScrollPane();
        scrPane.setFitToWidth(true);
        FlowPane fPane = new FlowPane();
        fPane.setVgap(20);
        fPane.setHgap(10);
        fPane.setPadding(new Insets(8));

        // int to assist with sizing the FlowPane to fit all the columns
        int numberOfColumns = 0;

        // once the ScrollPane has been created and set as the tabs content, a new VBox is created for each column
        // object associated with the selected project and its spacing is set
        for (int i = 0; i < columnsList.size(); i++) {
            VBox vertBox = new VBox();
            vertBox.setSpacing(10);

            // a new menuButton is created and given a fixed size. this button will act as both a dropdown for the
            // column options, and a Label for displaying the column name
            MenuButton dropdown = new MenuButton();
            dropdown.setMinWidth(320);
            dropdown.setMaxWidth(320);
            dropdown.setMinHeight(35);
            dropdown.setMaxHeight(35);

            // this simply assigns the 'currentColumn' String to the name of the dropdown when it is clicked on.
            dropdown.setOnMouseClicked(e -> currentColumn = dropdown.getText());

            // after creating the menuButton, the menuItems are created and their actions are set
            MenuItem renameColumn = new MenuItem("Rename Column");

            // the renameColumn MenuItem creates a TextInputDialog box to accept a String value upon clicking
            renameColumn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    TextInputDialog tiDialog = new TextInputDialog();
                    tiDialog.setTitle("Rename Column");
                    tiDialog.setHeaderText("Currently Renaming: " + dropdown.getText());
                    tiDialog.setContentText("New Column Name: ");
                    Optional<String> result = tiDialog.showAndWait();

                    // if the user inputs a name, the program checks to see that what the user inputted isn't already
                    // use by another column in that project. If it is, an alert box will display to say that it is.
                    if (result.isPresent()) {
                        String input = result.get();

                        ArrayList<String> columnNames = new ArrayList<>();
                        for (Column column : columnsList) {
                            if (column.getUser().equals(loggedInUser) && column.getProject().equals(selectedTabString)) {
                                columnNames.add(column.getColumnName());
                            }
                        }
                        if (columnNames.contains(input)) {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Error");
                            alert.setHeaderText("Column name already in use within project");
                            alert.showAndWait();

                            // if the name passes as being unique to the current project. the column object associated
                            // with the renameColumn buttons text is modified to have the columnName of the users input
                        } else {
                            for (Column column : columnsList) {
                                if (column.getProject().equals(selectedTabString) && column.getColumnName().equals(dropdown.getText()) && column.getUser().equals(loggedInUser)){
                                    column.setColumnName(input);
                                }
                            }

                            // the task objects within that columns are also modified to have the columnNames of the
                            // users input. This allows the tasks to still bne associated with the column after editing
                            for (Task task : tasksList) {
                                if (task.getColumnName().equals(dropdown.getText()) && task.getProjectName().equals(selectedTabString) && task.getUser().equals(loggedInUser)) {
                                    task.setColumnName(input);
                                }
                            }
                        }
                    }
                    // after the changes have been made the task and columns Lists are saved to the database, before the
                    // writeColumns() method runs again, rewriting all the columns and tasks with the changes
                    DC.saveTasks(taskList);
                    DC.saveColumns(columnList);
                    writeColumns();
                }
            });

            MenuItem newTask = new MenuItem("Add New Task");

            // the newTask MenuItem simply calls the sceneToTheCreateTask() method, passing the selected tab, and the
            // currentColumn. which was set when the user clicked on the MenuButton to access this newTask MenuItem
            newTask.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    try {
                        changeSceneToCreateTask(selectedTabString, currentColumn);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            MenuItem deleteColumn = new MenuItem("Delete Column");

            // this MenuItem creates a Confirmation Alert to confirm that the user wants to delete the current column.
            deleteColumn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Confirmation");
                    alert.setHeaderText("Delete Column");
                    alert.setContentText("Are you sure you want to delete this column?");
                    Optional<ButtonType> result = alert.showAndWait();

                    // If they select yes, the column object with the columnName of the selected column will be removed
                    // from the columnsList. Any task object associated with that column is also removed.
                    // the VBox is also removed from the current FlowPanes set of Children and so it is no longer displayed
                    if (result.get() == ButtonType.OK) {
                        columnsList.removeIf(Column -> Column.getColumnName().equals(currentColumn) && Column.getProject().equals(selectedTab.getText()) && Column.getUser().equals(loggedInUser));
                        fPane.getChildren().removeAll(vertBox);
                        if (!(tasksList.isEmpty())) {
                            tasksList.removeIf(Task -> Task.getColumnName().equals(currentColumn) && Task.getProjectName().equals(selectedTab.getText()) && Task.getUser().equals(loggedInUser));
                        }
                        // lists are saved to the database
                        DC.saveColumns(columnList);
                        DC.saveTasks(taskList);
                        writeColumns();
                    }
                }
            });

            // once the contents used for displaying columns have been created, they are assigned as needed.
            if (columnsList.get(i).getProject().equals(selectedTabString) && columnsList.get(i).getUser().equals(loggedInUser)) {


                // the created ScrollPane is set as the selected tab's content
                // since this ScrollPane already has a created  FlowPane as its content, the VBox which will contain the
                // column is added to the children of the FlowPane, and in turn, is scrollable
                fPane.getChildren().add(vertBox);
                vertBox.getChildren().add(dropdown);
                // the created MenuButton is given the name of the associated column objects columnName
                dropdown.setText(columnsList.get(i).getColumnName());
                // the created MenuItems are added to the Items of the MenuButton, making them accessible
                dropdown.getItems().addAll(newTask, renameColumn, deleteColumn);

                // upon creating a new column, a small tip is provided to guide the user on how to create tasks, as it
                // not seem completely obvious
                Label tip = new Label("Click to add tasks.");
                tip.setFont(Font.font("System", FontWeight.BOLD, 12));
                tip.setPadding(new Insets(0, 8, 0, 8));
                vertBox.getChildren().add(tip);

                scrPane.setContent(fPane);
                selectedTab.setContent(scrPane);

                //simply counting how many columns are being displayed on a project
                numberOfColumns++;



                // once the columns have been created, all available tasks will be displayed in the VBoxes of their
                // associated parents using new instances of a BorderPane with inner HBoxes
                for (Task task : tasksList) {

                    // creating and configuring the BorderPane to be a set size with a white background and thin border
                    BorderPane bPane = new BorderPane();
                    bPane.setMinWidth(320);
                    bPane.setMaxWidth(320);
                    bPane.setMinHeight(109);
                    bPane.setMaxHeight(109);
                    bPane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
                    bPane.setBorder(new Border(new BorderStroke(Color.LIGHTGREY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                    bPane.setPadding(new Insets(10));

                    // creating a center-aligned Label with wrapped text for the task description
                    Label taskDesc = new Label(task.getDescription());
                    taskDesc.setFont(Font.font(11));
                    taskDesc.setTextAlignment(TextAlignment.CENTER);
                    taskDesc.setWrapText(true);

                    // creating the first HBox to sit at the top of the borderpane, holding the taskName, and two
                    // hyperlinks for editing/deleting the tasks
                    HBox topBox = new HBox();
                    topBox.setAlignment(Pos.CENTER_LEFT);
                    topBox.setSpacing(8);
                    Label taskName = new Label(task.getTaskName());

                    // setting the taskName fixed width to 150, forcing the two hyperlinks to be on the far right of the
                    // HBox while the taskName remains in the left of the box. this also allows all tasks to remain
                    // patterned throughout the column.
                    taskName.setMinWidth(150);
                    taskName.setMaxWidth(150);
                    // setting taskName font to be bold
                    taskName.setFont(Font.font("System", FontWeight.findByWeight(700), 12));

                    // creating a new hyperlink to be placed on the top HBox
                    Hyperlink editTask = new Hyperlink("Edit Task");
                    // this link calls the changeSceneToEditTask() method, passing the current project name, current
                    // column name, and current task name
                    editTask.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            try {
                                changeSceneToEditTask(selectedTabString, dropdown.getText(), taskName.getText());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    // creating another new hyperlink to be placed in the top HBox
                    Hyperlink deleteTask = new Hyperlink("Delete Task");

                    // the deleteTask link creates a new Confirmation Alert box to confirm the user wants to delete the task
                    deleteTask.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {

                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                            alert.setTitle("Confirmation");
                            alert.setHeaderText("Delete Task");
                            alert.setContentText("Are you sure you want to delete this task?");
                            Optional<ButtonType> result = alert.showAndWait();

                            // if the user selects OK, the associated task is removed from the tasksList, and the
                            // writeColumns() method is then run again to display the updated workspace
                            if (result.get() == ButtonType.OK) {

                                tasksList.removeIf(Task -> Task.getTaskName().equals(taskName.getText()) && Task.getProjectName().equals(selectedTabString) && Task.getUser().equals(loggedInUser) && Task.getColumnName().equals(dropdown.getText()));
                                // the tasksList is saved to the database
                                DC.saveTasks(taskList);
                                writeColumns();
                            }
                        }
                    });

                    // once the links and taskName label have been created they are added to the top VBox
                    topBox.getChildren().addAll(taskName, editTask, deleteTask);

                    // if there is a task available to be displayed in the column, the tip Label is removed from the
                    // VBox and the BorderPane is added
                    if (task.getColumnName().equals(dropdown.getText()) && task.getProjectName().equals(selectedTabString) && task.getUser().equals(loggedInUser)) {
                        vertBox.getChildren().remove(tip);
                        vertBox.getChildren().add(bPane);

                        // the BorderPanes top content is set as the topBox, containing the associated taskName, and
                        // edit/delete task links. the center content is set as the associated taskDescription
                        bPane.setTop(topBox);
                        bPane.setCenter(taskDesc);

                        // if the task object which this BorderPane is being based off has a dueDateBoolean of true,
                        // the date is displayed on the bottom of the BorderPane in its own VBox
                        if (task.getDateBoolean().equals(true)) {

                            // new instance of VBox is created to hold the date and status of the task. the alignment is
                            // also adjusted and the spacing is set to display the date/status neatly on each task
                            HBox bottomBox = new HBox();
                            bottomBox.setAlignment(Pos.CENTER_RIGHT);
                            bottomBox.setSpacing(95);

                            // a new label is created to display whether the task has been completed, based off the
                            // task objects markAsCompleted Boolean value
                            Label status = new Label();
                            status.setPadding(new Insets(3));
                            if (task.getMarkAsCompleted().equals(true)) {
                                status.setText("Status: Completed");
                            } else {
                                status.setText("Status: Incomplete");
                            }

                            // at this point, the dueDate of the task object is formatted into an easy-to-read pattern
                            // of day/month/year and set as to the text of the date Label which will be displayed on the
                            // bottom VBox in the BorderPane
                            Label date = new Label("Date: " + task.getDueDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                            date.setPadding(new Insets(3));

                            // a new long value is created to calculate the days between today and one week from now
                            long daysBetween = ChronoUnit.DAYS.between(LocalDate.now(), task.getDueDate());
                            try {

                            // if the task is completed, and the due date has passed, the background colour of both the
                            // date and status Labels is set to green
                            if (task.getMarkAsCompleted().equals(true) && new SimpleDateFormat("dd/MM/yyyy").parse(task.getDueDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).after(new Date())) {
                                date.setStyle("-fx-background-color: #16f17d; -fx-background-radius: 7px; -fx-font-weight: 600");
                                status.setStyle("-fx-background-color: #16f17d; -fx-background-radius: 7px; -fx-font-weight: 600");
                            }
                            // if the task is completed, but the due date has passed, the background colour of the status
                            // Label is set to green
                            else if (task.getMarkAsCompleted().equals(true) && new SimpleDateFormat("dd/MM/yyyy").parse(task.getDueDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).before(new Date())) {
                                status.setStyle("-fx-background-color: #16f17d; -fx-background-radius: 7px; -fx-font-weight: 600");
                            }
                            // if the task is incomplete, and the due date has passed, the background colour of the date
                            // Label is set to orange
                            else if (task.getMarkAsCompleted().equals(false) && new SimpleDateFormat("dd/MM/yyyy").parse(task.getDueDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).before(new Date())) {
                                date.setStyle("-fx-background-color: #ff8400; -fx-background-radius: 7px; -fx-font-weight: 600");
                            }
                            // if the task is incomplete, and the due date is within the next 7 days, the background
                            // colour of the date Label is set to yellow
                            else if (task.getMarkAsCompleted().equals(false) && daysBetween<7) {
                                date.setStyle("-fx-background-color: GOLD; -fx-background-radius: 7px; -fx-font-weight: 600");
                            }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            // once the date and status Labels have been given the correct backgroSuperund colour settings
                            bottomBox.getChildren().addAll(date, status);
                            bPane.setBottom(bottomBox);
                        }
                    }
                }
            }
            // since the columns are a fixed width and the spacing within the FlowPane is also fixed, adding 332 to the
            // minimum width of the FlowPane for every column displayed creates the correct scrollable width
            fPane.setMinWidth(numberOfColumns * 332);
        }
    }

    //changes the scene to the project creator, passing the projectsList, usersMap, and loggedInUser String.
    public void newProjectClick() throws IOException {
        HashMap<String, User> userMap = this.usersMap;
        ArrayList<Project> projectList = this.projectsList;
        ArrayList<Column> columnList = this.columnsList;
        ArrayList<Task> taskList = this.tasksList;


        FXMLLoader loader = new FXMLLoader(getClass().getResource("projectCreator.fxml"));
        root = loader.load();

        projectCreatorController creatorController = loader.getController();
        creatorController.passData(taskList, columnList, projectList, userMap, loggedInUser, motivationalMessage);

        Stage stage = (Stage) editProfile.getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Project Creator");
        stage.show();

    }

    // opens a TextInputDialog box and asks the user to input a name for their new project. Like creating a project, if
    // the given name is already being used by another project on that user's workspace, the name will be denied
    public void editProjectClick() {
        ArrayList<Project> projectList = this.projectsList;
        ArrayList<Column> columnList = this.columnsList;
        ArrayList<Task> taskList = this.tasksList;

        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
        String selectedTabString = selectedTab.getText();
        TextInputDialog tiDialog = new TextInputDialog();
        tiDialog.setTitle("Edit Project");
        tiDialog.setHeaderText("Currently Editing: " + selectedTab.getText());
        tiDialog.setContentText("New Project Name: ");

        Optional<String> result = tiDialog.showAndWait();
        if (result.isPresent()) {
            String input = result.get();

            // if the user inputs anything, all projectNames of project objects where the value of 'user' is the logged-in
            // user String are added to a new arraylist. if the new arraylist contains what the user inputted for the
            // new project name, there will be an error displayed to say the project name is already in use. If the new
            // name is not in use, it will iterate through the projectsList and replace the name of the related project object
            ArrayList<String> projectNames = new ArrayList<>();
            for (Project project : projectsList) {
                if (project.getUser().equals(loggedInUser)) {
                    projectNames.add(project.getProjectName());
                }
            }
            if (projectNames.contains(input)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Project Name in use");

                alert.showAndWait();
            } else {
                for (Project project : projectsList) {
                    if (project != null && selectedTabString.equals(project.getProjectName()) && project.getUser().equals(loggedInUser)) {
                        selectedTab.setText(input);
                        project.setProjectName(input);
                    }
                }
                for (Column column : columnsList) {
                    if (column.getProject().equals(selectedTabString) && column.getUser().equals(loggedInUser)){
                        column.setProject(input);
                    }
                }
                for (Task task : tasksList) {
                    if (task.getProjectName().equals(selectedTabString) && task.getUser().equals(loggedInUser)) {
                        task.setProjectName(input);
                    }
                }
            }
            DC.saveProjects(projectList);
            DC.saveColumns(columnList);
            DC.saveTasks(taskList);
        }
    }

    // this provides a confirmation box for deleting a project, if OK is selected, the tab is removed from the tabPane
    // and the project object is removed from the projectsList.
    public void deleteProjectClick() throws IOException {
        ArrayList<Project> projectList = this.projectsList;
        ArrayList<Column> columnList = this.columnsList;
        ArrayList<Task> taskList = this.tasksList;
        HashMap<String, User> userMap = this.usersMap;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Delete Project");
        alert.setContentText("Are you sure you want to delete the current project?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {

            // deleting the final tab sends to user back to the login page, as removing all tabs directly from the
            // tabPane reaches a NullPointerException
            if (tabPane.getTabs().size() == 1) {
                Alert alert1 = new Alert(Alert.AlertType.CONFIRMATION);
                alert1.setTitle("Confirmation");
                alert1.setHeaderText("Delete Final Project");
                alert1.setContentText("Deleting the final project will send you back to the login page. \nAre you sure you want to continue?");
                Optional<ButtonType> result1 = alert1.showAndWait();
                if (result1.get() == ButtonType.OK) {

                    Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
                    String selectedTabString = selectedTab.getText();
                    projectsList.removeIf(Project -> Objects.equals(Project.getProjectName(), selectedTabString) && Objects.equals(Project.getUser(), loggedInUser));
                    columnsList.removeIf(Column -> Objects.equals(Column.getProject(), selectedTabString) && Objects.equals(Column.getUser(), loggedInUser));
                    tasksList.removeIf(Task -> Objects.equals(Task.getProjectName(), selectedTabString) && Objects.equals(Task.getUser(), loggedInUser));

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
                    root = loader.load();

                    loginController LoginController = loader.getController();
                    LoginController.passData(taskList, columnList, projectList, userMap);

                    Stage stage = (Stage) logoutButton.getScene().getWindow();
                    scene = new Scene(root);
                    stage.setScene(scene);
                    stage.setTitle("Login");
                    stage.show();
                }
            } else {
                Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
                String selectedTabString = selectedTab.getText();
                projectsList.removeIf(Project -> Objects.equals(Project.getProjectName(), selectedTabString) && Objects.equals(Project.getUser(), loggedInUser));
                columnsList.removeIf(Column -> Objects.equals(Column.getProject(), selectedTabString) && Objects.equals(Column.getUser(), loggedInUser));
                tasksList.removeIf(Task -> Objects.equals(Task.getProjectName(), selectedTabString) && Objects.equals(Task.getUser(), loggedInUser));
                tabPane.getTabs().remove(selectedTab);
            }
            DC.saveProjects(projectList);
            DC.saveColumns(columnList);
            DC.saveTasks(taskList);
        }
    }

    // this sets the selected project as the default, and sets all other projects on the workspace to not be default,
    // then rewrites all the project tabs with the default now set and selected
    public void setDefaultClick() {
        ArrayList<Project> projectList = this.projectsList;
        ArrayList<Column> columnList = this.columnsList;
        ArrayList<Task> taskList = this.tasksList;

        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
        for (Project project : projectsList) {
            if (project != null && (!(selectedTab.getText().equals(project.getProjectName()))) && project.getUser().equals(loggedInUser)) {
                project.setDefaultProject(false);
            }
            if (project != null && selectedTab.getText().equals(project.getProjectName()) && project.getUser().equals(loggedInUser)) {
                project.setDefaultProject(true);
            }
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText("The selected tab was set to default.");
        alert.showAndWait();

        // once the changes have been made it runs the passData() method, essentially recreating the tabs. originally,
        // the tabs were cleared from the tabPane, then the passData() method ran, however this led to a
        // NullPointerException, as the 'selectedTab' was lost in the process. to counter this, the passData() method
        // runs, creating new tabs with the newly appointed default project, while also keeping the old ones. The First
        // of the newly created tabs is selected, then all unselected tabs are added to a List. and all those tabs are
        // deleted. Finally, the second tab is deleted, as at this point, the second tab is an exact copy of the first.
        // While this is a seemingly unnecessarily drawn out process, it removes and rewrites all the tabs with the new
        // setting, while keeping track of the selected tab and avoiding any PointerException.
        passData(tasksList, columnsList, projectsList, usersMap, loggedInUser, motivationalMessage);
        tabPane.getSelectionModel().select((tabPane.getTabs().size() / 2));

        final ObservableList<Tab> tablist = tabPane.getTabs();
        ArrayList<Tab> markedTabs = new ArrayList<>();
        for (Tab tab : tablist) {
            if (tab != tabPane.getSelectionModel().getSelectedItem())
                markedTabs.add(tab);
        }
        tabPane.getTabs().removeAll(markedTabs);
        writeTabs();
        tabPane.getTabs().remove(1);
        DC.saveProjects(projectList);
        DC.saveColumns(columnList);
        DC.saveTasks(taskList);
    }

    // this removes the default setting from the selected project if it is set as default, otherwise if displays a warning
    public void removeDefaultClick(){
        ArrayList<Project> projectList = this.projectsList;
        ArrayList<Column> columnList = this.columnsList;
        ArrayList<Task> taskList = this.tasksList;

        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
        for (Project project : projectsList) {
            if ((selectedTab.getText().equals(project.getProjectName())) && project.getUser().equals(loggedInUser) && project.getDefaultProject().equals(true)) {
                project.setDefaultProject(false);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText("The default setting was removed from the current tab.");
                alert.showAndWait();

                // the same process that happens when refreshing the tabs in the set default 
                passData(tasksList, columnsList, projectsList, usersMap, loggedInUser, motivationalMessage);
                tabPane.getSelectionModel().select((tabPane.getTabs().size() / 2));

                final ObservableList<Tab> tablist = tabPane.getTabs();
                ArrayList<Tab> markedTabs = new ArrayList<>();
                for (Tab tab : tablist) {
                    if (tab != tabPane.getSelectionModel().getSelectedItem())
                        markedTabs.add(tab);
                }
                tabPane.getTabs().removeAll(markedTabs);
                writeTabs();
                tabPane.getTabs().remove(1);
                DC.saveProjects(projectList);
                DC.saveColumns(columnList);
                DC.saveTasks(taskList);

            } else if ((selectedTab.getText().equals(project.getProjectName())) && project.getUser().equals(loggedInUser) && project.getDefaultProject().equals(false)) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Error");
                alert.setHeaderText("This tab is not set as default.");
                alert.showAndWait();
            }
        }
    }


    //method brings up a dialog box, and uses the input to create a new column on the ColumnsList
    public void newColumnClick() {
        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
        String selectedTabString = selectedTab.getText();
        TextInputDialog tiDialog = new TextInputDialog();
        tiDialog.setTitle("New Column");
        tiDialog.setHeaderText("Adding new column to: " + selectedTab.getText());
        tiDialog.setContentText("New column name: ");

        Optional<String> result = tiDialog.showAndWait();
        if (result.isPresent() && (!(result.get().equals("")))) {
            String input = result.get();

            // if the user inputs anything, all columnNames of column objects where the value of 'user' is the logged-in
            // user String and the value of 'project' is the selectedTabString (current project name) are added to a new
            // arraylist. if the new arraylist contains what the user inputted for the new column name, there will be an
            // error displayed to say the column name is already in use. If the new name is not in use, it will be added
            // to the columnsList with the user set as the logged-in user and the project set as the selected tab String
            ArrayList<String> columnNames = new ArrayList<>();
            for (Column column : columnsList) {
                if (column.getUser().equals(loggedInUser) && column.getProject().equals(selectedTabString)) {
                    columnNames.add(column.getColumnName());
                }
            }
            if (columnNames.contains(input)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Column Name in use");
                alert.showAndWait();
            } else {
                ArrayList<Column> columnList = this.columnsList;

                C.setColumnName(input);
                C.setProject(selectedTabString);
                C.setUser(loggedInUser);

                columnsList.add(new Column(C.getColumnName(), C.getProject(), C.getUser()));
                DC.saveColumns(columnList);
                writeColumns();
            }
        } else if (result.isPresent() && result.get().equals("")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No input was made");
            alert.showAndWait();
        }
    }

    public void changeSceneToCreateTask(String currentProject, String currentColumn) throws IOException{
        ArrayList<Project> projectList = this.projectsList;
        ArrayList<Column> columnList = this.columnsList;
        ArrayList<Task> taskList = this.tasksList;
        HashMap<String, User> userMap = this.usersMap;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("createTask.fxml"));
        root = loader.load();

        createTaskController taskController = loader.getController();
        taskController.passData(taskList, columnList, projectList, userMap, loggedInUser, currentProject, currentColumn, motivationalMessage);

        Stage stage = (Stage) editProfile.getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("New Task");
        stage.show();
    }

    public void changeSceneToEditTask(String currentProject, String currentColumn, String currentTask) throws IOException {
        ArrayList<Project> projectList = this.projectsList;
        ArrayList<Column> columnList = this.columnsList;
        ArrayList<Task> taskList = this.tasksList;
        HashMap<String, User> userMap = this.usersMap;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("editTask.fxml"));
        root = loader.load();

        editTaskController taskController = loader.getController();
        taskController.passData(taskList, columnList, projectList, userMap, loggedInUser, currentProject, currentColumn, currentTask, motivationalMessage);

        Stage stage = (Stage) editProfile.getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Edit Task");
        stage.show();

    }

    // changing scene and passing data to editor for adjusting
    public void editProfileClick() throws IOException {
        ArrayList<Project> projectList = this.projectsList;
        HashMap<String, User> userMap = this.usersMap;
        ArrayList<Column> columnList = this.columnsList;
        ArrayList<Task> taskList = this.tasksList;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("profileEditor.fxml"));
        root = loader.load();

        profileEditorController EditorController = loader.getController();
        EditorController.passData(taskList, columnList, projectList, userMap, loggedInUser, motivationalMessage);

        Stage stage = (Stage) editProfile.getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Profile Editor");
        stage.show();
    }

    // returning to login page and passing data
    public void logoutButtonClick() throws IOException {
        HashMap<String, User> userMap = this.usersMap;
        ArrayList<Project> projectList = this.projectsList;
        ArrayList<Column> columnList = this.columnsList;
        ArrayList<Task> taskList = this.tasksList;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
        root = loader.load();

        loginController LoginController = loader.getController();
        LoginController.passData(taskList, columnList, projectList, userMap);

        Stage stage = (Stage) logoutButton.getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Login");
        stage.show();
    }

    @FXML
    public void initialize(){
        // creating a persistent Listener to run the writeColumns() method whenever the value of the selected tab changes.
        // of course, this method will perform differently depending on the value of the selected tab, and will write the
        // relevant column nodes to the selected project tabContent.
        tabPane.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<Tab>() {
                    @Override
                    public void changed(ObservableValue<? extends Tab> ov, Tab t, Tab t1) {
                        writeColumns();
                    }});

    }
}