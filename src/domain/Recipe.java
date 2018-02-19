package domain;

/**
 * Recipes represent a list of ingredients
 */
public class Recipe extends Food {
    Integer id;
    Integer servings;
    public Recipe(Integer id,String name,Integer servings,Integer calories,Float carbohydrates,Float protein,Float fats,Float fibers){
        super(name,calories,carbohydrates,protein,fats,fibers);
        this.id = id;
        this.servings = servings;
    }
    public Recipe(Integer id,String name,Integer servings) {
        super(name, 0);
        this.id = id;
        this.servings = servings;
    }
    public Recipe(String name,Integer servings) {
        super(name, 0);
        this.id = null;
        this.servings = servings;
    }
    public Recipe(String name){
        super(name,0,0f,0f,0f,0f);
        this.servings = 1;
        this.id = null;
    }
    public Integer getServings() {
        return servings;
    }

    public void setServings(Integer servings) {
        this.servings = servings;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}
