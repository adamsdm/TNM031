import java.math.BigInteger;
import java.util.Random;

public class RSA {
	private BigInteger n, d, e;
	//number of bits used for p and q
	private int bitLength = 512;
	
	public RSA()
	{
		Random rnd = new Random();
		BigInteger p = BigInteger.probablePrime(bitLength, rnd);
		BigInteger q;
		do {
			q = BigInteger.probablePrime(bitLength, rnd);
		}while (q == p);
		
		n = p.multiply(q);
		
	}
	
	
	public static void main(String[] args) {
		RSA r = new RSA();
		
	}

}
