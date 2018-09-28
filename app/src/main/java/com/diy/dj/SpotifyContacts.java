package com.diy.dj;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Parcel;
import android.util.Log;

import com.google.firebase.auth.UserInfo;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;
import com.squareup.okhttp.Response;


import java.util.HashMap;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Playlist;
import kaaes.spotify.webapi.android.models.UserPrivate;
import kaaes.spotify.webapi.android.models.PlaylistBase;



public class SpotifyContacts {
    private final String CLIENT_ID;
    private  final int REQUEST_CODE;
    private final String REDIRECT_URI;
    private static String TOKEN;
    private SpotifyAppRemote mSpotifyAppRemote;
    private static String TAG = SpotifyContacts.class.getName();
    private int hour;
    private int minute;
    private static SpotifyApi spotifyApi;
    private static SpotifyService spotifyService;
    private static UserPrivate user;
    private static String ID = "000000000000000000000";
    private static Playlist playlist;

    SpotifyContacts(){
        CLIENT_ID = "89f69947e5df4167818e334f46abef71";
        REQUEST_CODE = 1337;
        REDIRECT_URI = "com.diy.dj://callback";
    }

    public String getTOKEN() {
        return TOKEN;
    }

    public void setTOKEN(String token) {
        TOKEN = token;
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

    public void nextSong(){
        mSpotifyAppRemote.getPlayerApi().skipNext();
    }

    public void connectToDevice(final Context context){
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
        spotifyApi = newApi();
    }

    public void startPlaylist(){
        try{
            playlist = newPlaylist();
            mSpotifyAppRemote.getPlayerApi().play(playlist.uri);
        }
        catch (Exception e){
            Log.e( TAG, ID);
        }
    }

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
                spotifyApi.setAccessToken(TOKEN);
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
                user = getUser();
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        return null;
    }

    private static UserPrivate getUser(){
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
            Log.e( TAG, "ON POST got id: " + ID);
            newPlaylist();
        }
    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        return null;
    }

    private static Playlist newPlaylist(){
        new AsyncTask<Void, Void, Playlist>() {
            @Override
            protected Playlist doInBackground(Void... voids) {
                Playlist newPlaylist = null;
                try {
                    Log.e( TAG, "ON POST got id: " + ID);
                    Map<String,Object> body = new HashMap<>();
                    body.put("name","DIY-DJ-GENERATED-FIRST-PLAYLIST!!!");
                    body.put("public", false);
                    body.put("description", "my fucking god!");
                    newPlaylist = spotifyService.createPlaylist(ID , body);
                } catch (Exception e) {
                    Log.e(TAG, "Error Creating playilst: " + e.getCause());
                }
                return newPlaylist;
            }

            @Override
            protected void onPostExecute(Playlist newPlaylist) {
                playlist = newPlaylist;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        return null;
    }

    public void disconnetFromDevice(){
        SpotifyAppRemote.CONNECTOR.disconnect(mSpotifyAppRemote);
        }

    public String getCLIENT_ID() {
        return CLIENT_ID;
    }

    public int getREQUEST_CODE() {
        return REQUEST_CODE;
    }

    public String getREDIRECT_URI() {
        return REDIRECT_URI;
    }

    public void addingSongs(){
       // mSpotifyAppRemote.getPlayerApi().queue()
    }

    public void dodo(){
        mSpotifyAppRemote.getPlayerApi()
                .subscribeToPlayerState().setEventCallback(new Subscription.EventCallback<PlayerState>() {
                    public void onEvent(PlayerState playerState) {
                        final Track track = playerState.track;
                        if (track != null) {
                        Log.d("MainActivity", track.name + " by " + track.artist.name);
                }
            }
        });
    }
}
