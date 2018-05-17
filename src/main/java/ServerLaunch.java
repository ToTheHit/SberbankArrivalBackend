import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.Callable;

import org.awaitility.Awaitility;
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
        config.setHostname("localhost");
        config.setPort(9092);

        // A list in which stored sockets
        final ArrayList<SocketInfo> soc = new ArrayList<SocketInfo>();

        /*
        for (int i = 1; i< 5; i++) {
        	SocketInfo socket = new SocketInfo("user"+i, "nickname"+i, "trackTitle"+i, "trackAuthor"+i, "trackMiniArtURL"+i,"trackArtURL"+i, "trackPreviewURL"+i, "trackFullURL"+i, (double)i,(double)i);
            soc.add(socket);
        }*/

        //TODO: Заменить широту и долготу
        SocketInfo socket1 = new SocketInfo("User 1", "", "Gucci Gang", "Lil Pump", "https://is2-ssl.mzstatic.com/image/thumb/Music128/v4/17/13/93/171393f6-a2cc-aa0e-c5af-fe63a160a0b3/source/150x150bb.jpg", "https://is2-ssl.mzstatic.com/image/thumb/Music128/v4/17/13/93/171393f6-a2cc-aa0e-c5af-fe63a160a0b3/source/500x500bb.jpg", "https://audio-ssl.itunes.apple.com/apple-assets-us-std-000001/AudioPreview118/v4/8c/1a/b3/8c1ab302-b814-e148-b249-b16d3566121d/mzaf_1290930189139201618.plus.aac.p.m4a", "https://itunes.apple.com/ru/album/gucci-gang/1292381888?i=1292381954&l=en&uo=4", -1, -1);
        soc.add(socket1);
        SocketInfo socket2 = new SocketInfo("User 2", "", "Esskeetit", "Lil Pump", "https://is4-ssl.mzstatic.com/image/thumb/Music128/v4/7b/77/7f/7b777f07-d973-6163-375a-400f77eae771/source/150x150bb.jpg", "https://is4-ssl.mzstatic.com/image/thumb/Music128/v4/7b/77/7f/7b777f07-d973-6163-375a-400f77eae771/source/500x500bb.jpg", "https://audio-ssl.itunes.apple.com/apple-assets-us-std-000001/AudioPreview118/v4/d9/c0/e8/d9c0e81a-c7da-ebbb-2557-fd7c63749110/mzaf_7518946094717781395.plus.aac.p.m4a", "https://itunes.apple.com/ru/album/esskeetit/1370438300?i=1370438305&l=en&uo=4", -1, -1);
        soc.add(socket2);
        SocketInfo socket3 = new SocketInfo("User 3", "", "Got Me Thinking (feat. Veela)", "Maduk", "https://is4-ssl.mzstatic.com/image/thumb/Music71/v4/3b/c1/49/3bc149c4-c827-d5c6-eb09-0c830acf0a05/source/150x150bb.jpg", "https://is4-ssl.mzstatic.com/image/thumb/Music71/v4/3b/c1/49/3bc149c4-c827-d5c6-eb09-0c830acf0a05/source/500x500bb.jpg","https://audio-ssl.itunes.apple.com/apple-assets-us-std-000001/AudioPreview71/v4/be/4d/a2/be4da28f-be61-6930-50aa-ae70c4b59096/mzaf_5872772549499558060.plus.aac.p.m4a", "https://itunes.apple.com/ru/album/got-me-thinking-feat-veela/1179453307?i=1179453661&l=en&uo=4",-1,-1);
        soc.add(socket3);

        // Create socket-server
        final SocketIOServer server = new SocketIOServer(config);

        server.addEventListener("event", SocketInfo.class, new DataListener<SocketInfo>() {
            JSONObject tracks = new JSONObject();
            public void onData(SocketIOClient client, SocketInfo data, AckRequest ackRequest) {

                // Track search
                // Information about plugin: https://github.com/EtherealT/ItunesSearch
                tracks = new MediaSearch()
                        .with("Got Me Thinking")
                        .inCountry(CountryCode.RU)
                        .withLimit(5)
                        .execute();

                // Waiting while search is not completed
                // For the code to run, waitPrepared() must return 'true'
                // TODO: Сделать обработку события, когда поиск ничего не выдал
                Awaitility.await().until(waitPrepared());

                server.getBroadcastOperations().sendEvent("event", tracks.toString());
            }

            // Check length of search result
            Callable<Boolean> waitPrepared() {
                return new Callable<Boolean>() {
                    public Boolean call() throws Exception {
                        return tracks.length() > 0;
                    }
                };
            }
        });

        server.addEventListener("updateContent", SocketInfo.class, new DataListener<SocketInfo>() {
            public void onData(SocketIOClient client, SocketInfo data, AckRequest ackRequest) {
                Collection<JSONObject> items = new ArrayList<JSONObject>();
                for (SocketInfo socket : soc){

                    // TODO: реализовать алгоритм фильтрования сокетов по расстоянию.
                    JSONObject newClient = new JSONObject();
                    newClient.put("user", socket.getUser());
                    newClient.put("nickname", socket.getNickname());
                    newClient.put("trackTitle", socket.getTrackTitle());
                    newClient.put("trackAuthor", socket.getTrackAuthor());
                    newClient.put("trackArtURL", socket.getTrackArtURL());
                    newClient.put("trackPreviewURL", socket.getTrackPreviewURL());
                    newClient.put("trackFullURL", socket.getTrackFullURL());
                    newClient.put("latitube", socket.getLatitude());
                    newClient.put("longitude", socket.getLongitude());
                    items.add(newClient);
                }

                server.getBroadcastOperations().sendEvent("updateContent", items.toString());
            }
        });

        server.addEventListener("disconnect", SocketInfo.class, new DataListener<SocketInfo>() {
            public void onData(SocketIOClient client, SocketInfo data, AckRequest ackRequest) {
                client.disconnect();
            }
        });

        server.start();

        Thread.sleep(Integer.MAX_VALUE);

        server.stop();
    }

}
