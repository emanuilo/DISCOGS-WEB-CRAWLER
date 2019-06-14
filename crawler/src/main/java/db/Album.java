package db;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "album", schema = "crawler_db")
public class Album {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @ManyToOne
    private Artist artist;

    private String country;

    private Date released;

    private Integer versions;

    private Float rating;

}
