package db;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "arranged_by")
public class ArrangedBy {
    @EmbeddedId
    ArrangedBy_Id arrangedBy_id;

    @Embeddable
    private class ArrangedBy_Id implements Serializable {
        @ManyToOne
        private Artist artist;

        @ManyToOne
        private Track track;
    }
}
