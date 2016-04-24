package ru.ilyatrofimov.mobilization.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.orm.dsl.Ignore;
import com.orm.dsl.Table;
import com.orm.dsl.Unique;


/**
 * @author Ilya Trofimov
 */
@Table
public class Artist implements Comparable<Artist>, Parcelable {

    @Unique
    private Long id;
    private String name;
    private String genres;
    private int tracks;
    private int albums;
    private String link;
    private String description;
    private String coverSmall;
    private String coverBig;

    public Artist() {

    }

    public Artist(Long id) {
        this.id = id;
    }

    public Artist(Long id, String name, String genres, int tracks, int albums, String link
            , String description, String coverSmall, String coverBig) {
        this.id = id;
        this.name = name;
        this.genres = genres;
        this.tracks = tracks;
        this.albums = albums;
        this.link = link;
        this.description = description;
        this.coverSmall = coverSmall;
        this.coverBig = coverBig;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getGenres() {
        return genres;
    }

    public int getTracks() {
        return tracks;
    }

    public int getAlbums() {
        return albums;
    }

    public String getLink() {
        return link;
    }

    public String getDescription() {
        return description;
    }

    public String getCoverSmall() {
        return coverSmall;
    }

    public String getCoverBig() {
        return coverBig;
    }

    @Override
    public String toString() {
        if (this.name != null && !this.name.isEmpty()) {
            return this.name;
        }

        return String.valueOf(this.id);
    }

    @Override
    public int compareTo(Artist other) {
        if (this == other) {
            return 0;
        }

        return this.id.compareTo(other.getId());
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (this.id == null || other == null || !(other instanceof Artist)) {
            return false;
        }

        return this.id.equals(((Artist) other).id);
    }

    protected Artist(Parcel in) {
        id = in.readLong();
        name = in.readString();
        genres = in.readString();
        tracks = in.readInt();
        albums = in.readInt();
        link = in.readString();
        description = in.readString();
        coverSmall = in.readString();
        coverBig = in.readString();
    }

    public static final Creator<Artist> CREATOR = new Creator<Artist>() {
        @Override
        public Artist createFromParcel(Parcel in) {
            return new Artist(in);
        }

        @Override
        public Artist[] newArray(int size) {
            return new Artist[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(genres);
        dest.writeInt(tracks);
        dest.writeInt(albums);
        dest.writeString(link);
        dest.writeString(description);
        dest.writeString(coverSmall);
        dest.writeString(coverBig);
    }

    public static final class ArtistBuilder {
        private Long id;
        private String name;
        private String genres;
        private int tracks;
        private int albums;
        private String link;
        private String description;
        private String coverSmall;
        private String coverBig;

        public void setId(Long id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setGenres(String genres) {
            this.genres = genres;
        }

        public void setTracks(int tracks) {
            this.tracks = tracks;
        }

        public void setAlbums(int albums) {
            this.albums = albums;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void setCoverSmall(String coverSmall) {
            this.coverSmall = coverSmall;
        }

        public void setCoverBig(String coverBig) {
            this.coverBig = coverBig;
        }

        public Artist build() {
            return new Artist(this.id, this.name, this.genres, this.tracks, this.albums, this.link
                    , this.description, this.coverSmall, this.coverBig);
        }
    }

    @Ignore
    public static final String TAG = "ru.ilyatrofimov.mobilization.model.Artist";
}
