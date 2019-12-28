package db;

import javax.persistence.*;

@Entity
@Table(name = "genre")
public class Genre {
    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    private Album album;

    private String name;
}
