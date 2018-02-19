package damian.nanodegree.google.popularmovies;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.squareup.picasso.Picasso;

import damian.nanodegree.google.popularmovies.data.Movie;
import damian.nanodegree.google.popularmovies.databinding.ActivityDetailBinding;
import damian.nanodegree.google.popularmovies.utils.NetworkUtils;

public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_MOVIE_KEY = "movie";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        ActivityDetailBinding dataBinding =
                DataBindingUtil.setContentView(this, R.layout.activity_detail);

        fillUserInterface(dataBinding, getIntent());
    }

    private void fillUserInterface(ActivityDetailBinding dataBinding, Intent intent) {
        Movie extraMovieInfo = (Movie) intent.getSerializableExtra(EXTRA_MOVIE_KEY);

        dataBinding.tvPlot.setText(extraMovieInfo.getPlot());
        dataBinding.tvTitle.setText(extraMovieInfo.getTitle());
        dataBinding.tvRating.setText(String.valueOf(extraMovieInfo.getRating()));
        dataBinding.tvReleaseDate.setText(extraMovieInfo.getReleaseDate());

        Picasso.with(this).load(NetworkUtils.getImageURL(extraMovieInfo)).into(dataBinding.ivCover);
    }
}
