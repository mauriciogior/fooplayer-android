package com.mauriciogiordano.fooplayer.fragment;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mauriciogiordano.fooplayer.R;
import com.mauriciogiordano.fooplayer.TracksActivity;
import com.mauriciogiordano.fooplayer.adapter.TracksAdapter;
import com.mauriciogiordano.fooplayer.config.Constants;
import com.mauriciogiordano.fooplayer.database.Album;
import com.mauriciogiordano.fooplayer.database.Track;
import com.mauriciogiordano.fooplayer.network.Delegate;
import com.mauriciogiordano.fooplayer.network.Endpoints;
import com.mauriciogiordano.fooplayer.network.HttpClientHelper;
import com.mauriciogiordano.fooplayer.network.Network;
import com.mauriciogiordano.fooplayer.network.YouTubeUtility;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.List;

/**
 * Created by mauricio on 10/31/14.
 */
public class TracksFragment extends Fragment {

    private View rootView;
    private TracksAdapter tracksAdapter;
    private ListView tracksList;
    private TracksActivity mainActivity;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mainActivity = (TracksActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_tracks,
                container, false);

        mainActivity.mAlbum.loadTracks(getActivity(), new Album.TrackSearchListener() {
            @Override
            public void results(List<Track> tracks, Network.Status status, boolean err) {

                if (!err) {
                    tracksAdapter = new TracksAdapter(getActivity(), tracks);
                    tracksList = (ListView) rootView.findViewById(R.id.listview);

                    tracksList.setAdapter(tracksAdapter);

                    tracksList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            TracksAdapter.ViewHolder holder = (TracksAdapter.ViewHolder) view.getTag();

                            String query = holder.track.getArtist(mainActivity).getName()
                                            + " " + holder.track.getName();

                            searchTrack(query);
                        }
                    });
                }

            }
        });

        return rootView;
    }

    private void searchTrack(String query)
    {
        HttpClientHelper client = new HttpClientHelper(Endpoints.Youtube.HOST, Endpoints.Youtube.PATH, mainActivity);

        client.setSSL();

        client.addParamForGet("part", "snippet");
        client.addParamForGet("q", query);
        client.addParamForGet("type", "video");
        client.addParamForGet("key", Constants.YOUTUBE_APIKEY_WEB);

        Network.newRequest(client, Network.GET, new Delegate() {
            @Override
            public void requestResults(Network.Status status) {

                if (status.hasInternet) {
                    if (status.response.getStatusLine().getStatusCode() == 200) {
                        String videoURL = null;

                        try
                        {
                            JSONArray videos = status.result
                                    .getJSONArray("items");

                            videoURL = videos.getJSONObject(0).getJSONObject("id").getString("videoId");
                        }
                        catch(JSONException e)
                        {
                            e.printStackTrace();
                        }
                        catch(NullPointerException e)
                        {
                            e.printStackTrace();
                        }

                        Log.d("VideoURL", videoURL);

                        if(videoURL != null)
                        {
                            (new GetRTSP(videoURL)).execute("");
                        //    Intent lVideoIntent = new Intent(null, Uri.parse("ytv:" + videoURL), mainActivity, OpenYouTubePlayerActivity.class);
                        //    startActivity(lVideoIntent);
                        //    playTrack(videoURL);z
                        //    mainActivity.mPlayer.loadVideo(videoURL);
                        }
                    }
                }

            }
        });
    }

    private class GetRTSP extends AsyncTask<String, String, String> {

        private String videoURL;

        public GetRTSP(String videoURL)
        {
            this.videoURL = videoURL;
        }

        @Override
        protected String doInBackground(String... pParams) {

            try {
                String url = YouTubeUtility.calculateYouTubeUrl("18", true, this.videoURL);

                Log.d("URLLL", url);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
    /*
    public void playTrack(String url)
    {

        HttpClientHelper client = new HttpClientHelper(Endpoints.YoutubeRTSP.HOST,
                                    Endpoints.YoutubeRTSP.PATH + url, mainActivity);

        client.addParamForGet("alt", "jsonc");
        client.addParamForGet("v", "2");

        Network.newRequest(client, Network.GET, new Delegate() {
            @Override
            public void requestResults(Network.Status status) {

                if (status.hasInternet) {
                    if (status.response.getStatusLine().getStatusCode() == 200) {
                        String videoURL = null;

                        try {
                            JSONObject video = status.result.getJSONObject("data");

                            videoURL = video.getJSONObject("content")
                                            .optString("1", "");

                            if(videoURL.equals(""))
                            {
                                videoURL = video.getJSONObject("content")
                                        .optString("6", "");
                            }

                            Log.d("JSON", video.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }

                        if (videoURL != null) {
                            Log.d("URL", videoURL);
                            Uri video = Uri.parse(videoURL);
                            mainActivity.videoView.setVideoURI(video);
                            mainActivity.videoView.start();
                        }
                    }
                }

            }
        });
    }*/
}
