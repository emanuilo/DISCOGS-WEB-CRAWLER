package db;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "album_artist")
public class Album_Artist {
    @EmbeddedId
    Album_Artist_Id album_artist_id;

    @Embeddable
    public static class Album_Artist_Id implements Serializable{
        @ManyToOne
        Album album;

        @ManyToOne
        Artist artist;

        public Album_Artist_Id(Album album, Artist artist) {
            this.album = album;
            this.artist = artist;
        }
    }

    public Album_Artist() { }

    public Album_Artist(Album_Artist_Id album_artist_id) {
        this.album_artist_id = album_artist_id;
    }
}
