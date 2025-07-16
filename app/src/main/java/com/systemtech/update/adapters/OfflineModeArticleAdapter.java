package com.systemtech.update.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.systemtech.update.database.Article;

import java.util.ArrayList;
import java.util.List;

public class OfflineModeArticleAdapter extends RecyclerView.Adapter<OfflineModeArticleAdapter.ViewHolder>{

    private ArrayList<Article> articles = new ArrayList<>();


    public void setArticles(List<Article> articles) {
        this.articles.clear();
        this.articles.addAll(new ArrayList<>(articles));
        notifyDataSetChanged();
    }

    private Context context;

    public OfflineModeArticleAdapter(Context context) {
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
        holder.txtTitle.setText(articles.get(position).getTitle());
        holder.txtDescription.setText(articles.get(position).getDescription());
        holder.txtDate.setText(articles.get(position).getDate());

        // an onclick listener in the offline mode to let users know they cannot view the full article in offline mode
        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "You cannot view full articles in Offline Mode", Toast.LENGTH_LONG).show();
            }
        });

        // an onclick listener to add articles to saved preferences
        holder.parent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Confirm Save?");
                builder.setMessage("Are you sure you want to add this article to Saved Articles?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Utils.getInstance(context).addToSharedPreferences(articles.get(position));
                        Toast.makeText(context, "Added this article to your saved articles", Toast.LENGTH_LONG).show();
                    }
                });
                builder.setCancelable(true);
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // nothing in this block to dismiss alert dialog when clicked
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

    public class ViewHolder extends RecyclerView.ViewHolder {

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
