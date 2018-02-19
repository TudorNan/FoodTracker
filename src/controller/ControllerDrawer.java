package controller;

import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import main.Main;
import service.Service;
import utils.Observer;

public class ControllerDrawer implements Observer,ControlledScreen{
    private ScreenController screenController;
    private Service service;

    @FXML
    JFXButton logout;
    @FXML
    JFXButton myaccount;
    @FXML
    JFXButton settings;

    @Override
    public void reset_view() {

    }

    @Override
    public void setScreenParent(ScreenController screenController) {
        this.screenController = screenController;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public void account_button(){
        ControllerAccount controllerAccount = (ControllerAccount) screenController.getScreenController(Main.screen_accountViewID);
        controllerAccount.display_account_details();
        ControllerMenu controllerMenu = (ControllerMenu) screenController.getScreenController(Main.screen_mainID);
        controllerMenu.reset_view();
        screenController.setScreen(Main.screen_accountViewID);
    }

    public void logout(){

        screenController.logout();
        screenController.setCurrent_account_id(-1);
    }

    public void initialize(){

    }

    @Override
    public void update() {

    }
}
