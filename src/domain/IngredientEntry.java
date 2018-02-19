package domain;

import java.util.Queue;

public class IngredientEntry extends Ingredient{
    private Integer quantity;

    public IngredientEntry(Ingredient ing, Integer quantity){
        super(ing);
        this.quantity = quantity;
    }
    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}