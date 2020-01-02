package db;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "album_genre")
public class Album_Genre {
    @EmbeddedId
    Album_Genre_Id album_genre_id;

    @Embeddable
    public static class Album_Genre_Id implements Serializable{
        @ManyToOne
        Genre genre;

        @ManyToOne
        Album album;

        public Album_Genre_Id(Genre genre, Album album) {
            this.genre = genre;
            this.album = album;
        }
    }

    public Album_Genre() {
    }

    public Album_Genre(Album_Genre_Id album_genre_id) {
        this.album_genre_id = album_genre_id;
    }
}
