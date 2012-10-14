package me.xfl.dragon.processor.renderer;

import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import me.xfl.dragon.model.Common;
import me.xfl.dragon.processor.util.TopBars;
import me.xfl.dragon.util.Statistics;
import me.xfl.jugg.Keys;
import me.xfl.jugg.cache.PageCaches;
import me.xfl.jugg.servlet.HTTPRequestContext;
import me.xfl.jugg.servlet.renderer.freemarker.CacheFreeMarkerRenderer;

/**
 * <a href="http://freemarker.org">FreeMarker</a> HTTP response renderer.
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.7, Jul 16, 2012
 * @since 0.3.1
 */
public final class FrontRenderer extends CacheFreeMarkerRenderer {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(FrontRenderer.class.getName());
	/**
	 * Statistic utilities.
	 */
	private Statistics statistics = Statistics.getInstance();

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * Puts the top bar replacement flag into data model.
	 * </p>
	 */
	@Override
	protected void beforeRender(final HTTPRequestContext context) throws Exception {
		LOGGER.log(Level.FINEST, "Before render....");
		getDataModel().put(Common.TOP_BAR_REPLACEMENT_FLAG_KEY, Common.TOP_BAR_REPLACEMENT_FLAG);
	}

	@Override
	protected void doRender(final String html, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		LOGGER.log(Level.FINEST, "Do render....");
		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");

		PrintWriter writer;
		try {
			writer = response.getWriter();
		} catch (final Exception e) {
			writer = new PrintWriter(response.getOutputStream());
		}

		if (response.isCommitted()) { // response has been sent redirect
			writer.flush();
			writer.close();

			return;
		}

		final String pageContent = (String) request.getAttribute(PageCaches.CACHED_CONTENT);
		String output = html;
		if (null != pageContent) {
			// Adds the top bar HTML content for output
			// 下面和PageCacheFilter里有重复，不过第一访问的时候缓存为null，所以此处要替换TapBar内容。
			final String topBarHTML = TopBars.getTopBarHTML(request, response);
			output = html.replace(Common.TOP_BAR_REPLACEMENT_FLAG, topBarHTML);
		}

		writer.write(output);
		writer.flush();
		writer.close();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * Blog statistic view count +1.
	 * </p>
	 */
	@Override
	protected void afterRender(final HTTPRequestContext context) throws Exception {
		LOGGER.log(Level.FINEST, "After render....");

		try {
			statistics.incBlogViewCount(context.getRequest(), context.getResponse());
		} catch (final Exception e) {
			LOGGER.log(Level.WARNING, "Incs blog view count failed", e);
		}

		final HttpServletRequest request = context.getRequest();
		if ("mobile".equals((String) request.getAttribute(Keys.TEMAPLTE_DIR_NAME))) {
			// Skips page caching if requested by mobile device
			return;
		}

		super.afterRender(context);
	}
}
