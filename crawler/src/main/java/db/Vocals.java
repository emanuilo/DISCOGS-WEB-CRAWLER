package db;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "vocals")
public class Vocals {
    @EmbeddedId
    Vocals_Id vocals_id;

    class Vocals_Id implements Serializable{
        @ManyToOne
        Track track;

        @ManyToOne
        Artist artist;
    }
}
