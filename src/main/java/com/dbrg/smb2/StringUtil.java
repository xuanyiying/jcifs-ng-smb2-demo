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

}
