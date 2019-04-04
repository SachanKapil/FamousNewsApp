package com.famousnews;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.famousnews.api.NewsApiInterface;
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

    public static final String API_KEY = "7f75980f5eed4b76bef92d49c69edbc8";
    private static final String BASE_URL = "https://newsapi.org/v2/";
    private RecyclerView recyclerView;
    private MyAdapter myAdapter;
    private List<Article> articles = new ArrayList<>();
    private SwipeRefreshLayout refreshLayout;
    private ImageView errorImage;
    private TextView errorTitle, errorMessage;
    private Button btnRetry;
    private RelativeLayout errorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        refreshLayout = findViewById(R.id.refresh_layout);
        recyclerView = findViewById(R.id.recycler_view);
        errorLayout = findViewById(R.id.errorLayout);
        errorImage = findViewById(R.id.errorImage);
        errorTitle = findViewById(R.id.errorTitle);
        errorMessage = findViewById(R.id.errorMessage);
        btnRetry = findViewById(R.id.btnRetry);

        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(R.color.colorAccent);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        loadData();
    }

    private void loadData() {
        refreshLayout.setRefreshing(true);
        if (errorLayout.getVisibility() == View.VISIBLE) {
            errorLayout.setVisibility(View.GONE);
        }
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
                    articles = response.body().getArticle();
                    myAdapter = new MyAdapter(MainActivity.this, articles);
                    recyclerView.setAdapter(myAdapter);
                    myAdapter.notifyDataSetChanged();
                    refreshLayout.setRefreshing(false);
                } else {

                    refreshLayout.setRefreshing(false);
                    String errorCode;
                    switch (response.code()) {
                        case 404:
                            errorCode = "404 not found";
                            break;
                        case 500:
                            errorCode = "500 server broken";
                            break;
                        default:
                            errorCode = "unknown error";
                            break;
                    }
                    showErrorMessage(R.drawable.no_result, "No Result found", "Please Try Again!\n" + errorCode);
                }
            }

            @Override
            public void onFailure(Call<News> call, Throwable t) {
                refreshLayout.setRefreshing(false);
                if (articles.isEmpty()) {
                    showErrorMessage(R.drawable.no_network, "Oops...", "No Internet Connection, Please Try Again");
                }
                else
                {
                    Toast.makeText(MainActivity.this, "No Internet Connection !", Toast.LENGTH_SHORT).show();
                }

            }
        });
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

    @Override
    public void onRefresh() {
        loadData();
    }

    private void showErrorMessage(int imageView, String title, String message) {

        if (errorLayout.getVisibility() == View.GONE) {
            errorLayout.setVisibility(View.VISIBLE);
        }

        errorImage.setImageResource(imageView);
        errorTitle.setText(title);
        errorMessage.setText(message);

        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData();
            }
        });

    }

}
