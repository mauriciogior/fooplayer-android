package com.mauriciogiordano.fooplayer.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.mauriciogiordano.fooplayer.AlbumsActivity;
import com.mauriciogiordano.fooplayer.R;
import com.mauriciogiordano.fooplayer.adapter.ArtistsAdapter;
import com.mauriciogiordano.fooplayer.database.Artist;
import com.mauriciogiordano.fooplayer.network.Network;

import java.util.List;

/**
 * Created by mauricio on 10/31/14.
 */
public class ArtistsFragment extends Fragment {

    private View rootView;
    private ArtistsAdapter artistsAdapter;
    private GridView artistsGrid;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_artists,
                container, false);

        Artist.search("foo", getActivity(), new Artist.ArtistSearchListener() {
            @Override
            public void results(List<Artist> artists, Network.Status status, boolean err) {

                if (!err) {
                    artistsAdapter = new ArtistsAdapter(getActivity(), artists);
                    artistsGrid = (GridView) rootView.findViewById(R.id.gridview);

                    artistsGrid.setAdapter(artistsAdapter);

                    artistsGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            ArtistsAdapter.ViewHolder holder = (ArtistsAdapter.ViewHolder) view.getTag();

                            Intent intent = new Intent(getActivity(), AlbumsActivity.class);
                            intent.putExtra("artist-mbid", holder.artist.getMbid());
                            startActivity(intent);
                        }
                    });
                }

            }
        });

        return rootView;
    }
}
