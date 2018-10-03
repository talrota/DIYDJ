package com.diy.dj;


import android.content.Context;
import android.graphics.Bitmap;
import android.icu.text.SimpleDateFormat;
import android.os.AsyncTask;
import android.util.Log;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.types.ImageUri;

import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import kaaes.spotify.webapi.android.models.Track;
import com.spotify.protocol.types.Uri;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Category;
import kaaes.spotify.webapi.android.models.Playlist;
import kaaes.spotify.webapi.android.models.Recommendations;
import kaaes.spotify.webapi.android.models.UserPrivate;
import kaaes.spotify.webapi.android.models.CategoriesPager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class SpotifyContacts {
    private static final String CLIENT_ID = "89f69947e5df4167818e334f46abef71";
    private  final int  REQUEST_CODE = 1337;
    private static final String REDIRECT_URI = "com.diy.dj://callback";
    private static String spotifyTOKEN;
    private static SpotifyAppRemote mSpotifyAppRemote;
    private static String TAG = SpotifyContacts.class.getName();
    private int hour;
    private int minute;
    private static SpotifyApi spotifyApi;
    private static SpotifyService spotifyService;
    private static UserPrivate user;
    private static String ID = "000000000000000000000";
    private static Playlist playlist;
    private static String PartyTOKEN;
    private static CategoriesPager spotifyGenres;
    private static ArrayList<Category> categoryArrayList;
    private static ArrayList<Category> selectedGenres;
    public static boolean finishedConnection;
    public static Recommendations recomendtionRecived;
    public static List<Track> recomendedTrackes;
    public List<Track> unWantedTrackes;
    public List<Uri> wantedTrackes;
    public static int limit;
    private static LinkedList<String> seed_artist;
    private static LinkedList<String> seed_track;
    public static boolean firstSong;
    public static int playpause;
    public static int randomInt;



    SpotifyContacts(){
        limit = 10;
        playpause = 0;
        randomInt=0;
        firstSong = true;
        finishedConnection = false;
        user = null;
        spotifyTOKEN = null;
        hour = -99;
        minute = -99;
        spotifyService = null;
        spotifyApi = null;
        spotifyGenres = null;
        playlist = null;
        categoryArrayList = new ArrayList<Category>();
        selectedGenres = new ArrayList<Category>();
        unWantedTrackes = new ArrayList<Track>();
        wantedTrackes = new ArrayList<Uri>();
        recomendedTrackes = new ArrayList<Track>();
        seed_artist = new LinkedList<>();
        seed_artist.add("2RdwBSPQiwcmiDo9kixcl8");
        seed_track = new LinkedList<>();
        seed_track.add("2ZBNclC5wm4GtiWaeh0DMx");
    }

    // ------- getters and setters ------------
    public String getTOKEN() {
        return spotifyTOKEN;
    }

    public void setTOKEN(String token) {
        spotifyTOKEN = token;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public static String getCLIENT_ID() {
        return CLIENT_ID;
    }

    public int getREQUEST_CODE() {
        return REQUEST_CODE;
    }

    public static String getREDIRECT_URI() {
        return REDIRECT_URI;
    }

    public static String getPartyTOKEN() {
        return PartyTOKEN;
    }

    public static List<Category> getSpotifyGenres() {
//        for(int i = 0; i < categoryArrayList.size() ; i++ ){
//            Log.e("genre got : ", categoryArrayList.get(i).name);
//            for(int j = 0; j <  categoryArrayList.get(i).icons.size() ; j++ ){
//                Log.e("genre got : ", (categoryArrayList.get(i).icons.get(j).url));
//            }
//        }
        return categoryArrayList;
        }

    public ArrayList<Category> getSelectedGenres() {
        return selectedGenres;
    }

    public void add_artist_seed(String uri){
        String[] splitUri = uri.split(":");
        seed_artist.add(splitUri[splitUri.length-1]);
        if(seed_artist.size() > 5){
            seed_artist.removeFirst();
        }
    }

    public static String get_artist_seed(){
        String s = new String();
        int index = randomInt%seed_artist.size();
        randomInt++;
//        for (int i=0 ; i+1 < seed_artist.size(); i++ ){
//            s += seed_artist.get(i)+",";
//        }
        s += seed_artist.get(index);
        Log.e("seed_artist", s);
        return s;
        }

    public void add_track_seed(String uri){
            String[] splitUri = uri.split(":");
            seed_track.add(splitUri[splitUri.length-1]);
            if(seed_track.size() > 5){
                seed_track.removeFirst();
            }
    }

    public static String get_track_seed(){
        String s = new String();
        int index = randomInt%seed_artist.size();
        randomInt++;
//        for (int i=0 ; i+1 < seed_track.size(); i++ ){
//            s += seed_track.get(i)+",";
//        }
        s += seed_track.get(index);
        Log.e("seed_track", s);
            return s;

    }

            // ------- appRemote functions ------------

    public void nextSong(Context context){
        if (!mSpotifyAppRemote.isConnected()){
            concectToReomteApp(context);
        }else{
        mSpotifyAppRemote.getPlayerApi().skipNext();}
        Log.i("Next", "song");
    }

    public static void pausePlay(Context context){
        if (!mSpotifyAppRemote.isConnected()){
            concectToReomteApp(context);
        }else{
        if(playpause % 2 == 1){
            Log.i("pausePlay", "play");
            mSpotifyAppRemote.getPlayerApi().resume();
        }else{
            Log.i("pausePlay", "pause");
            mSpotifyAppRemote.getPlayerApi().pause();
        }
        playpause++;}
    }

    private static void concectToReomteApp(final Context context){
        ConnectionParams connectionParams =
                new ConnectionParams.Builder(getCLIENT_ID())
                        .setRedirectUri(getREDIRECT_URI())
                        .showAuthView(true)
                        .build();

        SpotifyAppRemote.CONNECTOR.connect(context, connectionParams,
                new Connector.ConnectionListener() {
                    @Override
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        Log.d(TAG, "Connected! Yay!");
                        // Now you can start interacting with App Remote
                    }
                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.e(TAG, throwable.getMessage(), throwable);
                        String msg = throwable.getMessage();
                        ErrorActivity.start(context, msg);
                        // Something went wrong when attempting to connect! Handle errors here
                    }
                });
    }

    public void connectToDevice(final Context context){
        spotifyApi = newApi();
        concectToReomteApp(context);
    }

    public void addSongs(String uri){
         mSpotifyAppRemote.getPlayerApi().queue(uri);
    }

    public void selectGenre(Category category){
        selectedGenres.add(category);
    }

    public void removeGenre(Category category){selectedGenres.remove(category);}

    public int numOfSelectedGenres(){
        return selectedGenres.size();
    }

    public static String selectedGenresString(Boolean withAnd){
        String s = new String();
        int size = selectedGenres.size();
        for(int i=0 ; i< selectedGenres.size(); i++){
            if (i+1 == size){
                s = s + selectedGenres.get(i).name + " ";
            }
            if (i+2 == size){
                    if(withAnd){
                    s = s + selectedGenres.get(i).name + " and ";
                    }else{
                        s = s + selectedGenres.get(i).name + ", ";
                    }
            }
            if(i+3 <= size ){
                s = s + selectedGenres.get(i).name + ", ";
            }
        }
        return s;
    }

    public void disconnetFromDevice(){
        SpotifyAppRemote.CONNECTOR.disconnect(mSpotifyAppRemote);
    }

    // ------ spotify web API functions

    private static SpotifyApi newApi(){
        new AsyncTask<Void, Void, SpotifyApi>() {
            @Override
            protected SpotifyApi doInBackground(Void... voids) {
                SpotifyApi api = null;
                try {
                    api = new SpotifyApi();
                    Log.d(TAG, api.toString());
                } catch (Exception e) {
                    Log.e(TAG, "Error creating API: " + e);
                }
                return api;
            }

            @Override
            protected void onPostExecute(SpotifyApi api) {
                spotifyApi = api;
                spotifyApi.setAccessToken(spotifyTOKEN);
                spotifyService = newService();
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        return null;
    }

    private static SpotifyService newService(){
        new AsyncTask<Void, Void, SpotifyService>() {
            @Override
            protected SpotifyService doInBackground(Void... voids) {
                SpotifyService service = null;
                try {
                    Log.e( TAG, "ON POST got id: " + ID);
                    service = spotifyApi.getService();
                } catch (Exception e) {
                    Log.e(TAG, "Error fetching service: " + e);
                }
                return service;
            }

            @Override
            protected void onPostExecute(SpotifyService newService) {
                spotifyService = newService;
                getGenres();
                user = getUser();

            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        return null;
    }

    public static UserPrivate getUser(){
        new AsyncTask<Void, Void, UserPrivate>() {
        @Override
        protected UserPrivate doInBackground(Void... voids) {
            UserPrivate userPrivate = null;
            try {
                userPrivate = spotifyService.getMe();
            } catch (Exception e) {
                Log.e(TAG, "Error fetching UserInfo: " + e);
            }
            return userPrivate;
        }

        @Override
        protected void onPostExecute(UserPrivate userPrivate) {
            user = userPrivate;
            ID = userPrivate.id;
            finishedConnection = true;
            Log.e( TAG, "ON POST got id: " + ID);
        }
    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        return null;
    }

    public CallResult<Bitmap> getImage(String stringUri){
        ImageUri uri = new ImageUri(stringUri);
        return mSpotifyAppRemote.getImagesApi().getImage(uri);

    }

    public static Playlist newPlaylist(){
        new AsyncTask<Void, Void, Playlist>() {
            @Override
            protected Playlist doInBackground(Void... voids) {
                Playlist newPlaylist = null;
                try {
                    Log.e( TAG, "ON POST got id: " + ID);
                    Map<String,Object> body = new HashMap<>();
                    String date = new SimpleDateFormat("dd/MM/YY", Locale.getDefault()).format(new Date());
                    String gen = selectedGenresString(true);
                    body.put("name","DIYDJ " +gen+ "Party Playlist " + date);
                    body.put("public", true);
                    body.put("description", "This is a " + gen +
                            "generated party playlist by DIYDJ!"+
                            " makeing sure you and your friends will Party all night long with the right music!");
                    Log.e("body playlist", body.toString());
                    newPlaylist = spotifyService.createPlaylist(ID , body);
                } catch (Exception e) {
                    Log.e(TAG, "Error Creating playilst: " + e.getCause());
                }
                return newPlaylist;
            }

            @Override
            protected void onPostExecute(Playlist newPlaylist) {
                playlist = newPlaylist;
                mSpotifyAppRemote.getPlayerApi().play(playlist.uri);
                getRecommendations();
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        return null;
    }

    public static ArrayList<Category> getGenres(){
        new AsyncTask<Void, Void, List<Category>>() {
            @Override
            protected ArrayList<Category> doInBackground(Void... voids) {
                ArrayList<Category> categories = new ArrayList<Category>();
                try {
                    Map<String,Object> body = new HashMap<>();
                    body.put("limit", "50");
                    categories.addAll(spotifyService.getCategories(body).categories.items);
                    ChooseGenres.start(LoginActivity.context, LoginActivity.spotifyContacts);
                } catch (Exception e) {
                    Log.e(TAG, "Error genres : " + e.getCause());
                }
                return categories;
            }
            @Override
            protected void onPostExecute(List<Category> result) {
                categoryArrayList.addAll(result);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        return null;
    }

    public static void appendTrack(final List<Uri> uris){
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                Void v = null;
                try {
                    Map<String,Object> body = new HashMap<>();
                    Map<String,Object> query = new HashMap<>();
                    body.put("uris", uris.get(0).uri);
//                    body.put("position", 0);
                    Log.e("body:", body.toString());
                    spotifyService.addTracksToPlaylist(ID, playlist.id, body, body, new Callback<Pager<PlaylistTrack>>() {
                        @Override
                        public void success(Pager<PlaylistTrack> playlistTrackPager, Response response) {
                            //Log.e("addTracksToPlaylist", playlistTrackPager.items.get(0).track.id);
                            mSpotifyAppRemote.getPlayerApi().queue(uris.get(0).uri);
                            if(firstSong){
                                mSpotifyAppRemote.getPlayerApi().play(playlist.uri);
                                pausePlay(PartyActivity.context);
                                firstSong = false;
                            }


                        }
                        @Override
                        public void failure(RetrofitError error) {
                            Log.e("addTracksToPlaylist - fail", error.getMessage());
                        }
                    });
                } catch (Exception e) {
                    Log.e(TAG, "Error addTracksToPlaylist : " + e.getMessage());
                }
                return v;
            }

        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public static Recommendations getRecomendedTracks(){
        new AsyncTask<Void, Void, Recommendations>() {
            @Override
            protected Recommendations doInBackground(Void... voids) {
                Recommendations recommendations = new Recommendations();
                try {
                    Map<String,Object> body = new HashMap<>();
                    body.put("limit", limit);
                    body.put("seed_artists", get_artist_seed());
                    body.put("seed_genres", selectedGenresString(false));
                    body.put("seed_tracks", get_track_seed());
                    recommendations = spotifyService.getRecommendations(body);
                } catch (Exception e) {
                    Log.e(TAG, "Error recomendation : " + e.getMessage());
                }
                return recommendations;
            }
            @Override
            protected void onPostExecute(Recommendations rec) {
                recomendedTrackes = rec.tracks;
                PartyActivity.itemsClicked = 0;
                if(rec.tracks != null) {
                    Log.e("got", rec.tracks.size() + "recomendations");
                    for (int i = 0; i < rec.tracks.size(); i++) {
                        Log.e("track name:", rec.tracks.get(i).name);
                    }
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        return null;
    }

    public static void getRecommendations(){
        recomendtionRecived = getRecomendedTracks();
//        Map<String,Object> body = new HashMap<>();
//        ArrayList genres = new ArrayList<String>();
//        for(int i=0; i < selectedGenres.size(); i++){
//            genres.add(selectedGenres.get(i).name);
//        }
//        body.put("seed_artists", new ArrayList<String>());
//        body.put("seed_genres", new ArrayList<String>());
//        body.put("seed_tracks", new ArrayList<String>());
//        body.put("danceability", "max_danceability");
//        body.put("energy", "max_energy");
//        body.put("popularity", "max_popularity");

//            recomendedTracks = spotifyService.getRecommendations(body);
//        , new Callback<Recommendations>() {
//            @Override
//            public void success(Recommendations recommendations, Response response) {
//                recomendedTracks = recommendations.tracks;
//            }
//            @Override
//            public void failure(RetrofitError error) {
//                Log.e("recommendation Error", error.toString());
//                getRecommendations();
//            }
//        });
    }
}
