package me.xfl.jugg.servlet.renderer;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

import me.xfl.jugg.servlet.HTTPRequestContext;

/**
 * HTML HTTP response renderer.
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Sep 11, 2011
 */
public final class TextHTMLRenderer extends AbstractHTTPResponseRenderer {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(TextHTMLRenderer.class.getName());
	/**
	 * Content to render.
	 */
	private String content;

	/**
	 * Sets the content with the specified content.
	 * 
	 * @param content
	 *            the specified content
	 */
	public void setContent(final String content) {
		this.content = content;
	}

	@Override
	public void render(final HTTPRequestContext context) {
		try {
			final HttpServletResponse response = context.getResponse();
			response.setContentType("text/html");
			response.setCharacterEncoding("UTF-8");

			final PrintWriter writer = response.getWriter();
			writer.write(content);
			writer.close();
		} catch (final IOException e) {
			LOGGER.log(Level.SEVERE, "Render failed", e);
		}
	}
}
