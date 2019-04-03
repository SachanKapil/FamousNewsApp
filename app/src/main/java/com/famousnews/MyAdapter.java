package com.famousnews;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.famousnews.models.Article;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private Context context;
    private List<Article> newsList;

    public MyAdapter(Context context, List<Article> newsList) {
        this.context = context;
        this.newsList = newsList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_view_item, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, int i) {

        Article model = newsList.get(i);
        Glide.with(context)
                .load(model.getUrlToImage())
                .placeholder(Utils.getRandomDrawbleColor())
                .error(Utils.getRandomDrawbleColor())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        myViewHolder.progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        myViewHolder.progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(myViewHolder.imageView);
        myViewHolder.author.setText(model.getAuthor());
        myViewHolder.published_at.setText(Utils.DateFormat(model.getPublishedAt()));
        myViewHolder.title.setText(model.getTitle());
        myViewHolder.description.setText(model.getDescription());
        myViewHolder.source.setText(model.getSource().getName());
        myViewHolder.time.setText(Utils.DateToTimeFormat(model.getPublishedAt()));

    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, author, published_at, source, time;
        ImageView imageView;
        ProgressBar progressBar;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_title);
            description = itemView.findViewById(R.id.tv_description);
            author = itemView.findViewById(R.id.tv_author);
            published_at = itemView.findViewById(R.id.tv_publishedAt);
            source = itemView.findViewById(R.id.tv_source);
            time = itemView.findViewById(R.id.tv_time);
            imageView = itemView.findViewById(R.id.iv_image);
            progressBar = itemView.findViewById(R.id.progress_bar_load_photo);

        }
    }
}
