package hexlet.code;

import com.zaxxer.hikari.HikariDataSource;
import hexlet.code.model.Url;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.utils.AnalyserUtils;
import hexlet.code.utils.NamedRoutes;
import io.javalin.Javalin;
import io.javalin.http.HttpStatus;
import io.javalin.testtools.JavalinTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Assertions;

import static hexlet.code.repository.BaseRepository.dataSource;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


public class AppTest {
    private Javalin app;
    private static MockWebServer mockServer;

    /**
     * Initializes the MockWebServer and enqueues a mock response for testing.
     * This method is called once before all tests in this class.
     *
     * @throws Exception if an error occurs while starting the server or reading the fixture file
     */
    @BeforeAll
    public static void beforeAll() throws Exception {
        mockServer = new MockWebServer();
        var body = getFixtureContent("pageForTest.html");
        MockResponse mockResponse = new MockResponse().setResponseCode(HttpStatus.OK.getCode()).setBody(body);
        mockServer.enqueue(mockResponse);
        mockServer.start();
        var url = removeLastChar(mockServer.url("/").toString());

    }

    /**
     * Reads the content of a fixture file from the resources directory.
     * @param filename the name of the fixture file to read
     * @return the content of the fixture file as a String
     * @throws IOException if an error occurs while reading the file
     */
    private static String getFixtureContent(String filename) throws IOException {
        Path path = Paths.get("src/test/resources/fixtures/" + filename).toAbsolutePath().normalize();
        return Files.readString(path);
    }

    public static String removeLastChar(String s) {
        return (s == null || s.isEmpty())
                ? null
                : (s.substring(0, s.length() - 1));
    }

    @AfterAll
    public static void afterAll() throws IOException {
        mockServer.shutdown();
    }


    @BeforeEach
    public final void startApp() throws SQLException, IOException {
        app = App.getApp();
    }

    @Test
    public void testMainPage() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get(NamedRoutes.mainPagePath());
            assertThat(response.code()).isEqualTo(200);
            Assertions.assertNotNull(response.body());
            assertThat(response.body().string()).contains("Анализатор страниц");
        });
    }


    @Test
    public void testUrlsPage() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get(NamedRoutes.urlsPath());
            assertThat(response.code()).isEqualTo(200);
        });
    }

    @Test
    public void testUrlPage() throws SQLException {
        var url = new Url(AnalyserUtils.getVerifyUrl("http://localhost:7070"));
        UrlRepository.save(url);
        UrlRepository.getEntities().forEach(u -> System.out.println(" - " + u.getName()));

        String verifiedUrl = AnalyserUtils.getVerifyUrl("http://localhost:7070");
        JavalinTest.test(app, (server, client) -> {
            var response = client.get(NamedRoutes.urlPath(url.getId()));
            assertThat(response.code()).isEqualTo(200);
            var urlTest = UrlRepository.findByName("http://localhost:7070")
                .orElse(new Url("")).getName();
            System.out.println("URL urLTest: " + urlTest);
            assertThat(urlTest).contains("http://localhost:7070");
        });
    }

    @Test
    public void testCreateUrl() {
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=http://localhost:7070";
            var response = client.post(NamedRoutes.urlsPath(), requestBody);
            assertThat(response.code()).isEqualTo(200);
            Assertions.assertNotNull(response.body());
            assertThat(response.body().string()).contains("http://localhost:7070");
        });
    }

    @Test
    public void testCreateErrorUrl() throws SQLException, IOException {
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=badNameSite";
            var response = client.post(NamedRoutes.urlsPath(), requestBody);
            System.out.println("BODY: " + response.message());
            Assertions.assertNotNull(response.body());
            System.out.println("BODY: " + response.body().string());

            assertThat(response.code()).isEqualTo(200);
            var urls = UrlRepository.findByName("badNameSite");
            assertThat(urls).isEmpty();
        });
    }

    @Test
    void testUrlNotFound() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get(NamedRoutes.urlPath(99999L));
            assertThat(response.code()).isEqualTo(404);
        });
    }

    //тест для обработчика добавления URL, который проверяет сохранение и отображение
    @Test
    void testCreateUrlIntegration() {
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=http://localhost:7070";
            var response = client.post(NamedRoutes.urlsPath(), requestBody);
            assertThat(response.code()).isEqualTo(200);

            // Проверка, что URL добавлен в репозиторий
            var urlOpt = UrlRepository.findByName("http://localhost:7070");
            assertThat(urlOpt).isPresent();

            // Проверка, что URL отображается на странице /urls
            var listResponse = client.get(NamedRoutes.urlsPath());
            assertThat(listResponse.code()).isEqualTo(200);
            Assertions.assertNotNull(listResponse.body());
            assertThat(listResponse.body().string()).contains("http://localhost:7070");
        });
    }

    @Test
    public void testMockRunCheckUrl() throws SQLException, InterruptedException {
        HttpUrl baseUrl = mockServer.url("/");
        JavalinTest.test(app, (server, client) -> {
            System.out.println("baseUrl=" + baseUrl.toString());
            client.post("/urls", "url=".concat(baseUrl.toString()));
            client.post(NamedRoutes.urlCheckPath(1L));
            var url = UrlRepository.findByName(AnalyserUtils.getVerifyUrl(baseUrl.toString()));
            assertThat(url).isNotEmpty();
            var urlCheck = UrlCheckRepository.getUrlLastCheck(1L);
            assertThat(urlCheck.getStatusCode()).isEqualTo(200);
            RecordedRequest request1 = mockServer.takeRequest();
            assertEquals("/", request1.getPath());
        });
    }

    @Test
    void testStore() {

        String inputUrl = "https://ru.hexlet.io";

        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=" + inputUrl;
            assertThat(client.post("/urls", requestBody).code()).isEqualTo(200);

            var response = client.get("/urls");
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string())
                    .contains(inputUrl);

            var actualUrl = getUrlByName(dataSource, inputUrl);
            System.out.println("TESTSTORE ACTUAL URL: " +actualUrl);
            assertThat(actualUrl).isNotNull();
            assertThat(actualUrl.get("name").toString()).isEqualTo(inputUrl);
        });
    }

    public static Map<String, Object> getUrlByName(HikariDataSource dataSource, String url) throws SQLException {
        var result = new HashMap<String, Object>();
        var sql = "SELECT * FROM urls WHERE name = ?";
        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, url);
            var resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                result.put("id", resultSet.getLong("id"));
                result.put("name", resultSet.getString("name"));
                return result;
            }
            return null;
        }
    }
}
