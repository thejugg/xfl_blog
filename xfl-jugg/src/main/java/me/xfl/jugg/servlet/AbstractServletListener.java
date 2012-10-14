package me.xfl.jugg.servlet;

import java.io.File;
import java.net.URL;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import me.xfl.jugg.Juggs;
import me.xfl.jugg.repository.jdbc.JdbcRepository;

/**
 * Abstract servlet listener.
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.3.0, Apr 5, 2012
 */
public abstract class AbstractServletListener implements ServletContextListener, ServletRequestListener, HttpSessionListener {
	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(AbstractServletListener.class.getName());

	/**
	 * Web root.
	 */
	private static String webRoot;

	static {
		final URL resource = ClassLoader.class.getResource("/");
		if (null != resource) {
			webRoot = resource.getPath();
		}
	}

	/**
	 * Initializes context, {@linkplain #webRoot web root}, locale and runtime
	 * environment.
	 * 
	 * @param servletContextEvent
	 *            servlet context event
	 */
	@Override
	public void contextInitialized(final ServletContextEvent servletContextEvent) {
		Juggs.initRuntimeEnv();
		LOGGER.info("Initializing the context....");

		Juggs.setLocale(Locale.SIMPLIFIED_CHINESE);
		LOGGER.log(Level.INFO, "Default locale[{0}]", Juggs.getLocale());

		final ServletContext servletContext = servletContextEvent.getServletContext();
		webRoot = servletContext.getRealPath("") + File.separator;
		LOGGER.log(Level.INFO, "Server[webRoot={0}, contextPath={1}]", new Object[] { webRoot,
				servletContextEvent.getServletContext().getContextPath() });

		// CronService.start();
	}

	/**
	 * Destroys the context, unregisters remote JavaScript services.
	 * 
	 * @param servletContextEvent
	 *            servlet context event
	 */
	@Override
	public void contextDestroyed(final ServletContextEvent servletContextEvent) {
		LOGGER.info("Destroying the context.....");
		Juggs.shutdown();
	}

	@Override
	public void requestDestroyed(final ServletRequestEvent servletRequestEvent) {
		if (Juggs.runsWithJDBCDatabase()) {
			JdbcRepository.dispose();
		}
	}

	@Override
	public abstract void requestInitialized(final ServletRequestEvent servletRequestEvent);

	@Override
	public abstract void sessionCreated(final HttpSessionEvent httpSessionEvent);

	@Override
	public abstract void sessionDestroyed(final HttpSessionEvent httpSessionEvent);

	/**
	 * Gets the absolute file path of web root directory on the server's file
	 * system.
	 * 
	 * @return the directory file path(tailing with {@link File#separator}).
	 */
	public static String getWebRoot() {
		return webRoot;
	}

}
