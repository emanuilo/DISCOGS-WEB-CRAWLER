package db;

import javax.persistence.*;

@Entity
@Table(name = "genre", schema = "crawler_db")
public class Genre {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    private Album album;

    private String name;
}
