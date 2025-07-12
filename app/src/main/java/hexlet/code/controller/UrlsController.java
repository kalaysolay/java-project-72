package hexlet.code.controller;

import hexlet.code.dto.UrlPage;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.utils.NamedRoutes;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import kong.unirest.Unirest;
import org.jsoup.Jsoup;
import java.sql.SQLException;
import static io.javalin.rendering.template.TemplateUtil.model;


public class UrlsController {
    public static void create(Context ctx) throws SQLException {
        var id = ctx.pathParamAsClass("id", Long.class).get();
        var url = UrlRepository.find(id)
                .orElseThrow(() -> new NotFoundResponse("Site not found"));
        try {
            var response = Unirest.get(url.getName())
                    .asString();
            int code = response.getStatus();

            var urlCheck = UrlCheck.builder()
                    .urlId(id)
                    .statusCode(code);

            var body = response.getBody();
            var doc = Jsoup.parse(body);
            urlCheck.title(doc.title());
            var tagH1 = doc.selectFirst("h1");
            if (tagH1 != null) {
                urlCheck.h1(doc.selectFirst("h1").text());
            }
            var tag = doc.selectFirst("meta[name=description]");
            if (tag != null) {
                urlCheck.description(tag.attribute("content").getValue());
            }
            var urlCheckObj = urlCheck.build();
            UrlCheckRepository.save(urlCheckObj);

            System.out.println("URLS_CONTROLLER LOG> H1 : " + urlCheckObj.getH1()
                    + " | Title : " + urlCheckObj.getTitle()
                    + " | Description : " + urlCheckObj.getDescription() + " | Status Code : "
                    + urlCheckObj.getStatusCode());
            var urlChecks = UrlCheckRepository.getEntityDetails(id);
            var page = new UrlPage(url, urlChecks);
            ctx.render("urls/show.jte", model("page", page));
        } catch (Exception e) {
            ctx.sessionAttribute("flash", "Incorrect URL or the site is not available");
            ctx.sessionAttribute("flashType", "warning");
            ctx.redirect(NamedRoutes.urlPath(id));
        }
    }
}

