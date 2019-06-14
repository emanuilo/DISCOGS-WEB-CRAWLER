package db;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "album_vocals", schema = "crawler_db")
public class Album_Vocals {
    @EmbeddedId
    private Album_Vocals_Id album_vocals_id;
}
