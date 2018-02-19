package controller;

import domain.Ingredient;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import main.Main;
import service.Service;
import utils.Effects;
import utils.MessageBox;
import utils.Observer;
import validator.ValidationException;

public class ControllerAllIngredients implements Observer,ControlledScreen{
    private ScreenController screenController;
    private Service service;
    private ObservableList<Ingredient> model = FXCollections.observableArrayList();
    public static Integer ITEMS_PER_PAGE = 15;


    @FXML
    TextField textBox_name;
    @FXML
    TextField textBox_calories;
    @FXML
    TextField textBox_fats;
    @FXML
    TextField textBox_fibers;
    @FXML
    TextField textBox_proteins;
    @FXML
    TextField textBox_carbohydrates;

    @FXML
    TableColumn tableColumn_name;
    @FXML
    TableColumn tableColumn_carbs;
    @FXML
    TableColumn tableColumn_proteins;
    @FXML
    TableColumn tableColumn_fats;
    @FXML
    TableColumn tableColumn_calories;
    @FXML
    TableColumn tableColumn_fibers;
    @FXML
    TableView tableViewAllIngredients;

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

    }

    @Override
    public void setScreenParent(ScreenController screenController) {
        this.screenController = screenController;
    }

    @Override
    public void update() {
        try {
            model = FXCollections.observableArrayList(service.get_all_ingredients());
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
        Ingredient ing = (Ingredient) tableViewAllIngredients.getSelectionModel().getSelectedItem();
        textBox_name.setText(ing.getName());
        textBox_carbohydrates.setText(ing.getCarbohydrates().toString());
        textBox_calories.setText(ing.getCalories().toString());
        textBox_fibers.setText(ing.getFiber().toString());
        textBox_fats.setText(ing.getFats().toString());
        textBox_proteins.setText(ing.getProteins().toString());
    }

    public void load_view(){
        try {
            model = FXCollections.observableArrayList(service.get_all_ingredients());
        } catch (ValidationException e) {


        }
        tableColumn_carbs.setCellValueFactory(new PropertyValueFactory<Ingredient,String>("carbohydrates"));
        tableColumn_fats.setCellValueFactory(new PropertyValueFactory<Ingredient,String>("fats"));
        tableColumn_fibers.setCellValueFactory(new PropertyValueFactory<Ingredient,String>("fiber"));
        tableColumn_proteins.setCellValueFactory(new PropertyValueFactory<Ingredient,String>("proteins"));
        tableColumn_calories.setCellValueFactory(new PropertyValueFactory<Ingredient,String>("calories"));
        tableColumn_name.setCellValueFactory(new PropertyValueFactory<Ingredient,String>("name"));
        tableViewAllIngredients.setItems(model);

        //todo add multiple selection
//        tableViewAllIngredients.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableViewAllIngredients.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                fill_fields();
            }
        });

        textBox_name.textProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue!="")
                filter();
            pagination.setCurrentPageIndex(0);
            updateTableView(0);
        });
        textBox_calories.textProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue!="")
                filter();
            pagination.setCurrentPageIndex(0);
            updateTableView(0);
        });
        textBox_carbohydrates.textProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue!="")
                filter();
            pagination.setCurrentPageIndex(0);
            updateTableView(0);
        });
        textBox_fats.textProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue!="")
                filter();
            pagination.setCurrentPageIndex(0);
            updateTableView(0);
        });
        textBox_fibers.textProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue!="")
                filter();
            pagination.setCurrentPageIndex(0);
            updateTableView(0);
        });
        textBox_proteins.textProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue!="")
                filter();
            pagination.setCurrentPageIndex(0);
            updateTableView(0);
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
            tableViewAllIngredients.setItems(page);
            return;
        }
        if(end>model.size())
            end = model.size();
        if(start<0)
            start = 0;
        page = FXCollections.observableArrayList(model.subList(start,end));
        tableViewAllIngredients.setItems(page);


    }
    public void reset_pagination(){
        updateTableView(0);
        pagination.setCurrentPageIndex(0);
    }

    public void createPagination() {

        pagination.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) ->
                updateTableView(newIndex.intValue()));
        updateTableView(0);

    }

    public void reset_fields(){
        tableViewAllIngredients.getSelectionModel().clearSelection();
        textBox_fats.clear();
        textBox_proteins.clear();
        textBox_calories.clear();
        textBox_carbohydrates.clear();
        textBox_fibers.clear();
        textBox_name.clear();
        update();
    }

    public void filter(){
        Float carbohydrates=null;
        Float proteins=null;
        Float fats=null;
        Float fibers=null;
        Integer calories=null;
        String name =null;
        if(!textBox_name.getText().isEmpty()){
            name = textBox_name.getText();
        }
        try {
            if (!textBox_calories.getText().isEmpty()) {
                calories = Integer.parseInt(textBox_calories.getText());
            }
        }catch (Exception ex) {
            MessageBox.showErrorMessage("Calories are whole numbers.","Error");
            return;
        }

        try {
            if (!textBox_carbohydrates.getText().isEmpty()) {
                carbohydrates = Float.parseFloat(textBox_carbohydrates.getText());
            }
            if (!textBox_proteins.getText().isEmpty()) {
                proteins = Float.parseFloat(textBox_proteins.getText());
            }
            if (!textBox_fats.getText().isEmpty()) {
                fats = Float.parseFloat(textBox_fats.getText());
            }
            if (!textBox_fibers.getText().isEmpty()) {
                fibers = Float.parseFloat(textBox_fibers.getText());
            }
        }catch (Exception ex) {
            MessageBox.showErrorMessage("Must enter numbers for nutritional values.","Error");
            return;
        }

        try {
            model = FXCollections.observableArrayList(service.get_filtered_ingredients(
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


    public void back(){
        screenController.setScreen(Main.screen_mainID);
    }


}
