package domain;

public class Ingredient extends Food{
    Integer id;
    public Ingredient(Integer id,String name, Integer calories, Float carbohydrates, Float proteins, Float fats, Float fiber) {
        super(name, calories, carbohydrates, proteins, fats, fiber);
        this.id = id;
    }
    public Ingredient(Integer id,String name, Integer calories) {
        super(name, calories);
        this.id = id;
    }
    public Ingredient(String name, Integer calories, Float carbohydrates, Float proteins, Float fats, Float fiber) {
        super(name, calories, carbohydrates, proteins, fats, fiber);
        this.id = null;
    }
    public Ingredient(Ingredient other){
        super(other.getName(),other.getCalories(),other.getCarbohydrates(),other.getProteins(),other.getFats(),other.getFiber());
        this.id = other.getId();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public String toString() {
        return "Ingredient{" +
                "id=" + id +
                " " + super.toString() + "}\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Ingredient that = (Ingredient) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }



}
