package db;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

public class Track {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
}
