package me.xfl.jugg.servlet.renderer;

import me.xfl.jugg.servlet.HTTPRequestContext;

/**
 * Abstract HTTP response renderer.
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Jul 16, 2011
 */
public abstract class AbstractHTTPResponseRenderer {

	/**
	 * Renders with the specified HTTP request context.
	 * 
	 * @param context
	 *            the specified HTTP request context
	 */
	public abstract void render(final HTTPRequestContext context);
}
