package client;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import klassen.Benutzer;

public class BenutzerFX {
	protected Benutzer serverBenutzer; // Das Server-Objekt, das von dieser FX-Klasse repräsentiert wird
	protected SimpleIntegerProperty id;
	protected SimpleStringProperty name;
	protected SimpleStringProperty passwort;
	protected SimpleStringProperty email;
	protected SimpleStringProperty telNummer;
	protected SimpleStringProperty adresse;
	protected SimpleStringProperty plz;

	@SuppressWarnings("exports")
	public BenutzerFX(Benutzer benutzer) {
		this.serverBenutzer = benutzer;
		this.id = new SimpleIntegerProperty(benutzer.getId());
		this.name = new SimpleStringProperty(benutzer.getName());
		this.passwort = new SimpleStringProperty(benutzer.getPasswort());
		this.email = new SimpleStringProperty(benutzer.getEmail());
		this.telNummer = new SimpleStringProperty(benutzer.getTelNummer());
		this.adresse = new SimpleStringProperty(benutzer.getAdresse());
		this.plz = new SimpleStringProperty(benutzer.getPlz());
	}

	@SuppressWarnings("exports")
	public Benutzer getServerBenutzer() {
		return serverBenutzer;
	}

	public int getId() {
		return id.get();
	}

	public void setId(int id) {
		this.id.set(id);
		serverBenutzer.setId(id);
	}

	@SuppressWarnings("exports")
	public SimpleIntegerProperty idProperty() {
		return id;
	}

	public String getName() {
		return name.get();
	}

	public void setName(String name) {
		this.name.set(name);
		serverBenutzer.setName(name);
	}

	@SuppressWarnings("exports")
	public SimpleStringProperty nameProperty() {
		return name;
	}

	public String getPasswort() {
		return passwort.get();
	}

	public void setPasswort(String passwort) {
		this.passwort.set(passwort);
		serverBenutzer.setPasswort(passwort);
	}

	@SuppressWarnings("exports")
	public SimpleStringProperty passwortProperty() {
		return passwort;
	}

	public String getEmail() {
		return email.get();
	}

	public void setEmail(String email) {
		this.email.set(email);
		serverBenutzer.setEmail(email);
	}

	@SuppressWarnings("exports")
	public SimpleStringProperty emailProperty() {
		return email;
	}

	public String getTelNummer() {
		return telNummer.get();
	}

	public void setTelNummer(String telNummer) {
		this.telNummer.set(telNummer);
		serverBenutzer.setTelNummer(telNummer);
	}

	@SuppressWarnings("exports")
	public SimpleStringProperty telNummerProperty() {
		return telNummer;
	}

	public String getAdresse() {
		return adresse.get();
	}

	public void setAdresse(String adresse) {
		this.adresse.set(adresse);
		serverBenutzer.setAdresse(adresse);
	}

	@SuppressWarnings("exports")
	public SimpleStringProperty adresseProperty() {
		return adresse;
	}

	public String getPlz() {
		return plz.get();
	}

	public void setPlz(String plz) {
		this.plz.set(plz);
		serverBenutzer.setPlz(plz);
	}

	@SuppressWarnings("exports")
	public SimpleStringProperty plzProperty() {
		return plz;
	}
}