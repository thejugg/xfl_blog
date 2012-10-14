package me.xfl.jugg.plugin;

import java.util.Map;

/**
 * View load event data.
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Jun 11, 2011
 */
public final class ViewLoadEventData {

	/**
	 * Name of the template file.
	 */
	private String viewName;
	/**
	 * Data model.
	 */
	private Map<String, Object> dataModel;

	/**
	 * Gets the data model.
	 * 
	 * @return data model
	 */
	public Map<String, Object> getDataModel() {
		return dataModel;
	}

	/**
	 * Sets the data model with the specified data model.
	 * 
	 * @param dataModel
	 *            the specified data model
	 */
	public void setDataModel(final Map<String, Object> dataModel) {
		this.dataModel = dataModel;
	}

	/**
	 * Gets the name of the template file.
	 * 
	 * @return name of the template file
	 */
	public String getViewName() {
		return viewName;
	}

	/**
	 * Sets the name of the template file with the specified name.
	 * 
	 * @param viewName
	 *            the specified name
	 */
	public void setViewName(final String viewName) {
		this.viewName = viewName;
	}
}
