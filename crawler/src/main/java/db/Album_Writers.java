package db;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "album_writers", schema = "crawler_db")
public class Album_Writers {
    @EmbeddedId
    private Album_Writers_Id album_writers_id;
}
