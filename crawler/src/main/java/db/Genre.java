package db;

import javax.persistence.*;

@Entity
@Table(name = "genre")
public class Genre {
    @Id
    @GeneratedValue
    private Integer id;

    private String name;

    public Genre(){}

    public Genre(String name) {
        this.name = name;
    }
}
