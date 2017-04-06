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

	public native void encrypt(long[] v, long[] k);
	public native void decrypt(long[] v, long[] k);

	// http://stackoverflow.com/questions/9303604/rounding-up-a-number-to-nearest-multiple-of-5
	private int roundUpClosestMult8(int num) 
	{
		int nearest = ((num + 4)/8) * 8;
		if(nearest < num)
			return nearest + 8;

    	return nearest;
	}

	public long[] byteArrToLongArr(byte[] bytes)
	{
		// use bytebuffer and pad!
		// http://stackoverflow.com/questions/4485128/how-do-i-convert-long-to-byte-and-back-in-java
		int bytesLen = bytes.length;
		System.out.println("sizeBytes: " + Integer.toString(bytes.length));

		// long is 64 bits, therefore 8 bytes
		// ensure that the length of bytes is a multiple of 8
		int mult8 = roundUpClosestMult8(bytesLen);
		System.out.println("near:" + Integer.toString(mult8));

		byte[] paddedBytes = new byte[mult8];
		if(mult8 != bytesLen) // not a multiple
		{
			int diff = mult8 - bytesLen;
			for(int i = 0; i<mult8;i++)
			{
				if(i <=diff)
					paddedBytes[i] = (byte)0;
				else
					paddedBytes[i] = bytes[i-diff];
			}
		}
		else
			paddedBytes = bytes;

		int longLen = (int)Math.ceil(paddedBytes.length/8);
		long[] l = new long[longLen];
		int ind = 0;

		// System.out.println("sizeBuff: " + Integer.toString(bufLen));
		System.out.println("sizeLong: " + Integer.toString(longLen));

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
		// {
		// 	// if(buf.remaining() < 8) // pad it
		// 	// {

		// 	// }

		// 	l[i] = buf.getLong();
		// 	i++;
		// }

		// long[] l = new long[bytes.length];
		
		// for(int i=0; i<bytes.length;i++)
		// {
		// 	Byte currByte = bytes[i];
		// 	l[i] = currByte.longValue();
 	// 	}
		
		return l;
	}

	public byte[] longArrToByteArr(long[] lArr)
	{
		byte[] b = new byte[lArr.length*8];
		
		for(int i=0;i<lArr.length;i++)
		{	
			ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
    		buffer.putLong(lArr[i]);
    		byte[] eightBytes = buffer.array();
    		int ind = 0;
			//0-7,8-15,16:
			for(int j=i*8;j<(i*8)+8;j++)
			{
				b[j] = eightBytes[ind];
				ind++;
			}
			// Long currLong = lArr[i];
 			// b[i] = currLong.byteValue();
		}
		
		return b;
	}
}