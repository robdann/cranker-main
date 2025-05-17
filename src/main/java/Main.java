import com.hsbc.cranker.mucranker.CrankerRouter;
import com.hsbc.cranker.mucranker.CrankerRouterBuilder;
import io.muserver.MuServer;
import io.muserver.MuServerBuilder;

public class Main {
    public static void main(String[] args) {

        // Use the mucranker library to create a router object - this creates handlers
        var router = CrankerRouterBuilder.crankerRouter()
                .start();

        // Start a server which will listen to connector registrations on a websocket
        var registrationServer = MuServerBuilder.muServer()
                .withHttpsPort(8444)
                .addHandler(router.createRegistrationHandler())
                .start();

        // Start the server that clients will send HTTP requests to
        var httpServer = MuServerBuilder.muServer()
                .withHttpsPort(Integer.parseInt(System.getenv("PORT")))
                .addHandler(router.createHttpHandler())
                .start();

        System.out.println("Cranker is available at " + httpServer.uri() +
                " with registration at " + registrationServer.uri());
    }
}
