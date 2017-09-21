
public class Server {

	private final int PORT = 8000;
	
	private static final String KEYSTORE_PASSWORD = "123456";
	private static final String TRUSTSTORE_PASSWORD = "abcdefg";
	
	private static final String KEYSTORE_PATH = "assets/lab3keystore.ks";
	private static final String TRUSTSTORE_PATH = "assets/lab3truststore.ks";
	
	public static void main(String[] args) {
	
		System.out.println("Hello server");
	}

}
