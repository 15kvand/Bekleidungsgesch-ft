package client;

import java.time.LocalDate;


import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import klassen.Artikel;
import klassen.WarenkorbElement;

public class WarenkorbElementFX {
	private WarenkorbElement serverWarenkorbElement;
	private SimpleIntegerProperty id;
	private SimpleIntegerProperty menge;
	private SimpleIntegerProperty status;

	private SimpleIntegerProperty artikelNummer;
	private SimpleStringProperty artikelName;
	private SimpleStringProperty artikelBeschreibung;
	private SimpleStringProperty artikelGroesse;
	private SimpleIntegerProperty artikelPreis;
	private SimpleObjectProperty<byte[]> artikelBild;


	@SuppressWarnings("exports")
	public WarenkorbElementFX(WarenkorbElement element) {
		this.serverWarenkorbElement = element;
		this.id = new SimpleIntegerProperty(element.getId());
		this.menge = new SimpleIntegerProperty(element.getMenge());
		this.status = new SimpleIntegerProperty(element.getStatus());

		Artikel artikel = element.getArtikel();
		if (artikel != null) {
			this.artikelNummer = new SimpleIntegerProperty(artikel.getArtikelNummer());
			this.artikelName = new SimpleStringProperty(artikel.getName());
			this.artikelBeschreibung = new SimpleStringProperty(artikel.getBeschreibung());
			this.artikelGroesse = new SimpleStringProperty(artikel.getGroesse());
			this.artikelPreis = new SimpleIntegerProperty(artikel.getPreis());
			this.artikelBild = new SimpleObjectProperty<>(artikel.getBild());
		} else {
			this.artikelNummer = new SimpleIntegerProperty();
			this.artikelName = new SimpleStringProperty();
			this.artikelBeschreibung = new SimpleStringProperty();
			this.artikelGroesse = new SimpleStringProperty();
			this.artikelPreis = new SimpleIntegerProperty();
			this.artikelBild = new SimpleObjectProperty<>(new byte[0]);
		}
	}



	@SuppressWarnings("exports")
	public WarenkorbElement getServerWarenkorbElement() {
		return serverWarenkorbElement;
	}

	public int getId() {
		return id.get();
	}


	public void setId(int id) {
		this.id.set(id);
		serverWarenkorbElement.setId(id);
	}

	public int getKundeID() {
		return serverWarenkorbElement.getWarenkorb().getKunde().getId();
	}



	public LocalDate getVerkaufsdatum() {
		if (serverWarenkorbElement != null && serverWarenkorbElement.getWarenkorb() != null) {
			return serverWarenkorbElement.getWarenkorb().getDatum();
		} else {
			return LocalDate.now(); 
		}
	}



	@SuppressWarnings("exports")
	public SimpleIntegerProperty idProperty() {
		return id;
	}

	public int getMenge() {
		return menge.get();
	}

	public void setMenge(int menge) {
		this.menge.set(menge);
		serverWarenkorbElement.setMenge(menge);
	}

	@SuppressWarnings("exports")
	public SimpleIntegerProperty mengeProperty() {
		return menge;
	}

	public int getStatus() {
		return status.get();
	}

	public void setStatus(int status) {
		this.status.set(status);
		serverWarenkorbElement.setStatus(status);
	}

	@SuppressWarnings("exports")
	public SimpleIntegerProperty statusProperty() {
		return status;
	}

	public int getArtikelNummer() {
		return artikelNummer.get();
	}

	@SuppressWarnings("exports")
	public SimpleIntegerProperty artikelNummerProperty() {
		return artikelNummer;
	}

	public String getArtikelName() {
		return artikelName.get();
	}

	@SuppressWarnings("exports")
	public SimpleStringProperty artikelNameProperty() {
		return artikelName;
	}


	public String getArtikelBeschreibung() {
		return artikelBeschreibung.get();
	}

	@SuppressWarnings("exports")
	public SimpleStringProperty artikelBeschreibungProperty() {
		return artikelBeschreibung;
	}

	public String getArtikelGroesse() {
		return artikelGroesse.get();
	}

	@SuppressWarnings("exports")
	public SimpleStringProperty artikelGroesseProperty() {
		return artikelGroesse;
	}

	public int getArtikelPreis() {
		return artikelPreis.get();
	}

	@SuppressWarnings("exports")
	public SimpleIntegerProperty artikelPreisProperty() {
		return artikelPreis;
	}

	public byte[] getArtikelBild() {
		return artikelBild.get();
	}

	public SimpleObjectProperty<byte[]> artikelBildProperty() {
		return artikelBild;
	}

	public void setArtikelBild(byte[] bild) {
		this.artikelBild.set(bild);
	}
}