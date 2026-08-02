package com.systemtech.update.adapters;

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

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder>{

   private final ArrayList<Article> articles = new ArrayList<>();
   public static final String WEB_VIEW_URL = "url";

    public void setArticles(List<Article> articles) {
        this.articles.clear();
        this.articles.addAll(new ArrayList<>(articles));
        notifyDataSetChanged();
    }

    private final Context context;

    public ArticleAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.article_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Article article = articles.get(position);
        holder.txtTitle.setText(article.getTitle());
        holder.txtDescription.setText(article.getDescription());
        holder.txtDate.setText(article.getDate());

        // setting an onclick listener for each news post to load the whole post in a new web view
        holder.parent.setOnClickListener(view -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition == RecyclerView.NO_POSITION) {
                return;
            }

            Article selectedArticle = articles.get(adapterPosition);
            Intent intent = new Intent(view.getContext(), WebPageActivity.class);
            intent.putExtra(WEB_VIEW_URL, selectedArticle.getLink());
            view.getContext().startActivity(intent);
        });

        // setting a long onclick listener for each news post to add it to savedPreferences
        holder.parent.setOnLongClickListener(view -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition == RecyclerView.NO_POSITION) {
                return true;
            }

            Article selectedArticle = articles.get(adapterPosition);
            new AlertDialog.Builder(view.getContext())
                    .setTitle("Confirm Save?")
                    .setMessage("Are you sure you want to add this article to Saved Articles?")
                    .setPositiveButton("Yes", (dialog, button) -> {
                        Utils.getInstance(context).addToSharedPreferences(selectedArticle);
                        Toast.makeText(context, "Added this article to your saved articles", Toast.LENGTH_LONG).show();
                    })
                    .setNegativeButton("No", null)
                    .setCancelable(true)
                    .show();
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtTitle, txtDescription, txtDate;
        private CardView parent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtTitle = itemView.findViewById(R.id.title);
            txtDescription = itemView.findViewById(R.id.description);
            txtDate = itemView.findViewById(R.id.date);
            parent = itemView.findViewById(R.id.parent);
        }
    }
}
