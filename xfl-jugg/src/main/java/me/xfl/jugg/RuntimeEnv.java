package me.xfl.jugg;

/**
 * Latke runtime environment.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Jun 24, 2011
 * @see Latkes#getRuntimeEnv() 
 */
public enum RuntimeEnv {

    /**
     * Indicates Latke runs on local (standard Servlet container).
     */
    LOCAL,
    /**
     * Indicates Latke runs on <a href="http://code.google.com/appengine">
     * Google App Engine</a>.
     */
    GAE,
}
