package com.example.woodfood;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;

public class SelectedRecipeActivity extends AppCompatActivity {

    private static final String LOG_TAG = SelectedRecipeActivity.class.getName();
    private FirebaseFirestore mFirestore;
    private CollectionReference mRecipes;
    private ArrayList<Recipe> mRecipesData;
    private RecipeAdapter mAdapter;
    private NotificationHandler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selected_recipe);

        Bundle extras = getIntent().getExtras();
        String id = extras.getString("id");
        Log.d(LOG_TAG, "id: " + id);

        mFirestore = FirebaseFirestore.getInstance();
        mRecipes = mFirestore.collection("Recipes");
        mRecipes.document(id).get().addOnSuccessListener(snapshot -> {
            Recipe recipe = snapshot.toObject(Recipe.class);
            recipe.setId(snapshot.getId());

            Log.d(LOG_TAG, "aktualis recept "+ recipe.getName());

            TextView recipeTitle = findViewById(R.id.recipeTitle);
            TextView ingredients = findViewById(R.id.ingredients);
            TextView preparation = findViewById(R.id.preparation);
            TextView time = findViewById(R.id.time);
            ImageView recipeImage = findViewById(R.id.recipeImage);
            TextView category = findViewById(R.id.category);

            recipeTitle.setText(recipe.getName());
            ingredients.setText(recipe.getIngredients());
            preparation.setText(recipe.getPreparation());
            time.setText(recipe.getTime());
            category.setText(recipe.getCategory());
            Glide.with(this).load(recipe.getImageResource()).into(recipeImage);

            this.findViewById(R.id.save_recipe).setOnClickListener(view ->{ updateAlertIcon(recipe);});

            mRecipesData = new ArrayList<>();
            mAdapter = new RecipeAdapter(this, mRecipesData);
            mFirestore = FirebaseFirestore.getInstance();
            mRecipes = mFirestore.collection("Recipes");
            mHandler = new NotificationHandler(this);
        });
    }
    public void back(View view) {
        finish();
    }

 @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.selected_recipe_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.back_button:
                Log.d(LOG_TAG, "Back clicked!");
                finish();
                return true;
            case R.id.recipe_book:
                Log.d(LOG_TAG, "recipe_book clicked!");
                Intent intent = new Intent(this, SavedRecipesActivity.class);
                this.startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void updateAlertIcon(Recipe recipe) {
        if (0 != recipe.getSaved()) {
            Toast.makeText(this, "A(z) "+ recipe.getName()+ " receptjét már elmentetted!", Toast.LENGTH_LONG).show();
            recipe.setSaved(1);
        } else {
            Toast.makeText(this, "A(z) "+ recipe.getName()+ " receptjét sikeresen elmentetted! :)", Toast.LENGTH_LONG).show();
            recipe.setSaved(1);
        }

        mRecipes.document(recipe._getId()).update("saved", recipe.getSaved()+1)
                .addOnFailureListener(fail -> {
                    Toast.makeText(this, "A "+ recipe.getName()+ " recepttje (" + recipe._getId() +  " nem lehet megváltoztatni.", Toast.LENGTH_LONG).show();
                });

        mHandler.send(recipe.getName() + " a mentett receptjeid között! :)");
    }
}