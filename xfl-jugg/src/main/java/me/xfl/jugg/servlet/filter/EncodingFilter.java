package me.xfl.jugg.servlet.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * General encoding filter.
 * 
 * <p>
 * Configured in web.xml filter. Init param requestEncoding & responseEncoding.
 * </p>
 * 
 * @author ArmstrongCN
 * @version 1.0.0.0, Aug 10, 2012
 */
public final class EncodingFilter implements Filter {

	/**
	 * Request encoding.
	 */
	private String requestEncoding = "UTF-8";
	/**
	 * Response encoding.
	 */
	private String responseEncoding = "UTF-8";

	@Override
	public void init(final FilterConfig filterConfig) throws ServletException {
		requestEncoding = filterConfig.getInitParameter("requestEncoding");
		responseEncoding = filterConfig.getInitParameter("responseEncoding");
	}

	/**
	 * Sets the request and response encoding to a given charset.
	 * 
	 * @param request
	 *            the specified request
	 * @param response
	 *            the specified response
	 * @param chain
	 *            filter chain
	 * @throws IOException
	 *             io exception
	 * @throws ServletException
	 *             servlet exception
	 */
	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException,
			ServletException {
		request.setCharacterEncoding(requestEncoding);
		response.setCharacterEncoding(responseEncoding);

		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
	}
}
