package client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import klassen.MitarbeiterList;

public class VerwaltungController {

	@FXML
	private TableView<MitarbeiterFX> mitarbeiterListeTabelle;

	@FXML
	private TableColumn<MitarbeiterFX, Integer> mitarbeiterIdColumn;

	@FXML
	private TableColumn<MitarbeiterFX, String> mitarbeiterNameColumn;

	@FXML
	private TableColumn<MitarbeiterFX, String> mitarbeiterAdresseColumn;

	@FXML
	private TableColumn<MitarbeiterFX, String> mitarbeiterEmailColumn;

	@FXML
	private TableColumn<MitarbeiterFX, String> mitarbeiterIbanColumn;

	@FXML
	private TableColumn<MitarbeiterFX, String> mitarbeiterTelnummerColumn;

	@FXML
	private Label gehaltsaufwandLabel;

	@FXML
	private Button hinzufuegenButton;

	@FXML
	private Button bearbeitenButton;

	@FXML
	private Button entfernenButton;

	@FXML
	private Button verkaufsstatistikButton;

	@FXML
	private Button lagerbestandButton;

	@FXML
	private Button zurueckButton;

	private ObservableList<MitarbeiterFX> mitarbeiterListe;

	// Initialisiert den VerwaltungController und lädt die Liste
	// der Mitarbeiter vom Server.
	@FXML
	private void initialize() {
		mitarbeiterIdColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
		mitarbeiterNameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
		mitarbeiterAdresseColumn.setCellValueFactory(cellData -> cellData.getValue().adresseProperty());
		mitarbeiterEmailColumn.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
		mitarbeiterIbanColumn.setCellValueFactory(cellData -> cellData.getValue().ibanProperty());
		mitarbeiterTelnummerColumn.setCellValueFactory(cellData -> cellData.getValue().telNummerProperty());

		loadMitarbeiterDaten();
	}


	// Lädt die Mitarbeiterdaten und füllt die Tabelle mit den Mitarbeitern.
	private void loadMitarbeiterDaten() {
		try {
			// Holen der Mitarbeiterliste vom Server
			MitarbeiterList mitarbeiterList = MitarbeiterdetailsController.getMitarbeiterListe();
			if (mitarbeiterList != null) {
				// Erstellen einer ObservableList<MitarbeiterFX>
				mitarbeiterListe = FXCollections.observableArrayList();

				// Konvertieren von Mitarbeiter zu MitarbeiterFX und Filtern der Admins
				mitarbeiterList.getMitarbeiter().stream()
				.filter(mitarbeiter -> !mitarbeiter.getRolle().equalsIgnoreCase("ADMIN"))
				.forEach(mitarbeiter -> mitarbeiterListe.add(new MitarbeiterFX(mitarbeiter)));

				mitarbeiterListeTabelle.setItems(mitarbeiterListe);
				updateGehaltsaufwand();
			} else {
				showError("Fehler", "Keine Mitarbeiterdaten gefunden.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			showError("Fehler beim Laden der Mitarbeiterdaten", e.getMessage());
		}
	}


	// Methode zur Aktualisierung des Gehaltsaufwands
	@SuppressWarnings("unused")
	private void updateGehaltsaufwand() {
		int totalSalaryExpense = 0;
		for (MitarbeiterFX mitarbeiterFX : mitarbeiterListe) {
			totalSalaryExpense += mitarbeiterFX.getGehalt();
		}
		gehaltsaufwandLabel.setText(totalSalaryExpense + " Euro");
	}


	// Event-Handler für den “Hinzufügen”-Button. Öffnet das Formular 
	// zum Erstellen eines neuen Mitarbeiters.
	@FXML
	private void handleHinzufuegenButtonAction() {
		// Hinzufügen eines neuen Mitarbeiters
		BG_Client.switchToMitarbeiterDetailsScene(null); // Null übergeben für neuen Mitarbeiter
		loadMitarbeiterDaten(); // Liste aktualisieren
	}


	// Event-Handler für den “Bearbeiten”-Button. Öffnet das Formular 
	// zum Bearbeiten des ausgewählten Mitarbeiters.
	@FXML
	private void handleBearbeitenButtonAction() {
		MitarbeiterFX selectedMitarbeiterFX = mitarbeiterListeTabelle.getSelectionModel().getSelectedItem();
		if (selectedMitarbeiterFX != null) {
			BG_Client.switchToMitarbeiterDetailsScene(selectedMitarbeiterFX);
			loadMitarbeiterDaten(); // Liste nach dem Bearbeiten aktualisieren
		} else {
			showError("Kein Mitarbeiter ausgewählt", "Bitte wählen Sie einen Mitarbeiter zum Bearbeiten aus.");
		}
	}


	// Event-Handler für den “Entfernen”-Button. Entfernt den ausgewählten 
	// Mitarbeiter nach Bestätigung.
	@FXML
	private void handleEntfernenButtonAction() {
		// Entfernen des ausgewählten Mitarbeiters
		MitarbeiterFX selectedMitarbeiterFX = mitarbeiterListeTabelle.getSelectionModel().getSelectedItem();
		if (selectedMitarbeiterFX != null) {
			try {
				MitarbeiterdetailsController.loeschenMitarbeiter(String.valueOf(selectedMitarbeiterFX.getId()));
				mitarbeiterListe.remove(selectedMitarbeiterFX); // Aus der Liste entfernen
				updateGehaltsaufwand();
			} catch (Exception e) {
				e.printStackTrace();
				showError("Fehler beim Entfernen des Mitarbeiters", e.getMessage());
			}
		} else {
			showError("Kein Mitarbeiter ausgewählt", "Bitte wählen Sie einen Mitarbeiter zum Entfernen aus.");
		}
	}


	// Event-Handler für den “Verkaufsstatistik”-Button. Öffnet die 
	// Verkaufsstatistikansicht.
	@FXML
	private void handleVerkaufsstatistikButtonAction() {
		BG_Client.switchToVerkaufsstatistikScene(); // Wechsel zur Verkaufsstatistik GUI
	}


	// Event-Handler für den “Lagerbestand”-Button. Öffnet die Lagerbestandsansicht.
	@FXML
	private void handleLagerbestandButtonAction() {
		BG_Client.switchToLagerbestandScene(); // Wechsel zur Lagerbestand GUI
	}


	// Event-Handler für den “Zurück”-Button. Schließt die aktuelle 
	// Ansicht und kehrt zum Hauptmenü zurück.
	@FXML
	private void handleZurueckButtonAction() {
		Stage stage = (Stage) zurueckButton.getScene().getWindow();
		stage.close();
		BG_Client.switchToMenuScene(); // Zurück zum Hauptmenü
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