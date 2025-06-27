package hexlet.code.model;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class Url {
    private int id;
    private String name;
    private LocalDateTime created_at;

    public Url(String name) {
        this.name = name;
        this.created_at = LocalDateTime.now();
    }
}