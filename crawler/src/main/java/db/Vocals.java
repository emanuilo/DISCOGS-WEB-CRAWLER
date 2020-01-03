package db;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "vocals")
public class Vocals {
    @EmbeddedId
    Vocals_Id vocals_id;

    @Embeddable
    public static class Vocals_Id implements Serializable{
        @ManyToOne
        Album album;

        @ManyToOne
        Artist artist;

        public Vocals_Id(Album album, Artist artist) {
            this.album = album;
            this.artist = artist;
        }
    }

    public Vocals() {
    }

    public Vocals(Vocals_Id vocals_id) {
        this.vocals_id = vocals_id;
    }
}
