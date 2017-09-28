import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.security.KeyStore;
import java.util.StringTokenizer;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManagerFactory;

public class Server {

	private int port = 8000;
	private static final int DEFAULT_PORT = 8189;
	
	private static final String KEYSTORE = "../assets/jpatkeystore.ks";
	private static final String TRUSTSTORE = "../assets/jpattruststore.ks";

	private static final String STOREPASSWD = "changeit";
	private static final String ALIASPASSWD = "changeit";
	
	
	/** Constructor
	 * 
	 * @param port The port that the server will listen to
	 */
	Server(int port){
		this.port = port;
	}
	
	public void run(){
		System.out.println(">>> SSLSERVER: Waiting for connection...");
		try {
			KeyStore ks = KeyStore.getInstance("JCEKS");
			ks.load( new FileInputStream( KEYSTORE ), STOREPASSWD.toCharArray() );
			
			KeyStore ts = KeyStore.getInstance("JCEKS");
			ts.load( new FileInputStream( TRUSTSTORE ), STOREPASSWD.toCharArray() );
			
			KeyManagerFactory kmf = KeyManagerFactory.getInstance("SUNX509");
			kmf.init(ks, ALIASPASSWD.toCharArray());
			
			TrustManagerFactory tmf = TrustManagerFactory.getInstance("SUNx509");
			tmf.init(ts);
			
			SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
			
			SSLServerSocketFactory sslServerFactory = sslContext.getServerSocketFactory();
			SSLServerSocket sss = (SSLServerSocket) sslServerFactory.createServerSocket(port);
			sss.setEnabledCipherSuites( sss.getSupportedCipherSuites() );
			SSLSocket incoming = (SSLSocket) sss.accept();

			System.out.println(">>> SSLSERVER: Connection established");

			BufferedReader in;
			in = new BufferedReader( new InputStreamReader(incoming.getInputStream() ) );
			
			PrintWriter out = new PrintWriter(incoming.getOutputStream(), true);
			
			String str;
			
			while(! (str = in.readLine()).equals("quit") ){
				
				System.out.println(">>> SSLSERVER: Recieved input " + str);
				double result = 0;
				StringTokenizer st = new StringTokenizer(str);
				
				try {
					while(st.hasMoreTokens()){
						Double d = new Double(st.nextToken());
						result += d.doubleValue();
					}
					out.println(">>> SSLSERVER: The result is " + result);
				} catch (NumberFormatException nfe) {
					out.println(">>> SSLSERVER: Sorry, your list contains an invalid number");
				}
			}
			
			System.out.println(">>> SSLSERVER: Shutting down");
			incoming.close();
			
		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}
		
	}
	
	
	
	public static void main(String[] args) {
		int port = DEFAULT_PORT;
		
		if(args.length > 0){
			port = Integer.parseInt(args[0]);
		}
		
		Server server = new Server(port);
		server.run();
		
	}

}
