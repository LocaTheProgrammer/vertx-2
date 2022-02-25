package it.si2001.vertex;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) throws SQLException {
		Vertx vertx = Vertx.vertx();

		String url = "jdbc:oracle:thin:@0.0.0.0:1522/XE";

		String userName = "demouser";
		String password = "demouser";

		Connection con = DriverManager.getConnection(url, userName, password);


		Statement st = con.createStatement();

		ResultSet rs = st.executeQuery("select * from testtable");

	
		while (rs.next()) {
			
			System.out.println(rs.getString(1));
		}
		
		// st.setInt(0, 3333);
		// creo il server sul quale gira l'app
		HttpServer httpServer = vertx.createHttpServer();

		// Router è simile al controller
		Router router = Router.router(vertx);
		router.get("/hello/:name")
									// routes
				.handler(routingContext -> {
					String name = routingContext.request().getParam("name");
					HttpServerResponse response = routingContext.response();
					response.setChunked(true); // per pushare i dati dal server a UI
					response.write("Hello " + name + " in get!!");

					response.end();

				});

		router.post("/bye")
				.consumes("*/json")// vuole solo dati json
				.handler(routingContext -> {
					HttpServerResponse response = routingContext.response();
					response.setChunked(true); // per pushare i dati dal server a UI
					response.write("Bye World in post!!");

					response.end();

				});

		// add a handler which sets the request body on the RoutingContext.
		router.route().handler(BodyHandler.create());
		// expose a POST method endpoint on the URI: /analyze
		router.post("/analyze").handler(req -> {
			JsonObject body = req.getBodyAsJson();

			// a JsonObject wraps a map and it exposes type-aware getters
			String postedText = body.getString("message");
			System.out.println(postedText);
			req.response().end("You POSTed JSON which contains a text attribute with the value: " + postedText);
		});

		// aggiungo la porta
		httpServer.requestHandler(router::accept).listen(8091);

	}

}
