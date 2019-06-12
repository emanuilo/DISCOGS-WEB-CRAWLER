package db;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

public class Style {

    @Id
    @GeneratedValue
    private Integer id;

    private String name;
}
