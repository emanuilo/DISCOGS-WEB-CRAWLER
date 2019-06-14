package db;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Embeddable
public class Album_Vocals_Id implements Serializable {
    @ManyToOne
    private Album album;

    @ManyToOne
    private Artist vocal;

    public Album_Vocals_Id() {}

    public Album_Vocals_Id(Album album, Artist vocal){
        this.album = album;
        this.vocal = vocal;
    }
}
