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
	
	private static final String KEYSTORE_PASSWORD = "123456";
	private static final String TRUSTSTORE_PASSWORD = "abcdefg";
	
	private static final String KEYSTORE_PATH = "../assets/lab3keystore.ks";
	private static final String TRUSTSTORE_PATH = "../assets/lab3truststore.ks";
	
	/** Constructor
	 * 
	 * @param port The port that the server will listen to
	 */
	Server(int port){
		this.port = port;
	}
	
	public void run(){
		
		try {
			KeyStore ks = KeyStore.getInstance("JCEKS");
			ks.load( new FileInputStream( KEYSTORE_PATH ), KEYSTORE_PASSWORD.toCharArray() );
			
			KeyStore ts = KeyStore.getInstance("JCEKS");
			ts.load( new FileInputStream( TRUSTSTORE_PATH ), TRUSTSTORE_PASSWORD.toCharArray() );
			
			KeyManagerFactory kmf = KeyManagerFactory.getInstance("SUNX509");
			kmf.init(ks, TRUSTSTORE_PASSWORD.toCharArray());
			
			TrustManagerFactory tmf = TrustManagerFactory.getInstance("SUNx509");
			tmf.init(ts);
			
			SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
			
			SSLServerSocketFactory sslServerFactory = sslContext.getServerSocketFactory();
			SSLServerSocket sss = (SSLServerSocket) sslServerFactory.createServerSocket(port);
			sss.setEnabledCipherSuites( sss.getSupportedCipherSuites() );
			SSLSocket incoming = (SSLSocket) sss.accept();
			
			BufferedReader in;
			in = new BufferedReader( new InputStreamReader(incoming.getInputStream() ) );
			
			PrintWriter out = new PrintWriter(incoming.getOutputStream(), true);
			
			String str;
			
			
			
			while(! (str = in.readLine()).equals("") ){
				
				double result = 0;
				StringTokenizer st = new StringTokenizer(str);
				
				try {
					while(st.hasMoreTokens()){
						Double d = new Double(st.nextToken());
						result += d.doubleValue();
					}
					out.print("The result is " + result);
				} catch (NumberFormatException nfe) {
					out.println("Sorry, your list contains an invalid number");
				}
			}
			
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
