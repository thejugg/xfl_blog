package me.xfl.jugg.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import me.xfl.jugg.Juggs;
import me.xfl.jugg.Keys;
import me.xfl.jugg.cache.PageCaches;
import me.xfl.jugg.servlet.renderer.AbstractHTTPResponseRenderer;
import me.xfl.jugg.servlet.renderer.HTTP404Renderer;
import me.xfl.jugg.util.StaticResources;
import me.xfl.jugg.util.Stopwatchs;
import me.xfl.jugg.util.Strings;

/**
 * Front controller for HTTP request dispatching.
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.2.0, Aug 10, 2012
 */
public final class HTTPRequestDispatcher extends HttpServlet {

	/**
	 * Default serial version uid.
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(HTTPRequestDispatcher.class.getName());
	/**
	 * Default Servlet name used by Tomcat, Jetty, JBoss, and GlassFish.
	 */
	private static final String COMMON_DEFAULT_SERVLET_NAME = "default";
	/**
	 * Default Servlet name used by Google App Engine.
	 */
	private static final String GAE_DEFAULT_SERVLET_NAME = "_ah_default";
	/**
	 * Current default servlet name.
	 */
	private String defaultServletName;

	/**
	 * Initializes this servlet.
	 * 
	 * <p>
	 * Scans classpath for discovering request processors, configured the
	 * 'default' servlet for static resource processing.
	 * </p>
	 * 
	 * @throws ServletException
	 *             servlet exception
	 * @see RequestProcessors#discover()
	 */
	@Override
	public void init() throws ServletException {
		Stopwatchs.start("Discovering Request Processors");
		try {
			LOGGER.info("Discovering request processors....");
			RequestProcessors.discover();
			LOGGER.info("Discovered request processors");
		} catch (final Exception e) {
			LOGGER.log(Level.SEVERE, "Initializes request processors failed", e);
		} finally {
			Stopwatchs.end();
		}

		final ServletContext servletContext = getServletContext();
		if (servletContext.getNamedDispatcher(COMMON_DEFAULT_SERVLET_NAME) != null) {
			defaultServletName = COMMON_DEFAULT_SERVLET_NAME;
		} else if (servletContext.getNamedDispatcher(GAE_DEFAULT_SERVLET_NAME) != null) {
			defaultServletName = GAE_DEFAULT_SERVLET_NAME;
		} else {
			throw new IllegalStateException("Unable to locate the default servlet for serving static content. "
					+ "Please set the 'defaultServletName' property explicitly.");
			// TODO: Loads from local.properties
		}

		LOGGER.log(Level.CONFIG, "The default servlet for serving static resource is [{0}]", defaultServletName);
	}

	/**
	 * Serves.
	 * 
	 * @param request
	 *            the specified HTTP servlet request
	 * @param response
	 *            the specified HTTP servlet response
	 * @throws ServletException
	 *             servlet exception
	 * @throws IOException
	 *             io exception
	 */
	@Override
	protected void service(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		final String resourcePath = request.getPathTranslated();
		final String requestURI = request.getRequestURI();

		LOGGER.log(Level.FINEST, "Request[contextPath={0}, pathTranslated={1}, requestURI={2}]", new Object[] { request.getContextPath(),
				resourcePath, requestURI });

		// 如果是图片、css、js文件之列的静态资源，则直接dispatch，然后return，下面的代码不再执行。
		// 第一次访问页面（非静态资源，如首页、文章、标签）那么就一直把service方法执行完毕，然后在
		// 把页面加入到缓存，之后的访问就直接从缓存中读取，writer.write。（详细见PageCacheFilter）
		if (StaticResources.isStatic(request)) {
			final RequestDispatcher requestDispatcher = getServletContext().getNamedDispatcher(defaultServletName);
			if (null == requestDispatcher) {
				throw new IllegalStateException("A RequestDispatcher could not be located for the default servlet ["
						+ this.defaultServletName + "]");
			}

			// 过滤器处理时，不进行后续过滤器链处理（FilterChain#doFilter()），而是直接转发请求给
			// Servlet（RequestDispatcher#forward()）。
			// 详见：http://88250.b3log.org/how-to-skip-filters-in-java
			requestDispatcher.forward(request, response);
			return;
		}

		final long startTimeMillis = System.currentTimeMillis();
		request.setAttribute(Keys.HttpRequest.START_TIME_MILLIS, startTimeMillis);

		// 在PageCacheFilter中有重复设置
		if (Juggs.isPageCacheEnabled()) {
			final String queryString = request.getQueryString();
			String pageCacheKey = (String) request.getAttribute(Keys.PAGE_CACHE_KEY);
			if (Strings.isEmptyOrNull(pageCacheKey)) {
				pageCacheKey = PageCaches.getPageCacheKey(requestURI, queryString);
				request.setAttribute(Keys.PAGE_CACHE_KEY, pageCacheKey);
			}
		}

		// 2012-8-29 0:18 Encoding configuration to filter/EncodingFilter
		// 下面两行代码用不到了
		// request.setCharacterEncoding("UTF-8");
		// response.setCharacterEncoding("UTF-8");

		final HTTPRequestContext context = new HTTPRequestContext();
		context.setRequest(request);
		context.setResponse(response);

		dispatch(context);

	}

	/**
	 * Dispatches with the specified context.
	 * 
	 * @param context
	 *            the specified specified context
	 * @throws ServletException
	 *             servlet exception
	 * @throws IOException
	 *             io exception
	 */
	public static void dispatch(final HTTPRequestContext context) throws ServletException, IOException {
		final HttpServletRequest request = context.getRequest();

		String requestURI = (String) request.getAttribute(Keys.HttpRequest.REQUEST_URI);
		if (Strings.isEmptyOrNull(requestURI)) {
			requestURI = request.getRequestURI();
		}

		String method = (String) request.getAttribute(Keys.HttpRequest.REQUEST_METHOD);
		if (Strings.isEmptyOrNull(method)) {
			method = request.getMethod();
		}

		LOGGER.log(Level.FINER, "Request[requestURI={0}, method={1}]", new Object[] { requestURI, method });

		try {
			final Object processorMethodRet = RequestProcessors.invoke(requestURI, Juggs.getContextPath(), method, context);
		} catch (final Exception e) {
			final String exceptionTypeName = e.getClass().getName();
			LOGGER.log(Level.FINER,
					"Occured error while processing request[requestURI={0}, method={1}, exceptionTypeName={2}, errorMsg={3}]",
					new Object[] { requestURI, method, exceptionTypeName, e.getMessage() });
			if ("com.google.apphosting.api.ApiProxy$OverQuotaException".equals(exceptionTypeName)) {
				PageCaches.removeAll();

				context.getResponse().sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
				return;
			}

			throw new ServletException(e);
		} catch (final Error e) {
			final Runtime runtime = Runtime.getRuntime();
			LOGGER.log(Level.FINER, "Memory status[total={0}, max={1}, free={2}]",
					new Object[] { runtime.totalMemory(), runtime.maxMemory(), runtime.freeMemory() });

			LOGGER.log(Level.SEVERE, e.getMessage(), e);

			throw e;
		}

		// XXX: processor method ret?
		final HttpServletResponse response = context.getResponse();
		if (response.isCommitted()) { // Sends redirect or send error
			final PrintWriter writer = response.getWriter();
			writer.flush();
			writer.close();

			return;
		}

		AbstractHTTPResponseRenderer renderer = context.getRenderer();

		if (null == renderer) {
			renderer = new HTTP404Renderer();
		}

		renderer.render(context);

	}

}
