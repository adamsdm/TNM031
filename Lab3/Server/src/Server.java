import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.security.KeyStore;

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
	private static final String FILES_FOLDER = "../assets/files/";

	private static final String STOREPASSWD = "changeit";
	private static final String ALIASPASSWD = "changeit";
	
	private PrintWriter out;
	private BufferedReader in;
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

			in = new BufferedReader( new InputStreamReader(incoming.getInputStream() ) );
			out = new PrintWriter(incoming.getOutputStream(), true);
			
			String str;
			
			while(! (str = in.readLine()).equals("quit") ){
				
				System.out.println(">>> SSLSERVER: Recieved input '" + str +"'");
				
				switch(str) {
				case "-u":
					recieveFile();
					break;
				case "-d":
					sendFile();
					break;
				case "-del":
					deleteFile();
					break;
				case "-ls":
					listFiles();
				default:
					out.println(">>> SSLSERVER::USAGE: [-u] --upload file [-d] --download file [-del] --delete file [-ls] --list files");
					break;
				}
			}
			
			System.out.println(">>> SSLSERVER: Shutting down");
			incoming.close();
			
		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}
		
	}
	
	private void sendFile(){
		try {
			String filename = in.readLine();
			System.out.println(filename);
			BufferedReader br = new BufferedReader(new FileReader(FILES_FOLDER + filename));
			out.println("OK");
			
			// Start reading and sending file
			String line = br.readLine();
			while(line != null){
				out.println(line);
				line = br.readLine();
			}
			
			out.println("DONE");
			
		} catch (IOException e) {
			out.println("FAIL");
			e.printStackTrace();
		}
		
		out.println(">>> SSLSERVER: Done");
	}
	
	private void recieveFile(){
		try {
			String filename = in.readLine();
			
			System.out.println(">>> SSLSERVER: Recieving file: " + filename);
			
			PrintWriter writer = new PrintWriter(FILES_FOLDER + filename, "UTF-8");
			
			while(true){
				String input = in.readLine();
				if(input.equals("DONE"))
					break;
				writer.println(input);
				
			}
			
			
			out.println(">>> SSLSERVER: Upload complete");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void deleteFile(){
		try {
			String filename = in.readLine();
			
			File file = new File(FILES_FOLDER + filename);
			
			if(file.delete()){
				out.println(">>> SSLSERVER: Removed file: " + filename);
			} else {
				out.println(">>> SSLSERVER: Couldn't remove file " + filename);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	// TODO: Not working properly, if client requests -ls no more commands can be inputed
	private void listFiles(){
		File folder = new File(FILES_FOLDER);
		File[] listOfFiles = folder.listFiles();
		
		String output = ">>> SSLSERVER: files on server [";

		for (int i = 0; i < listOfFiles.length; i++) {
	    	
			if (listOfFiles[i].isFile()) {
				output += listOfFiles[i].getName() + " ";
	    	} else if (listOfFiles[i].isDirectory()) {
	    		output += listOfFiles[i].getName() + "/ ";
	    	}
	    }
		output+= ']';
	    
		out.println(output);
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
