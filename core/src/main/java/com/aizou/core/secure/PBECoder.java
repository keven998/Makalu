package com.aizou.core.secure;


import android.util.Log;

import com.aizou.core.log.LogUtil;

import java.security.Key;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

/**
 * PBE安全编码组件
 * 
 */
public abstract class PBECoder extends Coder {

	private static final String TAG = "PBECoder";

	/**
	 * 支持以下任意一种算法
	 * 
	 * <pre>
	 * PBEWithMD5AndDES  
	 * PBEWithMD5AndTripleDES  
	 * PBEWithSHA1AndDESede 
	 * PBEWithSHA1AndRC2_40
	 * </pre>
	 */
	public static final String ALGORITHM = "PBEWITHMD5andDES";

	/**
	 * 盐初始化
	 * 
	 * @return
	 * @throws Exception
	 */
	public static byte[] initSalt() throws Exception {
		byte[] salt = new byte[8];
		Random random = new Random();
		random.nextBytes(salt);
		return salt;
	}

	/**
	 * 转换密钥<br>
	 * 
	 * @param password
	 * @return
	 * @throws Exception
	 */
	private static Key toKey(String password) throws Exception {
		PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray());
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
		SecretKey secretKey = keyFactory.generateSecret(keySpec);

		return secretKey;
	}

	
	/**
	 * 加密
	 * 
	 * @param data
	 *            数据
	 * @param password
	 *            密码
	 * @param salt
	 *            盐
	 * @return
	 * @throws Exception
	 */
	public static String encrypt(String data, String password, String salt)  throws Exception {
		
		byte[] byteData = encrypt(data.getBytes("UTF-8"), password, salt.getBytes("UTF-8"));
				
		return encryptBASE64(byteData);
    }
	
	
	/**
	 * 加密
	 * 
	 * @param data
	 *            数据
	 * @param password
	 *            密码
	 * @param salt
	 *            盐
	 * @return
	 * @throws Exception
	 */
	public static byte[] encrypt(byte[] data, String password, byte[] salt)
			throws Exception {

		Key key = toKey(password);

		PBEParameterSpec paramSpec = new PBEParameterSpec(salt, 100);
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);

		return cipher.doFinal(data);

	}
	
	/**
	 * 解密
	 * 
	 * @param data
	 *            数据
	 * @param password
	 *            密码
	 * @param salt
	 *            盐
	 * @return
	 * @throws Exception
	 */
	public static String decrypt(String data, String password, String salt)
			throws Exception {
		byte[] encryptBytesData = decryptBASE64(data);
		byte[] byteSalt = salt.getBytes("UTF-8");
		byte[] decByteData = decrypt(encryptBytesData, password, byteSalt);
		String outputStr = new String(decByteData, "UTF-8");
		return outputStr;
	}

	/**
	 * 解密
	 * 
	 * @param data
	 *            数据
	 * @param password
	 *            密码
	 * @param salt
	 *            盐
	 * @return
	 * @throws Exception
	 */
	public static byte[] decrypt(byte[] data, String password, byte[] salt)
			throws Exception {

		Key key = toKey(password);

		PBEParameterSpec paramSpec = new PBEParameterSpec(salt, 100);
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, key, paramSpec);

		return cipher.doFinal(data);

	}

	public static void main(String[] args) {

		String inputStr = "PBE132123123";
		String key;
		try {
			key = DESCoder.DEFAULT_KEY;
			LogUtil.i(TAG, "原文:\t" + inputStr);

            LogUtil.i(TAG, "密钥:\t" + key);

			String inputData = PBECoder.encrypt(inputStr,"123","salt");

			LogUtil.i(TAG, "加密后:\t" + inputData);

			String outputData = PBECoder.decrypt(inputData,"123","salt");

            LogUtil.i(TAG, "解密后:\t" + outputData);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}