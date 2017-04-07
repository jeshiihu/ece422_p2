/**
 * Java helper to encrypt and decrypt using sha-1
 */
import java.security.MessageDigest;


public class HashHelper {

	private MessageDigest md;

	public HashHelper(String alg)
	{	
		try
		{
			md = MessageDigest.getInstance(alg);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public String encrypt(String str) throws Exception
	{
		str = str.replaceAll("(\\s|\\n)", "");
		md.update(str.getBytes("UTF8"));
		return bytesToHex(md.digest());
	}

	// http://www.herongyang.com/Cryptography/SHA1-Message-Digest-in-Java.html
	// used to compare properly!
	private String bytesToHex(byte[] b) {
      char hexDigit[] = {'0', '1', '2', '3', '4', '5', '6', '7',
                         '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
      StringBuffer buf = new StringBuffer();
      for (int j=0; j<b.length; j++) {
         buf.append(hexDigit[(b[j] >> 4) & 0x0f]);
         buf.append(hexDigit[b[j] & 0x0f]);
      }

      return buf.toString();
   }

	public boolean matches(String shadow, String str)
	{
		String encryptedStr = "";
		try
		{
			encryptedStr = encrypt(str);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}

		System.out.println("shadow: " + shadow + ", Str: " + encryptedStr);
		return encryptedStr.equals(shadow);
	}
}