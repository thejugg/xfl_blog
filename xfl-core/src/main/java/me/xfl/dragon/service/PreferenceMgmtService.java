package me.xfl.dragon.service;

import static me.xfl.dragon.model.Preference.ADMIN_EMAIL;
import static me.xfl.dragon.model.Preference.BLOG_HOST;
import static me.xfl.dragon.model.Preference.PAGE_CACHE_ENABLED;
import static me.xfl.dragon.model.Preference.TIME_ZONE_ID;
import static me.xfl.dragon.model.Preference.VERSION;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.xfl.dragon.DragonServletListener;
import me.xfl.dragon.model.Preference;
import me.xfl.dragon.model.Skin;
import me.xfl.dragon.repository.PreferenceRepository;
import me.xfl.dragon.repository.impl.PreferenceRepositoryImpl;
import me.xfl.dragon.util.Skins;
import me.xfl.dragon.util.TimeZones;
import me.xfl.jugg.Juggs;
import me.xfl.jugg.repository.RepositoryException;
import me.xfl.jugg.repository.Transaction;
import me.xfl.jugg.service.LangPropsService;
import me.xfl.jugg.service.ServiceException;
import me.xfl.jugg.util.Locales;
import me.xfl.jugg.util.Strings;
import me.xfl.jugg.util.freemarker.Templates;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Preference management service.
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.5, May 16, 2012
 * @since 0.4.0
 */
public final class PreferenceMgmtService {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(PreferenceMgmtService.class.getName());
	/**
	 * Preference repository.
	 */
	private PreferenceRepository preferenceRepository = PreferenceRepositoryImpl.getInstance();
	/**
	 * Preference query service.
	 */
	private PreferenceQueryService preferenceQueryService = PreferenceQueryService.getInstance();
	/**
	 * Language service.
	 */
	private LangPropsService langPropsService = LangPropsService.getInstance();

	/**
	 * Updates the reply notification template with the specified reply
	 * notification template.
	 * 
	 * @param replyNotificationTemplate
	 *            the specified reply notification template
	 * @throws ServiceException
	 *             service exception
	 */
	public void updateReplyNotificationTemplate(final JSONObject replyNotificationTemplate) throws ServiceException {
		final Transaction transaction = preferenceRepository.beginTransaction();

		try {
			preferenceRepository.update(Preference.REPLY_NOTIFICATION_TEMPLATE, replyNotificationTemplate);
			transaction.commit();
		} catch (final Exception e) {
			if (transaction.isActive()) {
				transaction.rollback();
			}

			LOGGER.log(Level.SEVERE, "Updates reply notification failed", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * Updates the preference with the specified preference.
	 * 
	 * @param preference
	 *            the specified preference
	 * @throws ServiceException
	 *             service exception
	 */
	public void updatePreference(final JSONObject preference) throws ServiceException {
		@SuppressWarnings("unchecked")
		final Iterator<String> keys = preference.keys();
		while (keys.hasNext()) {
			final String key = keys.next();
			if (preference.isNull(key)) {
				throw new ServiceException("A value is null of preference[key=" + key + "]");
			}
		}

		// TODO: checks preference

		final Transaction transaction = preferenceRepository.beginTransaction();
		try {
			String blogHost = preference.getString(BLOG_HOST).toLowerCase().trim();
			if (StringUtils.startsWithIgnoreCase(blogHost, "http://")) {
				blogHost = blogHost.substring("http://".length());
			}
			if (blogHost.endsWith("/")) {
				blogHost = blogHost.substring(0, blogHost.length() - 1);
			}

			LOGGER.log(Level.FINER, "Blog Host[{0}]", blogHost);
			preference.put(BLOG_HOST, blogHost);

			final String skinDirName = preference.getString(Skin.SKIN_DIR_NAME);
			final String skinName = Skins.getSkinName(skinDirName);
			preference.put(Skin.SKIN_NAME, skinName);
			final Set<String> skinDirNames = Skins.getSkinDirNames();
			final JSONArray skinArray = new JSONArray();
			for (final String dirName : skinDirNames) {
				final JSONObject skin = new JSONObject();
				skinArray.put(skin);

				final String name = Skins.getSkinName(dirName);
				skin.put(Skin.SKIN_NAME, name);
				skin.put(Skin.SKIN_DIR_NAME, dirName);
			}
			final String webRootPath = DragonServletListener.getWebRoot();
			final String skinPath = webRootPath + Skin.SKINS + "/" + skinDirName;
			LOGGER.log(Level.FINER, "Skin path[{0}]", skinPath);
			Templates.CACHE.clear();

			preference.put(Skin.SKINS, skinArray.toString());

			final String timeZoneId = preference.getString(TIME_ZONE_ID);
			TimeZones.setTimeZone(timeZoneId);

			preference.put(Preference.SIGNS, preference.get(Preference.SIGNS).toString());

			final JSONObject oldPreference = preferenceQueryService.getPreference();
			final String adminEmail = oldPreference.getString(ADMIN_EMAIL);
			preference.put(ADMIN_EMAIL, adminEmail);

			if (!preference.has(PAGE_CACHE_ENABLED)) {
				preference.put(PAGE_CACHE_ENABLED, oldPreference.getBoolean(PAGE_CACHE_ENABLED));
			}

			final boolean pageCacheEnabled = preference.getBoolean(Preference.PAGE_CACHE_ENABLED);
			Templates.enableCache(pageCacheEnabled);

			final String version = oldPreference.optString(VERSION);
			if (!Strings.isEmptyOrNull(version)) {
				preference.put(VERSION, version);
			}

			final String localeString = preference.getString(Preference.LOCALE_STRING);
			LOGGER.log(Level.FINER, "Current locale[string={0}]", localeString);
			Juggs.setLocale(new Locale(Locales.getLanguage(localeString), Locales.getCountry(localeString)));

			preferenceRepository.update(Preference.PREFERENCE, preference);

			transaction.commit();

			Templates.MAIN_CFG.setDirectoryForTemplateLoading(new File(skinPath));

			if (preference.getBoolean(PAGE_CACHE_ENABLED)) {
				Juggs.enablePageCache();
			} else {
				Juggs.disablePageCache();
			}
		} catch (final JSONException e) {
			if (transaction.isActive()) {
				transaction.rollback();
			}
			LOGGER.log(Level.SEVERE, "Updates preference failed", e);
			throw new ServiceException(langPropsService.get("updateFailLabel"));
		} catch (final RepositoryException e) {
			if (transaction.isActive()) {
				transaction.rollback();
			}
			LOGGER.log(Level.SEVERE, "Updates preference failed", e);
			throw new ServiceException(langPropsService.get("updateFailLabel"));
		} catch (final IOException e) {
			if (transaction.isActive()) {
				transaction.rollback();
			}
			LOGGER.log(Level.SEVERE, "Updates preference failed", e);
			throw new ServiceException(langPropsService.get("updateFailLabel"));
		}

		LOGGER.log(Level.FINER, "Updates preference successfully");
	}

	/**
	 * Gets the {@link PreferenceMgmtService} singleton.
	 * 
	 * @return the singleton
	 */
	public static PreferenceMgmtService getInstance() {
		return SingletonHolder.SINGLETON;
	}

	/**
	 * Private constructor.
	 */
	private PreferenceMgmtService() {
	}

	/**
	 * Singleton holder.
	 * 
	 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
	 * @version 1.0.0.0, Oct 24, 2011
	 */
	private static final class SingletonHolder {

		/**
		 * Singleton.
		 */
		private static final PreferenceMgmtService SINGLETON = new PreferenceMgmtService();

		/**
		 * Private default constructor.
		 */
		private SingletonHolder() {
		}
	}
}
