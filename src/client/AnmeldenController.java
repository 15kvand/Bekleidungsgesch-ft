package client;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import klassen.Benutzer;
import klassen.Kunde;
import klassen.Mitarbeiter;

public class AnmeldenController {

	private static final String KUNDE_ENDPOINT = "kunde";
	private static final String MITARBEITER_ENDPOINT = "mitarbeiter";

	@FXML
	private TextField emailField;

	@FXML
	private PasswordField passwortField;

	@FXML
	private Button einloggenButton;

	@FXML
	private Button registrierenButton;


	// Event-Handler für den “Registrieren”-Button. Öffnet die Registrierungsseite.
	public void handleRegistrierenButtonAction() {
		// Öffnet die Registrierungsseite als modalen Dialog
		BG_Client.switchToBenutzerDatenVerwaltungScene();
	}


	// Event-Handler für den “Einloggen”-Button. Validiert die
	// Eingaben und meldet den Benutzer an.
	@SuppressWarnings("unused")
	public void handleEinloggenButtonAction() {
		String email = emailField.getText().trim();
		String passwort = passwortField.getText().trim();

		if (email.isEmpty() || passwort.isEmpty()) {
			showAlert("Eingabefehler", "Bitte geben Sie sowohl E-Mail als auch Passwort ein.");
			return;
		}

		try {
			BenutzerFX benutzerFX = leseBenutzerByEmailUndPasswort(email, passwort);
			if (benutzerFX != null) {
				benutzerFX.setPasswort(passwort);
				Benutzer benutzer = benutzerFX.getServerBenutzer();

				if (benutzerFX instanceof KundeFX) {
					BG_Client.setAktuellerBenutzer(benutzerFX, "Kunde");
				} else if (benutzerFX instanceof MitarbeiterFX) {
					BG_Client.setAktuellerBenutzer(benutzerFX, "Mitarbeiter");
				}
				BG_Client.switchToMenuScene();
			} else {
				showAlert("Anmeldung fehlgeschlagen", "Falsche E-Mail oder Passwort.");
			}

		} catch (Exception e) {
			e.printStackTrace();
			showAlert("Fehler", "Ein Fehler ist aufgetreten: " + e.getMessage());
		}
	}



	// Liest die Benutzerdaten (Kunde oder Mitarbeiter) 
	// basierend auf E-Mail und Passwort vom Server.
	public static BenutzerFX leseBenutzerByEmailUndPasswort(String email, String passwort) throws Exception {
		try {
			KundeFX kundeFX = leseKundeByEmailUndPasswort(email, passwort);
			if (kundeFX != null) {
				return kundeFX;
			}
		} catch (Exception e) {
			System.out.println("Kein Kunde mit dieser E-Mail und Passwort gefunden.");
		}

		try {
			MitarbeiterFX mitarbeiterFX = leseMitarbeiterByEmailUndPasswort(email, passwort);
			if (mitarbeiterFX != null) {
				return mitarbeiterFX;
			}
		} catch (Exception e) {
			System.out.println("Kein Mitarbeiter mit dieser E-Mail und Passwort gefunden.");
		}

		throw new Exception("Kein Benutzer mit der E-Mail " + email + " gefunden.");
	}



	// Liest die Kundendaten basierend auf E-Mail und Passwort vom Server.
	public static KundeFX leseKundeByEmailUndPasswort(String email, String passwort) throws Exception {
		String encodedEmail = URLEncoder.encode(email, StandardCharsets.UTF_8);
		String encodedPasswort = URLEncoder.encode(passwort, StandardCharsets.UTF_8);
		// Anfrage mit Email und Passwort
		String response = ServiceFunctions.get(KUNDE_ENDPOINT + "/email/" + encodedEmail + "/passwort/" + encodedPasswort, null, false);

		if (response != null && !response.isEmpty()) {
			Kunde kunde = new Kunde(response);
			kunde.setPasswort(passwort);   // Passwort setzen
			return new KundeFX(kunde);
		} else {
			throw new Exception("Kunde mit dieser E-Mail und Passwort wurde nicht gefunden.");
		}
	}




	// Liest die Mitarbeiterdaten basierend auf E-Mail und Passwort vom Server.
	public static MitarbeiterFX leseMitarbeiterByEmailUndPasswort(String email, String passwort) throws Exception {
		String encodedEmail = URLEncoder.encode(email, StandardCharsets.UTF_8);
		String encodedPasswort = URLEncoder.encode(passwort, StandardCharsets.UTF_8);
		// Anfrage mit Email und Passwort
		String response = ServiceFunctions.get(MITARBEITER_ENDPOINT + "/email/" + encodedEmail + "/passwort/" + encodedPasswort, null, false);

		if (response != null && !response.isEmpty()) {
			Mitarbeiter mitarbeiter = new Mitarbeiter(response);
			mitarbeiter.setPasswort(passwort);   // Passwort setzen
			return new MitarbeiterFX(mitarbeiter);
		} else {
			throw new Exception("Mitarbeiter mit dieser E-Mail und Passwort wurde nicht gefunden.");
		}
	}



	// Zeigt eine Fehlermeldung in einem Dialogfenster an.
	private void showAlert(String title, String message) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
}