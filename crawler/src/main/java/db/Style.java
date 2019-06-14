package db;

import javax.persistence.*;

@Entity
@Table(name = "style", schema = "crawler_db")
public class Style {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    private Album album;

    private String name;
}
