package ru.ilyatrofimov.mobilization.fragments;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.orm.SugarRecord;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import ru.ilyatrofimov.mobilization.MobilizationApp;
import ru.ilyatrofimov.mobilization.R;
import ru.ilyatrofimov.mobilization.activities.DetailActivity;
import ru.ilyatrofimov.mobilization.adapters.ArtistsAdapter;
import ru.ilyatrofimov.mobilization.model.Artist;
import ru.ilyatrofimov.mobilization.network.DataLoader;
import ru.ilyatrofimov.mobilization.network.JSONParser;
import ru.ilyatrofimov.mobilization.views.ItemDividerDecoration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * @author Ilya Trofimov
 */
public class MainActivityFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private ArtistsAdapter mArtistsAdapter;

    @Bind(R.id.swipe_refresh_container) SwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.recycler_placeholder) View mRecyclerPlaceholder;
    @Bind(R.id.recycler_artists) RecyclerView mArtistsRecycler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);

        init();

        return view;
    }

    private void init() {
        mArtistsAdapter = new ArtistsAdapter(getContext());

        mArtistsRecycler.setAdapter(mArtistsAdapter);
        mArtistsRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        mArtistsRecycler.addItemDecoration(new ItemDividerDecoration(getContext()));

        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.accent);

        if (SugarRecord.count(Artist.class) == 0) {
            onRefresh();
        } else {
            List<Artist> artists = SugarRecord.listAll(Artist.class);
            Collections.sort(artists, Collections.<Artist>reverseOrder());
            mArtistsAdapter.setArtistsList(artists);
        }

        mRecyclerPlaceholder.setVisibility(mArtistsAdapter.getItemCount() > 0
                ? View.GONE : View.VISIBLE);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Subscribe
    public void onItemClick(ArtistsAdapter.ItemClickedEvent clickedItem) {
        Activity parent = getActivity();

        if (parent != null) {
            Intent intent = new Intent(parent, DetailActivity.class);
            intent.putExtra(Artist.TAG
                    , mArtistsAdapter.getArtistsList().get(clickedItem.getPosition()));

            if (MobilizationApp.IS_LOLLIPOP_OR_HIGHER && getResources().getConfiguration()
                    .orientation == Configuration.ORIENTATION_PORTRAIT) {
                ImageView cover = (ImageView) clickedItem.getView()
                        .findViewById(R.id.img_artist_photo);

                Pair[] pairs = new Pair[]{
                        Pair.create(parent.findViewById(android.R.id.statusBarBackground)
                                , Window.STATUS_BAR_BACKGROUND_TRANSITION_NAME),
                        Pair.create(parent.findViewById(android.R.id.navigationBarBackground)
                                , Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME),
                        Pair.create(cover, cover.getTransitionName())};

                Bundle bundle = ActivityOptionsCompat
                        .makeSceneTransitionAnimation(parent, pairs).toBundle();
                ActivityCompat.startActivity(parent, intent, bundle);
            } else {
                startActivity(intent);
            }
        }
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        DataLoader.requestJSON();
    }

    @Subscribe
    public void onSuccessfulResponse(JSONArray response) {
        mSwipeRefreshLayout.setRefreshing(false);
        ArrayList<Artist> artists;

        try {
            artists = JSONParser.parseArtistsJSON(response);
        } catch (JSONException e) {
            Toast.makeText(getContext(), getString(R.string.status_server_not_responding)
                    , Toast.LENGTH_LONG).show();
            return;
        }

        Collections.sort(artists, Collections.<Artist>reverseOrder());
        mArtistsAdapter.setArtistsList(artists);
        mRecyclerPlaceholder.setVisibility(mArtistsAdapter.getItemCount() > 0
                ? View.GONE : View.VISIBLE);

        Toast.makeText(getContext(), R.string.status_successful_response, Toast.LENGTH_SHORT)
                .show();
    }

    @Subscribe
    public void onErrorResponse(VolleyError error) {
        mSwipeRefreshLayout.setRefreshing(false);

        String errorMsg = error instanceof ServerError
                ? getString(R.string.status_server_not_responding)
                : getString(R.string.status_no_connection);

        Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
}