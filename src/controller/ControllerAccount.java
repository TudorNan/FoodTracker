package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import domain.Account;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import main.Main;
import service.Service;
import utils.Effects;
import utils.MessageBox;
import utils.Observer;
import utils.Updatemode;
import validator.ValidationException;


public class ControllerAccount implements Observer,ControlledScreen{
    private ScreenController screenController;
    private Service service;
    private Updatemode updatemode;
    @FXML
    JFXButton button_change;
    @FXML
    Label message;
    @FXML
    JFXButton button_back;
    @FXML
    JFXButton button_change_password;
    @FXML
    JFXButton button_change_email;
    @FXML
    JFXButton button_change_goal;

    @FXML
    JFXPasswordField passwordField_1;
    @FXML
    JFXPasswordField passwordField_2;

    @FXML
    JFXTextField textField_1;
    @FXML
    JFXTextField textField_2;

    @FXML
    Label currentuser;
    @FXML
    Label currentgoal;
    @FXML
    Label currentemail;


    @Override
    public void setService(Service service) {
        this.service = service;
        this.service.addObserver(this);
    }
    public void button_main_menu(){
        screenController.setScreen(Main.screen_mainID);

    }
    @Override
    public void reset_view() {
        message.setVisible(false);
        updatemode = null;
        button_change.setVisible(false);
        textField_1.setPromptText("");
        textField_1.setVisible(false);
        textField_2.setPromptText("");
        textField_2.setVisible(false);
        passwordField_1.setPromptText("");
        passwordField_1.setVisible(false);
        passwordField_2.setPromptText("");
        passwordField_2.setVisible(false);
        currentemail.setText("");
        currentgoal.setText("");
        currentuser.setText("");
        textField_1.setText("");
        textField_2.setText("");
        passwordField_1.setText("");
        passwordField_2.setText("");
    }

    /*
    * Displays the account details in the view.
    * */
    public void display_account_details(){
        Account account;
        try {
            account = service.get_account_details(screenController.getCurrent_account_id());
            currentgoal.setText(String.valueOf(account.getKcalGoal()));
            currentemail.setText(String.valueOf(account.getEmail()));
            currentuser.setText(String.valueOf(account.getUsername()));
        } catch (ValidationException e) {
            reset_view();
        }
    }

    public void stop_app(){
        Platform.exit();
    }
    @Override
    public void setScreenParent(ScreenController screenController) {
        this.screenController = screenController;

    }

    public void prepare_change_password(){
        reset_view();
        display_account_details();
        passwordField_1.setPromptText("Old password.");
        passwordField_2.setPromptText("New password.");
        Effects.fadeInObject(passwordField_1,3000);
        Effects.fadeInObject(passwordField_2,3000);
        Effects.fadeInObject(button_change,3000);
        updatemode = Updatemode.PASSWORD;

    }
    public void prepare_change_email(){
        reset_view();
        display_account_details();
        passwordField_1.setPromptText("Current password.");
        Effects.fadeInObject(passwordField_1,3000);
        textField_2.setPromptText("New email address");
        Effects.fadeInObject(textField_2,3000);
        Effects.fadeInObject(button_change,3000);
        updatemode = Updatemode.EMAIL;

    }
    public void prepare_change_goal(){
        reset_view();
        display_account_details();
        textField_1.setPromptText("New calories goal");
        Effects.fadeInObject(textField_1,3000);
        Effects.fadeInObject(button_change,3000);
        updatemode = Updatemode.KCAL;
    }
    public void button_change(){
        if(updatemode.equals(Updatemode.PASSWORD))
            change_password();
        else if(updatemode.equals(Updatemode.EMAIL))
            change_email();
        else if (updatemode.equals(Updatemode.KCAL))
            change_goal();

    }

    public void change_goal(){
        Integer new_goal = 0;
        try{
            new_goal = Integer.parseInt(textField_1.getText());
        }catch (Exception e){
            MessageBox.showErrorMessage("Invalid calories format, must be an integer.","Calories goal error.");
            return;
        }
        try {
            service.update_account_goal(screenController.getCurrent_account_id(), new_goal);
            message.setText("Goal successfully changed.");
            reset_view();
            display_account_details();
            Effects.fadeInAndOutObject(message,3000);
        } catch (ValidationException e) {
            MessageBox.display_error(e);
        }
    }
    public void change_email() {
        String password = passwordField_1.getText();
        String new_email = textField_2.getText();
        try {
            service.update_account_email(screenController.getCurrent_account_id(),password,new_email);
            message.setText("Email successfully changed.");
            reset_view();
            display_account_details();
            Effects.fadeInAndOutObject(message,3000);
        } catch (ValidationException e) {
            if(e.getCode().equals(106))
                MessageBox.showErrorMessage("Invalid password","Change unsuccessfull.");
            else
                MessageBox.display_error(e);
        }
    }

    public void change_password(){
        String old_password = passwordField_1.getText();
        String new_password = passwordField_2.getText();
        try {
            service.update_account_password(screenController.getCurrent_account_id(),old_password,new_password);
            reset_view();
            display_account_details();
            message.setText("Password successfully changed.");
            message.setVisible(false);
            Effects.fadeInAndOutObject(message,3000);
        } catch (ValidationException e) {
            if(e.getCode().equals(106))
                MessageBox.showErrorMessage("Invalid old password.","Change unsucccessfull.");
            else
                MessageBox.display_error(e);

        }


    }
    @Override
    public void update() {
        display_account_details();
    }
    public void register(){

        screenController.setScreen(Main.screen_registerID);
    }
    public void initialize(){
        reset_view();

    }



}
