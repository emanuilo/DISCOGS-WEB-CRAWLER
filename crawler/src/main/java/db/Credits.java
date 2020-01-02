package db;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "credits")
public class Credits {
    @EmbeddedId
    Credits_Id credits_id;

    @Embeddable
    public static class Credits_Id implements Serializable {
        @ManyToOne
        Album album;

        @ManyToOne
        Artist artist;

        public Credits_Id(Album album, Artist artist) {
            this.album = album;
            this.artist = artist;
        }
    }

    public Credits() {
    }

    public Credits(Credits_Id credits_id) {
        this.credits_id = credits_id;
    }
}
