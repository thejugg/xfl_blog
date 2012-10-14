package me.xfl.dragon.util;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.xfl.dragon.repository.ArticleRepository;
import me.xfl.dragon.repository.PageRepository;
import me.xfl.dragon.repository.impl.ArticleRepositoryImpl;
import me.xfl.dragon.repository.impl.PageRepositoryImpl;
import me.xfl.jugg.repository.RepositoryException;
import me.xfl.jugg.util.Strings;

/**
 * Permalink utilities.
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.1.0.8, Aug 22, 2012
 * @since 0.3.1
 */
public final class Permalinks {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(Permalinks.class.getName());
	/**
	 * Article repository.
	 */
	private ArticleRepository articleRepository = ArticleRepositoryImpl.getInstance();
	/**
	 * Page repository.
	 */
	private PageRepository pageRepository = PageRepositoryImpl.getInstance();
	/**
	 * Reserved permalinks.
	 */
	public static final String[] RESERVED_LINKS = new String[] { "/", "/article", "/tags.html", "/tags", "/page", "/blog-articles-feed.do",
			"/tag-articles-feed.do", "/blog-articles-rss.do", "/tag-articles-rss.do", "/get-random-articles.do",
			"/article-random-double-gen.do", "/captcha.do", "/kill-browser.html", "/add-article-comment.do",
			"/add-article-from-symphony-comment.do", "/add-page-comment.do", "/get-article-content", "/sitemap.xml", "/login", "/logout",
			"/get-article-content", "/admin-index.do", "/admin-article.do", "/admin-article-list.do", "/admin-link-list.do",
			"/admin-preference.do", "/admin-file-list.do", "/admin-page-list.do", "/admin-others.do", "/admin-draft-list.do",
			"/admin-user-list.do", "/admin-plugin-list.do", "/admin-main.do", "/admin-about.do", "/admin-label", "/admin-about.do",
			"/rm-all-data.do", "/init", "/clear-cache.do", };

	/**
	 * Checks whether the specified article permalink matches the system
	 * generated format pattern ("/articles/yyyy/MM/dd/${articleId}.html").
	 * 
	 * @param permalink
	 *            the specified permalink
	 * @return {@code true} if matches, returns {@code false} otherwise
	 */
	public static boolean matchDefaultArticlePermalinkFormat(final String permalink) {
		final Pattern pattern = Pattern.compile("/articles/\\d{4}/\\d{2}/\\d{2}/\\d+\\.html");
		final Matcher matcher = pattern.matcher(permalink);

		return matcher.matches();
	}

	/**
	 * Checks whether the specified page permalink matches the system generated
	 * format pattern ("/pages/${pageId}.html").
	 * 
	 * @param permalink
	 *            the specified permalink
	 * @return {@code true} if matches, returns {@code false} otherwise
	 */
	public static boolean matchDefaultPagePermalinkFormat(final String permalink) {
		final Pattern pattern = Pattern.compile("/pages/\\d+\\.html");
		final Matcher matcher = pattern.matcher(permalink);

		return matcher.matches();
	}

	/**
	 * Checks whether the specified permalink is a
	 * {@link #invalidArticlePermalinkFormat(java.lang.String) invalid article
	 * permalink format} and
	 * {@link #invalidPagePermalinkFormat(java.lang.String) invalid page
	 * permalink format}.
	 * 
	 * @param permalink
	 *            the specified permalink
	 * @return {@code true} if invalid, returns {@code false} otherwise
	 */
	public static boolean invalidPermalinkFormat(final String permalink) {
		return invalidArticlePermalinkFormat(permalink) && invalidPagePermalinkFormat(permalink);
	}

	/**
	 * Checks whether the specified article permalink is invalid on format.
	 * 
	 * @param permalink
	 *            the specified article permalink
	 * @return {@code true} if invalid, returns {@code false} otherwise
	 */
	public static boolean invalidArticlePermalinkFormat(final String permalink) {
		if (Strings.isEmptyOrNull(permalink)) {
			return true;
		}

		if (matchDefaultArticlePermalinkFormat(permalink)) {
			return false;
		}

		return invalidUserDefinedPermalinkFormat(permalink);
	}

	/**
	 * Checks whether the specified page permalink is invalid on format.
	 * 
	 * @param permalink
	 *            the specified page permalink
	 * @return {@code true} if invalid, returns {@code false} otherwise
	 */
	public static boolean invalidPagePermalinkFormat(final String permalink) {
		if (Strings.isEmptyOrNull(permalink)) {
			return true;
		}

		if (matchDefaultPagePermalinkFormat(permalink)) {
			return false;
		}

		return invalidUserDefinedPermalinkFormat(permalink);
	}

	/**
	 * Checks whether the specified user-defined permalink is invalid on format.
	 * 
	 * @param permalink
	 *            the specified user-defined permalink
	 * @return {@code true} if invalid, returns {@code false} otherwise
	 */
	private static boolean invalidUserDefinedPermalinkFormat(final String permalink) {
		if (Strings.isEmptyOrNull(permalink)) {
			return true;
		}

		if (isReservedLink(permalink)) {
			return true;
		}

		if (Strings.isNumeric(permalink.substring(1))) {
			// See issue 120
			// (http://code.google.com/p/b3log-solo/issues/detail?id=120#c4)
			// for more details
			return true;
		}

		int slashCnt = 0;
		for (int i = 0; i < permalink.length(); i++) {
			if ('/' == permalink.charAt(i)) {
				slashCnt++;
			}

			if (slashCnt > 1) {
				return true;
			}
		}

		// FIXME: URL format check

		return false;
	}

	/**
	 * Determines whether the specified request URI is a reserved link.
	 * 
	 * <p>
	 * A URI starts with one of {@link Permalinks#RESERVED_LINKS reserved links}
	 * will be treated as reserved link.
	 * </p>
	 * 
	 * @param requestURI
	 *            the specified request URI
	 * @return {@code true} if it is a reserved link, returns {@code false}
	 *         otherwise
	 */
	private static boolean isReservedLink(final String requestURI) {
		for (int i = 0; i < Permalinks.RESERVED_LINKS.length; i++) {
			final String reservedLink = Permalinks.RESERVED_LINKS[i];
			if (reservedLink.startsWith(requestURI)) {

				return true;
			}
		}

		return false;
	}

	/**
	 * Determines whether the specified permalink exists.
	 * 
	 * @param permalink
	 *            the specified permalink
	 * @return {@code true} if exists, returns {@code false} otherwise
	 */
	public boolean exist(final String permalink) {
		try {
			return isReservedLink(permalink) || null != articleRepository.getByPermalink(permalink)
					|| null != pageRepository.getByPermalink(permalink) || permalink.endsWith(".ftl");
		} catch (final RepositoryException e) {
			LOGGER.log(Level.SEVERE, "Determines whether the permalink[" + permalink + "] exists failed, returns true", e);

			return true;
		}
	}

	/**
	 * Gets the {@link Permalinks} singleton.
	 * 
	 * @return the singleton
	 */
	public static Permalinks getInstance() {
		return SingletonHolder.SINGLETON;
	}

	/**
	 * Private default constructor.
	 */
	private Permalinks() {
	}

	/**
	 * Singleton holder.
	 * 
	 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
	 * @version 1.0.0.0, Jan 12, 2011
	 */
	private static final class SingletonHolder {

		/**
		 * Singleton.
		 */
		private static final Permalinks SINGLETON = new Permalinks();

		/**
		 * Private default constructor.
		 */
		private SingletonHolder() {
		}
	}
}
