package me.xfl.dragon.repository;

import me.xfl.jugg.repository.Repository;
import me.xfl.jugg.repository.RepositoryException;

import org.json.JSONObject;

/**
 * User repository.
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.4, Nov 8, 2011
 */
public interface UserRepository extends Repository {

	/**
	 * Gets a user by the specified email.
	 * 
	 * @param email
	 *            the specified email
	 * @return user, returns {@code null} if not found
	 * @throws RepositoryException
	 *             repository exception
	 */
	JSONObject getByEmail(final String email) throws RepositoryException;

	/**
	 * Determine whether the specified email is administrator's.
	 * 
	 * @param email
	 *            the specified email
	 * @return {@code true} if it is administrator's email, {@code false}
	 *         otherwise
	 * @throws RepositoryException
	 *             repository exception
	 */
	boolean isAdminEmail(final String email) throws RepositoryException;

	/**
	 * Gets the administrator user.
	 * 
	 * @return administrator user, returns {@code null} if not found or error
	 * @throws RepositoryException
	 *             repository exception
	 */
	JSONObject getAdmin() throws RepositoryException;
}
