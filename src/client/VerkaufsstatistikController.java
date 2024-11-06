package client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import klassen.WarenkorbElement;
import klassen.WarenkorbElementList;

import java.util.List;

public class VerkaufsstatistikController {

	@FXML
	private Label umsatzStatistikLabel; // Label für Umsatzstatistik

	@FXML
	private Button zurueckButton;

	@FXML
	private TableView<WarenkorbElementFX> verkaufsstatistikTabelle;

	@FXML
	private TableColumn<WarenkorbElementFX, Integer> verkaufsstatistikIdColumn;

	@FXML
	private TableColumn<WarenkorbElementFX, String> verkaufsstatistikNameColumn;

	@FXML
	private TableColumn<WarenkorbElementFX, String> verkaufsstatistikBeschreibungColumn;

	@FXML
	private TableColumn<WarenkorbElementFX, String> verkaufsstatistikGroesseColumn;

	@FXML
	private TableColumn<WarenkorbElementFX, Integer> verkaufsstatistikAnzahlColumn;

	private ObservableList<WarenkorbElementFX> verkaufteArtikelListe;


	// Initialisiert den VerkaufsstatistikController und lädt die Verkaufsdaten.
	@FXML
	private void initialize() {
		// Tabelle konfigurieren
		verkaufsstatistikIdColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
		verkaufsstatistikNameColumn.setCellValueFactory(cellData -> cellData.getValue().artikelNameProperty());
		verkaufsstatistikBeschreibungColumn.setCellValueFactory(cellData -> cellData.getValue().artikelBeschreibungProperty());
		verkaufsstatistikGroesseColumn.setCellValueFactory(cellData -> cellData.getValue().artikelGroesseProperty());
		verkaufsstatistikAnzahlColumn.setCellValueFactory(cellData -> cellData.getValue().mengeProperty().asObject());

		// Daten laden und initialisieren
		loadVerkaufteArtikel();
	}


	// Lädt die Liste der verkauften Artikel vom Server und füllt die Tabelle.
	private void loadVerkaufteArtikel() {
		try {
			// Abrufen der verkauften Artikel vom Server
			String response = ServiceFunctions.get("verkaufteartikel", null, true);

			// Parsen der Serverantwort zu einer WarenkorbElementList
			WarenkorbElementList elementList = new WarenkorbElementList(response);
			List<WarenkorbElement> verkaufteArtikel = elementList.getWarenkorbElemente();

			// Konvertieren zu ObservableList<WarenkorbElementFX>
			verkaufteArtikelListe = FXCollections.observableArrayList();
			for (WarenkorbElement element : verkaufteArtikel) {
				verkaufteArtikelListe.add(new WarenkorbElementFX(element));
			}

			// Artikel in die Tabelle setzen
			verkaufsstatistikTabelle.setItems(verkaufteArtikelListe);

			// Statistiken aktualisieren
			aktualisiereStatistiken();

		} catch (Exception e) {
			e.printStackTrace();
			showError("Fehler", "Fehler beim Laden der Verkaufsdaten: " + e.getMessage());
		}
	}


	// Berechnet und aktualisiert die Umsatzstatistik.
	private void aktualisiereStatistiken() {
		// Berechnungen für die Statistiken
		int gesamtUmsatz = 0;

		for (WarenkorbElementFX element : verkaufteArtikelListe) {
			gesamtUmsatz += element.getArtikelPreis() * element.getMenge();
		}

		// Anzeige der Statistiken
		umsatzStatistikLabel.setText(gesamtUmsatz + " Euro");
	}


	// Event-Handler für den “Zurück”-Button. Schließt die Verkaufsstatistikansicht.
	@FXML
	private void handleZurueckButtonAction() {
		Stage stage = (Stage) zurueckButton.getScene().getWindow();
		stage.close();
	}


	// Zeigt eine Fehlermeldung in einem Dialogfenster an.
	private void showError(String title, String message) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
}