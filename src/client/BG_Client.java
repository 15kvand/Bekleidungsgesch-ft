package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class BG_Client extends Application {

	private static Stage primaryStage;
	private static BenutzerFX aktuellerBenutzerFX; // Aktueller BenutzerFX
	private static String benutzerTyp; // "Kunde" oder "Mitarbeiter"
	private static String benutzerRolle; // ADMIN oder MITARBEITER

	private static ProduktlisteController produktlisteController;


	// Setzt den ProduktlisteController, damit andere Teile der 
	// Anwendung auf diesen zugreifen können.
	public static void setProduktlisteController(ProduktlisteController controller) {
		produktlisteController = controller;
	}


	// Gibt den aktuell gesetzten ProduktlisteController zurück.
	// Dies wird verwendet, um die Produktliste in verschiedenen Szenen zu verwalten.
	public static ProduktlisteController getProduktlisteController() {
		return produktlisteController;
	}


	// Startmethode der JavaFX-Anwendung. 
	// Initialisiert die Hauptbühne und öffnet die Anmeldeseite.
	@SuppressWarnings("exports")
	@Override
	public void start(Stage primaryStage) throws Exception {
		BG_Client.primaryStage = primaryStage;
		primaryStage.setTitle("Bekleidungsgeschäft");
		switchToLoginScene();  // Start mit Anmeldeseite
	}


	// Setzt den aktuell angemeldeten Benutzer und dessen Typ (Kunde oder Mitarbeiter).
	public static void setAktuellerBenutzer(BenutzerFX benutzerFX, String typ) {
		aktuellerBenutzerFX = benutzerFX;
		benutzerTyp = typ;
		if (benutzerFX instanceof MitarbeiterFX) {
			benutzerRolle = ((MitarbeiterFX) benutzerFX).getRolle();
		} else {
			benutzerRolle = null;
		}
	}


	// Gibt den aktuell angemeldeten Benutzer zurück.
	// Dieser Benutzer wird für die Authentifizierung und rollenbasierte Zugriffe verwendet
	public static BenutzerFX getAktuellerBenutzer() {
		return aktuellerBenutzerFX;
	}


	// Gibt den Benutzertyp ("Kunde" oder "Mitarbeiter") des aktuell angemeldeten Benutzers zurück.
	// Dies hilft, die Benutzeroberfläche und die Berechtigungen dynamisch zu steuern.
	public static String getBenutzerTyp() {
		return benutzerTyp;
	}


	// Gibt die Rolle des angemeldeten Benutzers zurück, falls es ein Mitarbeiter ist (z.B. "ADMIN", "MITARBEITER").
	// Diese Rolle steuert den Zugriff auf bestimmte Funktionen.
	public static String getBenutzerRolle() {
		return benutzerRolle;
	}


	// Wechselt die Szene zur angegebenen FXML-Datei.
	// Diese Methode wird verwendet, um zwischen verschiedenen GUI-Ansichten zu wechseln.
	public static void switchScene(String fxmlFile) {
		try {
			FXMLLoader loader = new FXMLLoader(BG_Client.class.getResource(fxmlFile));
			Parent root = loader.load();
			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Wechselt zur Hauptmenüszene.
	public static void switchToMenuScene() {
		switchScene("MenüSeite.fxml");
	}

	// Wechselt zur Anmeldeszene.
	public static void switchToLoginScene() {
		switchScene("AnmeldenSeite.fxml");
	}

	// Öffnet die Benutzerdatenverwaltung als modalen Dialog.
	public static void switchToBenutzerDatenVerwaltungScene() {
		showModal("BenutzerdatenverwaltungSeite.fxml", "Benutzerdatenverwaltung");
	}

	// Öffnet die Produktliste als modalen Dialog.
	public static void switchToProduktListeScene() {
		showModal("ProduktlisteSeite.fxml", "Produktliste");
	}

	// Öffnet das Artikeldetailsformular zum Hinzufügen oder Bearbeiten eines Artikels.
	public static void switchToArtikelDetailsScene(ArtikelFX artikelFX) {
		try {
			FXMLLoader loader = new FXMLLoader(BG_Client.class.getResource("ArtikeldetailsSeite.fxml"));
			Parent root = loader.load();

			// Controller holen und ArtikelFX setzen
			ArtikeldetailsController controller = loader.getController();
			controller.setArtikel(artikelFX);

			Stage dialog = new Stage();
			dialog.setTitle("Artikeldetails");
			dialog.initModality(Modality.APPLICATION_MODAL);
			dialog.initOwner(primaryStage);
			Scene scene = new Scene(root);
			dialog.setScene(scene);
			dialog.showAndWait();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Öffnet das Mitarbeiterdetailsformular zum Hinzufügen 
	// oder Bearbeiten eines Mitarbeiters.
	public static void switchToMitarbeiterDetailsScene(MitarbeiterFX mitarbeiterFX) {
		try {
			FXMLLoader loader = new FXMLLoader(BG_Client.class.getResource("MitarbeiterdetailsSeite.fxml"));
			Parent root = loader.load();

			// Controller holen und MitarbeiterFX setzen
			MitarbeiterdetailsController controller = loader.getController();
			controller.setMitarbeiter(mitarbeiterFX);

			Stage dialog = new Stage();
			dialog.setTitle("Mitarbeiterdetails");
			dialog.initModality(Modality.APPLICATION_MODAL);
			dialog.initOwner(primaryStage);
			Scene scene = new Scene(root);
			dialog.setScene(scene);
			dialog.showAndWait();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Öffnet die Verkaufsmanagementansicht.
	public static void switchToVerkaufsManagementScene() {
		showModal("VerkaufsmanagementSeite.fxml", "Verkaufsmanagement");
	}

	// Öffnet die Verwaltungsansicht.
	public static void switchToVerwaltungScene() {
		showModal("VerwaltungSeite.fxml", "Verwaltung");
	}

	// Öffnet die Lagerbestandsansicht.
	public static void switchToLagerbestandScene() {
		showModal("LagerbestandSeite.fxml", "Lagerbestand");
	}

	// Öffnet die Verkaufsstatistikansicht.
	public static void switchToVerkaufsstatistikScene() {
		showModal("VerkaufsstatistikSeite.fxml", "Verkaufsstatistik");
	}

	// Öffnet die Warenkorbansicht.
	public static void switchToWarenkorbScene() {
		showModal("WarenkorbSeite.fxml", "Warenkorb");
	}

	// Methode zur Anzeige eines modalen Dialogs
	private static void showModal(String fxmlFile, String title) {
		try {
			FXMLLoader loader = new FXMLLoader(BG_Client.class.getResource(fxmlFile));
			Parent root = loader.load();
			Stage dialog = new Stage();
			dialog.setTitle(title);
			dialog.initModality(Modality.APPLICATION_MODAL);
			dialog.initOwner(primaryStage);
			Scene scene = new Scene(root);
			dialog.setScene(scene);
			dialog.showAndWait();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}