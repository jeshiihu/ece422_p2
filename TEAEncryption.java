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
}