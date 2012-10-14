package me.xfl.jugg.servlet.renderer;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

import me.xfl.jugg.servlet.HTTPRequestContext;

/**
 * HTTP {@link HttpServletResponse#SC_NOT_FOUND 404 status} renderer.
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Oct 31, 2011
 */
public final class HTTP404Renderer extends AbstractHTTPResponseRenderer {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(HTTP404Renderer.class.getName());

	@Override
	public void render(final HTTPRequestContext context) {
		final HttpServletResponse response = context.getResponse();

		try {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);

			return;
		} catch (final IOException e) {
			LOGGER.log(Level.SEVERE, "Renders 404 error", e);
		}
	}
}
