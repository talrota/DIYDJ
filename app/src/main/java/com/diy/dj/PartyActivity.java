package com.diy.dj;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.ImageButton;
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
    public static Context context;
    public static int itemsClicked;
    private long timeToRefresh;
    private static SpotifyContacts spotifyContacts;
    Handler mHandler;

    public static void start(Context context, int hour, int minute, SpotifyContacts contacts) {
        Intent intent = new Intent(context, PartyActivity.class);
        PartyActivity.hour = hour;
        spotifyContacts = contacts;
        spotifyContacts.setHour(hour);
        spotifyContacts.setMinute(minute);
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
        if(SpotifyContacts.recomendedTrackes != null) {
            if (!SpotifyContacts.recomendedTrackes.isEmpty()) {
                recomendations = SpotifyContacts.recomendedTrackes;
                ListView listview = (ListView) findViewById(R.id.recomendedSongList);
                listview.setAdapter(new PartyActivity.mysongListadaptor(context, R.layout.song_item_layout, SpotifyContacts.recomendedTrackes));
            } else {
                recomendations = new ArrayList<Track>();
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    public void refreshView(View view) {
        spotifyContacts.getRecommendations();
        this.mHandler = new Handler();
        this.mHandler.postDelayed(refreshViewDelay,400);
     }

    private final Runnable refreshViewDelay = new Runnable() {
        public void run() {
            ListView listview = (ListView) findViewById(R.id.recomendedSongList);
            if (SpotifyContacts.recomendedTrackes != null) {
                listview.setAdapter(new PartyActivity.mysongListadaptor(context, R.layout.song_item_layout, SpotifyContacts.recomendedTrackes));
            } else {
                refreshView(listview);
            }
        }
    };

    public void getRecommendation(View view){
        spotifyContacts.getRecommendations();
    }

    public void pausePlay(View view){
        spotifyContacts.pausePlay(context);
        ImageButton b = findViewById(R.id.play_pause);
        //b.setImageDrawable(R.drawable.);
        }

    public void nextSong(View view) {
        spotifyContacts.nextSong(context);
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
            mainViewHolder.trackname.setText(spotifyContacts.recomendedTrackes.get(position).name);
            String artists = new String();
            for(int j =0; j < spotifyContacts.recomendedTrackes.get(position).artists.size(); j++){
                artists += spotifyContacts.recomendedTrackes.get(position).artists.get(j).name + ", ";
            }
            mainViewHolder.artist.setText(artists);
            mainViewHolder.inButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<Uri> list = new ArrayList<>();
                    Uri uri = new Uri(spotifyContacts.recomendedTrackes.get(position).uri);
                    spotifyContacts.add_track_seed(spotifyContacts.recomendedTrackes.get(position).id);
                    spotifyContacts.add_artist_seed(spotifyContacts.recomendedTrackes.get(position).artists.get(0).id);
                    list.add(uri);
                    itemsClicked ++;
                    if(itemsClicked >= spotifyContacts.limit){
                        refreshView(v);
                    }
                    spotifyContacts.appendTrack(list);
                    timeToRefresh += spotifyContacts.recomendedTrackes.get(position).duration_ms;
                    View vp =(View) v.getParent();
                    Toast.makeText(context, "added"+ spotifyContacts.recomendedTrackes.get(position).name +"to the playlist", Toast.LENGTH_SHORT).show();
                    vp.setVisibility(View.GONE);
                    }
            });
            mainViewHolder.outButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    spotifyContacts.unWantedTrackes.add(spotifyContacts.recomendedTrackes.get(position));
                    itemsClicked ++;
                    if(itemsClicked >= spotifyContacts.limit){
                        spotifyContacts.getRecommendations();
                    }
                    Toast.makeText(context, "removed "+ spotifyContacts.recomendedTrackes.get(position).name , Toast.LENGTH_SHORT).show();
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