package com.example.smartboardjacksydenham;

import javafx.application.Application;
import javafx.scene.image.Image;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * This class is used to test various methods used throughout the program. Tests should only be run one at a time, as
 * stated in the @Before method
 */

public class ControllerTest  {

    // new instances of Lists/Maps
    HashMap<String, User> usersMap = new HashMap<>();
    ArrayList<Project> projectsList = new ArrayList<>();
    ArrayList<Column> columnsList = new ArrayList<>();
    ArrayList<Task> tasksList = new ArrayList<>();

    //creating default profile picture File object for testing
    final File defaultPfp = new File("src/main/resources/com/example/smartboardjacksydenham/content/default.png");

    // method to launch application before a test is run to avoid the 'internal graphics not yet initialised' error
    // because of this however, tests should only be run ONE AT A TIME
    @Before
    public void setUp() throws Exception{
        Thread t = new Thread("Smartboard App Thread") {
            public void run() {
                Application.launch(Launcher.class);
            }
        };
        t.setDaemon(true);
        t.start();
        System.out.println("Application Launched\n");
        Thread.sleep(500);
    }

    // test to see that an arbitrary amount of users can be created and stored
    @Test
    public void profileCreationLimitTest(){
        User U = new User();
        int count = 1;
        for (int i = 0; i < 999; i++) {
            U.setFirstname("Jack");
            U.setLastname("Sydenham");
            U.setUsername("User " + count);
            U.setPassword("pass");
            U.setPfp(new Image(defaultPfp.toURI().toString()));

            usersMap.put(U.getUsername(), new User(U.getFirstname(), U.getLastname(), U.getUsername(), U.getPassword(), U.getPfp()));
            count++;
        }

        assertTrue(usersMap.containsKey("User 1") && usersMap.get("User 1") != null);
        assertTrue(usersMap.containsKey("User 999") && usersMap.get("User 999") != null);
    }

    // test to ensure username duplicates cannot happen
    @Test
    public void avoidDuplicateUsernames(){
        User U = new User();

        // creating user details variables
        String firstname;
        String lastname;
        String username;
        String password;


        // this acts as the users input for the profile creation
        firstname = "Jack";
        lastname = "Sydenham";
        username = "jj";
        password = "jackpass";

        // if the inputted username already exists as a key within the map, an error message is displayed. If it doesn't
        // a new user object is created with the inputted data and added to the map
         if (usersMap.containsKey(username)) {
             System.out.println("Username already in use...");
        } else {
             U.setFirstname(firstname);
             U.setLastname(lastname);
             U.setUsername(username);
             U.setPassword(password);
             U.setPfp((new Image(defaultPfp.toURI().toString())));

             usersMap.put(U.getUsername(), new User(U.getFirstname(), U.getLastname(), U.getUsername(), U.getPassword(), U.getPfp()));
         }

        firstname = "Daniel";
        lastname = "Ottrey";
        username = "DaneeBoi";
        password = "dan120";

        if (usersMap.containsKey(username)) {
            System.out.println("Username already in use...");
        } else {
            U.setFirstname(firstname);
            U.setLastname(lastname);
            U.setUsername(username);
            U.setPassword(password);
            U.setPfp((new Image(defaultPfp.toURI().toString())));

            usersMap.put(U.getUsername(), new User(U.getFirstname(), U.getLastname(), U.getUsername(), U.getPassword(), U.getPfp()));
        }

        // using a username that already exists. The error message is printed, and the user should NOT be added
        firstname = "Jonny";
        lastname = "O'Neil";
        username = "jj";
        password = "waltz";

        if (usersMap.containsKey(username)) {
            System.out.println("Username already in use...");
        } else {
            U.setFirstname(firstname);
            U.setLastname(lastname);
            U.setUsername(username);
            U.setPassword(password);
            U.setPfp((new Image(defaultPfp.toURI().toString())));

            usersMap.put(U.getUsername(), new User(U.getFirstname(), U.getLastname(), U.getUsername(), U.getPassword(), U.getPfp()));
        }

        // asserting that only two items were added to the usersMap, and that the firstname associated with the user jj
        // is NOT "Jonny". the error message also prints once, meaning one user was denied creation (the second jj)
        assertTrue(usersMap.size() == 2);
        assertFalse(usersMap.get("jj").getFirstname().equals("Jonny"));
    }

    // test for editing user
    @Test
    public void editProfileTest(){
        User U = new User();

        // simulating David being logged-in
        String loggedInUser = "Cloud37";

        User David = new User("David", "Moss", "Cloud37", "Elephant", (new Image(defaultPfp.toURI().toString())));
        User Jonathan = new User("Jonathan", "Kent", "JKent102", "Jonny102", (new Image(defaultPfp.toURI().toString())));
        usersMap.put(David.getUsername(), David);
        usersMap.put(Jonathan.getUsername(), Jonathan);

        // checking that users are in the map, and David's account has correct first name and lastname
        assertTrue(usersMap.containsKey("Cloud37") && usersMap.containsKey("JKent102"));
        assertTrue(usersMap.get(loggedInUser).getFirstname().equals("David") && usersMap.get(loggedInUser).getLastname().equals("Moss"));

        // since there can only be one value on the map with key "Cloud37", creating a new user with the new information
        // will simply overwrite the "Cloud37" user data
        U.setFirstname("James"); // user's new input
        U.setLastname("Beam"); // user's new input
        U.setUsername("Cloud37"); // cannot be edited, this remains unchanged
        U.setPassword("Elephant"); // also, cannot be edited
        U.setPfp((new Image(defaultPfp.toURI().toString())));
        usersMap.put(U.getUsername(), new User(U.getFirstname(), U.getLastname(), U.getUsername(), U.getPassword(), U.getPfp()));

        assertTrue(usersMap.containsKey("Cloud37") && usersMap.containsKey("JKent102"));
        assertTrue(usersMap.get(loggedInUser).getFirstname().equals("James") && usersMap.get(loggedInUser).getLastname().equals("Beam"));
        assertFalse(usersMap.get(loggedInUser).getFirstname().equals("David") && usersMap.get(loggedInUser).getLastname().equals("Moss"));

    }

    // test for deleting users
    // this test creates multiple users, projects, columns, and tasks, then deletes a user, and in turn, removes their
    // associated projects, columns, and tasks as well
    @Test
    public void deleteUserTest(){

        // simulating Luke being logged-in
        String loggedInUser = "Luke";

        User Jack = new User("Jack", "Sydenham", "Jack", "password", (new Image(defaultPfp.toURI().toString())));
        User Luke = new User("Luke", "Senn", "Luke", "password", (new Image(defaultPfp.toURI().toString())));
        usersMap.put(Jack.getUsername(), Jack);
        usersMap.put(Luke.getUsername(), Luke);

        Project jackProject = new Project("jackProject", "Jack", true);
        Project lukeProject = new Project("lukeProject", "Luke", true);
        projectsList.add(jackProject);
        projectsList.add(lukeProject);

        Column jackColumn = new Column("jackColumn", "jackProject", "Jack");
        Column lukeColumn = new Column("lukeColumn", "lukeProject", "Luke");
        columnsList.add(jackColumn);
        columnsList.add(lukeColumn);

        Task jackTask = new Task("jackTask", "this task in on jacks user account", false, null, false, "jackProject", "jackColumn", "Jack");
        Task lukeTask = new Task("lukeTask", "this task in on lukes user account", false, null, false, "lukeProject", "lukeColumn", "Luke");
        tasksList.add(jackTask);
        tasksList.add(lukeTask);

        // check to see that all items are on their respective lists
        assertTrue(usersMap.containsKey("Jack") && usersMap.containsKey("Luke"));
        assertTrue(projectsList.contains(jackProject) && projectsList.contains(lukeProject));
        assertTrue(columnsList.contains(jackColumn) && columnsList.contains(lukeColumn));
        assertTrue(tasksList.contains(jackTask) && tasksList.contains(lukeTask));

        usersMap.remove(loggedInUser);
        projectsList.removeIf(Projects -> Objects.equals(Projects.getUser(), loggedInUser));
        columnsList.removeIf(Column -> Objects.equals(Column.getUser(), loggedInUser));
        tasksList.removeIf(Task -> Objects.equals(Task.getUser(), loggedInUser));

        // with the assertion now being that all luke related items are NOT(!) in their maps, the test passes, meaning
        // all luke items have been removed
        assertTrue(usersMap.containsKey("Jack") && !(usersMap.containsKey("Luke")));
        assertTrue(projectsList.contains(jackProject) && !(projectsList.contains(lukeProject)));
        assertTrue(columnsList.contains(jackColumn) && !(columnsList.contains(lukeColumn)));
        assertTrue(tasksList.contains(jackTask) && !(tasksList.contains(lukeTask)));
    }

    // test for setting default project. 'Tester' Project on Jack user will be set to default. It should set all other
    // Projects on jack user to not default, and NOT set 'Tester' project on Luke user to default
    @Test
    public void setUnsetDefaultProject(){

        Project jackProject1 = new Project("jackProject1", "Jack", true);
        Project jackProject2 = new Project("jackProject2", "Jack", false);
        Project jackDefaultProject = new Project("Tester", "Jack", false);
        Project lukeProject1 = new Project("lukeProject1", "Luke", true);
        Project lukeDefaultProject = new Project("Tester", "Luke", false);
        projectsList.add(jackProject1);
        projectsList.add(jackProject2);
        projectsList.add(jackDefaultProject);
        projectsList.add(lukeProject1);
        projectsList.add(lukeDefaultProject);

        assertTrue(projectsList.get(0).getDefaultProject().equals(true)); // jackProject1 has default true
        assertTrue(projectsList.get(3).getDefaultProject().equals(true)); // lukeProject1 has default true

        String loggedInUser = "Jack"; // simulating loggedInUser
        String selectedTabName = "Tester"; // simulating current selected Project

        for (Project project : projectsList) {
            if (project != null && (!(selectedTabName.equals(project.getProjectName()))) && project.getUser().equals(loggedInUser)) {
                project.setDefaultProject(false);
            }
            if (project != null && selectedTabName.equals(project.getProjectName()) && project.getUser().equals(loggedInUser)) {
                project.setDefaultProject(true);
            }
        }

        assertTrue(projectsList.get(2).getDefaultProject().equals(true)); // Tester project on the Jack user was set to default
        assertTrue(projectsList.get(0).getDefaultProject().equals(false)); // jackProject1 was set to false as planned
        assertTrue(projectsList.get(3).getDefaultProject().equals(true)); // lukeProject1 default setting remains unchanged despite having the same name as the project modified on the Jack user

        // setting the now default 'Tester' Project to be not default
        for (Project project : projectsList) {
            if (project != null && (selectedTabName.equals(project.getProjectName())) && project.getUser().equals(loggedInUser)) {
                project.setDefaultProject(false);
            }
        }

        // one final assertion reveals that the 'Tester' Project on the Jack user has been set to not default, while the
        // 'Tester' Project on the Luke user is still set to default
        assertTrue(projectsList.get(0).getDefaultProject().equals(false)); // jackProject1
        assertTrue(projectsList.get(1).getDefaultProject().equals(false)); // jackProject2
        assertTrue(projectsList.get(2).getDefaultProject().equals(false)); // Tester (Jack)
        assertTrue(projectsList.get(3).getDefaultProject().equals(true)); // lukeProject1
        assertTrue(projectsList.get(4).getDefaultProject().equals(false)); // Tester (Luke)
    }



    @Test
    public void createButtonClick() {
        System.out.println("kms");
    }
}