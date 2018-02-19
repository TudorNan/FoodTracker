package controller;

import javafx.scene.Node;

public class ViewDetails {
    private Node node;
    private ControlledScreen controlledScreen;
    private Integer screenHeight;
    private Integer screenWidth;

    public ViewDetails(Node node, ControlledScreen controlledScreen, Integer screenHeight, Integer screenWidth) {
        this.node = node;
        this.controlledScreen = controlledScreen;
        this.screenHeight = screenHeight;
        this.screenWidth = screenWidth;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public ControlledScreen getControlledScreen() {
        return controlledScreen;
    }

    public void setControlledScreen(ControlledScreen controlledScreen) {
        this.controlledScreen = controlledScreen;
    }

    public Integer getScreenHeight() {
        return screenHeight;
    }

    public void setScreenHeight(Integer screenHeight) {
        this.screenHeight = screenHeight;
    }

    public Integer getScreenWidth() {
        return screenWidth;
    }

    public void setScreenWidth(Integer screenWidth) {
        this.screenWidth = screenWidth;
    }
}
