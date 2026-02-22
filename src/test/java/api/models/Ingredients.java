package api.models;

import java.util.List;

public class Ingredients {
    private List<String> ingredients;

    public Ingredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public List<String> getIngredients() { return ingredients; }
}