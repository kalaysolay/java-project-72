package hexlet.code.model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class Url {
    private long  id;
    private String name;
    private Timestamp createdAt;

    public Url(String name) {
        this.name = name;
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    public Url(String name, Timestamp timestamp) {
        this.name = name;
        this.createdAt = timestamp;
    }
}
