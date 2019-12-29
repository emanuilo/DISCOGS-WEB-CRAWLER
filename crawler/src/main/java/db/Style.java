package db;

import javax.persistence.*;

@Entity
@Table(name = "style")
public class Style {
    @Id
    @GeneratedValue
    private Integer id;

    private String name;

    public Style(String name) {
        this.name = name;
    }
}
