package me.xfl.dragon.service;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.xfl.dragon.model.ArchiveDate;
import me.xfl.dragon.repository.ArchiveDateRepository;
import me.xfl.dragon.repository.impl.ArchiveDateRepositoryImpl;
import me.xfl.jugg.repository.RepositoryException;
import me.xfl.jugg.service.ServiceException;

import org.json.JSONObject;


/**
 * Archive date query service.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Feb 7, 2012
 * @since 0.4.0
 */
public final class ArchiveDateQueryService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(ArchiveDateQueryService.class.getName());
    /**
     * Archive date repository.
     */
    private ArchiveDateRepository archiveDateRepository = ArchiveDateRepositoryImpl.getInstance();

    /**
     * Gets all archive dates.
     * 
     * @return a list of archive dates, returns an empty list if not found
     * @throws ServiceException service exception
     */
    public List<JSONObject> getArchiveDates() throws ServiceException {
        try {
            return archiveDateRepository.getArchiveDates();
        } catch (final RepositoryException e) {
            LOGGER.log(Level.SEVERE, "Gets archive dates failed", e);
            throw new ServiceException("Gets archive dates failed");
        }
    }

    /**
     * Gets an archive date by the specified archive date string.
     * 
     * @param archiveDateString the specified archive date string (yyyy/MM)
     * @return for example,
     * <pre>
     * {
     *     "archiveDate": {
     *         "oId": "",
     *         "archiveTime": "",
     *         "archiveDatePublishedArticleCount": int,
     *         "archiveDateArticleCount": int
     *     }
     * }
     * </pre>, returns {@code null} if not found
     * @throws ServiceException service exception
     */
    public JSONObject getByArchiveDateString(final String archiveDateString) throws ServiceException {
        final JSONObject ret = new JSONObject();

        try {
            final JSONObject archiveDate = archiveDateRepository.getByArchiveDate(archiveDateString);
            
            if (null == archiveDate) {
                return null;
            }
            
            ret.put(ArchiveDate.ARCHIVE_DATE, archiveDate);

            return ret;
        } catch (final RepositoryException e) {
            LOGGER.log(Level.SEVERE, "Gets archive date[string=" + archiveDateString + "] failed", e);
            throw new ServiceException("Gets archive date[string=" + archiveDateString + "] failed");
        }
    }

    /**
     * Gets the {@link ArchiveDateQueryService} singleton.
     *
     * @return the singleton
     */
    public static ArchiveDateQueryService getInstance() {
        return SingletonHolder.SINGLETON;
    }

    /**
     * Private constructor.
     */
    private ArchiveDateQueryService() {
    }

    /**
     * Singleton holder.
     *
     * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
     * @version 1.0.0.0, Feb 7, 2012
     */
    private static final class SingletonHolder {

        /**
         * Singleton.
         */
        private static final ArchiveDateQueryService SINGLETON =
                new ArchiveDateQueryService();

        /**
         * Private default constructor.
         */
        private SingletonHolder() {
        }
    }
}
