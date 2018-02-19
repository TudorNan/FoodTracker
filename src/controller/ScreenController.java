package controller;

import java.util.HashMap;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import main.Main;
import service.Service;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ScreenController extends StackPane {
    private HashMap<String, ViewDetails> screens = new HashMap<>();

    private Service service;
    private Integer current_account_id;
    private Stage primaryStage;
    public Integer getCurrent_account_id(){
        return current_account_id;
    }

    public ScreenController(Service service,Stage primaryStage) {
        super();
        this.service = service;
        this.primaryStage = primaryStage;
    }

    public void setCurrent_account_id(Integer id){
        this.current_account_id = id;
        service.notify_controllers();
    }
    public Stage getPrimaryStage(){
        return primaryStage;
    }

    public void addScreen(String name, Node screen,ControlledScreen controller,Integer heigth,Integer width) {
        screens.put(name, new ViewDetails(screen,controller,heigth,width));
    }

    public Node getScreen(String name) {
        return screens.get(name).getNode();
    }
    public ControlledScreen getScreenController(String name){
        return screens.get(name).getControlledScreen();
    }

    public void logout(){
        screens.forEach((k,v)->v.getControlledScreen().reset_view());
        setCurrent_account_id(-1);
        setScreen(Main.screen_loginID);
        service.notify_controllers();
    }
    public boolean loadScreen(String name, String resource,Integer heigth,Integer width) {
        try {
            FXMLLoader myLoader = new FXMLLoader(getClass().getResource(resource));
            Parent loadScreen = (Parent) myLoader.load();
            ControlledScreen screen = ((ControlledScreen) myLoader.getController());
            screen.setScreenParent(this);
            screen.setService(service);
            addScreen(name,loadScreen,screen,heigth,width);
            return true;
        } catch (Exception e) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, e);
            return false;
        }
    }

    public boolean setScreen(final String name) {
        //todo set dimensions hashmap
        if (screens.get(name) != null) {   //screen exists
            final DoubleProperty opacity = opacityProperty();
            if (!getChildren().isEmpty()) {    //at least a screen
                Timeline fade = new Timeline(
                        new KeyFrame(Duration.ZERO, new KeyValue(opacity, 1.0)),

                        new KeyFrame(new Duration(400), t -> {

                            getChildren().remove(0);

                            getChildren().add(0, screens.get(name).getNode());

                            Timeline fadeIn = new Timeline(
                                    new KeyFrame(Duration.ZERO, (e)->{primaryStage.setWidth(screens.get(name).getScreenWidth());
                                        primaryStage.setHeight(screens.get(name).getScreenHeight());
                                        primaryStage.centerOnScreen();},new KeyValue(opacity, 0.0)),

                                    new KeyFrame(new Duration(400), new KeyValue(opacity, 1.0)));

                            fadeIn.play();

                        }, new KeyValue(opacity, 0.0)));
                fade.play();

            } else {
                setOpacity(0.0);
                primaryStage.setWidth(screens.get(name).getScreenWidth());
                primaryStage.setHeight(screens.get(name).getScreenHeight());
                primaryStage.centerOnScreen();
                getChildren().add(screens.get(name).getNode());       //no other frame displayed
                Timeline fadeIn = new Timeline(
                        new KeyFrame(Duration.ZERO, new KeyValue(opacity, 0.0)),
                        new KeyFrame(new Duration(2500), new KeyValue(opacity, 1.0)));
                fadeIn.play();
            }
            return true;
        } else {
            System.out.println("Screen not loaded yet!!! \n");
            return false;
        }

    }
    public boolean unloadScreen(String name) {
        if (screens.remove(name) ==null) {
            System.out.println("Screen doesn't exist.");
            return false;
        } else {

            return true;
        }
    }
}
