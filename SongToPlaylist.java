 

import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SongToPlaylist {

	private int songId, playlistId;

	public SongToPlaylist(int songId, int playlistId) {
		this.songId = songId;
		this.playlistId = playlistId;
	}

	public int getSongId() {
		return songId;
	}

	public int getPlaylistId() {
		return playlistId;
	}

	public Song getSong() {
		PreparedStatement statement = Main.database.createStatement("SELECT * FROM SONG WHERE ID = ?");
		try {
			statement.setInt(1, songId);
		} catch (SQLException ex) {
			System.out.println("- setting ? error");
		}
		assert statement != null;

		ResultSet results = Main.database.runStatement(statement);

		if (results != null) {
			try {
				while (results.next()) {
					return new Song (
						results.getInt("Id"), 
						results.getInt("ArtistId"),
						results.getInt("GenreId"),
						results.getString("Title"), 
						results.getString("Year"),
						results.getString("Picture")
					);
				}
			} catch (SQLException resultsexception) {
				System.out.println("- Database result processing error: " + resultsexception.getMessage());
			}
		}
		return null;
	}

	/*
	public Playlist getPlaylist() {
		return null;
	}
	*/

	public static ArrayList<SongToPlaylist> getAllFromDatabase() {
		ArrayList<SongToPlaylist> all = new ArrayList<SongToPlaylist>();

		ResultSet results = Main.database.getResultOfQuery("SELECT SongId, PlaylistId FROM SongsToPlaylists");

		if (results != null)
			try {
				while (results.next()) all.add(new SongToPlaylist(
					results.getInt("SongId"),
					results.getInt("PlaylistId")
				));
			} catch (SQLException resultsexception) {
				System.out.println("- Database result processing error: " + resultsexception.getMessage());
			}

		return all;
	}

}
