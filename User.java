package com.example.smartboardjacksydenham;

import javafx.scene.image.Image;

import java.io.Serializable;
import java.util.HashMap;

/**
 * This class is used as the model for the User object used throughout the program.
 */

public class User implements Serializable {

    HashMap<String, User> usersMap = new HashMap<>();

    private String firstname;
    private String lastname;
    private String username;
    private String password;
    private Image pfp;


    public User() {
    }

    public User(String firstname, String lastname, String username, String password, Image pfp) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
        this.password = password;
        this.pfp = pfp;

    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Image getPfp() {
        return pfp;
    }

    public void setPfp(Image pfp) {
        this.pfp = pfp;
    }

    public HashMap<String, User> getUsersMap() {
        return usersMap;
    }


    @Override
    public String toString() {
        return "Users{" +
                "firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", pfp=" + pfp +
                '}';
    }

}
