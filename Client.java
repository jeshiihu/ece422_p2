/**
 * Java program: client
 */

import javax.crypto.*;

public class Client {
	public static void main(String[] args) {
		System.loadLibrary("tea");

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
			System.out.println("key bytes: " + key.getEncoded().toString());
			long[] longKey = tea.byteArrToLongArr(key.getEncoded());
			System.out.println("key long: " + longKey.toString());
			byte[] ne = tea.LongArrToByteArr(longKey);
			System.out.println("new long: " + ne.toString());



			tea.encrypt(lval, longKey);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}