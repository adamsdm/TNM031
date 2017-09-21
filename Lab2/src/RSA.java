import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class RSA {
	private BigInteger n, e, p, q, d;
	//number of bits used for p and q
	private int bitLength = 1024;
	
	public RSA()
	{
		Random rnd = new Random();
		BigInteger p = BigInteger.probablePrime(bitLength, rnd);
		BigInteger q;
		do {
			q = BigInteger.probablePrime(bitLength, rnd);
		}while (q == p);
		
		n = p.multiply(q);

		BigInteger temp = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));
		
		// Find a 'e' where gcd(e, (p-1)(q-1)) = 1
		e = new BigInteger("3");
		while(temp.gcd(e).intValue() > 1){
			e = e.add(new BigInteger("2"));
		}
		
		// de = 1 (=) d = 1/e (mod(p-1)(q-1))
		d = e.modInverse(temp);
		
	}
	
	public String encrypt(String text){
		BigInteger enc = new BigInteger(text.getBytes());
		enc = enc.modPow(e, n);
		
		return enc.toString();
	}
	
	public String decrypt(String text){
		
		BigInteger enc = new BigInteger(text);
		BigInteger dec = enc.modPow(d, n);
		
		return new String( (dec).toByteArray() );
	}
	
	
	public BigInteger getN() {
		return n;
	}


	public BigInteger getE() {
		return e;
	}


	public static void main(String[] args) {

		RSA rsa = new RSA();
		
		String original = "I just got this working";
		
		String encrypted = rsa.encrypt(original);
		String decrypted = rsa.decrypt(encrypted);
		
		System.out.println(decrypted);
		
	}

}
