package client;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import klassen.Kunde;
import klassen.KundeList;
import klassen.Warenkorb;
import klassen.WarenkorbElement;

import java.util.List;

public class VerkaufsmanagementController {

	@FXML
	private TableView<KundeFX> kundenListeTabelle;

	@FXML
	private TableColumn<KundeFX, Integer> kundenIdColumn;

	@FXML
	private TableColumn<KundeFX, String> kundenNameColumn;

	@FXML
	private TableColumn<KundeFX, String> kundenDatumColumn;

	@FXML
	private TableColumn<KundeFX, String> kundenEmailColumn;

	@FXML
	private TableColumn<KundeFX, String> kundenAdresseColumn;

	@FXML
	private TableColumn<KundeFX, String> kundenTelNummerColumn;

	@FXML
	private TableView<WarenkorbElementFX> bestellListeTabelle;

	@FXML
	private TableColumn<WarenkorbElementFX, Integer> bestellungIdColumn;

	@FXML
	private TableColumn<WarenkorbElementFX, String> bestellungNameColumn;

	@FXML
	private TableColumn<WarenkorbElementFX, String> bestellungBeschreibungColumn;

	@FXML
	private TableColumn<WarenkorbElementFX, String> bestellungGroesseColumn;

	@FXML
	private TableColumn<WarenkorbElementFX, Integer> bestellungPreisColumn;

	@FXML
	private TableColumn<WarenkorbElementFX, Integer> bestellungAnzahlColumn;

	@FXML
	private TableColumn<WarenkorbElementFX, Boolean> bestellungAnfrageColumn;

	@FXML
	private Button zurueckButton;

	private ObservableList<KundeFX> kundenListe = FXCollections.observableArrayList();
	private ObservableList<WarenkorbElementFX> bestellListe = FXCollections.observableArrayList();



	// Initialisiert den VerkaufsmanagementController, lädt die Kundenliste 
	// und konfiguriert die Tabellen.
	@FXML
	private void initialize() {
		// Initialisierung der Kundenliste
		kundenIdColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
		kundenNameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
		kundenDatumColumn.setCellValueFactory(cellData -> cellData.getValue().registrierungsdatumProperty().asString());
		kundenEmailColumn.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
		kundenAdresseColumn.setCellValueFactory(cellData -> cellData.getValue().adresseProperty());
		kundenTelNummerColumn.setCellValueFactory(cellData -> cellData.getValue().telNummerProperty());

		kundenListeTabelle.setItems(kundenListe);

		// Initialisierung der Bestellliste
		bestellungIdColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
		bestellungNameColumn.setCellValueFactory(cellData -> cellData.getValue().artikelNameProperty());
		bestellungBeschreibungColumn.setCellValueFactory(cellData -> cellData.getValue().artikelBeschreibungProperty());
		bestellungGroesseColumn.setCellValueFactory(cellData -> cellData.getValue().artikelGroesseProperty());
		bestellungPreisColumn.setCellValueFactory(cellData -> cellData.getValue().artikelPreisProperty().asObject());
		bestellungAnzahlColumn.setCellValueFactory(cellData -> cellData.getValue().mengeProperty().asObject());

		// Initialisierung der Anfrage-Spalte mit CheckBoxen
		bestellungAnfrageColumn.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue().getStatus() == 3));

		bestellungAnfrageColumn.setCellFactory(column -> new TableCell<WarenkorbElementFX, Boolean>() {
			private final CheckBox checkBox = new CheckBox();

			@Override
			protected void updateItem(Boolean item, boolean empty) {
				super.updateItem(item, empty);
				if (empty) {
					setGraphic(null);
				} else {
					WarenkorbElementFX elementFX = getTableView().getItems().get(getIndex());
					checkBox.setSelected(elementFX.getStatus() == 3);

					checkBox.setOnAction(event -> {
						if (checkBox.isSelected()) {
							elementFX.setStatus(3); // Status auf "verschickt" setzen
						} else {
							elementFX.setStatus(2); // Zurück auf "bestellt" setzen
						}

						try {
							WarenkorbController.bearbeitenWarenkorbArtikel(elementFX.getServerWarenkorbElement());
						} catch (Exception e) {
							e.printStackTrace();
							showError("Fehler beim Aktualisieren des Bestellstatus", e.getMessage());
						}
					});
					setGraphic(checkBox);
				}
			}
		});

		bestellListeTabelle.setItems(bestellListe);

		try {
			ladeKundenListe();
		} catch (Exception e) {
			e.printStackTrace();
			showError("Fehler beim Laden der Kundenliste", e.getMessage());
		}

		// Listener für die Auswahl eines Kunden
		kundenListeTabelle.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<KundeFX>() {
			@Override
			public void changed(ObservableValue<? extends KundeFX> observable, KundeFX oldValue, KundeFX newValue) {
				if (newValue != null) {
					try {
						ladeBestellungenFuerKunden(newValue.getId());
					} catch (Exception e) {
						e.printStackTrace();
						showError("Fehler beim Laden der Bestellungen", e.getMessage());
					}
				}
			}
		});
	}


	// Lädt die Liste aller Kunden vom Server und füllt die Tabelle.
	private void ladeKundenListe() throws Exception {
		KundeList kundeList = getKundenListe();
		if (kundeList != null && kundeList.getKunden() != null) {
			kundenListe.clear();
			for (Kunde kunde : kundeList.getKunden()) {
				kundenListe.add(new KundeFX(kunde));
			}
		}
	}


	// Lädt die Bestellungen für den ausgewählten Kunden und füllt die Bestelltabelle.
	private void ladeBestellungenFuerKunden(int kundeId) throws Exception {
		List<Warenkorb> warenkoerbe = WarenkorbController.leseWarenkoerbeMitStatus(kundeId, 2);
		bestellListe.clear();
		for (Warenkorb warenkorb : warenkoerbe) {
			if (warenkorb.getElemente() != null) {
				for (WarenkorbElement element : warenkorb.getElemente()) {
					bestellListe.add(new WarenkorbElementFX(element));
				}
			}
		}
	}


	// Ruft die Kundenliste vom Server ab.
	@SuppressWarnings("exports")
	public static KundeList getKundenListe() throws Exception {
		String response = ServiceFunctions.get("kundeliste", null, true);
		if (response != null && !response.isEmpty()) {
			return new KundeList(response);
		} else {
			return null;
		}
	}


	// Event-Handler für den “Zurück”-Button. Schließt die 
	// Verkaufsmanagementansicht und kehrt zum Hauptmenü zurück.
	@FXML
	private void handleZurueckButtonAction() {
		Stage stage = (Stage) zurueckButton.getScene().getWindow();
		stage.close();
		BG_Client.switchToMenuScene();
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