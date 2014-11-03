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
import com.mauriciogiordano.fooplayer.network.ImageHelper;

import java.util.List;

/**
 * Created by mauricio on 10/31/14.
 */
public class ArtistsAdapter extends BaseAdapter {

    private Context context;
    private List<Artist> artists;
    private LayoutInflater inflater;

    public ArtistsAdapter(Context context, List<Artist> artists) {
        this.context = context;
        this.artists = artists;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public static class ViewHolder
    {
        public TextView artistName;
        public ImageView cover;
        public Artist artist;
    }

    @Override
    public int getCount() {
        return artists.size();
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
            convertView = inflater.inflate(R.layout.adapter_artists, null);
            holder = new ViewHolder();

            holder.artistName = (TextView) convertView.findViewById(R.id.artist_name);
            holder.cover = (ImageView) convertView.findViewById(R.id.cover);

            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.artist = artists.get(i);

        ImageHelper.loadImage(holder.artist.getImage(), holder.cover, context);
        holder.artistName.setText(holder.artist.getName());

        return convertView;
    }
}
