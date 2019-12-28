package db;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "album_genre")
public class Album_Genre {
    @EmbeddedId
    Album_Genre_Id album_genre_id;

    class Album_Genre_Id implements Serializable{
        @ManyToOne
        Genre genre;

        @ManyToOne
        Album album;
    }


}
