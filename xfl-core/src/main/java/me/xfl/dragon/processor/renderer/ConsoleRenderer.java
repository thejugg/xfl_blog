package me.xfl.dragon.processor.renderer;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.xfl.dragon.DragonServletListener;
import me.xfl.jugg.servlet.HTTPRequestContext;
import me.xfl.jugg.servlet.renderer.freemarker.AbstractFreeMarkerRenderer;
import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * <a href="http://freemarker.org">FreeMarker</a> HTTP response renderer for
 * administrator console and initialization rendering.
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Mar 29, 2012
 * @since 0.4.1
 */
public final class ConsoleRenderer extends AbstractFreeMarkerRenderer {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(ConsoleRenderer.class.getName());
	/**
	 * FreeMarker configuration.
	 */
	public static final Configuration TEMPLATE_CFG;

	static {
		TEMPLATE_CFG = new Configuration();
		TEMPLATE_CFG.setDefaultEncoding("UTF-8");
		try {
			final String webRootPath = DragonServletListener.getWebRoot();

			TEMPLATE_CFG.setDirectoryForTemplateLoading(new File(webRootPath));
		} catch (final IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	@Override
	protected Template getTemplate(final String templateDirName, final String templateName) throws IOException {
		return TEMPLATE_CFG.getTemplate(templateName);
	}

	@Override
	protected void beforeRender(final HTTPRequestContext context) throws Exception {
	}

	@Override
	protected void afterRender(final HTTPRequestContext context) throws Exception {
	}
}
