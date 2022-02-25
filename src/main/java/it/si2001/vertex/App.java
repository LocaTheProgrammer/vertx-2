package it.si2001.vertex;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import it.si2001.model.User;

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

		// creo il server sul quale gira l'app
		HttpServer httpServer = vertx.createHttpServer();

		// Router è simile al controller
		Router router = Router.router(vertx);
		router.route().handler(BodyHandler.create());

		// aggiungo la porta
		httpServer.requestHandler(router::accept).listen(8091);
		router.get("/getAll")
				// routes
				.handler(routingContext -> {
					List<User> userList = new ArrayList<>();
					try {
						Statement st = con.createStatement();
						ResultSet rs = st.executeQuery("select * from utenti");
						if (rs != null) {
							User u = new User();

							while (rs.next()) {
								u.setId(rs.getInt(1));
								u.setName(rs.getString(2));
								u.setSurname(rs.getString(3));
								userList.add(u);
							}
						}

					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					HttpServerResponse response = routingContext.response();
					response.setChunked(true); // per pushare i dati dal server a UI
					String json = new Gson().toJson(userList);
					response.write(json);

					response.end();

				});

		router.get("/get/:id")
				// routes
				.handler(routingContext -> {
					String id = routingContext.request().getParam("id");
					User u = new User();
					try {
						Statement st = con.createStatement();
						ResultSet rs = st.executeQuery("select * from utenti where ID = " + id);
						while (rs.next()) {
							u.setId(rs.getInt(1));
							u.setName(rs.getString(2));
							u.setSurname(rs.getString(3));
						}

					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					HttpServerResponse response = routingContext.response();
					response.setChunked(true); // per pushare i dati dal server a UI
					response.write(u.toString());

					response.end();

				});

		router.post("/create").consumes("*/json")// vuole solo dati json
				.handler(routingContext -> {
					JsonObject body = routingContext.getBodyAsJson();
					System.out.println(body);

					// a JsonObject wraps a map and it exposes type-aware getters
					String name = body.getString("name");
					String surname = body.getString("surname");

					User u = new User();
					u.setName(name);
					u.setSurname(surname);
					HttpServerResponse response = routingContext.response();
					PreparedStatement st;
					try {
						st = con.prepareStatement("insert into utenti(id, nome, cognome) values(?,?,?)"); // indice
																											// dovrebbe
																											// essere ai

						st.setInt(1, 3); // metto indice fisso solo per test
						st.setString(2, u.getName());
						st.setString(3, u.getSurname());
						int result = st.executeUpdate();
						System.out.println(result);

						response.setChunked(true); // per pushare i dati dal server a UI
						String json = new Gson().toJson(u);
						response.write(json);

					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						response.write(e1.toString());
					}

					response.end();

				});

		router.delete("/delete/:id").handler(routingContext -> {
			String id = routingContext.request().getParam("id");
			User u = new User();
			HttpServerResponse response = routingContext.response();
			try {
				Statement st = con.createStatement();
				st.executeQuery("delete from utenti where ID = " + id);

				response.setChunked(true); // per pushare i dati dal server a UI
				response.write("deleted");

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				response.write(e.toString());
			}

			response.end();
		});

		// add a handler which sets the request body on the RoutingContext.

	}

}
