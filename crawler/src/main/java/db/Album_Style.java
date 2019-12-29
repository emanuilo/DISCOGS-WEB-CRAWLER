package db;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "album_style")
public class Album_Style {
    @EmbeddedId
    Album_Style_Id album_style_id;

    class Album_Style_Id implements Serializable{
        @ManyToOne
        Style style;

        @ManyToOne
        Album album;
    }
}