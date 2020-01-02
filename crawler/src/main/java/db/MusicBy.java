package db;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "music_by")
public class MusicBy {
    @EmbeddedId
    MusicBy_Id musicBy_id;

    @Embeddable
    public static class MusicBy_Id implements Serializable {
        @ManyToOne
        Track track;

        @ManyToOne
        Artist artist;

        public MusicBy_Id(Track track, Artist artist) {
            this.track = track;
            this.artist = artist;
        }
    }

    public MusicBy() {
    }

    public MusicBy(MusicBy_Id musicBy_id) {
        this.musicBy_id = musicBy_id;
    }
}
