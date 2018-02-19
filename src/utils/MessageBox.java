package utils;

import javafx.scene.control.Alert;
import validator.ValidationException;

public class MessageBox {
    public static void showMessage(Alert.AlertType type, String header, String text){
        Alert message=new Alert(type);
        message.setHeaderText(header);
        message.setContentText(text);
        message.showAndWait();
    }

    public static void showErrorMessage(String text,String title){
        Alert message=new Alert(Alert.AlertType.ERROR);
        message.setHeaderText(text);
        message.setTitle(title);
        message.showAndWait();
    }
    public static void display_error(ValidationException ex){
        switch(ex.getCode()){
            case 100:
                MessageBox.showErrorMessage("Username uses characters not allowed.","Account error.");
                break;
            case 101:
                MessageBox.showErrorMessage("Email format not valid.","Account error.");
                break;
            case 102:
                MessageBox.showErrorMessage("Calories must be a positive integer","Account error.");
                break;
            case 103:
                MessageBox.showErrorMessage("Account password too weak","Account error.");
                break;
            case 104:
                MessageBox.showErrorMessage("Account id not found.","Account error.");
                break;
            case 107:
                MessageBox.showErrorMessage("Username or email already exists.","Account error.");
                break;
            case 250:
                MessageBox.showErrorMessage("Account id not found.","ID error.");
                break;
            case 200:
                MessageBox.showErrorMessage("Incorrect account id.","ID error.");
                break;
            case 105:
                MessageBox.showErrorMessage("Calories cannot be null or 0.","Calories error.");
                break;
            case 400:
                MessageBox.showErrorMessage("Database connection lost.","Database error.");
                break;
            case 500:
                MessageBox.showErrorMessage("Permission level not high enough.","Permission issue.");
                break;
            case 600:
                MessageBox.showErrorMessage("Database connection lost.","Database error.");
                break;
            case 700:
                MessageBox.showErrorMessage("Error saving recipe.","Database error.");
                break;
            default:
                MessageBox.showErrorMessage(ex.getMessage(),"Error.");
                break;
        }
    }
}
