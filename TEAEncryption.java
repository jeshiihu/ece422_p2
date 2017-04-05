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

	// public int[] strToIntArr(String str)
	// {
	// 	char [] chArr = str.toCharArray();
	// 	int[] intArr = new int[chArr.length];
		
	// 	for(int i=0; i<chArr.length;i++){
	// 		intArr[i] = chArr[i];
 // 		}
		
	// 	return intArr;
	// }

	// public String intArrToStr(int[] lArr)
	// {
	// 	String str = "";
		
	// 	for(int l : lArr)
 // 			str += (char)l;
		
	// 	return str;
	// }

	public long[] byteArrToLongArr(byte[] bytes)
	{
		long[] l = new long[bytes.length];
		
		for(int i=0; i<bytes.length;i++)
		{
			Byte currByte = bytes[i];
			l[i] = currByte.longValue();
 		}
		
		return l;
	}

	public byte[] LongArrToByteArr(long[] lArr)
	{
		byte[] b = new byte[lArr.length];
		
		for(int i=0;i<lArr.length;i++)
		{
			Long currLong = lArr[i];
 			b[i] = currLong.byteValue();
		}
		
		return b;
	}
}