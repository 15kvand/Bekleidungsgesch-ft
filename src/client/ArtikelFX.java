package client;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import klassen.Artikel;

public class ArtikelFX {
	private Artikel serverArtikel;
	private SimpleIntegerProperty artikelNummer;
	private SimpleStringProperty name;
	private SimpleStringProperty beschreibung;
	private SimpleIntegerProperty preis;
	private SimpleStringProperty groesse;
	private SimpleObjectProperty<byte[]> bild;
	private SimpleIntegerProperty anzahl;
	private SimpleBooleanProperty deaktiv;

	@SuppressWarnings("exports")
	public ArtikelFX(Artikel artikel) {
		this.serverArtikel = artikel;
		this.artikelNummer = new SimpleIntegerProperty(artikel.getArtikelNummer());
		this.name = new SimpleStringProperty(artikel.getName());
		this.beschreibung = new SimpleStringProperty(artikel.getBeschreibung());
		this.preis = new SimpleIntegerProperty(artikel.getPreis());
		this.groesse = new SimpleStringProperty(artikel.getGroesse());
		this.bild = new SimpleObjectProperty<>(artikel.getBild());
		this.anzahl = new SimpleIntegerProperty(artikel.getAnzahl());
		this.deaktiv = new SimpleBooleanProperty(artikel.isDeaktiv());
	}

	@SuppressWarnings("exports")
	public Artikel getServerArtikel() {
		return serverArtikel;
	}

	public int getArtikelNummer() {
		return artikelNummer.get();
	}

	public void setArtikelNummer(int artikelNummer) {
		this.artikelNummer.set(artikelNummer);
		serverArtikel.setArtikelNummer(artikelNummer);
	}

	@SuppressWarnings("exports")
	public SimpleIntegerProperty artikelNummerProperty() {
		return artikelNummer;
	}

	public String getName() {
		return name.get();
	}

	public void setName(String name) {
		this.name.set(name);
		serverArtikel.setName(name);
	}

	@SuppressWarnings("exports")
	public SimpleStringProperty nameProperty() {
		return name;
	}

	public String getBeschreibung() {
		return beschreibung.get();
	}

	public void setBeschreibung(String beschreibung) {
		this.beschreibung.set(beschreibung);
		serverArtikel.setBeschreibung(beschreibung);
	}

	@SuppressWarnings("exports")
	public SimpleStringProperty beschreibungProperty() {
		return beschreibung;
	}

	public int getPreis() {
		return preis.get();
	}

	public void setPreis(int preis) {
		this.preis.set(preis);
		serverArtikel.setPreis(preis);
	}

	@SuppressWarnings("exports")
	public SimpleIntegerProperty preisProperty() {
		return preis;
	}

	public String getGroesse() {
		return groesse.get();
	}

	public void setGroesse(String groesse) {
		this.groesse.set(groesse);
		serverArtikel.setGroesse(groesse);
	}

	@SuppressWarnings("exports")
	public SimpleStringProperty groesseProperty() {
		return groesse;
	}

	public byte[] getBild() {
		return bild.get();
	}

	public void setBild(byte[] bild) {
		this.bild.set(bild);
		serverArtikel.setBild(bild);
	}

	public SimpleObjectProperty<byte[]> bildProperty() {
		return bild;
	}

	public int getAnzahl() {
		return anzahl.get();
	}

	public void setAnzahl(int anzahl) {
		this.anzahl.set(anzahl);
		serverArtikel.setAnzahl(anzahl);
	}

	@SuppressWarnings("exports")
	public SimpleIntegerProperty anzahlProperty() {
		return anzahl;
	}

	public boolean isDeaktiv() {
		return deaktiv.get();
	}

	public void setDeaktiv(boolean deaktiv) {
		this.deaktiv.set(deaktiv);
		serverArtikel.setDeaktiv(deaktiv);
	}

	@SuppressWarnings("exports")
	public SimpleBooleanProperty deaktivProperty() {
		return deaktiv;
	}
}