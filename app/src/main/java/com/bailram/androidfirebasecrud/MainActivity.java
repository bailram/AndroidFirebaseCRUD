package com.bailram.androidfirebasecrud;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bailram.androidfirebasecrud.adapter.ArtistList;
import com.bailram.androidfirebasecrud.models.Artist;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    // use to pass artist name and id to another activity
    public static final String ARTIST_NAME = "com.bailram.androidfirebasecrud.artistname";
    public static final String ARTIST_ID = "com.bailram.androidfirebasecrud.artistid";

    // view object
    private EditText editTextName;
    private Spinner spinnerGenre;
    private Button buttonAddArtist;
    private ListView listViewArtist;

    // array list to store all the artist from firebase realtime database
    List<Artist> artists;

    // database reference object
    private DatabaseReference databaseArtists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // getting the reference of artists node
        databaseArtists = FirebaseDatabase.getInstance().getReference("artists");

        // getting views
        editTextName = findViewById(R.id.editTextName);
        spinnerGenre = findViewById(R.id.spinnerGenres);
        listViewArtist = findViewById(R.id.listViewArtists);
        buttonAddArtist = findViewById(R.id.buttonAddArtist);

        // init list to store artists
        artists = new ArrayList<>();

        // add onclicklistener to button
        buttonAddArtist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // calling the method addArtist() to write operation
                addArtist();
            }
        });

        // add onclicklistener to item listview to handle when item clicked will do intent
        listViewArtist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // getting the selected artist
                Artist artist = artists.get(i);

                // creating an intent
                Intent intent = new Intent(getApplicationContext(), ArtistActivity.class);
                // putting artist name and id to intent
                intent.putExtra(ARTIST_ID, artist.getArtistId());
                intent.putExtra(ARTIST_NAME, artist.getArtistName());

                // starting the activity with intent
                startActivity(intent);
            }
        });

        /*
         * add onlongclicklistener to item listview to handle when item click and hold just for while
         * */
        listViewArtist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Artist artist = artists.get(i);
                showUpdateDeleteDialog(artist.getArtistId(), artist.getArtistName());
                return true;
            }
        });
    }

    /*
     * This method is to show dialog update or delete dialog
     * */
    private void showUpdateDeleteDialog(final String artistId, String artistName) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = getLayoutInflater();
        final View dialogView = layoutInflater.inflate(R.layout.update_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText editTextName = dialogView.findViewById(R.id.editTextName);
        final Spinner spinnerGenre = dialogView.findViewById(R.id.spinnerGenres);
        final Button buttonUpdate = dialogView.findViewById(R.id.buttonUpdateArtist);
        final Button buttonDelete = dialogView.findViewById(R.id.buttonDeleteArtist);

        dialogBuilder.setTitle(artistName);
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        // add onclicklistener to button update on dialog
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editTextName.getText().toString().trim();
                String genre = spinnerGenre.getSelectedItem().toString();
                if(!TextUtils.isEmpty(name)) {
                    updateArtist(artistId, name, genre);
                    alertDialog.dismiss();
                }
            }
        });

        // add onclicklistener to button delete on dialog
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteArtist(artistId);
                alertDialog.dismiss();
            }
        });
    }

    /*
    * This method is saving a new artist to the
    * Firebase Realtime Database
    * */
    private void addArtist() {
        // getting the values to save
        String name = editTextName.getText().toString().trim();
        String genre = spinnerGenre.getSelectedItem().toString();

        // checking if the value is provided
        if (!TextUtils.isEmpty(name)) {
            // getting a unique id using push().getKey() method
            // it will create a unique id and we will use it as the Primary Key for our Artist
            String id = databaseArtists.push().getKey();

            // creating an Artist Object
            Artist artist = new Artist(id,name,genre);

            // saving the Artist
            databaseArtists.child(id).setValue(artist);

            // setting edittext to blank again
            editTextName.setText("");

            // displaying a success toast
            Toast.makeText(this, "Artist added", Toast.LENGTH_SHORT).show();
        } else {
            // if the value from edittextname is not given displaying a toast
            Toast.makeText(this, "Please enter a name", Toast.LENGTH_SHORT).show();
        }
    }

    /*
     * This method is updating a artist data to the
     * Firebase Realtime Database
     * */
    private boolean updateArtist(String id, String name, String genre){
        // getting the specified artist reference
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("artists").child(id);

        // updating artist
        Artist artist = new Artist(id, name, genre);
        databaseReference.setValue(artist);
        Toast.makeText(getApplicationContext(), "Artist Updated", Toast.LENGTH_SHORT).show();
        return true;
    }

    /*
     * This method is deleting a artist data to the
     * Firebase Realtime Database
     * */
    private boolean deleteArtist(String id) {
        // getting the specified artist reference
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("artists").child(id);

        // removing artist
        databaseReference.removeValue();

        // getting the tracks reference for the specified artist
        DatabaseReference databaseReferenceTrack = FirebaseDatabase.getInstance().getReference("tracks").child(id);

        // removing all tracks with id are same with artist id was deleted
        databaseReferenceTrack.removeValue();
        Toast.makeText(getApplicationContext(), "Artist Deleted", Toast.LENGTH_SHORT).show();

        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        // attaching value event listener
        databaseArtists.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // clearing the previous artist list
                artists.clear();

                // iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    // getting artist
                    Artist artist = postSnapshot.getValue(Artist.class);
                    // adding artist to the list
                    artists.add(artist);
                }

                // creating adapter
                ArtistList artistAdapter = new ArtistList(MainActivity.this, artists);
                // attaching adapter to the listView
                listViewArtist.setAdapter(artistAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}