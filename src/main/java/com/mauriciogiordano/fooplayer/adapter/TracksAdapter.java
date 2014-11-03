package com.mauriciogiordano.fooplayer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mauriciogiordano.fooplayer.R;
import com.mauriciogiordano.fooplayer.database.Artist;
import com.mauriciogiordano.fooplayer.database.Track;

import java.util.List;

/**
 * Created by mauricio on 10/31/14.
 */
public class TracksAdapter extends BaseAdapter {

    private Context context;
    private List<Track> tracks;
    private LayoutInflater inflater;

    public TracksAdapter(Context context, List<Track> tracks) {
        this.context = context;
        this.tracks = tracks;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public static class ViewHolder
    {
        public TextView trackName;
        public TextView artistName;
        public ImageView cover;
        public Track track;
    }

    @Override
    public int getCount() {
        return tracks.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {

        ViewHolder holder;

        if(convertView == null)
        {
            convertView = inflater.inflate(R.layout.adapter_tracks, null);
            holder = new ViewHolder();

            holder.trackName = (TextView) convertView.findViewById(R.id.track_name);
            holder.artistName = (TextView) convertView.findViewById(R.id.artist_name);

            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.track = tracks.get(i);

        holder.trackName.setText(holder.track.getName());

        Artist artist = holder.track.getArtist(context);

        if(artist != null)
            holder.artistName.setText(artist.getName());

        return convertView;
    }
}
