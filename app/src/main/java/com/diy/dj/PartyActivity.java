package com.diy.dj;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.types.Uri;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Category;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;

public class PartyActivity extends AppCompatActivity {


    private List<Track> recomendations;
    private static int hour;
    private static int minute;
    private boolean push;
    Context context;
    int itemsClicked;
    private long timeToRefresh;
    private static SpotifyContacts spotifyContacts;

    public static void start(Context context, int hour, int minute, SpotifyContacts contacts) {
        Intent intent = new Intent(context, PartyActivity.class);
        PartyActivity.hour = hour;
        spotifyContacts = contacts;
//        spotifyContacts.setHour(hour);
     //   spotifyContacts.setMinute(minute);
        PartyActivity.minute = minute;
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        timeToRefresh = 0;
        itemsClicked = 0;
        setContentView(R.layout.activity_party);
        context = this;
        Toolbar toolbar = findViewById(R.id.menuToolbar);
        setSupportActionBar(toolbar);
        if (!SpotifyContacts.recomendedTrackes.isEmpty()) {
            Log.e("got", "here - party");
            recomendations = SpotifyContacts.recomendedTrackes;
            ListView listview = (ListView) findViewById(R.id.recomendedSongList);
            listview.setAdapter(new PartyActivity.mysongListadaptor(context, R.layout.song_item_layout, SpotifyContacts.recomendedTrackes));
        } else {
            Log.e("fuck", "man");
            recomendations = new ArrayList<Track>();
//            refreshView(findViewById(R.id.recomendedSongList));
//            final ListView listview = (ListView) findViewById(R.id.recomendedSongList);
//            listview.setAdapter(new PartyActivity.mysongListadaptor(context, R.layout.song_item_layout, recomendations));
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    public void refreshView(View view) {
    }


    public void getRecommendation(View view){
        spotifyContacts.getRecommendations();
    }

    public void nextSong(View view) {
        spotifyContacts.nextSong();
    }

    protected void onStop() {
        super.onStop();
        spotifyContacts.disconnetFromDevice();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logoutViaMenu:
                FirebaseAuth.getInstance().signOut();
                spotifyContacts.disconnetFromDevice();
                finish();
                PreLoginActivity.start(this);
                break;
        }
        return true;
    }

    private class mysongListadaptor extends ArrayAdapter<Track> {

        private int layout;
        private List<Track> mObjects;

        public mysongListadaptor(@NonNull Context context, int resource, @NonNull List<Track> objects) {
            super(context, resource, objects);
            mObjects = objects;
            layout = resource;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            PartyActivity.TrackHolder mainViewHolder = null;
            if(convertView == null){
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);
                convertView.setVisibility(View.VISIBLE);
                PartyActivity.TrackHolder trackHolder = new PartyActivity.TrackHolder();
                trackHolder.trackname = (TextView) convertView.findViewById(R.id.trackname);
                trackHolder.artist = (TextView) convertView.findViewById(R.id.artist);
                trackHolder.inButton = (Button) convertView.findViewById(R.id.inButton);
                trackHolder.outButton = (Button) convertView.findViewById(R.id.outButton);
                convertView.setTag(trackHolder);
            }
            mainViewHolder = (PartyActivity.TrackHolder) convertView.getTag();
            mainViewHolder.trackname.setText(getItem(position).name);
            String artists = new String();
            for(int j =0; j < getItem(position).artists.size(); j++){
                artists += getItem(position).artists.get(j).name + ", ";
            }
            mainViewHolder.artist.setText(artists);
            mainViewHolder.inButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<Uri> list = new ArrayList<>();
                    Uri uri = new Uri(recomendations.get(position).uri);
                    list.add(uri);
                    spotifyContacts.appendTrack(list);
                    timeToRefresh += recomendations.get(position).duration_ms;
                    View vp =(View) v.getParent();
                    Toast.makeText(context, "added"+ recomendations.get(position).name +"to the playlist", Toast.LENGTH_SHORT).show();
                    vp.setVisibility(View.GONE);
                    }
            });
            mainViewHolder.outButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    spotifyContacts.unWantedTrackes.add(recomendations.get(position));
                    Toast.makeText(context, "removed "+ recomendations.get(position).name , Toast.LENGTH_SHORT).show();
                    View vp =(View) v.getParent();
                    vp.setVisibility(View.GONE);
                }
            });

            return convertView;
        }
    }

    public class TrackHolder {
        TextView trackname;
        TextView artist;
        Button inButton;
        Button outButton;
    }
}