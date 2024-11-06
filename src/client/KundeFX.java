package client;

import java.time.LocalDate;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import klassen.Kunde;

public class KundeFX extends BenutzerFX {
	private SimpleObjectProperty<LocalDate> registrierungsdatum;

	@SuppressWarnings("exports")
	public KundeFX(Kunde kunde) {
		super(kunde); // Aufruf des Konstruktors der Superklasse
		this.registrierungsdatum = new SimpleObjectProperty<>(kunde.getRegistrierungsdatum());
	}

	@SuppressWarnings("exports")
	@Override
	public Kunde getServerBenutzer() {
		return (Kunde) super.getServerBenutzer();
	}

	public int getId() {
		return id.get();
	}

	public void setId(int id) {
		this.id.set(id);
		getServerBenutzer().setId(id);
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
		getServerBenutzer().setName(name);
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
		getServerBenutzer().setPasswort(passwort);
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
		getServerBenutzer().setEmail(email);
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
		getServerBenutzer().setTelNummer(telNummer);
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
		getServerBenutzer().setAdresse(adresse);
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
		getServerBenutzer().setPlz(plz);
	}

	@SuppressWarnings("exports")
	public SimpleStringProperty plzProperty() {
		return plz;
	}

	public LocalDate getRegistrierungsdatum() {
		return registrierungsdatum.get();
	}

	public void setRegistrierungsdatum(LocalDate registrierungsdatum) {
		this.registrierungsdatum.set(registrierungsdatum);
		getServerBenutzer().setRegistrierungsdatum(registrierungsdatum);
	}

	public SimpleObjectProperty<LocalDate> registrierungsdatumProperty() {
		return registrierungsdatum;
	}
}