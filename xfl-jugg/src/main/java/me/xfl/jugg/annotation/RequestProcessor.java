package me.xfl.jugg.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that an annotated type for HTTP servlet request processing.
 * 
 * <p>
 * A request processor is the C (controller) of MVC pattern, which has some
 * methods for requests processing, see {@link RequestProcessing} for more
 * details.
 * </p>
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Jul 17, 2011
 * @see RequestProcessing
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestProcessor {

}
