package client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Callback;
import klassen.Artikel;
import klassen.ArtikelList;
import klassen.WarenkorbElement;
import javafx.scene.control.TableCell;
import javafx.scene.control.Alert;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.imageio.ImageIO;

public class ProduktlisteController {

	private static final String ARTIKEL_ENDPOINT = "artikel";
	private static final String ARTIKEL_LIST_ENDPOINT = "artikelliste";


	@FXML
	private TableView<ArtikelFX> produktListeTabelle;

	@FXML
	private TableColumn<ArtikelFX, ImageView> produktBildColumn;

	@FXML
	private TableColumn<ArtikelFX, Integer> produktIdColumn;

	@FXML
	private TableColumn<ArtikelFX, String> produktNameColumn;

	@FXML
	private TableColumn<ArtikelFX, String> produktBeschreibungColumn;

	@FXML
	private TableColumn<ArtikelFX, String> produktGroesseColumn;

	@FXML
	private TableColumn<ArtikelFX, Integer> produktPreisColumn;

	@FXML
	private Button inDenKorbLegenButton;

	@FXML
	private Button zurKasseGehenButton;

	@FXML
	private Button hinzufuegenButton;

	@FXML
	private Button bearbeitenButton;

	@FXML
	private Button entfernenButton;

	@FXML
	private Button zurueckButton;

	private ObservableList<ArtikelFX> produktListe;

	private Stage dialogStage;


	// Initialisiert den ProduktlisteController, konfiguriert die Tabellen
	// und prüft die Benutzerrolle, um die Sichtbarkeit der Buttons zu setzen.
	@FXML
	private void initialize() throws IOException, URISyntaxException {	

		BG_Client.setProduktlisteController(this);


		produktIdColumn.setCellValueFactory(cellData -> cellData.getValue().artikelNummerProperty().asObject());
		produktNameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
		produktBeschreibungColumn.setCellValueFactory(cellData -> cellData.getValue().beschreibungProperty());
		produktGroesseColumn.setCellValueFactory(cellData -> cellData.getValue().groesseProperty());
		produktPreisColumn.setCellValueFactory(cellData -> cellData.getValue().preisProperty().asObject());

		produktBildColumn.setCellFactory(new Callback<TableColumn<ArtikelFX, ImageView>, TableCell<ArtikelFX, ImageView>>() {
			@Override
			public TableCell<ArtikelFX, ImageView> call(TableColumn<ArtikelFX, ImageView> param) {
				return new TableCell<ArtikelFX, ImageView>() {
					private final ImageView imageView = new ImageView();

					@Override
					protected void updateItem(ImageView imageViewItem, boolean empty) {
						super.updateItem(imageViewItem, empty);
						if (empty) {
							setGraphic(null);
						} else {
							ArtikelFX artikelFX = getTableView().getItems().get(getIndex());
							byte[] bildDaten = artikelFX.getBild();

							if (bildDaten != null && bildDaten.length > 0) {
								try {
									ByteArrayInputStream bis = new ByteArrayInputStream(bildDaten);
									BufferedImage bufferedImage = ImageIO.read(bis);
									if (bufferedImage != null) {
										Image image = SwingFXUtils.toFXImage(bufferedImage, null);
										imageView.setImage(image);
										imageView.setFitWidth(100);
										imageView.setFitHeight(100);
										imageView.setPreserveRatio(true);
										setGraphic(imageView);
									} else {
										System.err.println("Fehler beim Laden des Bildes für Artikel " + artikelFX.getName());
										setGraphic(null);
									}
								} catch (Exception e) {
									System.err.println("Fehler beim Laden des Bildes: " + e.getMessage());
									setGraphic(null);
								}
							} else {
								setGraphic(null);
							}
						}
					}
				};
			}
		});

		String benutzerTyp = BG_Client.getBenutzerTyp();
		String benutzerRolle = BG_Client.getBenutzerRolle();

		if ("Kunde".equals(benutzerTyp)) {
			hinzufuegenButton.setVisible(false);
			bearbeitenButton.setVisible(false);
			entfernenButton.setVisible(false);
			inDenKorbLegenButton.setVisible(true);
			zurKasseGehenButton.setVisible(true);
		} else if ("Mitarbeiter".equals(benutzerTyp) || ("ADMIN".equals(benutzerRolle))) {
			hinzufuegenButton.setVisible(true);
			bearbeitenButton.setVisible(true);
			entfernenButton.setVisible(true);
			inDenKorbLegenButton.setVisible(false);
			zurKasseGehenButton.setVisible(false);
		}

		loadArtikelDaten();
	}


	// Lädt die Artikeldaten vom Server und füllt die Produktliste.
	public void loadArtikelDaten() {
		try {
			String response = getArtikel();
			ArtikelList artikelList = new ArtikelList(response);
			List<Artikel> artikelListe = artikelList.getArtikel();

			// Konvertieren Sie Artikel zu ArtikelFX
			List<ArtikelFX> artikelFXListe = new ArrayList<>();
			for (Artikel artikel : artikelListe) {
				artikelFXListe.add(new ArtikelFX(artikel));
			}

			produktListe = FXCollections.observableArrayList(artikelFXListe);
			produktListeTabelle.setItems(produktListe);
		} catch (Exception e) {
			e.printStackTrace();
			showError("Fehler beim Laden der Artikeldaten", e.getMessage());
		}
	}


	// Event-Handler für den “Zurück”-Button. Schließt die Produktliste 
	// und kehrt zum Hauptmenü zurück.
	@FXML
	private void handleZurueckButtonAction() {
		dialogStage = (Stage) produktListeTabelle.getScene().getWindow();
		dialogStage.close();
		BG_Client.switchToMenuScene();
	}


	// Event-Handler für den “Hinzufügen”-Button. Öffnet das 
	// Formular zum Erstellen eines neuen Artikels.
	@FXML
	private void handleHinzufuegenButtonAction() {
		BG_Client.switchToArtikelDetailsScene(null); // null  für neuen Artikel übergeben
		// nach dem Hinzufügen die Artikelliste aktualisieren
		loadArtikelDaten();
	}


	// Event-Handler für den “Bearbeiten”-Button. Öffnet das Formular zum 
	// Bearbeiten des ausgewählten Artikels.
	@SuppressWarnings("unused")
	@FXML
	private void handleBearbeitenButtonAction() {
		ArtikelFX selectedArtikelFX = produktListeTabelle.getSelectionModel().getSelectedItem();
		if (selectedArtikelFX != null) {
			Artikel selectedArtikel = selectedArtikelFX.getServerArtikel();
			BG_Client.switchToArtikelDetailsScene(selectedArtikelFX);
			// nach dem Bearbeiten die Artikelliste aktualisieren
			loadArtikelDaten();
		} else {
			showError("Kein Artikel ausgewählt", "Bitte wählen Sie einen Artikel zum Bearbeiten aus.");
		}
	}


	// Event-Handler für den “Entfernen”-Button. Entfernt den 
	// ausgewählten Artikel nach Bestätigung.
	@FXML
	private void handleEntfernenButtonAction() {
		ArtikelFX selectedArtikelFX = produktListeTabelle.getSelectionModel().getSelectedItem();
		if (selectedArtikelFX != null) {
			Artikel selectedArtikel = selectedArtikelFX.getServerArtikel();

			// Bestätigungsdialog anzeigen
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
			alert.setTitle("Artikel entfernen");
			alert.setHeaderText("Sind Sie sicher, dass Sie den Artikel entfernen möchten?");
			alert.setContentText("Artikel: " + selectedArtikel.getName());
			Optional<ButtonType> result = alert.showAndWait();
			if (result.isPresent() && result.get() == ButtonType.OK) {
				try {
					// DELETE-Anfrage an den Server senden
					loeschenArtikel(String.valueOf(selectedArtikel.getArtikelNummer()));
					// Artikel aus der Tabelle entfernen
					produktListe.remove(selectedArtikelFX);
				} catch (Exception e) {
					e.printStackTrace();
					showError("Fehler beim Entfernen des Artikels", e.getMessage());
				}
			}
		} else {
			showError("Kein Artikel ausgewählt", "Bitte wählen Sie einen Artikel zum Entfernen aus.");
		}
	}


	// Event-Handler für den “In den Korb legen”-Button. Fügt den 
	// ausgewählten Artikel dem Warenkorb hinzu.
	@FXML
	private void handleInDenKorbLegenButtonAction() {
		ArtikelFX selectedArtikelFX = produktListeTabelle.getSelectionModel().getSelectedItem();
		if (selectedArtikelFX != null) {
			try {
				// Artikel vom Server abrufen, um aktuelle Daten zu erhalten
				Artikel selectedArtikel = selectedArtikelFX.getServerArtikel();
				String artikelResponse = ServiceFunctions.get(ARTIKEL_ENDPOINT, String.valueOf(selectedArtikel.getArtikelNummer()), true);
				Artikel serverArtikel = new Artikel(artikelResponse);

				int gewuenschteMenge = 1;

				// Überprüfen, ob genügend Bestand vorhanden ist
				if (serverArtikel.getAnzahl() >= gewuenschteMenge) {
					// Artikel zum Warenkorb hinzufügen
					if (WarenkorbController.isArtikelImWarenkorb(serverArtikel.getArtikelNummer())) {
						// Menge im Warenkorb erhöhen
						WarenkorbController.erhoeheWarenkorbMenge(serverArtikel.getArtikelNummer(), gewuenschteMenge);
					} else {
						// Neues Warenkorb-Element erstellen und hinzufügen
						WarenkorbElement warenkorbElement = new WarenkorbElement();
						warenkorbElement.setArtikel(serverArtikel);
						warenkorbElement.setMenge(gewuenschteMenge);
						warenkorbElement.setStatus(1);

						// Warenkorb-Element zum Server hinzufügen
						WarenkorbController.neuerWarenkorbArtikel(warenkorbElement);
					}

					// Verfügbare Menge des Artikels reduzieren
					serverArtikel.setAnzahl(serverArtikel.getAnzahl() - gewuenschteMenge);
					// Artikel auf dem Server aktualisieren
					bearbeitenArtikel(String.valueOf(serverArtikel.getArtikelNummer()), serverArtikel);

					// Artikelliste aktualisieren
					loadArtikelDaten();

					// Erfolgsmeldung anzeigen
					Alert alert = new Alert(Alert.AlertType.INFORMATION);
					alert.setTitle("Artikel hinzugefügt");
					alert.setHeaderText(null);
					alert.setContentText("Der Artikel wurde erfolgreich zum Warenkorb hinzugefügt.");
					alert.showAndWait();

				} else {
					showError("Nicht genügend Bestand", "Es sind nur noch " + serverArtikel.getAnzahl() + " Stück verfügbar.");
				}

			} catch (Exception e) {
				e.printStackTrace();
				showError("Fehler", "Es ist ein Fehler aufgetreten: " + e.getMessage());
			}
		} else {
			showError("Kein Artikel ausgewählt", "Bitte wählen Sie einen Artikel aus der Tabelle aus.");
		}
	}


	// Event-Handler für den “Zur Kasse gehen”-Button. Öffnet die Warenkorbansicht.
	@FXML
	private void handleZurKasseGehenButtonAction() {
		try {
			// zu Warenkorb-Szene wechslen
			BG_Client.switchToWarenkorbScene();
		} catch (Exception e) {
			e.printStackTrace();
			showError("Fehler", "Es ist ein Fehler beim Wechsel zur Warenkorb-Szene aufgetreten: " + e.getMessage());
		}
	}


	// Erstellt einen neuen Artikel auf dem Server.
	@SuppressWarnings("exports")
	public static void neuerArtikel(Artikel artikel) {
		try {
			String artikelXML = artikel.serializeXML();
			ServiceFunctions.post(ARTIKEL_ENDPOINT, null, artikelXML, true);
		} catch (Exception e) {
			e.printStackTrace();
			// Fehlerbehandlung
		}
	}


	// Aktualisiert einen bestehenden Artikel auf dem Server.
	@SuppressWarnings("exports")
	public static void bearbeitenArtikel(String artikelId, Artikel artikel) throws Exception {
		String artikelXML = artikel.serializeXML();
		ServiceFunctions.put(ARTIKEL_ENDPOINT, artikelId, artikelXML, true);
	}


	// Löscht einen Artikel vom Server.
	public static void loeschenArtikel(String artikelId) throws Exception {
		ServiceFunctions.delete(ARTIKEL_ENDPOINT, artikelId, true);
	}


	// Ruft die Artikelliste vom Server ab.
	public static String getArtikel() {
		try {
			String response = ServiceFunctions.get(ARTIKEL_LIST_ENDPOINT, null, false);
			System.out.println("Server-Response:\n" + response); 
			return response;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
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
}