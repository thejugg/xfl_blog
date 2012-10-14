package me.xfl.jugg.user;

import me.xfl.jugg.Juggs;
import freemarker.log.Logger;

/**
 * User service factory.
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.3, Dec 29, 2011
 */
@SuppressWarnings("unchecked")
public final class UserServiceFactory {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(UserServiceFactory.class.getName());
	/**
	 * User service.
	 */
	private static final UserService USER_SERVICE;

	static {
		LOGGER.info("Constructing User Service....");

		try {
			Class<UserService> serviceClass = null;

			switch (Juggs.getRuntime("userService")) {
			case GAE:
				serviceClass = (Class<UserService>) Class.forName("me.xfl.jugg.user.gae.GAEUserService");
				USER_SERVICE = serviceClass.newInstance();
				break;
			case LOCAL:
				serviceClass = (Class<UserService>) Class.forName("me.xfl.jugg.user.local.LocalUserService");
				USER_SERVICE = serviceClass.newInstance();
				break;
			default:
				throw new RuntimeException("Jugg runs in the hell.... Please set the enviornment correctly");
			}
		} catch (final Exception e) {
			throw new RuntimeException("Can not initialize User Service!", e);
		}

		LOGGER.info("Constructed User Service");
	}

	/**
	 * Gets user service (always be an instance of
	 * {@link org.b3log.latke.user.local.LocalUserService}).
	 * 
	 * @return user service
	 */
	public static UserService getUserService() {
		return USER_SERVICE;
	}

	/**
	 * Private default constructor.
	 */
	private UserServiceFactory() {
	}
}
