package generated_entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "media_type")
public class MediaType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "media_type_id", nullable = false)
    @NotNull
    private Integer mediaTypeId;

    @Column(name = "name", length = 120, nullable = true)
    @Size(max = 120)
    private String name;

    public Integer getMediaTypeId() {
        return mediaTypeId;
    }

    public void setMediaTypeId(Integer mediaTypeId) {
        this.mediaTypeId = mediaTypeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
