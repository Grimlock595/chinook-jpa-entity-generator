package generated_entities;

import java.io.Serializable;
import java.util.Objects;

public class PlaylistTrackId implements Serializable {

    private Integer playlistId;

    private Integer trackId;

    public PlaylistTrackId() {
    }

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

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        PlaylistTrackId that = (PlaylistTrackId) object;
        return Objects.equals(playlistId, that.playlistId) && Objects.equals(trackId, that.trackId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playlistId, trackId);
    }
}
