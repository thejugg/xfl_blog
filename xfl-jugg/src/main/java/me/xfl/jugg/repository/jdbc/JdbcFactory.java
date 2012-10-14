package me.xfl.jugg.repository.jdbc;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.xfl.jugg.Juggs;
import me.xfl.jugg.RuntimeDatabase;

/**
 * 
 * JdbcFactory.
 * 
 * @author <a href="mailto:wmainlove@gmail.com">Love Yao</a>
 * @version 1.0.0.0, Dec 20, 2011
 */
public final class JdbcFactory implements JdbcDatabase {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(JdbcRepository.class.getName());
	/**
	 * the holder of the databaseSolution.
	 */
	private AbstractJdbcDatabaseSolution databaseSolution;

	/**
	 * the singleton of jdbcfactory.
	 */
	private static JdbcFactory jdbcFactory;

	/**
	 * all JdbcDatabaseSolution className in here.
	 */
	@SuppressWarnings("serial")
	private static Map<RuntimeDatabase, String> jdbcDatabaseSolutionMap = new HashMap<RuntimeDatabase, String>() {
		{
			put(RuntimeDatabase.MYSQL, "me.xfl.jugg.repository.jdbc.mysql.MysqlJdbcDatabaseSolution");

		}
	};

	// @Override
	// 2012-8-7 9:40 public boolean createTable(final String tableName,
	// final List<FieldDefinition> fieldDefinitions) throws SQLException {
	// return databaseSolution.createTable(tableName, fieldDefinitions);
	// }

	@Override
	public boolean clearTable(final String tableName, final boolean ifdrop) throws SQLException {
		return databaseSolution.clearTable(tableName, ifdrop);
	}

	/**
	 * singleton way to get jdbcFactory.
	 * 
	 * @return JdbcFactory jdbcFactory.
	 */
	public static synchronized JdbcFactory createJdbcFactory() {

		if (jdbcFactory == null) {
			jdbcFactory = new JdbcFactory();
		}
		return jdbcFactory;
	}

	/**
	 * Private constructor.
	 */
	private JdbcFactory() {

		/**
		 * Latkes.getRuntimeDatabase();
		 */
		final String databaseSolutionClassName = jdbcDatabaseSolutionMap.get(Juggs.getRuntimeDatabase());
		try {
			databaseSolution = (AbstractJdbcDatabaseSolution) Class.forName(databaseSolutionClassName).newInstance();
		} catch (final Exception e) {
			LOGGER.log(Level.SEVERE, "init the [" + databaseSolutionClassName + "]JdbcDatabaseSolution instance wrong", e);
		}

	}

	@Override
	public String queryPage(final int start, final int end, final String selectSql, final String filterSql, final String orderBySql,
			final String tableName) {

		return databaseSolution.queryPage(start, end, selectSql, filterSql, orderBySql, tableName);
	}

	@Override
	public String getRandomlySql(final String tableName, final int fetchSize) {

		return databaseSolution.getRandomlySql(tableName, fetchSize);
	}

}
