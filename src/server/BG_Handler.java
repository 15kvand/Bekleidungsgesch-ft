package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.net.URLDecoder;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import klassen.Artikel;
import klassen.ArtikelList;
import klassen.Benutzer;
import klassen.Kunde;
import klassen.KundeList;
import klassen.Mitarbeiter;
import klassen.MitarbeiterList;
import klassen.Warenkorb;
import klassen.WarenkorbElement;
import klassen.WarenkorbElementList;
import klassen.WarenkorbList;

public class BG_Handler implements HttpHandler {

	static {
		// Datenbank-Tabellen werden beim Start erstellt
		try {
			DatenbankV2.createTables();
		} catch (SQLException e) {
			System.out.println("Fehler beim Erstellen der Tabellen: " + e.getMessage());
		}
	}



	// Hauptmethode, die eingehende HTTP-Anfragen verarbeitet und 
	// an die entsprechenden Handler-Methoden weiterleitet.
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		String method = exchange.getRequestMethod().toUpperCase();
		URI uri = exchange.getRequestURI();

		System.out.println(uri + " " + method);  // Protokollierung der eingehenden Anfrage

		String[] paths = uri.getPath().substring(1).split("/");

		try {
			switch (method) {
			case "GET":
				handleGet(exchange, paths);
				break;
			case "POST":
				handlePost(exchange, paths);
				break;
			case "PUT":
				handlePut(exchange, paths);
				break;
			case "DELETE":
				handleDelete(exchange, paths);
				break;
			default:
				sendResponse(exchange, 405, "<meldung>Methode nicht erlaubt</meldung>");
			}
		} catch (SQLException e) {
			sendResponse(exchange, 500, "<meldung>Fehler in der Datenbank: " + e.getMessage() + "</meldung>");
		}
	}

	/*
	 * 
		• GET:
		• /artikelliste                               Für alle Benutzer zugänglich.
		• /alleartikelliste                           Nur für Admin (für Lagerbestand).
		• /verkaufteartikel                           Nur für Admin (für Verkaufsstatistik).
		• /kundeliste                                 Nur für Mitarbeiter (ADMIN und MITARBEITER).
		• /mitarbeiterliste                           Nur für Admin.
		• /warenkorb/{kundeId}                        Nur für den jeweiligen Kunden oder Mitarbeiter.
		• /warenkorb/kunde/{kundeId}/status/{status}  Nur für den jeweiligen Kunden oder Mitarbeiter.
		• /warenkorbartikel/{warenkorbId}             Für Kunden (eigene Warenkörbe) und Mitarbeiter.
		• /artikel/{artikelId}                        Für alle Benutzer zugänglich.
	 */


	// Verarbeitet eingehende HTTP-GET-Anfragen und ruft die 
	// entsprechenden Daten aus der Datenbank ab.
	private void handleGet(HttpExchange exchange, String[] paths) throws IOException, SQLException {
		String response = "";
		int statusCode = 200;

		if (paths.length == 1) {
			String endpoint = paths[0].toLowerCase();
			if (endpoint.equals("artikelliste")) {
				// Alle Benutzer können die Artikelliste abrufen
				ArrayList<Artikel> artikelListe = DatenbankV2.leseArtikel().getArtikel();
				response = new ArtikelList(artikelListe).serializeXML();
			} else if (endpoint.equals("kundeliste")) {
				// Alle Mitarbeiter (ADMIN und MITARBEITER) können die Kundenliste abrufen
				Benutzer benutzerKundeListe = authenticateUser(exchange);
				if (benutzerKundeListe instanceof Mitarbeiter) {
					ArrayList<Kunde> kundenListe = DatenbankV2.leseKunden().getKunden();
					response = new KundeList(kundenListe).serializeXML();
				} else {
					statusCode = 403;
					response = "<meldung>Zugriff verweigert</meldung>";
				}
			} else if (endpoint.equals("verkaufteartikel")) {
				// Nur Admin darf diese Daten abrufen
				Benutzer benutzer = authenticateUser(exchange);
				if (benutzer instanceof Mitarbeiter && "ADMIN".equalsIgnoreCase(((Mitarbeiter) benutzer).getRolle())) {
					List<WarenkorbElement> verkaufteArtikel = DatenbankV2.leseVerkaufteArtikel();
					WarenkorbElementList elementList = new WarenkorbElementList(verkaufteArtikel);
					response = elementList.serializeXML();
				} else {
					statusCode = 403;
					response = "<meldung>Zugriff verweigert</meldung>";
				}
			} else if (endpoint.equals("mitarbeiterliste")) {
				// Nur Admin kann die Mitarbeiterliste abrufen
				Benutzer benutzerMitarbeiterListe = authenticateUser(exchange);
				if (benutzerMitarbeiterListe instanceof Mitarbeiter) {
					Mitarbeiter mitarbeiter = (Mitarbeiter) benutzerMitarbeiterListe;
					if ("ADMIN".equalsIgnoreCase(mitarbeiter.getRolle())) {
						ArrayList<Mitarbeiter> mitarbeiterListe = DatenbankV2.leseMitarbeiter().getMitarbeiter();
						response = new MitarbeiterList(mitarbeiterListe).serializeXML();
					} else {
						statusCode = 403;
						response = "<meldung>Zugriff verweigert</meldung>";
					}
				} else {
					statusCode = 403;
					response = "<meldung>Zugriff verweigert</meldung>";
				}
			} else if (endpoint.equals("alleartikelliste")) {
				// Für Lagerbestand GUI
				Benutzer benutzerLagerbestand = authenticateUser(exchange);
				if (benutzerLagerbestand instanceof Mitarbeiter && "ADMIN".equalsIgnoreCase(((Mitarbeiter) benutzerLagerbestand).getRolle())) {
					ArrayList<Artikel> alleartikelListe = DatenbankV2.leseAlleArtikel().getArtikel();
					response = new ArtikelList(alleartikelListe).serializeXML();
				} else {
					statusCode = 403;
					response = "<meldung>Zugriff verweigert</meldung>";
				}
			} else {
				statusCode = 404;
				response = "<meldung>Nicht gefunden</meldung>";
			}
		} else if (paths.length == 5 && paths[1].equalsIgnoreCase("email") && paths[3].equalsIgnoreCase("passwort")) {
			String email = URLDecoder.decode(paths[2], StandardCharsets.UTF_8);
			String passwort = URLDecoder.decode(paths[4], StandardCharsets.UTF_8);
			String userType = paths[0].toLowerCase();
			if (userType.equals("kunde")) {
				Kunde kunde = DatenbankV2.leseKundeByEmail(email);
				if (kunde != null && kunde.getPasswort().equals(passwort)) {
					response = kunde.serializeXML();
				} else {
					statusCode = 404;
					response = "<meldung>Ungültige Anmeldeinformationen</meldung>";
				}
			} else if (userType.equals("mitarbeiter")) {
				Mitarbeiter mitarbeiter = DatenbankV2.leseMitarbeiterByEmail(email);
				if (mitarbeiter != null && mitarbeiter.getPasswort().equals(passwort)) {
					response = mitarbeiter.serializeXML();
				} else {
					statusCode = 404;
					response = "<meldung>Ungültige Anmeldeinformationen</meldung>";
				}
			} else {
				statusCode = 404;
				response = "<meldung>Nicht gefunden</meldung>";
			}
		} else if (paths.length == 2 && paths[0].equalsIgnoreCase("warenkorb")) {
			int kundeId = Integer.parseInt(paths[1]);
			Benutzer benutzer = authenticateUser(exchange);
			if (benutzer instanceof Kunde && benutzer.getId() == kundeId) {
				// Der Kunde kann seinen eigenen Warenkorb abrufen
				Warenkorb warenkorb = DatenbankV2.leseWarenkorb(kundeId);
				if (warenkorb != null) {
					response = warenkorb.serializeXML();
				} else {
					statusCode = 404;
					response = "<meldung>Warenkorb nicht gefunden</meldung>";
				}
			} else if (benutzer instanceof Mitarbeiter) {
				// Mitarbeiter oder Admin können jeden Warenkorb abrufen
				Warenkorb warenkorb = DatenbankV2.leseWarenkorb(kundeId);
				if (warenkorb != null) {
					response = warenkorb.serializeXML();
				} else {
					statusCode = 404;
					response = "<meldung>Warenkorb nicht gefunden</meldung>";
				}
			} else {
				statusCode = 403;
				response = "<meldung>Zugriff verweigert</meldung>";
			}
		} else if (paths.length == 2 && paths[0].equalsIgnoreCase("warenkorbartikel")) {
			int warenkorbId = Integer.parseInt(paths[1]);
			Benutzer benutzer = authenticateUser(exchange);
			if (benutzer instanceof Kunde || benutzer instanceof Mitarbeiter) {
				// Kunden können ihre Warenkorbartikel abrufen
				// Mitarbeiter können Warenkorbartikel von Kunden abrufen
				ArrayList<WarenkorbElement> elemente = DatenbankV2.leseWarenkorbArtikel(warenkorbId);
				ArrayList<Artikel> artikelListe = new ArrayList<>();
				for (WarenkorbElement element : elemente) {
					artikelListe.add(element.getArtikel());
				}
				response = new ArtikelList(artikelListe).serializeXML();
			} else {
				statusCode = 403;
				response = "<meldung>Zugriff verweigert</meldung>";
			}
		} else if (paths.length == 2 && paths[0].equalsIgnoreCase("artikel")) {
			int artikelId = Integer.parseInt(paths[1]);
			Artikel artikel = DatenbankV2.leseArtikelById(artikelId);
			if (artikel != null) {
				response = artikel.serializeXML();
			} else {
				statusCode = 404;
				response = "<meldung>Artikel nicht gefunden</meldung>";
			}
		} else if (paths.length == 5 && paths[0].equalsIgnoreCase("warenkorb") && paths[1].equalsIgnoreCase("kunde") && paths[3].equalsIgnoreCase("status")) {
			int kundeId = Integer.parseInt(paths[2]);
			int status = Integer.parseInt(paths[4]);
			Benutzer benutzer = authenticateUser(exchange);
			if (benutzer instanceof Mitarbeiter || (benutzer instanceof Kunde && benutzer.getId() == kundeId)) {
				// Warenkörbe des Kunden mit dem angegebenen Status abrufen
				ArrayList<Warenkorb> warenkoerbe = DatenbankV2.leseWarenkoerbeByKundeIdUndStatus(kundeId, status);
				if (!warenkoerbe.isEmpty()) {
					WarenkorbList warenkorbList = new WarenkorbList();
					warenkorbList.setWarenkoerbe(warenkoerbe);
					response = warenkorbList.serializeXML();
				} else {
					statusCode = 404;
					response = "<meldung>Keine Warenkörbe gefunden</meldung>";
				}
			} else {
				statusCode = 403;
				response = "<meldung>Zugriff verweigert</meldung>";
			}
		} else {
			statusCode = 404;
			response = "<meldung>Nicht gefunden</meldung>";
		}

		sendResponse(exchange, statusCode, response);
	}

	/*
	 * 
		• POST:
	    • /kunde                                   Registrierung ohne Authentifizierung.
	    • /mitarbeiter                             Nur Admin kann neue Mitarbeiter hinzufügen.
	    • /artikel                                 Mitarbeiter können Artikel hinzufügen.
	    • /warenkorbartikel                        Nur Kunden können Artikel zu ihrem Warenkorb hinzufügen.
	 */



	// Verarbeitet eingehende HTTP-POST-Anfragen zum Erstellen 
	// neuer Ressourcen in der Datenbank.
	private void handlePost(HttpExchange exchange, String[] paths) throws IOException, SQLException {
		InputStream is = exchange.getRequestBody();
		String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);

		if (paths.length == 1) {  // Erstellung neuer Ressourcen, z.B. /kunde
			switch (paths[0].toLowerCase()) {

			case "kunde":
				// Kundenregistrierung ohne Authentifizierung
				Kunde kunde = new Kunde(body);

				// Prüfen, ob die E-Mail-Adresse bereits existiert
				Kunde existierenderKunde = DatenbankV2.leseKundeByEmail(kunde.getEmail());

				// Überprüfen, ob die E-Mail-Adresse bereits bei einem Mitarbeiter existiert
				Mitarbeiter existierenderMitarbeiter = DatenbankV2.leseMitarbeiterByEmail(kunde.getEmail());

				if (existierenderKunde != null || existierenderMitarbeiter != null) {
					// E-Mail-Adresse bereits verwendet
					sendResponse(exchange, 409, "<meldung>E-Mail-Adresse wird bereits verwendet</meldung>");
				} else {
					// E-Mail-Adresse ist frei, Kunde hinzufügen
					DatenbankV2.insertKunde(kunde);
					sendResponse(exchange, 201, "<meldung>Kunde hinzugefügt</meldung>");
				}
				break;


			case "mitarbeiter":
				// Nur Admin kann neue Mitarbeiter hinzufügen
				Benutzer benutzer = authenticateUser(exchange);
				if (benutzer instanceof Mitarbeiter && "ADMIN".equalsIgnoreCase(((Mitarbeiter) benutzer).getRolle())) {
					Mitarbeiter mitarbeiter = new Mitarbeiter(body);

					// Überprüfen, ob die E-Mail-Adresse bereits bei einem Kunden existiert
					Kunde existingKunde = DatenbankV2.leseKundeByEmail(mitarbeiter.getEmail());

					// Überprüfen, ob die E-Mail-Adresse bereits bei einem Mitarbeiter existiert
					Mitarbeiter existingMitarbeiter = DatenbankV2.leseMitarbeiterByEmail(mitarbeiter.getEmail());

					if (existingKunde != null || existingMitarbeiter != null) {
						// E-Mail-Adresse bereits vergeben
						sendResponse(exchange, 409, "<meldung>E-Mail-Adresse wird bereits verwendet</meldung>");
					} else {
						// E-Mail-Adresse ist frei, Mitarbeiter hinzufügen
						DatenbankV2.insertMitarbeiter(mitarbeiter);
						sendResponse(exchange, 201, "<meldung>Mitarbeiter hinzugefügt</meldung>");
					}
				} else {
					sendResponse(exchange, 403, "<meldung>Zugriff verweigert</meldung>");
				}
				break;


			case "artikel":
				// Alle Mitarbeiter (ADMIN und MITARBEITER) können Artikel hinzufügen
				Benutzer benutzerArtikel = authenticateUser(exchange);
				if (benutzerArtikel instanceof Mitarbeiter) {
					Artikel artikel = new Artikel(body);
					DatenbankV2.insertArtikel(artikel);
					sendResponse(exchange, 201, "<meldung>Artikel hinzugefügt</meldung>");
				} else {
					sendResponse(exchange, 403, "<meldung>Zugriff verweigert</meldung>");
				}
				break;
			case "warenkorb":
				sendResponse(exchange, 400, "<meldung>Direktes Erstellen eines Warenkorbs nicht erlaubt</meldung>");
				break;
			case "warenkorbartikel":
				// Nur Kunden können Warenkorb-Artikel hinzufügen
				Benutzer benutzerWarenkorb = authenticateUser(exchange);
				if (benutzerWarenkorb instanceof Kunde) {
					WarenkorbElement element = new WarenkorbElement(body);
					Artikel artikel = DatenbankV2.leseArtikelById(element.getArtikel().getArtikelNummer());


					if (artikel != null && artikel.getAnzahl() >= element.getMenge()) {

						// Warenkorb für den Kunden abrufen oder erstellen
						Warenkorb warenkorb = DatenbankV2.leseWarenkorb(benutzerWarenkorb.getId());
						if (warenkorb == null) {
							warenkorb = new Warenkorb();
							warenkorb.setKunde((Kunde) benutzerWarenkorb);
							warenkorb.setDatum(LocalDate.now());
							warenkorb.setStatus(1); 
							DatenbankV2.insertWarenkorb(warenkorb);
						}
						element.setWarenkorb(warenkorb);

						// Prüfen, ob der Artikel bereits im Warenkorb ist
						WarenkorbElement vorhandenesElement = DatenbankV2.leseWarenkorbElementByArtikelId(warenkorb.getId(), artikel.getArtikelNummer());
						if (vorhandenesElement != null) {
							// Menge erhöhen
							vorhandenesElement.setMenge(vorhandenesElement.getMenge() + element.getMenge());
							DatenbankV2.updateWarenkorbArtikel(vorhandenesElement);
						} else {
							// Neues Warenkorb-Element hinzufügen
							DatenbankV2.insertWarenkorbArtikel(element);
						}

						sendResponse(exchange, 201, "<meldung>Warenkorb-Artikel hinzugefügt</meldung>");
					} else {
						sendResponse(exchange, 400, "<meldung>Artikel nicht verfügbar</meldung>");
					}
				} else {
					sendResponse(exchange, 403, "<meldung>Zugriff verweigert</meldung>");
				}
				break;
			default:
				sendResponse(exchange, 400, "<meldung>Ungültige Anfrage</meldung>");
				break;
			}
		} else {
			sendResponse(exchange, 400, "<meldung>Ungültige Anfrage</meldung>");
		}
	}

	/*
	 * 
 	• PUT:
     • /kunde/{kundeId}                         Kunde kann nur eigene Daten aktualisieren.
     • /mitarbeiter/{mitarbeiterId}             Mitarbeiter können eigene Daten aktualisieren,
                                                Admin kann alle Mitarbeiterdaten aktualisieren.
     • /artikel/{artikelId}                     Mitarbeiter können Artikel bearbeiten.
     • /warenkorb/{warenkorbId}                 Kunden können ihren eigenen Warenkorb aktualisieren.
     • /warenkorbartikel/{warenkorbArtikelId}   Mitarbeiter und Kunden können Warenkorb-Artikel aktualisieren.
	 */



	// Verarbeitet eingehende HTTP-PUT-Anfragen zum Aktualisieren
	// bestehender Ressourcen in der Datenbank.
	private void handlePut(HttpExchange exchange, String[] paths) throws IOException, SQLException {
		InputStream is = exchange.getRequestBody();
		String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);

		if (paths.length == 2) {
			int id = Integer.parseInt(paths[1]);
			switch (paths[0].toLowerCase()) {
			case "kunde":
				//  nur Kunde kann seine eigenen Daten aktualisieren
				Benutzer benutzerKunde = authenticateUser(exchange);
				if (benutzerKunde instanceof Kunde && benutzerKunde.getId() == id) {
					Kunde kunde = new Kunde(body);
					kunde.setId(id);
					DatenbankV2.updateKunde(kunde);
					sendResponse(exchange, 200, "<meldung>Kunde aktualisiert</meldung>");
				} else {
					sendResponse(exchange, 403, "<meldung>Zugriff verweigert</meldung>");
				}
				break;
			case "mitarbeiter":
				// Mitarbeiter (rolle MITARBEITER) kann nur seine eigenen Daten aktualisieren
				// Nur Admin kann Mitarbeiterdaten aktualisieren
				Benutzer benutzerMitarbeiter = authenticateUser(exchange);
				if (benutzerMitarbeiter instanceof Mitarbeiter) {
					Mitarbeiter mitarbeiter = (Mitarbeiter) benutzerMitarbeiter;
					if ("ADMIN".equalsIgnoreCase(mitarbeiter.getRolle())) {
						// Admin kann alle Mitarbeiterdaten aktualisieren
						Mitarbeiter mitarbeiterUpdate = new Mitarbeiter(body);
						mitarbeiterUpdate.setId(id);
						DatenbankV2.updateMitarbeiter(mitarbeiterUpdate);
						sendResponse(exchange, 200, "<meldung>Mitarbeiter aktualisiert</meldung>");
					} else if (mitarbeiter.getId() == id) {
						// Mitarbeiter kann eigene Daten aktualisieren
						Mitarbeiter mitarbeiterUpdate = new Mitarbeiter(body);
						mitarbeiterUpdate.setId(id);
						DatenbankV2.updateMitarbeiter(mitarbeiterUpdate);
						sendResponse(exchange, 200, "<meldung>Mitarbeiter aktualisiert</meldung>");
					} else {
						sendResponse(exchange, 403, "<meldung>Zugriff verweigert</meldung>");
					}
				} else {
					sendResponse(exchange, 403, "<meldung>Zugriff verweigert</meldung>");
				}
				break;
			case "artikel":
				// Alle Mitarbeiter (ADMIN und MITARBEITER) können Artikel bearbeiten
				Benutzer benutzerArtikel = authenticateUser(exchange);
				if (benutzerArtikel instanceof Mitarbeiter || benutzerArtikel instanceof Kunde) {
					Artikel artikel = new Artikel(body);
					artikel.setArtikelNummer(id);
					DatenbankV2.updateArtikel(artikel);
					sendResponse(exchange, 200, "<meldung>Artikel aktualisiert</meldung>");
				} else {
					sendResponse(exchange, 403, "<meldung>Zugriff verweigert</meldung>");
				}
				break;
			case "warenkorbartikel":
				// Mitarbeiter können den Status eines Warenkorb-Artikels ändern
				Benutzer benutzer = authenticateUser(exchange);
				if (benutzer instanceof Mitarbeiter || benutzer instanceof Kunde) {
					WarenkorbElement element = new WarenkorbElement(body);
					element.setId(id);
					DatenbankV2.updateWarenkorbArtikel(element);
					sendResponse(exchange, 200, "<meldung>Warenkorb-Artikel aktualisiert</meldung>");
				} else {
					sendResponse(exchange, 403, "<meldung>Zugriff verweigert</meldung>");
				}
				break;
			case "warenkorb":
				Benutzer benutzerWarenkorb = authenticateUser(exchange);
				if (benutzerWarenkorb instanceof Kunde) {
					int kundeId = benutzerWarenkorb.getId();
					Warenkorb warenkorb = DatenbankV2.leseWarenkorbById(id);
					if (warenkorb != null && warenkorb.getKunde().getId() == kundeId) {
						DatenbankV2.updateWarenkorbStatus(id, 2); // Warenkorb auf abgeschlossen setzen
						sendResponse(exchange, 200, "<meldung>Warenkorb aktualisiert</meldung>");
					} else {
						sendResponse(exchange, 403, "<meldung>Zugriff verweigert</meldung>");
					}
				}
				break;
			default:
				sendResponse(exchange, 400, "<meldung>Ungültige Anfrage</meldung>");
				break;
			}
		} else {
			sendResponse(exchange, 400, "<meldung>Ungültige Anfrage</meldung>");
		}
	}

	/*
	 * 
 	• DELETE:
     • /mitarbeiter/{mitarbeiterId}             Nur Admin kann Mitarbeiter löschen.
     • /artikel/{artikelId}                     Mitarbeiter können Artikel löschen.
     • /warenkorbartikel/{warenkorbArtikelId}   Kunde kann Artikel aus eigenem Warenkorb löschen.
	 */


	// Verarbeitet eingehende HTTP-DELETE-Anfragen zum Löschen von
	// Ressourcen in der Datenbank.
	private void handleDelete(HttpExchange exchange, String[] paths) throws IOException, SQLException {
		if (paths.length == 2) {
			int id = Integer.parseInt(paths[1]);
			switch (paths[0].toLowerCase()) {
			case "mitarbeiter":
				// Nur Admin kann Mitarbeiter löschen
				Benutzer benutzer = authenticateUser(exchange);
				if (benutzer instanceof Mitarbeiter && "ADMIN".equalsIgnoreCase(((Mitarbeiter) benutzer).getRolle())) {
					DatenbankV2.deleteMitarbeiter(id);
					sendResponse(exchange, 204, "");
				} else {
					sendResponse(exchange, 403, "<meldung>Zugriff verweigert</meldung>");
				}
				break;
			case "artikel":
				// Alle Mitarbeiter (ADMIN und MITARBEITER) können Artikel löschen
				Benutzer benutzerArtikel = authenticateUser(exchange);
				if (benutzerArtikel instanceof Mitarbeiter) {
					DatenbankV2.deleteArtikel(id);
					sendResponse(exchange, 204, "");
				} else {
					sendResponse(exchange, 403, "<meldung>Zugriff verweigert</meldung>");
				}
				break;
			case "warenkorbartikel":
				// Kunde kann Warenkorb-Artikel löschen
				Benutzer benutzerWarenkorb = authenticateUser(exchange);
				if (benutzerWarenkorb instanceof Kunde) {
					// Überprüfe, ob das Warenkorb-Element zum Kunden gehört
					WarenkorbElement element = DatenbankV2.leseWarenkorbElementById(id);
					if (element != null && element.getWarenkorb().getKunde().getId() == benutzerWarenkorb.getId()) {
						DatenbankV2.deleteWarenkorbArtikel(id);
						sendResponse(exchange, 204, "");
					} else {
						sendResponse(exchange, 403, "<meldung>Zugriff verweigert</meldung>");
					}
				} else {
					sendResponse(exchange, 403, "<meldung>Zugriff verweigert</meldung>");
				}
				break;
			default:
				sendResponse(exchange, 400, "<meldung>Ungültige Anfrage</meldung>");
				break;
			}
		} else {
			sendResponse(exchange, 400, "<meldung>Ungültige Anfrage</meldung>");
		}
	}



	// Sendet eine HTTP-Antwort mit dem angegebenen Statuscode und Antworttext.
	private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {

		// Protokollierung der ausgehenden Antwort
		System.out.println("   returncode = " + statusCode + "\n   responsebody = '" + response + "'");  

		exchange.getResponseHeaders().set("Content-Type", "application/xml; charset=UTF-8");
		exchange.sendResponseHeaders(statusCode, response.getBytes(StandardCharsets.UTF_8).length);
		try (OutputStream os = exchange.getResponseBody()) {
			os.write(response.getBytes(StandardCharsets.UTF_8));
		}
	}

	// Authentifiziert den Benutzer anhand der bereitgestellten 
	// E-Mail und Passwort in den HTTP-Headern.
	private Benutzer authenticateUser(HttpExchange exchange) throws IOException, SQLException {
		Headers headers = exchange.getRequestHeaders();
		String email = headers.getFirst("X-User-Email");
		String password = headers.getFirst("X-User-Password");

		// Protokollierung der Authentifizierung
		System.out.println("Authentifizierung des Benutzers mit E-Mail: " + email);  


		if (email == null || password == null) {
			return null;
		}

		Kunde kunde = DatenbankV2.leseKundeByEmail(email);
		if (kunde != null && kunde.getPasswort().equals(password)) {
			return kunde;
		}

		Mitarbeiter mitarbeiter = DatenbankV2.leseMitarbeiterByEmail(email);
		if (mitarbeiter != null && mitarbeiter.getPasswort().equals(password)) {
			return mitarbeiter;
		}

		return null;
	}
}