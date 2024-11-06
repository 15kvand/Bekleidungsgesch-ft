package client;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import klassen.Kunde;
import klassen.Mitarbeiter;

public class BenutzerdatenverwaltungController {

	private static final String KUNDE_ENDPOINT = "kunde";


	// FXML-Elemente für die Eingabefelder
	@FXML
	private TextField emailField;

	@FXML
	private TextField passwortField;

	@FXML
	private TextField adresseField;

	@FXML
	private TextField nameField;

	@FXML
	private TextField plzField;

	@FXML
	private TextField telNummerField;

	@FXML
	private Button speichernButton;

	@FXML
	private Button zurueckButton;

	private boolean isEditing = false;  // überprüft, ob wir einen bestehenden Benutzer bearbeiten
	private Kunde aktuellerKunde;
	private Mitarbeiter aktuellerMitarbeiter;


	// Initialisiert den Controller und lädt die Daten 
	// des aktuellen Benutzers, falls vorhanden.
	public void initialize() {
		// überprüft, ob es sich um eine Bearbeitung oder um eine Neuregistrierung handelt
		if (BG_Client.getAktuellerBenutzer() != null) {
			if (BG_Client.getBenutzerTyp().equals("Kunde")) {
				this.aktuellerKunde = (Kunde) BG_Client.getAktuellerBenutzer().getServerBenutzer();
				isEditing = true;
				ladeKundenDaten(aktuellerKunde);
			} else if (BG_Client.getBenutzerTyp().equals("Mitarbeiter")) {
				this.aktuellerMitarbeiter = (Mitarbeiter) BG_Client.getAktuellerBenutzer().getServerBenutzer();
				isEditing = true;
				ladeMitarbeiterDaten(aktuellerMitarbeiter);
			}
		}
	}


	// Event-Handler für den “Speichern”-Button. 
	// Speichert die Änderungen an den Benutzerdaten oder 
	// registriert einen neuen Benutzer.
	@FXML
	public void handleSpeichernButtonAction() {
		try {
			if (isEditing) {
				// Bearbeiten von Kundendaten
				if (BG_Client.getBenutzerTyp().equals("Kunde")) {
					aktualisiereKundendaten(aktuellerKunde);
					bearbeitenKunde(String.valueOf(aktuellerKunde.getId()), aktuellerKunde);  // HTTP-PUT Anfrage
				} else if (BG_Client.getBenutzerTyp().equals("Mitarbeiter")) {
					aktualisiereMitarbeiterdaten(aktuellerMitarbeiter);
					MitarbeiterdetailsController.bearbeitenMitarbeiter(String.valueOf(aktuellerMitarbeiter.getId()), aktuellerMitarbeiter);  // HTTP-PUT Anfrage
				}
				closeDialog();

			} else {
				// Neuen Kunden registrieren
				Kunde neuerKunde = erstelleNeuenKunden();
				neuerKunde(neuerKunde);  // HTTP-POST Anfrage
				showAlert(Alert.AlertType.INFORMATION, "Erfolg", "Registrierung erfolgreich. Sie können sich jetzt anmelden.");
				closeDialog();
			}
		} catch (Exception e) {
			// Überprüfe, ob es sich um einen erwarteten Fehler handelt
			if (e.getMessage().contains("Die E-Mail-Adresse wird bereits verwendet")) {
				showAlert(Alert.AlertType.ERROR, "Fehler", e.getMessage());
			} else {
				// Unerwartete Fehler protokollieren
				e.printStackTrace();
				showAlert(Alert.AlertType.ERROR, "Fehler", "Ein unerwarteter Fehler ist aufgetreten: " + e.getMessage());
			}
		}
	}


	// Event-Handler für den “Zurück”-Button. Schließt die Benutzerdatenverwaltung.
	@FXML
	public void handleZurueckButtonAction() {
		closeDialog();
	}


	// Lädt die Daten eines Kunden in die Eingabefelder.
	private void ladeKundenDaten(Kunde kunde) {
		emailField.setText(kunde.getEmail());
		passwortField.setText(kunde.getPasswort());
		nameField.setText(kunde.getName());
		adresseField.setText(kunde.getAdresse());
		plzField.setText(kunde.getPlz());
		telNummerField.setText(kunde.getTelNummer());
	}


	// Lädt die Daten eines Mitarbeiters in die Eingabefelder.
	private void ladeMitarbeiterDaten(Mitarbeiter mitarbeiter) {
		emailField.setText(mitarbeiter.getEmail());
		passwortField.setText(mitarbeiter.getPasswort());
		nameField.setText(mitarbeiter.getName());
		adresseField.setText(mitarbeiter.getAdresse());
		plzField.setText(mitarbeiter.getPlz());
		telNummerField.setText(mitarbeiter.getTelNummer());
	}


	// Aktualisiert das Kundenobjekt mit den Daten aus den Eingabefeldern.
	private void aktualisiereKundendaten(Kunde kunde) {
		kunde.setName(nameField.getText().trim());
		kunde.setPasswort(passwortField.getText());
		kunde.setAdresse(adresseField.getText().trim());
		kunde.setPlz(plzField.getText().trim());
		kunde.setTelNummer(telNummerField.getText().trim());
	}


	// Aktualisiert das Mitarbeiterobjekt mit den Daten aus den Eingabefeldern.
	private void aktualisiereMitarbeiterdaten(Mitarbeiter mitarbeiter) {
		mitarbeiter.setName(nameField.getText().trim());
		mitarbeiter.setPasswort(passwortField.getText());
		mitarbeiter.setAdresse(adresseField.getText().trim());
		mitarbeiter.setPlz(plzField.getText().trim());
		mitarbeiter.setTelNummer(telNummerField.getText().trim());
	}


	// Erstellt ein neues Kundenobjekt basierend auf den Eingabedaten.
	private Kunde erstelleNeuenKunden() {
		Kunde neuerKunde = new Kunde();
		neuerKunde.setEmail(emailField.getText().trim());
		neuerKunde.setPasswort(passwortField.getText());
		neuerKunde.setName(nameField.getText().trim());
		neuerKunde.setAdresse(adresseField.getText().trim());
		neuerKunde.setPlz(plzField.getText().trim());
		neuerKunde.setTelNummer(telNummerField.getText().trim());
		return neuerKunde;
	}



	// Registriert einen neuen Kunden auf dem Server.
	@SuppressWarnings("exports")
	public static void neuerKunde(Kunde kunde) throws Exception {
		String kundeXML = kunde.serializeXML();
		try {
			// Registrierung erfordert keine Authentifizierung
			ServiceFunctions.post(KUNDE_ENDPOINT, null, kundeXML, false);
		} catch (Exception e) {
			// Überprüfen, ob die Ausnahme durch eine bereits verwendete E-Mail verursacht wurde
			if (e.getMessage().contains("Statuscode 409")) {
				throw new Exception("Die E-Mail-Adresse wird bereits verwendet. Bitte verwenden Sie eine andere E-Mail-Adresse.");
			} else {
				// Andere Fehler weiterwerfen
				throw e;
			}
		}
	}



	// Aktualisiert die Daten eines bestehenden Kunden auf dem Server.
	@SuppressWarnings("exports")
	public static void bearbeitenKunde(String kundeId, Kunde kunde) throws Exception {
		String kundeXML = kunde.serializeXML();
		ServiceFunctions.put(KUNDE_ENDPOINT, kundeId, kundeXML, true);
	}



	// Liest die Daten eines Kunden vom Server.
	@SuppressWarnings("exports")
	public static Kunde leseKunde(String kundeId) throws Exception {
		String response = ServiceFunctions.get(KUNDE_ENDPOINT, kundeId, true);
		if (response != null && !response.isEmpty()) {
			return new Kunde(response);  // Rückgabe des Kundenobjektes
		} else {
			throw new Exception("Kunde mit der ID " + kundeId + " wurde nicht gefunden.");
		}
	}


	// Schließt das aktuelle Dialogfenster.
	private void closeDialog() {
		Stage stage = (Stage) speichernButton.getScene().getWindow();
		stage.close();
	}


	// Zeigt einen Alert-Dialog mit dem angegebenen Typ, Titel und Nachricht an.
	private void showAlert(Alert.AlertType alertType, String title, String message) {
		Alert alert = new Alert(alertType);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
}