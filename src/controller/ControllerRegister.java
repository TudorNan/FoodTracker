package controller;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.util.Duration;
import main.Main;
import service.Service;
import utils.Effects;
import utils.MessageBox;
import utils.Observer;
import validator.ValidationException;

import java.sql.SQLException;


public class ControllerRegister implements ControlledScreen, Observer {
    private Service service;
    private ScreenController screenController;
    PauseTransition visiblePause;
    @FXML
    JFXTextField textField_user;
    @FXML
    JFXTextField textField_email;

    @FXML
    JFXPasswordField passwordField_pass;
    @FXML
    JFXPasswordField passwordField_pass2;

    @FXML
    Label label_registered;

    //Sets the current screen to the login screen
    public void change_screen_login(){
        screenController.setScreen(Main.screen_loginID);
    }

    @Override
    public void reset_view() {
        label_registered.setVisible(false);
        textField_email.clear();
        textField_user.clear();
        passwordField_pass2.clear();
        passwordField_pass.clear();
    }

    @Override
    public void setScreenParent(ScreenController screenController) {
        this.screenController = screenController;
    }

    public void register() {
        if(textField_email.getText().isEmpty()) {
            MessageBox.showErrorMessage("Email field must be filled.","Registration error.");
            return;
        }

        if (passwordField_pass.getText().isEmpty() || passwordField_pass2.getText().isEmpty())
        {
            MessageBox.showErrorMessage("Password must be specified.","Registration error.");
            return;
        }
        if(!passwordField_pass.getText().equals(passwordField_pass2.getText())){
            MessageBox.showErrorMessage("Password mismatch.","Registration error.");
            return;
        }
        try {
            service.register(textField_user.getText(),textField_email.getText(),passwordField_pass.getText());
            label_registered.setVisible(true);
            Effects.fadeOutObject(label_registered,3000);
            textField_email.clear();
            textField_user.clear();
            passwordField_pass2.clear();
            passwordField_pass.clear();
            change_screen_login();
        } catch (ValidationException e) {
            MessageBox.display_error(e);
        }
    }
    public void initialize(){
        label_registered.setVisible(false);

    }
    @Override
    public void setService(Service service) {
        this.service = service;
        service.addObserver(this);
    }

    @Override
    public void update() {

    }
}
