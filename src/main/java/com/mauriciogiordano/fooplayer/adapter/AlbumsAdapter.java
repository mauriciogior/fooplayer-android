package com.mauriciogiordano.fooplayer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mauriciogiordano.fooplayer.R;
import com.mauriciogiordano.fooplayer.database.Album;
import com.mauriciogiordano.fooplayer.network.ImageHelper;

import java.util.List;

/**
 * Created by mauricio on 10/31/14.
 */
public class AlbumsAdapter extends BaseAdapter {

    private Context context;
    private List<Album> albums;
    private LayoutInflater inflater;

    public AlbumsAdapter(Context context, List<Album> albums) {
        this.context = context;
        this.albums = albums;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public static class ViewHolder
    {
        public TextView albumName;
        public ImageView cover;
        public Album album;
    }

    @Override
    public int getCount() {
        return albums.size();
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

            holder.albumName = (TextView) convertView.findViewById(R.id.artist_name);
            holder.cover = (ImageView) convertView.findViewById(R.id.cover);

            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.album = albums.get(i);

        ImageHelper.loadImage(holder.album.getImage(), holder.cover, context);
        holder.albumName.setText(holder.album.getName());

        return convertView;
    }
}
