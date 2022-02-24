package it.si2001.vertex;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();

		// creo il server sul quale gira l'app
		HttpServer httpServer = vertx.createHttpServer();

		// Router è simile al controller
		Router router = Router.router(vertx);
		Route handler1 = router.get("/hello/:name")// ogni richiesta verrà intercettata qui perchè non ci sono altre
													// routes
				.handler(routingContext -> {
					String name = routingContext.request().getParam("name");
					HttpServerResponse response = routingContext.response();
					response.setChunked(true); // per pushare i dati dal server a UI
					response.write("Hello "+name+" in get!!");

					response.end();

				});

		Route handler2 = router
				.post("/bye")// ogni richiesta verrà intercettata qui perchè non ci sono altre routes
				.consumes("*/json")//vuole solo dati json
				.handler(routingContext -> {
					HttpServerResponse response = routingContext.response();
					response.setChunked(true); // per pushare i dati dal server a UI
					response.write("Bye World in post!!");

					response.end();

				});

		// aggiungo la porta
		httpServer.requestHandler(router::accept).listen(8091);

	}
}
