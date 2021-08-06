package com.digitalminds.projectlibrary.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.digitalminds.projectlibrary.BooksByCategory;
import com.digitalminds.projectlibrary.R;
import com.digitalminds.projectlibrary.models.Category;
import com.digitalminds.projectlibrary.utils.Const;
import com.digitalminds.projectlibrary.utils.SharedPrefs;
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou;

import java.util.ArrayList;

public class CategoriesCardsAdapter extends RecyclerView.Adapter<CategoriesCardsAdapter.ViewHolder> {


    private ArrayList<Category> categories;
    Context context;
    String currentLanguage;

    // RecyclerView recyclerView;
    public CategoriesCardsAdapter(ArrayList<Category> categories, Context context) {
        this.categories = categories;
        this.context = context;
        currentLanguage = SharedPrefs.getString(context, SharedPrefs.GENERAL_FILE, SharedPrefs.KEY_APP_LANGUAGE_ID, SharedPrefs.APP_LANGUAGE_ENGLISH);
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
        Category category = categories.get(position);
        //just for performance cuz i need this values multiple times
        String catName = category.getCategoryName(currentLanguage);

        GlideToVectorYou
                .init()
                .with(context)
                .load(Uri.parse(category.getCategoryIcon()), holder.catIcon);

        holder.catName.setText(catName);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toCategory = new Intent(context, BooksByCategory.class);
                toCategory.putExtra(Const.KEY_CATEGORIES_EN, category.getCategoryNameEN());
                toCategory.putExtra(Const.KEY_CATEGORIES_AR, category.getCategoryNameAR());
                toCategory.putExtra(Const.KEY_CATEGORIES_KU, category.getCategoryNameKU());
                context.startActivity(toCategory);
            }
        });

    }

    @Override
    public int getItemCount() {
        return categories.size();
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
