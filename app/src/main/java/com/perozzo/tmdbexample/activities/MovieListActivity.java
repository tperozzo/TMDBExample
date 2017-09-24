package com.perozzo.tmdbexample.activities;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.perozzo.tmdbexample.R;
import com.perozzo.tmdbexample.classes.APIUtils;
import com.perozzo.tmdbexample.classes.Movie;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Perozzo on 15/09/2017.
 * Activity to search and list the movies
 */

public class MovieListActivity extends AppCompatActivity {

    private String RV_ITENS_SAVED = "rv_itens_saved";
    private String API_KEY = "83d01f18538cb7a275147492f84c3698";
    private String TMDB_URL =
            "https://api.themoviedb.org/3/search/movie?api_key=" + API_KEY + "&language=en-US&query=";
    private String TMDB_URL_PART2 = "&page=1&include_adult=false";
    private String MOVIE = "MOVIE";

    private String movieTitle = "";
    private String year;

    private Context ctx;
    private LinearLayoutManager llm;

    private List<Movie> movieList;

    private MovieAdapter movieAdapter;
    private RecyclerView movieRV;
    private SearchView searchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_movie_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Calendar now = Calendar.getInstance();   // Gets the current date and time
        year = String.valueOf(now.get(Calendar.YEAR));

        ctx = this;

        if(savedInstanceState == null)
            movieList = new ArrayList<>();
        else
            movieList = (ArrayList<Movie>)savedInstanceState.getSerializable(RV_ITENS_SAVED);

        movieRV = (RecyclerView) findViewById(R.id.question_rv);

        llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        movieAdapter = new MovieAdapter(ctx, movieList);
        movieRV.setAdapter(movieAdapter);

        movieRV.setLayoutManager(llm);
    }

    //Method to verify connection
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    protected void onSaveInstanceState(Bundle state) {
        state.putSerializable(RV_ITENS_SAVED, (Serializable)  movieList);
        super.onSaveInstanceState(state);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        // Retrieve the SearchView and plug it into SearchManager
        searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(isOnline()) {

                    movieTitle = searchView.getQuery().toString();
                    movieAdapter.clear();
                    MovieAsyncTask task = new MovieAsyncTask();
                    task.execute(TMDB_URL + movieTitle + TMDB_URL_PART2);
                    searchView.setIconified(true);
                    searchView.clearFocus();

                    (menu.findItem(R.id.action_search)).collapseActionView();
                }
                else{
                    Toast.makeText(ctx, getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
                    searchView.setIconified(true);
                    searchView.clearFocus();

                    (menu.findItem(R.id.action_search)).collapseActionView();

                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });
        //searchView.setIconified(false);
        return true;
    }

    //AsyncTask to load questions
    private class MovieAsyncTask extends AsyncTask<String,Void,List<Movie>> {

        private ProgressDialog progressDialog;

        @Override
        protected List<Movie> doInBackground(String... strings) {
            List<Movie> result = APIUtils.fetchMovies(TMDB_URL + movieTitle + TMDB_URL_PART2);
            if(result != null) {
                if (!result.isEmpty()) {
                    for (int i = 0; i < result.size(); i++)
                        movieList.add(result.get(i));
                }
            }
            return result;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(MovieListActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            super.onPreExecute();
        }
        @Override
        protected void onPostExecute(List<Movie> data) {
            if(progressDialog.isShowing()){
                progressDialog.dismiss();
            }

            movieAdapter.notifyDataSetChanged();
            movieRV.setAdapter(movieAdapter);
        }
    }

    //RecyclerView Adapter
    public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder>{
        private List<Movie> mList;
        private LayoutInflater mLayoutInflater;
        private Context ctx;

        public MovieAdapter(Context c, List<Movie> l){
            ctx = c;
            mList = l;
            mLayoutInflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mLayoutInflater.inflate(R.layout.movie_item, parent, false);
            ViewHolder mvh =  new ViewHolder(view);
            return mvh;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.movie_tv.setText(mList.get(position).getTitle());
            if(mList.get(position).getYear().equals(""))
                holder.year_tv.setText("Unknown");
            else {
                if (mList.get(position).getYear().equals(year)) {
                    holder.year_tv.setTextColor(getResources().getColor(R.color.red));
                    holder.year_tv.setTypeface(null, Typeface.BOLD);
                }
                holder.year_tv.setText(mList.get(position).getYear());
            }
            if(mList.get(position).getPoster_path().equals("") || mList.get(position).getPoster_path().equals("https://image.tmdb.org/t/p/w500/null")) {
                holder.poster_image_pb.setIndeterminate(false);
                holder.poster_image_pb.setVisibility(View.GONE);
                holder.poster_image_iv.setBackgroundColor(getResources().getColor(R.color.grey));
                Picasso.with(ctx).load(R.drawable.unknown).fit().into(holder.poster_image_iv);
            }
            else {
                Picasso.with(ctx).load(mList.get(position).getPoster_path()).fit().into(holder.poster_image_iv, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.poster_image_pb.setIndeterminate(false);
                        holder.poster_image_pb.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        holder.poster_image_pb.setIndeterminate(false);
                        holder.poster_image_pb.setVisibility(View.GONE);
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        public void clear() {
            int size = this.mList.size();
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    this.mList.remove(0);
                }

                this.notifyItemRangeRemoved(0, size);
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            public ImageView poster_image_iv;
            public ProgressBar poster_image_pb;
            public TextView movie_tv;
            public TextView year_tv;
            public LinearLayout ll;

            public ViewHolder(View itemView) {
                super(itemView);
                ll = (LinearLayout) itemView.findViewById(R.id.movie_ll);
                poster_image_iv = (ImageView) itemView.findViewById(R.id.poster_image_iv);
                poster_image_pb = (ProgressBar) itemView.findViewById(R.id.poster_image_pb);
                movie_tv = (TextView) itemView.findViewById(R.id.title_tv);
                year_tv = (TextView) itemView.findViewById(R.id.year_tv);

                ll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(MovieListActivity.this, DetailActivity.class);
                        i.putExtra(MOVIE, mList.get(getAdapterPosition()));
                        startActivity(i);
                    }
                });
            }
        }
    }
}
