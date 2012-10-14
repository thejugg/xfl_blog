package me.xfl.jugg;

/**
 * Jugg runtime JDBC database.
 * 
 * <p>
 * If Jugg runs on local environment, Jugg will read database configurations
 * from file "local.properties".
 * </p>
 * 
 * @author <a href="mailto:wmainlove@gmail.com">Love Yao</a>
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.2, May 29, 2012
 * @see Juggs#getRuntimeDatabase()
 */
public enum RuntimeDatabase {

	/**
	 * Oracle.
	 */
	ORACLE,
	/**
	 * MySQL.
	 */
	MYSQL,
	/**
	 * SYBASE.
	 */
	SYBASE,
	/**
	 * MSSQL.
	 */
	MSSQL,
	/**
	 * DB2.
	 */
	DB2
}
