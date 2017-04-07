/**
 * Helping with the DH agreement handshake
 *
 * http://exampledepot.8waytrips.com/egs/javax.crypto/KeyAgree.html
 * https://docs.oracle.com/javase/7/docs/api/java/security/SecureRandom.html
 */

import java.math.BigInteger;
import java.security.*;
import java.security.spec.*;
import javax.crypto.*;
import javax.crypto.spec.*;

public class DHCrypt 
{
	private PrivateKey privKey;
	private PublicKey pubKey;
	private PublicKey otherPubKey;
	private SecretKey sharedSecret;

	public DHCrypt(BigInteger p, BigInteger g, int size) throws Exception
	{
		System.out.println("Starting DH Key Agreement...");
		//generate the key pair
		KeyPairGenerator gen = KeyPairGenerator.getInstance("DH");
		DHParameterSpec dhSpec = new DHParameterSpec(p,g,size);
		gen.initialize(dhSpec);
		KeyPair keyPair = gen.generateKeyPair();
		privKey = keyPair.getPrivate();
		pubKey = keyPair.getPublic();
	}

	public void setOtherKey(byte[] otherKeyBytes) throws Exception
	{
		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(otherKeyBytes);
    	KeyFactory keyFact = KeyFactory.getInstance("DH");
    	otherPubKey = keyFact.generatePublic(x509KeySpec);
	}

	public void generateSharedSecret() throws Exception
	{
		if(otherPubKey == null)
			throw new Exception("Error: other key has not been set");

		KeyAgreement ka = KeyAgreement.getInstance("DH");
    	ka.init(privKey);
    	ka.doPhase(otherPubKey, true);
    	sharedSecret = ka.generateSecret("AES");
    	if(sharedSecret == null)
    		throw new Exception("Unable to generate secret key");

		System.out.println("Generated shared key successfully!\n");
	}

	public PrivateKey getPrivate()
	{
		return privKey;
	}

	public PublicKey getPublic()
	{
		return pubKey;
	}

	public SecretKey getShared()
	{
		return sharedSecret;
	}
}






