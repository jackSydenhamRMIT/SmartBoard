package com.example.smartboardjacksydenham;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.Serializable;

/**
 * The only purpose of this class is to launch the program to the login page when ran, after calling the createTables() method
 */

public class Launcher extends Application implements Serializable{

    DatabaseController DC = new DatabaseController();

    // method to launch the program, displaying the Login Page
    @Override
    public void start(Stage stage) throws IOException {

        // creates missing tables
        DC.createTables();

        Parent root = FXMLLoader.load(getClass().getResource("login.fxml")); //the login page fxml file is called,
        Scene scene = new Scene(root);

        stage.setScene(scene); //set as the current scene,
        stage.setTitle("Login"); //given a title,
        stage.show(); //and displayed

    }

    public static void main(String[] args) {
        launch(args);
    }

}
