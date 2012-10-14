package me.xfl.jugg.servlet.renderer.freemarker;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import me.xfl.jugg.Juggs;
import me.xfl.jugg.Keys;
import me.xfl.jugg.cache.PageCaches;
import me.xfl.jugg.servlet.HTTPRequestContext;
import me.xfl.jugg.util.Strings;

import org.json.JSONObject;

/**
 * <a href="http://freemarker.org">FreeMarker</a> HTTP response renderer.
 * 
 * <p>
 * This renderer will put page content into cache.
 * <p>
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.4, May 5, 2012
 */
public class CacheFreeMarkerRenderer extends AbstractFreeMarkerRenderer {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(CacheFreeMarkerRenderer.class.getName());

	@Override
	protected void beforeRender(final HTTPRequestContext context) throws Exception {
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * Processes page caching.
	 * </p>
	 */
	@Override
	protected void afterRender(final HTTPRequestContext context) throws Exception {
		final HttpServletRequest request = context.getRequest();
		final String pageContent = (String) request.getAttribute(PageCaches.CACHED_CONTENT);

		if (null == pageContent) {
			return;
		}

		if (Juggs.isPageCacheEnabled()) {

			// 详见Keys.PAGE_CACHE_KEY在PageCacheFilter类中有request.setAttribute设置
			final String cachedPageKey = (String) request.getAttribute(Keys.PAGE_CACHE_KEY);
			if (Strings.isEmptyOrNull(cachedPageKey)) {
				return;
			}

			LOGGER.log(Level.FINEST, "Caching page[cachedPageKey={0}]", cachedPageKey);

			// AbstractCacheableAction类中的check方法
			check(request, pageContent);

			final JSONObject cachedValue = new JSONObject();
			// 下面的request.getAttribute，在每个Processor中有设置
			cachedValue.put(PageCaches.CACHED_CONTENT, pageContent);
			cachedValue.put(PageCaches.CACHED_TYPE, request.getAttribute(PageCaches.CACHED_TYPE));
			cachedValue.put(PageCaches.CACHED_OID, request.getAttribute(PageCaches.CACHED_OID));
			cachedValue.put(PageCaches.CACHED_TITLE, request.getAttribute(PageCaches.CACHED_TITLE));
			cachedValue.put(PageCaches.CACHED_LINK, request.getAttribute(PageCaches.CACHED_LINK));
			if (null != request.getAttribute(PageCaches.CACHED_PWD)) {
				cachedValue.put(PageCaches.CACHED_PWD, request.getAttribute(PageCaches.CACHED_PWD));
			}

			PageCaches.put(cachedPageKey, cachedValue, request);
			LOGGER.log(Level.FINEST, "Cached page[cachedPageKey={0}]", cachedPageKey);
		}
	}

	/**
	 * Checks if all conditions for caching page are ready by the specified
	 * request and content.
	 * 
	 * @param request
	 *            the specified request
	 * @param content
	 *            the specified content
	 */
	public static void check(final HttpServletRequest request, final String content) {
		if (Strings.isEmptyOrNull(content) || Strings.isEmptyOrNull((String) request.getAttribute(PageCaches.CACHED_TYPE))
				|| Strings.isEmptyOrNull((String) request.getAttribute(PageCaches.CACHED_OID))
				|| Strings.isEmptyOrNull((String) request.getAttribute(PageCaches.CACHED_TITLE))
				|| Strings.isEmptyOrNull((String) request.getAttribute(PageCaches.CACHED_LINK))) {
			throw new IllegalArgumentException("Illegal arguments for caching page, " + "resolve this bug first!");
		}
	}
}
