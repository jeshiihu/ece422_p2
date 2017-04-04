/**
 * JNI code to interface c code for the TEA
 * encryption algorithm provided
 *
 * encrypt.c
 * decrypt.c
 */

public class TEAEncryption
{	
	public TEAEncryption() {}

	public native void encrypt(long[] v, long[] k);
	public native void decrypt(long[] v, long[] k);

	public long[] strToLongArr(String str)
	{
		char [] chArr = str.toCharArray();
		long[] longStr = new long[chArr.length];
		
		for(int i=0; i<chArr.length;i++){
			longStr[i] = chArr[i];
 		}
		
		return longStr;
	}

	public String longArrToStr(long[] lArr)
	{
		String str = "";
		
		for(long l : lArr)
 			str += (char)l;
		
		return str;
	}

	public long[] byteArrToLongArr(byte[] bytes)
	{
		long[] l = new long[bytes.length];
		
		for(int i=0; i<bytes.length;i++){
			l[i] = bytes[i];
 		}
		
		return l;
	}

	public byte[] LongArrToByteArr(long[] lArr)
	{
		byte[] b = new byte[lArr.length];
		
		for(int i=0;i<lArr.length;i++)
 			b[i] += lArr[i];
		
		return b;
	}
}