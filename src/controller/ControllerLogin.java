package controller;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import domain.Account;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import main.Main;
import service.Service;
import utils.MessageBox;
import utils.Observer;
import validator.ValidationException;

public class ControllerLogin implements Observer,ControlledScreen{
    private ScreenController screenController;
    private Service service;
    @FXML
    Label label_incorrect;
    @FXML
    JFXTextField textField_user;
    @FXML
    JFXPasswordField passwordField_pass;
    @Override
    public void setService(Service service) {
        this.service = service;
        this.service.addObserver(this);
    }
    public void login(){
        Account account;
        try {
            account = service.login(textField_user.getText().toString(),passwordField_pass.getText().toString());
            screenController.setCurrent_account_id(account.getId());
            screenController.setScreen(Main.screen_mainID);

        } catch (ValidationException e) {
            if(e.getCode().equals(106))
                label_incorrect.setVisible(true);
            else{
                MessageBox.display_error(e);
            }
        }

    }
    public void stop_app(){
        Platform.exit();
    }
    @Override
    public void setScreenParent(ScreenController screenController) {
        this.screenController = screenController;
    }
    @Override
    public void update() {

    }
    public void register(){
        screenController.setScreen(Main.screen_registerID);
    }
    public void initialize(){
        label_incorrect.setVisible(false);
    }
    public void reset_view(){
        label_incorrect.setVisible(false);
        textField_user.clear();
        passwordField_pass.clear();
    }



}
