package service;


import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import domain.Account;
import domain.Ingredient;
import domain.IngredientEntry;
import domain.Recipe;
import repository.AccountRepository;
import repository.IngredientRepository;
import repository.RecipeRepository;
import utils.Observable;
import validator.ValidationException;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class Service extends Observable {
    private IngredientRepository ingredientRepository;
    private AccountRepository accountRepository;
    private RecipeRepository recipeRepository;
    public static int ADMIN_LEVEL = 1;

    public Service(IngredientRepository ingredientRepository, AccountRepository accountRepository, RecipeRepository recipeRepository) {
        this.ingredientRepository = ingredientRepository;
        this.accountRepository = accountRepository;
        this.recipeRepository = recipeRepository;
    }
    /*
    * RETURNS an Account with all account details if the username password combination is valid
     */
    public Account login(String username, String password) throws ValidationException {
        return accountRepository.login(username,password);
    }

    /*
       *  Stores an Account in the database, 'acc'-id is irrelevant, a new id will be provided
       * */
    public void register(String username,String email,String password) throws ValidationException {
        Account account = new Account(username,email,0);
        accountRepository.store(account,password);
    }
    /*
   * Returns an account with details if the id was found
   * */
    public Account get_account_details(Integer account_id) throws ValidationException {
        return accountRepository.getAccountDetails(account_id);
    }


    /*
    * UPDATES an account's calories goal
    * */
    public void update_account_goal(Integer account_id,Integer kcalGoal) throws ValidationException {
        accountRepository.update_goal(account_id,kcalGoal);
        notify_controllers();
    }


    /*
    *  UPDATES an account's email
    * */
    public void update_account_email(Integer account_id,String account_password,String new_email) throws ValidationException {
        accountRepository.login(accountRepository.getAccountDetails(account_id).getUsername(),account_password);
        accountRepository.update_email(account_id,new_email);
    }
    /*
   *  UPDATES an account's password
   * */
    public void update_account_password(Integer account_id,String old_password,String new_password) throws ValidationException {
        accountRepository.login(accountRepository.getAccountDetails(account_id).getUsername(),old_password);
        accountRepository.update_password(account_id,new_password);
    }
    /*
   *  Stores an ingredient, with nutritional values for 100 grams
   * */
    public void store_ingredient(Integer account_id ,String name,Integer calories,Float carbohydrates,Float proteins,Float fats,Float fibers) throws ValidationException {
        ingredientRepository.store(account_id,new Ingredient(name,calories,carbohydrates,proteins,fats,fibers));
    }
    /*
       *  Updates an ingredient, account_id is needed for permission,ingredient_id must be specified, null fields will not be changed
       * */
    public void update_ingredient(Integer acccount_id,Integer ingredient_id,String new_name,Integer new_calories,Float new_carbohydrates,Float new_proteins,Float new_fats,Float new_fibers) throws ValidationException {
        if(accountRepository.get_permission(acccount_id)>= ADMIN_LEVEL)
            ingredientRepository.update(ingredient_id,new Ingredient(new_name,new_calories,new_carbohydrates,new_proteins,new_fats,new_fibers));

        else{
            throw new ValidationException("No permission for this action.",500);
        }
    }

    public List<Ingredient> get_filtered_ingredients_for_account(Integer account_id,String name,Integer calories,
                                                                 Float carbohydrates,Float proteins,Float fats,Float fibers) throws ValidationException {
        return ingredientRepository.filter_with_precision_for_account(account_id,new Ingredient(name,calories,carbohydrates,proteins,fats,fibers),0.2f);

    }

    public List<Ingredient>get_filtered_ingredients_by_name(String name) throws ValidationException {
        return ingredientRepository.getAll()
                .stream()
                .filter((account)->account.getName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }
    public List<Ingredient> get_all_ingredients_for_account(Integer account_id) throws ValidationException {
        return ingredientRepository.getAllId(account_id);
    }

    /*Deletes an ingredient, acccount_id needed for permission.
        Throws ValidationException-500 if permission level not high enough
    * */
    public void delete_ingredient(Integer acccount_id,Integer id_ingredient) throws ValidationException {

        if(accountRepository.get_permission(acccount_id)>=ADMIN_LEVEL)
            ingredientRepository.delete(id_ingredient);
        else{
            throw new ValidationException("No permission for this action.",500);
        }
    }

    public void notify_controllers(){
        notifyObservers();
    }

    public List<Recipe> get_all_recipes() throws ValidationException {
        return recipeRepository.getAll();
    }
    public List<Recipe> get_all_recipes_by_name(String name) throws  ValidationException{
        return recipeRepository.getAll()
                .stream()
                .filter((account)->account.getName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }
    public List<Recipe> get_all_recipes_for_account(Integer account_id) throws ValidationException {
        return recipeRepository.getAll_account(account_id);
    }
    public List<IngredientEntry> get_all_ingredients_for_recipe(Integer recipe_id) throws ValidationException {
        return recipeRepository.get_ingredients_for_recipe(recipe_id);
    }
    public List<Recipe> get_all_recipes_for_account_by_name(Integer account_id,String name) throws ValidationException {
        return recipeRepository.getAll_account(account_id)
                .stream()
                .filter((recipe)->recipe.getName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }
    public List<Ingredient> get_all_ingredients() throws ValidationException {
        return ingredientRepository.getAll();
    }
    public List<Ingredient> get_filtered_ingredients(String name,Integer calories,
                                                                 Float carbohydrates,Float proteins,Float fats,Float fibers) throws ValidationException {
        return ingredientRepository.filter_with_precision(new Ingredient(name,calories,carbohydrates,proteins,fats,fibers),0.2f);

    }
    public void delete_recipe(Integer account_id,Integer id_recipe) throws ValidationException {

        if (accountRepository.get_permission(account_id) >= ADMIN_LEVEL)
            recipeRepository.delete_recipe(id_recipe);
        else {
            throw new ValidationException("No permission for this action.", 500);
        }
    }
    public void store_recipe(Integer account_id,Recipe recipe,List<IngredientEntry> ingredientEntries) throws ValidationException {
            recipeRepository.store_recipe(account_id,recipe,ingredientEntries);
    }
    public void update_recipe(Integer account_id,Integer id_recipe,Recipe new_recipe,List<IngredientEntry> new_ingredientEntries) throws ValidationException {
        if (accountRepository.get_permission(account_id) >= ADMIN_LEVEL){
            recipeRepository.delete_recipe(id_recipe);
            recipeRepository.store_recipe(account_id, new_recipe, new_ingredientEntries);
        }else {
            throw new ValidationException("No permission for this action.", 500);
        }
    }
    public void export_recipe_pdf(Recipe rec,String PATH) throws ValidationException {
        Document document = new Document();
        PATH  = new StringBuilder(PATH).append(rec.getName()+".pdf").toString();

        List<IngredientEntry> ingredientEntries = get_all_ingredients_for_recipe(rec.getId());
        try {
            PdfWriter.getInstance(document,new FileOutputStream(PATH));
            document.open();
            document.add(new Paragraph("Ingredients for '" + rec.getName()+"'.",new Font(BaseFont.createFont(),25)));
            document.add(new Paragraph("Servings: "+rec.getServings()));
            document.add(new Paragraph("Ingredients list: \n"));
            document.add(new Paragraph("\n"));

            for (IngredientEntry ingredientEntry : ingredientEntries) {
                document.add(new Paragraph(ingredientEntry.getName() + " - "+ingredientEntry.getQuantity()+"g"));
            }

            document.close();
        } catch (DocumentException e) {
            throw new ValidationException("Error saving pdf.",900);
        } catch (FileNotFoundException e) {
            throw new ValidationException("Error saving pdf.",900);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
