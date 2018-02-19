package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerSlideCloseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import main.Main;
import service.Service;
import utils.Observer;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ControllerMenu implements Observer,ControlledScreen{
    private ScreenController screenController;
    private Service service;
    HamburgerSlideCloseTransition hsct;
    @FXML
    private JFXHamburger hamburger;
    @FXML
    private JFXDrawer drawer;
    @FXML
    private JFXButton button_plan;
    @FXML
    private JFXButton button_statistics;

    private void init_drawer(){
        try{

            VBox toolbar = (VBox) screenController.getScreen(Main.screen_drawerID);
            drawer.setSidePane(toolbar);
        }catch (Exception ex){
            Logger.getLogger(ControllerMenu.class.getName()).log(Level.SEVERE,null,ex);
        }
        hsct = new HamburgerSlideCloseTransition(hamburger);
        hsct.setRate(-1);
        hamburger.addEventHandler(MouseEvent.MOUSE_CLICKED, (e)-> {
            hsct.setRate(hsct.getRate()*-1);
            hsct.play();
            if(drawer.isHidden())
                drawer.open();
            else
                drawer.close();

        });

    }

    public void reset_view(){
        drawer.close();
        hsct.setRate(-1);
        hsct.play();
    }

    @Override
    public void setService(Service service) {
        this.service = service;
        this.service.addObserver(this);
    }

    public void stop_app(){
        Platform.exit();
    }

    public void display_menu_ingredients(){
        screenController.setScreen(Main.screen_ingredientsID);
        //prepare the stage for the view
        ControllerIngredients controllerIngredients = (ControllerIngredients) screenController.getScreenController(Main.screen_ingredientsID);
        controllerIngredients.load_view();
        controllerIngredients.createPagination();
    }
    public void display_menu_all_ingredients(){
        screenController.setScreen(Main.screen_ingredients_all_ID);
        //prepare the stage for the view
        ControllerAllIngredients controllerIngredients = (ControllerAllIngredients) screenController.getScreenController(Main.screen_ingredients_all_ID);
        controllerIngredients.load_view();
        controllerIngredients.createPagination();
    }
    public void display_menu_all_recipes(){
        screenController.setScreen(Main.screen_recipesAllID);
        //prepare the stage for the view

        ControllerAllRecipes controllerAllRecipes = (ControllerAllRecipes) screenController.getScreenController(Main.screen_recipesAllID);
        controllerAllRecipes.load_view();
        controllerAllRecipes.createPagination();
    }
    public void display_menu_recipes(){
        screenController.setScreen(Main.screen_recipesID);
        //prepare the stage for the view
        ControllerRecipes controllerRecipes = (ControllerRecipes) screenController.getScreenController(Main.screen_recipesID);
        controllerRecipes.load_view();
        controllerRecipes.createPagination();
    }
    @Override
    public void setScreenParent(ScreenController screenController) {
        this.screenController = screenController;
        init_drawer();
    }

    @Override
    public void update() {

    }

    public void initialize(){
        button_plan.setDisable(true);
        button_statistics.setDisable(true);
    }



}
