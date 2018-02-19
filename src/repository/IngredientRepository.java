package repository;

import domain.Ingredient;
import validator.ValidationException;
import validator.Validator;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static utils.Utils.setValues;

public class IngredientRepository {
    private Validator<Ingredient> validator;
    private String connectionURL;
    private Connection connection_ssms;
    private String QUERY_GET_ALL_ID = new StringBuilder().append("USE RecipesApp;").append("SELECT ing.id,ing.name,ing.calories,ing.carbohydrates,ing.proteins,ing.fats,ing.fibers\n" +
            "FROM Account account INNER JOIN Account_ingredients ai ON ai.account_id = account.id INNER JOIN Ingredient ing ON ing.id = ingredient_id\n" +
            "WHERE account.id = ?;").toString();
    private String QUERY_INSERT_OWNERSHIP = new StringBuilder().append("USE RecipesApp;").append("INSERT INTO Account_ingredients (account_id,ingredient_id) ").append("VALUES (?, ?);").toString();

    private String QUERY_INSERT = new StringBuilder().append("USE RecipesApp;").append("INSERT INTO Ingredient (name, calories, carbohydrates, proteins,fats,fibers) ").append("VALUES (?, ?, ?, ?, ?, ?);").toString();
    private String QUERY_GET_ALL = new StringBuilder().append("USE RecipesApp;").append("SELECT * FROM Ingredient").toString();
    private String QUERY_DELETE_ID =  new StringBuilder().append("USE RecipesApp;").append("DELETE FROM Ingredient ").append(
            "WHERE Ingredient.id = ?").toString();
    private String QUERY_UPDATE = new StringBuilder().append("USE RecipesApp;").append("UPDATE Ingredient\n").toString();


    /*
    * Repository constructor
    * Throws ValidationException-401 if a connection to the database couldn't be established
    * */
    public IngredientRepository(Validator<Ingredient> validator, String connectionURL) throws ValidationException {
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
    * Returns a list of all Ingredients in the database for an account_id
    * Throws ValidationException-ErrorCode 400 if there is any database exeption
    * */
    public List<Ingredient> getAllId(Integer account_id) throws ValidationException {
        List<Ingredient> final_list = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try{
            preparedStatement = connection_ssms.prepareStatement(QUERY_GET_ALL_ID);
            preparedStatement.setInt(1,account_id);
            resultSet = preparedStatement.executeQuery();
            final_list = build_ingredient_list(resultSet);
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ValidationException("Database Exception",400);
        }
        return final_list;
    }

    /*
       *  Stores an ingredient in the database, id is irrelevant, a new id will be provided
       *  Throws ValidationException-300 if fields are not correct
       *         ValidationException-400 if Database exception
       *  If fields in ingredient are null, will be stored as 0 ( ex: proteins = null => proteins = 0)
       * */
    public void store(Integer account_id,Ingredient ingredient) throws ValidationException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<Object> args = new ArrayList<>();
        validator.validate(ingredient);
        String generatedID[] = {"id"};
        try{
            preparedStatement = connection_ssms.prepareStatement(QUERY_INSERT,Statement.RETURN_GENERATED_KEYS);
            args.add(ingredient.getName());
            args.add(ingredient.getCalories());
            args.add(ingredient.getCarbohydrates());
            args.add(ingredient.getProteins());
            args.add(ingredient.getFats());
            args.add(ingredient.getFiber());
            setValues(preparedStatement,args);

            int error = preparedStatement.executeUpdate();


            if(error == 0)
                throw new ValidationException("Error saving Ingredient.",300);
            else{
                //get auto generated id for ingredient
                ResultSet rs = preparedStatement.getGeneratedKeys();
                Integer generated_id = 0;
                while(rs.next()){
                    generated_id = rs.getInt(1);
                }
                //prepare the insert ownership statement
                PreparedStatement ownershipStatement;
                ownershipStatement = connection_ssms.prepareStatement(QUERY_INSERT_OWNERSHIP);
                ownershipStatement.setInt(1,account_id);
                ownershipStatement.setInt(2, generated_id);
                error = ownershipStatement.executeUpdate();
                if(error == 0)
                    throw new ValidationException("Error saving ingredient ownership.",600);
            }
        } catch (SQLException e) {
            e.printStackTrace();

            throw new ValidationException("Database Exception",400);
        }
    }

    /*
       *  Deletes the ingredient from the database with the specified id
       *  Throws ValidationException-200 if id is null or 0
       *         ValidationException-250 if id wasn't found
       *         ValidationException-400 if database exception
       * */
    public void delete(Integer id_ingredient) throws ValidationException {
        if(id_ingredient.equals(0) || id_ingredient == null)
            throw new ValidationException("Id null/0",200);

        PreparedStatement preparedStatement = null;
        try{
            preparedStatement = connection_ssms.prepareStatement(QUERY_DELETE_ID);
            preparedStatement.setInt(1,id_ingredient);
            int error = preparedStatement.executeUpdate();
            if(error == 0)
                throw new ValidationException("Id not found!",250);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getErrorCode());
            throw new ValidationException("Database Exception",400);
        }
    }



    /*
    * Returns a list of all Ingredients in the database with corresponding ids(each ingredient has a set id)
    * Throws ValidationException-ErrorCode 400 if there is any database exeption
    * */
    public List<Ingredient> getAll() throws ValidationException {
        List<Ingredient> final_list = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try{
            preparedStatement = connection_ssms.prepareStatement(QUERY_GET_ALL);
            resultSet = preparedStatement.executeQuery();
            final_list = build_ingredient_list(resultSet);
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ValidationException("Database Exception",400);
        }
        return final_list;
    }

    /*
    * RETURNS an ingredient list from a resultSet of Ingredients (id,name,calories,carbs,proteins,fats,fibers)
    * Throws ValidationException-400 if database exception
    * */
    public List<Ingredient> build_ingredient_list(ResultSet resultSet) throws ValidationException {
        Integer id;
        String name;
        Integer calories;
        Float carbs;
        Float proteins;
        Float fats;
        Float fibers;

        List<Ingredient> final_list = new ArrayList<>();
        try{
            while(resultSet.next()){
                id = resultSet.getInt("id");
                name = resultSet.getString("name");
                calories = resultSet.getInt("calories");
                carbs = resultSet.getFloat("carbohydrates");
                proteins = resultSet.getFloat("proteins");
                fats = resultSet.getFloat("fats");
                fibers = resultSet.getFloat("fibers");
                Ingredient ing = new Ingredient(id,name,calories,carbs,proteins,fats,fibers);
                final_list.add(ing);
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ValidationException("Database Exception",400);
        }
        return final_list;
    }
    /*
    *  FINDS all ingredients in the database that contain 'ingredient_substring' (case insensitive)
    *  Throws ValidationException-400 if database exception
    *
    * */

    public List<Ingredient> filter_name(String ingredient_substring) throws ValidationException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        //check if all fields are null
        List<String> args = new ArrayList<>();
        StringBuilder statement = new StringBuilder().append(QUERY_GET_ALL);

        statement.append("\n WHERE name LIKE '%").append(ingredient_substring).append("%';");
        try{
            preparedStatement = connection_ssms.prepareStatement(statement.toString());
            resultSet = preparedStatement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ValidationException("Database Exception",400);
        }
        return build_ingredient_list(resultSet);

    }


    /*
    *  FINDS all ingredients in the database exactly matching the non-null fields of 'filter_ingredient'
    *  Throws ValidationException-400 if database exception
    * */
    public List<Ingredient> filter_precise(Ingredient filter_ingredient) throws ValidationException {
        validator.validate_partial(filter_ingredient);
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        //check if all fields are null

        if(     filter_ingredient.getId() == null &&
                filter_ingredient.getCalories() == null &&
                filter_ingredient.getFiber() == null &&
                filter_ingredient.getProteins() == null &&
                filter_ingredient.getName() ==null &&
                filter_ingredient.getFats() == null &&
                filter_ingredient.getCarbohydrates() == null)
            return getAll();
        try{
            List<Object> args = new ArrayList<>();

            StringBuilder statement = new StringBuilder().append(QUERY_GET_ALL);
            //builds the fields which have to be modified
            statement.append("\n WHERE ");

            if(filter_ingredient.getId() != null){
                statement.append("id=? and ");
                args.add(filter_ingredient.getId());
            }
            if(filter_ingredient.getName() != null){
                statement.append("name=? and ");
                args.add(filter_ingredient.getName());
            }

            if(filter_ingredient.getCalories()!= null)
            {
                statement.append("calories=? and ");
                args.add(filter_ingredient.getCalories());

            }
            if(filter_ingredient.getCarbohydrates()!= null)
            {
                statement.append("carbohydrates=? and ");
                args.add(filter_ingredient.getCarbohydrates());

            }
            if(filter_ingredient.getProteins()!= null)
            {
                statement.append("proteins=? and ");
                args.add(filter_ingredient.getProteins());

            }
            if(filter_ingredient.getFats()!= null)
            {
                statement.append("fats=? and ");
                args.add(filter_ingredient.getFats());
            }
            if(filter_ingredient.getFiber()!= null)
            {
                statement.append("fibers=? and ");
                args.add(filter_ingredient.getFiber());
            }
            //removes last 'and' if needed
            if(statement.toString().endsWith("and "))
                statement = new StringBuilder().append(statement.toString().replaceFirst("....$",""))    ;

            preparedStatement = connection_ssms.prepareStatement(statement.toString());

            setValues(preparedStatement,args);

            resultSet = preparedStatement.executeQuery();

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getErrorCode());
            throw new ValidationException("Database Exception",400);
        }
        return build_ingredient_list(resultSet);
    }

    /*
    *  FINDS all ingredients matching the filter ingredient for the account_id
    *  ex: for precision 1, an ingredient with 10.2 carbs will match for 9.3 carbs
    *  Throws ValidationException-400 if database exception
    * */
    public List<Ingredient> filter_with_precision_for_account(Integer account_id,Ingredient filter_ingredient,Float precision) throws ValidationException {
        validator.validate_partial(filter_ingredient);
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        //check if all fields are null
        if(     filter_ingredient.getId() == null &&
                filter_ingredient.getCalories() == null &&
                filter_ingredient.getFiber() == null &&
                filter_ingredient.getProteins() == null &&
                filter_ingredient.getName() ==null &&
                filter_ingredient.getFats() == null &&
                filter_ingredient.getCarbohydrates() == null)
            return getAllId(account_id);
        try{
            List<Object> args = new ArrayList<>();
            StringBuilder sql = new StringBuilder().append("USE RecipesApp;").append("SELECT ing.id,ing.name,ing.calories,ing.carbohydrates,ing.proteins,ing.fats,ing.fibers\n" +
                    "FROM Account account INNER JOIN Account_ingredients ai ON ai.account_id = account.id INNER JOIN Ingredient ing ON ing.id = ingredient_id\n");
            StringBuilder statement = sql;
            //builds the fields which have to be modified
            statement.append("\n WHERE account.id =? and ");
            args.add(account_id);
            if(filter_ingredient.getName() != null){
                statement.append("ing.name LIKE '%"+filter_ingredient.getName()+"%' and ");
            }

            if(filter_ingredient.getCalories()!= null)
            {
                statement.append("abs(ing.calories-?)<? and ");
                args.add(filter_ingredient.getCalories().toString());
                args.add(precision);

            }
            if(filter_ingredient.getCarbohydrates()!= null)
            {
                statement.append("abs(ing.carbohydrates-?)<? and ");
                args.add(filter_ingredient.getCarbohydrates().toString());
                args.add(precision);
            }
            if(filter_ingredient.getProteins()!= null)
            {
                statement.append("abs(ing.proteins-?)<? and ");
                args.add(filter_ingredient.getProteins().toString());
                args.add(precision);
            }
            if(filter_ingredient.getFats()!= null)
            {
                statement.append("abs(ing.fats-?)<? and ");
                args.add(filter_ingredient.getFats().toString());
                args.add(precision);
            }
            if(filter_ingredient.getFiber()!= null)
            {
                statement.append("abs(ing.fibers-?)<? and ");
                args.add(filter_ingredient.getFiber().toString());
                args.add(precision);
            }
            //removes last 'and' if needed
            if(statement.toString().endsWith("and "))
                statement = new StringBuilder().append(statement.toString().replaceFirst("....$",""))    ;

            preparedStatement = connection_ssms.prepareStatement(statement.toString());
            setValues(preparedStatement,args);

            resultSet = preparedStatement.executeQuery();

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getErrorCode());
            throw new ValidationException("Database Exception",400);
        }
        return build_ingredient_list(resultSet);
    }


    /*
    *  FINDS all ingredients like the filter_precise method, but for the float fields it uses the precision to determine if it matches or not
    *  ex: for precision 1, an ingredient with 10.2 carbs will match for 9.3 carbs
    *  Throws ValidationException-400 if database exception
    * */
    public List<Ingredient> filter_with_precision(Ingredient filter_ingredient,Float precision) throws ValidationException {
        validator.validate_partial(filter_ingredient);
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        //check if all fields are null
        if(     filter_ingredient.getId() == null &&
                filter_ingredient.getCalories() == null &&
                filter_ingredient.getFiber() == null &&
                filter_ingredient.getProteins() == null &&
                filter_ingredient.getName() ==null &&
                filter_ingredient.getFats() == null &&
                filter_ingredient.getCarbohydrates() == null)
            return getAll();
        try{
            List<Object> args = new ArrayList<>();
            StringBuilder statement = new StringBuilder().append(QUERY_GET_ALL);
            //builds the fields which have to be modified
            statement.append("\n WHERE ");
            if(filter_ingredient.getName() != null){
                statement.append("name LIKE '%"+filter_ingredient.getName()+"%' and ");
            }

            if(filter_ingredient.getCalories()!= null)
            {
                statement.append("abs(calories-?)<? and ");
                args.add(filter_ingredient.getCalories().toString());
                args.add(precision);

            }
            if(filter_ingredient.getCarbohydrates()!= null)
            {
                statement.append("abs(carbohydrates-?)<? and ");
                args.add(filter_ingredient.getCarbohydrates().toString());
                args.add(precision);
            }
            if(filter_ingredient.getProteins()!= null)
            {
                statement.append("abs(proteins-?)<? and ");
                args.add(filter_ingredient.getProteins().toString());
                args.add(precision);
            }
            if(filter_ingredient.getFats()!= null)
            {
                statement.append("abs(fats-?)<? and ");
                args.add(filter_ingredient.getFats().toString());
                args.add(precision);
            }
            if(filter_ingredient.getFiber()!= null)
            {
                statement.append("abs(fibers-?)<? and ");
                args.add(filter_ingredient.getFiber().toString());
                args.add(precision);
            }
            //removes last 'and' if needed
            if(statement.toString().endsWith("and "))
                statement = new StringBuilder().append(statement.toString().replaceFirst("....$",""))    ;

            preparedStatement = connection_ssms.prepareStatement(statement.toString());
            setValues(preparedStatement,args);

            resultSet = preparedStatement.executeQuery();

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getErrorCode());
            throw new ValidationException("Database Exception",400);
        }
        return build_ingredient_list(resultSet);
    }



    /*
    * UPDATES the ingredient with the specified id in the database
    * -id of new_ingredient is not used
    * -null values for fields in new_ingredient mean the specified field will not be modified in the existing db object
    *   Throws ValidationException-250 if id wasn't found
    *   Throws ValidationException-200 if id is 0 or null
    *   Throws ValidationException-400 if database exception
    * */
    public void update(Integer id, Ingredient new_ingredient) throws ValidationException {
        validator.validate_partial(new_ingredient);
        if(id.equals(0) || id == null )
            throw new ValidationException("Id must be specified",200);
        PreparedStatement preparedStatement = null;
        //check if all fields are null
        if(new_ingredient.getFiber() == null &&
                new_ingredient.getProteins() == null &&
                new_ingredient.getName() ==null &&
                new_ingredient.getFats() == null &&
                new_ingredient.getCarbohydrates() == null)
                return;
        try{
            List<Object> args = new ArrayList<>();
            StringBuilder statement = new StringBuilder().append(QUERY_UPDATE);
            //builds the fields which have to be modified
            statement.append("SET ");
            if(new_ingredient.getName() != null){
                statement.append("name=?,");
                args.add(new_ingredient.getName());
            }

            if(new_ingredient.getCalories()!= null)
            {
                statement.append("calories=?,");
                args.add(new_ingredient.getCalories());

            }
            if(new_ingredient.getCarbohydrates()!= null)
            {
                statement.append("carbohydrates=?,");
                args.add(new_ingredient.getCarbohydrates());

            }
            if(new_ingredient.getProteins()!= null)
            {
                statement.append("proteins=?,");
                args.add(new_ingredient.getProteins());

            }
            if(new_ingredient.getFats()!= null)
            {
                statement.append("fats=?,");
                args.add(new_ingredient.getFats());
            }
            if(new_ingredient.getFiber()!= null)
            {
                statement.append("fibers=?,");
                args.add(new_ingredient.getFiber());
            }
            //removes extra comma if needed
            if(statement.toString().endsWith(","))
                statement = new StringBuilder().append(statement.toString().replaceFirst(".$",""));

            //builds the id condition
            statement.append("\nWHERE id=?;");
            args.add(id);

            preparedStatement = connection_ssms.prepareStatement(statement.toString());
            setValues(preparedStatement,args);

            int error = preparedStatement.executeUpdate();

            if(error == 0)
                throw  new ValidationException("Id not found.",250);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getErrorCode());
            throw new ValidationException("Database Exception",400);
        }

    }
}
