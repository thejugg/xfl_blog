package me.xfl.dragon.service;

import java.util.logging.Level;
import java.util.logging.Logger;

import me.xfl.dragon.model.Statistic;
import me.xfl.dragon.repository.StatisticRepository;
import me.xfl.dragon.repository.impl.StatisticRepositoryImpl;
import me.xfl.jugg.repository.RepositoryException;
import me.xfl.jugg.service.ServiceException;

import org.json.JSONObject;

/**
 * Statistic query service.
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Jul 18, 2012
 * @since 0.5.0
 */
public final class StatisticQueryService {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(StatisticQueryService.class.getName());
	/**
	 * Statistic repository.
	 */
	private StatisticRepository statisticRepository = StatisticRepositoryImpl.getInstance();

	/**
	 * Gets the statistic.
	 * 
	 * @return statistic, returns {@code null} if not found
	 * @throws ServiceException
	 *             if repository exception
	 */
	public JSONObject getStatistic() throws ServiceException {
		try {
			final JSONObject ret = statisticRepository.get(Statistic.STATISTIC);
			if (null == ret) {
				LOGGER.log(Level.WARNING, "Can not load statistic from repository");
				return null;
			}

			return ret;
		} catch (final RepositoryException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Gets the {@link StatisticQueryService} singleton.
	 * 
	 * @return the singleton
	 */
	public static StatisticQueryService getInstance() {
		return SingletonHolder.SINGLETON;
	}

	/**
	 * Private constructor.
	 */
	private StatisticQueryService() {
	}

	/**
	 * Singleton holder.
	 * 
	 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
	 * @version 1.0.0.0, Oct 24, 2011
	 */
	private static final class SingletonHolder {

		/**
		 * Singleton.
		 */
		private static final StatisticQueryService SINGLETON = new StatisticQueryService();

		/**
		 * Private default constructor.
		 */
		private SingletonHolder() {
		}
	}
}
