package db;

import javax.persistence.*;

@Entity
@Table(name = "style")
public class Style {
    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    private Album album;

    private String name;
}
