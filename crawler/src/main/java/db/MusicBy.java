package db;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "music_by")
public class MusicBy {
    @EmbeddedId
    MusicBy_Id musicBy_id;

    @Embeddable
    class MusicBy_Id implements Serializable {
        @ManyToOne
        Track track;

        @ManyToOne
        Artist artist;
    }
}
