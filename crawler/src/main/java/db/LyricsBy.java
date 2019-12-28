package db;


import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "lyrics_by")
public class LyricsBy {
    @EmbeddedId
    LyricsBy_Id lyricsBy_id;

    @Embeddable
    class LyricsBy_Id implements Serializable {
        @ManyToOne
        Track track;

        @ManyToOne
        Artist artist;
    }
}
