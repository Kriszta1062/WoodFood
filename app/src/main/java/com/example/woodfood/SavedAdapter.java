package com.example.woodfood;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;

public class SavedAdapter extends RecyclerView.Adapter<SavedAdapter.ViewHolder> implements Filterable {

    private ArrayList<Recipe> mRecipeData = new ArrayList<>();
    private ArrayList<Recipe> mRecipeDataAll = new ArrayList<>();
    private Context mContext;
    private int lastPosition = -1;

    SavedAdapter(Context context, ArrayList<Recipe> recipesData) {
        this.mRecipeData = recipesData;
        this.mRecipeDataAll = recipesData;
        this.mContext = context;
    }

    @Override
    public SavedAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.saved_recipes_list, parent, false));
    }

    @Override
    public void onBindViewHolder(SavedAdapter.ViewHolder holder, int position) {
        Recipe currentRecipe = mRecipeData.get(position);
        holder.bindTo(currentRecipe);

        if(holder.getAdapterPosition() > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.sample_anim);
            holder.itemView.startAnimation(animation);
            lastPosition = holder.getAdapterPosition();
        }
    }

    @Override
    public int getItemCount() {
        return mRecipeData.size();
    }

    @Override
    public Filter getFilter() {
        return recipeFilter;
    }

    private Filter recipeFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<Recipe> filteredList = new ArrayList<>();
            FilterResults results = new FilterResults();

            if(charSequence == null || charSequence.length() == 0) {
                results.count = mRecipeDataAll.size();
                results.values = mRecipeDataAll;
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();
                for(Recipe recipe : mRecipeDataAll) {
                    if(recipe.getName().toLowerCase().contains(filterPattern)){
                        filteredList.add(recipe);
                    }
                }
                results.count = filteredList.size();
                results.values = filteredList;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            mRecipeData = (ArrayList)filterResults.values;
            notifyDataSetChanged();
        }
    };

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mTitleText;
        private TextView mTimeText;
        private ImageView mRecipeImage;
        private TextView mCategory;

        ViewHolder(View recipeView) {
            super(recipeView);

            mTitleText = recipeView.findViewById(R.id.recipeTitle);
            mRecipeImage = recipeView.findViewById(R.id.recipeImage);
            mCategory = recipeView.findViewById(R.id.category);
            mTimeText = recipeView.findViewById(R.id.time);
        }

        void bindTo(Recipe currentRecipe){
            mTitleText.setText(currentRecipe.getName());
            mTimeText.setText(currentRecipe.getTime());
            mCategory.setText(currentRecipe.getCategory());

            Glide.with(mContext).load(currentRecipe.getImageResource()).into(mRecipeImage);
            itemView.findViewById(R.id.more).setOnClickListener(view -> {
                Intent intent = new Intent(this.mTitleText.getContext(), SelectedRecipeActivity.class);
                intent.putExtra("id", currentRecipe._getId());
                this.mTitleText.getContext().startActivity(intent);
                Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.blink_anim);
                view.startAnimation(animation);
            });
            itemView.findViewById(R.id.unsave).setOnClickListener(view ->{
                ((SavedRecipesActivity)mContext).unsaveRecipe(currentRecipe);
                Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.blink_anim);
                view.startAnimation(animation);
            });
        }
    }
}
