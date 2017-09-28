import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

public class Client {
	
	private InetAddress host;
	private int port;
	
	private static final int DEFAULT_PORT = 8189;
	
	private static final String KEYSTORE = "../assets/jpatkeystore.ks";
	private static final String TRUSTSTORE = "../assets/jpattruststore.ks";

	private static final String STOREPASSWD = "changeit";
	private static final String ALIASPASSWD = "changeit"; 
	
	private BufferedReader socketIn;
	private PrintWriter socketOut;
	private BufferedReader inputReader;
	
	public Client(InetAddress host, int port){
		this.host = host;
		this.port = port;
	}
	
	public void run(){
		
		try {
			
			KeyStore ks = KeyStore.getInstance("JCEKS");
			ks.load(new FileInputStream(KEYSTORE), STOREPASSWD.toCharArray());
			
			KeyStore ts = KeyStore.getInstance("JCEKS");
			ts.load(new FileInputStream(TRUSTSTORE), STOREPASSWD.toCharArray());
			
			KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
			kmf.init(ks, ALIASPASSWD.toCharArray());
			
			TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
			tmf.init(ts);
			
			SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
			
			SSLSocketFactory sslFact = sslContext.getSocketFactory();
			SSLSocket client = (SSLSocket) sslFact.createSocket(host, port);
			client.setEnabledCipherSuites(client.getSupportedCipherSuites());
			
			inputReader = new BufferedReader(new InputStreamReader(System.in));
			socketIn = new BufferedReader(new InputStreamReader(client.getInputStream()));
			socketOut = new PrintWriter(client.getOutputStream(), true);
			
			
			
			String str;
			System.out.print("> ");
			while(! (str = inputReader.readLine()).equals("quit") ){
				
				switch(str) {
				case "-u":
					sendFile();
					break;
				case "-d":
					recieveFile();
					break;
				case "-del":
					deleteFile();
					break;
				case "-ls":
					socketOut.println("-ls");
					break;
				default:
					System.out.println(">>> SSLCLIENT::USAGE: [-u] --upload file [-d] --download file [-del] --delete file [-ls] --list files");
					System.out.print("> ");
					continue; // Continue here since the nothing will be sent to the server
				}

				System.out.println(socketIn.readLine());
				System.out.print("> ");
				
			}
					
		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}
	}
	
	private void sendFile(){
		System.out.print("Enter filename: ");
		
		try {
			String filename = inputReader.readLine();
			socketOut.println("-u");
			socketOut.println(filename);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private void recieveFile(){
		System.out.print("Enter filename: ");
		
		try {
			String filename = inputReader.readLine();
			socketOut.println("-d");
			socketOut.println(filename);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void deleteFile(){
		System.out.println("> Enter filename: ");
	}
	
	public static void main(String[] args) {
		
		try {
			InetAddress host = InetAddress.getLocalHost();
			int port = DEFAULT_PORT;
			
			if(args.length > 0){
				port = Integer.parseInt(args[0]);
			}
			
			if(args.length > 1){
				host = InetAddress.getByName(args[1]);
			}
			
			Client client = new Client(host, port);
			client.run();
			
		} catch (UnknownHostException uhx ) {
			System.out.println(uhx);
			uhx.printStackTrace();
		}

	}

}
