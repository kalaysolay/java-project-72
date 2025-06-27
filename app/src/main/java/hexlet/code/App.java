package hexlet.code;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.stream.Collectors;

import com.zaxxer.hikari.HikariDataSource;
import hexlet.code.repository.BaseRepository;
import io.javalin.Javalin;
import com.zaxxer.hikari.HikariConfig;

public class App {

    public static void main(String[] args) throws SQLException, IOException {
        var app = getApp()
                .get("/", ctx -> ctx.result("Hello World"))
                .start(getPort());
    }


    public static Javalin getApp() throws SQLException, IOException {
        var hikariConfig = new HikariConfig();
        //Инициализация базы данных
        hikariConfig.setJdbcUrl("jdbc:h2:mem:project;DB_CLOSE_DELAY=-1;");
        var dataSource = new HikariDataSource(hikariConfig);
        var url = App.class.getClassLoader().getResourceAsStream("mainschema.sql");

        //Чтение SQL-скрипта
        var sql = new BufferedReader(new InputStreamReader(url))
                .lines().collect(Collectors.joining("\n"));


        try (var connection = dataSource.getConnection();
                var statement = connection.createStatement()) {
            statement.execute(sql);
        }
        BaseRepository.dataSource = dataSource;

        var app = Javalin.create(config -> {
            config.bundledPlugins.enableDevLogging();
        });
        //app.start(getPort());
        return app;
    }

    private static String readResourceFile(String fileName) throws IOException {
        var inputStream = App.class.getClassLoader().getResourceAsStream(fileName);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }
    private static int getPort() {
        String port = System.getenv().getOrDefault("PORT", "7070");
        return Integer.valueOf(port);
    }

}
