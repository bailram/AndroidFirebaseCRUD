package com.bailram.androidfirebasecrud.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bailram.androidfirebasecrud.R;
import com.bailram.androidfirebasecrud.models.Track;

import java.util.List;

public class TrackList extends ArrayAdapter<Track> {
    private Activity context;
    private List<Track> tracks;

    public TrackList(Activity context, List<Track> tracks) {
        super(context, R.layout.layout_artist_list, tracks);
        this.context = context;
        this.tracks = tracks;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = context.getLayoutInflater();
        View listViewItem = layoutInflater.inflate(R.layout.layout_artist_list,null,true);

        TextView textViewName = listViewItem.findViewById(R.id.textViewName);
        TextView textViewRating = listViewItem.findViewById(R.id.textViewGenre);


         Track track = tracks.get(position);
         textViewName.setText(track.getTrackName());
         textViewRating.setText(String.valueOf(track.getRating()));

        return listViewItem;
    }
}
