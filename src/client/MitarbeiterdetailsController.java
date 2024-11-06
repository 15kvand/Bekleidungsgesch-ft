package client;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import klassen.Mitarbeiter;
import klassen.MitarbeiterList;

public class MitarbeiterdetailsController {

	private static final String MITARBEITER_ENDPOINT = "mitarbeiter";
	private static final String MITARBEITER_LIST_ENDPOINT = "mitarbeiterliste";

	@FXML
	private TextField nameField;

	@FXML
	private TextField emailField;

	@FXML
	private TextField adresseField;

	@FXML
	private TextField telNummerField;

	@FXML
	private TextField plzField;

	@FXML
	private TextField ibanField;

	@FXML
	private TextField gehaltField;

	@FXML
	private TextField passwortField;

	@FXML
	private Button speichernButton;

	@FXML
	private Button zurueckButton;

	private MitarbeiterFX mitarbeiterFX; // Aktueller MitarbeiterFX

	// Setzt den aktuellen MitarbeiterFX und füllt die Eingabefelder für die Bearbeitung.
	public void setMitarbeiter(MitarbeiterFX mitarbeiterFX) {
		this.mitarbeiterFX = mitarbeiterFX;
		if (mitarbeiterFX != null) {
			// Existierender Mitarbeiter, Felder füllen
			nameField.setText(mitarbeiterFX.getName());
			emailField.setText(mitarbeiterFX.getEmail());
			adresseField.setText(mitarbeiterFX.getAdresse());
			telNummerField.setText(mitarbeiterFX.getTelNummer());
			plzField.setText(mitarbeiterFX.getPlz());
			ibanField.setText(mitarbeiterFX.getIban());
			gehaltField.setText(String.valueOf(mitarbeiterFX.getGehalt()));
			passwortField.setText(mitarbeiterFX.getPasswort());
		} else {
			// Neuer Mitarbeiter, Felder leeren
			nameField.setText("");
			emailField.setText("");
			adresseField.setText("");
			telNummerField.setText("");
			plzField.setText("");
			ibanField.setText("");
			gehaltField.setText("");
			passwortField.setText("");
		}
	}



	// Event-Handler für den “Speichern”-Button. Speichert die 
	// Änderungen oder erstellt einen neuen Mitarbeiter.
	@FXML
	private void handleSpeichernButtonAction() {
		// Validierung der Eingaben
		if (!validateFields()) {
			showError("Ungültige Eingaben", "Bitte stellen Sie sicher, dass alle Felder korrekt ausgefüllt sind und das Gehalt eine gültige Zahl ist.");
			return;
		}

		if (mitarbeiterFX == null) {
			// Neuen MitarbeiterFX erstellen
			Mitarbeiter neuerMitarbeiter = new Mitarbeiter();
			mitarbeiterFX = new MitarbeiterFX(neuerMitarbeiter);
		}

		// Daten aus den Feldern setzen
		mitarbeiterFX.setName(nameField.getText());
		mitarbeiterFX.setEmail(emailField.getText());
		mitarbeiterFX.setAdresse(adresseField.getText());
		mitarbeiterFX.setTelNummer(telNummerField.getText());
		mitarbeiterFX.setPlz(plzField.getText());
		mitarbeiterFX.setIban(ibanField.getText());
		mitarbeiterFX.setPasswort(passwortField.getText());

		try {
			int gehalt = Integer.parseInt(gehaltField.getText());
			mitarbeiterFX.setGehalt(gehalt);
		} catch (NumberFormatException e) {
			showError("Ungültiges Gehalt", "Bitte geben Sie eine gültige Zahl für das Gehalt ein.");
			return;
		}

		try {
			if (mitarbeiterFX.getId() == 0) {
				// Neuer Mitarbeiter - POST-Anfrage senden
				neuerMitarbeiter(mitarbeiterFX.getServerBenutzer());
				showSuccess("Erfolg", "Der Mitarbeiter wurde erfolgreich hinzugefügt.");
			} else {
				// Existierender Mitarbeiter - PUT-Anfrage senden
				bearbeitenMitarbeiter(String.valueOf(mitarbeiterFX.getId()), mitarbeiterFX.getServerBenutzer());
				showSuccess("Erfolg", "Der Mitarbeiter wurde erfolgreich aktualisiert.");
			}
			// Dialog schließen
			Stage stage = (Stage) speichernButton.getScene().getWindow();
			stage.close();
		} catch (Exception e) {
			// Fehlerbehandlung
			if (e.getMessage().contains("Die E-Mail-Adresse wird bereits verwendet")) {
				showError("Fehler", e.getMessage());
			} else {
				e.printStackTrace();
				showError("Fehler", "Ein unerwarteter Fehler ist aufgetreten: " + e.getMessage());
			}
		}
	}


	// Event-Handler für den “Zurück”-Button. Schließt das Mitarbeiterdetailsformular.
	@FXML
	private void handleZurueckButtonAction() {
		// Dialog schließen
		Stage stage = (Stage) zurueckButton.getScene().getWindow();
		stage.close();
	}


	// Validiert die Felder für die Eingabe eines Mitarbeiters.
	// Überprüft, ob alle erforderlichen Felder korrekt ausgefüllt sind (z.B. Name, E-Mail, etc.),
	// und gibt eine entsprechende Fehlermeldung zurück, falls ein Feld ungültig ist.
	private boolean validateFields() {
		// Überprüfen, ob alle Felder gefüllt sind
		boolean allFieldsFilled = !nameField.getText().isEmpty()
				&& !emailField.getText().isEmpty()
				&& !adresseField.getText().isEmpty()
				&& !telNummerField.getText().isEmpty()
				&& !plzField.getText().isEmpty()
				&& !ibanField.getText().isEmpty()
				&& !gehaltField.getText().isEmpty()
				&& !passwortField.getText().isEmpty();

		// Überprüfen, ob Gehalt eine gültige Zahl ist
		boolean gehaltIsNumber = gehaltField.getText().matches("\\d+");

		return allFieldsFilled && gehaltIsNumber;
	}



	// Erstellt einen neuen Mitarbeiter auf dem Server.
	@SuppressWarnings("exports")
	public static void neuerMitarbeiter(Mitarbeiter mitarbeiter) throws Exception {
		String mitarbeiterXML = mitarbeiter.serializeXML();
		try {
			ServiceFunctions.post(MITARBEITER_ENDPOINT, null, mitarbeiterXML, true);
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


	// Liest die Daten eines Mitarbeiters vom Server.
	public static MitarbeiterFX leseMitarbeiter(String mitarbeiterId) throws Exception {
		String response = ServiceFunctions.get(MITARBEITER_ENDPOINT, mitarbeiterId, true);
		if (response != null && !response.isEmpty()) {
			Mitarbeiter mitarbeiter = new Mitarbeiter(response);
			return new MitarbeiterFX(mitarbeiter);
		} else {
			throw new Exception("Mitarbeiter mit der ID " + mitarbeiterId + " wurde nicht gefunden.");
		}
	}


	// Aktualisiert einen bestehenden Mitarbeiter auf dem Server.
	@SuppressWarnings("exports")
	public static void bearbeitenMitarbeiter(String mitarbeiterId, Mitarbeiter mitarbeiter) throws Exception {
		String mitarbeiterXML = mitarbeiter.serializeXML();
		ServiceFunctions.put(MITARBEITER_ENDPOINT, mitarbeiterId, mitarbeiterXML, true);
	}


	// Löscht einen Mitarbeiter vom Server.
	public static void loeschenMitarbeiter(String mitarbeiterId) throws Exception {
		ServiceFunctions.delete(MITARBEITER_ENDPOINT, mitarbeiterId, true);
	}


	// Ruft die Mitarbeiterliste vom Server ab.
	@SuppressWarnings("exports")
	public static MitarbeiterList getMitarbeiterListe() throws Exception {
		try {
			String response = ServiceFunctions.get(MITARBEITER_LIST_ENDPOINT, null, true);
			if (response != null && !response.isEmpty()) {
				MitarbeiterList mitarbeiterList = new MitarbeiterList();
				mitarbeiterList.deserializeXML(response);
				return mitarbeiterList;
			} else {
				return null;
			}
		} catch (Exception e) {
			throw new Exception("Fehler beim Abrufen der Mitarbeiterliste: " + e.getMessage());
		}
	}


	// Zeigt eine Fehlermeldung in einem Dialogfenster an.
	private void showError(String title, String message) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}



	// Zeigt eine Erfolgsmeldung in einem Dialogfenster an.
	private void showSuccess(String title, String message) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
}