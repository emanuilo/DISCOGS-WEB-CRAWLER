package db;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "album_style")
public class Album_Style {
    @EmbeddedId
    Album_Style_Id album_style_id;

    @Embeddable
    public static class Album_Style_Id implements Serializable{
        @ManyToOne
        Style style;

        @ManyToOne
        Album album;

        public Album_Style_Id(Style style, Album album){
            this.style = style;
            this.album = album;
        }
    }

    public Album_Style(Album_Style_Id album_style_id){
        this.album_style_id = album_style_id;
    }
}
