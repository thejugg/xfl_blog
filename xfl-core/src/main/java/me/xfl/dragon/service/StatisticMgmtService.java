package me.xfl.dragon.service;

import java.util.logging.Level;
import java.util.logging.Logger;

import me.xfl.dragon.model.Statistic;
import me.xfl.dragon.repository.StatisticRepository;
import me.xfl.dragon.repository.impl.StatisticRepositoryImpl;
import me.xfl.jugg.repository.RepositoryException;
import me.xfl.jugg.repository.Transaction;
import me.xfl.jugg.service.ServiceException;

import org.json.JSONObject;

/**
 * Statistic management service.
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Jul 18, 2012
 * @since 0.5.0
 */
public final class StatisticMgmtService {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(StatisticMgmtService.class.getName());
	/**
	 * Statistic repository.
	 */
	private StatisticRepository statisticRepository = StatisticRepositoryImpl.getInstance();

	/**
	 * Updates the statistic with the specified statistic.
	 * 
	 * @param statistic
	 *            the specified statistic
	 * @throws ServiceException
	 *             service exception
	 */
	public void updateStatistic(final JSONObject statistic) throws ServiceException {
		final Transaction transaction = statisticRepository.beginTransaction();
		try {
			statisticRepository.update(Statistic.STATISTIC, statistic);
			transaction.commit();
		} catch (final RepositoryException e) {
			if (transaction.isActive()) {
				transaction.rollback();
			}
			LOGGER.log(Level.SEVERE, "Updates statistic failed", e);
		}

		LOGGER.log(Level.FINER, "Updates statistic successfully");
	}

	/**
	 * Gets the {@link StatisticMgmtService} singleton.
	 * 
	 * @return the singleton
	 */
	public static StatisticMgmtService getInstance() {
		return SingletonHolder.SINGLETON;

	}

	/**
	 * Private constructor.
	 */
	private StatisticMgmtService() {
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
		private static final StatisticMgmtService SINGLETON = new StatisticMgmtService();

		/**
		 * Private default constructor.
		 */
		private SingletonHolder() {
		}
	}
}
