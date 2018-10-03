package com.diy.dj;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.spotify.protocol.client.CallResult;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import kaaes.spotify.webapi.android.models.CategoriesPager;
import kaaes.spotify.webapi.android.models.Category;
import kaaes.spotify.webapi.android.models.Image;

import java.net.URL;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ChooseGenres extends AppCompatActivity {

    private List<Category> genresData;
    private static AuthenticationResponse recResponse;
    private static SpotifyContacts spotifyContacts;
    private Button button;
    private Context context;

    public static void start(Context context, SpotifyContacts contacts){
        spotifyContacts = contacts;
        Intent intent = new Intent(context, ChooseGenres.class);
        context.startActivity(intent);
    }

    public  void openSettings(View view){
        Settings.start(this, spotifyContacts);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choosegenres);
        button = findViewById(R.id.chooseGenresbutton);
        genresData = spotifyContacts.getSpotifyGenres();
        context = this;
        final ListView listview = (ListView) findViewById(R.id.genreslistview);
        listview.setAdapter(new myListadaptor(this, R.layout.ganre_item_layout ,genresData));
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (spotifyContacts.getSelectedGenres().contains(genresData.get(position))){
                    spotifyContacts.removeGenre(genresData.get(position));
                    Toast.makeText(context, "unselected " + genresData.get(position).name, Toast.LENGTH_SHORT).show();
                }else {
                    spotifyContacts.selectGenre(genresData.get(position));
                    Toast.makeText(context, "selected " + genresData.get(position).name, Toast.LENGTH_SHORT).show();
                }
                ViewHolder vh = new ViewHolder(view);
                int sum = parent.getChildCount();
                for (int i=0; i < sum; i++){
                    View v = parent.getChildAt(i);
                    if(spotifyContacts.selectedGenresString(false).contains(((TextView)v.findViewById(R.id.genre_name)).getText())){
                        v.setBackgroundColor(getResources().getColor(R.color.spotifYellow));
                        TextView t = v.findViewById(R.id.genre_name);
                        t.setTextColor(getResources().getColor(R.color.spotifyBlack));
                    }else{
                        v.setBackgroundColor(getResources().getColor(R.color.spotifyBlack));
                        TextView t = v.findViewById(R.id.genre_name);
                        t.setTextColor(getResources().getColor(R.color.spotifYellow));
                    }
                }
                if(spotifyContacts.numOfSelectedGenres() >= 3){
                    button.setBackgroundColor(getResources().getColor(R.color.spotifyGreen));
                }else{
                    button.setBackgroundColor(getResources().getColor(R.color.spotifyRed));
                }
            }
        });
    }

    public void button(View view){
        int num = spotifyContacts.numOfSelectedGenres();
        if (spotifyContacts.finishedConnection & num >= 2 & num <= 5){
            Settings.start(this, spotifyContacts);
            spotifyContacts.newPlaylist();
        }if( num >= 2){
            Toast.makeText(this, "select at least 2 genres", Toast.LENGTH_SHORT).show();
        }if( num > 5 ){
            Toast.makeText(this, "select up to 5 genres", Toast.LENGTH_SHORT).show();
        }if(!spotifyContacts.finishedConnection){
            Toast.makeText(this, "still connecting to Spotify", Toast.LENGTH_SHORT).show();
        }
    }

    private class myListadaptor extends ArrayAdapter<Category>{

        private int layout;
        private List<Category> mObjects;

        public myListadaptor(@NonNull Context context, int resource, @NonNull List<Category> objects) {
            super(context, resource, objects);
            mObjects = objects;
            layout = resource;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder mainViewHolder = null;
            if(convertView == null){
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.thumbnail = (ImageView) convertView.findViewById(R.id.genre_image);
                viewHolder.name = (TextView) convertView.findViewById(R.id.genre_name);
                convertView.setTag(viewHolder);
            }
            mainViewHolder = (ViewHolder) convertView.getTag();
            mainViewHolder.name.setText(getItem(position).name);
            Image image = getItem(position).icons.get(0);
            final ViewHolder finalMainViewHolder = mainViewHolder;
//            spotifyContacts.getImage(image.url).setResultCallback(new CallResult.ResultCallback<Bitmap>() {
//                @Override
//                public void onResult(Bitmap bitmap) {
//                    Log.e("got here", "oh no");
//
//                    finalMainViewHolder.thumbnail.setImageBitmap(bitmap);
//                }
//            });
            return convertView;
        }
    }

    public class ViewHolder{
        ImageView thumbnail;
        TextView name;
        ViewHolder(){}

        ViewHolder(View view){
            thumbnail = (ImageView) view.findViewById(R.id.genre_image);
            name = (TextView) view.findViewById(R.id.genre_name);
        }
    }
}
