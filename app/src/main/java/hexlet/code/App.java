package hexlet.code;

import java.sql.SQLException;
import io.javalin.Javalin;

public class App {
    public static Javalin getApp() throws SQLException {
        var app = Javalin.create(config -> {
            config.bundledPlugins.enableDevLogging();
        });
        app.start();
        return app;
    }

}
