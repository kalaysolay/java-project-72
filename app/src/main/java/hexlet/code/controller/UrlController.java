package hexlet.code.controller;
import hexlet.code.dto.MainPage;
import hexlet.code.dto.UrlPage;
import hexlet.code.dto.UrlsPage;

import hexlet.code.utils.NamedRoutes;
import hexlet.code.model.Url;
import hexlet.code.repository.UrlRepository;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collections;

import static io.javalin.rendering.template.TemplateUtil.model;


public class UrlController {

    public static void indexStart(Context ctx) throws SQLException {
        var page = new MainPage();
        ctx.render("index.jte", Collections.singletonMap("page", page));
    }

    public static void show(Context ctx) throws SQLException {
        var id = ctx.pathParamAsClass("id", Long.class).get();
        var url = UrlRepository.find(id)
                .orElseThrow(() -> new NotFoundResponse("Url with idd = " + id + " not found"));
        var page = new UrlPage(url);
        ctx.render("url/url.jte", model("page", page));
    }

    public static void create(Context ctx) throws SQLException, URISyntaxException, IOException {
        var urlString = ctx.formParam("url");
        var uri = new URI(urlString);
        var url = uri.toURL();
        var authority = uri.getAuthority();
        var protocol = url.getProtocol();
        var urlSave = new Url(protocol + "://" + authority, new Timestamp(System.currentTimeMillis()));
        UrlRepository.save(urlSave);
        ctx.redirect(NamedRoutes.urlsPath());
    }

    public static void index(Context ctx) throws SQLException {
        var urls = UrlRepository.getEntities();
        var page = new UrlsPage(urls);
        ctx.render("urls/index.jte", model("page", page));
    }
}
