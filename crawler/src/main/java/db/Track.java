package db;

import javax.persistence.*;

@Entity
@Table(name = "track")
public class Track {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private int duration; //seconds

    @ManyToOne
    private Album album;
}
