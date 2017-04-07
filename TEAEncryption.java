/**
 * JNI code to interface c code for the TEA
 * encryption algorithm provided
 *
 * encrypt.c
 * decrypt.c
 */
import java.nio.*;
import java.lang.Math;

public class TEAEncryption
{	
	public TEAEncryption() {}
	
	public byte[] teaEncrypt(byte[] data, byte[] key)
	{
		long[] lData = byteArrToLongArr(data);
		long[] lKey = byteArrToLongArr(key);
		encrypt(lData, lKey);

		return longArrToByteArr(lData);
	}

	public byte[] teaDecrypt(byte[] data, byte[] key)
	{
		long[] lData = byteArrToLongArr(data);
		long[] lKey = byteArrToLongArr(key);
		decrypt(lData, lKey);

		// byte[] b = removePadding(longArrToByteArr(lData));

		return longArrToByteArr(lData);
	}

	private native void encrypt(long[] v, long[] k);
	private native void decrypt(long[] v, long[] k);

	// http://stackoverflow.com/questions/9303604/rounding-up-a-number-to-nearest-multiple-of-5
	private int roundUpClosestMult8(int num) 
	{
		int nearest = ((num + 4)/8) * 8;
		if(nearest < num)
			nearest = nearest + 8;

		if(nearest <= 8)
			nearest = 16;
		
    	return nearest;
	}

	private long[] byteArrToLongArr(byte[] bytes)
	{
		// use bytebuffer and pad!
		// http://stackoverflow.com/questions/4485128/how-do-i-convert-long-to-byte-and-back-in-java
		// long is 64 bits, therefore 8 bytes
		// ensure that the length of bytes is a multiple of 8
		int bytesLen = bytes.length;
		int mult8 = roundUpClosestMult8(bytesLen);

		byte[] paddedBytes = new byte[mult8];
		int diff = mult8 - bytesLen;
		for(int i = 0; i<mult8;i++)
		{	// pad 0s at the end!
			if(i >= bytesLen)
				paddedBytes[i] = (byte)0;
			else
				paddedBytes[i] = bytes[i];
		}

		int longLen = (int)Math.ceil(paddedBytes.length/8);
		long[] l = new long[longLen];
		int ind = 0;

		for(int i = 0; i< paddedBytes.length; i+=8)
		{
			byte[] eightBytes = new byte[8];
			for(int j=0; j<8;j++)
				eightBytes[j] = paddedBytes[i+j];

			ByteBuffer buf = ByteBuffer.allocate(Long.BYTES);
			buf.put(eightBytes);
			buf.flip();
			l[ind] = buf.getLong();
			ind++;
		}

		return l;
	}

	private byte[] removePadding(byte[] b)
	{
		// get rid of padded 0s
		int idx = b.length-1;
		for(;idx>=0;idx--)
		{
			if(b[idx] != 0)
				break;
		}

		byte[] newB = new byte[idx+1];
		for(int i = 0; i<idx+1; i++)
			newB[i] = b[i];

		return newB;
	}

	private byte[] longArrToByteArr(long[] lArr)
	{
		byte[] b = new byte[lArr.length*8];
		
		for(int i=0;i<lArr.length;i++)
		{	
			ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
    		buffer.putLong(lArr[i]);
    		byte[] eightBytes = buffer.array();
    		int ind = 0;
			for(int j=i*8;j<(i*8)+8;j++)
			{
				b[j] = eightBytes[ind];
				// System.out.println(b[j]);
				ind++;
			}
		}
		
		return b;
	}
}