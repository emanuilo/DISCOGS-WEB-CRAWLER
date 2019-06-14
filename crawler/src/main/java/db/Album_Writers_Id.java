package db;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Embeddable
public class Album_Writers_Id implements Serializable {
    @ManyToOne
    private Album album;

    @ManyToOne
    private Artist writer;

    public Album_Writers_Id() {}

    public Album_Writers_Id(Album album, Artist writer){
        this.album = album;
        this.writer = writer;
    }
}
