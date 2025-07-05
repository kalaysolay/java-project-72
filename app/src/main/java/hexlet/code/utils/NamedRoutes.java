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

    public static String urlPath(Long id) {
        return urlPath(String.valueOf(id));
    }
}
