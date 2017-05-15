package com.miaosu.util;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.binary.Hex;


import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class RSAUtilTaiyong {

    public static final String RSA = "RSA";
    
    //太永私钥
    static String privateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAI2IeNFq9YwLuC/CHSgbdjsHfPWT"+
	"KbZse5o+5NmBUtARAZA2Gpb1Tvl9cNWWjKvZdMY2WluY3rjMbZMf2bJMEzzaMZWEvc4qZRIQE6sw"+
	"WFSq1Vp98GIWLj4NFTMykXJRtE29dzowXFqf7bRZPoG/+hND5x2OV2Su55NED81q6gsVAgMBAAEC"+
	"gYBoL/JglxAzC92Gqqqz/6NFSPnf/XlTZQCIR4xH4T+JEzvHYUypm+OLHaCWWsErMGUaFKDnlN0/"+
	"xJ1VFLk422+832bLNpwMT1oBkFOfwh0/w/7HFvmo/NSURzR1gyzWixzPPA/Fy9Jw+HZqunvlTX4r"+
	"zxCY00d50xTgPsHleTVs8QJBAMkXU3BWhiiVKhFbVgfvKoiTvTf0MXoGSUrMPd0Bt7CGL5BuXMJL"+
	"uXbJp64XnibVyWEn2cptdb6FqBkVnb+WEdsCQQC0LeuUHu3zY0UDgrzrd5vwISpe68hmrk6mWTEm"+
	"QjnQDKIGEesZ7RKd0f8JfCyK7GqWaxXQGHmR6jtheQym60HPAkBdGaWRzx1R0K2nV4gfKEWi8fOa"+
	"4j295wcr0B+cIN1wps+CqPXdJr35FyfA3EJmbHhNuOuGYuMukCbhZjagqtQdAkEAi1HSjcbYsVC6"+
	"BXvq/puO7T+H1ru0SolPcVsYIQAmWdPa2Jy0UGi682N0M+5bNtSVmleVF0JFhmk6nyUNfmf8iwJB"+
	"ALWMyHIR9+yqWidPFEhoAI8rU+eXlBNxi9vR05BYMKy5cVkMI/InfAMIrPotQxRO8rsDRgsrdCzr"+
	"1vu4OolnTJQ=";
	//太永公钥
	String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCNiHjRavWMC7gvwh0oG3Y7B3z1kym2bHuaPuTZgVLQEQGQNhqW9U75fXDVloyr2XTGNlpbmN64zG2TH9myTBM82jGVhL3OKmUSEBOrMFhUqtVaffBiFi4+DRUzMpFyUbRNvXc6MFxan+20WT6Bv/oTQ+cdjldkrueTRA/NauoLFQIDAQAB";

    /**
     * 生成RSA密钥
     * 
     * @return
     * @throws Exception
     */
    public static RSADTO generateRSA() throws Exception {
        RSADTO rsa = null;

        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA);
            keyPairGenerator.initialize(1024);

            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            PublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
            PrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
            rsa = new RSADTO();
            String publicString = keyToString(publicKey);
            String privateString = keyToString(privateKey);
            rsa.setPublicKey(publicString);
            rsa.setPrivateKey(privateString);

        } catch (NoSuchAlgorithmException e) {
        	e.printStackTrace();
            throw new Exception(e.getMessage());
        }

        return rsa;
    }

    /**
     * BASE64 String 转换为 PublicKey
     * 
     * @param publicKeyString
     * @return
     * @throws Exception
     */
    public static PublicKey getPublicKey(String publicKeyString) throws Exception {
        PublicKey publicKey = null;
        BASE64Decoder base64Decoder = new BASE64Decoder();
        try {
            byte[] keyByteArray = base64Decoder.decodeBuffer(publicKeyString);
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyByteArray);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA);
            publicKey = keyFactory.generatePublic(x509KeySpec);
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }

        return publicKey;
    }

    /**
     * BASE64 String 转换为 PrivateKey
     * 
     * @param privateKeyString
     * @return
     * @throws Exception
     */
    public static PrivateKey getPrivateKey(String privateKeyString) throws Exception {
        PrivateKey privateKey = null;
        BASE64Decoder base64Decoder = new BASE64Decoder();

        try {
            byte[] keyByteArray = base64Decoder.decodeBuffer(privateKeyString);
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyByteArray);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA);
            privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }

        return privateKey;

    }

    /**
     * RSA 加密返回byte[]
     * 
     * @param content
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static byte[] encodeBytePrivate(byte[] content, PrivateKey privateKey) throws Exception {
        byte[] encodeContent = null;
        try {
            Cipher cipher = Cipher.getInstance(RSA);
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            encodeContent = cipher.doFinal(content);
        } catch (NoSuchAlgorithmException e) {
        	e.printStackTrace();
            throw new Exception(e.getMessage());
        } catch (NoSuchPaddingException e) {
        	e.printStackTrace();
            throw new Exception(e.getMessage());
        } catch (InvalidKeyException e) {
        	e.printStackTrace();
            throw new Exception(e.getMessage());
        } catch (IllegalBlockSizeException e) {
        	e.printStackTrace();
            throw new Exception(e.getMessage());
        } catch (BadPaddingException e) {
        	e.printStackTrace();
            throw new Exception(e.getMessage());
        }
        return encodeContent;
    }

    /**
     * RSA 加密返回 Hex
     * 
     * @param content
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static String encodeStringPrivate(byte[] content, PrivateKey privateKey) throws Exception {
        byte[] encodeContent = encodeBytePrivate(content, privateKey);
        String stringContent = Hex.encodeHexString(encodeContent);
        return stringContent;
    }

    /**
     * 解密返回byte[]
     * 
     * @param content
     * @param publicKey
     * @return
     * @throws Exception
     */
    public static byte[] decodeBytePublic(byte[] content, PublicKey publicKey) throws Exception {
        byte[] decodeContent = null;
        try {
            Cipher cipher = Cipher.getInstance(RSA);
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            decodeContent = cipher.doFinal(content);
        } catch (NoSuchAlgorithmException e) {
        	e.printStackTrace();
            throw new Exception(e.getMessage());
        } catch (NoSuchPaddingException e) {
        	e.printStackTrace();
            throw new Exception(e.getMessage());
        } catch (InvalidKeyException e) {
        	e.printStackTrace();
            throw new Exception(e.getMessage());
        } catch (IllegalBlockSizeException e) {
        	e.printStackTrace();
            throw new Exception(e.getMessage());
        } catch (BadPaddingException e) {
        	e.printStackTrace();
            throw new Exception(e.getMessage());
        }
        return decodeContent;
    }

    /**
     * 解密 Hex字符串
     * 
     * @param content
     * @param publicKey
     * @return
     * @throws Exception
     */
    public static byte[] decodeStringPublic(String content, PublicKey publicKey) throws Exception {
        byte[] decodeContent = null;
        try {
            byte[] sourceByteArray = Hex.decodeHex(content.toCharArray());
            decodeContent = decodeBytePublic(sourceByteArray, publicKey);
        } catch (IOException e) {
        	e.printStackTrace();
            throw new Exception(e.getMessage());
        }
        return decodeContent;
    }

    /**
     * 将Key转为String
     * 
     * @param key
     * @return
     */
    private static String keyToString(Key key) {
        byte[] keyByteArray = key.getEncoded();
        BASE64Encoder base64 = new BASE64Encoder();
        String keyString = base64.encode(keyByteArray);
        return keyString;
    }
    
    public static class RSADTO {
        /**
         * 公钥
         */
        private String publicKey;

        /**
         * 私钥
         */
        private String privateKey;

		public String getPublicKey() {
			return publicKey;
		}

		public void setPublicKey(String publicKey) {
			this.publicKey = publicKey;
		}
		public String getPrivateKey() {
			return privateKey;
		}

		public void setPrivateKey(String privateKey) {
			this.privateKey = privateKey;
		}
    }
    
    public static class MD5Util {
        /**
         * MD5加密
         *
         * @param s
         * @return
         */
        public static String MD5(String s) {
            char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
            try {
                byte[] btInput = s.getBytes("utf8");
                // 获得MD5摘要算法的 MessageDigest 对象
                MessageDigest mdInst = MessageDigest.getInstance("MD5");
                // 使用指定的字节更新摘要
                mdInst.update(btInput);
                // 获得密文
                byte[] md = mdInst.digest();
                // 把密文转换成十六进制的字符串形式
                int j = md.length;
                char str[] = new char[j * 2];
                int k = 0;
                for (int i = 0; i < j; i++) {
                    byte byte0 = md[i];
                    str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                    str[k++] = hexDigits[byte0 & 0xf];
                }
                return new String(str);
            } catch (Exception e) {
            	e.printStackTrace();
                return null;
            }
        }
    }
    
//    public static String getSignatureTaiyong(Map<String, String> params){
//    	// 先将参数以其参数名的字典序升序进行排序
//		Map<String, String> sortedParams = new TreeMap<String, String>(params);
//		Set<Entry<String, String>> entrys = sortedParams.entrySet();
//
//		// 遍历排序后的字典，将所有参数按"key=value"格式拼接在一起
//		StringBuilder basestring = new StringBuilder();
//		for (Entry<String, String> param : entrys) {
//			if (param.getValue() != null && (!param.getValue().trim().isEmpty())
//					&& (!param.getValue().trim().equalsIgnoreCase(null))) {
//				basestring.append(param.getKey().trim()).append("=").append(param.getValue().trim()).append("&");
//			}
//		}
//		basestring.deleteCharAt(basestring.length() - 1);
//		String paramStr = basestring.toString();
//    	return RSAUtilTaiyong.encodeStringPrivate(MD5Util.MD5(paramStr).getBytes(), RSAUtilTaiyong.getPrivateKey(privateKey));
//    }
    
    public static String getSignatureTaiyong(Map<String, String> params){
    	// 先将参数以其参数名的字典序升序进行排序
		Map<String, String> sortedParams = new TreeMap<String, String>(params);
		Set<Entry<String, String>> entrys = sortedParams.entrySet();

		// 遍历排序后的字典，将所有参数按"key=value"格式拼接在一起
		StringBuilder basestring = new StringBuilder();
		for (Entry<String, String> param : entrys) {
			if (param.getValue() != null && (!param.getValue().trim().isEmpty())
					&& (!param.getValue().trim().equalsIgnoreCase(null))) {
				basestring.append(param.getKey().trim()).append("=").append(param.getValue().trim()).append("&");
			}
		}
		basestring.deleteCharAt(basestring.length() - 1);
		String paramStr = basestring.toString();
    	try {
			return RSAUtilTaiyong.encodeStringPrivate(MD5Util.MD5(paramStr).getBytes(), RSAUtilTaiyong.getPrivateKey(privateKey));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
    }

    
    public static void main(String[] args) throws Exception {
    	RSADTO dto = generateRSA();
		System.out.println("私钥 :" + dto.getPrivateKey());
		System.out.println("公钥 :" + dto.getPublicKey());
		
		
		String paramStr = "a=1&b=2";
		String privateKey = dto.getPrivateKey();
		String sign = RSAUtilTaiyong.encodeStringPrivate(MD5Util.MD5(paramStr).getBytes(), RSAUtilTaiyong.getPrivateKey(privateKey));
		System.out.println(sign);
	}

}
