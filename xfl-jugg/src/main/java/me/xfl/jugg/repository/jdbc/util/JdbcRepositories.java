package me.xfl.jugg.repository.jdbc.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.xfl.jugg.repository.RepositoryException;
import me.xfl.jugg.util.Repositories;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * JdbcRepositories utilities.
 * 
 * @author <a href="mailto:wmainlove@gmail.com">Love Yao</a>
 * @version 1.0.0.0, Dec 20, 2011
 */
public final class JdbcRepositories {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(JdbcRepositories.class.getName());
	/**
	 * the String jsonType to JdbcType.
	 */
	// @SuppressWarnings("serial")
	// private static final Map<String, Integer> JSONTYPETOJDBCTYPEMAP =
	// new HashMap<String, Integer>() {
	// {
	//
	// put("int", Types.INTEGER);
	// put("long", Types.BIGINT);
	// put("String", Types.VARCHAR);
	// put("boolean", Types.BOOLEAN);
	// put("double", Types.DOUBLE);
	//
	// }
	// };
	/**
	 * /** to json "repositories".
	 */
	private static final String REPOSITORIES = "repositories";
	/**
	 * /** to json "name".
	 */
	private static final String NAME = "name";
	/**
	 * /** to json "keys".
	 */
	private static final String KEYS = "keys";
	/**
	 * /** to json "type".
	 */
	private static final String TYPE = "type";
	/**
	 * /** to json "nullable".
	 */
	private static final String NULLABLE = "nullable";
	/**
	 * /** to json "length".
	 */
	private static final String LENGTH = "length";
	/**
	 * ** to json "iskey".
	 */
	private static final String ISKEY = "iskey";
	/**
	 * the default keyname.
	 */
	public static final String OID = "oId";
	/**
	 * store all repository filed definition in a Map.
	 * <p>
	 * key: the name of the repository value: list of all the FieldDefinition
	 * </p>
	 */
	private static Map<String, List<FieldDefinition>> repositoriesMap = null;

	/**
	 * get the RepositoriesMap ,lazy load.
	 * 
	 * @return Map<String, List<FieldDefinition>>
	 */
	public static Map<String, List<FieldDefinition>> getRepositoriesMap() {
		if (repositoriesMap == null) {
			try {
				initRepositoriesMap();
			} catch (final Exception e) {
				LOGGER.log(Level.SEVERE, "initRepositoriesMap mistake " + e.getMessage(), e);
			}
		}

		return repositoriesMap;
	}

	/**
	 * init the repositoriesMap.
	 * 
	 * @throws JSONException
	 *             JSONException
	 * @throws RepositoryException
	 *             RepositoryException
	 */
	private static void initRepositoriesMap() throws JSONException, RepositoryException {
		final JSONObject jsonObject = Repositories.getRepositoriesDescription();
		if (jsonObject == null) {
			LOGGER.warning("the repository description[repository.json] miss");
			return;
		}

		jsonToRepositoriesMap(jsonObject);
	}

	/**
	 * analysis json data structure to java Map structure.
	 * 
	 * @param jsonObject
	 *            json Model
	 * @throws JSONException
	 *             JSONException
	 */
	private static void jsonToRepositoriesMap(final JSONObject jsonObject) throws JSONException {
		repositoriesMap = new HashMap<String, List<FieldDefinition>>();

		final JSONArray repositoritArray = jsonObject.getJSONArray(REPOSITORIES);

		JSONObject repositoritObject = null;
		JSONObject fieldDefinitionObject = null;

		for (int i = 0; i < repositoritArray.length(); i++) {
			repositoritObject = repositoritArray.getJSONObject(i);
			final String repositoryName = repositoritObject.getString(NAME);

			final List<FieldDefinition> fieldDefinitions = new ArrayList<FieldDefinition>();
			repositoriesMap.put(repositoryName, fieldDefinitions);

			final JSONArray keysJsonArray = repositoritObject.getJSONArray(KEYS);

			FieldDefinition definition = null;
			for (int j = 0; j < keysJsonArray.length(); j++) {
				fieldDefinitionObject = keysJsonArray.getJSONObject(j);
				definition = fillFieldDefinitionData(fieldDefinitionObject);
				fieldDefinitions.add(definition);
			}
		}
	}

	/**
	 * fillFieldDefinitionData.
	 * 
	 * @param fieldDefinitionObject
	 *            josn model
	 * @return {@link FieldDefinition}
	 * @throws JSONException
	 *             JSONException
	 */
	private static FieldDefinition fillFieldDefinitionData(final JSONObject fieldDefinitionObject) throws JSONException {
		final FieldDefinition fieldDefinition = new FieldDefinition();
		fieldDefinition.setName(fieldDefinitionObject.getString(NAME));

		// final Integer type =
		// JSONTYPETOJDBCTYPEMAP
		// .get(fieldDefinitionObject.getString(TYPE));
		// if (type == null) {
		// LOGGER.severe("the type [" + fieldDefinitionObject.getString(TYPE)
		// + "] no mapping defined now!!!!");
		// throw new RuntimeException("the type ["
		// + fieldDefinitionObject.getString(TYPE)
		// + "] no mapping defined now!!!!");
		// }

		fieldDefinition.setType(fieldDefinitionObject.getString(TYPE));
		fieldDefinition.setNullable(fieldDefinitionObject.optBoolean(NULLABLE));
		fieldDefinition.setLength(fieldDefinitionObject.optInt(LENGTH));
		fieldDefinition.setIsKey(fieldDefinitionObject.optBoolean(ISKEY));

		/**
		 * the default key name is 'old'.
		 */
		if (OID.equals(fieldDefinition.getName())) {
			fieldDefinition.setIsKey(true);
		}

		return fieldDefinition;

	}

	/**
	 * set the repositoriesMap.
	 * 
	 * @param repositoriesMap
	 *            repositoriesMap
	 */
	public static void setRepositoriesMap(final Map<String, List<FieldDefinition>> repositoriesMap) {
		JdbcRepositories.repositoriesMap = repositoriesMap;
	}

	/**
	 * Private constructor.
	 */
	private JdbcRepositories() {
	}
}
