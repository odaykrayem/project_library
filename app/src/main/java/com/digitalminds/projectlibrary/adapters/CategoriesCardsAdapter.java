package com.digitalminds.projectlibrary.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.digitalminds.projectlibrary.R;
import com.digitalminds.projectlibrary.models.Category;

//todo implement the onclick method when choosing a category
public class CategoriesCardsAdapter extends RecyclerView.Adapter<CategoriesCardsAdapter.ViewHolder> {


    private Category[] categories;

    // RecyclerView recyclerView;
    public CategoriesCardsAdapter(Category[] categories) {
        this.categories = categories;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //the usual shit
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.categories_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category category = categories[position];
        holder.catIcon.setImageResource(category.getIconId());
        holder.catName.setText(category.getName());

        //TODO wait until we get the book counts from server to set the real value
        holder.catBookCounts.setText(String.valueOf(category.getNumOfBooks()));


    }

    @Override
    public int getItemCount() {
        return categories.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView catIcon;
        public TextView catName;
        public TextView catBookCounts;
        public ViewHolder(View itemView) {
            super(itemView);
            this.catIcon = itemView.findViewById(R.id.cat_icon_id);
            this.catName = itemView.findViewById(R.id.cat_name_txt_id);
            this.catBookCounts = itemView.findViewById(R.id.cat_books_count);
        }
    }
}
