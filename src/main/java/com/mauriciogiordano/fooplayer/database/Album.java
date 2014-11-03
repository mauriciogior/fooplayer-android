package com.mauriciogiordano.fooplayer.database;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.mauriciogiordano.fooplayer.config.Constants;
import com.mauriciogiordano.fooplayer.network.Delegate;
import com.mauriciogiordano.fooplayer.network.Endpoints;
import com.mauriciogiordano.fooplayer.network.HttpClientHelper;
import com.mauriciogiordano.fooplayer.network.Network;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mauricio on 10/31/14.
 */
public class Album extends Bean
{
    @DatabaseField(generatedId = false, id = true)
    private String mbid;

    @DatabaseField
    private String artistMbid;
    @DatabaseField
    private String name;
    @DatabaseField
    private String url;
    @DatabaseField
    private String image;

    public Album() { super(Album.class); }

    public Album(JSONObject object)
    {
        super(Album.class);

        mbid = object.optString("mbid", "");

        try
        {
            artistMbid = object.getJSONObject("artist").optString("mbid", "");
        } catch(JSONException e) {}

        name = object.optString("name", "");
        url = object.optString("url", "");

        try
        {
            JSONArray images = object.getJSONArray("image");

            if(images.length() >= 4)
            {
                image = images.getJSONObject(3).getString("#text");
            }
            else if(images.length() > 0)
            {
                image = images.getJSONObject(0).getString("#text");
            }
        } catch(JSONException e) {}
    }

    /**
     * Search for album's tracks.
     * @param context The application context.
     * @param trackSearchListener The callback.
     */
    public void loadTracks(final Context context, final TrackSearchListener trackSearchListener)
    {
        Dao<Track, Integer> dao = null;

        List<Track> tracks = null;

        try {
            dao = DatabaseHelper.getInstance(context).getDao(Track.class);

            tracks = dao.queryForEq("albumMbid", mbid);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        if(tracks == null || tracks.size() == 0) {
            HttpClientHelper client = new HttpClientHelper(Endpoints.LastFM.HOST, Endpoints.LastFM.PATH, context);

            client.addParamForGet("api_key", Constants.LASTFM_APIKEY);
            client.addParamForGet("method", "album.getinfo");
            client.addParamForGet("mbid", getMbid());
            client.addParamForGet("format", "json");

            Network.newRequest(client, Network.GET, new Delegate() {
                @Override
                public void requestResults(Network.Status status) {
                    List<Track> trackList = null;
                    boolean err = false;

                    if (status.hasInternet) {
                        if (status.response.getStatusLine().getStatusCode() == 200) {
                            try {
                                JSONArray tracks = status.result
                                        .getJSONObject("album")
                                        .getJSONObject("tracks")
                                        .getJSONArray("track");

                                trackList = new ArrayList<Track>();

                                for (int i = 0; i < tracks.length(); i++) {
                                    Track track = new Track(tracks.getJSONObject(i));

                                    track.setAlbumMbid(getMbid());
                                    track.save(context);

                                    trackList.add(track);
                                }
                            } catch (JSONException e) {
                                err = true;
                            }
                        } else {
                            err = true;
                        }
                    } else {
                        err = true;
                    }

                    trackSearchListener.results(trackList, status, err);
                }
            });
        }
        else
        {
            trackSearchListener.results(tracks, null, false);
        }
    }

    /**
     * Get an album.
     * @param mbid The album's id.
     * @param context The application context.
     * @param albumGetListener The callback.
     */
    public static void get(String mbid, final Context context, final AlbumGetListener albumGetListener)
    {
        Dao<Album, Integer> dao = null;

        Album album = null;

        try {
            dao = DatabaseHelper.getInstance(context).getDao(Album.class);

            album = dao.queryForEq("mbid", mbid).get(0);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        if(album == null)
        {
            HttpClientHelper client = new HttpClientHelper(Endpoints.LastFM.HOST, Endpoints.LastFM.PATH, context);

            client.addParamForGet("api_key", Constants.LASTFM_APIKEY);
            client.addParamForGet("method", "album.getinfo");
            client.addParamForGet("mbid", mbid);
            client.addParamForGet("format", "json");

            Network.newRequest(client, Network.GET, new Delegate() {
                @Override
                public void requestResults(Network.Status status) {
                    Album album = null;
                    boolean err = false;

                    if (status.hasInternet) {
                        if (status.response.getStatusLine().getStatusCode() == 200) {
                            try {
                                JSONObject albumJSON = status.result
                                        .getJSONObject("album");

                                album = new Album(albumJSON);
                            } catch (JSONException e) {
                                err = true;
                            }
                        } else {
                            err = true;
                        }
                    } else {
                        err = true;
                    }

                    albumGetListener.results(album, status, err);
                }
            });
        }
        else
        {
            albumGetListener.results(album, null, false);
        }
    }

    public void save(Context context) {
        this.save(context, this);
    }

    public String getMbid() {
        return mbid;
    }

    public void setMbid(String mbid) {
        this.mbid = mbid;
    }

    public String getArtistMbid() {
        return artistMbid;
    }

    public void setArtistMbid(String artistMbid) {
        this.artistMbid = artistMbid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public static abstract class AlbumGetListener
    {
        public abstract void results(Album album, Network.Status status, boolean err);
    }

    public static abstract class TrackSearchListener
    {
        public abstract void results(List<Track> tracks, Network.Status status, boolean err);
    }
}
