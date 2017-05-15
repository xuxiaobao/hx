package com.miaosu.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import sun.misc.BASE64Decoder;

/**
 * @author JavaDigest
 *
 */
public class RSAUtil
{
	/**
	 * String to hold name of the encryption algorithm.
	 */
	public static final String ALGORITHM = "RSA";

	//亚飞达公钥
	public static String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnhpp6TcD/oC8/YGSRRH+6VX9n+AYyNSqVf9XvQ1YsUypRR0kuN+vEWO817yM7FKC/qnqfoKm8ThxyPXem9cmKVJRG6V/RxxXZk3/nOb7YpIk0KkF+KT7yofBhbai0aEvICoXQe3iAMY48wyTh4KjeQr2zQy+TF8jVYRJbpgF89wIDAQAB";

	//public static String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC6n28XwOdmHeoHxbldJzGXBn4kwrjufROmWjks6l+anNc9Ws6gYwZCuBKDvM20GPpqq6BjhdDp3E0qv+WUrspdiOwL4ivS2iGbYnRmwQdFpG3jQn1wyHssTenLV1K20GYakw/NUBMuhHbkN+NtEjH7L70BmHTfs9StwaGr/+6JgwIDAQAB";

	public static String privateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBALqfbxfA52Yd6gfFuV0nMZcGfiTCuO59E6ZaOSzqX5qc1z1azqBjBkK4EoO8zbQY+mqroGOF0OncTSq/5ZSuyl2I7AviK9LaIZtidGbBB0WkbeNCfXDIeyxN6ctXUrbQZhqTD81QEy6EduQ3420SMfsvvQGYdN+z1K3Boav/7omDAgMBAAECgYEAp/lRGOQ8YyGRwCUrzri0XecuKxBJO//Aa/7Rb6gVHSkGYucVDC+VNwBPSdvqmqvQ2apY1rtfat8rZcsLbWRWJ4hc59/HaKH9/iLeNBEdHNu2V/oQaADEdJ9Brc4k5IU+eyQr/GbYnYhZxz7+dtn60rDWH3phrg3P4sbMUSNz8uECQQDlsjhweHug0J/EHr/Fl3lXMslkxq9dI+DQDhDxQz82EsRP/cWebK95AXGHJ2I8B9Hk0Hxh4fn+30x4OfbTOTPPAkEAz/55EVvQAUTC4lARMLmi+4pq80o/+2e6b+0QzCg10YzhxLtnYmZnSvZyq263HCaZhehnoWYgFsVKsL/7GXyYDQJBALCA2s3SR22z4pEZ79MuKpfO9uLxqh+wSjiWFn7OZexvT1sIbqrmaZBag5qPsPTgLXHeozW0KB6qCGD6aai19BECQGlqAHQSwz4RWnAWwk+elgLEaWX6iKjjLnNRhATkkDovG7F4hXIykkFE3GMUTst+qKpcWkFLoBHKVgt0kU+efPUCP1sMFyuYNIJFNzvQTTpCcNtnX5mGNZddJD89Ey/emjYnrNFlRMrt3OpsLHKIEKSMUPoV9BfNebtv/cHw9BTOhg==";

	/**
	 * Encrypt the plain text using public key.
	 *
	 * @param text
	 *            : original plain text
	 * @param key
	 *            :The public key
	 * @return Encrypted text
	 * @throws NoSuchPaddingException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 * @throws Exception
	 */
	public static String encrypt(String text, Key key) throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException
	{
		byte[] cipherText = null;

		// get an RSA cipher object and print the provider
		final Cipher cipher = Cipher.getInstance(ALGORITHM);
		// encrypt the plain text using the public key
		cipher.init(Cipher.ENCRYPT_MODE, key);
		cipherText = cipher.doFinal(text.getBytes());

		return getHexString(cipherText);
	}

	public static String signPrivateKey(String text) throws InvalidKeyException, Exception
	{
		// MD2withRSA、MD5withRSA 或 SHA1withRSA
		Signature signature = Signature.getInstance("SHA1withRSA");
		signature.initSign(getPrivateKey());
		signature.update("miaosu".getBytes());
		byte[] signBytes = signature.sign();

		return getHexString(signBytes);

	}

	/**
	 * Decrypt text using private key.
	 *
	 * @param text
	 *            :encrypted text
	 * @param key
	 *            :The private key
	 * @return plain text
	 * @throws NoSuchPaddingException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 * @throws DecoderException
	 * @throws UnsupportedEncodingException
	 * @throws Exception
	 */
	public static String decrypt(String text, Key key) throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException, DecoderException, UnsupportedEncodingException
	{
		byte[] dectyptedText = null;

		// get an RSA cipher object and print the provider
		final Cipher cipher = Cipher.getInstance(ALGORITHM);

		// decrypt the text using the private key
		cipher.init(Cipher.DECRYPT_MODE, key);
		dectyptedText = cipher.doFinal(Hex.decodeHex(text.toCharArray()));

		return new String(dectyptedText,"utf-8");
	}

	public static boolean verifyPublicKey(String text) throws InvalidKeyException, Exception
	{

		Signature signature = Signature.getInstance("SHA1withRSA");

		signature.initVerify(getPublicKey());

		signature.update("miaosu".getBytes());

		return signature.verify(text.getBytes());
	}

	public static byte[] hexStringToByteArray(String s)
	{
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2)
		{
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
		}
		return data;
	}

	public static String getHexString(byte[] b)
	{
		String result = "";
		for (int i = 0; i < b.length; i++)
		{
			result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
		}
		return result;
	}

	public static PrivateKey getPrivateKey() throws Exception
	{
		byte[] keyByteArray = new BASE64Decoder().decodeBuffer(privateKey);
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyByteArray);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		return keyFactory.generatePrivate(pkcs8KeySpec);
	}

	public static PublicKey getPublicKey() throws Exception
	{
		byte[] keyByteArray = new BASE64Decoder().decodeBuffer(publicKey);
		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyByteArray);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		return keyFactory.generatePublic(x509KeySpec);
	}
}