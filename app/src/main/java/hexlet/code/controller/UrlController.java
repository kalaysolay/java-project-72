package hexlet.code.controller;
import hexlet.code.dto.MainPage;
import hexlet.code.dto.UrlPage;
import hexlet.code.dto.UrlsPage;

import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.utils.AnalyserUtils;
import hexlet.code.utils.NamedRoutes;
import hexlet.code.model.Url;
import hexlet.code.repository.UrlRepository;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;

import io.javalin.validation.ValidationException;
import java.sql.SQLException;
import java.util.HashMap;


import static hexlet.code.repository.UrlCheckRepository.getUrlLastCheck;
import static hexlet.code.utils.AnalyserUtils.getVerifyUrl;
import static io.javalin.rendering.template.TemplateUtil.model;


public class UrlController {

    public static void indexStart(Context ctx) throws SQLException {
        var page = new MainPage(ctx.consumeSessionAttribute("url-value"));
        page.setFlash(ctx.consumeSessionAttribute("flash"));
        page.setFlashType(ctx.consumeSessionAttribute("flash-type"));
        ctx.render("index.jte", model("page", page));
    }

    public static void create(Context ctx) throws SQLException {
        try {
            var urlName = ctx.formParamAsClass("url", String.class)
                    .check(value -> !value.isEmpty(), "URL cannot be empty")
                    .check(AnalyserUtils::checkUrl, "Incorrect URL format")
                    .get();
            var verifyUrl = getVerifyUrl(urlName);
            if (UrlRepository.findByName(verifyUrl).isPresent()) {
                ctx.sessionAttribute("flash", "Site already exists");
                ctx.sessionAttribute("flash-type", "info");
            } else {
                var url = new Url(getVerifyUrl(verifyUrl));
                UrlRepository.save(url);
                ctx.sessionAttribute("flash", "Site added successfully");
                ctx.sessionAttribute("flash-type", "success");
            }
            ctx.consumeSessionAttribute("url-value");
            ctx.redirect(NamedRoutes.urlsPath());
        } catch (ValidationException e) {
            var errorMessage = e.getErrors().entrySet().stream().findFirst()
                    .get().getValue().getFirst().getMessage();
            ctx.sessionAttribute("flash", errorMessage);
            ctx.sessionAttribute("flash-type", "warning");
            ctx.sessionAttribute("url-value", ctx.formParam("url"));
            ctx.redirect(NamedRoutes.mainPagePath());

        }
    }

    /** * Display a list of URLs with their last checks.
     *
     * @param ctx the Javalin context
     * @throws SQLException if there is an error accessing the database
     */
    public static void index(Context ctx) throws SQLException {
        var urls = UrlRepository.getEntities();
        var urlsLastChecks = new HashMap<Long, UrlCheck>();
        urls.forEach(url -> {
            var check = getUrlLastCheck(url.getId());
            if (check != null) {
                urlsLastChecks.put(url.getId(), check);
            }
        });
        var page = new UrlsPage(urls, urlsLastChecks);
        page.setFlash(ctx.consumeSessionAttribute("flash"));
        page.setFlashType(ctx.consumeSessionAttribute("flash-type"));
        ctx.render("urls/index.jte", model("page", page));
    }

    /**
     * Show a single URL page with its details and checks.
     *
     * @param ctx the Javalin context
     * @throws SQLException if there is an error accessing the database
     */
    public static void show(Context ctx) throws SQLException {
        var id = ctx.pathParamAsClass("id", Long.class).get();
        var url = UrlRepository.find(id)
                .orElseThrow(() -> new NotFoundResponse("Site not found"));
        var urlChecks = UrlCheckRepository.getEntityDetails(id);
        var page = new UrlPage(url, urlChecks);
        ctx.render("urls/show.jte", model("page", page));
    }
}
