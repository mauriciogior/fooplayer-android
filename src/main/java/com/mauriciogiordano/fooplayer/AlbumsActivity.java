package com.mauriciogiordano.fooplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.mauriciogiordano.fooplayer.database.Artist;
import com.mauriciogiordano.fooplayer.fragment.AlbumsFragment;
import com.mauriciogiordano.fooplayer.network.Network;

public class AlbumsActivity extends Activity {

    public Artist mArtist = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foo);

        if (savedInstanceState == null) {

            Intent intent = getIntent();

            Bundle extras = intent.getExtras();

            String mbid = extras.getString("artist-mbid");

            Log.d("MBID", mbid);

            Artist.get(mbid, this, new Artist.ArtistGetListener() {
                @Override
                public void results(Artist artist, Network.Status status, boolean err) {

                    mArtist = artist;

                    Log.d("ARTIST IS NULL", String.valueOf(artist == null));

                    getFragmentManager().beginTransaction()
                            .add(R.id.container, new AlbumsFragment()).commit();
                }
            });
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.foo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
