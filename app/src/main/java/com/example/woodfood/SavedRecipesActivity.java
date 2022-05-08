package com.example.woodfood;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;

public class SavedRecipesActivity extends AppCompatActivity {

    private static final String LOG_TAG = RecipeListActivity.class.getName();

    private FirebaseUser user;
    private int gridNumber = 1;
    private int recipeLimit = 10;
    private RecyclerView mRecyclerView;
    private ArrayList<Recipe> mRecipesData;
    private SavedAdapter mAdapter;
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
        mAdapter = new SavedAdapter(this, mRecipesData);
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
                if(recipe.getSaved() > 0){
                    mRecipesData.add(recipe);
                }
            }

            if (mRecipesData.size() == 0) {
                queryData();
            }
            mAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.saved_recipes_menu, menu);
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
                Log.d(LOG_TAG, "Back clicked!");
                finish();
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

    public void unsaveRecipe(Recipe recipe) {
            Toast.makeText(this, "A(z) "+ recipe.getName()+ "-t törölted a mentett receptjeid közül!", Toast.LENGTH_LONG).show();
            recipe.setSaved(0);

        mRecipes.document(recipe._getId()).update("saved", 0)
                .addOnFailureListener(fail -> {
                    Toast.makeText(this, "A "+ recipe.getName()+ " receptjét (" + recipe._getId() +  " nem lehet megváltoztatni.", Toast.LENGTH_LONG).show();
                });

        mHandler.send(recipe.getName() + "-t törölted a mentett receptjeid közül!");
        queryData();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        unregisterReceiver(powerReceiver);
    }
}



