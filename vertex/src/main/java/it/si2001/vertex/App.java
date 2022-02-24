package it.si2001.vertex;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
		
		//creo il server sul quale gira l'app
		HttpServer httpServer = vertx.createHttpServer();
		
	
		//Router è simile al controller
		Router router = Router.router(vertx);
		
		router
		.route()//ogni richiesta verrà intercettata qui perchè non ci sono altre routes
		.handler(routingContext ->{
			HttpServerResponse response = routingContext.response();
			response.putHeader("content-type", "text/plain");
			response.end("Hello World");
		});
		
		
		//aggiungo la porta
		httpServer
		.requestHandler(router::accept)
		.listen(8091);
		
	}
}
