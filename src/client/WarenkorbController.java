package client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Callback;
import klassen.Artikel;
import klassen.Kunde;
import klassen.Warenkorb;
import klassen.WarenkorbElement;
import klassen.WarenkorbList;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

public class WarenkorbController {

	private static final String WARENKORB_ENDPOINT = "warenkorb";
	private static final String WARENKORB_ARTIKEL_ENDPOINT = "warenkorbartikel";


	@FXML
	private TableView<WarenkorbElementFX> warenkorbTabelle;

	@FXML
	private TableColumn<WarenkorbElementFX, Integer> warenkorbIdColumn;

	@FXML
	private TableColumn<WarenkorbElementFX, String> artikelNameColumn;

	@FXML
	private TableColumn<WarenkorbElementFX, String> warenkorbBeschreibungColumn;

	@FXML
	private TableColumn<WarenkorbElementFX, String> warenkorbGroesseColumn;

	@FXML
	private TableColumn<WarenkorbElementFX, Integer> preisColumn;

	@FXML
	private TableColumn<WarenkorbElementFX, Integer> mengeColumn;

	@FXML
	private TableColumn<WarenkorbElementFX, ImageView> warenkorbBildColumn;

	@FXML
	private Button entfernenButton;

	@FXML
	private Button bestellenButton;

	@FXML
	private Label gesamtbetragLabel;


	// Initialisiert den WarenkorbController, konfiguriert die Tabellen und lädt die 
	// Warenkorbdaten des aktuellen Benutzers.
	@FXML
	private void initialize() {
		// Spalten initialisieren
		warenkorbIdColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
		artikelNameColumn.setCellValueFactory(cellData -> cellData.getValue().artikelNameProperty());
		mengeColumn.setCellValueFactory(cellData -> cellData.getValue().mengeProperty().asObject());
		preisColumn.setCellValueFactory(cellData -> cellData.getValue().artikelPreisProperty().asObject());
		warenkorbBeschreibungColumn.setCellValueFactory(cellData -> cellData.getValue().artikelBeschreibungProperty());
		warenkorbGroesseColumn.setCellValueFactory(cellData -> cellData.getValue().artikelGroesseProperty());

		// CellFactory für das Bild setzen
		warenkorbBildColumn.setCellFactory(new Callback<>() {
			@Override
			public TableCell<WarenkorbElementFX, ImageView> call(TableColumn<WarenkorbElementFX, ImageView> param) {
				return new TableCell<>() {
					private final ImageView imageView = new ImageView();

					@Override
					protected void updateItem(ImageView item, boolean empty) {
						super.updateItem(item, empty);
						if (empty || getTableRow() == null) {
							setGraphic(null);
						} else {
							WarenkorbElementFX element = getTableRow().getItem();
							if (element != null && element.getArtikelBild() != null && element.getArtikelBild().length > 0) {
								ByteArrayInputStream bis = new ByteArrayInputStream(element.getArtikelBild());
								Image image = new Image(bis);
								imageView.setImage(image);
								imageView.setFitHeight(50);
								imageView.setFitWidth(50);
								setGraphic(imageView);
							} else {
								setGraphic(null);
							}
						}
					}
				};
			}
		});

		// Warenkorbdaten laden
		loadWarenkorbDaten();
	}


	// Lädt die Warenkorbdaten vom Server und füllt die 
	// Tabelle mit den Artikeln im Warenkorb.
	private void loadWarenkorbDaten() {
		try {
			BenutzerFX aktuellerBenutzerFX = BG_Client.getAktuellerBenutzer();
			if (aktuellerBenutzerFX instanceof KundeFX) {
				Kunde kunde = (Kunde) aktuellerBenutzerFX.getServerBenutzer();
				Warenkorb warenkorb = leseWarenkorb(kunde.getId());
				if (warenkorb != null) {
					List<WarenkorbElement> elemente = warenkorb.getElemente();
					ObservableList<WarenkorbElementFX> warenkorbElementeFX = FXCollections.observableArrayList();
					for (WarenkorbElement element : elemente) {
						if (element.getStatus() == 1) { // Nur Elemente mit Status 1 hinzufügen
							warenkorbElementeFX.add(new WarenkorbElementFX(element));
						}
					}
					warenkorbTabelle.setItems(warenkorbElementeFX);

					// Button deaktivieren, wenn der Warenkorb leer ist
					bestellenButton.setDisable(warenkorbElementeFX.isEmpty());

					aktualisiereGesamtbetrag();
				} else {
					// Warenkorb ist leer oder existiert nicht
					warenkorbTabelle.setItems(FXCollections.observableArrayList());


					bestellenButton.setDisable(true);


					gesamtbetragLabel.setText("0 €");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	// Berechnet den Gesamtbetrag aller Artikel im
	// Warenkorb und aktualisiert die Anzeige.
	private void aktualisiereGesamtbetrag() {
		int gesamtbetrag = 0;
		for (WarenkorbElementFX element : warenkorbTabelle.getItems()) {
			gesamtbetrag += element.getArtikelPreis() * element.getMenge();
		}
		gesamtbetragLabel.setText(gesamtbetrag + " Euro");
	}



	// Event-Handler für den “Entfernen”-Button. Verringert die Menge eines Artikels 
	// oder entfernt ihn vollständig aus dem Warenkorb.
	@FXML
	private void handleEntfernenButtonAction() {
		WarenkorbElementFX selectedElement = warenkorbTabelle.getSelectionModel().getSelectedItem();
		if (selectedElement != null) {
			try {
				// Aktuelle Menge abrufen
				int aktuelleMenge = selectedElement.getMenge();
				if (aktuelleMenge > 1) {
					// Menge um eins reduzieren
					int neueMenge = aktuelleMenge - 1;
					selectedElement.setMenge(neueMenge);

					// WarenkorbElement auf dem Server aktualisieren
					WarenkorbElement warenkorbElement = selectedElement.getServerWarenkorbElement();
					warenkorbElement.setMenge(neueMenge);
					bearbeitenWarenkorbArtikel(warenkorbElement);

					// Verfügbare Menge des Artikels um eins erhöhen
					Artikel artikel = warenkorbElement.getArtikel();
					artikel.setAnzahl(artikel.getAnzahl() + 1);
					// Artikel auf dem Server aktualisieren
					ProduktlisteController.bearbeitenArtikel(String.valueOf(artikel.getArtikelNummer()), artikel);

					// Tabelle aktualisieren
					warenkorbTabelle.refresh();
				} else {
					// Wenn die Menge 1 ist, das WarenkorbElement entfernen
					// WarenkorbElement vom Server löschen
					loeschenWarenkorbArtikel(String.valueOf(selectedElement.getId()));

					// Verfügbare Menge des Artikels um eins erhöhen
					Artikel artikel = selectedElement.getServerWarenkorbElement().getArtikel();
					artikel.setAnzahl(artikel.getAnzahl() + 1);
					// Artikel auf dem Server aktualisieren
					ProduktlisteController.bearbeitenArtikel(String.valueOf(artikel.getArtikelNummer()), artikel);

					// Element aus der Tabelle entfernen
					warenkorbTabelle.getItems().remove(selectedElement);
				}

				// Gesamtbetrag aktualisieren
				aktualisiereGesamtbetrag();

				// Artikeldaten in der Produktliste aktualisieren
				BG_Client.getProduktlisteController().loadArtikelDaten();

				// Bestellen-Button erneut überprüfen
				bestellenButton.setDisable(warenkorbTabelle.getItems().isEmpty());


			} catch (Exception e) {
				e.printStackTrace();
				showError("Fehler", "Es ist ein Fehler aufgetreten: " + e.getMessage());
			}
		} else {
			showError("Du hast leider keinen Artikel ausgewählt.", "Hey, keine Hose, kein Tshirt?...Hast du gar nichts ausgewählt?");
		}
	}
	
	



	// Event-Handler für den “Bestellen”-Button. Leitet den Bestellvorgang ein 
	// und aktualisiert den Status der Warenkorb-Elemente.
	@FXML
	private void handleBestellenButtonAction() {
		try {
			BenutzerFX aktuellerBenutzerFX = BG_Client.getAktuellerBenutzer();
			if (aktuellerBenutzerFX instanceof KundeFX) {
				Kunde kunde = (Kunde) aktuellerBenutzerFX.getServerBenutzer();

				// Bestellung durchführen
				bestellenWarenkorb(kunde.getId());



				// Erfolgsmeldung anzeigen
				Alert alert = new Alert(Alert.AlertType.INFORMATION);
				alert.setTitle("Bestellung erfolgreich");
				alert.setHeaderText(null);
				alert.setContentText("Ihre Bestellung wurde erfolgreich aufgegeben.");
				alert.showAndWait();

				// Dialog schließen
				Stage stage = (Stage) bestellenButton.getScene().getWindow();
				stage.close();


			}
		} catch (Exception e) {
			e.printStackTrace();
			showError("Fehler", "Es ist ein Fehler aufgetreten: " + e.getMessage());
		}
	}


	// Erstellt einen neuen Warenkorb für den Benutzer auf dem Server.
	@SuppressWarnings("exports")
	public static void neuerWarenkorb(Warenkorb warenkorb) throws Exception {
		String warenkorbXML = warenkorb.serializeXML();
		ServiceFunctions.post(WARENKORB_ENDPOINT, null, warenkorbXML, true);
	}


	// Liest den aktuellen Warenkorb eines Benutzers vom Server aus.
	@SuppressWarnings("exports")
	public static Warenkorb leseWarenkorb(int kundeId) throws Exception {
		String response = ServiceFunctions.get(WARENKORB_ENDPOINT, String.valueOf(kundeId), true); 
		if (response != null && !response.isEmpty()) {
			return new Warenkorb(response);
		} else {
			return null;
		}
	}


	// Überprüft, ob ein bestimmter Artikel bereits im Warenkorb ist.
	public static boolean isArtikelImWarenkorb(int artikelNummer) throws Exception {
		BenutzerFX aktuellerBenutzerFX = BG_Client.getAktuellerBenutzer();
		if (aktuellerBenutzerFX instanceof KundeFX) {
			Kunde kunde = (Kunde) aktuellerBenutzerFX.getServerBenutzer();
			Warenkorb warenkorb = leseWarenkorb(kunde.getId());
			if (warenkorb != null) {
				for (WarenkorbElement element : warenkorb.getElemente()) {
					if (element.getStatus() == 1 && element.getArtikel().getArtikelNummer() == artikelNummer) {
						return true;
					}
				}
			}
		}
		return false;
	}

	// Erhöht die Menge eines Artikels im Warenkorb um die angegebene Anzahl.
	public static void erhoeheWarenkorbMenge(int artikelNummer, int menge) throws Exception {
		BenutzerFX aktuellerBenutzerFX = BG_Client.getAktuellerBenutzer();
		if (aktuellerBenutzerFX instanceof KundeFX) {
			Kunde kunde = (Kunde) aktuellerBenutzerFX.getServerBenutzer();
			Warenkorb warenkorb = leseWarenkorb(kunde.getId());
			if (warenkorb != null) {
				for (WarenkorbElement element : warenkorb.getElemente()) {
					if (element.getStatus() == 1 && element.getArtikel().getArtikelNummer() == artikelNummer) {
						// Neue Menge berechnen
						int neueMenge = element.getMenge() + menge;
						element.setMenge(neueMenge);
						// Warenkorb-Element auf dem Server aktualisieren
						bearbeitenWarenkorbArtikel(element);
						return;
					}
				}
			} else {
				throw new Exception("Warenkorb nicht gefunden.");
			}
		} else {
			throw new Exception("Benutzer ist kein Kunde.");
		}
	}


	// Fügt ein neues Warenkorb-Element zum Warenkorb des Benutzers auf dem Server hinzu.
	@SuppressWarnings("exports")
	public static void neuerWarenkorbArtikel(WarenkorbElement warenkorbElement) throws Exception {
		String warenkorbElementXML = warenkorbElement.serializeXML();
		ServiceFunctions.post(WARENKORB_ARTIKEL_ENDPOINT, null, warenkorbElementXML, true);
	}


	// Liest alle Warenkorbartikel für einen bestimmten Warenkorb.
	// Die Methode sendet eine Anfrage an den Server, um die Artikel 
	// zu einem bestimmten Warenkorb abzurufen
	public static String leseWarenkorbArtikel(String warenkorbId) throws Exception {
		return ServiceFunctions.get(WARENKORB_ARTIKEL_ENDPOINT, warenkorbId, true);
	}



	// Liest alle Warenkörbe eines Kunden mit einem bestimmten Status.
	@SuppressWarnings("exports")
	public static List<Warenkorb> leseWarenkoerbeMitStatus(int kundeId, int status) throws Exception {
		String endpoint = WARENKORB_ENDPOINT + "/kunde/" + kundeId + "/status/" + status;
		String response = ServiceFunctions.get(endpoint, null, true);
		if (response != null && !response.isEmpty()) {
			WarenkorbList warenkorbList = new WarenkorbList(response);
			return warenkorbList.getWarenkoerbe();
		} else {
			return new ArrayList<>();
		}
	}


	// Aktualisiert ein bestehendes Warenkorb-Element auf dem Server.
	@SuppressWarnings("exports")
	public static void bearbeitenWarenkorbArtikel(WarenkorbElement element) throws Exception {
		String elementXML = element.serializeXML();
		ServiceFunctions.put(WARENKORB_ARTIKEL_ENDPOINT, String.valueOf(element.getId()), elementXML, true);
	}


	// Löscht ein Warenkorb-Element vom Server.
	public static void loeschenWarenkorbArtikel(String warenkorbArtikelId) throws Exception {
		ServiceFunctions.delete(WARENKORB_ARTIKEL_ENDPOINT, warenkorbArtikelId, true);
	}


	// Führt die Bestellung des gesamten Warenkorbs durch 
	// und aktualisiert den Status auf dem Server.
	public static void bestellenWarenkorb(int kundeId) throws Exception {
		Warenkorb warenkorb = leseWarenkorb(kundeId);
		if (warenkorb != null) {
			for (WarenkorbElement element : warenkorb.getElemente()) {
				// Status des Warenkorb-Elements auf "bestellt" setzen
				element.setStatus(2);
				bearbeitenWarenkorbArtikel(element);
			}
			// Warenkorb auf abgeschlossen setzen
			ServiceFunctions.put("warenkorb", String.valueOf(warenkorb.getId()), "<status>2</status>", true);

		} else {
			throw new Exception("Warenkorb nicht gefunden.");
		}
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
