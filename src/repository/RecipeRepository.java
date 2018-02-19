package repository;

import domain.Account;
import domain.Ingredient;
import domain.IngredientEntry;
import domain.Recipe;
import utils.Utils;
import validator.ValidationException;
import validator.Validator;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RecipeRepository {
    private Validator<Recipe> validator;
    private String connectionURL;
    private Connection connection_ssms;

    private String QUERY_INSERT = new StringBuilder().append("USE RecipesApp;").append("INSERT INTO Ingredient (name, calories, carbohydrates, proteins,fats,fibers) ").append("VALUES (?, ?, ?, ?, ?, ?);").toString();
    private String QUERY_GET_ALL = new StringBuilder().append("USE RecipesApp;")
            .append("SELECT re.id,re.name,re.servings,\n" +
                    "CONVERT(DECIMAL(10,1),SUM(ing.calories*quantity/100)/re.servings) as calories,\n" +
                    "CONVERT(DECIMAL(10,1),SUM(ing.carbohydrates*quantity/100)/re.servings) as carbohydrates,\n" +
                    "CONVERT(DECIMAL(10,1),SUM(ing.proteins*quantity/100)/re.servings) as proteins,\n" +
                    "CONVERT(DECIMAL(10,1),SUM(ing.fats*quantity/100)/re.servings) as fats,\n" +
                    "CONVERT(DECIMAL(10,1),SUM(ing.fibers*quantity/100)/re.servings) as fibers\n" +
                    "FROM Recipe re INNER JOIN Recipe_ingredients ri ON ri.recipe_id = re.id \n" +
                    "INNER JOIN Ingredient ing ON ing.id = ri.ingredient_id \n" +
                    "GROUP BY re.id,re.name,re.servings").toString();

    private String QUERY_DELETE_ID =  new StringBuilder().append("USE RecipesApp;").append("DELETE FROM Recipe ").append(
            "WHERE Recipe.id = ?").toString();
    private String QUERY_STORE_RECIPE_INGREDIENTS = new StringBuilder().append("USE RecipesApp;").append("INSERT INTO Recipe_ingredients(recipe_id,ingredient_id,quantity) VALUES\n").toString();
    private String QUERY_STORE_RECIPE = new StringBuilder().append("USE RecipesApp;").append("INSERT INTO Recipe(name,servings) VALUES(?,?)\n").toString();
    private String QUERY_UPDATE = new StringBuilder().append("USE RecipesApp;").append("UPDATE Ingredient\n").toString();
    private String QUERY_INSERT_OWNER =  new StringBuilder().append("USE RecipesApp;").append("INSERT INTO Account_recipes(account_id,recipe_id) VALUES(?,?)\n").toString();
    private String QUERY_GET_ALL_ID =new StringBuilder().append("USE RecipesApp;")
            .append("SELECT re.id,re.name,re.servings,\n" +
                    "CONVERT(DECIMAL(10,1),SUM(ing.calories*quantity/100)/re.servings) as calories,\n" +
                    "CONVERT(DECIMAL(10,1),SUM(ing.carbohydrates*quantity/100)/re.servings) as carbohydrates,\n" +
                    "CONVERT(DECIMAL(10,1),SUM(ing.proteins*quantity/100)/re.servings) as proteins,\n" +
                    "CONVERT(DECIMAL(10,1),SUM(ing.fats*quantity/100)/re.servings) as fats,\n" +
                    "CONVERT(DECIMAL(10,1),SUM(ing.fibers*quantity/100)/re.servings) as fibers\n" +
                    "\n" +
                    "FROM Recipe re INNER JOIN Recipe_ingredients ri ON ri.recipe_id = re.id \n" +
                    "INNER JOIN Ingredient ing ON ing.id = ri.ingredient_id \n" +
                    "INNER JOIN Account_recipes ar ON ar.recipe_id = re.id \n" +
                    "INNER JOIN Account ac ON ac.id = ar.account_id\n" +
                    "WHERE ac.id=?\n" +
                    "GROUP BY re.id,re.name,re.servings\n")
            .toString();
    private String QUERY_GET_INGREDIENTS = new StringBuilder().append("USE RecipesApp;").append("SELECT ing.id,ing.name,ri.quantity,ing.calories,ing.carbohydrates,ing.proteins,ing.fats,ing.fibers\n" +
            "FROM Recipe re INNER JOIN Recipe_ingredients ri ON ri.recipe_id = re.id INNER JOIN Ingredient ing on ing.id = ri.ingredient_id\n" +
            "WHERE re.id=?").toString();
    /*
    * Repository constructor
    * Throws ValidationException-401 if a connection to the database couldn't be established
    * */
    public RecipeRepository(Validator<Recipe> validator, String connectionURL) throws ValidationException {
        this.validator = validator;
        this.connectionURL = connectionURL;
        try{
            connection_ssms = DriverManager.getConnection(connectionURL);
        } catch (SQLException e1) {
            e1.printStackTrace();
            throw new ValidationException("Database Connection Exception",401);
        }
    }
    /*
    * Returns a list of all Recipes in the database with nutritional values
    * Throws ValidationException-ErrorCode 400 if there is any database exeption
    * */
    public List<Recipe> getAll() throws ValidationException {
        List<Recipe> final_list = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try{
            preparedStatement = connection_ssms.prepareStatement(QUERY_GET_ALL);
            resultSet = preparedStatement.executeQuery();
            final_list = build_recipe_list(resultSet);
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ValidationException("Database Exception",400);
        }
        return final_list;
    }
    public List<IngredientEntry> get_ingredients_for_recipe(Integer recipe_id) throws ValidationException {
        Integer id;
        String name;
        Integer calories;
        Integer quantity;
        Float carbs;
        Float proteins;
        Float fats;
        Float fibers;

        List<IngredientEntry> final_list = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try{
            preparedStatement = connection_ssms.prepareStatement(QUERY_GET_INGREDIENTS);
            preparedStatement.setInt(1,recipe_id);
            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                id = resultSet.getInt("id");
                name = resultSet.getString("name");
                quantity = resultSet.getInt("quantity");
                calories = resultSet.getInt("calories");
                carbs = resultSet.getFloat("carbohydrates");
                proteins = resultSet.getFloat("proteins");
                fats = resultSet.getFloat("fats");
                fibers = resultSet.getFloat("fibers");
                IngredientEntry ingredientEntry = new IngredientEntry(new Ingredient(id,name,calories,carbs,proteins,fats,fibers),quantity);
                final_list.add(ingredientEntry);
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ValidationException("Database Exception",400);
        }
        return final_list;
    }
    /*
    * RETURNS a Recipe list from a resultSet of Recipes INNER JOINED with Recipe_ingredients (id,name,calories,carbs,proteins,fats,fibers)
    * Throws ValidationException-400 if database exception
    * */
    private List<Recipe> build_recipe_list(ResultSet resultSet) throws ValidationException {
        Integer id;
        String name;
        Integer calories;
        Integer servings;
        Float carbs;
        Float proteins;
        Float fats;
        Float fibers;

        List<Recipe> final_list = new ArrayList<>();
        try{
            while(resultSet.next()){
                id = resultSet.getInt("id");
                name = resultSet.getString("name");
                servings = resultSet.getInt("servings");
                calories = resultSet.getInt("calories");
                carbs = resultSet.getFloat("carbohydrates");
                proteins = resultSet.getFloat("proteins");
                fats = resultSet.getFloat("fats");
                fibers = resultSet.getFloat("fibers");
                Recipe recipe = new Recipe(id,name,servings,calories,carbs,proteins,fats,fibers);
                final_list.add(recipe);
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ValidationException("Database Exception",400);
        }
        return final_list;
    }
    /*
    * Returns a list of all Recipes submitted by account_id in the database,with nutritional values
    * Throws ValidationException-ErrorCode 400 if there is any database exeption
    * */
    public List<Recipe> getAll_account(Integer account_id) throws ValidationException {
        List<Recipe> final_list = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try{
            preparedStatement = connection_ssms.prepareStatement(QUERY_GET_ALL_ID);
            preparedStatement.setInt(1,account_id);
            resultSet = preparedStatement.executeQuery();
            final_list = build_recipe_list(resultSet);
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ValidationException("Database Exception",400);
        }
        return final_list;
    }
    public void delete_recipe(Integer recipe_id) throws ValidationException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<Object> args = new ArrayList<>();
        try {
            preparedStatement = connection_ssms.prepareStatement(QUERY_DELETE_ID);
            preparedStatement.setInt(1, recipe_id);
            int error = preparedStatement.executeUpdate();
            if(error == 0)
                throw new ValidationException("Recipe id not found.",250);
        } catch (SQLException e) {
            throw new ValidationException("Database error while deleting recipe.", e.getErrorCode());
        }
    }

    public void store_recipe(Integer account_id,Recipe recipe,List<IngredientEntry> ingredients) throws ValidationException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<Object> args = new ArrayList<>();
        try {
            preparedStatement = connection_ssms.prepareStatement(QUERY_STORE_RECIPE,Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, recipe.getName());
            preparedStatement.setInt(2,recipe.getServings());
            int error  = preparedStatement.executeUpdate();
            if(error == 0){
                throw new ValidationException("Couldn't store recipe",700);
            }else{
                Integer generated_key=0;
                resultSet = preparedStatement.getGeneratedKeys();
                while(resultSet.next())
                {
                    generated_key= resultSet.getInt(1);
                    StringBuilder sql = new StringBuilder(QUERY_STORE_RECIPE_INGREDIENTS);
                    for (IngredientEntry ingredient : ingredients) {
                        sql.append("(?,?,?),");
                        args.add(generated_key);
                        args.add(ingredient.getId());
                        args.add(ingredient.getQuantity());
                    }
                    if(sql.toString().endsWith(","))
                        sql = new StringBuilder().append(sql.toString().replaceFirst(".$",""));

                    preparedStatement = connection_ssms.prepareStatement(sql.toString());
                    Utils.setValues(preparedStatement,args);
                    error = preparedStatement.executeUpdate();
                    if(error == 0){
                        throw new ValidationException("Couldn't store recipe",700);
                    }else{
                        //store owner
                        preparedStatement = connection_ssms.prepareStatement(QUERY_INSERT_OWNER);
                        preparedStatement.setInt(1,account_id);
                        preparedStatement.setInt(2,generated_key);
                        error = preparedStatement.executeUpdate();
                        if(error == 0) {
                            throw new ValidationException("Couldn't store recipe", 700);
                        }
                    }
                }
            }

        }catch (SQLException e) {
            throw new ValidationException(e.getMessage(),e.getErrorCode());
        }
    }
}
