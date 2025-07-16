package com.systemtech.update.adapters;

import static com.systemtech.update.adapters.ArticleAdapter.WEB_VIEW_URL;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
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

public class SavedPreferencesArticleAdapter extends RecyclerView.Adapter<SavedPreferencesArticleAdapter.ViewHolder>{

    private static final String TAG = "SavedPreferencesArticle";

    private ArrayList<Article> articles = new ArrayList<>();

    private Context context;

    public SavedPreferencesArticleAdapter(Context context) {
        this.context = context;
    }

    public void setArticles(ArrayList<Article> articles) {
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
        holder.txtCategory.setText(articles.get(position).getCategory());
        holder.txtTitle.setText(articles.get(position).getTitle());
        holder.txtDescription.setText(articles.get(position).getDescription());
        holder.txtDate.setText(articles.get(position).getDate());
        String link = articles.get(position).getLink();

        // setting an onclick listener for each news post to load the whole post in a new web view
        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), WebPageActivity.class );
                intent.putExtra(WEB_VIEW_URL, link);
                v.getContext().startActivity(intent);
            }
        });

        // setting a long onclick listener for each news article to be able to delete them from savedPreferences
        holder.parent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Confirm deletion?");
                builder.setMessage("Are you sure you want to delete this article from Saved Articles?");
                builder.setCancelable(true);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Utils.getInstance(view.getContext()).removeFromSharedPreferences(articles.get(position));
                        articles.remove(position);

                        // testing if my deletion works fine
//                        ArrayList<Article> size_test = new ArrayList<>();
//                        size_test = Utils.instance.getUserFav();
//                        Log.d(TAG, "onClick: inside delete onclick, number of articles inside utils is:" + size_test.size());
                        notifyItemRemoved(position);
                        Toast.makeText(context, "Removed this article from Saved Articles", Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private CardView parent;
        private TextView txtTitle, txtDescription, txtDate, txtCategory;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.title);
            txtDescription = itemView.findViewById(R.id.description);
            txtCategory = itemView.findViewById(R.id.category);
            txtDate = itemView.findViewById(R.id.date);
            parent = itemView.findViewById(R.id.parent);
        }
    }
}
