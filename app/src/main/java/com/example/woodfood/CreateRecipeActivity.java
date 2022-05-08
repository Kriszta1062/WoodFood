package com.example.woodfood;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;


public class CreateRecipeActivity extends AppCompatActivity {

    private static final String LOG_TAG = RegisterAcitivity.class.getName();

    EditText recipeNameEditText;
    EditText recipeCategoryEditText;
    EditText recipeIngredientsEditText;
    EditText recipePrepEditText;
    EditText recipeTimeEditText;

    private FirebaseFirestore mFirestore;
    private CollectionReference mRecipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_recipe);

        recipeNameEditText = findViewById(R.id.recipeNameEditText);
        recipeCategoryEditText = findViewById(R.id.recipeCategoryEditText);
        recipeIngredientsEditText = findViewById(R.id.recipeIngredientsEditText);
        recipePrepEditText = findViewById(R.id.recipePrepEditText);
        recipeTimeEditText = findViewById(R.id.recipeTimeEditText);

        mFirestore = FirebaseFirestore.getInstance();
        mRecipes = mFirestore.collection("Recipes");

        Log.i(LOG_TAG, "onCreate");
    }

    public void createRecipe(View view) {
        String name = recipeNameEditText.getText().toString();
        String category = recipeCategoryEditText.getText().toString();
        String ingredients = recipeIngredientsEditText.getText().toString();
        String preparation = recipePrepEditText.getText().toString();
        String time = recipeTimeEditText.getText().toString();

        Log.i("CreateRecipeActivity", "Új recept létrehozva: " + name);

        mRecipes.add(new Recipe(name, ingredients, preparation, time, category, R.drawable.food,0));

        startBrowsing();
    }
    public void cancel(View view) {
        finish();
    }

    private void startBrowsing(){
        Intent intent = new Intent(this, RecipeListActivity.class);
        startActivity(intent);
    }
}