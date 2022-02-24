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
		
		//creo il server sul quale gira l'app
		HttpServer httpServer = vertx.createHttpServer();
		
	
		//Router è simile al controller
		Router router = Router.router(vertx);
		Route handler1 = router
		.route("/hello")//ogni richiesta verrà intercettata qui perchè non ci sono altre routes
		.handler(routingContext ->{
			HttpServerResponse response = routingContext.response();
			response.setChunked(true); //per pushare i dati dal server a UI
			response.write("Hello World!!");
			
			routingContext
			.vertx()
			.setTimer(3000, tid -> routingContext.next()); //per creare altre route
			
		});
		
		Route handler2 = router
				.route("/hello")//ogni richiesta verrà intercettata qui perchè non ci sono altre routes
				.handler(routingContext ->{
					HttpServerResponse response = routingContext.response();
					response.setChunked(true); //per pushare i dati dal server a UI
					response.write("Hello World 2!!");
					
					routingContext
					.vertx()
					.setTimer(3000, tid -> routingContext.next()); //per creare altre route
					
				});
		
		Route handler3 = router
				.route("/hello")//ogni richiesta verrà intercettata qui perchè non ci sono altre routes
				.handler(routingContext ->{
					HttpServerResponse response = routingContext.response();
					response.setChunked(true); //per pushare i dati dal server a UI
					response.write("Hello World 3!!");
					
					response.end("this is the end"); //per non andare piu avanti
					
				});
		
		
		//aggiungo la porta
		httpServer
		.requestHandler(router::accept)
		.listen(8091);
		
	}
}
