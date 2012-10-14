package me.xfl.dragon;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletRequestEvent;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;

import me.xfl.dragon.model.Preference;
import me.xfl.dragon.model.Skin;
import me.xfl.dragon.repository.PreferenceRepository;
import me.xfl.dragon.repository.impl.PreferenceRepositoryImpl;
import me.xfl.dragon.util.Skins;
import me.xfl.dragon.util.Statistics;
import me.xfl.jugg.Keys;
import me.xfl.jugg.repository.Transaction;
import me.xfl.jugg.servlet.AbstractServletListener;
import me.xfl.jugg.util.Requests;
import me.xfl.jugg.util.Stopwatchs;
import me.xfl.jugg.util.Strings;
import me.xfl.jugg.util.freemarker.Templates;

import org.json.JSONObject;

/**
 * B3log Solo servlet listener.
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.7.5, Aug 16, 2011
 * @since 0.3.1
 */
public final class DragonServletListener extends AbstractServletListener {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(DragonServletListener.class.getName());
	/**
     * Enter escape.
     */
    public static final String ENTER_ESC = "_esc_enter_88250_";

	@Override
	public void contextInitialized(final ServletContextEvent servletContextEvent) {
		Stopwatchs.start("Context Initialized");

		super.contextInitialized(servletContextEvent);

		// Default to skin "ease", loads from preference later
		Skins.setDirectoryForTemplateLoading("ease");

		final PreferenceRepository preferenceRepository = PreferenceRepositoryImpl.getInstance();

		final Transaction transaction = preferenceRepository.beginTransaction();

		try {
			loadPreference();

			if (transaction.isActive()) {
				transaction.commit();
			}
		} catch (final Exception e) {
			if (transaction.isActive()) {
				transaction.rollback();
			}
		}

		// 暂时没有 PluginManager.getInstance().load();

		// 暂时没有 registerEventProcessor();

		LOGGER.info("Initialized the context");

		Stopwatchs.end();
		LOGGER.log(Level.FINE, "Stopwatch: {0}{1}", new Object[] { Strings.LINE_SEPARATOR, Stopwatchs.getTimingStat() });
	}

	@Override
	public void contextDestroyed(final ServletContextEvent servletContextEvent) {
		super.contextDestroyed(servletContextEvent);

		LOGGER.info("Destroyed the context");
	}

	@Override
	public void requestInitialized(final ServletRequestEvent servletRequestEvent) {
		final HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequestEvent.getServletRequest();
		final String requestURI = httpServletRequest.getRequestURI();
		Stopwatchs.start("Request Initialized[requestURI=" + requestURI + "]");

		if (Requests.searchEngineBotRequest(httpServletRequest)) {
			LOGGER.log(Level.FINER, "Request made from a search engine[User-Agent={0}]", httpServletRequest.getHeader("User-Agent"));
			httpServletRequest.setAttribute(Keys.HttpRequest.IS_SEARCH_ENGINE_BOT, true);
		} else {
			// Gets the session of this request
			final HttpSession session = httpServletRequest.getSession();
			LOGGER.log(Level.FINE, "Gets a session[id={0}, remoteAddr={1}, User-Agent={2}, isNew={3}]", new Object[] { session.getId(),
					httpServletRequest.getRemoteAddr(), httpServletRequest.getHeader("User-Agent"), session.isNew() });
			// Online visitor count
			Statistics.onlineVisitorCount(httpServletRequest);
		}

		resolveSkinDir(httpServletRequest);
	}

	@Override
	public void requestDestroyed(final ServletRequestEvent servletRequestEvent) {
		Stopwatchs.end();

		LOGGER.log(Level.FINE, "Stopwatch: {0}{1}", new Object[] { Strings.LINE_SEPARATOR, Stopwatchs.getTimingStat() });
		Stopwatchs.release();

		super.requestDestroyed(servletRequestEvent);
	}

	@Override
	public void sessionCreated(final HttpSessionEvent httpSessionEvent) {

	}

	// Note: This method will never invoked on GAE production environment
	@Override
	public void sessionDestroyed(final HttpSessionEvent httpSessionEvent) {

	}

	/**
	 * Loads preference.
	 * 
	 * <p>
	 * Loads preference from repository, loads skins from skin directory then
	 * sets it into preference if the skins changed. Puts preference into cache
	 * and persists it to repository finally.
	 * </p>
	 * 
	 * <p>
	 * <b>Note</b>: Do NOT use method
	 * {@linkplain me.xfl.dragon.util.Preferences#getPreference()} to load it,
	 * caused by the method may retrieve it from cache.
	 * </p>
	 */
	private void loadPreference() {
		Stopwatchs.start("Load Preference");

		LOGGER.info("Loading preference....");

		final PreferenceRepository preferenceRepository = PreferenceRepositoryImpl.getInstance();
		JSONObject preference;

		try {
			preference = preferenceRepository.get(Preference.PREFERENCE);
			if (null == preference) {
				LOGGER.log(Level.WARNING, "Can't not init default skin, please init B3log Solo first");
				return;
			}

			Skins.loadSkins(preference);

			final boolean pageCacheEnabled = preference.getBoolean(Preference.PAGE_CACHE_ENABLED);
			Templates.enableCache(pageCacheEnabled);
		} catch (final Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);

			throw new IllegalStateException(e);
		}

		Stopwatchs.end();
	}

	/**
	 * Resolve skin (template) for the specified HTTP servlet request.
	 * 
	 * @param httpServletRequest
	 *            the specified HTTP servlet request
	 */
	private void resolveSkinDir(final HttpServletRequest httpServletRequest) {
		try {
			final PreferenceRepository preferenceRepository = PreferenceRepositoryImpl.getInstance();
			final JSONObject preference = preferenceRepository.get(Preference.PREFERENCE);
			if (null == preference) { // Did not initialize yet
				return;
			}

			final String requestURI = httpServletRequest.getRequestURI();

			String desiredView = Requests.mobileSwitchToggle(httpServletRequest);

			if (desiredView == null && !Requests.mobileRequest(httpServletRequest) || desiredView != null && desiredView.equals("normal")) {
				desiredView = preference.getString(Skin.SKIN_DIR_NAME);
			} else {
				desiredView = "mobile";
				LOGGER.log(Level.FINER, "The request [URI={0}] comes frome mobile device", requestURI);
			}

			httpServletRequest.setAttribute(Keys.TEMAPLTE_DIR_NAME, desiredView);
		} catch (final Exception e) {
			LOGGER.log(Level.SEVERE, "Resolves skin failed", e);
		}
	}

}
