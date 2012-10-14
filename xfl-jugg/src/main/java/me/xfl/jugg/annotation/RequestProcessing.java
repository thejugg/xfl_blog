package me.xfl.jugg.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import me.xfl.jugg.servlet.HTTPRequestMethod;
import me.xfl.jugg.servlet.URIPatternMode;

/**
 * Indicates that an annotated method for HTTP servlet request processing.
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.4, May 1, 2012
 * @see RequestProcessor
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestProcessing {

	/**
	 * The dispatching URI path patterns of a request.
	 * 
	 * <p>
	 * Semantics of these values adapting to the URL patterns
	 * (&lt;url-pattern/&gt;) configures in web application descriptor (web.xml)
	 * of a servlet. Ant-style path pattern and regular expression pattern are
	 * also supported.
	 * </p>
	 */
	String[] value() default {};

	/**
	 * The URI patterns mode.
	 */
	URIPatternMode uriPatternsMode() default URIPatternMode.ANT_PATH;

	/**
	 * The HTTP request methods the annotated method should process.
	 */
	HTTPRequestMethod[] method() default { HTTPRequestMethod.GET };

	/**
	 * Checks dose whether the URI patterns with context path.
	 * 
	 * <p>
	 * For example, the context path is /blog, and the annotation
	 * 
	 * <pre>
	 * {@code @RequestProcessing(value = "/index")}
	 * </pre>
	 * 
	 * means to serve /blog/index.
	 * </p>
	 * 
	 * <p>
	 * If the annotation
	 * 
	 * <pre>
	 * {@code @RequestProcessing(value = "/index", isWithContextPath=false)}
	 * </pre>
	 * 
	 * means to serve /index.
	 * </p>
	 */
	boolean isWithContextPath() default true;
}
