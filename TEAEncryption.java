/**
 * JNI code to interface c code for the TEA
 * encryption algorithm provided
 *
 * encrypt.c
 * decrypt.c
 */

public class TEAEncryption
{
	private native void encrypt(long[] v, long[] k);
	private native void decrypt(long[] v, long[] k);
}