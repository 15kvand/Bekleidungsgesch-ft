package client;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import klassen.Meldung;

public class ServiceFunctions {

	private static final String BASE_URL = "http://localhost:8080";
	private static final HttpClient client = HttpClient.newHttpClient();


	// Sendet eine HTTP-GET-Anfrage an den Server und gibt die Antwort zurück.
	public static String get(String endpoint, String id, boolean requiresAuth) throws Exception {
		try {
			String url = BASE_URL + "/" + endpoint;
			if (id != null && !id.isEmpty()) {
				url += "/" + id;
			}
			URI uri = new URI(url);

			HttpRequest.Builder requestBuilder = HttpRequest.newBuilder(uri).GET();

			if (requiresAuth && BG_Client.getAktuellerBenutzer() != null) {
				BenutzerFX benutzerFX = BG_Client.getAktuellerBenutzer();
				requestBuilder.header("X-User-Email", benutzerFX.getEmail());
				requestBuilder.header("X-User-Password", benutzerFX.getPasswort());
			}

			HttpRequest request = requestBuilder.build();

			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

			int statusCode = response.statusCode();
			String responseBody = response.body();

			// Logging des Statuscodes und der Antwort
			System.out.println("GET-Anfrage an URL: " + url);
			System.out.println("Statuscode: " + statusCode);
			System.out.println("Antwortkörper: " + responseBody);

			if (statusCode == 200) {
				return responseBody;
			} else if (statusCode == 404) {
				// Ressource nicht gefunden
				return null;
			} else {
				String meldungText = null;
				try {
					Meldung meldung = new Meldung(responseBody);
					meldungText = meldung.getText();
				} catch (Exception e) {
					// Loggen, falls die Meldung nicht geparst werden kann
					System.err.println("Fehler beim Parsen der Meldung: " + e.getMessage());
				}
				throw new Exception("Fehler bei GET-Anfrage: " + meldungText + " (Statuscode: " + statusCode + ")");
			}
		} catch (IOException | URISyntaxException | InterruptedException e) {
			e.printStackTrace();
			throw new Exception("Fehler bei GET-Anfrage: " + e.getMessage());
		}
	}



	// Sendet eine HTTP-POST-Anfrage an den Server, um neue Daten zu erstellen.
	public static void post(String endpoint, String id, String detail, boolean requiresAuth) throws Exception {
		try {
			String url = BASE_URL + "/" + endpoint;
			if (id != null && !id.isEmpty()) {
				url += "/" + id;
			}
			URI uri = new URI(url);

			HttpRequest.Builder requestBuilder = HttpRequest.newBuilder(uri)
					.POST(HttpRequest.BodyPublishers.ofString(detail))
					.header("Content-Type", "application/xml; charset=UTF-8");

			if (requiresAuth && BG_Client.getAktuellerBenutzer() != null) {
				BenutzerFX benutzerFX = BG_Client.getAktuellerBenutzer();
				requestBuilder.header("X-User-Email", benutzerFX.getEmail());
				requestBuilder.header("X-User-Password", benutzerFX.getPasswort());
			}

			HttpRequest request = requestBuilder.build();

			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

			int statusCode = response.statusCode();
			String responseBody = response.body();

			// Logging des Statuscodes und der Antwort
			System.out.println("POST-Anfrage an URL: " + url);
			System.out.println("Statuscode: " + statusCode);
			System.out.println("Antwortkörper: " + responseBody);

			if (statusCode == 201) {
				// Erfolgreich erstellt
				return;
			} else {
				// Fehler aufgetreten, Fehlermeldung extrahieren
				String meldungText = null;
				try {
					Meldung meldung = new Meldung(responseBody);
					meldungText = meldung.getText();
				} catch (Exception e) {
					// Falls das Parsing fehlschlägt, verwenden wir den gesamten Antwortkörper
					meldungText = responseBody;
				}
				if (meldungText == null || meldungText.isEmpty()) {
					meldungText = "Fehler bei POST-Anfrage";
				}
				// Statuscode in die Fehlermeldung einfügen
				throw new Exception("Statuscode " + statusCode + ": " + meldungText);
			}
		} catch (IOException | URISyntaxException | InterruptedException e) {
			throw new Exception("Fehler bei POST-Anfrage: " + e.getMessage());
		}
	}


	// Sendet eine HTTP-PUT-Anfrage an den Server, um bestehende Daten zu aktualisieren.
	public static void put(String endpoint, String id, String detail, boolean requiresAuth) throws Exception {
		try {
			String url = BASE_URL + "/" + endpoint + "/" + id;
			URI uri = new URI(url);

			HttpRequest.Builder requestBuilder = HttpRequest.newBuilder(uri)
					.PUT(HttpRequest.BodyPublishers.ofString(detail))
					.header("Content-Type", "application/xml; charset=UTF-8");

			if (requiresAuth && BG_Client.getAktuellerBenutzer() != null) {
				BenutzerFX benutzerFX = BG_Client.getAktuellerBenutzer();
				requestBuilder.header("X-User-Email", benutzerFX.getEmail());
				requestBuilder.header("X-User-Password", benutzerFX.getPasswort());
			}

			HttpRequest request = requestBuilder.build();

			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

			int statusCode = response.statusCode();
			String responseBody = response.body();

			// Logging des Statuscodes und der Antwort
			System.out.println("PUT-Anfrage an URL: " + url);
			System.out.println("Statuscode: " + statusCode);
			System.out.println("Antwortkörper: " + responseBody);

			if (statusCode != 200) {
				throw new Exception("Fehler bei PUT-Anfrage: " + new Meldung(response.body()).getText());
			}
		} catch (IOException | URISyntaxException | InterruptedException e) {
			throw new Exception("Fehler bei PUT-Anfrage: " + e.getMessage());
		}
	}


	// Sendet eine HTTP-DELETE-Anfrage an den Server, um Daten zu löschen.
	public static void delete(String endpoint, String id, boolean requiresAuth) throws Exception {
		try {
			String url = BASE_URL + "/" + endpoint + "/" + id;
			URI uri = new URI(url);

			HttpRequest.Builder requestBuilder = HttpRequest.newBuilder(uri).DELETE();

			if (requiresAuth && BG_Client.getAktuellerBenutzer() != null) {
				BenutzerFX benutzerFX = BG_Client.getAktuellerBenutzer();
				requestBuilder.header("X-User-Email", benutzerFX.getEmail());
				requestBuilder.header("X-User-Password", benutzerFX.getPasswort());
			}

			HttpRequest request = requestBuilder.build();

			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

			int statusCode = response.statusCode();
			String responseBody = response.body();

			// Logging des Statuscodes und der Antwort
			System.out.println("DELETE-Anfrage an URL: " + url);
			System.out.println("Statuscode: " + statusCode);
			System.out.println("Antwortkörper: " + responseBody);

			if (statusCode != 204) {
				throw new Exception("Fehler bei DELETE-Anfrage: " + new Meldung(response.body()).getText());
			}
		} catch (IOException | URISyntaxException | InterruptedException e) {
			throw new Exception("Fehler bei DELETE-Anfrage: " + e.getMessage());
		}
	}
}