package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.sun.net.httpserver.HttpServer;



public class BG_Server {

	public static void main(String[] args) {
		try {
			// Initialisierung der Serveradresse und Portnummer
			InetAddress inet = InetAddress.getByName("localhost");
			InetSocketAddress addr = new InetSocketAddress(inet, 8080); 
			HttpServer server = HttpServer.create(addr, 0);

			//  den Kontext und den zugehörigen Handler registrieren
			server.createContext("/", new BG_Handler());

			//  den Executor für den Server setzen, um parallele Anfragen zu behandeln
			server.setExecutor(Executors.newCachedThreadPool());

			//  den Server starten
			server.start();
			System.out.println("BekleidungsgeschaeftServer läuft - Zum Beenden Eingabetaste drücken");

			//  auf Benutzereingabe warten
			try {
				System.in.read();
			} catch (IOException e) {
				e.printStackTrace();
			}

			//  den Server und den Executor stoppen
			System.out.println("BekleidungsgeschaeftServer wird gestoppt");
			server.stop(0);
			((ExecutorService) server.getExecutor()).shutdown();

		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
