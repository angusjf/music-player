 

import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.SQLException;

class LibraryController {

	public LibraryController() {
		
	}

	public ArrayList<Song> getSongs() {
		return Song.getAllFromDatabase();
	}

	public ArrayList<Album> getAlbums() {
		return Album.getAllFromDatabase();
	}

	public ArrayList<Artist> getArtists() {
		return Artist.getAllFromDatabase();
	}

	public ArrayList<Genre> getGenres() {
		return Genre.getAllFromDatabase();
	}

	public ArrayList<Playlist> getPlaylists() {
		return Playlist.getAllFromDatabase();
	}

	public ArrayList<SongToPlaylist> getSongsToPlaylists() {
		return SongToPlaylist.getAllFromDatabase();
	}

	public ArrayList<ArtistFeaturedInSong> getArtistsFeaturedInSongs() {
		return ArtistFeaturedInSong.getAllFromDatabase();
	}

	public void importAlbum() { //TODO

	}

	public void saveLibraryToDatabase() { //TODO
		
	}
}
