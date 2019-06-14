package db;

import javax.persistence.*;

@Entity
@Table(name = "track", schema = "crawler_db")
public class Track {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @ManyToOne
    private Album album;
}
