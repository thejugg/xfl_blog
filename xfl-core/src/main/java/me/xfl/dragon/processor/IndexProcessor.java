package me.xfl.dragon.processor;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import me.xfl.dragon.model.Article;
import me.xfl.dragon.model.Common;
import me.xfl.dragon.model.PageTypes;
import me.xfl.dragon.model.Preference;
import me.xfl.dragon.processor.renderer.FrontRenderer;
import me.xfl.dragon.processor.util.Filler;
import me.xfl.dragon.service.PreferenceQueryService;
import me.xfl.dragon.util.Skins;
import me.xfl.jugg.Juggs;
import me.xfl.jugg.Keys;
import me.xfl.jugg.annotation.RequestProcessing;
import me.xfl.jugg.annotation.RequestProcessor;
import me.xfl.jugg.cache.PageCaches;
import me.xfl.jugg.model.Pagination;
import me.xfl.jugg.service.LangPropsService;
import me.xfl.jugg.service.ServiceException;
import me.xfl.jugg.servlet.HTTPRequestContext;
import me.xfl.jugg.servlet.HTTPRequestMethod;
import me.xfl.jugg.servlet.URIPatternMode;
import me.xfl.jugg.servlet.renderer.freemarker.AbstractFreeMarkerRenderer;
import me.xfl.jugg.util.Requests;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

/**
 * Index processor.
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.1.0.8, May 17, 2012
 * @since 0.3.1
 */
@RequestProcessor
public final class IndexProcessor {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(IndexProcessor.class.getName());
	/**
	 * Filler.
	 */
	private Filler filler = Filler.getInstance();
	/**
	 * Preference query service.
	 */
	private PreferenceQueryService preferenceQueryService = PreferenceQueryService.getInstance();
	/**
	 * Language service.
	 */
	private LangPropsService langPropsService = LangPropsService.getInstance();

	/**
	 * Shows index with the specified context.
	 * 
	 * @param context
	 *            the specified context
	 */
	@RequestProcessing(value = { "/\\d*", "" }, uriPatternsMode = URIPatternMode.REGEX, method = HTTPRequestMethod.GET)
	public void showIndex(final HTTPRequestContext context) {
		final AbstractFreeMarkerRenderer renderer = new FrontRenderer();
		context.setRenderer(renderer);

		renderer.setTemplateName("index.ftl");
		final Map<String, Object> dataModel = renderer.getDataModel();

		final HttpServletRequest request = context.getRequest();
		final HttpServletResponse response = context.getResponse();
		final String requestURI = request.getRequestURI();
		try {
			final int currentPageNum = getCurrentPageNum(requestURI);
			final JSONObject preference = preferenceQueryService.getPreference();

			Skins.fillSkinLangs(preference.optString(Preference.LOCALE_STRING), (String) request.getAttribute(Keys.TEMAPLTE_DIR_NAME),
					dataModel);

			final Map<String, String> langs = langPropsService.getAll(Juggs.getLocale());
			// 下面4个request.setAttribute为了是在afterRender中加入PageCaches缓存
			request.setAttribute(PageCaches.CACHED_TYPE, langs.get(PageTypes.INDEX_ARTICLES));
			request.setAttribute(PageCaches.CACHED_OID, "No id");
			request.setAttribute(PageCaches.CACHED_TITLE, langs.get(PageTypes.INDEX_ARTICLES) + "  [" + langs.get("pageNumLabel") + "="
					+ currentPageNum + "]");
			request.setAttribute(PageCaches.CACHED_LINK, requestURI);

			filler.fillIndexArticles(dataModel, currentPageNum, preference);

			@SuppressWarnings("unchecked")
			final List<JSONObject> articles = (List<JSONObject>) dataModel.get(Article.ARTICLES);
			if (articles.isEmpty()) {
				try {
					response.sendError(HttpServletResponse.SC_NOT_FOUND);

					return;
				} catch (final IOException ex) {
					LOGGER.severe(ex.getMessage());
				}
			}

			// 以后加上 filler.fillSide(request, dataModel, preference);
			filler.fillBlogHeader(request, dataModel, preference);
			filler.fillBlogFooter(dataModel, preference);

			dataModel.put(Pagination.PAGINATION_CURRENT_PAGE_NUM, currentPageNum);
			final String previousPageNum = Integer.toString(currentPageNum > 1 ? currentPageNum - 1 : 0);
			dataModel.put(Pagination.PAGINATION_PREVIOUS_PAGE_NUM, "0".equals(previousPageNum) ? "" : previousPageNum);
			final Integer pageCount = (Integer) dataModel.get(Pagination.PAGINATION_PAGE_COUNT);
			if (pageCount == currentPageNum + 1) { // The next page is the last
													// page
				dataModel.put(Pagination.PAGINATION_NEXT_PAGE_NUM, "");
			} else {
				dataModel.put(Pagination.PAGINATION_NEXT_PAGE_NUM, currentPageNum + 1);
			}

			dataModel.put(Common.PATH, "");
		} catch (final ServiceException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);

			try {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
			} catch (final IOException ex) {
				LOGGER.severe(ex.getMessage());
			}
		}
	}

	/**
	 * Gets the request page number from the specified request URI.
	 * 
	 * @param requestURI
	 *            the specified request URI
	 * @return page number, returns {@code -1} if the specified request URI can
	 *         not convert to an number
	 */
	private static int getCurrentPageNum(final String requestURI) {
		final String pageNumString = StringUtils.substringAfter(requestURI, "/");

		return Requests.getCurrentPageNum(pageNumString);
	}

}
