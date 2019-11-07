package com.dbrg.smb2;


/**
 * @author wangying
 */
public final class StringUtil {
	private StringUtil() {
		throw new IllegalStateException("Utility class");
	}

	public static boolean isNotEmpty(String value) {
		return !isEmpty(value);
	}

	public static boolean isEmpty(String value) {
		return value == null || value.length() == 0;
	}

	public static String transBlank(String value) {
		StringBuffer tempStr = new StringBuffer();
		for (int i = 0; i < value.length(); i++) {
			if (' '== value.charAt(i)) {
				tempStr.append("\040");
			} else {
				tempStr.append(value.charAt(i));
			}
		}
		return tempStr.toString();
	}
}
