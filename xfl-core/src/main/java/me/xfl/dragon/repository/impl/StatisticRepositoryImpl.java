package me.xfl.dragon.repository.impl;

import java.util.logging.Logger;

import me.xfl.dragon.model.Statistic;
import me.xfl.dragon.repository.StatisticRepository;
import me.xfl.jugg.repository.AbstractRepository;

/**
 * Statistic repository.
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Jan 12, 2011
 * @since 0.3.1
 */
public final class StatisticRepositoryImpl extends AbstractRepository implements StatisticRepository {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(StatisticRepositoryImpl.class.getName());
	/**
	 * Singleton.
	 */
	private static final StatisticRepositoryImpl SINGLETON = new StatisticRepositoryImpl(Statistic.STATISTIC);

	/**
	 * Gets the {@link StatisticRepositoryImpl} singleton.
	 * 
	 * @return the singleton
	 */
	public static StatisticRepositoryImpl getInstance() {
		return SINGLETON;
	}

	/**
	 * Private constructor.
	 * 
	 * @param name
	 *            the specified name
	 */
	private StatisticRepositoryImpl(final String name) {
		super(name);
	}
}
