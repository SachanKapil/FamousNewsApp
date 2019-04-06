package com.famousnews;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import com.famousnews.api.NewsApiInterface;
import com.famousnews.database.AppDatabase;
import com.famousnews.database.UserDAO;
import com.famousnews.models.Article;
import com.famousnews.models.News;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private static final String API_KEY = "7f75980f5eed4b76bef92d49c69edbc8";
    private static final String BASE_URL = "https://newsapi.org/v2/";
    private RecyclerView recyclerView;
    private MyAdapter myAdapter;
    private final List<Article> articles = new ArrayList<>();
    private SwipeRefreshLayout refreshLayout;
    private UserDAO userDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_view);
        refreshLayout = findViewById(R.id.refresh_layout);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(R.color.lightGreen);

        AppDatabase appDatabase = AppDatabase.getAppDatabase();
        userDAO = appDatabase.userDao();

        setAdapterToRecyclerView(articles);
        loadData();
    }

    private void loadData() {
        refreshLayout.setRefreshing(true);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        NewsApiInterface newsApiInterface = retrofit.create(NewsApiInterface.class);
        String country = Utils.getCountry();
        Call<News> call = newsApiInterface.getNews(country, API_KEY);
        call.enqueue(new Callback<News>() {
            @Override
            public void onResponse(Call<News> call, Response<News> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (!articles.isEmpty()) {
                        articles.clear();
                    }
                    List<Article> latestArticlesList = response.body().getArticle();
                    articles.addAll(latestArticlesList);
                    updateDatabase(latestArticlesList);
                    refreshLayout.setRefreshing(false);
                } else {
                    refreshLayout.setRefreshing(false);
                    loadOldArticlesFromDatabase();
                    showErrorMessage("No Result found, Error !");
                }
            }

            @Override
            public void onFailure(Call<News> call, Throwable t) {
                refreshLayout.setRefreshing(false);
                loadOldArticlesFromDatabase();
                showErrorMessage("No Internet Connection !");
            }
        });
    }

    private void updateDatabase(List<Article> articles) {
        userDAO.delete();
        for (Article article : articles) {
            userDAO.insertArticle(article);
        }
        myAdapter.notifyDataSetChanged();
    }

    private void loadOldArticlesFromDatabase() {
        List<Article> oldArticlesList = userDAO.getArticle();
        articles.addAll(oldArticlesList);
        myAdapter.notifyDataSetChanged();
    }

//    private void loadDataWithSelfParsing() {
//        refreshLayout.setRefreshing(true);
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(BASE_URL)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//        NewsApiInterface newsApiInterface = retrofit.create(NewsApiInterface.class);
//        String country = Utils.getCountry();
//        Call<ResponseBody> call = newsApiInterface.getNews(country, API_KEY);
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    if (!articles.isEmpty()) {
//                        articles.clear();
//                    }
//                    articles = parseJsonResponseToModel(response).getArticle();
//                    myAdapter = new MyAdapter(MainActivity.this, articles);
//                    recyclerView.setAdapter(myAdapter);
//                    myAdapter.notifyDataSetChanged();
//                    refreshLayout.setRefreshing(false);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                refreshLayout.setRefreshing(false);
//                Toast.makeText(MainActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
//
//            }
//        });
//
//    }
//
//    private News parseJsonResponseToModel(Response<ResponseBody> response) {
//        News myNews = new News();
//        if (response.body() != null) {
//            try {
//                String myResponse = response.body().string();
//                JSONObject myObject = new JSONObject(myResponse);
//                myNews.setStatus(myObject.getString("status"));
//                myNews.setTotalResults(myObject.getInt("totalResults"));
//                JSONArray myArray = myObject.getJSONArray("articles");
//                List<Article> articlesList = new ArrayList<>();
//                for (int i = 0; i < myArray.length(); i++) {
//                    Article myArticle = new Article();
//                    Source mySource = new Source();
//                    JSONObject myArticleObject = myArray.getJSONObject(i);
//                    JSONObject mySourceObject = myArticleObject.getJSONObject("source");
//                    mySource.setId(mySourceObject.getString("id"));
//                    mySource.setName(mySourceObject.getString("name"));
//                    myArticle.setSource(mySource);
//                    myArticle.setAuthor(myArticleObject.getString("author"));
//                    myArticle.setTitle(myArticleObject.getString("title"));
//                    myArticle.setDescription(myArticleObject.getString("description"));
//                    myArticle.setUrl(myArticleObject.getString("url"));
//                    myArticle.setUrlToImage(myArticleObject.getString("urlToImage"));
//                    myArticle.setPublishedAt(myArticleObject.getString("publishedAt"));
//                    myArticle.setContent(myArticleObject.getString("content"));
//                    articlesList.add(myArticle);
//                }
//                myNews.setArticle(articlesList);
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//        return myNews;
//    }

    private void setAdapterToRecyclerView(List<Article> articles) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        myAdapter = new MyAdapter(this, articles);
        recyclerView.setAdapter(myAdapter);
        initListener();
    }

    private void initListener() {
        myAdapter.setOnItemClickListener(new MyAdapter.ItemCallback() {
            @Override
            public void onItemClick(int position) {
                Article clickedArticle = articles.get(position);
                Intent intent = new Intent(MainActivity.this, NewsDetailActivity.class);
                intent.putExtra("url", clickedArticle.getUrl());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onRefresh() {
        loadData();
    }

    private void showErrorMessage(String message) {
        Snackbar mSnackBar = Snackbar.make(refreshLayout, message, Snackbar.LENGTH_INDEFINITE)
                .setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        loadData();
                    }
                });
        mSnackBar.setActionTextColor(Color.WHITE);
        mSnackBar.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    protected void onDestroy() {
        AppDatabase.destroyInstance();
        super.onDestroy();
    }
}
