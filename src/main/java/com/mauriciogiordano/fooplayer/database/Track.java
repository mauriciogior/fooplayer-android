package com.mauriciogiordano.fooplayer.database;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;

/**
 * Created by mauricio on 10/31/14.
 */
public class Track extends Bean
{
    @DatabaseField(generatedId = false, id = true)
    private String mbid;

    @DatabaseField
    private String artistMbid;
    @DatabaseField
    private String albumMbid;

    @DatabaseField
    private String name;
    @DatabaseField
    private int duration;
    @DatabaseField
    private String url;

    private Artist artist;

    public Track() { super(Track.class); }

    public Track(JSONObject object)
    {
        super(Track.class);

        mbid = object.optString("mbid", "");

        try
        {
            artistMbid = object.getJSONObject("artist").optString("mbid", "");
        } catch(JSONException e) {}

        name = object.optString("name", "");
        duration = object.optInt("duration", 0);
        url = object.optString("url", "");
    }

    public Artist getArtist(Context context)
    {
        Dao<Artist, Integer> dao = null;

        Artist artist = null;

        try {
            dao = DatabaseHelper.getInstance(context).getDao(Artist.class);

            artist = dao.queryForEq("mbid", artistMbid).get(0);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return artist;
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

    public String getAlbumMbid() {
        return albumMbid;
    }

    public void setAlbumMbid(String albumMbid) {
        this.albumMbid = albumMbid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
