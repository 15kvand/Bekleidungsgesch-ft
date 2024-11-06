package client;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import klassen.Artikel;

public class ArtikeldetailsController {

	@FXML
	private TextField artikelNameField;

	@FXML
	private TextField artikelPreisField;

	@FXML
	private TextField artikelBeschreibungField;

	@FXML
	private TextField artikelGroesseField;

	@FXML
	private TextField artikelAnzahlField;

	@FXML
	private Button speichernButton;

	@FXML
	private Button zurueckButton;

	@FXML
	private Button bildHochladenButton;

	private ArtikelFX artikelFX; // Aktueller ArtikelFX
	private byte[] hochgeladenesBild; // Speicher für das hochgeladene Bild


	// Setzt den aktuellen ArtikelFX und füllt die Eingabefelder für die Bearbeitung.
	public void setArtikel(ArtikelFX artikelFX) {
		this.artikelFX = artikelFX;
		if (artikelFX != null) {
			// Existierender Artikel, Felder füllen
			artikelNameField.setText(artikelFX.getName());
			artikelPreisField.setText(String.valueOf(artikelFX.getPreis()));
			artikelBeschreibungField.setText(artikelFX.getBeschreibung());
			artikelGroesseField.setText(artikelFX.getGroesse());
			artikelAnzahlField.setText(String.valueOf(artikelFX.getAnzahl()));
			hochgeladenesBild = artikelFX.getBild();
		} else {
			// Neuer Artikel, Felder leeren
			artikelNameField.setText("");
			artikelPreisField.setText("");
			artikelBeschreibungField.setText("");
			artikelGroesseField.setText("");
			artikelAnzahlField.setText("");
			hochgeladenesBild = null;
		}
	}




	// Event-Handler für den “Speichern”-Button. Speichert die Änderungen
	// oder erstellt einen neuen Artikel.
	@FXML
	private void handleSpeichernButtonAction() {
		// Validierung der Eingaben
		if (!validateFields()) {
			showError("Ungültige Eingaben", "Bitte stellen Sie sicher, dass alle Felder korrekt ausgefüllt sind und dass Preis und Anzahl gültige Zahlen sind.");
			return;
		}

		// ArtikelFX-Objekt erstellen oder aktualisieren
		if (artikelFX == null) {
			// Neuer Artikel
			artikelFX = new ArtikelFX(new Artikel());
		}

		// ArtikelFX-Eigenschaften aus den Feldern setzen
		artikelFX.setName(artikelNameField.getText());
		artikelFX.setBeschreibung(artikelBeschreibungField.getText());
		artikelFX.setGroesse(artikelGroesseField.getText());
		artikelFX.setBild(hochgeladenesBild);

		// Preis und Anzahl setzen (wir haben bereits validiert, dass es Zahlen sind)
		int preis = Integer.parseInt(artikelPreisField.getText());
		artikelFX.setPreis(preis);

		int anzahl = Integer.parseInt(artikelAnzahlField.getText());
		artikelFX.setAnzahl(anzahl);

		try {
			if (artikelFX.getArtikelNummer() == 0) {
				// Neuer Artikel, POST-Anfrage senden
				ProduktlisteController.neuerArtikel(artikelFX.getServerArtikel());
			} else {
				// Existierender Artikel, PUT-Anfrage senden
				ProduktlisteController.bearbeitenArtikel(String.valueOf(artikelFX.getArtikelNummer()), artikelFX.getServerArtikel());
			}

			// Dialog schließen
			Stage stage = (Stage) speichernButton.getScene().getWindow();
			stage.close();
		} catch (Exception e) {
			e.printStackTrace();
			showError("Fehler beim Speichern", "Es ist ein Fehler aufgetreten: " + e.getMessage());
		}
	}


	// Event-Handler für den “Zurück”-Button. Schließt das Artikeldetailsformular.
	@FXML
	private void handleZurueckButtonAction() {
		// Dialog schließen
		Stage stage = (Stage) zurueckButton.getScene().getWindow();
		stage.close();
	}


	// Event-Handler für den “Bild hochladen”-Button. Öffnet einen 
	// Dateiauswahldialog zum Hochladen eines Bildes.
	@FXML
	private void handleBildHochladenButtonAction() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Bild auswählen");
		fileChooser.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("Bilddateien", "*.png", "*.jpg", "*.jpeg", "*.gif")
				);
		File selectedFile = fileChooser.showOpenDialog(bildHochladenButton.getScene().getWindow());
		if (selectedFile != null) {
			try {
				// Bilddaten lesen und speichern
				hochgeladenesBild = Files.readAllBytes(selectedFile.toPath());
				// Erfolgsmeldung anzeigen
				Alert alert = new Alert(Alert.AlertType.INFORMATION);
				alert.setTitle("Bild hochgeladen");
				alert.setHeaderText(null);
				alert.setContentText("Das Bild wurde erfolgreich hochgeladen.");
				alert.showAndWait();
			} catch (IOException e) {
				e.printStackTrace();
				// Fehler anzeigen
				showError("Fehler beim Laden des Bildes", e.getMessage());
			}
		}
	}

	// Validiert die Eingabefelder für Artikelname, Preis und Anzahl
	private boolean validateFields() {
		// Überprüfen, ob alle erforderlichen Felder ausgefüllt sind
		boolean allFieldsFilled = !artikelNameField.getText().isEmpty()
				&& !artikelPreisField.getText().isEmpty()
				&& !artikelAnzahlField.getText().isEmpty()
				&& !artikelBeschreibungField.getText().isEmpty()
				&& !artikelGroesseField.getText().isEmpty();


		// Überprüfen, ob Preis eine gültige Zahl ist
		boolean preisIsNumber = artikelPreisField.getText().matches("\\d+");

		// Überprüfen, ob Anzahl eine gültige Zahl ist
		boolean anzahlIsNumber = artikelAnzahlField.getText().matches("\\d+");

		return allFieldsFilled && preisIsNumber && anzahlIsNumber;
	}


	// Zeigt eine Fehlermeldung in Dialogfenster an.
	private void showError(String title, String message) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
}