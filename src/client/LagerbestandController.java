package client;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import klassen.Artikel;
import klassen.ArtikelList;
import javafx.scene.control.TableCell;
import javafx.scene.image.Image;
import javafx.embed.swing.SwingFXUtils;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.ArrayList;

public class LagerbestandController {

	private static final String ARTIKEL_LIST_ENDPOINT = "alleartikelliste";

	@FXML
	private TableView<ArtikelFX> lagerbestandTabelle;

	@FXML
	private TableColumn<ArtikelFX, ArtikelFX> lagerbestandBildColumn;

	@FXML
	private TableColumn<ArtikelFX, Integer> lagerbestandIdColumn;

	@FXML
	private TableColumn<ArtikelFX, String> lagerbestandNameColumn;

	@FXML
	private TableColumn<ArtikelFX, String> lagerbestandBeschreibungColumn;

	@FXML
	private TableColumn<ArtikelFX, String> lagerbestandGroesseColumn;

	@FXML
	private TableColumn<ArtikelFX, Integer> lagerbestandPreisColumn;

	@FXML
	private TableColumn<ArtikelFX, Integer> lagerbestandMengeColumn;

	@FXML
	private ComboBox<String> lagerbestandCombobox;

	@FXML
	private Label gesamtanzahlLabel;

	@FXML
	private Button zurueckButton;

	private ObservableList<ArtikelFX> artikelListe;


	// Initialisiert den LagerbestandController, konfiguriert 
	// die Tabelle und lädt die Artikeldaten.
	@FXML
	private void initialize() {
		// Initialisiert die ComboBox
		lagerbestandCombobox.setItems(FXCollections.observableArrayList("Alle Artikel", "Lagernde Artikel", "Nicht lagernde Artikel"));
		lagerbestandCombobox.getSelectionModel().selectFirst();

		// Fügt einen listener zur ComboBox hinzu
		lagerbestandCombobox.setOnAction(event -> filterArtikelListe());

		// Initialisiert die Spalten der TableView 
		lagerbestandIdColumn.setCellValueFactory(cellData -> cellData.getValue().artikelNummerProperty().asObject());
		lagerbestandNameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
		lagerbestandBeschreibungColumn.setCellValueFactory(cellData -> cellData.getValue().beschreibungProperty());
		lagerbestandGroesseColumn.setCellValueFactory(cellData -> cellData.getValue().groesseProperty());
		lagerbestandPreisColumn.setCellValueFactory(cellData -> cellData.getValue().preisProperty().asObject());
		lagerbestandMengeColumn.setCellValueFactory(cellData -> cellData.getValue().anzahlProperty().asObject());

		lagerbestandBildColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue()));

		lagerbestandBildColumn.setCellFactory(column -> new TableCell<ArtikelFX, ArtikelFX>() {
			private final ImageView imageView = new ImageView();

			@Override
			protected void updateItem(ArtikelFX item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setGraphic(null);
				} else {
					byte[] bildDaten = item.getBild();
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
								setGraphic(null);
							}
						} catch (Exception e) {
							e.printStackTrace();
							setGraphic(null);
						}
					} else {
						setGraphic(null);
					}
				}
			}
		});


		// lädt die Daten
		loadArtikelDaten();
	}


	// Lädt alle Artikeldaten, einschließlich deaktivierter Artikel, vom Server.
	private void loadArtikelDaten() {
		try {
			String response = getAlleArtikel(); // Neue Methode für den neuen Endpoint
			ArtikelList artikelList = new ArtikelList(response);
			List<Artikel> artikelListeFromServer = artikelList.getArtikel();

			// Konvertieren von Artikel zu ArtikelFX
			List<ArtikelFX> artikelFXListe = new ArrayList<>();
			for (Artikel artikel : artikelListeFromServer) {
				artikelFXListe.add(new ArtikelFX(artikel));
			}

			artikelListe = FXCollections.observableArrayList(artikelFXListe);
			// Filtern entsprechend der Auswahl
			filterArtikelListe();

		} catch (Exception e) {
			e.printStackTrace();
			showError("Fehler beim Laden der Artikeldaten", e.getMessage());
		}
	}


	// Ruft die komplette Artikelliste vom Server ab.
	public static String getAlleArtikel() {
		try {
			String response = ServiceFunctions.get(ARTIKEL_LIST_ENDPOINT, null, true); // Authentifizierung erforderlich
			System.out.println("Server-Response:\n" + response); 
			return response;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}


	// Filtert die Artikelliste basierend auf der Auswahl 
	// in Combobox (z.B. “Alle Artikel”, “Lagernde Artikel”).
	private void filterArtikelListe() {
		String filterOption = lagerbestandCombobox.getValue();
		List<ArtikelFX> filteredList = new ArrayList<>();

		if ("Alle Artikel".equals(filterOption)) {
			filteredList = new ArrayList<>(artikelListe);
		} else if ("Lagernde Artikel".equals(filterOption)) {
			for (ArtikelFX artikelFX : artikelListe) {
				if (artikelFX.getAnzahl() > 0) {
					filteredList.add(artikelFX);
				}
			}
		} else if ("Nicht lagernde Artikel".equals(filterOption)) {
			for (ArtikelFX artikelFX : artikelListe) {
				if (artikelFX.getAnzahl() <= 0) {
					filteredList.add(artikelFX);
				}
			}
		}

		lagerbestandTabelle.setItems(FXCollections.observableArrayList(filteredList));

		// Update gesamtanzahlLabel
		int gesamtAnzahl = 0;
		for (ArtikelFX artikelFX : filteredList) {
			gesamtAnzahl += artikelFX.getAnzahl();
		}
		gesamtanzahlLabel.setText(String.valueOf(gesamtAnzahl));
	}


	// Event-Handler für den “Zurück”-Button. Schließt die Lagerbestandsansicht.
	@FXML
	private void handleZurueckButtonAction() {
		// Aktuelles Fenster schließen
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