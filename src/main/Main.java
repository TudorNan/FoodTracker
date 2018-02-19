package main;


import controller.ControllerLogin;
import controller.ScreenController;
import domain.Account;
import domain.Ingredient;
import domain.IngredientEntry;
import domain.Recipe;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import repository.AccountRepository;
import repository.IngredientRepository;
import repository.RecipeRepository;
import service.Service;
import utils.MessageBox;
import validator.*;

import java.util.ArrayList;
import java.util.List;

public class Main extends Application {
    private String connectionURL ="jdbc:sqlserver://localhost:1433;databaseName=master;user=sa;password=trackyourfood2018";
    private IngredientRepository ingredientRepository;
    private Validator<Ingredient> validator = new IngredientValidator();
    private AccountRepository accountRepository;
    private Validator<Account> validator_account = new AccountValidator();
    private RecipeRepository recipeRepository;
    private Validator<Recipe> validator_recipe = new RecipeValidator();

    private Service service;

    public static String screen_loginID = "screen_login";
    public static String screen_loginFILE = "../view/login.fxml";
    public static String screen_registerID = "screen_register";
    public static String screen_registerFILE = "../view/register.fxml";
    public static String screen_mainID = "screen_main";
    public static String screen_mainFILE = "../view/menu.fxml";
    public static String screen_drawerID = "screen_drawer";
    public static String screen_drawerFILE ="../view/drawer.fxml";
    public static String screen_accountViewID = "screen_account";
    public static String screen_accountViewFILE ="../view/account.fxml";
    public static String screen_ingredientsID ="screen_ingredients";
    public static String screen_ingredientsFILE ="../view/ingredients.fxml";
    public static String screen_ingredients_all_ID ="screen_all_ingredients";
    public static String screen_ingredients_all_FILE ="../view/allingredients.fxml";
    public static String screen_recipesID ="screen_recipes";
    public static String screen_recipesFILE ="../view/recipes.fxml";
    public static String screen_recipesEditID ="screen_recipe_edit";
    public static String screen_recipesEditFILE ="../view/editRecipe.fxml";

    public static String screen_recipesAllID ="screen_all_recipes";
    public static String screen_recipesAllFILE ="../view/allrecipes.fxml";

    public void tests(){
        //System.out.println(ingredientRepository.getAll());
        //ingredientRepository.delete(1);
        //System.out.println(ingredientRepository.filter_with_precision(new Ingredient(null,null,54,null,null,null,null),1.1f));
        //System.out.println(ingredientRepository.filter_precise(new Ingredient(null,null,null,13.1f,0.5f,null,null)));

        //accountRepository.delete(4);
        //accountRepository.store(new Account("Marius22","Marius22@gmail.com",null),"parolastrong2k");
        //accountRepository.update_email(1,"marcelL@gmail.com");
        //System.out.println(accountRepository.login("Marcel2001","pass"));
        //System.out.println(recipeRepository.getAll());
    }

    public void initialize_database_connection() throws ValidationException {
            ingredientRepository = new IngredientRepository(validator,connectionURL);
            accountRepository = new AccountRepository(validator_account,connectionURL);
            recipeRepository = new RecipeRepository(validator_recipe,connectionURL);
            service = new Service(ingredientRepository,accountRepository,recipeRepository);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        initialize_database_connection();

        FXMLLoader loader = new FXMLLoader();
        ScreenController mainController = new ScreenController(service,primaryStage);

        mainController.loadScreen(screen_loginID,screen_loginFILE,600,1024);
        mainController.loadScreen(screen_registerID,screen_registerFILE,600,1024);
        mainController.loadScreen(screen_drawerID,screen_drawerFILE,300,150); // must be initialized before menu!
        mainController.loadScreen(screen_mainID,screen_mainFILE,600,1024);
        mainController.loadScreen(screen_accountViewID,screen_accountViewFILE,600,1024);
        mainController.loadScreen(screen_ingredientsID,screen_ingredientsFILE,800,1440);
        mainController.loadScreen(screen_ingredients_all_ID,screen_ingredients_all_FILE,800,1440);
        mainController.loadScreen(screen_recipesID,screen_recipesFILE,800,1440);
        mainController.loadScreen(screen_recipesEditID,screen_recipesEditFILE,800,1440);
        mainController.loadScreen(screen_recipesAllID,screen_recipesAllFILE,800,1440);

        mainController.setScreen(screen_loginID);

        Group root = new Group();
        root.getChildren().addAll(mainController);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Food Tracker");
        primaryStage.initStyle(StageStyle.UNDECORATED);
        //TODO remove decorated
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
