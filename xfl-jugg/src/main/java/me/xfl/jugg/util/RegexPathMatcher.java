package me.xfl.jugg.util;

import java.util.regex.Pattern;

/**
 * Regular expression path matcher.
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Oct 31, 2011
 * @see AntPathMatcher
 */
public final class RegexPathMatcher {

	/**
	 * Determines whether the specified path matches the specified regular
	 * expression pattern.
	 * 
	 * @param pattern
	 *            the specified regular expression pattern
	 * @param path
	 *            the specified path
	 * @return {@code true} if matches, returns {@code false} otherwise
	 */
	public static boolean match(final String pattern, final String path) {
		final Pattern p = Pattern.compile(pattern);
		
		return p.matcher(path).matches();
	}

	/**
	 * Private constructor.
	 */
	private RegexPathMatcher() {

	}
}
