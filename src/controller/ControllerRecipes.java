package controller;

import domain.Ingredient;
import domain.IngredientEntry;
import domain.Recipe;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
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

public class ControllerRecipes implements Observer,ControlledScreen{
    private ScreenController screenController;
    private Service service;
    private ObservableList<Recipe> model = FXCollections.observableArrayList();
    private ObservableList<IngredientEntry> model_ingredients = FXCollections.observableArrayList();

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
    TableColumn column_servings;

    @FXML
    TableView tableView;


    @FXML
    TableColumn column_ingname;
    @FXML
    TableColumn column_amount;

    @FXML
    TableView tableView_ingredients;

    @FXML
    TextField textField_search;

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
        update();
    }

    public void stop_app(){
        Platform.exit();
    }

    public void back(){
        screenController.setScreen(Main.screen_mainID);
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
            model = FXCollections.observableArrayList(service.get_all_recipes_for_account(screenController.getCurrent_account_id()));
            reset_pagination();
        } catch (ValidationException e) {
            MessageBox.display_error(e);
        }
    }

    public void load_view(){
        try {
            model = FXCollections.observableArrayList(service.get_all_recipes_for_account(screenController.getCurrent_account_id()));

        } catch (ValidationException e) {

        }
        column_carbs.setCellValueFactory(new PropertyValueFactory<Recipe,String>("carbohydrates"));
        column_fats.setCellValueFactory(new PropertyValueFactory<Recipe,String>("fats"));
        column_fibers.setCellValueFactory(new PropertyValueFactory<Recipe,String>("fiber"));
        column_proteins.setCellValueFactory(new PropertyValueFactory<Recipe,String>("proteins"));
        column_calories.setCellValueFactory(new PropertyValueFactory<Recipe,String>("calories"));
        column_name.setCellValueFactory(new PropertyValueFactory<Recipe,String>("name"));
        column_servings.setCellValueFactory(new PropertyValueFactory<Recipe,String>("servings"));

        column_ingname.setCellValueFactory(new PropertyValueFactory<IngredientEntry,String>("name"));
        column_amount.setCellValueFactory((Callback<TableColumn.CellDataFeatures<IngredientEntry, String>, ObservableValue>) param -> {
            IngredientEntry a = param.getValue();
            return Bindings.createStringBinding(()->a.getQuantity().toString()+"g");
        });
        tableView.setItems(model);
        tableView_ingredients.setItems(model_ingredients);
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                display_ingredients();
            }
        });
        textField_search.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                if(newValue!="") {
                    model = FXCollections.observableArrayList(service.get_all_recipes_for_account_by_name(screenController.getCurrent_account_id(), textField_search.getText()));
                }else{
                    model = FXCollections.observableArrayList(service.get_all_recipes_for_account(screenController.getCurrent_account_id()));
                }
                pagination.setCurrentPageIndex(0);
                updateTableView(0);

            } catch (ValidationException e) {
            e.printStackTrace();
        }
        });

    }

    public void export(){
        if(tableView.getSelectionModel().isEmpty()) {
            MessageBox.showMessage(Alert.AlertType.INFORMATION,"No recipe selected.","You have to select a recipe to be exported to .pdf");
            return;
        }else{
            Recipe selected = (Recipe) tableView.getSelectionModel().getSelectedItem();
            try {
                service.export_recipe_pdf(selected, "D:\\Facultate\\Projects\\Recipes\\");
                MessageBox.showMessage(Alert.AlertType.CONFIRMATION,"Exported.","D:\\Facultate\\Projects\\Recipes\\"+selected.getName()+".pdf");
            } catch (ValidationException e) {
                MessageBox.display_error(e);
            }
        }

    }
    public void updateTableView(Integer newIndex) {

        Integer start = newIndex * ITEMS_PER_PAGE;
        Integer end = (1 + newIndex) * ITEMS_PER_PAGE;
        ObservableList<Recipe> page = FXCollections.observableArrayList();
        if ((start < 0 && end < 0) || (start > model.size() && end > model.size())) {
            tableView.setItems(page);
            return;
        }
        if (end > model.size())
        { end = model.size();}
        if(start<0)
            start = 0;
        page = FXCollections.observableArrayList(model.subList(start,end));
        tableView.setItems(page);
    }

    public void delete_recipe(){
        Recipe current_recipe = (Recipe) tableView.getSelectionModel().getSelectedItem();
        try {
            service.delete_recipe(screenController.getCurrent_account_id(),current_recipe.getId());
            model = FXCollections.observableArrayList(service.get_all_recipes_for_account(screenController.getCurrent_account_id()));
        } catch (ValidationException e) {
            MessageBox.display_error(e);
        }
        reset_pagination();
    }
    public void create_recipe(){
        screenController.setScreen(Main.screen_recipesEditID);
        //prepare the stage for the view
        ControllerRecipeEdit controllerRecipeEdit = (ControllerRecipeEdit) screenController.getScreenController(Main.screen_recipesEditID);
        controllerRecipeEdit.prepare_view_create();
        controllerRecipeEdit.createPagination();
    }
    public void modify_recipe(){
        if(tableView.getSelectionModel().isEmpty())
            return;
        Recipe recipe = (Recipe) tableView.getSelectionModel().getSelectedItem();

        screenController.setScreen(Main.screen_recipesEditID);
        //prepare the stage for the view
        ControllerRecipeEdit controllerRecipeEdit = (ControllerRecipeEdit) screenController.getScreenController(Main.screen_recipesEditID);
        controllerRecipeEdit.prepare_view_update(recipe);
        controllerRecipeEdit.createPagination();
    }
    public void createPagination() {

        pagination.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) ->
                updateTableView(newIndex.intValue()));
        updateTableView(0);

    }

    public void display_ingredients(){
        Recipe current_recipe = (Recipe) tableView.getSelectionModel().getSelectedItem();
        Integer current_recipe_id = current_recipe.getId();
        try {
            model_ingredients = FXCollections.observableArrayList(service.get_all_ingredients_for_recipe(current_recipe_id));
            tableView_ingredients.setItems(model_ingredients);
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }

    public void initialize(){

    }




}
