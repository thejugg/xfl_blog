package me.xfl.jugg.cache;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.xfl.jugg.Juggs;
import me.xfl.jugg.RuntimeEnv;

/**
 * Cache factory.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.9, Aug 27, 2012
 */
public final class CacheFactory {
	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(CacheFactory.class.getName());
	/**
	 * Caches.
	 */
	private static final Map<String, Cache<String, ?>> CACHES = Collections.synchronizedMap(new HashMap<String, Cache<String, ?>>());

	/**
	 * Removes all caches.
	 */
	public static synchronized void removeAll() {
		RuntimeEnv runtime = Juggs.getRuntime("cache");

		// Always LOCAL cache if runs on a standard servlet container
		if (RuntimeEnv.LOCAL == Juggs.getRuntimeEnv()) {
			runtime = RuntimeEnv.LOCAL;
		}

		switch (runtime) {
		case GAE:
			final Iterator<Cache<String, ?>> iterator = CACHES.values().iterator();
			if (iterator.hasNext()) {
				iterator.next().removeAll();
				// Clears one will clears all on GAE
				break;
			}

			break;
		case LOCAL:
			// Clears cache one by one
			for (final Map.Entry<String, Cache<String, ?>> entry : CACHES.entrySet()) {
				final Cache<String, ?> cache = entry.getValue();
				cache.removeAll();
				LOGGER.log(Level.FINEST, "Clears cache[name={0}]", entry.getKey());
			}

			break;
		default:
			throw new RuntimeException("Latke runs in the hell.... Please set the enviornment correctly");
		}
	}

	/**
	 * Gets a cache specified by the given cache name.
	 * 
	 * @param cacheName
	 *            the given cache name
	 * @return a cache specified by the given cache name
	 */
	@SuppressWarnings("unchecked")
	public static synchronized Cache<String, ? extends Serializable> getCache(final String cacheName) {
		LOGGER.log(Level.INFO, "Constructing Cache[name={0}]....", cacheName);

		Cache<String, ?> ret = CACHES.get(cacheName);

		try {
			if (null == ret) {
				switch (Juggs.getRuntime("cache")) {
				case LOCAL:
					final Class<Cache<String, ?>> localLruCache = (Class<Cache<String, ?>>) Class
							.forName("me.xfl.jugg.cache.local.LruMemoryCache");
					ret = localLruCache.newInstance();
					break;
				case GAE:
					final Class<Cache<String, ?>> gaeMemcache = (Class<Cache<String, ?>>) Class.forName("me.xfl.jugg.cache.gae.Memcache");
					final Constructor<Cache<String, ?>> constructor = gaeMemcache.getConstructor(String.class);
					ret = constructor.newInstance(cacheName);
					break;
				default:
					throw new RuntimeException("Latke runs in the hell.... Please set the enviornment correctly");
				}
				CACHES.put(cacheName, ret);
			}
		} catch (final Exception e) {
			throw new RuntimeException("Can not get cache: " + e.getMessage(), e);
		}

		return (Cache<String, Serializable>) ret;
	}

	/**
	 * Private default constructor.
	 */
	private CacheFactory() {
	}
}
