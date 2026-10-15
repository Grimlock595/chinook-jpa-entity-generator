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

@IdClass(PlaylistTrackId.class)
@Entity
@Table(name = "playlist_track")
public class PlaylistTrack {

    @Id
    @Column(name = "playlist_id", nullable = false)
    @NotNull
    private Integer playlistId;

    @Id
    @Column(name = "track_id", nullable = false)
    @NotNull
    private Integer trackId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "playlist_id", referencedColumnName = "playlist_id", insertable = false, updatable = false)
    private Playlist playlist;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "track_id", referencedColumnName = "track_id", insertable = false, updatable = false)
    private Track track;

    public Integer getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(Integer playlistId) {
        this.playlistId = playlistId;
    }

    public Integer getTrackId() {
        return trackId;
    }

    public void setTrackId(Integer trackId) {
        this.trackId = trackId;
    }

    public Playlist getPlaylist() {
        return playlist;
    }

    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
    }

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }

}
