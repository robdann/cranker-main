import io.muserver.MuServer;
import io.muserver.MuServerBuilder;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

public class MainTest {

    @Test
    public void testCatchAllBlocks() throws IOException, InterruptedException {
        var testServer = MuServerBuilder.httpServer()
                .addHandler((req, res) -> {
                    Thread.sleep(9000);
                    res.status(200);
                    res.write(String.valueOf(req.query().get("param")));
                    return true;
                })
                .start();

        System.setProperty("port", "9090");
        System.setProperty("catch.all.url", testServer.uri().toString());
        Main.main(new String[]{});
        Thread.sleep(1000);
        var client = HttpClient.newHttpClient();
        var param = UUID.randomUUID().toString();
        var response = client.send(HttpRequest.newBuilder(URI.create("http://localhost:9090/this-is-not-mapped-anywhere/hello?param=%s".formatted(param))).build(), HttpResponse.BodyHandlers.ofString());
        assertEquals(param, response.body());

    }
}
