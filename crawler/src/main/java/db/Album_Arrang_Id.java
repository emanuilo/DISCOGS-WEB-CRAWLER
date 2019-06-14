package db;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Embeddable
public class Album_Arrang_Id implements Serializable {
    @ManyToOne
    private Album album;

    @ManyToOne
    private Artist arranger;

    public Album_Arrang_Id() {}

    public Album_Arrang_Id(Album album, Artist arranger){
        this.album = album;
        this.arranger = arranger;
    }
}
