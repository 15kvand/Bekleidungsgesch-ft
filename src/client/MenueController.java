package client;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class MenueController {

	@FXML
	private Button produktListeButton;

	@FXML
	private Button benutzerDatenButton;

	@FXML
	private Button warenkorbButton;

	@FXML
	private Button verkaufsManagementButton;

	@FXML
	private Button verwaltungButton;


	// Initialisiert das Menü und passt die sichtbaren Buttons entsprechend 
	// der Benutzerrolle an.
	@FXML
	private void initialize() {
		String benutzerTyp = BG_Client.getBenutzerTyp();
		String benutzerRolle = BG_Client.getBenutzerRolle();

		if ("Kunde".equals(benutzerTyp)) {
			verkaufsManagementButton.setVisible(false);
			verwaltungButton.setVisible(false);
			benutzerDatenButton.setVisible(true);
			produktListeButton.setVisible(true);
			warenkorbButton.setVisible(true);

		} else if ("Mitarbeiter".equals(benutzerTyp)) {
			warenkorbButton.setVisible(false);
			verwaltungButton.setVisible(false);
			produktListeButton.setVisible(true);
			verkaufsManagementButton.setVisible(true);
			benutzerDatenButton.setVisible(true);

			if ("ADMIN".equals(benutzerRolle)) {
				warenkorbButton.setVisible(false);
				benutzerDatenButton.setVisible(false);
				verkaufsManagementButton.setVisible(true);
				verwaltungButton.setVisible(true);
				produktListeButton.setVisible(true);
			}
		}
	}


	// Event-Handler für den “Produktliste”-Button. Öffnet die Produktliste.
	@FXML
	private void handleProduktListeButtonAction() {
		BG_Client.switchToProduktListeScene();
	}


	// Event-Handler für den “Benutzerdaten”-Button. Öffnet die Benutzerdatenverwaltung.
	@FXML
	private void handleBenutzerDatenButtonAction() {
		BG_Client.switchToBenutzerDatenVerwaltungScene();
	}


	// Event-Handler für den “Warenkorb”-Button. Öffnet den Warenkorb.
	@FXML
	private void handleWarenkorbButtonAction() {
		BG_Client.switchToWarenkorbScene();
	}


	// Event-Handler für den “Verkaufsmanagement”-Button. Öffnet die 
	// Verkaufsmanagementansicht.
	@FXML
	private void handleVerkaufsManagementButtonAction() {
		BG_Client.switchToVerkaufsManagementScene();
	}


	// Event-Handler für den “Verwaltung”-Button. Öffnet die Verwaltungsansicht.
	@FXML
	private void handleVerwaltungButtonAction() {
		BG_Client.switchToVerwaltungScene();
	}
}