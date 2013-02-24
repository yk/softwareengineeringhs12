package com.xbockr.dr.repository;

public class RepositoryInputValidator {
	/**
	 * @param arg
	 *            Checks the argument of the option -d. Mustn't contain ISO
	 *            control characters and mustn't be longer than 1000 characters.
	 *            Throws RuntimeExcetpion in case of invalid argument.
	 */
	public static void checkArgDescription(String arg) {
		// no need to check if null or empty. getOptarg() throws exception
		// 'cause arg is required
		if (arg.length() > 1000) {
			throw new RuntimeException(
					"Description of data set mustn't be longer than 1000 characters!");
		}
		// check for illegal characters
		String aString = arg;
		for (char c : aString.toCharArray()) {
			if (Character.isISOControl(c)) {
				throw new RuntimeException(
						"Description of data set must not contain ISO control characters");
			}
		}
	}
	
	/**
	 * @param arg
	 *            Checks the argument of the option -n. Mustn't contain lower
	 *            case letters and mustn't be longer than 40 characters. Throws
	 *            RuntimeExcetpion in case of invalid argument.
	 */
	public static void checkArgName(String arg) {
		// check length
		if (arg.length() > 40) {
			throw new RuntimeException(
					"Unique name of data set must not be longer than 40 characters!");
		}
		// check for lowercase letters
		String aString = arg;
		for (char c : aString.toCharArray()) {
			if (Character.isLowerCase(c)) {
				throw new RuntimeException(
						"Name must not contain lowercase letters");
			}
		}
		if (arg.equals(".xbockr")) {
			throw new RuntimeException("NO!");
		}
	}
}
