package net.wintersjames.gameserver;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author james
 */
public class StringUtils {
	
	public static String sha256sum(String... s) {
		
		try {
			
			String message = "";
			for(String string: s) {
				message += string;
			}

			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(message.getBytes(StandardCharsets.UTF_8));

			StringBuilder retval = new StringBuilder(2*hash.length);
			for(byte b: hash) {
				String digit = Integer.toHexString(0xFF & b);
				if(digit.length() == 1) {
						retval.append("0");
				}
				retval.append(digit);
			}
			
			return retval.toString();

		} catch (NoSuchAlgorithmException e) {
			return "";
		}
	}
}
