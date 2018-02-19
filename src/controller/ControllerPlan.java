package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import main.Main;
import service.Service;
import utils.Observer;

public class ControllerPlan implements Observer,ControlledScreen{
    private ScreenController screenController;
    private Service service;

    @Override
    public void setService(Service service) {
        this.service = service;
        this.service.addObserver(this);
    }

    @Override
    public void reset_view() {

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

    }



}
