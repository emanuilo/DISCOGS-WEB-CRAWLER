package db;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "artist")
public class Artist {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String website;

    public Artist(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
