import java.lang.reflect.Field;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.Callable;

import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import me.tobiadeyinka.itunessearch.entities.ReturnLanguage;
import me.tobiadeyinka.itunessearch.exceptions.NoMatchFoundException;
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

    public static void main(String[] args) throws Exception {
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

        SocketInfo socket1 = new SocketInfo("User 1", "", "В Питере - пить", "Ленинград", "Ленинград", "В Питере - пить - Single", "Рок", "https://is2-ssl.mzstatic.com/image/thumb/Music60/v4/f4/f9/66/f4f966c8-ec3e-73d4-97fd-fddad347f59d/source/300x300bb.jpg", "https://audio-ssl.itunes.apple.com/apple-assets-us-std-000001/AudioPreview30/v4/da/91/b8/da91b895-83d9-3f26-a0f2-ff1659db4e83/mzaf_5172019951187949608.plus.aac.p.m4a", "https://itunes.apple.com/ru/album/%D0%B2-%D0%BF%D0%B8%D1%82%D0%B5%D1%80%D0%B5-%D0%BF%D0%B8%D1%82%D1%8C/1107863720?i=1107864143&uo=4", "near", 56.324232, 44.030892);
        soc_near.add(socket1);
        SocketInfo socket2 = new SocketInfo("User 2", "", "Party Like a Russian", "Robbie Williams", "Robbie Williams", "The Heavy Entertainment Show (Deluxe)", "Поп-музыка", "https://is5-ssl.mzstatic.com/image/thumb/Music62/v4/a7/28/b5/a728b592-b3dd-f968-b1dc-a9ac901ac257/source/300x300bb.jpg", "https://audio-ssl.itunes.apple.com/apple-assets-us-std-000001/AudioPreview62/v4/eb/aa/d0/ebaad0ba-cbed-f548-96a9-ffc79e3a59ce/mzaf_1599664002516820993.plus.aac.p.m4a", "https://itunes.apple.com/ru/album/party-like-a-russian/1156851310?i=1156851520&uo=4", "near", 56.324333, 44.030962 );
        soc_near.add(socket2);
        SocketInfo socket3 = new SocketInfo("User 3", "", "No Roots", "Alice Merton", "Alice Merton", "No Roots - Single", "Альтернативная музыка", "https://is1-ssl.mzstatic.com/image/thumb/Music118/v4/19/60/01/19600112-1dfc-96a7-74bf-c9740e6fbc39/source/300x300bb.jpg", "https://audio-ssl.itunes.apple.com/apple-assets-us-std-000001/AudioPreview128/v4/a5/ac/d2/a5acd2cd-204d-b105-287d-32d70f27dc28/mzaf_2704331413308334440.plus.aac.p.m4a", "https://itunes.apple.com/ru/album/no-roots/1264771986?i=1264771989&uo=4", "near", 56.324432, 44.030597);
        soc_near.add(socket3);
        SocketInfo socket4 = new SocketInfo("User 4", "", "Бомба", "Ленинград", "Ленинград", "Бомба - Single", "Рок", "https://is1-ssl.mzstatic.com/image/thumb/Music1/v4/eb/a5/e4/eba5e428-26a1-bed0-d23c-20119447b969/source/300x300bb.jpg", "https://audio-ssl.itunes.apple.com/apple-assets-us-std-000001/Music5/v4/d1/e3/97/d1e397bf-7a9d-c841-31cc-be7769761841/mzaf_7438514052279884277.plus.aac.p.m4a", "https://itunes.apple.com/ru/album/%D0%B1%D0%BE%D0%BC%D0%B1%D0%B0/994156511?i=994156512&uo=4", "near", 56.324151, 44.031713);
        soc_near.add(socket4);
        SocketInfo socket5 = new SocketInfo("User 5", "", "Bon Appétit (feat. Migos)", "Katy Perry", "Katy Perry", "Witness", "Поп-музыка", "https://is4-ssl.mzstatic.com/image/thumb/Music117/v4/f5/89/15/f5891541-f666-7bc3-5e7e-de5c87a664e5/source/300x300bb.jpg", "https://audio-ssl.itunes.apple.com/apple-assets-us-std-000001/AudioPreview127/v4/38/2b/32/382b325d-2138-264b-1a49-329e666713c4/mzaf_7896431060259967753.plus.aac.p.m4a", "https://itunes.apple.com/ru/album/bon-app%C3%A9tit-feat-migos/1236471560?i=1236471571&uo=4", "near", 56.324560, 44.030141);
        soc_near.add(socket5);

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
//                    System.out.println("  "+distance);
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


    static void addRegionalTracks(ArrayList<SocketInfo> soc_region) throws Exception {
        new JSONObject();
        JSONObject tmp_track;

        new JSONObject();
        JSONObject hot_tracks;

        hot_tracks = MusicLookup.hotTracks(CountryCode.RU, 15);
        Awaitility.await().until(waitPrepared(hot_tracks));

        int i = 0, j = 0;
        while(i < 8) {
            JSONObject single_hot_track = hot_tracks.getJSONObject("feed").getJSONArray("results").getJSONObject(j);
            try {
                tmp_track = MusicLookup.getSongById(Integer.parseInt(single_hot_track.getString("id")));
                Awaitility.await().until(waitPrepared(tmp_track));

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
                socket.setTrackFullURL(single_hot_track.getString("url").replaceAll("app=itunes", ""));
                socket.setPlaylist("regional");
                socket.setLatitude(-1);
                socket.setLongitude(-1);

                soc_region.add(socket);
                i++;
            }
            catch (NoMatchFoundException e) {
                //If somehow there is no song with the recently found id
            }
            j++;
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