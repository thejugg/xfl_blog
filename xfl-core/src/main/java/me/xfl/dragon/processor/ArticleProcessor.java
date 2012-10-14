package me.xfl.dragon.processor;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import me.xfl.dragon.model.ArchiveDate;
import me.xfl.dragon.model.Article;
import me.xfl.dragon.model.Common;
import me.xfl.dragon.model.PageTypes;
import me.xfl.dragon.model.Preference;
import me.xfl.dragon.model.Tag;
import me.xfl.dragon.model.UserExt;
import me.xfl.dragon.processor.renderer.ConsoleRenderer;
import me.xfl.dragon.processor.renderer.FrontRenderer;
import me.xfl.dragon.processor.util.Filler;
import me.xfl.dragon.service.ArchiveDateQueryService;
import me.xfl.dragon.service.ArticleQueryService;
import me.xfl.dragon.service.CommentQueryService;
import me.xfl.dragon.service.PreferenceQueryService;
import me.xfl.dragon.service.TagQueryService;
import me.xfl.dragon.service.UserQueryService;
import me.xfl.dragon.util.Articles;
import me.xfl.dragon.util.Skins;
import me.xfl.dragon.util.Users;
import me.xfl.dragon.util.comparator.Comparators;
import me.xfl.jugg.Juggs;
import me.xfl.jugg.Keys;
import me.xfl.jugg.annotation.RequestProcessing;
import me.xfl.jugg.annotation.RequestProcessor;
import me.xfl.jugg.cache.PageCaches;
import me.xfl.jugg.model.Pagination;
import me.xfl.jugg.model.User;
import me.xfl.jugg.service.LangPropsService;
import me.xfl.jugg.service.ServiceException;
import me.xfl.jugg.servlet.HTTPRequestContext;
import me.xfl.jugg.servlet.HTTPRequestMethod;
import me.xfl.jugg.servlet.URIPatternMode;
import me.xfl.jugg.servlet.renderer.JSONRenderer;
import me.xfl.jugg.servlet.renderer.TextHTMLRenderer;
import me.xfl.jugg.servlet.renderer.freemarker.AbstractFreeMarkerRenderer;
import me.xfl.jugg.util.CollectionUtils;
import me.xfl.jugg.util.Dates;
import me.xfl.jugg.util.Locales;
import me.xfl.jugg.util.Paginator;
import me.xfl.jugg.util.Requests;
import me.xfl.jugg.util.Stopwatchs;
import me.xfl.jugg.util.Strings;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;



/**
 * Article processor.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.1.2.4, May 21, 2012
 * @since 0.3.1
 */
@RequestProcessor
public final class ArticleProcessor {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(ArticleProcessor.class.getName());
    /**
     * Article query service.
     */
    private ArticleQueryService articleQueryService = ArticleQueryService.getInstance();
    /**
     * Tag query service.
     */
    private TagQueryService tagQueryService = TagQueryService.getInstance();
    /**
     * Comment query service.
     */
    private CommentQueryService commentQueryService = CommentQueryService.getInstance();
    /**
     * Filler.
     */
    private Filler filler = Filler.getInstance();
    /**
     * Language service.
     */
    private LangPropsService langPropsService = LangPropsService.getInstance();
    /**
     * Article utilities.
     */
    private Articles articleUtils = Articles.getInstance();
    /**
     * Preference query service.
     */
    private PreferenceQueryService preferenceQueryService = PreferenceQueryService.getInstance();
    /**
     * Archive date query service.
     */
    private ArchiveDateQueryService archiveDateQueryService = ArchiveDateQueryService.getInstance();
    /**
     * User query service.
     */
    private UserQueryService userQueryService = UserQueryService.getInstance();
    /**
     * Default update count for article random value.
     */
    private static final int DEFAULT_UPDATE_CNT = 10;

    /**
     * Shows the article view password form.
     * 
     * @param context the specified context
     * @param request the specified HTTP servlet request
     * @param response the specified HTTP servlet response
     * @throws Exception exception 
     */
    @RequestProcessing(value = "/console/article-pwd", method = HTTPRequestMethod.GET)
    public void showArticlePwdForm(final HTTPRequestContext context,
                                   final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        final String articleId = request.getParameter("articleId");
        final String articlePermalink = request.getParameter("articlePermalink");
        final String articleTitle = request.getParameter("articleTitle");
        final String articleAbstract = request.getParameter("articleAbstract");
        final String msg = request.getParameter(Keys.MSG);

        if (Strings.isEmptyOrNull(articleId) || Strings.isEmptyOrNull(articlePermalink) || Strings.isEmptyOrNull(articleTitle)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        final JSONObject article = articleQueryService.getArticleById(articleId);
        if (null == article) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        final AbstractFreeMarkerRenderer renderer = new ConsoleRenderer();
        context.setRenderer(renderer);
        renderer.setTemplateName("article-pwd.ftl");

        final Map<String, Object> dataModel = renderer.getDataModel();

        dataModel.put("articleId", articleId);
        dataModel.put("articlePermalink", articlePermalink);
        dataModel.put("articleTitle", articleTitle);
        dataModel.put("articleAbstract", articleAbstract);
        if (!Strings.isEmptyOrNull(msg)) {
            dataModel.put(Keys.MSG, msg);
        }

        final Map<String, String> langs = langPropsService.getAll(Juggs.getLocale());
        dataModel.putAll(langs);

        final JSONObject preference = preferenceQueryService.getPreference();
        dataModel.put(Preference.BLOG_TITLE, preference.getString(Preference.BLOG_TITLE));
        dataModel.put(Preference.BLOG_HOST, preference.getString(Preference.BLOG_HOST));
        dataModel.put(Common.VERSION, Juggs.getXFLVersion());
        dataModel.put(Common.STATIC_RESOURCE_VERSION, Juggs.getStaticResourceVersion());
        dataModel.put(Common.YEAR, String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));

        Keys.fillServer(dataModel);
        filler.fillMinified(dataModel);
    }

    /**
     * Processes the article view password form submits.
     * 
     * @param context the specified context
     * @param request the specified HTTP servlet request
     * @param response the specified HTTP servlet response
     * @throws Exception exception 
     */
    @RequestProcessing(value = "/console/article-pwd", method = HTTPRequestMethod.POST)
    public void onArticlePwdForm(final HTTPRequestContext context,
                                 final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        try {
            final String articleId = request.getParameter("articleId");
            final String pwdTyped = request.getParameter("pwdTyped");

            final JSONObject article = articleQueryService.getArticleById(articleId);

            if (article.getString(Article.ARTICLE_VIEW_PWD).equals(pwdTyped)) {
                final HttpSession session = request.getSession(false);
                if (null != session) {
                    @SuppressWarnings("unchecked")
                    Map<String, String> viewPwds = (Map<String, String>) session.getAttribute(Common.ARTICLES_VIEW_PWD);
                    if (null == viewPwds) {
                        viewPwds = new HashMap<String, String>();
                    }

                    viewPwds.put(articleId, pwdTyped);

                    session.setAttribute(Common.ARTICLES_VIEW_PWD, viewPwds);
                }

                response.sendRedirect(Juggs.getServePath() + article.getString(Article.ARTICLE_PERMALINK));
                return;
            }

            response.sendRedirect(Juggs.getServePath() + "/console/article-pwd" + articleUtils.buildArticleViewPwdFormParameters(article)
                                  + '&' + Keys.MSG + '=' + URLEncoder.encode(langPropsService.get("passwordNotMatchLabel"), "UTF-8"));
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, "Processes article view password form submits failed", e);

            try {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            } catch (final IOException ex) {
                LOGGER.severe(ex.getMessage());
            }
        }
    }

    /**
     * Gets random articles with the specified context.
     * 
     * @param context the specified context
     * @throws Exception exception 
     */
    @RequestProcessing(value = "/get-random-articles.do", method = HTTPRequestMethod.POST)
    public void getRandomArticles(final HTTPRequestContext context) throws Exception {
        final JSONObject jsonObject = new JSONObject();

        final JSONObject preference = preferenceQueryService.getPreference();
        final int displayCnt = preference.getInt(Preference.RANDOM_ARTICLES_DISPLAY_CNT);

        if (0 == displayCnt) {
            jsonObject.put(Common.RANDOM_ARTICLES, new ArrayList<JSONObject>());

            final JSONRenderer renderer = new JSONRenderer();
            context.setRenderer(renderer);
            renderer.setJSONObject(jsonObject);

            return;
        }

        Stopwatchs.start("Get Random Articles");
        final List<JSONObject> randomArticles = getRandomArticles(preference);

        jsonObject.put(Common.RANDOM_ARTICLES, randomArticles);

        final JSONRenderer renderer = new JSONRenderer();
        context.setRenderer(renderer);
        renderer.setJSONObject(jsonObject);

        Stopwatchs.end();
    }

    /**
     * Gets relevant articles with the specified context.
     * 
     * @param context the specified context
     * @param request the specified request
     * @param response the specified response
     * @throws Exception exception 
     */
    @RequestProcessing(value = "/article/id/*/relevant/articles", method = HTTPRequestMethod.GET)
    public void getRelevantArticles(final HTTPRequestContext context,
                                    final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        final JSONObject jsonObject = new JSONObject();

        final JSONObject preference = preferenceQueryService.getPreference();

        final int displayCnt = preference.getInt(Preference.RELEVANT_ARTICLES_DISPLAY_CNT);
        if (0 == displayCnt) {
            jsonObject.put(Common.RANDOM_ARTICLES, new ArrayList<JSONObject>());

            final JSONRenderer renderer = new JSONRenderer();
            context.setRenderer(renderer);
            renderer.setJSONObject(jsonObject);

            return;
        }

        Stopwatchs.start("Get Relevant Articles");
        final String requestURI = request.getRequestURI();

        final String articleId = StringUtils.substringBetween(requestURI, "/article/id/", "/relevant/articles");
        if (Strings.isEmptyOrNull(articleId)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);

            return;
        }

        final JSONObject article = articleQueryService.getArticleById(articleId);
        if (null == article) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);

            return;
        }

        final List<JSONObject> relevantArticles = articleQueryService.getRelevantArticles(article, preference);
        jsonObject.put(Common.RELEVANT_ARTICLES, relevantArticles);

        final JSONRenderer renderer = new JSONRenderer();
        context.setRenderer(renderer);
        renderer.setJSONObject(jsonObject);

        Stopwatchs.end();
    }

    /**
     * Gets article content with the specified context.
     * 
     * @param context the specified context
     * @param request the specified request 
     */
    @RequestProcessing(value = "/get-article-content", method = HTTPRequestMethod.GET)
    public void getArticleContent(final HTTPRequestContext context, final HttpServletRequest request) {
        final String articleId = request.getParameter("id");

        if (Strings.isEmptyOrNull(articleId)) {
            return;
        }

        final TextHTMLRenderer renderer = new TextHTMLRenderer();
        context.setRenderer(renderer);

        String content;
        try {
            content = articleQueryService.getArticleContent(articleId);
        } catch (final ServiceException e) {
            LOGGER.log(Level.SEVERE, "Can not get article content", e);
            return;
        }

        if (null == content) {
            return;
        }

        renderer.setContent(content);
    }

    /**
     * Gets articles paged with the specified context.
     * 
     * @param context the specified context
     * @param request the specified request
     */
    @RequestProcessing(value = "/articles/\\d+", uriPatternsMode = URIPatternMode.REGEX, method = HTTPRequestMethod.GET)
    public void getArticlesByPage(final HTTPRequestContext context, final HttpServletRequest request) {
        final JSONObject jsonObject = new JSONObject();
        final int currentPageNum = getArticlesPagedCurrentPageNum(request.getRequestURI());

        Stopwatchs.start("Get Articles Paged[pageNum=" + currentPageNum + ']');

        try {
            jsonObject.put(Keys.STATUS_CODE, true);

            final JSONObject preference = preferenceQueryService.getPreference();
            final int pageSize = preference.getInt(Preference.ARTICLE_LIST_DISPLAY_COUNT);
            final int windowSize = preference.getInt(Preference.ARTICLE_LIST_PAGINATION_WINDOW_SIZE);

            final StringBuilder pathBuilder = new StringBuilder();
            pathBuilder.append(currentPageNum).append('/').append(pageSize).append('/').append(windowSize);

            final JSONObject requestJSONObject = Requests.buildPaginationRequest(pathBuilder.toString());
            requestJSONObject.put(Article.ARTICLE_IS_PUBLISHED, true);

            final JSONObject result = articleQueryService.getArticles(requestJSONObject);
            final List<JSONObject> articles = CollectionUtils.jsonArrayToList(result.getJSONArray(Article.ARTICLES));

            final boolean hasMultipleUsers = Users.getInstance().hasMultipleUsers();
            if (hasMultipleUsers) {
                filler.setArticlesExProperties(articles, preference);
            } else {
                if (!articles.isEmpty()) {
                    final JSONObject author = articleUtils.getAuthor(articles.get(0));
                    filler.setArticlesExProperties(articles, author, preference);
                }
            }

            jsonObject.put(Keys.RESULTS, result);
        } catch (final Exception e) {
            jsonObject.put(Keys.STATUS_CODE, false);
            LOGGER.log(Level.SEVERE, "Gets article paged failed", e);
        } finally {
            Stopwatchs.end();
        }


        final JSONRenderer renderer = new JSONRenderer();
        context.setRenderer(renderer);
        renderer.setJSONObject(jsonObject);
    }

    /**
     * Gets tag articles paged with the specified context.
     * 
     * @param context the specified context
     * @param request the specified request
     */
    @RequestProcessing(value = "/articles/tags/.+/\\d+", uriPatternsMode = URIPatternMode.REGEX, method = HTTPRequestMethod.GET)
    public void getTagArticlesByPage(final HTTPRequestContext context, final HttpServletRequest request) {
        final JSONObject jsonObject = new JSONObject();

        String tagTitle = getTagArticlesPagedTag(request.getRequestURI());

        try {
            tagTitle = URLDecoder.decode(tagTitle, "UTF-8");
        } catch (final UnsupportedEncodingException e) {
            LOGGER.log(Level.SEVERE, "Gets tag title failed[requestURI=" + request.getRequestURI() + ']', e);
            tagTitle = "";
        }

        final int currentPageNum = getTagArticlesPagedCurrentPageNum(request.getRequestURI());

        Stopwatchs.start("Get Tag-Articles Paged[tagTitle=" + tagTitle + ", pageNum=" + currentPageNum + ']');

        try {
            jsonObject.put(Keys.STATUS_CODE, true);

            final JSONObject preference = preferenceQueryService.getPreference();
            final int pageSize = preference.getInt(Preference.ARTICLE_LIST_DISPLAY_COUNT);

            final JSONObject tagQueryResult = tagQueryService.getTagByTitle(tagTitle);

            if (null == tagQueryResult) {
                throw new Exception("Can not foud tag[title=" + tagTitle + "]");
            }

            final JSONObject tag = tagQueryResult.getJSONObject(Tag.TAG);
            final String tagId = tag.getString(Keys.OBJECT_ID);
            final List<JSONObject> articles = articleQueryService.getArticlesByTag(tagId, currentPageNum, pageSize);

            final int tagArticleCount = tag.getInt(Tag.TAG_PUBLISHED_REFERENCE_COUNT);
            final int pageCount = (int) Math.ceil((double) tagArticleCount / (double) pageSize);

            final boolean hasMultipleUsers = Users.getInstance().hasMultipleUsers();
            if (hasMultipleUsers) {
                filler.setArticlesExProperties(articles, preference);
            } else {
                if (!articles.isEmpty()) {
                    final JSONObject author = articleUtils.getAuthor(articles.get(0));
                    filler.setArticlesExProperties(articles, author, preference);
                }
            }

            final JSONObject result = new JSONObject();
            final JSONObject pagination = new JSONObject();
            pagination.put(Pagination.PAGINATION_PAGE_COUNT, pageCount);
            result.put(Pagination.PAGINATION, pagination);

            result.put(Article.ARTICLES, articles);

            jsonObject.put(Keys.RESULTS, result);
        } catch (final Exception e) {
            jsonObject.put(Keys.STATUS_CODE, false);
            LOGGER.log(Level.SEVERE, "Gets article paged failed", e);
        } finally {
            Stopwatchs.end();
        }

        final JSONRenderer renderer = new JSONRenderer();
        context.setRenderer(renderer);
        renderer.setJSONObject(jsonObject);
    }

    /**
     * Gets tag articles paged with the specified context.
     * 
     * @param context the specified context
     * @param request the specified request
     */
    @RequestProcessing(value = "/articles/archives/.+/\\d+", uriPatternsMode = URIPatternMode.REGEX, method = HTTPRequestMethod.GET)
    public void getArchivesArticlesByPage(final HTTPRequestContext context, final HttpServletRequest request) {
        final JSONObject jsonObject = new JSONObject();

        final String archiveDateString = getArchivesArticlesPagedArchive(request.getRequestURI());
        final int currentPageNum = getArchivesArticlesPagedCurrentPageNum(request.getRequestURI());

        Stopwatchs.start("Get Archive-Articles Paged[archive=" + archiveDateString + ", pageNum=" + currentPageNum + ']');

        try {
            jsonObject.put(Keys.STATUS_CODE, true);

            final JSONObject preference = preferenceQueryService.getPreference();
            final int pageSize = preference.getInt(Preference.ARTICLE_LIST_DISPLAY_COUNT);

            final JSONObject archiveQueryResult = archiveDateQueryService.getByArchiveDateString(archiveDateString);
            if (null == archiveQueryResult) {
                throw new Exception("Can not foud archive[archiveDate=" + archiveDateString + "]");
            }

            final JSONObject archiveDate = archiveQueryResult.getJSONObject(ArchiveDate.ARCHIVE_DATE);
            final String archiveDateId = archiveDate.getString(Keys.OBJECT_ID);

            final int articleCount = archiveDate.getInt(ArchiveDate.ARCHIVE_DATE_PUBLISHED_ARTICLE_COUNT);
            final int pageCount = (int) Math.ceil((double) articleCount / (double) pageSize);

            final List<JSONObject> articles = articleQueryService.getArticlesByArchiveDate(archiveDateId, currentPageNum, pageSize);

            final boolean hasMultipleUsers = Users.getInstance().hasMultipleUsers();
            if (hasMultipleUsers) {
                filler.setArticlesExProperties(articles, preference);
            } else {
                if (!articles.isEmpty()) {
                    final JSONObject author = articleUtils.getAuthor(articles.get(0));
                    filler.setArticlesExProperties(articles, author, preference);
                }
            }

            final JSONObject result = new JSONObject();
            final JSONObject pagination = new JSONObject();
            pagination.put(Pagination.PAGINATION_PAGE_COUNT, pageCount);
            result.put(Pagination.PAGINATION, pagination);

            result.put(Article.ARTICLES, articles);

            jsonObject.put(Keys.RESULTS, result);
        } catch (final Exception e) {
            jsonObject.put(Keys.STATUS_CODE, false);
            LOGGER.log(Level.SEVERE, "Gets article paged failed", e);
        } finally {
            Stopwatchs.end();
        }

        final JSONRenderer renderer = new JSONRenderer();
        context.setRenderer(renderer);
        renderer.setJSONObject(jsonObject);
    }

    /**
     * Shows author articles with the specified context.
     * 
     * @param context the specified context
     * @param request the specified request
     * @param response the specified response
     * @throws IOException io exception
     * @throws JSONException json exception 
     */
    @RequestProcessing(value = "/authors/**", method = HTTPRequestMethod.GET)
    public void showAuthorArticles(final HTTPRequestContext context, final HttpServletRequest request, final HttpServletResponse response)
            throws IOException, JSONException {
        final AbstractFreeMarkerRenderer renderer = new FrontRenderer();
        context.setRenderer(renderer);

        renderer.setTemplateName("author-articles.ftl");

        try {
            String requestURI = request.getRequestURI();
            if (!requestURI.endsWith("/")) {
                requestURI += "/";
            }

            final String authorId = getAuthorId(requestURI);

            LOGGER.log(Level.FINER, "Request author articles[requestURI={0}, authorId={1}]",
                       new Object[]{requestURI, authorId});

            final int currentPageNum = getAuthorCurrentPageNum(requestURI, authorId);
            if (-1 == currentPageNum) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            LOGGER.log(Level.FINER, "Request author articles[authorId={0}, currentPageNum={1}]",
                       new Object[]{authorId, currentPageNum});

            final JSONObject preference = preferenceQueryService.getPreference();
            if (null == preference) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            final int pageSize = preference.getInt(Preference.ARTICLE_LIST_DISPLAY_COUNT);
            final int windowSize = preference.getInt(Preference.ARTICLE_LIST_PAGINATION_WINDOW_SIZE);

            final JSONObject result = userQueryService.getUser(authorId);
            final JSONObject author = result.getJSONObject(User.USER);

            final Map<String, String> langs = langPropsService.getAll(Juggs.getLocale());
            request.setAttribute(PageCaches.CACHED_TYPE, langs.get(PageTypes.AUTHOR_ARTICLES));
            request.setAttribute(PageCaches.CACHED_OID, "No id");
            request.setAttribute(PageCaches.CACHED_TITLE,
                                 langs.get(PageTypes.AUTHOR_ARTICLES) + "  ["
                                 + langs.get("pageNumLabel") + "=" + currentPageNum + ", "
                                 + langs.get("authorLabel") + "=" + author.getString(User.USER_NAME) + "]");
            request.setAttribute(PageCaches.CACHED_LINK, requestURI);

            final String authorEmail = author.getString(User.USER_EMAIL);
            final List<JSONObject> articles = articleQueryService.getArticlesByAuthorEmail(authorEmail, currentPageNum, pageSize);
            if (articles.isEmpty()) {
                try {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                } catch (final IOException ex) {
                    LOGGER.severe(ex.getMessage());
                }
            }

            filler.setArticlesExProperties(articles, author, preference);

            if (preference.optBoolean(Preference.ENABLE_ARTICLE_UPDATE_HINT)) {
                Collections.sort(articles, Comparators.ARTICLE_UPDATE_DATE_COMPARATOR);
            } else {
                Collections.sort(articles, Comparators.ARTICLE_CREATE_DATE_COMPARATOR);
            }

            final int articleCount = author.getInt(UserExt.USER_PUBLISHED_ARTICLE_COUNT);
            final int pageCount = (int) Math.ceil((double) articleCount / (double) pageSize);

            final List<Integer> pageNums = Paginator.paginate(currentPageNum, pageSize, pageCount, windowSize);

            final Map<String, Object> dataModel = renderer.getDataModel();
            prepareShowAuthorArticles(pageNums, dataModel, pageCount, currentPageNum, articles, author, preference);
            filler.fillBlogHeader(request, dataModel, preference);
            filler.fillSide(request, dataModel, preference);
            Skins.fillSkinLangs(preference.optString(Preference.LOCALE_STRING),
                                (String) request.getAttribute(Keys.TEMAPLTE_DIR_NAME), dataModel);
        } catch (final ServiceException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            try {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            } catch (final IOException ex) {
                LOGGER.severe(ex.getMessage());
            }
        }
    }

    /**
     * Shows archive articles with the specified context.
     * 
     * @param context the specified context
     * @param request the specified request
     * @param response the specified response 
     */
    @RequestProcessing(value = "/archives/**", method = HTTPRequestMethod.GET)
    public void showArchiveArticles(final HTTPRequestContext context,
                                    final HttpServletRequest request, final HttpServletResponse response) {
        final AbstractFreeMarkerRenderer renderer = new FrontRenderer();
        context.setRenderer(renderer);

        renderer.setTemplateName("archive-articles.ftl");

        try {
            String requestURI = request.getRequestURI();
            if (!requestURI.endsWith("/")) {
                requestURI += "/";
            }

            final String archiveDateString = getArchiveDate(requestURI);
            final int currentPageNum = getArchiveCurrentPageNum(requestURI);
            if (-1 == currentPageNum) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            LOGGER.log(Level.FINER, "Request archive date[string={0}, currentPageNum={1}]",
                       new Object[]{archiveDateString, currentPageNum});
            final JSONObject result = archiveDateQueryService.getByArchiveDateString(archiveDateString);
            if (null == result) {
                LOGGER.log(Level.WARNING, "Can not find articles for the specified archive date[string={0}]", archiveDateString);
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            final JSONObject archiveDate = result.getJSONObject(ArchiveDate.ARCHIVE_DATE);
            final String archiveDateId = archiveDate.getString(Keys.OBJECT_ID);

            final JSONObject preference = preferenceQueryService.getPreference();
            final int pageSize = preference.getInt(Preference.ARTICLE_LIST_DISPLAY_COUNT);

            final int articleCount = archiveDate.getInt(ArchiveDate.ARCHIVE_DATE_PUBLISHED_ARTICLE_COUNT);
            final int pageCount = (int) Math.ceil((double) articleCount / (double) pageSize);

            final List<JSONObject> articles = articleQueryService.getArticlesByArchiveDate(archiveDateId, currentPageNum, pageSize);
            if (articles.isEmpty()) {
                try {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                } catch (final IOException ex) {
                    LOGGER.severe(ex.getMessage());
                }
            }

            final boolean hasMultipleUsers = Users.getInstance().hasMultipleUsers();
            if (hasMultipleUsers) {
                filler.setArticlesExProperties(articles, preference);
            } else {
                if (!articles.isEmpty()) {
                    final JSONObject author = articleUtils.getAuthor(articles.get(0));
                    filler.setArticlesExProperties(articles, author, preference);
                }
            }

            sort(preference, articles);

            final Map<String, Object> dataModel = renderer.getDataModel();

            Skins.fillSkinLangs(preference.optString(Preference.LOCALE_STRING),
                                (String) request.getAttribute(Keys.TEMAPLTE_DIR_NAME), dataModel);

            final String cachedTitle = prepareShowArchiveArticles(preference, dataModel, articles,
                                                                  currentPageNum,
                                                                  pageCount, archiveDateString,
                                                                  archiveDate);

            filler.fillBlogHeader(request, dataModel, preference);
            filler.fillSide(request, dataModel, preference);

            final Map<String, String> langs = langPropsService.getAll(Juggs.getLocale());
            request.setAttribute(PageCaches.CACHED_TYPE, langs.get(PageTypes.DATE_ARTICLES));
            request.setAttribute(PageCaches.CACHED_OID, archiveDateId);
            request.setAttribute(PageCaches.CACHED_TITLE, cachedTitle + "  [" + langs.get("pageNumLabel") + "=" + currentPageNum + "]");
            request.setAttribute(PageCaches.CACHED_LINK, requestURI);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            try {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            } catch (final IOException ex) {
                LOGGER.severe(ex.getMessage());
            }
        }
    }

    /**
     * Updates article random double value.
     * 
     * @param request the specified request
     */
    @RequestProcessing(value = "/article-random-double-gen.do", method = HTTPRequestMethod.GET)
    public void updateArticlesRandomValue(final HttpServletRequest request) {
        // Commented for issue 308, see http://code.google.com/p/b3log-solo/issues/detail?id=308#c4 and 
        // cron.xml for more details.
//        int updateCnt = DEFAULT_UPDATE_CNT;
//        try {
//            updateCnt =
//                    Integer.valueOf(request.getParameter("cnt"));
//        } catch (final NumberFormatException e) {
//            LOGGER.log(Level.WARNING, e.getMessage(), e);
//        }
//
//        try {
//            articleMgmtService.updateArticlesRandomValue(updateCnt);
//        } catch (final ServiceException e) {
//            LOGGER.log(Level.SEVERE, "Updates articles random values failed", e);
//        }
    }

    /**
     * Shows an article with the specified context.
     * 
     * @param context the specified context
     * @param request the specified HTTP servlet request
     * @param response the specified HTTP servlet response
     * @throws IOException io exception 
     */
    @RequestProcessing(value = "/article", method = HTTPRequestMethod.GET)
    public void showArticle(final HTTPRequestContext context, final HttpServletRequest request, final HttpServletResponse response)
            throws IOException {
        // See PermalinkFiler#dispatchToArticleOrPageProcessor()
        final JSONObject article = (JSONObject) request.getAttribute(Article.ARTICLE);
        if (null == article) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        final String articleId = article.optString(Keys.OBJECT_ID);
        LOGGER.log(Level.FINER, "Article[id={0}]", articleId);
        final AbstractFreeMarkerRenderer renderer = new FrontRenderer();
        context.setRenderer(renderer);
        renderer.setTemplateName("article.ftl");

        try {
            final JSONObject preference = preferenceQueryService.getPreference();

            final boolean allowVisitDraftViaPermalink = preference.getBoolean(Preference.ALLOW_VISIT_DRAFT_VIA_PERMALINK);
            if (!article.optBoolean(Article.ARTICLE_IS_PUBLISHED) && !allowVisitDraftViaPermalink) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);

                return;
            }

            LOGGER.log(Level.FINEST, "Article[title={0}]", article.getString(Article.ARTICLE_TITLE));

            articleQueryService.markdown(article);

            final Map<String, String> langs = langPropsService.getAll(Juggs.getLocale());

            request.setAttribute(PageCaches.CACHED_TYPE, langs.get(PageTypes.ARTICLE));
            request.setAttribute(PageCaches.CACHED_OID, articleId);
            request.setAttribute(PageCaches.CACHED_TITLE, article.getString(Article.ARTICLE_TITLE));
            request.setAttribute(PageCaches.CACHED_LINK, article.getString(Article.ARTICLE_PERMALINK));
            request.setAttribute(PageCaches.CACHED_PWD, article.optString(Article.ARTICLE_VIEW_PWD));

            // For <meta name="description" content="${article.articleAbstract}"/>
            final String metaDescription = Jsoup.parse(article.optString(Article.ARTICLE_ABSTRACT)).text();
            article.put(Article.ARTICLE_ABSTRACT, metaDescription);

            if (preference.getBoolean(Preference.ENABLE_ARTICLE_UPDATE_HINT)) {
                article.put(Common.HAS_UPDATED, articleUtils.hasUpdated(article));
            } else {
                article.put(Common.HAS_UPDATED, false);
            }

            final JSONObject author = articleUtils.getAuthor(article);
            final String authorName = author.getString(User.USER_NAME);
            article.put(Common.AUTHOR_NAME, authorName);
            final String authorId = author.getString(Keys.OBJECT_ID);
            article.put(Common.AUTHOR_ID, authorId);
            article.put(Common.AUTHOR_ROLE, author.getString(User.USER_ROLE));

            final Map<String, Object> dataModel = renderer.getDataModel();

            prepareShowArticle(preference, dataModel, article);

            filler.fillBlogHeader(request, dataModel, preference);
            filler.fillSide(request, dataModel, preference);
            Skins.fillSkinLangs(preference.optString(Preference.LOCALE_STRING),
                                (String) request.getAttribute(Keys.TEMAPLTE_DIR_NAME), dataModel);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            try {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            } catch (final IOException ex) {
                LOGGER.severe(ex.getMessage());
            }
        }
    }

    /**
     * Sorts the specified articles by the specified preference.
     * 
     * @param preference the specified preference
     * @param articles the specified articles
     * @throws JSONException json exception
     * @see Comparators#ARTICLE_UPDATE_DATE_COMPARATOR
     * @see Comparators#ARTICLE_CREATE_DATE_COMPARATOR
     */
    private void sort(final JSONObject preference, final List<JSONObject> articles) throws JSONException {
        if (preference.getBoolean(Preference.ENABLE_ARTICLE_UPDATE_HINT)) {
            Collections.sort(articles, Comparators.ARTICLE_UPDATE_DATE_COMPARATOR);
        } else {
            Collections.sort(articles, Comparators.ARTICLE_CREATE_DATE_COMPARATOR);
        }
    }

    /**
     * Gets archive date from the specified URI.
     * 
     * @param requestURI the specified request URI
     * @return archive date
     */
    private static String getArchiveDate(final String requestURI) {
        final String path = requestURI.substring((Juggs.getContextPath() + "/archives/").length());

        return path.substring(0, "yyyy/MM".length());
    }

    /**
     * Gets the request page number from the specified request URI.
     * 
     * @param requestURI the specified request URI
     * @return page number, returns {@code -1} if the specified request URI
     * can not convert to an number
     */
    private static int getArchiveCurrentPageNum(final String requestURI) {
        final String pageNumString = requestURI.substring((Juggs.getContextPath() + "/archives/yyyy/MM/").length());

        return Requests.getCurrentPageNum(pageNumString);
    }

    /**
     * Gets author id from the specified URI.
     * 
     * @param requestURI the specified request URI
     * @return author id
     */
    private static String getAuthorId(final String requestURI) {
        final String path = requestURI.substring((Juggs.getContextPath() + "/authors/").length());

        final int idx = path.indexOf("/");
        if (-1 == idx) {
            return path.substring(0);
        } else {
            return path.substring(0, idx);
        }
    }

    /**
     * Gets the request page number from the specified request URI.
     * 
     * @param requestURI the specified request URI
     * @return page number
     */
    private static int getArticlesPagedCurrentPageNum(final String requestURI) {
        final String pageNumString = requestURI.substring((Juggs.getContextPath() + "/articles/").length());

        return Requests.getCurrentPageNum(pageNumString);
    }

    /**
     * Gets the request page number from the specified request URI.
     * 
     * @param requestURI the specified request URI
     * @return page number
     */
    private static int getTagArticlesPagedCurrentPageNum(final String requestURI) {
        return Requests.getCurrentPageNum(StringUtils.substringAfterLast(requestURI, "/"));
    }

    /**
     * Gets the request tag from the specified request URI.
     * 
     * @param requestURI the specified request URI
     * @return tag
     */
    private static String getTagArticlesPagedTag(final String requestURI) {
        String tagAndPageNum = requestURI.substring((Juggs.getContextPath() + "/articles/tags/").length());

        if (!tagAndPageNum.endsWith("/")) {
            tagAndPageNum += "/";
        }

        return StringUtils.substringBefore(tagAndPageNum, "/");
    }

    /**
     * Gets the request page number from the specified request URI.
     * 
     * @param requestURI the specified request URI
     * @return page number
     */
    private static int getArchivesArticlesPagedCurrentPageNum(final String requestURI) {
        return Requests.getCurrentPageNum(StringUtils.substringAfterLast(requestURI, "/"));
    }

    /**
     * Gets the request archive from the specified request URI.
     * 
     * @param requestURI the specified request URI
     * @return archive, for example "2012/05"
     */
    private static String getArchivesArticlesPagedArchive(final String requestURI) {
        String archiveAndPageNum = requestURI.substring((Juggs.getContextPath() + "/articles/archives/").length());

        if (!archiveAndPageNum.endsWith("/")) {
            archiveAndPageNum += "/";
        }

        return StringUtils.substringBeforeLast(archiveAndPageNum, "/");
    }

    /**
     * Gets the request page number from the specified request URI and author id.
     * 
     * @param requestURI the specified request URI
     * @param authorId the specified author id
     * @return page number
     */
    private static int getAuthorCurrentPageNum(final String requestURI, final String authorId) {
        final String pageNumString = requestURI.substring((Juggs.getContextPath() + "/authors/" + authorId + "/").length());

        return Requests.getCurrentPageNum(pageNumString);
    }

    /**
     * Gets the random articles.
     *
     * @param preference the specified preference
     * @return a list of articles, returns an empty list if not found
     */
    private List<JSONObject> getRandomArticles(final JSONObject preference) {
        try {
            final int displayCnt = preference.getInt(Preference.RANDOM_ARTICLES_DISPLAY_CNT);
            final List<JSONObject> ret = articleQueryService.getArticlesRandomly(displayCnt);

            return ret;
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            return Collections.emptyList();
        }
    }

    /**
     * Prepares the specified data model for rendering author articles.
     * 
     * @param pageNums the specified page numbers
     * @param dataModel the specified data model
     * @param pageCount the specified page count
     * @param currentPageNum the specified  current page number 
     * @param articles the specified articles
     * @param author the specified author
     * @param preference the specified preference
     * @throws ServiceException service exception
     */
    private void prepareShowAuthorArticles(final List<Integer> pageNums,
                                           final Map<String, Object> dataModel,
                                           final int pageCount,
                                           final int currentPageNum,
                                           final List<JSONObject> articles,
                                           final JSONObject author,
                                           final JSONObject preference) throws ServiceException {
        if (0 != pageNums.size()) {
            dataModel.put(Pagination.PAGINATION_FIRST_PAGE_NUM, pageNums.get(0));
            dataModel.put(Pagination.PAGINATION_LAST_PAGE_NUM, pageNums.get(pageNums.size() - 1));
        }
        dataModel.put(Pagination.PAGINATION_PAGE_COUNT, pageCount);
        dataModel.put(Pagination.PAGINATION_PAGE_NUMS, pageNums);

        dataModel.put(Pagination.PAGINATION_CURRENT_PAGE_NUM, currentPageNum);
        final String previousPageNum = Integer.toString(currentPageNum > 1 ? currentPageNum - 1 : 0);
        dataModel.put(Pagination.PAGINATION_PREVIOUS_PAGE_NUM, "0".equals(previousPageNum) ? "" : previousPageNum);
        if (pageCount == currentPageNum + 1) { // The next page is the last page
            dataModel.put(Pagination.PAGINATION_NEXT_PAGE_NUM, "");
        } else {
            dataModel.put(Pagination.PAGINATION_NEXT_PAGE_NUM, currentPageNum + 1);
        }

        dataModel.put(Article.ARTICLES, articles);
        final String authorId = author.optString(Keys.OBJECT_ID);
        dataModel.put(Common.PATH, "/authors/" + authorId);
        dataModel.put(Keys.OBJECT_ID, authorId);

        dataModel.put(Common.AUTHOR_NAME, author.optString(User.USER_NAME));
        dataModel.put(Pagination.PAGINATION_CURRENT_PAGE_NUM, currentPageNum);

        filler.fillBlogFooter(dataModel, preference);
    }

    /**
     * Prepares the specified data model for rendering archive articles.
     * 
     * @param preference the specified preference
     * @param dataModel the specified data model
     * @param articles the specified articles
     * @param currentPageNum the specified current page number
     * @param pageCount the specified page count
     * @param archiveDateString the specified archive data string
     * @param archiveDate the specified archive date
     * @return page title for caching
     * @throws Exception  exception
     */
    private String prepareShowArchiveArticles(final JSONObject preference,
                                              final Map<String, Object> dataModel,
                                              final List<JSONObject> articles,
                                              final int currentPageNum,
                                              final int pageCount,
                                              final String archiveDateString,
                                              final JSONObject archiveDate) throws Exception {
        final int pageSize = preference.getInt(Preference.ARTICLE_LIST_DISPLAY_COUNT);
        final int windowSize = preference.getInt(Preference.ARTICLE_LIST_PAGINATION_WINDOW_SIZE);

        final List<Integer> pageNums = Paginator.paginate(currentPageNum, pageSize, pageCount, windowSize);

        dataModel.put(Article.ARTICLES, articles);
        final String previousPageNum = Integer.toString(currentPageNum > 1 ? currentPageNum - 1 : 0);
        dataModel.put(Pagination.PAGINATION_PREVIOUS_PAGE_NUM, "0".equals(previousPageNum) ? "" : previousPageNum);
        if (pageCount == currentPageNum + 1) { // The next page is the last page
            dataModel.put(Pagination.PAGINATION_NEXT_PAGE_NUM, "");
        } else {
            dataModel.put(Pagination.PAGINATION_NEXT_PAGE_NUM, currentPageNum + 1);
        }
        dataModel.put(Pagination.PAGINATION_CURRENT_PAGE_NUM, currentPageNum);
        dataModel.put(Pagination.PAGINATION_FIRST_PAGE_NUM, pageNums.get(0));
        dataModel.put(Pagination.PAGINATION_LAST_PAGE_NUM, pageNums.get(pageNums.size() - 1));
        dataModel.put(Pagination.PAGINATION_PAGE_COUNT, pageCount);
        dataModel.put(Pagination.PAGINATION_PAGE_NUMS, pageNums);
        dataModel.put(Common.PATH, "/archives/" + archiveDateString);
        dataModel.put(Keys.OBJECT_ID, archiveDate.getString(Keys.OBJECT_ID));

        filler.fillBlogFooter(dataModel, preference);
        final long time = archiveDate.getLong(ArchiveDate.ARCHIVE_TIME);
        final String dateString = ArchiveDate.DATE_FORMAT.format(time);
        final String[] dateStrings = dateString.split("/");
        final String year = dateStrings[0];
        final String month = dateStrings[1];
        archiveDate.put(ArchiveDate.ARCHIVE_DATE_YEAR, year);
        final String language = Locales.getLanguage(preference.getString(Preference.LOCALE_STRING));
        String ret;
        if ("en".equals(language)) {
            archiveDate.put(ArchiveDate.ARCHIVE_DATE_MONTH, Dates.EN_MONTHS.get(month));
            ret = Dates.EN_MONTHS.get(month) + " " + year;
        } else {
            archiveDate.put(ArchiveDate.ARCHIVE_DATE_MONTH, month);
            ret = year + " " + dataModel.get("yearLabel") + " " + month + " " + dataModel.get("monthLabel");
        }
        dataModel.put(ArchiveDate.ARCHIVE_DATE, archiveDate);

        return ret;
    }

    /**
     * Prepares the specified data model for rendering article.
     * 
     * @param preference the specified preference
     * @param dataModel the specified data model
     * @param article the specified article
     * @throws Exception exception
     */
    private void prepareShowArticle(final JSONObject preference, final Map<String, Object> dataModel, final JSONObject article)
            throws Exception {
        article.put(Common.COMMENTABLE, article.getBoolean(Article.ARTICLE_COMMENTABLE));
        article.put(Common.PERMALINK, article.getString(Article.ARTICLE_PERMALINK));
        dataModel.put(Article.ARTICLE, article);
        final String articleId = article.getString(Keys.OBJECT_ID);

        Stopwatchs.start("Get Article Sign");
        LOGGER.finer("Getting article sign....");
        article.put(Common.ARTICLE_SIGN, articleUtils.getSign(article.getString(Article.ARTICLE_SIGN_ID), preference));
        LOGGER.finer("Got article sign");
        Stopwatchs.end();

        Stopwatchs.start("Get Next Article");
        LOGGER.finer("Getting the next article....");
        final JSONObject nextArticle = articleQueryService.getNextArticle(articleId);
        if (null != nextArticle) {
            dataModel.put(Common.NEXT_ARTICLE_PERMALINK, nextArticle.getString(Article.ARTICLE_PERMALINK));
            dataModel.put(Common.NEXT_ARTICLE_TITLE, nextArticle.getString(Article.ARTICLE_TITLE));
            LOGGER.finer("Got the next article");
        }
        Stopwatchs.end();

        Stopwatchs.start("Get Previous Article");
        LOGGER.finer("Getting the previous article....");
        final JSONObject previousArticle = articleQueryService.getPreviousArticle(articleId);
        if (null != previousArticle) {
            dataModel.put(Common.PREVIOUS_ARTICLE_PERMALINK, previousArticle.getString(Article.ARTICLE_PERMALINK));
            dataModel.put(Common.PREVIOUS_ARTICLE_TITLE, previousArticle.getString(Article.ARTICLE_TITLE));
            LOGGER.finer("Got the previous article");
        }
        Stopwatchs.end();

        Stopwatchs.start("Get Article CMTs");
        LOGGER.finer("Getting article's comments....");
        final int cmtCount = article.getInt(Article.ARTICLE_COMMENT_COUNT);
        if (0 != cmtCount) {
            final List<JSONObject> articleComments = commentQueryService.getComments(articleId);
            dataModel.put(Article.ARTICLE_COMMENTS_REF, articleComments);
        } else {
            dataModel.put(Article.ARTICLE_COMMENTS_REF, Collections.emptyList());
        }
        LOGGER.finer("Got article's comments");
        Stopwatchs.end();

        dataModel.put(Preference.EXTERNAL_RELEVANT_ARTICLES_DISPLAY_CNT,
                      preference.getInt(Preference.EXTERNAL_RELEVANT_ARTICLES_DISPLAY_CNT));
        dataModel.put(Preference.RANDOM_ARTICLES_DISPLAY_CNT, preference.getInt(Preference.RANDOM_ARTICLES_DISPLAY_CNT));
        dataModel.put(Preference.RELEVANT_ARTICLES_DISPLAY_CNT, preference.getInt(Preference.RELEVANT_ARTICLES_DISPLAY_CNT));

        filler.fillBlogFooter(dataModel, preference);
    }
}
