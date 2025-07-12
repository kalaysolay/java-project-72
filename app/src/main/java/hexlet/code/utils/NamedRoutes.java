package hexlet.code.utils;

public class NamedRoutes {

    /**
     * роут главной страницы.
     * @return - роут
     */
    public static String mainPagePath() {
        return "/";
    }

    /**
     * роут страницы со всеми УРЛами.
     * @return - возвращает роут /urls
     */
    public static String urlsPath() {
        return "/urls";
    }

    /**
     * роут страницы с конкретным УРЛ.
     * @param id айди URL
     * @return - возвращает роут /url/id
     */
    public static String urlPath(String id) {
        return "/urls/" + id;
    }

    /**
     * роут страницы с конкретным УРЛом для проверки.
     * @param id айди URL
     * @return - возвращает роут /urls/id/checks
     */
    public static String urlPath(Long id) {
        return urlPath(String.valueOf(id));
    }

    /**
     * роут страницы с конкретным УРЛом для проверки.
     * @param id айди URL
     * @return - возвращает роут /urls/id/checks
     */
    public static String urlCheckPath(Long id) {
        return urlCheckPath(String.valueOf(id));
    }

    public static String urlCheckPath(String id) {
        return urlsPath().concat("/").concat(id).concat("/checks");
    }
}
