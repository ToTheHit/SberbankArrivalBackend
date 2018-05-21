import java.lang.reflect.Field;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.Callable;

import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import me.tobiadeyinka.itunessearch.entities.ReturnLanguage;
import me.tobiadeyinka.itunessearch.lookup.MusicLookup;
import me.tobiadeyinka.itunessearch.search.MusicSearch;
import org.awaitility.Awaitility;
import org.json.JSONArray;
import org.json.JSONObject;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.DataListener;
import com.neovisionaries.i18n.CountryCode;

import me.tobiadeyinka.itunessearch.search.MediaSearch;
import sun.misc.Unsafe;


public class ServerLaunch {
    public static void disableWarning() {
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            Unsafe u = (Unsafe) theUnsafe.get(null);

            Class cls = Class.forName("jdk.internal.module.IllegalAccessLogger");
            Field logger = cls.getDeclaredField("logger");
            u.putObjectVolatile(cls, u.staticFieldOffset(logger), null);
        } catch (Exception e) {
            // ignore
        }
    }

    public static void main(String[] args) throws InterruptedException {
        // Clear warnings in console.
        // TODO: Разобраться в причине предупреждений
        disableWarning();
        // Create configuration for socket-server
        Configuration config = new Configuration();
        config.setHostname("0.0.0.0");

        if (args.length == 0) {
            config.setPort(3333);
        }
        else if (args.length == 1){
            config.setPort(Integer.parseInt(args[0]));
        }
        else if (args.length == 2){
            config.setPort(Integer.parseInt(args[0]));
            Globals.maxDistance = Integer.parseInt(args[1]);
        }
        // A list in which stored sockets of songs which are being played near user
        final ArrayList<SocketInfo> soc_near = new ArrayList<SocketInfo>();

        // A list in which stored sockets of popular in region songs
        final ArrayList<SocketInfo> soc_region = new ArrayList<SocketInfo>();

        SocketInfo socket1 = new SocketInfo("VkIIT", "", "Gucci Gang", "Lil Pump", "", "", "", "https://is2-ssl.mzstatic.com/image/thumb/Music128/v4/17/13/93/171393f6-a2cc-aa0e-c5af-fe63a160a0b3/source/300x300bb.jpg", "https://audio-ssl.itunes.apple.com/apple-assets-us-std-000001/AudioPreview118/v4/8c/1a/b3/8c1ab302-b814-e148-b249-b16d3566121d/mzaf_1290930189139201618.plus.aac.p.m4a", "https://itunes.apple.com/ru/album/gucci-gang/1292381888?i=1292381954&l=en&uo=4", "near", 56.132891, 44.174117);
        soc_near.add(socket1);
        SocketInfo socket2 = new SocketInfo("User 2", "", "Esskeetit", "Lil Pump", "", "", "", "https://is4-ssl.mzstatic.com/image/thumb/Music128/v4/7b/77/7f/7b777f07-d973-6163-375a-400f77eae771/source/300x150bb.jpg", "https://audio-ssl.itunes.apple.com/apple-assets-us-std-000001/AudioPreview118/v4/d9/c0/e8/d9c0e81a-c7da-ebbb-2557-fd7c63749110/mzaf_7518946094717781395.plus.aac.p.m4a", "https://itunes.apple.com/ru/album/esskeetit/1370438300?i=1370438305&l=en&uo=4", "near", 56.132891, 44.174117);
        soc_near.add(socket2);
        SocketInfo socket3 = new SocketInfo("User 3", "", "Got Me Thinking (feat. Veela)", "Maduk", "", "", "", "https://is4-ssl.mzstatic.com/image/thumb/Music71/v4/3b/c1/49/3bc149c4-c827-d5c6-eb09-0c830acf0a05/source/300x300bb.jpg", "https://audio-ssl.itunes.apple.com/apple-assets-us-std-000001/AudioPreview71/v4/be/4d/a2/be4da28f-be61-6930-50aa-ae70c4b59096/mzaf_5872772549499558060.plus.aac.p.m4a", "https://itunes.apple.com/ru/album/got-me-thinking-feat-veela/1179453307?i=1179453661&l=en&uo=4", "near", 56.132891, 44.174117);
        soc_near.add(socket3);

        //Add popular tracks in RU region when server starts
        addRegionalTracks(soc_region);

        // Create socket-server
        final SocketIOServer server = new SocketIOServer(config);

        server.addEventListener("addTrack", SocketInfo.class, new DataListener<SocketInfo>() {
            JSONObject tracks = new JSONObject();
            public void onData(SocketIOClient client, SocketInfo data, AckRequest ackRequest) {
                if (client.get("userID") == null) {
                    client.set("userID", data.getUser());
                }

                // Track search
                // Information about plugin: https://github.com/EtherealT/ItunesSearch
                tracks = new MusicSearch()
                        .with(data.getArtist() + " " + data.getTitle())
                        .inCountry(CountryCode.RU)
                        .withReturnLanguage(ReturnLanguage.JAPANESE)
                        .withLimit(1)
                        .execute();

                // Waiting while search is not completed
                // For the code to run, waitPrepared() must return 'true'
                Awaitility.await().until(waitPrepared(tracks));

               if (tracks.get(tracks.names().getString(0)) != (Object)0) {
                   deletePreviousTrack(data.getUser(), soc_near);
                   addTrack(tracks, data, soc_near);
                   System.out.println("-> New track: "+tracks.getJSONArray("results").getJSONObject(0).getString("artistName")+" - "+tracks.getJSONArray("results").getJSONObject(0).getString("trackName"));

                   server.getBroadcastOperations().sendEvent("addTrack", tracks.toString());
               }
               else System.out.println("MediaSearch: Nothing found");
            }
        });

        server.addEventListener("updateContent", SocketInfo.class, new DataListener<SocketInfo>() {
            public void onData(SocketIOClient client, SocketInfo data, AckRequest ackRequest) {
                System.out.println("-> Update content");
                Collection<JSONObject> items = new ArrayList<JSONObject>();
                for (SocketInfo socket : soc_near) {
                    if (data.getUser().equals(socket.getUser())) {
                        socket.setLatitude(data.getLatitude());
                        socket.setLongitude(data.getLongitude());
                        continue;
                    }

                    double distance = measure(socket.getLatitude(), socket.getLongitude(), data.getLatitude(), data.getLongitude());

                    if (distance < Globals.maxDistance) {
                        JSONObject newClient = new JSONObject();
                        newClient.put("user", socket.getUser());
                        newClient.put("nickname", socket.getNickname());
                        newClient.put("title", socket.getTitle());
                        newClient.put("artist", socket.getArtist());
                        newClient.put("albumArtist", socket.getArtist());
                        newClient.put("albumTitle", socket.getAlbumTitle());
                        newClient.put("genre", socket.getGenre());
                        newClient.put("artwork", socket.getArtwork());
                        newClient.put("trackPreviewURL", socket.getTrackPreviewURL());
                        newClient.put("trackFullURL", socket.getTrackFullURL());
                        newClient.put("playlist", socket.getPlaylist());
                        newClient.put("latitude", socket.getLatitude());
                        newClient.put("longitude", socket.getLongitude());
                        items.add(newClient);
                    }
                }
                server.getClient(client.getSessionId()).sendEvent("updateContent", items.toString());
//                server.getBroadcastOperations().sendEvent("updateContent", items.toString());
            }

            double measure(double lat1, double lon1, double lat2, double lon2) {
                double R = 6378.137;
                double dLat = lat2 * Math.PI / 180 - lat1 * Math.PI / 180;
                double dLon = lon2 * Math.PI / 180 - lon1 * Math.PI / 180;
                double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                        Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
                                Math.sin(dLon / 2) * Math.sin(dLon / 2);
                double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
                double d = R * c;
                return d * 1000;
            }
        });

        server.addEventListener("getRegional", SocketInfo.class, new DataListener<SocketInfo>() {
            public void onData(SocketIOClient client, SocketInfo socketInfo, AckRequest ackRequest) throws Exception {
                Collection<JSONObject> items = new ArrayList<JSONObject>();
                for (SocketInfo socket : soc_region) {
                    JSONObject newClient = new JSONObject();
                    newClient.put("user", socket.getUser());
                    newClient.put("nickname", socket.getNickname());
                    newClient.put("title", socket.getTitle());
                    newClient.put("artist", socket.getArtist());
                    newClient.put("albumArtist", socket.getArtist());
                    newClient.put("albumTitle", socket.getAlbumTitle());
                    newClient.put("genre", socket.getGenre());
                    newClient.put("artwork", socket.getArtwork());
                    newClient.put("trackPreviewURL", socket.getTrackPreviewURL());
                    newClient.put("trackFullURL", socket.getTrackFullURL());
                    newClient.put("playlist", socket.getPlaylist());
                    newClient.put("latitude", socket.getLatitude());
                    newClient.put("longitude", socket.getLongitude());
                    items.add(newClient);
                }
                server.getClient(client.getSessionId()).sendEvent("getRegional", items.toString());
            }
        });

        server.addEventListener("getAll", SocketInfo.class, new DataListener<SocketInfo>() {
            public void onData(SocketIOClient client, SocketInfo socketInfo, AckRequest ackRequest) throws Exception {
                Collection<JSONObject> items = new ArrayList<JSONObject>();
                for (SocketInfo socket : soc_near) {
                        JSONObject newClient = new JSONObject();
                        newClient.put("user", socket.getUser());
                        newClient.put("nickname", socket.getNickname());
                        newClient.put("title", socket.getTitle());
                        newClient.put("artist", socket.getArtist());
                        newClient.put("albumArtist", socket.getArtist());
                        newClient.put("albumTitle", socket.getAlbumTitle());
                        newClient.put("genre", socket.getGenre());
                        newClient.put("artwork", socket.getArtwork());
                        newClient.put("trackPreviewURL", socket.getTrackPreviewURL());
                        newClient.put("trackFullURL", socket.getTrackFullURL());
                        newClient.put("playlist", socket.getPlaylist());
                        newClient.put("latitude", socket.getLatitude());
                        newClient.put("longitude", socket.getLongitude());
                        items.add(newClient);
                }
                server.getClient(client.getSessionId()).sendEvent("getAllTracks", items.toString());
            }
        });

        server.addEventListener("disconnect", SocketInfo.class, new DataListener<SocketInfo>() {
            public void onData(SocketIOClient client, SocketInfo data, AckRequest ackRequest) {
                client.disconnect();
            }
        });


        server.addConnectListener(new ConnectListener() {
            public void onConnect(SocketIOClient client) {
                System.out.println("Socket connect. Count: "+server.getAllClients().size());
            }
        });

        server.addDisconnectListener(new DisconnectListener() {
            public void onDisconnect(SocketIOClient client) {
                System.out.println("Socket disconnect. Count: "+server.getAllClients().size());
                if (client.get("userID") != null) {
                    System.out.println("Disconnect ID: " + client.get("userID"));
                    deletePreviousTrack(client.get("userID").toString(), soc_near);
                }
            }
        });

        server.start();

        Thread.sleep(Integer.MAX_VALUE);

        server.stop();
    }

    public static void addTrack(JSONObject track, SocketInfo data, ArrayList<SocketInfo> soc_near) {
        JSONObject tmp_track = track.getJSONArray("results").getJSONObject(0);

        SocketInfo socket = new SocketInfo(
                data.getUser(),
                "",
                tmp_track.getString("trackName"),
                tmp_track.getString("artistName"),
                tmp_track.getString("artistName"),
                tmp_track.getString("collectionName"),
                tmp_track.getString("primaryGenreName"),
                tmp_track.getString("artworkUrl100").replaceAll("100", "300"),
                tmp_track.getString("previewUrl"),
                tmp_track.getString("trackViewUrl"),
                "near",
                data.getLatitude(),
                data.getLongitude()
        );
        soc_near.add(socket);
    }

    static void deletePreviousTrack(String user, ArrayList<SocketInfo> soc_near) {
        Iterator<SocketInfo> i = soc_near.iterator();
        while (i.hasNext()) {
            SocketInfo s = i.next();
            if (s.getUser().equals(user)) {
                System.out.println("Deleted: "+s.getArtist()+" - "+s.getTitle());
                i.remove();
            }
        }
    }


    static void addRegionalTracks(ArrayList<SocketInfo> soc_region) throws InterruptedException {
        new JSONObject();
        JSONObject tmp_track;

        new JSONObject();
        JSONObject hot_tracks;

        hot_tracks = MusicLookup.topSongs(CountryCode.RU, 8);
        Awaitility.await().until(waitPrepared(hot_tracks));
        for (int i = 0; i < 8; i++) {
            //System.out.println(hot_tracks.getJSONObject("feed").getJSONArray("results").getJSONObject(i));
            JSONObject single_hot_track = hot_tracks.getJSONObject("feed").getJSONArray("results").getJSONObject(i);

            tmp_track = new MusicSearch()
                    .with(single_hot_track.getString("artistName") + " " + single_hot_track.getString("name"))
                    .inCountry(CountryCode.RU)
                    .withLimit(1)
                    .execute();
            Awaitility.await().until(waitPrepared(tmp_track));
/*            SocketInfo socket = new SocketInfo(
                    "",
                    "",
                    single_hot_track.getString("name"),
                    single_hot_track.getString("artistName"),
                    single_hot_track.getString("artistName"),
                    single_hot_track.getString("collectionName"),
                    single_hot_track.getJSONArray("genres").getJSONObject(0).getString("name"),
                    single_hot_track.getString("artworkUrl100").replaceAll("100", "300"),
                    tmp_track.getJSONArray("results").getJSONObject(0).getString("previewUrl"),
                    single_hot_track.getString("url"),
                    "regional",
                    -1,
                    -1
            );*/

            SocketInfo socket = new SocketInfo();
            socket.setUser("");
            socket.setNickname("");
            socket.setTitle(single_hot_track.getString("name"));
            socket.setArtist(single_hot_track.getString("artistName"));
            socket.setAlbumArtist(single_hot_track.getString("artistName"));
            socket.setAlbumTitle(single_hot_track.getString("collectionName"));
            socket.setGenre(single_hot_track.getJSONArray("genres").getJSONObject(0).getString("name"));
            socket.setArtwork(single_hot_track.getString("artworkUrl100").replaceAll("100", "300"));
            socket.setTrackPreviewURL(tmp_track.getJSONArray("results").getJSONObject(0).getString("previewUrl"));
            socket.setTrackFullURL(single_hot_track.getString("url"));
            socket.setPlaylist("regional");
            socket.setLatitude(-1);
            socket.setLongitude(-1);

            soc_region.add(socket);
        }
    }

    // Check length of search result
    static Callable<Boolean> waitPrepared(final JSONObject tracks) {
        return new Callable<Boolean>() {
            public Boolean call() throws Exception {
                return tracks.length() > 0;
            }
        };
    }
}


