package db;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

public class Artist {

    @Id
    @GeneratedValue
    private Long id;

    private String name;


}
