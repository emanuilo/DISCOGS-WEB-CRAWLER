package db;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "album_arrangers", schema = "crawler_db")
public class Album_Arrangers {
    @EmbeddedId
    private Album_Arrang_Id album_arrang_id;
}
