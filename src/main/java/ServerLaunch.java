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

        // TODO: Написать тестовые сокеты с реальными данными
        for (int i = 1; i< 5; i++) {
        	SocketInfo socket = new SocketInfo("user"+i, "nickname"+i, "trackTitle"+i, "trackAuthor"+i, "trackArtURL"+i, "trackPreviewURL"+i, "trackFullURL"+i, (double)i,(double)i);
            soc.add(socket);
        }

        // Create socket-server
        final SocketIOServer server = new SocketIOServer(config);

        server.addEventListener("event", SocketInfo.class, new DataListener<SocketInfo>() {
            JSONObject tracks = new JSONObject();
            public void onData(SocketIOClient client, SocketInfo data, AckRequest ackRequest) {

                // Track search
                // Information about plagin: https://github.com/EtherealT/ItunesSearch
                tracks = new MediaSearch()
                        .with("Alice Merton No Roots")
                        .inCountry(CountryCode.RU)
                        .withLimit(4)
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
                    newClient.put("longtube", socket.getLongitube());
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
