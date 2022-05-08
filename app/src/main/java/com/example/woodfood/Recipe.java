package com.example.woodfood;

public class Recipe {
    private String id;
    private String name;
    private String ingredients;
    private String preparation;
    private String time;
    private String category;
    private  int imageResource;
    private int saved;

    public Recipe() {}

    public Recipe(String name, String ingredients, String preparation, String time, String category, int imageResource, int saved) {
        this.name = name;
        this.ingredients = ingredients;
        this.preparation = preparation;
        this.time = time;
        this.category = category;
        this.imageResource = imageResource;
        this.saved = saved;
    }

    public String getName() {
        return name;
    }
    public String getIngredients() {
        return ingredients;
    }
    public String getPreparation() {
        return preparation;
    }
    public String getTime() {
        return time;
    }
    public String getCategory() {
        return category;
    }
    public int getImageResource() {
        return imageResource;
    }
    public String _getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public int getSaved() {
        return saved;
    }
    public void setSaved(int saved) {
        this.saved = saved;
    }
}
