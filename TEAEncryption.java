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
		int[] iData = bytesToInts(data);
		int[] iKey = bytesToInts(key);
		encrypt(iData, iKey);

		return intsToBytes(iData);
	}

	public byte[] teaDecrypt(byte[] data, byte[] key)
	{
		int[] iData = bytesToInts(data);
		int[] iKey = bytesToInts(key);
		decrypt(iData, iKey);

		return removePadding(intsToBytes(iData));
	}

	private native void encrypt(int[] v, int[] k);
	private native void decrypt(int[] v, int[] k);

	// http://stackoverflow.com/questions/9303604/rounding-up-a-number-to-nearest-multiple-of-5
	private int roundUpClosestMult(int mult, int num) 
	{
		int nearest = ((num + (mult)/2)/mult) * mult;
		if(nearest < num)
			nearest = nearest + mult;

		if(nearest <= mult)
			nearest = mult*2;
		
		return nearest;
	}

	private int[] bytesToInts(byte[] bytes)
	{
		// use bytebuffer and pad!
		// https://docs.oracle.com/javase/7/docs/api/java/nio/ByteBuffer.html
		int bytesLen = bytes.length;
		int mult = roundUpClosestMult(4, bytesLen);
		int[] ints = new int[mult/4];

		ByteBuffer bBuf = ByteBuffer.allocate(mult);

		byte[] paddedBuffer = new byte[mult];

		for(int i = 0; i<mult; i++)
		{
			int diff = mult-bytesLen;
			if(i < diff)
				paddedBuffer[i] = (byte)0;
			else
				paddedBuffer[i] = bytes[i-diff];
		}

		bBuf.put(paddedBuffer);
		bBuf.order(ByteOrder.BIG_ENDIAN);
		bBuf.flip();

		// process all
		while (bBuf.remaining() > 0)
			ints[bBuf.position()/4] = bBuf.getInt();

		return removePadding(ints);
	}

	private int[] removePadding(int[] ints)
	{
		// get rid of padded 0s
		int idx = ints.length-1;
		for(;idx>=0;idx--)
		{
			if(ints[idx] != 0)
				break;
		}

		int[] newInts = new int[idx+1];
		for(int j = 0; j< newInts.length; j++)
			newInts[j] = ints[j];

		return newInts;
	}

	private byte[] removePadding(byte[] b)
	{
		int idx = 0;
		for(;idx<b.length;idx++)
		{
			if(b[idx] != 0)
				break;
		}

		// System.out.println("INDEX: "+ Integer.toString(idx));
		byte[] newBytes = new byte[b.length-idx];
		for(int j = 0; j< newBytes.length; j++)
			newBytes[j] = b[j+idx];
			
		return newBytes;
	}

	private byte[] intsToBytes(int[] ints)
	{
		ByteBuffer buffer = ByteBuffer.allocate(ints.length*4);
		for(int i=0;i<ints.length;i++)
			buffer.putInt(ints[i]);

		return buffer.array();
	}
}