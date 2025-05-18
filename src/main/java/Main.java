import com.hsbc.cranker.connector.CrankerConnectorBuilder;
import com.hsbc.cranker.mucranker.CrankerRouterBuilder;
import io.muserver.MuServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        // Use the mucranker library to create a router object - this creates handlers
        var router = CrankerRouterBuilder.crankerRouter()
                .start();

        // Start a server which will listen to connector registrations on a websocket
        var registrationServer = MuServerBuilder.muServer()
                .withHttpPort(8444)
                .addHandler(router.createRegistrationHandler())
                .start();

        // Start the server that clients will send HTTP requests to
        var httpServer = MuServerBuilder.muServer()
                .withHttpsPort(Integer.parseInt(System.getenv("PORT")))
                .addHandler(router.createHttpHandler())
                .start();

        System.out.println("Cranker is available at " + httpServer.uri() +
                " with registration at " + registrationServer.uri());

        var catchAll = System.getenv("CATCH_ALL_URL");
        if (catchAll != null) {
            log.info("Starting catchAll connector pointing to {}", catchAll);
            CrankerConnectorBuilder.connector()
                    .withRoute("*")
                    .withTarget(URI.create(catchAll))
                    .withRouterLookupByDNS(URI.create("ws://localhost:%s".formatted(registrationServer.uri().getPort())))
                    .start();
        }
    }
}
