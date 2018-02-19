package controller;

import domain.Ingredient;
import domain.IngredientEntry;
import domain.Recipe;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import main.Main;
import service.Service;
import utils.MessageBox;
import utils.Observer;
import validator.ValidationException;

import java.util.ArrayList;
import java.util.List;

public class ControllerRecipeEdit implements Observer,ControlledScreen{
    private ScreenController screenController;
    private Service service;
    private ObservableList<Ingredient> model_all_ingredients = FXCollections.observableArrayList();
    private ObservableList<IngredientEntry> model_current_ingredients = FXCollections.observableArrayList();

    private List<IngredientEntry> current_ingredients;
    private MODE actionMode;
    private Integer current_recipe_id;

    private enum MODE{
        UPDATE,CREATION
    }
    public static Integer ITEMS_PER_PAGE = 15;

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
    TableView tableView_all;
    @FXML
    TableView tableView_current;

    @FXML
    TableColumn column_ingname;
    @FXML
    TableColumn column_amount;

    @FXML
    TextField textField_name;
    @FXML
    TextField textField_search;
    @FXML
    TextField textField_quantity;
    @FXML
    TextField textField_servings;


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
        textField_servings.clear();
        textField_name.clear();
        textField_quantity.clear();
        textField_search.clear();

    }

    /*
    * Prepares the view for updating a recipe
    *
    * */
    public void prepare_view_update(Recipe recipe){
        actionMode = MODE.UPDATE;
        current_recipe_id = recipe.getId();
        try {
            current_ingredients = service.get_all_ingredients_for_recipe(recipe.getId());
        } catch (ValidationException e) {
            MessageBox.display_error(e);
        }
        textField_name.setText(recipe.getName());
        textField_servings.setText(String.valueOf(recipe.getServings()));
        model_current_ingredients = FXCollections.observableArrayList(current_ingredients);
        load_view();
    }

    /*
    * Prepares the view for creating a new recipe
    * */
    public void prepare_view_create(){
        actionMode = MODE.CREATION;
        reset_view();
        current_ingredients = new ArrayList<>();

        current_recipe_id = null;
        model_current_ingredients = FXCollections.observableArrayList(new ArrayList<>());

        load_view();

    }

    public void stop_app(){
        Platform.exit();
    }

    public void back(){
        screenController.setScreen(Main.screen_recipesID);
    }

    @Override
    public void setScreenParent(ScreenController screenController) {
        this.screenController = screenController;
    }

    public void reset_pagination(){
        updateTableView(0);
        pagination.setCurrentPageIndex(0);
    }

    @Override
    public void update() {
        try {
            model_all_ingredients = FXCollections.observableArrayList(service.get_all_ingredients());
            reset_pagination();
        } catch (ValidationException e) {
            MessageBox.display_error(e);
        }
    }

    public void load_view(){
        try {
            model_all_ingredients = FXCollections.observableArrayList(service.get_all_ingredients());

        } catch (ValidationException e) {

        }
        column_carbs.setCellValueFactory(new PropertyValueFactory<Recipe,String>("carbohydrates"));
        column_fats.setCellValueFactory(new PropertyValueFactory<Recipe,String>("fats"));
        column_fibers.setCellValueFactory(new PropertyValueFactory<Recipe,String>("fiber"));
        column_proteins.setCellValueFactory(new PropertyValueFactory<Recipe,String>("proteins"));
        column_calories.setCellValueFactory(new PropertyValueFactory<Recipe,String>("calories"));
        column_name.setCellValueFactory(new PropertyValueFactory<Recipe,String>("name"));


        column_ingname.setCellValueFactory(new PropertyValueFactory<IngredientEntry,String>("name"));
        column_amount.setCellValueFactory((Callback<TableColumn.CellDataFeatures<IngredientEntry, String>, ObservableValue>) param -> {
            IngredientEntry a = param.getValue();
            return Bindings.createStringBinding(()->a.getQuantity().toString()+"g");
        });
        tableView_all.setItems(model_all_ingredients);
        tableView_current.setItems(model_current_ingredients);

        textField_search.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                if(newValue!="") {
                    model_all_ingredients = FXCollections.observableArrayList(service.get_filtered_ingredients_by_name(textField_search.getText()));
                }else{
                    model_all_ingredients = FXCollections.observableArrayList(service.get_all_ingredients());
                }
                pagination.setCurrentPageIndex(0);
                updateTableView(0);

            } catch (ValidationException e) {
                MessageBox.display_error(e);
        }
        });

    }

    public void updateTableView(Integer newIndex){

        Integer start = newIndex*ITEMS_PER_PAGE;
        Integer end = (1+newIndex)*ITEMS_PER_PAGE;
        ObservableList<Ingredient> page = FXCollections.observableArrayList();
        if((start < 0 && end <0) || (start> model_all_ingredients.size() && end > model_all_ingredients.size()) ) {
            tableView_all.setItems(page);
            return;
        }
        if(end> model_all_ingredients.size())
            end = model_all_ingredients.size();
        if(start<0)
            start = 0;
        page = FXCollections.observableArrayList(model_all_ingredients.subList(start,end));
        tableView_all.setItems(page);
    }


    public void add_ingredient(){
        Integer quantity=0;
        if(tableView_all.getSelectionModel().isEmpty())
            return;
        try {
            quantity = Integer.parseInt(textField_quantity.getText());
        }catch (Exception ex) {
            MessageBox.showErrorMessage("Quantity(grams) must be a whole number.", "Error.");
            return;
        }
        Ingredient current_ingredient = (Ingredient) tableView_all.getSelectionModel().getSelectedItem();
        current_ingredients.add(new IngredientEntry(current_ingredient,quantity));
        model_current_ingredients = FXCollections.observableArrayList(current_ingredients);
        tableView_current.setItems(model_current_ingredients);


    }

    public void remove_ingredient(){
        if(tableView_current.getSelectionModel().isEmpty())
            return;
        Ingredient current_ingredient = (Ingredient) tableView_current.getSelectionModel().getSelectedItem();
        current_ingredients.remove(current_ingredient);
        model_current_ingredients = FXCollections.observableArrayList(current_ingredients);
        tableView_current.setItems(model_current_ingredients);

    }

    public void store(){
        Integer servings;
        try {
            servings = Integer.parseInt(textField_servings.getText());
            if(servings <= 0) {
                MessageBox.showErrorMessage("Servings must be a whole, positive number.", "Error.");
                return;
            }

        }catch (Exception ex) {
            MessageBox.showErrorMessage("Servings must be a whole, positive number.", "Error.");
            return;
        }

        if(textField_name.getText().equals(""))
        {
            MessageBox.showErrorMessage("Must enter a valid name for the recipe!","Incorrect name.");
            return;
        }
        try {
            service.store_recipe(screenController.getCurrent_account_id(),new Recipe(textField_name.getText(),servings),current_ingredients);
        } catch (ValidationException e) {
            MessageBox.display_error(e);
        }
        screenController.setScreen(Main.screen_recipesID);
        MessageBox.showMessage(Alert.AlertType.CONFIRMATION,"Confirmation","Recipe has been saved successfully.");
        service.notify_controllers();
    }

    public void modify(){
        Integer servings;
        try {
            servings = Integer.parseInt(textField_servings.getText());
            if(servings <= 0) {
                MessageBox.showErrorMessage("Servings must be a whole, positive number.", "Error.");
                return;
            }

        }catch (Exception ex) {
            MessageBox.showErrorMessage("Servings must be a whole, positive number.", "Error.");
            return;
        }
        if(textField_name.getText().equals(""))
        {
            MessageBox.showErrorMessage("Must enter a valid name for the recipe!","Incorrect name.");
            return;
        }try {
            service.update_recipe(screenController.getCurrent_account_id(),current_recipe_id,new Recipe(textField_name.getText(),servings),current_ingredients);
        } catch (ValidationException e) {
            MessageBox.display_error(e);
            return;
        }
        screenController.setScreen(Main.screen_recipesID);
        MessageBox.showMessage(Alert.AlertType.CONFIRMATION,"Confirmation","Recipe has been modified.");
        service.notify_controllers();

    }
    /*
    * Will either store or modify the current recipe depending on (MODE)actionMode
    * */
    public void save_button(){
        if(actionMode.equals(MODE.CREATION))
            store();
        else
            modify();
    }

    public void createPagination() {

        pagination.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) ->
                updateTableView(newIndex.intValue()));
        updateTableView(0);

    }

    public void initialize(){

    }




}
