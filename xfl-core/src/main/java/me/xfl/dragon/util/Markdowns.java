package me.xfl.dragon.util;

import java.io.StringReader;
import java.io.StringWriter;

import me.xfl.jugg.util.Strings;

import org.tautua.markdownpapers.Markdown;

/**
 * <a href="http://en.wikipedia.org/wiki/Markdown">Markdown</a> utilities.
 * 
 * <p>
 * Uses the <a href="http://markdown.tautua.org/">MarkdownPapers</a> as the
 * converter.
 * </p>
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Apr 28, 2012
 * @since 0.4.5
 */
public final class Markdowns {

	/**
	 * Converts the specified markdown text to HTML.
	 * 
	 * @param markdownText
	 *            the specified markdown text
	 * @return converted HTML, returns {@code null} if the specified markdown
	 *         text is "" or {@code null}
	 * @throws Exception
	 *             exception
	 */
	public static String toHTML(final String markdownText) throws Exception {
		if (Strings.isEmptyOrNull(markdownText)) {
			return null;
		}

		final StringWriter writer = new StringWriter();
		final Markdown markdown = new Markdown();

		markdown.transform(new StringReader(markdownText), writer);

		return writer.toString();
	}

	/**
	 * Private constructor.
	 */
	private Markdowns() {
	}
}
