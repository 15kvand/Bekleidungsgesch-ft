/**
 * 
 */
/**
 * 
 */
module BGV2_Client {
	requires javafx.graphics;
	requires javafx.fxml;
	requires javafx.controls;
	requires BGV2_Klassen;
	requires java.sql;
	requires java.net.http;
	requires javafx.swing;
	requires java.desktop;
	requires javafx.base;
	opens client to javafx.fxml;
	exports client;

}