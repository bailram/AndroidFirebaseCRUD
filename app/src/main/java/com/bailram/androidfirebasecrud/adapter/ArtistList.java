package com.bailram.androidfirebasecrud.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bailram.androidfirebasecrud.R;
import com.bailram.androidfirebasecrud.models.Artist;

import java.util.List;

public class ArtistList extends ArrayAdapter<Artist> {
    private Activity context;
    private List<Artist> artists;

    public ArtistList(Activity context, List<Artist> artists){
        super(context, R.layout.layout_artist_list, artists);
        this.context = context;
        this.artists = artists;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = context.getLayoutInflater();
        View listViewItem = layoutInflater.inflate(R.layout.layout_artist_list, null, true);

        TextView textViewName = listViewItem.findViewById(R.id.textViewName);
        TextView textViewGenre = listViewItem.findViewById(R.id.textViewGenre);

        Artist artist = artists.get(position);
        textViewName.setText(artist.getArtistName());
        textViewGenre.setText(artist.getArtistGenre());

        return listViewItem;
    }
}
