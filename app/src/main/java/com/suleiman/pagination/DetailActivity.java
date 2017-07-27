package com.suleiman.pagination;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.suleiman.pagination.api.MovieApi;
import com.suleiman.pagination.api.MovieService;
import com.suleiman.pagination.models.Result;
import com.suleiman.pagination.models.TopRatedMovies;
import com.suleiman.pagination.utils.PaginationAdapterCallback;
import com.suleiman.pagination.utils.PaginationScrollListener;

import java.util.List;
import java.util.concurrent.TimeoutException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {

    private TextView mMovieTitle;
    private TextView mMovieDesc;
    private TextView mYear; // displays "year | language"
    private ImageView mPosterImg;
    private Result result;
    private static final String BASE_URL_IMG = "https://image.tmdb.org/t/p/w150";

    private static final String TAG = "DetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);

        if (getIntent() != null && getIntent().getExtras().containsKey("result")) {
            result = getIntent().getExtras().getParcelable("result");
        }

        mMovieTitle = (TextView) findViewById(R.id.movie_title);
        mMovieDesc = (TextView) findViewById(R.id.movie_desc);
        mYear = (TextView) findViewById(R.id.movie_year);
        mPosterImg = (ImageView) findViewById(R.id.movie_poster);
        loadMovieDetails(result);
    }

    private void loadMovieDetails(Result result) {
        if (result == null) {
            return;
        }

        mMovieTitle.setText(result.getTitle());
        mYear.setText(
                result.getReleaseDate().substring(0, 4)  // we want the year only
                        + " | "
                        + result.getOriginalLanguage().toUpperCase()
        );
        mMovieDesc.setText(result.getOverview());

        /**
         * Using Glide to handle image loading.
         * Learn more about Glide here:
         * <a href="http://blog.grafixartist.com/image-gallery-app-android-studio-1-4-glide/" />
         */
        Glide
                .with(this)
                .load(BASE_URL_IMG + result.getPosterPath())
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        e.printStackTrace();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {

                        return false;   // return false if you want Glide to handle everything else.
                    }
                })
                .diskCacheStrategy(DiskCacheStrategy.ALL)   // cache both original & resized image
                .centerCrop()
                .crossFade()
                .into(mPosterImg);
    }

    /**
     * Remember to add android.permission.ACCESS_NETWORK_STATE permission.
     *
     * @return
     */
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }
}
