package me.xfl.jugg.repository.jdbc.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.xfl.jugg.Juggs;
import me.xfl.jugg.util.Callstacks;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;
import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * JDBC connection utilities.
 * 
 * <p>
 * Uses <a href="http://jolbox.com/">BoneCP</a> as the underlying connection
 * pool.
 * </p>
 * 
 * @author <a href="mailto:wmainlove@gmail.com">Love Yao</a>
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.6, Aug 27, 2012
 */
public final class Connections {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(Connections.class.getName());
	/**
	 * Pool type.
	 */
	private static String poolType;
	/**
	 * Connection pool - BoneCP.
	 */
	private static BoneCP boneCP;
	/**
	 * Connection pool - c3p0.
	 */
	private static ComboPooledDataSource c3p0;
	/**
	 * Transaction isolation.
	 */
	private static String transactionIsolation;
	/**
	 * Transaction isolation integer value.
	 */
	private static int transactionIsolationInt;
	/**
	 * JDBC URL.
	 */
	private static String url;
	/**
	 * JDBC user name.
	 */
	private static String userName;
	/**
	 * JDBC password.
	 */
	private static String password;

	static {
		try {
			final String driver = Juggs.getLocalProperty("jdbc.driver");
			Class.forName(driver);

			poolType = Juggs.getLocalProperty("jdbc.pool");

			url = Juggs.getLocalProperty("jdbc.URL");
			userName = Juggs.getLocalProperty("jdbc.username");
			password = Juggs.getLocalProperty("jdbc.password");
			final int minConnCnt = Integer.valueOf(Juggs.getLocalProperty("jdbc.minConnCnt"));
			final int maxConnCnt = Integer.valueOf(Juggs.getLocalProperty("jdbc.maxConnCnt"));
			transactionIsolation = Juggs.getLocalProperty("jdbc.transactionIsolation");
			if ("NONE".equals(transactionIsolation)) {
				transactionIsolationInt = Connection.TRANSACTION_NONE;
			} else if ("READ_COMMITTED".equals(transactionIsolation)) {
				transactionIsolationInt = Connection.TRANSACTION_READ_COMMITTED;
			} else if ("READ_UNCOMMITTED".equals(transactionIsolation)) {
				transactionIsolationInt = Connection.TRANSACTION_READ_UNCOMMITTED;
			} else if ("REPEATABLE_READ".equals(transactionIsolation)) {
				transactionIsolationInt = Connection.TRANSACTION_REPEATABLE_READ;
			} else if ("SERIALIZABLE".equals(transactionIsolation)) {
				transactionIsolationInt = Connection.TRANSACTION_SERIALIZABLE;
			} else {
				throw new IllegalStateException("Undefined transaction isolation [" + transactionIsolation + ']');
			}

			if ("BoneCP".equals(poolType)) {
				LOGGER.log(Level.FINE, "Initializing database connection pool [BoneCP]");

				final BoneCPConfig config = new BoneCPConfig();
				config.setDefaultAutoCommit(false);
				config.setDefaultTransactionIsolation(transactionIsolation);
				config.setJdbcUrl(url);
				config.setUsername(userName);
				config.setPassword(password);
				config.setMinConnectionsPerPartition(minConnCnt);
				config.setMaxConnectionsPerPartition(maxConnCnt);
				config.setPartitionCount(1);
				config.setDisableJMX(true);

				boneCP = new BoneCP(config);
			} else if ("c3p0".equals(poolType)) {
				LOGGER.log(Level.FINE, "Initializing database connection pool [c3p0]");

				// Disable JMX
				System.setProperty("com.mchange.v2.c3p0.management.ManagementCoordinator",
						"com.mchange.v2.c3p0.management.NullManagementCoordinator");

				c3p0 = new ComboPooledDataSource();
				c3p0.setUser(userName);
				c3p0.setPassword(password);
				c3p0.setJdbcUrl(url);
				c3p0.setDriverClass(driver);
				c3p0.setInitialPoolSize(minConnCnt);
				c3p0.setMinPoolSize(minConnCnt);
				c3p0.setMaxPoolSize(maxConnCnt);
				c3p0.setMaxStatementsPerConnection(maxConnCnt);
			} else if ("none".equals(poolType)) {
				LOGGER.info("Do not use database connection pool");
			}

			LOGGER.info("Initialized connection pool");
		} catch (final Exception e) {
			LOGGER.log(Level.SEVERE, "Can not initialize database connection", e);
		}
	}

	/**
	 * Gets a connection.
	 * 
	 * @return a connection
	 * @throws SQLException
	 *             SQL exception
	 */
	public static Connection getConnection() throws SQLException {
		if (LOGGER.isLoggable(Level.FINEST)) {
			Callstacks.printCallstack(Level.FINEST, new String[] { "me.xfl" }, null);
		}

		if ("BoneCP".equals(poolType)) {
			LOGGER.log(Level.FINEST, "Connection pool[createdConns={0}, freeConns={1}, leasedConns={2}]",
					new Object[] { boneCP.getTotalCreatedConnections(), boneCP.getTotalFree(), boneCP.getTotalLeased() });

			return boneCP.getConnection();
		} else if ("c3p0".equals(poolType)) {
			LOGGER.log(Level.FINEST, "Connection pool[createdConns={0}, freeConns={1}, leasedConns={2}]",
					new Object[] { c3p0.getNumConnections(), c3p0.getNumIdleConnections(), c3p0.getNumBusyConnections() });
			final Connection ret = c3p0.getConnection();
			ret.setTransactionIsolation(transactionIsolationInt);

			return ret;
		} else if ("none".equals(poolType)) {
			return DriverManager.getConnection(url, userName, password);
		}

		throw new IllegalStateException("Not found database connection pool [" + poolType + "]");
	}

	/**
	 * Shutdowns the connection pool.
	 */
	public static void shutdownConnectionPool() {
		if (null != boneCP) {
			boneCP.shutdown();
		}

		LOGGER.info("Shutdowns connection pool sucessfully");
	}

	/**
	 * Private constructor.
	 */
	private Connections() {
	}
}
