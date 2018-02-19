package validator;

import domain.Ingredient;

import static java.lang.Float.compare;

public class IngredientValidator implements Validator<Ingredient>{

    /*
    * Validates an ingredient for storing
    * Throws ValidationException-300 if fields are incorrect
    * */
    @Override
    public void validate(Ingredient obj) throws ValidationException {
        String message = "";
        if(compare(obj.getCalories(),0)<0 ||
                compare(obj.getCarbohydrates(),0)<0 ||
                compare(obj.getFats(),0)<0 ||
                compare(obj.getFiber(),0)<0 ||
                compare(obj.getProteins(),0)<0)
            message+= "Nutritional values cannot pe less than 0.\n";
        if(obj.getName() == "")
            message+= "Name of an ingredient must be specified.";
        if(message != "")
            throw new ValidationException(message,300);
    }

    /*
    * Validates an object with null fields(replaces them with correct values)
    * Throws ValidationException-300 if non-null fields are incorrect
    * */
    @Override
    public void validate_partial(Ingredient obj) throws ValidationException {
        Integer id;
        Float carbs;
        Float fats;
        Float fibers;
        Float proteins;
        Integer calories;
        String name;
        if(obj.getId() == null)
            id = 0;
        else{
            id=obj.getId();
        }
        if(obj.getCalories() == null)
            calories = 0;
        else{
            calories=obj.getCalories();
        }
        if(obj.getCarbohydrates() == null)
            carbs = 0f;
        else{
            carbs=obj.getCarbohydrates();
        }
        if(obj.getProteins() == null)
            proteins = 0f;
        else{
            proteins=obj.getProteins();
        }
        if(obj.getName() == null)
            name = "empty";
        else{
            name = obj.getName();
        }

        if(obj.getFats() == null)
            fats = 0f;
        else{
            fats=obj.getFats();
        }
        if(obj.getFiber() == null)
            fibers = 0f;
        else{
            fibers=obj.getFiber();
        }
        Ingredient ing = new Ingredient(id,name,calories,carbs,proteins,fats,fibers);
        validate(ing);

    }
}
