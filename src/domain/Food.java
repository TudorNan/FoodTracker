package domain;

import java.sql.ResultSet;

/*
* A food has nutritional values for 100 grams
* */

public class Food {
    private String name;
    private Integer calories;
    private Float carbohydrates;
    private Float proteins;
    private Float fats;
    private Float fiber;

    @Override
    public String toString() {
        return "Food{" +
                "name='" + name + '\'' +
                ", calories=" + calories +
                ", carbohydrates=" + carbohydrates +
                ", proteins=" + proteins +
                ", fats=" + fats +
                ", fiber=" + fiber +
                '}';
    }

    public Food(String name, Integer calories, Float carbohydrates, Float proteins, Float fats, Float fiber) {
        this.name = name;
        this.calories = calories;
        this.carbohydrates = carbohydrates;
        this.proteins = proteins;
        this.fats = fats;
        this.fiber = fiber;
    }
    public Food(String name,Integer calories){
        this.name = name;
        this.calories = calories;
        this.carbohydrates = 0f;
        this.proteins = 0f;
        this.fats =0f;
        this.fiber = 0f;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCalories() {
        return calories;
    }

    public void setCalories(Integer calories) {
        this.calories = calories;
    }

    public Float getCarbohydrates() {
        return carbohydrates;
    }

    public void setCarbohydrates(Float carbohydrates) {
        this.carbohydrates = carbohydrates;
    }

    public Float getProteins() {
        return proteins;
    }

    public void setProteins(Float proteins) {
        this.proteins = proteins;
    }

    public Float getFats() {
        return fats;
    }

    public void setFats(Float fats) {
        this.fats = fats;
    }

    public Float getFiber() {
        return fiber;
    }

    public void setFiber(Float fiber) {
        this.fiber = fiber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Food food = (Food) o;

        if (name != null ? !name.equals(food.name) : food.name != null) return false;
        if (calories != null ? !calories.equals(food.calories) : food.calories != null) return false;
        if (carbohydrates != null ? !carbohydrates.equals(food.carbohydrates) : food.carbohydrates != null)
            return false;
        if (proteins != null ? !proteins.equals(food.proteins) : food.proteins != null) return false;
        if (fats != null ? !fats.equals(food.fats) : food.fats != null) return false;
        return fiber != null ? fiber.equals(food.fiber) : food.fiber == null;
    }

}
