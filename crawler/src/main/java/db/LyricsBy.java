package db;


import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "lyrics_by")
public class LyricsBy {
    @EmbeddedId
    LyricsBy_Id lyricsBy_id;

    @Embeddable
    public static class LyricsBy_Id implements Serializable {
        @ManyToOne
        Track track;

        @ManyToOne
        Artist artist;

        public LyricsBy_Id(Track track, Artist artist) {
            this.track = track;
            this.artist = artist;
        }
    }

    public LyricsBy() { }

    public LyricsBy(LyricsBy_Id lyricsBy_id) {
        this.lyricsBy_id = lyricsBy_id;
    }
}
