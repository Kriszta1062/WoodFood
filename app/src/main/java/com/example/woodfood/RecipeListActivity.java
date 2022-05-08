package com.example.woodfood;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;

public class RecipeListActivity extends AppCompatActivity {

    private static final String LOG_TAG = RecipeListActivity.class.getName();
    private FirebaseUser user;
    private int gridNumber = 1;
    private int recipeLimit = 10;
    private RecyclerView mRecyclerView;
    private ArrayList<Recipe> mRecipesData;
    private RecipeAdapter mAdapter;
    private FirebaseFirestore mFirestore;
    private CollectionReference mRecipes;
    private NotificationHandler mHandler;
    private boolean viewRow = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            Log.e(LOG_TAG, "Authenticated user!");
        }else{
            Log.e(LOG_TAG, "Unauthenticated user!");
            finish();
        }

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, gridNumber));
        mRecipesData = new ArrayList<>();
        mAdapter = new RecipeAdapter(this, mRecipesData);
        mRecyclerView.setAdapter(mAdapter);

        mFirestore = FirebaseFirestore.getInstance();
        mRecipes = mFirestore.collection("Recipes");

        queryData();

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        this.registerReceiver(powerReceiver, filter);

        mHandler = new NotificationHandler(this);

    }
    BroadcastReceiver powerReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String intentAction = intent.getAction();

            if (intentAction == null)
                return;

            switch (intentAction) {
                case Intent.ACTION_POWER_CONNECTED:
                    recipeLimit = 20;
                    queryData();
                    break;
                case Intent.ACTION_POWER_DISCONNECTED:
                    recipeLimit = 10;
                    queryData();
                    break;
            }
        }
    };

    private void queryData() {
        mRecipesData.clear();
        mRecipes.orderBy("saved", Query.Direction.DESCENDING).limit(recipeLimit).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                Recipe recipe = document.toObject(Recipe.class);
                recipe.setId(document.getId());
                mRecipesData.add(recipe);
            }

            if (mRecipesData.size() == 0) {
                initializeData();
                queryData();
            }
            mAdapter.notifyDataSetChanged();
        });
    }

    public void deleteItem(Recipe recipe) {
        DocumentReference ref = mRecipes.document(recipe._getId());
        ref.delete()
                .addOnSuccessListener(success -> {
                    Log.d(LOG_TAG, "Recept sikeresen törölve: " + recipe._getId());
                })
                .addOnFailureListener(fail -> {
                    Toast.makeText(this, "A "+ recipe.getName()+ " recepttje (" + recipe._getId() + ") nem törölhető.", Toast.LENGTH_LONG).show();
                });
        queryData();
        mHandler.cancel();
    }

    private void initializeData() {
        String[] recipesList = getResources().getStringArray(R.array.recipes_names);
        String[] recipesIngredients = getResources().getStringArray(R.array.recipes_ingredients);
        String[] recipesPreparation = getResources().getStringArray(R.array.recipes_preparation);
        String[] recipesTime = getResources().getStringArray(R.array.recipes_time);
        String[] recipesCategory = getResources().getStringArray(R.array.recipes_category);
        TypedArray itemsImageResources = getResources().obtainTypedArray(R.array.recipes_images);

        for (int i = 0; i < recipesList.length; i++) {
            mRecipes.add(new Recipe(
                    recipesList[i],
                    recipesIngredients[i],
                    recipesPreparation[i],
                    recipesTime[i],
                    recipesCategory[i],
                    itemsImageResources.getResourceId(i, 0),
                    0));
        }
        itemsImageResources.recycle();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.recipe_list_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search_bar);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.d(LOG_TAG, s);
                mAdapter.getFilter().filter(s);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.back_button:
                Log.d(LOG_TAG, "Logout clicked!");
                FirebaseAuth.getInstance().signOut();
                finish();
                return true;
            case R.id.recipe_book:
                Log.d(LOG_TAG, "recipe_book clicked!");
                Intent intent = new Intent(this, SavedRecipesActivity.class);
                this.startActivity(intent);
                return true;
            case R.id.new_recipe:
                Log.d(LOG_TAG, "new_recipe clicked!");
                Intent intentNew = new Intent(this, CreateRecipeActivity.class);
                this.startActivity(intentNew);
                return true;
            case R.id.view_selector:
                if (!viewRow) {
                    changeSpanCount(item, R.drawable.ic_view_grid, 1);
                } else {
                    changeSpanCount(item, R.drawable.ic_view_row, 2);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void changeSpanCount(MenuItem item, int drawableId, int spanCount) {
        viewRow = !viewRow;
        item.setIcon(drawableId);
        GridLayoutManager layoutManager = (GridLayoutManager) mRecyclerView.getLayoutManager();
        layoutManager.setSpanCount(spanCount);
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
        queryData();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        unregisterReceiver(powerReceiver);
    }
}



