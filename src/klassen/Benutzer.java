package klassen;

public abstract class Benutzer {
    protected String name;
    protected String passwort;
    protected String email;
    protected String telNummer;
    protected String adresse;
    protected int id;
    protected String plz;

    public Benutzer(String name, String passwort, String email, String telNummer, String adresse, int id, String plz) {
        this.name = name;
        this.passwort = passwort;
        this.email = email;
        this.telNummer = telNummer;
        this.adresse = adresse;
        this.id = id;
        this.plz = plz;
    }

    
    // Gibt den Namen zurück.
    public String getName() {
        return name;
    }

    
    // Setzt den Namen.
    public void setName(String name) {
        this.name = name;
    }

    
    // Gibt das Passwort zurück.
    public String getPasswort() {
        return passwort;
    }

    
    // Setzt das Passwort.
    public void setPasswort(String passwort) {
        this.passwort = passwort;
    }

    
    // Gibt die E-Mail-Adresse zurück.
    public String getEmail() {
        return email;
    }

    
    // Setzt die E-Mail-Adresse.
    public void setEmail(String email) {
        this.email = email;
    }

    
    // Gibt die Telefonnummer zurück.
    public String getTelNummer() {
        return telNummer;
    }

    
    // Setzt die Telefonnummer.
    public void setTelNummer(String telNummer) {
        this.telNummer = telNummer;
    }

    
    // Gibt die Adresse zurück.
    public String getAdresse() {
        return adresse;
    }

    
    // Setzt die Adresse.
    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    
    // Gibt die Benutzer-ID zurück.
    public int getId() {
        return id;
    }

    
    // Setzt die Benutzer-ID.
    public void setId(int id) {
        this.id = id;
    }

    
    // Gibt die Postleitzahl zurück.
    public String getPlz() {
        return plz;
    }

    
    // Setzt die Postleitzahl.
    public void setPlz(String plz) {
        this.plz = plz;
    }
}