/**
 * Java program: client
 */

import javax.crypto.*;

public class Client {
	public static void main(String[] args) {
		TEAEncryption tea = new TEAEncryption();

		String val = "cello";
		long[] lval = tea.strToLongArr(val);
		String re = tea.longArrToStr(lval);

		System.out.println("Orig: " + val);
		System.out.println("lval: " + lval.toString());
		System.out.println("re: " + re);

		SecretKey key;
		try
		{
			KeyGenerator keyGen = KeyGenerator.getInstance("AES");
			key = keyGen.generateKey();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}