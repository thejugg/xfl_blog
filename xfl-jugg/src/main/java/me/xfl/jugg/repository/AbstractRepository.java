package me.xfl.jugg.repository;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.xfl.jugg.Juggs;
import me.xfl.jugg.Keys;
import me.xfl.jugg.RuntimeDatabase;
import me.xfl.jugg.RuntimeEnv;
import me.xfl.jugg.cache.Cache;
import me.xfl.jugg.model.Pagination;
import me.xfl.jugg.repository.jdbc.JDBCRepositoryException;
import me.xfl.jugg.util.Callstacks;
import me.xfl.jugg.util.Repositories;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Abstract repository.
 * 
 * <p>
 * This is a base adapter for wrapped {@link #repository repository}, the
 * underlying repository will be instantiated in the
 * {@link #AbstractRepository(java.lang.String) constructor} with
 * {@link Latkes#getRuntimeEnv() the current runtime environment}.
 * </p>
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.1.1, Aug 27, 2012
 */
public abstract class AbstractRepository implements Repository {
	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(AbstractRepository.class.getName());
	/**
	 * Repository.
	 */
	private Repository repository;

	/**
	 * Constructs a repository with the specified name.
	 * 
	 * @param name
	 *            the specified name
	 */
	@SuppressWarnings("unchecked")
	public AbstractRepository(final String name) {
		final RuntimeEnv runtimeEnv = Juggs.getRuntimeEnv();
		try {
			Class<Repository> repositoryClass = null;

			switch (runtimeEnv) {
			case LOCAL:
				RuntimeDatabase runtimeDatabase = Juggs.getRuntimeDatabase();
				switch (runtimeDatabase) {
				case MYSQL:
					repositoryClass = (Class<Repository>) Class.forName("me.xfl.jugg.repository.jdbc.JdbcRepository");
					break;
				default:
					throw new RuntimeException("The runtime database [" + runtimeDatabase + "] is not support NOW!");
				}
				break;
			case GAE:
				repositoryClass = (Class<Repository>) Class.forName("me.xfl.jugg.repository.gae.GAERepository");
				break;
			default:
				throw new RuntimeException("Latke runs in the hell.... Please set the enviornment correctly");
			}

			final Constructor<Repository> constructor = repositoryClass.getConstructor(String.class);
			repository = constructor.newInstance(name);
		} catch (final Exception e) {
			throw new RuntimeException("Can not initialize repository!", e);
		}

		Repositories.addRepository(repository);
		LOGGER.log(Level.INFO, "Constructed repository[name={0}]", name);
	}

	@Override
	public String add(final JSONObject jsonObject) throws RepositoryException {
		if (!isWritable() && !isInternalCall()) {
			throw new RepositoryException("The repository[name=" + getName() + "] is not writable at present");
		}

		Repositories.check(getName(), jsonObject, Keys.OBJECT_ID);

		return repository.add(jsonObject);
	}

	@Override
	public void update(final String id, final JSONObject jsonObject) throws RepositoryException {
		if (!isWritable() && !isInternalCall()) {
			throw new RepositoryException("The repository[name=" + getName() + "] is not writable at present");
		}

		Repositories.check(getName(), jsonObject, Keys.OBJECT_ID);

		repository.update(id, jsonObject);
	}

	@Override
	public void remove(final String id) throws RepositoryException {
		if (!isWritable() && !isInternalCall()) {
			throw new RepositoryException("The repository[name=" + getName() + "] is not writable at present");
		}

		repository.remove(id);
	}

	@Override
	public JSONObject get(final String id) throws RepositoryException {
		try {
			return repository.get(id);
		} catch (final JDBCRepositoryException e) {
			LOGGER.log(Level.WARNING, "SQL exception[msg={0}]", e.getMessage());
			return null;
		}
	}

	@Override
	public Map<String, JSONObject> get(final Iterable<String> ids) throws RepositoryException {
		return repository.get(ids);
	}

	@Override
	public boolean has(final String id) throws RepositoryException {
		return repository.has(id);
	}

	@Override
	public JSONObject get(final Query query) throws RepositoryException {
		try {
			return repository.get(query);
		} catch (final JDBCRepositoryException e) {
			LOGGER.log(Level.WARNING, "SQL exception[msg={0}]", e.getMessage());

			// XXX: Results.defaultPagination?
			final JSONObject ret = new JSONObject();
			final JSONObject pagination = new JSONObject();
			ret.put(Pagination.PAGINATION, pagination);
			pagination.put(Pagination.PAGINATION_PAGE_COUNT, 0);
			final JSONArray results = new JSONArray();
			ret.put(Keys.RESULTS, results);

			return ret;
		}
	}

	@Override
	public List<JSONObject> getRandomly(final int fetchSize) throws RepositoryException {
		return repository.getRandomly(fetchSize);
	}

	@Override
	public long count() throws RepositoryException {
		return repository.count();
	}

	@Override
	public Transaction beginTransaction() {
		return repository.beginTransaction();
	}

	@Override
	public final boolean isCacheEnabled() {
		return repository.isCacheEnabled();
	}

	@Override
	public final void setCacheEnabled(final boolean isCacheEnabled) {
		repository.setCacheEnabled(isCacheEnabled);
	}

	@Override
	public String getName() {
		return repository.getName();
	}

	@Override
	public Cache<String, Serializable> getCache() {
		return repository.getCache();
	}

	@Override
	public boolean isWritable() {
		return repository.isWritable();
	}

	@Override
	public void setWritable(final boolean writable) {
		repository.setWritable(writable);
	}

	/**
	 * Gets the underlying repository.
	 * 
	 * @return underlying repository
	 */
	protected Repository getUnderlyingRepository() {
		return repository;
	}

	/**
	 * Checks the current method is whether invoked as internal call.
	 * 
	 * @return {@code true} if the current method is invoked as internal call,
	 *         return {@code false} otherwise
	 */
	private static boolean isInternalCall() {
		return Callstacks.isCaller("me.xfl.jugg.remote.RepositoryAccessor", "*");
	}
}
