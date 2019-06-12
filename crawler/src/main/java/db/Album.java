package db;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

public class Album {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String country;

    private Integer released;

    private Integer versions;

    private Float rating;

}
