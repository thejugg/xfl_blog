package me.xfl.jugg.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import me.xfl.jugg.servlet.renderer.AbstractHTTPResponseRenderer;

/**
 * HTTP request context.
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Jul 16, 2011
 */
public final class HTTPRequestContext {

	/**
	 * Request.
	 */
	private HttpServletRequest request;
	/**
	 * Response.
	 */
	private HttpServletResponse response;
	/**
	 * Renderer.
	 */
	private AbstractHTTPResponseRenderer renderer;

	/**
	 * Gets the renderer.
	 * 
	 * @return renderer
	 */
	public AbstractHTTPResponseRenderer getRenderer() {
		return renderer;
	}

	/**
	 * Sets the renderer with the specified renderer.
	 * 
	 * @param renderer
	 *            the specified renderer
	 */
	public void setRenderer(final AbstractHTTPResponseRenderer renderer) {
		this.renderer = renderer;
	}

	/**
	 * Gets the request.
	 * 
	 * @return request
	 */
	public HttpServletRequest getRequest() {
		return request;
	}

	/**
	 * Sets the request with the specified request.
	 * 
	 * @param request
	 *            the specified request
	 */
	public void setRequest(final HttpServletRequest request) {
		this.request = request;
	}

	/**
	 * Gets the response.
	 * 
	 * @return response
	 */
	public HttpServletResponse getResponse() {
		return response;
	}

	/**
	 * Sets the response with the specified response.
	 * 
	 * @param response
	 *            the specified response
	 */
	public void setResponse(final HttpServletResponse response) {
		this.response = response;
	}
}
