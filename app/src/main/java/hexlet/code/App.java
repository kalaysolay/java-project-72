package hexlet.code;

import java.sql.SQLException;
import io.javalin.Javalin;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class App {

    public static void main(String[] args) throws SQLException {
        var app = getApp()
                .get("/", ctx -> ctx.result("Hello World"))
                .start(getPort());
    }


    public static Javalin getApp() throws SQLException {
        var hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl("jdbc:h2:mem:project;DB_CLOSE_DELAY=-1;");

        var app = Javalin.create(config -> {
            config.bundledPlugins.enableDevLogging();
        });
        app.start();
        return app;
    }

    private static int getPort() {
        String port = System.getenv().getOrDefault("PORT", "7070");
        return Integer.valueOf(port);
    }

}
