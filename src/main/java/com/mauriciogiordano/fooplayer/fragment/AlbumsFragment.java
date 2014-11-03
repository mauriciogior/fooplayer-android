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
import com.mauriciogiordano.fooplayer.TracksActivity;
import com.mauriciogiordano.fooplayer.adapter.AlbumsAdapter;
import com.mauriciogiordano.fooplayer.database.Album;
import com.mauriciogiordano.fooplayer.database.Artist;
import com.mauriciogiordano.fooplayer.network.Network;

import java.util.List;

/**
 * Created by mauricio on 10/31/14.
 */
public class AlbumsFragment extends Fragment {

    private View rootView;
    private AlbumsAdapter albumsAdapter;
    private GridView albumsGrid;
    private AlbumsActivity mainActivity;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mainActivity = (AlbumsActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_artists,
                container, false);

        mainActivity.mArtist.loadAlbums(getActivity(), new Artist.AlbumSearchListener() {
            @Override
            public void results(List<Album> albums, Network.Status status, boolean err) {

                if (!err) {
                    albumsAdapter = new AlbumsAdapter(getActivity(), albums);
                    albumsGrid = (GridView) rootView.findViewById(R.id.gridview);

                    albumsGrid.setAdapter(albumsAdapter);

                    albumsGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            AlbumsAdapter.ViewHolder holder = (AlbumsAdapter.ViewHolder) view.getTag();

                            Intent intent = new Intent(getActivity(), TracksActivity.class);
                            intent.putExtra("album-mbid", holder.album.getMbid());
                            startActivity(intent);
                        }
                    });
                }

            }
        });

        return rootView;
    }
}
