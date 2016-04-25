package ru.ilyatrofimov.mobilization.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.squareup.picasso.Picasso;
import org.greenrobot.eventbus.EventBus;
import ru.ilyatrofimov.mobilization.R;
import ru.ilyatrofimov.mobilization.model.Artist;

import java.util.ArrayList;
import java.util.List;


/**
 * Artists Adapter holds RecyclerView.ViewHolder that represents an Artist
 *
 * @author Ilya Trofimov
 */
public class ArtistsAdapter extends RecyclerView.Adapter<ArtistsAdapter.ViewHolderArtistsList> {
    private List<Artist> mArtistsList;
    private LayoutInflater mInflater;
    private Context mContext;

    public ArtistsAdapter(Context context) {
        mArtistsList = new ArrayList<>();
        mInflater = LayoutInflater.from(context);
        mContext = context;
    }

    @Override
    public ViewHolderArtistsList onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.list_item_artist, parent, false);
        final ViewHolderArtistsList holder = new ViewHolderArtistsList(view);

        // Post event when an item was clicked. Event holds the view and it's position in adapter
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new ItemClickedEvent(v, holder.getAdapterPosition()));
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolderArtistsList holder, int position) {
        // Sets values to views and nothing else

        Artist artist = mArtistsList.get(position);
        String coverSmallURL = artist.getCoverSmall();
        String name = artist.getName();
        String genres = artist.getGenres();
        int albums = artist.getAlbums();
        int tracks = artist.getTracks();

        if (coverSmallURL != null && !coverSmallURL.isEmpty()) {
            Picasso.with(mContext).load(coverSmallURL)
                    .placeholder(R.drawable.default_cover_artist).into(holder.cover);
        }

        if (name != null && !name.isEmpty()) {
            holder.name.setText(name);
        }

        if (genres != null && !genres.isEmpty()) {
            holder.genres.setText(genres);
            holder.genres.setVisibility(View.VISIBLE);
        }

        String discography = "";

        if (albums > 0) {
            discography = mContext.getResources()
                    .getQuantityString(R.plurals.plural_albums, albums, albums);
        }

        if (tracks > 0) {
            if (albums > 0) {
                discography += ", ";
            }

            discography += mContext.getResources()
                    .getQuantityString(R.plurals.plural_tracks, tracks, tracks);
        }

        if (!discography.isEmpty()) {
            holder.discography.setText(discography);
            holder.discography.setVisibility(View.VISIBLE);
        }
    }

    public void setArtistsList(List<Artist> artistsList) {
        this.mArtistsList = artistsList;
        notifyDataSetChanged();
    }

    public List<Artist> getArtistsList() {
        return mArtistsList;
    }

    @Override
    public int getItemCount() {
        return mArtistsList.size();
    }

    static class ViewHolderArtistsList extends RecyclerView.ViewHolder {
        @Bind(R.id.img_artist_photo) ImageView cover;
        @Bind(R.id.text_artist_name) TextView name;
        @Bind(R.id.text_artist_genres) TextView genres;
        @Bind(R.id.text_artist_discography) TextView discography;

        public ViewHolderArtistsList(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    /**
     * Item clicked event for EventBus
     */
    public static class ItemClickedEvent {
        private int position;
        private View view;

        public ItemClickedEvent(View view, int position) {
            this.position = position;
            this.view = view;
        }

        /**
         * @return position of clicked item in adapter
         */
        public int getPosition() {
            return position;
        }

        /**
         * @return clicked view
         */
        public View getView() {
            return view;
        }
    }
}
