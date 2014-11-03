package com.mauriciogiordano.fooplayer;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.MediaController;
import android.widget.VideoView;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubePlayer;
import com.mauriciogiordano.fooplayer.database.Album;
import com.mauriciogiordano.fooplayer.fragment.TracksFragment;
import com.mauriciogiordano.fooplayer.network.Network;

public class TracksActivity extends YouTubeBaseActivity {

    public Album mAlbum = null;

    public YouTubePlayer mPlayer = null;

    public VideoView videoView = null;
    public MediaController mediaController = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracks);

        //videoView = (VideoView) findViewById(R.id.video_view);

        //mediaController = new MediaController(this);
        //mediaController.setAnchorView(videoView);
        //mediaController.setMediaPlayer(videoView);

        //videoView.setMediaController(mediaController);

        /*YouTubePlayerView youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
        youTubeView.initialize(Constants.YOUTUBE_APIKEY_ANDROID, new YouTubePlayer.OnInitializedListener()
        {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                Toast.makeText(getApplicationContext(), "SUCCESS", Toast.LENGTH_LONG).show();

                TracksActivity.this.mPlayer = youTubePlayer;
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                Toast.makeText(getApplicationContext(), "FAIL", Toast.LENGTH_LONG).show();

                youTubeInitializationResult.getErrorDialog(TracksActivity.this, 0).show();
            }
        });*/

        if (savedInstanceState == null) {

            Intent intent = getIntent();

            Bundle extras = intent.getExtras();

            String mbid = extras.getString("album-mbid");

            Album.get(mbid, this, new Album.AlbumGetListener() {
                @Override
                public void results(Album album, Network.Status status, boolean err) {

                    mAlbum = album;

                    getFragmentManager().beginTransaction()
                            .add(R.id.container, new TracksFragment()).commit();
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
