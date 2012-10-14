package me.xfl.dragon.processor.util;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import me.xfl.dragon.model.Common;
import me.xfl.dragon.processor.LoginProcessor;
import me.xfl.dragon.processor.renderer.ConsoleRenderer;
import me.xfl.dragon.util.Statistics;
import me.xfl.dragon.util.Users;
import me.xfl.jugg.Keys;
import me.xfl.jugg.model.Role;
import me.xfl.jugg.model.User;
import me.xfl.jugg.service.LangPropsService;
import me.xfl.jugg.service.ServiceException;
import me.xfl.jugg.user.UserService;
import me.xfl.jugg.user.UserServiceFactory;
import me.xfl.jugg.util.Requests;
import me.xfl.jugg.util.Stopwatchs;

import org.json.JSONException;
import org.json.JSONObject;

import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * Top bar utilities.
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @author <a href="mailto:dongxv.vang@gmail.com">Dongxu Wang</a>
 * @version 1.0.0.4, May 4, 2012
 * @since 0.3.5
 */
public final class TopBars {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(TopBars.class.getName());
	/**
	 * User utilities.
	 */
	private static Users userUtils = Users.getInstance();
	/**
	 * User service.
	 */
	private static UserService userService = UserServiceFactory.getUserService();
	/**
	 * Language service.
	 */
	private static LangPropsService langPropsService = LangPropsService.getInstance();

	/**
	 * Generates top bar HTML.
	 * 
	 * @param request
	 *            the specified request
	 * @param response
	 *            the specified response
	 * @return top bar HTML
	 * @throws ServiceException
	 *             service exception
	 */
	public static String getTopBarHTML(final HttpServletRequest request, final HttpServletResponse response) throws ServiceException {
		Stopwatchs.start("Gens Top Bar HTML");

		try {
			final Template topBarTemplate = ConsoleRenderer.TEMPLATE_CFG.getTemplate("top-bar.ftl");
			final StringWriter stringWriter = new StringWriter();

			final Map<String, Object> topBarModel = new HashMap<String, Object>();

			LoginProcessor.tryLogInWithCookie(request, response);
			final JSONObject currentUser = userUtils.getCurrentUser(request);

			Keys.fillServer(topBarModel);
			topBarModel.put(Common.IS_LOGGED_IN, false);

			topBarModel.put(Common.IS_MOBILE_REQUEST, Requests.mobileRequest(request));
			topBarModel.put("mobileLabel", langPropsService.get("mobileLabel"));

			topBarModel.put("onlineVisitor1Label", langPropsService.get("onlineVisitor1Label"));
			topBarModel.put(Common.ONLINE_VISITOR_CNT, Statistics.getOnlineVisitorCount());

			if (null == currentUser) {
				topBarModel.put(Common.LOGIN_URL, userService.createLoginURL(Common.ADMIN_INDEX_URI));
				topBarModel.put("loginLabel", langPropsService.get("loginLabel"));

				topBarTemplate.process(topBarModel, stringWriter);

				return stringWriter.toString();
			}

			topBarModel.put(Common.IS_LOGGED_IN, true);
			topBarModel.put(Common.LOGOUT_URL, userService.createLogoutURL("/"));
			topBarModel.put(Common.IS_ADMIN, Role.ADMIN_ROLE.equals(currentUser.getString(User.USER_ROLE)));

			topBarModel.put("clearAllCacheLabel", langPropsService.get("clearAllCacheLabel"));
			topBarModel.put("clearCacheLabel", langPropsService.get("clearCacheLabel"));
			topBarModel.put("adminLabel", langPropsService.get("adminLabel"));
			topBarModel.put("logoutLabel", langPropsService.get("logoutLabel"));

			final String userName = currentUser.getString(User.USER_NAME);
			topBarModel.put(User.USER_NAME, userName);

			topBarTemplate.process(topBarModel, stringWriter);

			return stringWriter.toString();
		} catch (final JSONException e) {
			LOGGER.log(Level.SEVERE, "Gens top bar HTML failed", e);
			throw new ServiceException(e);
		} catch (final IOException e) {
			LOGGER.log(Level.SEVERE, "Gens top bar HTML failed", e);
			throw new ServiceException(e);
		} catch (final TemplateException e) {
			LOGGER.log(Level.SEVERE, "Gens top bar HTML failed", e);
			throw new ServiceException(e);
		} finally {
			Stopwatchs.end();
		}
	}

	/**
	 * Private default constructor.
	 */
	private TopBars() {
	}
}
