package me.xfl.jugg.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * MD5 hash.
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.4, Dec 3, 2010
 */
public final class MD5 {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(MD5.class.getName());
	/**
	 * Message digest.
	 */
	private static MessageDigest messageDigest;
	/**
	 * Low 8 bits all sets to 1.
	 */
	private static final int LOW_8_BITS_1 = 0xff;
	/**
	 * Append size.
	 */
	private static final int APPEND_SIZE = 16;

	/**
	 * Private default constructor.
	 */
	private MD5() {
	}

	static {
		try {
			messageDigest = MessageDigest.getInstance("MD5");
		} catch (final NoSuchAlgorithmException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Hashs(MD5) the specified string.
	 * 
	 * @param string
	 *            the specified string
	 * @return hashed string from the specified string
	 */
	public static String hash(final String string) {
		final char[] charArray = string.toCharArray();
		final byte[] byteArray = new byte[charArray.length];

		for (int i = 0; i < charArray.length; i++) {
			byteArray[i] = (byte) charArray[i];
		}

		final byte[] bytes = messageDigest.digest(byteArray);
		final StringBuffer hexValue = new StringBuffer();

		for (int i = 0; i < bytes.length; i++) {
			final int val = ((int) bytes[i]) & LOW_8_BITS_1;
			if (val < APPEND_SIZE) {
				hexValue.append("0");
			}

			hexValue.append(Integer.toHexString(val));
		}

		return hexValue.toString();
	}
}
