package ru.ilyatrofimov.mobilization.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import ru.ilyatrofimov.mobilization.R;
import ru.ilyatrofimov.mobilization.model.Artist;


/**
 * Detail Activity Fragment holds views that displays artist info
 *
 * @author Ilya Trofimov
 */
public class DetailActivityFragment extends Fragment {
    private Artist mArtist;

    @Bind(R.id.text_artist_genres) TextView mGenres;
    @Bind(R.id.text_artist_discography) TextView mDiscography;
    @Bind(R.id.text_artist_biography) TextView mBiographyContent;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Activity parent = getActivity();

        if (parent != null && parent.getIntent() != null
                && parent.getIntent().getExtras() != null) {
            mArtist = parent.getIntent().getParcelableExtra(Artist.TAG);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, view);

        if (mArtist != null) {
            init();
        }

        return view;
    }

    private void init() {
        // Sets values to views and nothing else

        String genres = mArtist.getGenres();
        String biography = mArtist.getDescription();
        int albums = mArtist.getAlbums();
        int tracks = mArtist.getTracks();

        if (genres != null && !genres.isEmpty()) {
            mGenres.setText(genres);
            mGenres.setVisibility(View.VISIBLE);
        }

        String discography = "";

        if (albums > 0) {
            discography = getContext().getResources()
                    .getQuantityString(R.plurals.plural_albums, albums, albums);
        }

        if (tracks > 0) {
            if (albums > 0) {
                char interpunct = '\u2219'; // dot-separator symbol
                String space = "   ";
                discography += space + interpunct + space; // append separator
            }

            discography += getContext().getResources()
                    .getQuantityString(R.plurals.plural_tracks, tracks, tracks);
        }

        if (!discography.isEmpty()) {
            mDiscography.setText(discography);
            mDiscography.setVisibility(View.VISIBLE);
        }

        if (biography != null && biography.length() > 1) {
            mBiographyContent.setText(biography);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}