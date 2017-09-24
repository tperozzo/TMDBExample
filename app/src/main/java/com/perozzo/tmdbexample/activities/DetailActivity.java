package com.perozzo.tmdbexample.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.perozzo.tmdbexample.R;
import com.perozzo.tmdbexample.classes.Movie;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * Created by Perozzo on 15/09/2017.
 * Activity to show the movies detail
 */

public class DetailActivity extends AppCompatActivity {

    private String MOVIE = "MOVIE";
    private Movie movie;
    private Context ctx;

    private FrameLayout frameLayout;
    private ImageView backdrop_image_iv;
    private ProgressBar backdrop_image_pb;
    private TextView title_tv;
    private TextView vote_average_tv;
    private TextView vote_count_tv;
    private TextView overview_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ctx = this;

        frameLayout = (FrameLayout) findViewById(R.id.frame_layout);
        backdrop_image_iv = (ImageView) findViewById(R.id.backdrop_image_iv);
        backdrop_image_pb = (ProgressBar) findViewById(R.id.backdrop_image_pb);
        title_tv = (TextView) findViewById(R.id.title_tv);
        vote_average_tv = (TextView) findViewById(R.id.vote_average_tv);
        vote_count_tv = (TextView) findViewById(R.id.vote_count_tv);
        overview_tv = (TextView) findViewById(R.id.overview_tv);

        final Intent intent = getIntent();
        movie = (Movie) intent.getSerializableExtra(MOVIE);
        if(movie != null) {
            initWidgets();
        }
    }

    //Initialize widgets from interface
    private void initWidgets(){
        if(movie.getBackdrop_path().equals(null) || movie.getBackdrop_path().equals("") || movie.getBackdrop_path().equals("https://image.tmdb.org/t/p/w500/null")) {
            frameLayout.setVisibility(View.GONE);
            backdrop_image_iv.setVisibility(View.GONE);
            backdrop_image_pb.setIndeterminate(false);
            backdrop_image_pb.setVisibility(View.GONE);
        }
        else {
            if(isOnline()) {
                Picasso.with(ctx).load(movie.getBackdrop_path()).fit().into(backdrop_image_iv, new Callback() {
                    @Override
                    public void onSuccess() {
                        backdrop_image_pb.setIndeterminate(false);
                        backdrop_image_pb.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        backdrop_image_pb.setIndeterminate(false);
                        backdrop_image_pb.setVisibility(View.GONE);
                    }
                });
            }
            else {
                Toast.makeText(ctx, getString(R.string.no_connection_load_image), Toast.LENGTH_SHORT).show();
                backdrop_image_pb.setIndeterminate(false);
                backdrop_image_pb.setVisibility(View.GONE);
                frameLayout.setVisibility(View.GONE);
                backdrop_image_iv.setVisibility(View.GONE);
            }
        }
        title_tv.setText(movie.getTitle());
        vote_average_tv.setText(getString(R.string.vote_average) + " " + String.valueOf(movie.getVote_average()));
        vote_count_tv.setText(getString(R.string.vote_count) + " " + String.valueOf(movie.getVote_count()));

        if(movie.getOverview().equals(""))
            overview_tv.setVisibility(View.GONE);
        else
            overview_tv.setText(movie.getOverview());
    }

    //Method to verify connection, necessary to download the image
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
