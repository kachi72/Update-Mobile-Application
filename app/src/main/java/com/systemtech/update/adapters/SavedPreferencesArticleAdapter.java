package com.systemtech.update.adapters;

import static com.systemtech.update.adapters.ArticleAdapter.WEB_VIEW_URL;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.systemtech.update.R;
import com.systemtech.update.Utils;
import com.systemtech.update.WebPageActivity;
import com.systemtech.update.database.Article;

import java.util.ArrayList;
import java.util.List;

public class SavedPreferencesArticleAdapter extends RecyclerView.Adapter<SavedPreferencesArticleAdapter.ViewHolder>{

    private final ArrayList<Article> articles = new ArrayList<>();

    private final Context context;
    private final Runnable onArticlesChanged;

    public SavedPreferencesArticleAdapter(Context context, Runnable onArticlesChanged) {
        this.context = context;
        this.onArticlesChanged = onArticlesChanged;
    }

    public void setArticles(List<Article> articles) {
        this.articles.clear();
        this.articles.addAll(new ArrayList<>(articles));
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.saved_preferences_article_item, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Article article = articles.get(position);
        holder.txtCategory.setText(article.getCategory());
        holder.txtTitle.setText(article.getTitle());
        holder.txtDescription.setText(article.getDescription());
        holder.txtDate.setText(article.getDate());
        holder.articleSurface.setBackgroundResource(getCategoryBackground(article.getCategory()));

        // setting an onclick listener for each news post to load the whole post in a new web view
        holder.parent.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition == RecyclerView.NO_POSITION) {
                return;
            }

            Article selectedArticle = articles.get(adapterPosition);
            Intent intent = new Intent(v.getContext(), WebPageActivity.class );
            intent.putExtra(WEB_VIEW_URL, selectedArticle.getLink());
            v.getContext().startActivity(intent);
        });

        // setting a long onclick listener for each news article to be able to delete them from savedPreferences
        holder.parent.setOnLongClickListener(view -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition == RecyclerView.NO_POSITION) {
                return true;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setTitle("Remove saved article?");
            builder.setMessage("This article will be removed from your saved stories.");
            builder.setCancelable(true);
            builder.setPositiveButton("Remove", (dialogInterface, button) -> {
                int currentPosition = holder.getAdapterPosition();
                if (currentPosition == RecyclerView.NO_POSITION) {
                    return;
                }

                Article selectedArticle = articles.get(currentPosition);
                Utils.getInstance(view.getContext()).removeFromSharedPreferences(selectedArticle);
                articles.remove(currentPosition);
                notifyItemRemoved(currentPosition);
                onArticlesChanged.run();
                Toast.makeText(context, "Removed from Saved Stories", Toast.LENGTH_LONG).show();
            });
            builder.setNegativeButton("Keep", null);
            builder.show();
            return true;
        });
    }

    private int getCategoryBackground(String category) {
        if (category == null) {
            return R.drawable.saved_article_neutral;
        }

        switch (category) {
            case "CyberSecurity":
                return R.drawable.gradient_cyber;
            case "AI/ML":
                return R.drawable.gradient_ai;
            case "Software Engineering":
                return R.drawable.gradient_software;
            case "Networking":
                return R.drawable.gradient_network;
            case "Data Science":
                return R.drawable.gradient_data;
            case "UI/UX":
                return R.drawable.gradient_ui;
            default:
                return R.drawable.saved_article_neutral;
        }
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private CardView parent;
        private View articleSurface;
        private TextView txtTitle, txtDescription, txtDate, txtCategory;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.title);
            txtDescription = itemView.findViewById(R.id.description);
            txtCategory = itemView.findViewById(R.id.category);
            txtDate = itemView.findViewById(R.id.date);
            parent = itemView.findViewById(R.id.parent);
            articleSurface = itemView.findViewById(R.id.articleSurface);
        }
    }
}
