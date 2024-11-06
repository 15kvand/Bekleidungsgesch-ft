package client;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import klassen.Mitarbeiter;

public class MitarbeiterFX extends BenutzerFX {
    private SimpleStringProperty iban;
    private SimpleIntegerProperty gehalt;
    private SimpleStringProperty rolle;

    @SuppressWarnings("exports")
	public MitarbeiterFX(Mitarbeiter mitarbeiter) {
        super(mitarbeiter); // Aufruf des Konstruktors der Superklasse
        this.iban = new SimpleStringProperty(mitarbeiter.getIban());
        this.gehalt = new SimpleIntegerProperty(mitarbeiter.getGehalt());
        this.rolle = new SimpleStringProperty(mitarbeiter.getRolle());
    }

    @SuppressWarnings("exports")
	@Override
    public Mitarbeiter getServerBenutzer() {
        return (Mitarbeiter) super.getServerBenutzer();
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

    public String getIban() {
        return iban.get();
    }

    public void setIban(String iban) {
        this.iban.set(iban);
        getServerBenutzer().setIban(iban);
    }

    @SuppressWarnings("exports")
	public SimpleStringProperty ibanProperty() {
        return iban;
    }

    public int getGehalt() {
        return gehalt.get();
    }

    public void setGehalt(int gehalt) {
        this.gehalt.set(gehalt);
        getServerBenutzer().setGehalt(gehalt);
    }

    @SuppressWarnings("exports")
	public SimpleIntegerProperty gehaltProperty() {
        return gehalt;
    }

    public String getRolle() {
        return rolle.get();
    }

    public void setRolle(String rolle) {
        this.rolle.set(rolle);
        getServerBenutzer().setRolle(rolle);
    }

    @SuppressWarnings("exports")
	public SimpleStringProperty rolleProperty() {
        return rolle;
    }
}