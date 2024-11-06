package server;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;

import klassen.Artikel;
import klassen.ArtikelList;
import klassen.Kunde;
import klassen.KundeList;
import klassen.Mitarbeiter;
import klassen.MitarbeiterList;
import klassen.Warenkorb;
import klassen.WarenkorbElement;


public class DatenbankV2 {

	public static final String DB_LOCATION = "/Users/daurenmukhtarov/Desktop/JavaWifi/BekleidungsgeschäftDB2";
	public static final String CONN_STRING = "jdbc:derby:" + DB_LOCATION + ";create=true";


	public static final String ARTIKEL_TABLE = "Artikel";
	public static final String ARTIKEL_NUMMER = "ARTIKELNUMMER";
	public static final String ARTIKEL_NAME = "NAME";
	public static final String ARTIKEL_BESCHREIBUNG = "BESCHREIBUNG";
	public static final String ARTIKEL_PREIS = "PREIS";
	public static final String ARTIKEL_GROESSE = "GROESSE";
	public static final String ARTIKEL_BILD = "BILD";
	public static final String ARTIKEL_ANZAHL = "ANZAHL";
	public static final String ARTIKEL_DEAKTIV = "DEAKTIV";

	public static final String KUNDE_TABLE = "Kunde";
	public static final String KUNDE_ID = "KUNDEID";
	public static final String KUNDE_NAME = "KUNDENAME";
	public static final String KUNDE_PASSWORT = "KUNDEPASSWORT";
	public static final String KUNDE_EMAIL = "KUNDEEMAIL";
	public static final String KUNDE_ADRESSE = "KUNDEADRESSE";
	public static final String KUNDE_TEL_NUMMER = "KUNDETELNUMMER";
	public static final String KUNDE_PLZ = "KUNDEPLZ";
	public static final String KUNDE_REGISTRATIONSDATUM = "REGISTRATIONSDATUM";

	public static final String MITARBEITER_TABLE = "Mitarbeiter";
	public static final String MITARBEITER_ID = "MITARBEITERID";
	public static final String MITARBEITER_NAME = "MITARBEITERNAME";
	public static final String MITARBEITER_PASSWORT = "MITARBEITERPASSWORT";
	public static final String MITARBEITER_EMAIL = "MITARBEITEREMAIL";
	public static final String MITARBEITER_ADRESSE = "MITARBEITERADRESSE";
	public static final String MITARBEITER_TEL_NUMMER	 = "MITARBEITERTELNUMMER";
	public static final String MITARBEITER_IBAN = "MITARBEITERIBAN";
	public static final String MITARBEITER_PLZ = "MITARBEITERPLZ";
	public static final String MITARBEITER_GEHALT = "MITARBEITERGEHALT";
	public static final String MITARBEITER_ROLLE = "ROLLE"; 

	public static final String WARENKORB_TABLE = "Warenkorb";
	public static final String WARENKORB_ID = "WARENKORBID";
	public static final String WARENKORB_KUNDE_ID = "WARENKORBKUNDEID";
	public static final String WARENKORB_DATUM = "DATUM";
	public static final String WARENKORB_STATUS = "WARENKORBSTATUS"; // 1 - offen(aktiver Warenkorb), 2 - abgeschlossen(bestellt)

	public static final String WARENKORB_ARTIKEL_TABLE = "Warenkorb_Artikel";
	public static final String WARENKORB_ARTIKEL_ID = "WARENKORBARTIKELID";
	public static final String WARENKORB_ARTIKEL_WARENKORB_ID = "WARENKORBARTIKELWARENKORBID";
	public static final String WARENKORB_ARTIKEL_ARTIKEL_NUMMER = "WARENKORBARTIKELARTIKELNUMMER";
	public static final String WARENKORB_ARTIKEL_STATUS = "STATUS"; // 1 - inwarenkorb, 2 - bestellt, 3 - verschickt


	// Erstellt alle benötigten Datenbanktabellen, falls sie nicht bereits existieren.
	public static void createTables() throws SQLException {
		createArtikelTable();
		createMitarbeiterTable();
		createKundeTable();
		createWarenkorbTable();
		createWarenkorbArtikelTable();
	}


	// Erstellt die Tabelle für Artikel in der Datenbank, falls sie nicht bereits existiert.
	public static void createArtikelTable() throws SQLException {
		String createTable = "CREATE TABLE " + ARTIKEL_TABLE + " (" +
				ARTIKEL_NUMMER + " INT GENERATED ALWAYS AS IDENTITY, " +
				ARTIKEL_NAME + " VARCHAR(255), " +
				ARTIKEL_BESCHREIBUNG + " VARCHAR(255), " +
				ARTIKEL_PREIS + " INT, " +
				ARTIKEL_GROESSE + " VARCHAR(10), " +
				ARTIKEL_BILD + " BLOB, " +
				ARTIKEL_ANZAHL + " INT, " +
				ARTIKEL_DEAKTIV + " BOOLEAN DEFAULT FALSE, " + 
				"PRIMARY KEY(" + ARTIKEL_NUMMER + "))";
		executeCreateTable(createTable, ARTIKEL_TABLE);
	}


	// Erstellt die Tabelle für Kunden in der Datenbank, falls sie nicht bereits existiert.
	// Erstellt außerdem ein Admin-Konto für den Zugriff.
	public static void createKundeTable() throws SQLException {
		String createTable = "CREATE TABLE " + KUNDE_TABLE + " (" +
				KUNDE_ID + " INT GENERATED ALWAYS AS IDENTITY, " +
				KUNDE_NAME + " VARCHAR(255), " +
				KUNDE_PASSWORT + " VARCHAR(255), " +
				KUNDE_EMAIL + " VARCHAR(255), " +
				KUNDE_ADRESSE + " VARCHAR(255), " +
				KUNDE_TEL_NUMMER + " VARCHAR(20), " +
				KUNDE_PLZ + " VARCHAR(10), " +
				KUNDE_REGISTRATIONSDATUM + " DATE, " +
				"PRIMARY KEY(" + KUNDE_ID + "))";
		executeCreateTable(createTable, KUNDE_TABLE);
		Mitarbeiter admin = new Mitarbeiter();
		admin.setName("Admin");
		admin.setPasswort("admin"); 
		admin.setEmail("admin");
		admin.setRolle("ADMIN");

		DatenbankV2.insertMitarbeiter(admin);
		System.out.println("Admin-Konto wurde erstellt.");
	}


	// Erstellt die Tabelle für Mitarbeiter in der Datenbank, falls sie nicht bereits existiert.
	public static void createMitarbeiterTable() throws SQLException {
		String createTable = "CREATE TABLE " + MITARBEITER_TABLE + " (" +
				MITARBEITER_ID + " INT GENERATED ALWAYS AS IDENTITY, " +
				MITARBEITER_NAME + " VARCHAR(255), " +
				MITARBEITER_PASSWORT + " VARCHAR(255), " +
				MITARBEITER_EMAIL + " VARCHAR(255), " +
				MITARBEITER_ADRESSE + " VARCHAR(255), " +
				MITARBEITER_TEL_NUMMER + " VARCHAR(20), " +
				MITARBEITER_IBAN + " VARCHAR(34), " +
				MITARBEITER_PLZ + " VARCHAR(30), " +
				MITARBEITER_GEHALT + " INT, " +
				MITARBEITER_ROLLE + " VARCHAR(50) NOT NULL DEFAULT 'MITARBEITER', " +
				"PRIMARY KEY(" + MITARBEITER_ID + "))";
		executeCreateTable(createTable, MITARBEITER_TABLE);
	}


	// Erstellt die Tabelle für Warenkörbe in der Datenbank, falls sie nicht bereits existiert.
	public static void createWarenkorbTable() throws SQLException {
		String createTable = "CREATE TABLE " + WARENKORB_TABLE + " (" +
				WARENKORB_ID + " INT GENERATED ALWAYS AS IDENTITY, " +
				WARENKORB_KUNDE_ID + " INT, " +
				WARENKORB_DATUM + " DATE, " +
				WARENKORB_STATUS + " INT DEFAULT 1, " +
				"PRIMARY KEY(" + WARENKORB_ID + "), " +
				"FOREIGN KEY(" + WARENKORB_KUNDE_ID + ") REFERENCES " + KUNDE_TABLE + "(" + KUNDE_ID + "))";
		executeCreateTable(createTable, WARENKORB_TABLE);
	}


	// Erstellt die Tabelle für Warenkorb-Artikel in der Datenbank, falls sie nicht bereits existiert.
	public static void createWarenkorbArtikelTable() throws SQLException {
		String createTable = "CREATE TABLE " + WARENKORB_ARTIKEL_TABLE + " (" +
				WARENKORB_ARTIKEL_ID + " INT GENERATED ALWAYS AS IDENTITY, " +
				WARENKORB_ARTIKEL_WARENKORB_ID + " INT, " +
				WARENKORB_ARTIKEL_ARTIKEL_NUMMER + " INT, " +
				"menge INT, " +
				WARENKORB_ARTIKEL_STATUS + " INT, " +
				"PRIMARY KEY(" + WARENKORB_ARTIKEL_ID + "), " +
				"FOREIGN KEY(" + WARENKORB_ARTIKEL_WARENKORB_ID + ") REFERENCES " + WARENKORB_TABLE + "(" + WARENKORB_ID + "), " +
				"FOREIGN KEY(" + WARENKORB_ARTIKEL_ARTIKEL_NUMMER + ") REFERENCES " + ARTIKEL_TABLE + "(" + ARTIKEL_NUMMER + "))";
		executeCreateTable(createTable, WARENKORB_ARTIKEL_TABLE);
	}


	// Hilfsmethode, die das SQL-Statement zur Erstellung einer Tabelle 
	// ausführt, falls die Tabelle noch nicht existiert.
	private static void executeCreateTable(String createTableSQL, String tableName) throws SQLException {
		try (Connection conn = DriverManager.getConnection(CONN_STRING);
				Statement stmt = conn.createStatement()) {
			ResultSet rs = conn.getMetaData().getTables(null, null, tableName.toUpperCase(), new String[]{"TABLE"});
			if (!rs.next()) {
				stmt.executeUpdate(createTableSQL);
			}
		}
	}


	// Liest alle verkauften Artikel aus der Datenbank aus, um Verkaufsstatistiken zu generieren.
	public static ArrayList<WarenkorbElement> leseVerkaufteArtikel() throws SQLException {
		ArrayList<WarenkorbElement> verkaufteArtikelListe = new ArrayList<>();
		String query = "SELECT * FROM " + WARENKORB_ARTIKEL_TABLE + " INNER JOIN " + ARTIKEL_TABLE +
				" ON " + WARENKORB_ARTIKEL_TABLE + "." + WARENKORB_ARTIKEL_ARTIKEL_NUMMER + " = " + ARTIKEL_TABLE + "." + ARTIKEL_NUMMER +
				" WHERE " + WARENKORB_ARTIKEL_STATUS + " = 3";

		try (Connection conn = DriverManager.getConnection(CONN_STRING);
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(query)) {

			while (rs.next()) {
				Artikel artikel = new Artikel(
						rs.getInt(ARTIKEL_NUMMER),
						rs.getString(ARTIKEL_NAME),
						rs.getString(ARTIKEL_BESCHREIBUNG),
						rs.getInt(ARTIKEL_PREIS),
						rs.getString(ARTIKEL_GROESSE),
						rs.getBytes(ARTIKEL_BILD),
						rs.getInt(ARTIKEL_ANZAHL)
						);

				WarenkorbElement element = new WarenkorbElement(
						rs.getInt(WARENKORB_ARTIKEL_ID),
						null,  
						artikel,
						rs.getInt("menge"),
						rs.getInt(WARENKORB_ARTIKEL_STATUS)
						);

				verkaufteArtikelListe.add(element);
			}
		}
		return verkaufteArtikelListe;
	}



	// Liest alle aktiven Artikel aus der Datenbank, die nicht deaktiviert sind und einen Bestand > 0 haben.
	// Diese Methode wird verwendet, um die aktuelle Produktliste für die Produktliste-GUI anzuzeigen.
	public static ArtikelList leseArtikel() throws SQLException {
		ArrayList<Artikel> artikelListe = new ArrayList<>();
		try (Connection conn = DriverManager.getConnection(CONN_STRING);
				Statement stmt = conn.createStatement()) {
			String select = "SELECT * FROM " + ARTIKEL_TABLE + " WHERE " + ARTIKEL_DEAKTIV + " = FALSE" +  // Nur aktive Artikel
					// Nur Artikel mit Bestand > 0
					" AND " + ARTIKEL_ANZAHL + " > 0"; 
			ResultSet rs = stmt.executeQuery(select);
			while (rs.next()) {
				Artikel artikel = new Artikel();
				artikel.setArtikelNummer(rs.getInt(ARTIKEL_NUMMER));
				artikel.setName(rs.getString(ARTIKEL_NAME));
				artikel.setBeschreibung(rs.getString(ARTIKEL_BESCHREIBUNG));
				artikel.setPreis(rs.getInt(ARTIKEL_PREIS));
				artikel.setGroesse(rs.getString(ARTIKEL_GROESSE));
				byte[] bildDaten = rs.getBytes(ARTIKEL_BILD);
				System.out.println("Bilddaten Länge für Artikel " + artikel.getArtikelNummer() + ": " + (bildDaten != null ? bildDaten.length : "null"));
				artikel.setBild(bildDaten);
				artikel.setAnzahl(rs.getInt(ARTIKEL_ANZAHL));
				artikel.setDeaktiv(rs.getBoolean(ARTIKEL_DEAKTIV)); 
				artikelListe.add(artikel);
			}
			rs.close();
			return new ArtikelList(artikelListe);
		}
	}

	// Liest alle Artikel aus der Datenbank, die nicht deaktiviert sind, unabhängig vom Bestand.
	// Diese Methode wird in der Lagerbestands-GUI verwendet, 
	// um alle Artikel anzuzeigen und zu filtern (auch solche mit Anzahl 0).
	public static ArtikelList leseAlleArtikel() throws SQLException {
		ArrayList<Artikel> artikelListe = new ArrayList<>();
		try (Connection conn = DriverManager.getConnection(CONN_STRING);
				Statement stmt = conn.createStatement()) {
			String select = "SELECT * FROM " + ARTIKEL_TABLE + " WHERE " + ARTIKEL_DEAKTIV + " = FALSE";
			ResultSet rs = stmt.executeQuery(select);
			while (rs.next()) {
				Artikel artikel = new Artikel();
				artikel.setArtikelNummer(rs.getInt(ARTIKEL_NUMMER));
				artikel.setName(rs.getString(ARTIKEL_NAME));
				artikel.setBeschreibung(rs.getString(ARTIKEL_BESCHREIBUNG));
				artikel.setPreis(rs.getInt(ARTIKEL_PREIS));
				artikel.setGroesse(rs.getString(ARTIKEL_GROESSE));
				artikel.setBild(rs.getBytes(ARTIKEL_BILD));
				artikel.setAnzahl(rs.getInt(ARTIKEL_ANZAHL));
				artikel.setDeaktiv(rs.getBoolean(ARTIKEL_DEAKTIV));
				artikelListe.add(artikel);
			}
			rs.close();
			return new ArtikelList(artikelListe);
		}
	}



	// Liest alle Kunden aus der Datenbank und gibt sie als Liste zurück.
	public static KundeList leseKunden() throws SQLException {
		ArrayList<Kunde> kundenListe = new ArrayList<>();
		try (Connection conn = DriverManager.getConnection(CONN_STRING);
				Statement stmt = conn.createStatement()) {
			String select = "SELECT * FROM " + KUNDE_TABLE;
			ResultSet rs = stmt.executeQuery(select);
			while (rs.next()) {
				kundenListe.add(new Kunde(
						rs.getInt(KUNDE_ID),
						rs.getString(KUNDE_NAME),
						rs.getString(KUNDE_PASSWORT),
						rs.getString(KUNDE_EMAIL),
						rs.getString(KUNDE_ADRESSE),
						rs.getString(KUNDE_TEL_NUMMER),
						rs.getString(KUNDE_PLZ),
						rs.getDate(KUNDE_REGISTRATIONSDATUM).toLocalDate()
						));
			}
			rs.close();
			return new KundeList(kundenListe);
		}
	}



	// Liest alle Mitarbeiter aus der Datenbank und gibt sie als Liste zurück.
	public static MitarbeiterList leseMitarbeiter() throws SQLException {
		ArrayList<Mitarbeiter> mitarbeiterListe = new ArrayList<>();
		try (Connection conn = DriverManager.getConnection(CONN_STRING);
				Statement stmt = conn.createStatement()) {
			String select = "SELECT * FROM " + MITARBEITER_TABLE;
			ResultSet rs = stmt.executeQuery(select);
			while (rs.next()) {
				mitarbeiterListe.add(new Mitarbeiter(
						rs.getInt(MITARBEITER_ID),
						rs.getString(MITARBEITER_NAME),
						rs.getString(MITARBEITER_PASSWORT),
						rs.getString(MITARBEITER_EMAIL),
						rs.getString(MITARBEITER_ADRESSE),
						rs.getString(MITARBEITER_TEL_NUMMER),
						rs.getString(MITARBEITER_IBAN),
						rs.getString(MITARBEITER_PLZ),
						rs.getInt(MITARBEITER_GEHALT),
						rs.getString(MITARBEITER_ROLLE)
						));
			}
			rs.close();
			return new MitarbeiterList(mitarbeiterListe);
		}
	}



	// Liest den aktuellen Warenkorb eines bestimmten Kunden 
	// aus der Datenbank, inklusive der enthaltenen Warenkorb-Elemente.
	public static Warenkorb leseWarenkorb(int kundeId) throws SQLException {
		ArrayList<WarenkorbElement> elementeListe = new ArrayList<>();
		Warenkorb warenkorb = null;

		try (Connection conn = DriverManager.getConnection(CONN_STRING);
				Statement stmt = conn.createStatement()) {

			// SQL-Abfrage mit INNER JOIN, um die Warenkorb- und Artikeldaten zu verknüpfen
			String select = "SELECT * FROM " + WARENKORB_ARTIKEL_TABLE +
					" INNER JOIN " + ARTIKEL_TABLE +
					" ON " + WARENKORB_ARTIKEL_TABLE + "." + WARENKORB_ARTIKEL_ARTIKEL_NUMMER + " = " + ARTIKEL_TABLE + "." + ARTIKEL_NUMMER +
					" INNER JOIN " + WARENKORB_TABLE +
					" ON " + WARENKORB_ARTIKEL_TABLE + "." + WARENKORB_ARTIKEL_WARENKORB_ID + " = " + WARENKORB_TABLE + "." + WARENKORB_ID +
					" WHERE " + WARENKORB_TABLE + "." + WARENKORB_KUNDE_ID + " = " + kundeId + " AND " + WARENKORB_STATUS + " = 1";

			ResultSet rs = stmt.executeQuery(select);

			// Verarbeitung der Resultate
			if (rs.next()) {
				int warenkorbId = rs.getInt(WARENKORB_ID);
				LocalDate datum = rs.getDate(WARENKORB_DATUM) != null ? rs.getDate(WARENKORB_DATUM).toLocalDate() : null;
				warenkorb = new Warenkorb(warenkorbId, elementeListe, leseKunde(kundeId), datum, 1);

				// Schleife durch die Warenkorb-Artikel
				do {
					Artikel artikel = new Artikel(
							rs.getInt(ARTIKEL_NUMMER),
							rs.getString(ARTIKEL_NAME),
							rs.getString(ARTIKEL_BESCHREIBUNG),
							rs.getInt(ARTIKEL_PREIS),
							rs.getString(ARTIKEL_GROESSE),
							rs.getBytes(ARTIKEL_BILD),
							rs.getInt(ARTIKEL_ANZAHL)
							);

					WarenkorbElement element = new WarenkorbElement(
							rs.getInt(WARENKORB_ARTIKEL_ID),
							warenkorb, 
							artikel,
							rs.getInt("menge"),
							rs.getInt(WARENKORB_ARTIKEL_STATUS)
							);

					elementeListe.add(element); // Warenkorb-Element zur Liste hinzufügen
				} while (rs.next());

			}

			rs.close();
		}

		return warenkorb;
	}


	// Liest alle Warenkorb-Artikel eines bestimmten Warenkorbs aus 
	// der Datenbank und gibt sie als Liste zurück.
	public static ArrayList<WarenkorbElement> leseWarenkorbArtikel(int warenkorbId) throws SQLException {
		ArrayList<WarenkorbElement> elementeListe = new ArrayList<>();
		try (Connection conn = DriverManager.getConnection(CONN_STRING);
				Statement stmt = conn.createStatement()) {
			String select = "SELECT * FROM " + WARENKORB_ARTIKEL_TABLE +
					" INNER JOIN " + ARTIKEL_TABLE +
					" ON " + WARENKORB_ARTIKEL_TABLE + "." + WARENKORB_ARTIKEL_ARTIKEL_NUMMER + " = " + ARTIKEL_TABLE + "." + ARTIKEL_NUMMER +
					" WHERE " + WARENKORB_ARTIKEL_WARENKORB_ID + " = " + warenkorbId;
			ResultSet rs = stmt.executeQuery(select);
			while (rs.next()) {
				Artikel artikel = new Artikel(
						rs.getInt(ARTIKEL_NUMMER),
						rs.getString(ARTIKEL_NAME),
						rs.getString(ARTIKEL_BESCHREIBUNG),
						rs.getInt(ARTIKEL_PREIS),
						rs.getString(ARTIKEL_GROESSE),
						rs.getBytes(ARTIKEL_BILD),
						rs.getInt(ARTIKEL_ANZAHL)
						);

				WarenkorbElement element = new WarenkorbElement(
						rs.getInt(WARENKORB_ARTIKEL_ID),
						new Warenkorb(warenkorbId, null, null, null, 1),
						artikel,
						rs.getInt("menge"), 
						rs.getInt("status") 
						);

				elementeListe.add(element);
			}
			rs.close();
			return elementeListe;
		}
	}


	// Liest ein einzelnes Warenkorb-Element anhand seiner ID aus der Datenbank.
	public static WarenkorbElement leseWarenkorbElementById(int id) throws SQLException {
		WarenkorbElement element = null;
		String query = "SELECT * FROM " + WARENKORB_ARTIKEL_TABLE + 
				" WHERE " + WARENKORB_ARTIKEL_ID + " = ?";
		try (Connection conn = DriverManager.getConnection(CONN_STRING);
				PreparedStatement pstmt = conn.prepareStatement(query)) {
			pstmt.setInt(1, id);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				int warenkorbId = rs.getInt(WARENKORB_ARTIKEL_WARENKORB_ID);
				int artikelNummer = rs.getInt(WARENKORB_ARTIKEL_ARTIKEL_NUMMER);
				Warenkorb warenkorb = leseWarenkorbById(warenkorbId); 
				Artikel artikel = leseArtikelById(artikelNummer);     
				element = new WarenkorbElement(
						rs.getInt(WARENKORB_ARTIKEL_ID),
						warenkorb,
						artikel,
						rs.getInt("menge"),
						rs.getInt(WARENKORB_ARTIKEL_STATUS)
						);
			}
		}
		return element;
	}

	// Liest einen Warenkorb anhand seiner ID aus der 
	// Datenbank, inklusive der enthaltenen Warenkorb-Elemente.
	public static Warenkorb leseWarenkorbById(int warenkorbId) throws SQLException {
		Warenkorb warenkorb = null;
		String query = "SELECT * FROM " + WARENKORB_TABLE + " WHERE " + WARENKORB_ID + " = ?";
		try (Connection conn = DriverManager.getConnection(CONN_STRING);
				PreparedStatement pstmt = conn.prepareStatement(query)) {
			pstmt.setInt(1, warenkorbId);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				int kundeId = rs.getInt(WARENKORB_KUNDE_ID);
				LocalDate datum = rs.getDate(WARENKORB_DATUM).toLocalDate();
				Kunde kunde = leseKunde(kundeId); // Bereits vorhandene Methode zum Laden des Kunden
				ArrayList<WarenkorbElement> elementeListe = leseWarenkorbArtikel(warenkorbId); // Laden der WarenkorbElemente
				warenkorb = new Warenkorb(warenkorbId, elementeListe, kunde, datum, 1);
			}
		}
		return warenkorb;
	}

	// Liest einen Artikel anhand seiner Artikelnummer aus der Datenbank.
	public static Artikel leseArtikelById(int artikelNummer) throws SQLException {
		Artikel artikel = null;
		String query = "SELECT * FROM " + ARTIKEL_TABLE + " WHERE " + ARTIKEL_NUMMER + " = ?";
		try (Connection conn = DriverManager.getConnection(CONN_STRING);
				PreparedStatement pstmt = conn.prepareStatement(query)) {
			pstmt.setInt(1, artikelNummer);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				artikel = new Artikel();
				artikel.setArtikelNummer(rs.getInt(ARTIKEL_NUMMER));
				artikel.setName(rs.getString(ARTIKEL_NAME));
				artikel.setBeschreibung(rs.getString(ARTIKEL_BESCHREIBUNG));
				artikel.setPreis(rs.getInt(ARTIKEL_PREIS));
				artikel.setGroesse(rs.getString(ARTIKEL_GROESSE));
				artikel.setBild(rs.getBytes(ARTIKEL_BILD));
				artikel.setAnzahl(rs.getInt(ARTIKEL_ANZAHL));
				artikel.setDeaktiv(rs.getBoolean(ARTIKEL_DEAKTIV));
			}
		}
		return artikel;
	}


	// Liest einen Kunden anhand seiner ID aus der Datenbank.
	private static Kunde leseKunde(int kundeId) throws SQLException {
		Kunde kunde = null;
		try (Connection conn = DriverManager.getConnection(CONN_STRING);
				Statement stmt = conn.createStatement()) {
			String select = "SELECT * FROM " + KUNDE_TABLE + " WHERE " + KUNDE_ID + " = " + kundeId;
			ResultSet rs = stmt.executeQuery(select);

			if (rs.next()) {
				kunde = new Kunde(
						rs.getInt(KUNDE_ID),
						rs.getString(KUNDE_NAME),
						rs.getString(KUNDE_PASSWORT),
						rs.getString(KUNDE_EMAIL),
						rs.getString(KUNDE_ADRESSE),
						rs.getString(KUNDE_TEL_NUMMER),
						rs.getString(KUNDE_PLZ),
						rs.getDate(KUNDE_REGISTRATIONSDATUM).toLocalDate()
						);
			}
			rs.close();
		}
		return kunde;
	}



	// Fügt einen neuen Artikel in die Datenbank ein und setzt 
	// die generierte Artikelnummer im Artikelobjekt.
	public static void insertArtikel(Artikel artikel) throws SQLException {
		String insertSQL = "INSERT INTO " + ARTIKEL_TABLE + " (" + ARTIKEL_NAME + ", " + ARTIKEL_BESCHREIBUNG + ", " +
				ARTIKEL_PREIS + ", " + ARTIKEL_GROESSE + ", " + ARTIKEL_BILD + ", " + ARTIKEL_ANZAHL + ", " + ARTIKEL_DEAKTIV + ") VALUES (?, ?, ?, ?, ?, ?, ?)";
		try (Connection conn = DriverManager.getConnection(CONN_STRING);
				PreparedStatement pstmt = conn.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS)) {

			pstmt.setString(1, artikel.getName());
			pstmt.setString(2, artikel.getBeschreibung());
			pstmt.setInt(3, artikel.getPreis());
			pstmt.setString(4, artikel.getGroesse());
			pstmt.setBytes(5, artikel.getBild());
			pstmt.setInt(6, artikel.getAnzahl());
			pstmt.setBoolean(7, artikel.isDeaktiv()); 
			pstmt.executeUpdate();

			ResultSet generatedKeys = pstmt.getGeneratedKeys();
			if (generatedKeys.next()) {
				artikel.setArtikelNummer(generatedKeys.getInt(1));
			}
		} catch (SQLException e) {
			throw e;
		}
	}



	// Fügt einen neuen Kunden in die Datenbank ein und setzt 
	// die generierte Kunden-ID im Kundenobjekt
	public static void insertKunde(Kunde kunde) throws SQLException {
		String insertSQL = "INSERT INTO " + KUNDE_TABLE + " (" + KUNDE_NAME + ", " + KUNDE_PASSWORT + ", " + KUNDE_EMAIL + ", " + KUNDE_ADRESSE + ", " + KUNDE_TEL_NUMMER + ", " + KUNDE_PLZ + ", " + KUNDE_REGISTRATIONSDATUM + ") VALUES (?, ?, ?, ?, ?, ?, ?)";
		try (Connection conn = DriverManager.getConnection(CONN_STRING);
				PreparedStatement pstmt = conn.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS)) {
			pstmt.setString(1, kunde.getName());
			pstmt.setString(2, kunde.getPasswort());
			pstmt.setString(3, kunde.getEmail());
			pstmt.setString(4, kunde.getAdresse());
			pstmt.setString(5, kunde.getTelNummer());
			pstmt.setString(6, kunde.getPlz());
			pstmt.setDate(7, java.sql.Date.valueOf(kunde.getRegistrierungsdatum()));
			pstmt.executeUpdate();

			// generierte Kunden-ID abrufen
			ResultSet generatedKeys = pstmt.getGeneratedKeys();
			if (generatedKeys.next()) {
				kunde.setId(generatedKeys.getInt(1));  //  die generierte ID im Kunde-Objekt setzen
			}
		} catch (SQLException e) {
			throw e;
		}
	}


	// Fügt einen neuen Mitarbeiter in die Datenbank ein und setzt 
	// die generierte Mitarbeiter-ID im Mitarbeiterobjekt.
	public static void insertMitarbeiter(Mitarbeiter mitarbeiter) throws SQLException {
		String insertSQL = "INSERT INTO " + MITARBEITER_TABLE + " (" + MITARBEITER_NAME + ", " + MITARBEITER_PASSWORT + ", " + MITARBEITER_EMAIL + ", " + MITARBEITER_ADRESSE + ", " + MITARBEITER_TEL_NUMMER + ", " + MITARBEITER_IBAN + ", " + MITARBEITER_PLZ + ", " + MITARBEITER_GEHALT + ", " + MITARBEITER_ROLLE + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try (Connection conn = DriverManager.getConnection(CONN_STRING);
				PreparedStatement pstmt = conn.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS)) {

			pstmt.setString(1, mitarbeiter.getName());
			pstmt.setString(2, mitarbeiter.getPasswort());
			pstmt.setString(3, mitarbeiter.getEmail());
			pstmt.setString(4, mitarbeiter.getAdresse());
			pstmt.setString(5, mitarbeiter.getTelNummer());
			pstmt.setString(6, mitarbeiter.getIban());
			pstmt.setString(7, mitarbeiter.getPlz());
			pstmt.setInt(8, mitarbeiter.getGehalt());
			pstmt.setString(9, mitarbeiter.getRolle());

			pstmt.executeUpdate();

			// generierte Mitarbeiter-ID abrufen
			ResultSet generatedKeys = pstmt.getGeneratedKeys();
			if (generatedKeys.next()) {
				mitarbeiter.setId(generatedKeys.getInt(1)); //  die generierte ID im Mitarbeiter-Objekt setzen
			}
		} catch (SQLException e) {
			throw e;
		}
	}


	// Fügt einen neuen Warenkorb in die Datenbank ein und 
	// setzt die generierte Warenkorb-ID im Warenkorbobjekt.
	public static void insertWarenkorb(Warenkorb warenkorb) throws SQLException {
		String insertSQL = "INSERT INTO " + WARENKORB_TABLE + " (" + WARENKORB_KUNDE_ID + ", " + WARENKORB_DATUM + ", " + WARENKORB_STATUS + ") VALUES (?, ?, ?)";
		try (Connection conn = DriverManager.getConnection(CONN_STRING);
				PreparedStatement pstmt = conn.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS)) {
			pstmt.setInt(1, warenkorb.getKunde().getId());
			pstmt.setDate(2, java.sql.Date.valueOf(warenkorb.getDatum()));
			pstmt.setInt(3, warenkorb.getStatus());
			pstmt.executeUpdate();

			// generierte Warenkorb-ID abrufen
			ResultSet generatedKeys = pstmt.getGeneratedKeys();
			if (generatedKeys.next()) {
				warenkorb.setId(generatedKeys.getInt(1));  //  die generierte ID im Warenkorb-Objekt setzen
			}
		} catch (SQLException e) {
			throw e;
		}
	}


	// Fügt ein neues Warenkorb-Element in die Datenbank ein.
	public static void insertWarenkorbArtikel(WarenkorbElement element) throws SQLException {
		String insertSQL = "INSERT INTO " + WARENKORB_ARTIKEL_TABLE + " (" +
				WARENKORB_ARTIKEL_WARENKORB_ID + ", " +
				WARENKORB_ARTIKEL_ARTIKEL_NUMMER + ", " +
				"menge, status) VALUES (?, ?, ?, ?)";
		try (Connection conn = DriverManager.getConnection(CONN_STRING);
				PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {

			pstmt.setInt(1, element.getWarenkorb().getId());
			pstmt.setInt(2, element.getArtikel().getArtikelNummer());
			pstmt.setInt(3, element.getMenge());
			pstmt.setInt(4, element.getStatus());

			pstmt.executeUpdate();
		} catch (SQLException e) {
			throw e;
		}
	}


	//  Deaktiviert einen Artikel in der Datenbank, anstatt ihn physisch zu löschen.
	public static void deleteArtikel(int artikelNummer) throws SQLException {
		String updateSQL = "UPDATE " + ARTIKEL_TABLE + " SET " + ARTIKEL_DEAKTIV + " = TRUE WHERE " + ARTIKEL_NUMMER + " = ?";
		try (Connection conn = DriverManager.getConnection(CONN_STRING);
				PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {
			pstmt.setInt(1, artikelNummer);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			throw e;
		}
	}

	// Löscht einen Mitarbeiter aus der Datenbank.
	public static void deleteMitarbeiter(int mitarbeiterId) throws SQLException {
		String deleteSQL = "DELETE FROM " + MITARBEITER_TABLE + " WHERE " + MITARBEITER_ID + " = ?";
		try (Connection conn = DriverManager.getConnection(CONN_STRING);
				PreparedStatement pstmt = conn.prepareStatement(deleteSQL)) {
			pstmt.setInt(1, mitarbeiterId);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			throw e;
		}
	}


	// Löscht ein Warenkorb-Artikel aus der Datenbank.
	public static void deleteWarenkorbArtikel(int id) throws SQLException {
		String deleteSQL = "DELETE FROM " + WARENKORB_ARTIKEL_TABLE + " WHERE " + WARENKORB_ARTIKEL_ID + " = ?";
		try (Connection conn = DriverManager.getConnection(CONN_STRING);
				PreparedStatement pstmt = conn.prepareStatement(deleteSQL)) {
			pstmt.setInt(1, id);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			throw e;
		}
	}


	// Aktualisiert die Daten eines bestehenden Mitarbeiters in der Datenbank.
	public static void updateMitarbeiter(Mitarbeiter mitarbeiter) throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;

		try {
			conn = DriverManager.getConnection(CONN_STRING);
			String update = "UPDATE " + MITARBEITER_TABLE + " SET " +
					MITARBEITER_NAME + " = ?, " +
					MITARBEITER_PASSWORT + " = ?, " +
					MITARBEITER_EMAIL + " = ?, " +
					MITARBEITER_ADRESSE + " = ?, " +
					MITARBEITER_TEL_NUMMER + " = ?, " +
					MITARBEITER_IBAN + " = ?, " +
					MITARBEITER_PLZ + " = ?, " +
					MITARBEITER_GEHALT + " = ?, " +
					MITARBEITER_ROLLE + " = ? " +
					"WHERE " + MITARBEITER_ID + " = ?";

			pstmt = conn.prepareStatement(update);
			pstmt.setString(1, mitarbeiter.getName());
			pstmt.setString(2, mitarbeiter.getPasswort());
			pstmt.setString(3, mitarbeiter.getEmail());
			pstmt.setString(4, mitarbeiter.getAdresse());
			pstmt.setString(5, mitarbeiter.getTelNummer());
			pstmt.setString(6, mitarbeiter.getIban());
			pstmt.setString(7, mitarbeiter.getPlz());
			pstmt.setInt(8, mitarbeiter.getGehalt());
			pstmt.setString(9, mitarbeiter.getRolle());
			pstmt.setInt(10, mitarbeiter.getId());

			pstmt.executeUpdate();
		} catch (SQLException e) {
			throw e;
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					throw e;
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					throw e;
				}
			}
		}
	}


	// Aktualisiert die Daten eines bestehenden Artikels in der Datenbank.
	public static void updateArtikel(Artikel artikel) throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;

		try {
			conn = DriverManager.getConnection(CONN_STRING);
			String update = "UPDATE " + ARTIKEL_TABLE + " SET " +
					ARTIKEL_NAME + " = ?, " +
					ARTIKEL_BESCHREIBUNG + " = ?, " +
					ARTIKEL_PREIS + " = ?, " +
					ARTIKEL_GROESSE + " = ?, " +
					ARTIKEL_BILD + " = ?, " +
					ARTIKEL_ANZAHL + " = ?, " +
					ARTIKEL_DEAKTIV + " = ? " + 
					"WHERE " + ARTIKEL_NUMMER + " = ?";

			pstmt = conn.prepareStatement(update);
			pstmt.setString(1, artikel.getName());
			pstmt.setString(2, artikel.getBeschreibung());
			pstmt.setInt(3, artikel.getPreis());
			pstmt.setString(4, artikel.getGroesse());
			pstmt.setBytes(5, artikel.getBild());
			pstmt.setInt(6, artikel.getAnzahl());
			pstmt.setBoolean(7, artikel.isDeaktiv()); 
			pstmt.setInt(8, artikel.getArtikelNummer());

			pstmt.executeUpdate();
		} catch (SQLException e) {
			throw e;
		} finally {
			if (pstmt != null) pstmt.close();
			if (conn != null) conn.close();
		}
	}


	// Aktualisiert die Daten eines bestehenden Kunden in der Datenbank.
	public static void updateKunde(Kunde kunde) throws SQLException {
		try (Connection conn = DriverManager.getConnection(CONN_STRING);
				PreparedStatement pstmt = conn.prepareStatement(
						"UPDATE " + KUNDE_TABLE + " SET " +
								KUNDE_NAME + " = ?, " +
								KUNDE_PASSWORT + " = ?, " +
								KUNDE_EMAIL + " = ?, " +
								KUNDE_ADRESSE + " = ?, " +
								KUNDE_TEL_NUMMER + " = ?, " +
								KUNDE_PLZ + " = ?, " +
								KUNDE_REGISTRATIONSDATUM + " = ? " +
								"WHERE " + KUNDE_ID + " = ?")) {

			pstmt.setString(1, kunde.getName());
			pstmt.setString(2, kunde.getPasswort());
			pstmt.setString(3, kunde.getEmail());
			pstmt.setString(4, kunde.getAdresse());
			pstmt.setString(5, kunde.getTelNummer());
			pstmt.setString(6, kunde.getPlz());
			pstmt.setDate(7, java.sql.Date.valueOf(kunde.getRegistrierungsdatum()));
			pstmt.setInt(8, kunde.getId());

			pstmt.executeUpdate();
		} catch (SQLException e) {
			throw e;
		}
	}


	// Aktualisiert den Status eines Warenkorbs in der Datenbank (z.B. von offen zu abgeschlossen)
	public static void updateWarenkorbStatus(int warenkorbId, int status) throws SQLException {
		String sql = "UPDATE " + WARENKORB_TABLE + " SET " + WARENKORB_STATUS + " = ? WHERE " + WARENKORB_ID + " = ?";
		try (Connection conn = DriverManager.getConnection(CONN_STRING);
				PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, status);
			pstmt.setInt(2, warenkorbId);
			pstmt.executeUpdate();
		}
	}



	// Aktualisiert die Daten eines bestehenden Warenkorb-Elements in der Datenbank.
	public static void updateWarenkorbArtikel(WarenkorbElement element) throws SQLException {
		String updateSQL = "UPDATE " + WARENKORB_ARTIKEL_TABLE + " SET " +
				"menge = ?, " +
				WARENKORB_ARTIKEL_STATUS + " = ? " +
				"WHERE " + WARENKORB_ARTIKEL_ID + " = ?";
		try (Connection conn = DriverManager.getConnection(CONN_STRING);
				PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {

			pstmt.setInt(1, element.getMenge());
			pstmt.setInt(2, element.getStatus());
			pstmt.setInt(3, element.getId());

			pstmt.executeUpdate();
		} catch (SQLException e) {
			throw e;
		}
	}



	// Liest alle Warenkörbe eines Kunden mit einem bestimmten Status aus der Datenbank.
	public static ArrayList<Warenkorb> leseWarenkoerbeByKundeIdUndStatus(int kundeId, int status) throws SQLException {
		ArrayList<Warenkorb> warenkoerbe = new ArrayList<>();
		String sql = "SELECT * FROM " + WARENKORB_TABLE + " WHERE " + WARENKORB_KUNDE_ID + " = ? AND " + WARENKORB_STATUS + " = ?";
		try (Connection conn = DriverManager.getConnection(CONN_STRING);
				PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, kundeId);
			pstmt.setInt(2, status);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Warenkorb warenkorb = new Warenkorb();
				int warenkorbId = rs.getInt(WARENKORB_ID);
				warenkorb.setId(warenkorbId);
				warenkorb.setKunde(leseKunde(kundeId));
				warenkorb.setDatum(rs.getDate(WARENKORB_DATUM).toLocalDate());
				warenkorb.setStatus(rs.getInt(WARENKORB_STATUS));
				warenkorb.setElemente(leseWarenkorbArtikel(warenkorbId));
				warenkoerbe.add(warenkorb);
			}
		}
		return warenkoerbe;
	}


	// Liest ein Warenkorb-Element aus der Datenbank anhand der Warenkorb-ID und Artikelnummer.
	public static WarenkorbElement leseWarenkorbElementByArtikelId(int warenkorbId, int artikelNummer) throws SQLException {
		// SQL-Abfrage, um das Warenkorb-Element basierend auf Warenkorb-ID und Artikelnummer abzurufen
		String sql = "SELECT * FROM " + WARENKORB_ARTIKEL_TABLE + " INNER JOIN " + ARTIKEL_TABLE +
				" ON " + WARENKORB_ARTIKEL_TABLE + "." + WARENKORB_ARTIKEL_ARTIKEL_NUMMER + " = " + ARTIKEL_TABLE + "." + ARTIKEL_NUMMER +
				" WHERE " + WARENKORB_ARTIKEL_WARENKORB_ID + " = ? AND " + WARENKORB_ARTIKEL_ARTIKEL_NUMMER + " = ?";
		try (Connection conn = DriverManager.getConnection(CONN_STRING);
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, warenkorbId);
			stmt.setInt(2, artikelNummer);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				// Den Artikel erstellen
				Artikel artikel = new Artikel(
						rs.getInt(ARTIKEL_NUMMER),
						rs.getString(ARTIKEL_NAME),
						rs.getString(ARTIKEL_BESCHREIBUNG),
						rs.getInt(ARTIKEL_PREIS),
						rs.getString(ARTIKEL_GROESSE),
						rs.getBytes(ARTIKEL_BILD),
						rs.getInt(ARTIKEL_ANZAHL)
						);

				// WarenkorbElement erstellen
				WarenkorbElement element = new WarenkorbElement(
						rs.getInt(WARENKORB_ARTIKEL_ID),
						new Warenkorb(warenkorbId, null, null, null, 1),
						artikel,
						rs.getInt("menge"),
						rs.getInt(WARENKORB_ARTIKEL_STATUS)
						);

				return element;
			} else {
				return null;
			}
		}
	}


	// Liest einen Kunden aus der Datenbank anhand seiner E-Mail-Adresse.
	public static Kunde leseKundeByEmail(String email) throws SQLException {
		Kunde kunde = null;
		String query = "SELECT * FROM " + KUNDE_TABLE + " WHERE " + KUNDE_EMAIL + " = ?";
		try (Connection conn = DriverManager.getConnection(CONN_STRING);
				PreparedStatement pstmt = conn.prepareStatement(query)) {
			pstmt.setString(1, email);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				kunde = new Kunde(
						rs.getInt(KUNDE_ID),
						rs.getString(KUNDE_NAME),
						rs.getString(KUNDE_PASSWORT),
						rs.getString(KUNDE_EMAIL),
						rs.getString(KUNDE_ADRESSE),
						rs.getString(KUNDE_TEL_NUMMER),
						rs.getString(KUNDE_PLZ),
						rs.getDate(KUNDE_REGISTRATIONSDATUM).toLocalDate()
						);
			}
		}
		return kunde;
	}


	// Liest einen Mitarbeiter aus der Datenbank anhand seiner E-Mail-Adresse.
	public static Mitarbeiter leseMitarbeiterByEmail(String email) throws SQLException {
		Mitarbeiter mitarbeiter = null;
		String query = "SELECT * FROM " + MITARBEITER_TABLE + " WHERE " + MITARBEITER_EMAIL + " = ?";
		try (Connection conn = DriverManager.getConnection(CONN_STRING);
				PreparedStatement pstmt = conn.prepareStatement(query)) {
			pstmt.setString(1, email);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				mitarbeiter = new Mitarbeiter(
						rs.getInt(MITARBEITER_ID),
						rs.getString(MITARBEITER_NAME),
						rs.getString(MITARBEITER_PASSWORT),
						rs.getString(MITARBEITER_EMAIL),
						rs.getString(MITARBEITER_ADRESSE),
						rs.getString(MITARBEITER_TEL_NUMMER),
						rs.getString(MITARBEITER_IBAN),
						rs.getString(MITARBEITER_PLZ),
						rs.getInt(MITARBEITER_GEHALT),
						rs.getString(MITARBEITER_ROLLE)
						);
			}
		}
		return mitarbeiter;
	}

}

