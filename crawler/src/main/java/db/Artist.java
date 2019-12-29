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

    public Artist(String name, String website) {
        this.name = name;
        this.website = website;
    }

    public String getName() {
        return name;
    }
}
