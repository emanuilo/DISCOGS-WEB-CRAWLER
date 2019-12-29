package db;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "album_artist")
public class Album_Artist {
    @EmbeddedId
    Album_Artist_Id album_artist_id;

    public class Album_Artist_Id implements Serializable{
        @ManyToOne
        Album album;

        @ManyToOne
        Artist artist;
    }
}
