package me.xfl.jugg.service;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.xfl.jugg.Juggs;
import me.xfl.jugg.Keys;
import me.xfl.jugg.model.Label;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Language service.
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.1.4, Sep 30, 2011
 */
public final class LangPropsService {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(LangPropsService.class.getName());
	/**
	 * Language properties.
	 */
	private static final Map<Locale, Map<String, String>> LANGS = new HashMap<Locale, Map<String, String>>();

	/**
	 * Gets all language properties as a map by the specified locale.
	 * 
	 * @param locale
	 *            the specified locale
	 * @return a map of language configurations
	 */
	public Map<String, String> getAll(final Locale locale) {
		Map<String, String> ret = LANGS.get(locale);

		if (null == ret) {
			ret = new HashMap<String, String>();
			ResourceBundle langBundle = null;
			try {
				langBundle = ResourceBundle.getBundle(Keys.LANGUAGE, locale);
			} catch (final MissingResourceException e) {
				LOGGER.log(Level.WARNING, "{0}, using default locale[{1}] instead", new Object[] { e.getMessage(), Juggs.getLocale() });

				try {
					langBundle = ResourceBundle.getBundle(Keys.LANGUAGE, Juggs.getLocale());
				} catch (final MissingResourceException ex) {
					LOGGER.log(Level.WARNING, "{0}, using default lang.properties instead", new Object[] { e.getMessage() });
					langBundle = ResourceBundle.getBundle(Keys.LANGUAGE);
				}
			}

			final Enumeration<String> keys = langBundle.getKeys();
			while (keys.hasMoreElements()) {
				final String key = keys.nextElement();
				final String value = langBundle.getString(key);
				ret.put(key, value);
			}

			LANGS.put(locale, ret);
		}

		return ret;
	}

	/**
	 * Gets all language properties as labels from lang_(by the specified
	 * locale).properties file. If not found lang_(locale).properties
	 * configurations, using {@link Juggs#getLocale()} instead.
	 * 
	 * @param locale
	 *            the specified locale
	 * @return for example,
	 * 
	 *         <pre>
	 * {
	 *     "msgs": {
	 *         "localeNotFound":
	 *             "Unsupported locale, using default locale(zh_CN) instead."
	 *     },
	 *     "labels": [
	 *         {"labelId": "projectName", "labelText": "B3log Web"},
	 *         ....
	 *     ]
	 * }
	 * </pre>
	 */
	public JSONObject getLabels(final Locale locale) {
		final JSONObject ret = new JSONObject();
		ResourceBundle langBundle = null;

		try {
			langBundle = ResourceBundle.getBundle(Keys.LANGUAGE, locale);
		} catch (final MissingResourceException e) {
			LOGGER.log(Level.WARNING, "{0}, using default locale[{1}]  instead", new Object[] { e.getMessage(), Juggs.getLocale() });

			langBundle = ResourceBundle.getBundle(Keys.LANGUAGE, Juggs.getLocale());
		}

		final Enumeration<String> keys = langBundle.getKeys();
		final JSONArray labels = new JSONArray();

		ret.put(Label.LABELS, labels);

		while (keys.hasMoreElements()) {
			final JSONObject label = new JSONObject();
			final String key = keys.nextElement();
			label.put(Label.LABEL_ID, key);
			label.put(Label.LABEL_TEXT, langBundle.getString(key));

			labels.put(label);
		}

		return ret;
	}

	/**
	 * Gets a value from {@link Juggs#getLocale() the current locale} specified
	 * language properties file with the specified key.
	 * 
	 * @param key
	 *            the specified key
	 * @return value
	 */
	public String get(final String key) {
		return get(Keys.LANGUAGE, key, Juggs.getLocale());
	}

	/**
	 * Gets a value from baseName_locale.properties file with the specified key.
	 * If not found baseName_(locale).properties configurations, using
	 * {@link Juggs#getLocale()} instead.
	 * 
	 * @param baseName
	 *            base name of resource bundle, options as the following:
	 *            <ul>
	 *            <li>{@link Keys#LANGUAGE}</li>
	 *            </ul>
	 * @param key
	 *            the specified key
	 * @param locale
	 *            the specified locale
	 * @return the value of the specified key
	 */
	private String get(final String baseName, final String key, final Locale locale) {
		if (!Keys.LANGUAGE.equals(baseName)) {
			final RuntimeException e = new RuntimeException("i18n resource[baseName=" + baseName + "] not found");
			LOGGER.log(Level.SEVERE, e.getMessage(), e);

			throw e;
		}

		try {
			return ResourceBundle.getBundle(baseName, locale).getString(key);
		} catch (final MissingResourceException e) {
			LOGGER.log(Level.WARNING, "{0}, get it from default locale[{1}]", new Object[] { e.getMessage(), Juggs.getLocale() });

			return ResourceBundle.getBundle(baseName, Juggs.getLocale()).getString(key);
		}
	}

	/**
	 * Gets the {@link LangPropsService} singleton.
	 * 
	 * @return the singleton
	 */
	public static LangPropsService getInstance() {
		return SingletonHolder.SINGLETON;
	}

	/**
	 * Private default constructor.
	 */
	private LangPropsService() {
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
		private static final LangPropsService SINGLETON = new LangPropsService();

		/**
		 * Private default constructor.
		 */
		private SingletonHolder() {
		}
	}
}
