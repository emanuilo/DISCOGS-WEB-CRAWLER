package db;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "arranged_by")
public class ArrangedBy {
    @EmbeddedId
    ArrangedBy_Id arrangedBy_id;

    @Embeddable
    public static class ArrangedBy_Id implements Serializable {
        @ManyToOne
        private Track track;

        @ManyToOne
        private Artist artist;

        public ArrangedBy_Id(Track track, Artist artist) {
            this.track = track;
            this.artist = artist;
        }
    }

    public ArrangedBy() {
    }

    public ArrangedBy(ArrangedBy_Id arrangedBy_id) {
        this.arrangedBy_id = arrangedBy_id;
    }
}
