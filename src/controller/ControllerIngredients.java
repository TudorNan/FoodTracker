package controller;

import com.jfoenix.controls.JFXTreeTableView;
import domain.Ingredient;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import main.Main;
import service.Service;
import utils.Effects;
import utils.MessageBox;
import utils.Observer;
import validator.ValidationException;

public class ControllerIngredients implements Observer,ControlledScreen{
    private ScreenController screenController;
    private Service service;
    private ObservableList<Ingredient> model = FXCollections.observableArrayList();
    public static Integer ITEMS_PER_PAGE = 15;


    @FXML
    TextField textField_name;
    @FXML
    TextField textField_calories;
    @FXML
    TextField textField_fats;
    @FXML
    TextField textField_fibers;
    @FXML
    TextField textField_proteins;
    @FXML
    TextField textField_carbohydrates;

    @FXML
    TableColumn column_name;
    @FXML
    TableColumn column_carbs;
    @FXML
    TableColumn column_proteins;
    @FXML
    TableColumn column_fats;
    @FXML
    TableColumn column_calories;
    @FXML
    TableColumn column_fibers;
    @FXML
    TableView tableView;

    @FXML
    Label label_success;
    @FXML
    Pagination pagination;

    @Override
    public void setService(Service service) {
        this.service = service;
        this.service.addObserver(this);

    }

    @Override
    public void reset_view() {
        textField_name.clear();
        textField_proteins.clear();
        textField_fats.clear();
        textField_carbohydrates.clear();
        textField_fibers.clear();
        textField_calories.clear();
    }

    @Override
    public void setScreenParent(ScreenController screenController) {
        this.screenController = screenController;
    }

    @Override
    public void update() {
        try {
            model = FXCollections.observableArrayList(service.get_all_ingredients_for_account(screenController.getCurrent_account_id()));
            reset_pagination();
        } catch (ValidationException e) {
            MessageBox.display_error(e);
        }
    }

    public void register(){

        screenController.setScreen(Main.screen_registerID);
    }

    public void post_initialize(){

    }

    public void fill_fields(){
        Ingredient ing = (Ingredient)tableView.getSelectionModel().getSelectedItem();
        textField_name.setText(ing.getName());
        textField_carbohydrates.setText(ing.getCarbohydrates().toString());
        textField_calories.setText(ing.getCalories().toString());
        textField_fibers.setText(ing.getFiber().toString());
        textField_fats.setText(ing.getFats().toString());
        textField_proteins.setText(ing.getProteins().toString());


    }

    public void load_view(){
        try {
            model = FXCollections.observableArrayList(service.get_all_ingredients_for_account(screenController.getCurrent_account_id()));
        } catch (ValidationException e) {


        }
        column_carbs.setCellValueFactory(new PropertyValueFactory<Ingredient,String>("carbohydrates"));
        column_fats.setCellValueFactory(new PropertyValueFactory<Ingredient,String>("fats"));
        column_fibers.setCellValueFactory(new PropertyValueFactory<Ingredient,String>("fiber"));
        column_proteins.setCellValueFactory(new PropertyValueFactory<Ingredient,String>("proteins"));
        column_calories.setCellValueFactory(new PropertyValueFactory<Ingredient,String>("calories"));
        column_name.setCellValueFactory(new PropertyValueFactory<Ingredient,String>("name"));
        tableView.setItems(model);

        //todo add multiple selection
//        tableViewAllIngredients.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                fill_fields();
            }
        });
    }

    public void initialize(){
        label_success.setVisible(false);
    }

    public void stop_app(){
        Platform.exit();
    }


    public void updateTableView(Integer newIndex){



        Integer start = newIndex*ITEMS_PER_PAGE;
        Integer end = (1+newIndex)*ITEMS_PER_PAGE;
        ObservableList<Ingredient> page = FXCollections.observableArrayList();
        if((start < 0 && end <0) || (start>model.size() && end >model.size()) ) {
            tableView.setItems(page);
            return;
        }
        if(end>model.size())
            end = model.size();
        if(start<0)
            start = 0;
        page = FXCollections.observableArrayList(model.subList(start,end));
        tableView.setItems(page);
    }
    public void createPagination() {

        pagination.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) ->
                updateTableView(newIndex.intValue()));
        updateTableView(0);

    }


    public void add(){
        Float carbohydrates;
        Float proteins;
        Float fats;
        Float fibers;
        Integer calories;
        String name = textField_name.getText();

        if(name.isEmpty()){
            MessageBox.showErrorMessage("Must enter a name for the ingredient.","Error storing ingredient.");
            return;
        }
        try{
            calories = Integer.parseInt(textField_calories.getText());
        }catch (Exception ex){
            MessageBox.showErrorMessage("Calories must be a whole number.","Error storing ingredient.");
            return;
        }

        try{
            carbohydrates = Float.parseFloat(textField_carbohydrates.getText());
            proteins = Float.parseFloat(textField_proteins.getText());
            fats =Float.parseFloat(textField_fats.getText());
            fibers =Float.parseFloat(textField_fibers.getText());
        }catch (Exception ex){
            MessageBox.showErrorMessage("Values for carbohydrates,proteins,fats or fibers must be decimal numbers.","Error storing ingredient.");
            return;
        }
        try {
            service.store_ingredient(screenController.getCurrent_account_id(),name,calories,carbohydrates,proteins,fats,fibers);

        } catch (ValidationException e) {
            MessageBox.display_error(e);
            return;
        }
        service.notify_controllers();
        label_success.setText("Successfully added the ingredient.");
        Effects.fadeInAndOutObject(label_success,2000);
    }

    public void reset_fields(){
        tableView.getSelectionModel().clearSelection();
        textField_fats.clear();
        textField_proteins.clear();
        textField_calories.clear();
        textField_carbohydrates.clear();
        textField_fibers.clear();
        textField_name.clear();
        update();
    }

    public void filter(){
        Float carbohydrates=null;
        Float proteins=null;
        Float fats=null;
        Float fibers=null;
        Integer calories=null;
        String name =null;
        if(!textField_name.getText().isEmpty()){
            name = textField_name.getText();
        }
        try {
            if (!textField_calories.getText().isEmpty()) {
                calories = Integer.parseInt(textField_calories.getText());
            }
        }catch (Exception ex) {
            MessageBox.showErrorMessage("Calories are whole numbers.","Error");
            return;
        }

        try {
            if (!textField_carbohydrates.getText().isEmpty()) {
                carbohydrates = Float.parseFloat(textField_carbohydrates.getText());
            }
            if (!textField_proteins.getText().isEmpty()) {
                proteins = Float.parseFloat(textField_proteins.getText());
            }
            if (!textField_fats.getText().isEmpty()) {
                fats = Float.parseFloat(textField_fats.getText());
            }
            if (!textField_fibers.getText().isEmpty()) {
                fibers = Float.parseFloat(textField_fibers.getText());
            }
        }catch (Exception ex) {
            MessageBox.showErrorMessage("Must enter numbers for nutritional values.","Error");
            return;
        }

        try {
            model = FXCollections.observableArrayList(service.get_filtered_ingredients_for_account(screenController.getCurrent_account_id(),
                    name,calories,carbohydrates,proteins,fats,fibers
                    ));
            reset_pagination();
        } catch (ValidationException e) {
            MessageBox.display_error(e);
            return;
        }
        label_success.setText("Ingredients filtered.");
        Effects.fadeInAndOutObject(label_success,2000);

    }
    public void reset_pagination(){
        updateTableView(0);
        pagination.setCurrentPageIndex(0);
    }
    public void delete(){
        Ingredient ing = (Ingredient)tableView.getSelectionModel().getSelectedItem();
        try {
            service.delete_ingredient(screenController.getCurrent_account_id(),ing.getId());
        } catch (ValidationException e) {
            MessageBox.display_error(e);
            return;
        }
        service.notify_controllers();
        label_success.setText("Successfully deleted the ingredient.");
        Effects.fadeInAndOutObject(label_success,2000);
    }

    public void modify(){
        Float carbohydrates;
        Float proteins;
        Float fats;
        Float fibers;
        Integer calories;
        String name = textField_name.getText();
        Ingredient ing = (Ingredient)tableView.getSelectionModel().getSelectedItem();
        if(name.isEmpty()){
            MessageBox.showErrorMessage("Must enter a name for the ingredient.","Error modifying ingredient.");
            return;
        }
        try{
            calories = Integer.parseInt(textField_calories.getText());
        }catch (Exception ex){
            MessageBox.showErrorMessage("Calories must be a whole number.","Error modifying ingredient.");
            return;
        }

        try{
            carbohydrates = Float.parseFloat(textField_carbohydrates.getText());
            proteins = Float.parseFloat(textField_proteins.getText());
            fats =Float.parseFloat(textField_fats.getText());
            fibers =Float.parseFloat(textField_fibers.getText());
        }catch (Exception ex){
            MessageBox.showErrorMessage("Values for carbohydrates,proteins,fats or fibers must be decimal numbers.","Error modifying ingredient.");
            return;
        }
        try {
            service.update_ingredient(screenController.getCurrent_account_id(),ing.getId(),name,calories,carbohydrates,proteins,fats,fibers);
            service.notify_controllers();
        } catch (ValidationException e) {
            MessageBox.display_error(e);
        }
        label_success.setText("Successfully modified the ingredient.");
        Effects.fadeInAndOutObject(label_success,2000);

    }

    public void back(){
        screenController.setScreen(Main.screen_mainID);
    }


}
