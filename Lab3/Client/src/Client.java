import java.io.BufferedReader;
import java.io.FileInputStream;
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

	private static final String STOREPASSWD = "changeit";
	private static final String ALIASPASSWD = "changeit"; 
	
	private static final String KEYSTORE_PATH = "../assets/clientKeystore.ks";
	private static final String TRUSTSTORE_PATH = "../assets/clientTruststore.ks";
	
	
	
	public Client(InetAddress host, int port){
		this.host = host;
		this.port = port;
	}
	
	public void run(){
		
		try {
			
			KeyStore ks = KeyStore.getInstance("JCEKS");
			ks.load(new FileInputStream(KEYSTORE_PATH), STOREPASSWD.toCharArray());
			
			KeyStore ts = KeyStore.getInstance("JCEKS");
			ts.load(new FileInputStream(TRUSTSTORE_PATH), STOREPASSWD.toCharArray());
			
			KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
			kmf.init(ks, ALIASPASSWD.toCharArray());
			
			TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
			tmf.init(ts);
			
			SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
			
			SSLSocketFactory sslFact = sslContext.getSocketFactory();
			SSLSocket client = (SSLSocket) sslFact.createSocket(host, port);
			client.setEnabledCipherSuites(client.getSupportedCipherSuites());
			
			BufferedReader socketIn ;
			socketIn = new BufferedReader(new InputStreamReader(client.getInputStream()));
			PrintWriter socketOut = new PrintWriter(client.getOutputStream(), true);
			
			String numbers = "1.2 3.4 5.6";
			System.out.println("Adding the numbers " + numbers + " together securely");
			socketOut.println(numbers);
			
			System.out.println(socketIn.readLine());
			socketOut.println("");
					
		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}
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
