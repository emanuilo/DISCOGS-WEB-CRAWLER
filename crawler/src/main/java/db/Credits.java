package db;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "credits")
public class Credits {
    @EmbeddedId
    Credits_Id credits_id;

    class Credits_Id implements Serializable {
        @ManyToOne
        Album album;

        @ManyToOne
        Artist artist;
    }
}
