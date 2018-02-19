package utils;

import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.util.Duration;

public class Effects{
    public static void fadeOutObject(Node node,Integer duration){
        FadeTransition ft = new FadeTransition(Duration.millis(duration), node);
        node.toFront();
        ft.setFromValue(1.0);
        ft.setToValue(0);
        ft.play();
    }
    public static void fadeInObject(Node node,Integer duration){
        FadeTransition ft = new FadeTransition(Duration.millis(duration), node);
        node.toFront();
        node.setVisible(true);
        ft.setFromValue(0);
        ft.setToValue(1.0);
        ft.play();
    }
    public static void fadeInAndOutObject(Node node,Integer duration){
        FadeTransition ft = new FadeTransition(Duration.millis(duration), node);
        node.toFront();
        node.setVisible(true);
        ft.setFromValue(0);
        ft.setToValue(1.0);
        ft.setAutoReverse(true);
        ft.setCycleCount(2);
        ft.play();
    }

}