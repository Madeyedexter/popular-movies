package app.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Menu;
import android.view.MenuItem;

import app.popularmovies.model.Movie;

public class MainActivity extends AppCompatActivity implements MovieAdapter.ThumbClickListener {

    RecyclerView movieRecyclerView;
    MovieAdapter movieAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        movieRecyclerView = (RecyclerView) findViewById(R.id.rv_movies);
        GridLayoutManager layoutManager = new GridLayoutManager(this,2, LinearLayoutManager.VERTICAL, false);
        movieRecyclerView.setLayoutManager(layoutManager);

        movieAdapter = new MovieAdapter();

        movieRecyclerView.setAdapter(movieAdapter);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.so_most_popular:
                return true;

            case R.id.so_top_rated:
                return true;

            default:return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onThumbClicked(Movie movie) {

        Intent intent = new Intent(MainActivity.this,DetailActivity.class);
        intent.putExtra("movie",movie);
        startActivity(intent);
    }
}
