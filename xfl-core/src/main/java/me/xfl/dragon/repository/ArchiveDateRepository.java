package me.xfl.dragon.repository;

import java.util.List;

import me.xfl.jugg.repository.Repository;
import me.xfl.jugg.repository.RepositoryException;

import org.json.JSONObject;

/**
 * Archive date repository.
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.2, Jul 2, 2011
 */
public interface ArchiveDateRepository extends Repository {

	/**
	 * Gets an archive date by the specified archive date string.
	 * 
	 * @param archiveDate
	 *            the specified archive date stirng (yyyy/MM)
	 * @return an archive date, {@code null} if not found
	 * @throws RepositoryException
	 *             repository exception
	 */
	JSONObject getByArchiveDate(final String archiveDate) throws RepositoryException;

	/**
	 * Gets archive dates.
	 * 
	 * @return a list of archive date, returns an empty list if not found
	 * @throws RepositoryException
	 *             repository exception
	 */
	List<JSONObject> getArchiveDates() throws RepositoryException;
}
