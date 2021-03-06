package ru.ilyatrofimov.mobilization.network;

import com.orm.SugarRecord;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ru.ilyatrofimov.mobilization.model.Artist;

import java.util.ArrayList;


/**
 * Util class to parse JSON into POJO
 *
 * @author Ilya Trofimov
 */
public final class JSONParser {
    /**
     * Gets JSON Array and tries to parse it into ArrayList of Artist object
     *
     * @param jsonArray JSON that will be parsed
     * @return list of Artists parsed from JSON
     * @throws JSONException if list is empty and there was an error or JSON is not valid
     */
    public static ArrayList<Artist> parseArtistsJSON(JSONArray jsonArray) throws JSONException {
        final ArrayList<Artist> artists = new ArrayList<>();
        boolean wasError = false;

        if (isJSONArrayValid(jsonArray)) {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject artistJSON = jsonArray.getJSONObject(i);

                    // We don't consider artists w/o id in json
                    if (!isJSONContains(artistJSON, JSON_KEY_ID)) {
                        continue;
                    }

                    artists.add(getArtistPOJO(artistJSON));
                } catch (JSONException e) {
                    wasError = true;
                }
            }
        }

        // Throw exception if no artists was parsed because of error
        if (!isJSONArrayValid(jsonArray) || wasError && artists.isEmpty()) {
            throw new JSONException("Can not parse JSON");
        }

        // Save artists
        new Thread(new Runnable() {
            @Override
            public void run() {
                SugarRecord.saveInTx(artists);
            }
        }).start();

        return artists;
    }

    /**
     * Gets JSON Object and tries to parse it into Artist object
     *
     * @param artistJSON JSON that will be parsed
     * @return Artist object parsed from JSON
     * @throws JSONException if JSON can not be parsed
     */
    private static Artist getArtistPOJO(JSONObject artistJSON) throws JSONException {
        Artist.ArtistBuilder artistBuilder = new Artist.ArtistBuilder();
        artistBuilder.setId(artistJSON.getLong(JSON_KEY_ID));

        // Name parsing
        if (isJSONContains(artistJSON, JSON_KEY_NAME)) {
            artistBuilder.setName(artistJSON.getString(JSON_KEY_NAME));
        }

        // Genres array parsing
        if (isJSONContains(artistJSON, JSON_KEY_GENRES)) {
            JSONArray genresJSONArray = artistJSON.getJSONArray(JSON_KEY_GENRES);
            if (isJSONArrayValid(genresJSONArray)) {
                artistBuilder.setGenres(genresJSONArray.join(", ").replace("\"", ""));
            }
        }

        // Tracks parsing
        if (isJSONContains(artistJSON, JSON_KEY_TRACKS)) {
            artistBuilder.setTracks(artistJSON.getInt(JSON_KEY_TRACKS));
        }

        // Albums parsing
        if (isJSONContains(artistJSON, JSON_KEY_ALBUMS)) {
            artistBuilder.setAlbums(artistJSON.getInt(JSON_KEY_ALBUMS));
        }

        // Link parsing
        if (isJSONContains(artistJSON, JSON_KEY_LINK)) {
            artistBuilder.setLink(artistJSON.getString(JSON_KEY_LINK));
        }

        // Description parsing
        if (isJSONContains(artistJSON, JSON_KEY_DESCRIPTION)) {
            String description = artistJSON.getString(JSON_KEY_DESCRIPTION);
            description = description.substring(0, 1).toUpperCase() + description.substring(1);
            artistBuilder.setDescription(description);
        }

        // Covers parsing
        if (isJSONContains(artistJSON, JSON_KEY_COVER)) {
            JSONObject covers = artistJSON.getJSONObject(JSON_KEY_COVER);

            // Small cover
            if (isJSONContains(covers, JSON_KEY_COVER_SMALL)) {
                artistBuilder.setCoverSmall(covers.getString(JSON_KEY_COVER_SMALL));
            }

            // Big cover
            if (isJSONContains(covers, JSON_KEY_COVER_BIG)) {
                artistBuilder.setCoverBig(covers.getString(JSON_KEY_COVER_BIG));
            }
        }

        return artistBuilder.build();
    }

    /**
     * Validates is JSON Object valid and is it contains required key
     *
     * @param jsonObject JSON that will be validated
     * @param key that will be checked for the presence of
     * @return true if JSON is valid and it contains required key, false otherwise
     */
    private static boolean isJSONContains(JSONObject jsonObject, String key) {
        return (jsonObject != null && jsonObject.has(key) && !jsonObject.isNull(key));
    }

    /**
     * Validates is JSON Array valid
     *
     * @param jsonArray JSON Array that will be validated
     * @return true if JSON is valid, false otherwise
     */
    private static boolean isJSONArrayValid(JSONArray jsonArray) {
        return (jsonArray != null && jsonArray.length() > 0);
    }

    // JSON Keys
    private static final String JSON_KEY_ID = "id";
    private static final String JSON_KEY_NAME = "name";
    private static final String JSON_KEY_GENRES = "genres";
    private static final String JSON_KEY_TRACKS = "tracks";
    private static final String JSON_KEY_ALBUMS = "albums";
    private static final String JSON_KEY_LINK = "link";
    private static final String JSON_KEY_DESCRIPTION = "description";
    private static final String JSON_KEY_COVER = "cover";
    private static final String JSON_KEY_COVER_SMALL = "small";
    private static final String JSON_KEY_COVER_BIG = "big";

    private JSONParser() {
        // To prevent creating instances
    }
}
