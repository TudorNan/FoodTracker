package repository;

import com.sun.org.apache.xpath.internal.operations.Bool;
import domain.Account;
import domain.Ingredient;
import validator.ValidationException;
import validator.Validator;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static utils.Utils.setValues;

public class AccountRepository {
    private Validator<Account> accountValidator;
    private String connectionURL;
    private Connection connection_ssms;

    private String QUERY_INSERT = new StringBuilder().append("USE RecipesApp;").append("INSERT INTO Account(username,password,email,kcalGoal)").append(" VALUES(?,?,?,?);").toString();
    private String QUERY_GET_ALL = new StringBuilder().append("USE RecipesApp;").append("SELECT id,username,email FROM Account").toString();
    private String QUERY_DELETE_ID =  new StringBuilder().append("USE RecipesApp;").append("DELETE FROM Account ").append(
            "WHERE id = ?").toString();
    private String QUERY_UPDATE = new StringBuilder().append("USE RecipesApp;").append("UPDATE Account\n").toString();
    private String QUERY_LOGIN = new StringBuilder().append("USE RecipesApp;").append("SELECT * FROM Account \n WHERE username=? and password=?;").toString();
    private String QUERY_FIND_ID = new StringBuilder().append("USE RecipesApp;").append("SELECT username,email,kcalGoal FROM Account \n WHERE id=?;").toString();
    private String QUERY_GET_PERMISSION = new StringBuilder().append("USE RecipesApp;").append("SELECT permissionLevel FROM Permission \n WHERE account_id=?;").toString();
    private String QUERY_GET_ID_FROM_USER =  new StringBuilder().append("USE RecipesApp;").append("SELECT id FROM Account \n WHERE username=?;").toString();
    private String QUERY_SET_PERMISSION = new StringBuilder().append("USE RecipesApp;").append("INSERT INTO Permission(account_id,permissionLevel)").append(" VALUES(?,?);").toString();
    /*
        * Repository constructor
        * Throws ValidationException-401 if a connection to the database couldn't be established
        * */
    public AccountRepository(Validator<Account> validator, String connectionURL) throws ValidationException {
        this.accountValidator = validator;
        this.connectionURL = connectionURL;
        try{
            connection_ssms = DriverManager.getConnection(connectionURL);
        } catch (SQLException e1) {
            e1.printStackTrace();
            throw new ValidationException("Database Connection Exception",401);
        }
    }

    /*
    * Returns a list of all accounts
    * Throws ValidationException-ErrorCode 400 if there is any database exeption
    * */
    public List<Account> getAll() throws ValidationException {
        List<Account> final_list = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try{
            preparedStatement = connection_ssms.prepareStatement(QUERY_GET_ALL);
            resultSet = preparedStatement.executeQuery();
            final_list = build_account_list(resultSet);
            resultSet.close();
        } catch (SQLException e) {
            throw new ValidationException("Database Exception",400);
        }
        return final_list;
    }

    /*
    * Returns an account with details if the id was found
    * Throws ValidationException-ErrorCode 400 if there is any database exeption
    * Throws ValidationException-250 if id wasn't found
    * */
    public Account getAccountDetails(Integer id) throws ValidationException {
        PreparedStatement preparedStatement = null;
        Boolean found = false;
        String username="",email = "";
        Integer kcalGoal = 0;
        ResultSet resultSet = null;
        try{
            preparedStatement = connection_ssms.prepareStatement(QUERY_FIND_ID);
            preparedStatement.setInt(1,id);

            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                found=true;
                username = resultSet.getString("username");
                email = resultSet.getString("email");
                kcalGoal = resultSet.getInt("kcalGoal");
            }
            resultSet.close();
            if(found)
                return new Account(username,email,kcalGoal);
            else
                throw  new ValidationException("Id not found.",250);

        } catch (SQLException e) {
            throw new ValidationException("Database Exception",400);
        }
    }

    /*
   * RETURNS an Account list from a resultSet of Accounts (id,username,email)
   * Throws ValidationException-400 if database exception
   * */
    public List<Account> build_account_list(ResultSet resultSet) throws ValidationException {
        Integer id;
        String username;
        String email;
        List<Account> final_list = new ArrayList<>();
        try{
            while(resultSet.next()){
                id = resultSet.getInt("id");
                username = resultSet.getString("username");
                email = resultSet.getString("email");
                Account acc = new Account(id,username,email);
                final_list.add(acc);
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ValidationException("Database Exception",400);
        }
        return final_list;
    }

    /*
   * RETURNS an Account with all account details if the username password combination is valid
   * Throws ValidationException-106 if username or password invalid
   * Throws ValidationException-400 if database exception
   * */
    public Account login(String username, String password) throws ValidationException {
        PreparedStatement preparedStatement = null;
        boolean empty_result = true;
        ResultSet resultSet = null;
        Integer id = 0;
        Integer kcalGoal = 0;
        String email = "";
        try {
            preparedStatement = connection_ssms.prepareStatement(QUERY_LOGIN);
            preparedStatement.setString(1,username);
            preparedStatement.setString(2,password);
            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                empty_result = false;
                id = resultSet.getInt("id");
                kcalGoal = resultSet.getInt("kcalGoal");
                email = resultSet.getString("email");
            }
            if(empty_result)
                throw new ValidationException("Invalid username or password",106);
            return new Account(id,username,email,kcalGoal);

        } catch (SQLException e) {
            e.printStackTrace();
            throw new ValidationException("Database Exception",400);
        }
    }


    /*
       *  Stores an Account in the database, 'acc'-id is irrelevant, a new id will be provided
       *  if Kcal Goal is null , 0 will be stored instead
       *  Throws ValidationException-103 if password too weak
       *  Throws ValidationException-101->102 if a fields had an incorrect format
       *  Throws ValidationException-104 db error,account was not saved
       *  Throws ValidationException-400 Database error
       *  Throws ValidationException-502 if there was a problem creating a permission for account
       *  Throws ValidationException-107 db error,username already exists
        *  */
    public void store(Account acc,String password) throws ValidationException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<Object> args = new ArrayList<>();
        accountValidator.validate(acc);
        if (password.length()<6 && password.matches("[0-9a-zA-Z]+"))
            throw new ValidationException("Password too weak.",103);
        try{
            preparedStatement = connection_ssms.prepareStatement(QUERY_INSERT);
            args.add(acc.getUsername());
            args.add(password);
            args.add(acc.getEmail());
            if(acc.getKcalGoal()== null)
                args.add(0);
            else{
                args.add(acc.getKcalGoal());
            }
            setValues(preparedStatement,args);

            int error = preparedStatement.executeUpdate();

            if(error == 0)
                throw new ValidationException("Error saving Account.", 104);
            else{
                //store permission
                Integer id = get_user_id(acc.getUsername());
                preparedStatement = connection_ssms.prepareStatement(QUERY_SET_PERMISSION);
                preparedStatement.setInt(1,id);
                preparedStatement.setInt(2,0);
                error = preparedStatement.executeUpdate();
                if(error == 0)
                {
                    throw new ValidationException("Error saving Permission.",502);
                }

            }
        } catch (SQLException e) {
            if(e.getErrorCode()== 2627)
                throw new ValidationException("Username or email already exists",107);
            else
                throw new ValidationException("Database Exception",400);
        }
    }

    /*
       *  Deletes the Account from the database with the specified id
       *  Throws ValidationException-200 if id is null or 0
       *         ValidationException-250 if id wasn't found
       *         ValidationException-400 if database exception
       * */


    public void delete(Integer id_account) throws ValidationException {
        if(id_account.equals(0) || id_account == null)
            throw new ValidationException("Id must be specified",200);
        PreparedStatement preparedStatement = null;
        try{
            preparedStatement = connection_ssms.prepareStatement(QUERY_DELETE_ID);
            preparedStatement.setInt(1,id_account);
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
        * UPDATES an account's calories goal
        *   Throws ValidationException-200 if id is null or 0
        *   Throws ValidationException-105 if calories goal is null
        *   Throws ValidationException-250 if id wasn't found
        *   Throws ValidationException-400 if database exception
        * */
    public void update_goal(Integer id_account,Integer calories_goal) throws ValidationException {
        accountValidator.validate_partial(new Account(null,null,calories_goal));
        List<Object> args = new ArrayList<>();
        if(id_account.equals(0) || id_account == null )
            throw new ValidationException("Id must be specified",200);
        if(calories_goal == null)
            throw new ValidationException("Calorie goal must be specified",105);
        PreparedStatement preparedStatement = null;
        try{
            StringBuilder statement = new StringBuilder().append(QUERY_UPDATE);
            //builds the fields which have to be modified
            statement.append("SET kcalGoal=?");
            //builds the id condition
            args.add(calories_goal);
            statement.append("\nWHERE id=?;");
            args.add(id_account);
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
    /*
        * UPDATES an account's email
        *   Throws ValidationException-200 if id is null or 0
        *   Throws ValidationException-101 if email format is incorrect
        *   Throws ValidationException-250 if id wasn't found
        *   Throws ValidationException-400 if database exception
        * */
    public void update_email(Integer id_account,String newEmail) throws ValidationException {
        accountValidator.validate_partial(new Account(null,newEmail,(Integer)null));
        List<Object> args = new ArrayList<>();
        if(id_account.equals(0) || id_account == null )
            throw new ValidationException("Id must be specified",200);
        PreparedStatement preparedStatement = null;
        //check if all fields are null
        try{
            StringBuilder statement = new StringBuilder().append(QUERY_UPDATE);
            //builds the fields which have to be modified
            statement.append("SET email=?");
            //builds the id condition
            args.add(newEmail);
            statement.append("\nWHERE id=?;");
            args.add(id_account);


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
    /*
        Updates an account's password
    *   Throws ValidationException-103 if password too weak
    *   Throws ValidationException-200 if id is null or 0
    *   Throws ValidationException-250 if id wasn't found
    *   Throws ValidationException-400 if database exception
    *  */
    public void update_password(Integer id_account,String newPassword) throws ValidationException {

        if (newPassword.length()<7 && newPassword.matches("[a-zA-Z]*"))
            throw new ValidationException("Password too weak.",103);
        List<Object> args = new ArrayList<>();
        if(id_account.equals(0) || id_account == null )
            throw new ValidationException("Id must be specified",200);
        PreparedStatement preparedStatement = null;
        //check if all fields are null
        try{
            StringBuilder statement = new StringBuilder().append(QUERY_UPDATE);
            //builds the fields which have to be modified
            statement.append("SET password=?");
            //builds the id condition
            args.add(newPassword);
            statement.append("\nWHERE id=?;");
            args.add(id_account);


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
    public void publish_recipe(Integer id_account,Integer id_recipe){

    }

    /*
            Retrieves an account's permission level
        *   Throws ValidationException-103 if password too weak
        *   Throws ValidationException-200 if id is null or 0
        *   Throws ValidationException-250 if id wasn't found
        *   Throws ValidationException-400 if database exception
        *  */
    public Integer get_permission(Integer id_account) throws ValidationException {
        PreparedStatement preparedStatement = null;
        Integer permissionLevel= 0;
        ResultSet resultSet = null;
        boolean empty_result = true;
        try {
            preparedStatement = connection_ssms.prepareStatement(QUERY_GET_PERMISSION);
            preparedStatement.setInt(1,id_account);
            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                empty_result=false;
                permissionLevel = resultSet.getInt("permissionLevel");
            }
            if(empty_result)
                throw new ValidationException("Account not found in permissions.",500);
            return permissionLevel;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ValidationException("Database Exception",400);
        }
    }
    public Integer get_user_id(String username) throws ValidationException {
        PreparedStatement preparedStatement = null;
        Integer id=0;
        ResultSet resultSet = null;
        boolean empty_result = true;
        try {
            preparedStatement = connection_ssms.prepareStatement(QUERY_GET_ID_FROM_USER);
            preparedStatement.setString(1,username);
            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                empty_result=false;
                id = resultSet.getInt("id");
            }
            if(empty_result)
                throw new ValidationException("Username not found.",108);
            return id;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ValidationException("Database Exception",400);
        }
    }
}