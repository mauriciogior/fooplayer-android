package com.mauriciogiordano.fooplayer.database;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.stmt.PreparedQuery;
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
public class Artist extends Bean
{
    @DatabaseField(generatedId = false, id = true)
    private String mbid;

    @DatabaseField
    private String name;
    @DatabaseField
    private String url;
    @DatabaseField
    private String image;

    private List<Album> albums = null;

    public Artist() { super(Artist.class); }

    public Artist(JSONObject object)
    {
        super(Artist.class);

        mbid = object.optString("mbid", "");
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
     * Search for artist's albums.
     * @param context The application context.
     * @param albumSearchListener The callback.
     */
    public void loadAlbums(final Context context, final AlbumSearchListener albumSearchListener)
    {
        Dao<Album, Integer> dao = null;

        List<Album> albums = null;

        try {
            dao = DatabaseHelper.getInstance(context).getDao(Album.class);

            albums = dao.queryForEq("artistMbid", mbid);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        if(albums == null || albums.size() == 0) {
            HttpClientHelper client = new HttpClientHelper(Endpoints.LastFM.HOST, Endpoints.LastFM.PATH, context);

            client.addParamForGet("api_key", Constants.LASTFM_APIKEY);
            client.addParamForGet("method", "artist.gettopalbums");
            client.addParamForGet("mbid", getMbid());
            client.addParamForGet("format", "json");

            Network.newRequest(client, Network.GET, new Delegate() {
                @Override
                public void requestResults(Network.Status status) {
                    List<Album> albumList = null;
                    boolean err = false;

                    if (status.hasInternet) {
                        if (status.response.getStatusLine().getStatusCode() == 200) {
                            try {
                                JSONArray albums = status.result
                                        .getJSONObject("topalbums")
                                        .getJSONArray("album");

                                albumList = new ArrayList<Album>();

                                for (int i = 0; i < albums.length(); i++) {
                                    Album album = new Album(albums.getJSONObject(i));

                                    album.save(context);

                                    albumList.add(album);
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

                    albumSearchListener.results(albumList, status, err);
                }
            });
        }
        else
        {
            albumSearchListener.results(albums, null, false);
        }
    }

    /**
     * Search for artist.
     * @param input The search query.
     * @param context The application context.
     * @param artistSearchListener The callback.
     */
    public static void search(String input, final Context context, final ArtistSearchListener artistSearchListener)
    {
        HttpClientHelper client = new HttpClientHelper(Endpoints.LastFM.HOST, Endpoints.LastFM.PATH, context);

        client.addParamForGet("api_key", Constants.LASTFM_APIKEY);
        client.addParamForGet("method", "artist.search");
        client.addParamForGet("artist", input);
        client.addParamForGet("format", "json");

        Network.newRequest(client, Network.GET, new Delegate() {
            @Override
            public void requestResults(Network.Status status) {
                List<Artist> artistList = null;
                boolean err = false;

                if (status.hasInternet) {
                    if (status.response.getStatusLine().getStatusCode() == 200) {
                        try {
                            JSONArray artists = status.result
                                    .getJSONObject("results")
                                    .getJSONObject("artistmatches")
                                    .getJSONArray("artist");

                            artistList = new ArrayList<Artist>();

                            for (int i = 0; i < artists.length(); i++) {
                                Artist artist = new Artist(artists.getJSONObject(i));

                                artist.save(context);

                                artistList.add(artist);
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

                artistSearchListener.results(artistList, status, err);
            }
        });
    }

    /**
     * Search for artist.
     * @param input The search query.
     * @param context The application context.
     */
    public static List<Artist> search(String input, final Context context)
    {
        Dao<Artist, Integer> dao = null;

        List<Artist> artists = null;

        try {
            dao = DatabaseHelper.getInstance(context).getDao(Artist.class);

            PreparedQuery<Artist> query = dao.queryBuilder().where().like("name", "%" + input + "%").prepare();

            artists = dao.query(query);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return artists;
    }

    /**
     * Get an artist.
     * @param mbid The artist's id.
     * @param context The application context.
     * @param artistGetListener The callback.
     */
    public static void get(String mbid, final Context context, final ArtistGetListener artistGetListener)
    {
        Dao<Artist, Integer> dao = null;

        Artist artist = null;

        try {
            dao = DatabaseHelper.getInstance(context).getDao(Artist.class);

            artist = dao.queryForEq("mbid", mbid).get(0);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        if(artist == null)
        {
            HttpClientHelper client = new HttpClientHelper(Endpoints.LastFM.HOST, Endpoints.LastFM.PATH, context);

            client.addParamForGet("api_key", Constants.LASTFM_APIKEY);
            client.addParamForGet("method", "artist.getinfo");
            client.addParamForGet("mbid", mbid);
            client.addParamForGet("format", "json");

            Network.newRequest(client, Network.GET, new Delegate() {
                @Override
                public void requestResults(Network.Status status) {
                    Artist artist = null;
                    boolean err = false;

                    if (status.hasInternet) {
                        if (status.response.getStatusLine().getStatusCode() == 200) {
                            try {
                                JSONObject artistJSON = status.result
                                        .getJSONObject("artist");

                                artist = new Artist(artistJSON);
                            } catch (JSONException e) {
                                err = true;
                            }
                        } else {
                            err = true;
                        }
                    } else {
                        err = true;
                    }

                    artistGetListener.results(artist, status, err);
                }
            });
        }
        else
        {
            artistGetListener.results(artist, null, false);
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

    public static abstract class ArtistGetListener
    {
        public abstract void results(Artist artist, Network.Status status, boolean err);
    }

    public static abstract class ArtistSearchListener
    {
        public abstract void results(List<Artist> artists, Network.Status status, boolean err);
    }

    public static abstract class AlbumSearchListener
    {
        public abstract void results(List<Album> albums, Network.Status status, boolean err);
    }
}
