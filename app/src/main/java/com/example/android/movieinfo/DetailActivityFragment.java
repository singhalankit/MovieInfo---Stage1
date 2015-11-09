package com.example.android.movieinfo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by Singhal on 11/8/2015.
 */
public class DetailActivityFragment extends Fragment {
    private final String LOG_TAG = DetailActivityFragment.class.getSimpleName();
    MovieParse movie;
    public DetailActivityFragment() {

    }

    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        Intent detail_intent = getActivity().getIntent();
        View detailRootView =  inflater.inflate(R.layout.fragment_detail, container, false);


            movie = (MovieParse)detail_intent.getParcelableExtra(detail_intent.EXTRA_TEXT);
            DisplayMovieInfo(detailRootView);

        return  detailRootView;
    }



    public void DisplayMovieInfo(View detailRootView) {
        TextView title = (TextView) detailRootView.findViewById(R.id.detail_title_view);
        ImageView poster = (ImageView) detailRootView.findViewById(R.id.detail_image_view);
        TextView release = (TextView) detailRootView.findViewById(R.id.detail_release_date);
        TextView rating = (TextView) detailRootView.findViewById(R.id.detail_rating_view);
        TextView overview = (TextView) detailRootView.findViewById(R.id.detail_overview_view);

        title.setText(movie.getTitle());
        Picasso.with(getActivity()).load(movie.getPoster()).into(poster);
        release.setText(movie.getReleaseDate());
        rating.setText(movie.getVoteAverage());
        overview.setText(movie.getOverview());


    }

}
