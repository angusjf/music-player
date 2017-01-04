import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

public class Playlist implements HasSongs {
	
	private int id;
	private String name;

	public Playlist (int id, String name) {
		this.id = id;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public String toString() {
		return name;
	}

	public ArrayList<Song> getSongs() {
		ArrayList<Song> songs = new ArrayList<Song>();

		PreparedStatement statement = Main.database.createStatement("SELECT SONGS.ID, SONGS.ALBUMID, SONGS.TRACKNUMBER, SONGS.NAME, SONGS.FILE, SONGS.LENGTH FROM PLAYLISTS INNER JOIN SONGSTOPLAYLISTS ON PLAYLISTS.ID = SONGSTOPLAYLISTS.PLAYLISTID INNER JOIN SONGS ON SONGSTOPLAYLISTS.SONGID = SONGS.ID WHERE PLAYLISTID = ?");
		try {
			statement.setInt(1, id);
		} catch (SQLException ex) {
			System.out.println("- setting ? error");
		}
		assert statement != null;

		ResultSet results = Main.database.runSelectStatement(statement);

		if (results != null) {
			try {
				while (results.next()) {
					songs.add(
						new Song(
							results.getInt("Id"), 
							results.getInt("AlbumId"),
							results.getInt("TrackNumber"),
							results.getString("Name"), 
							results.getString("File"),
							results.getString("Length")
						)
					);
				}
			} catch (SQLException resultsexception) {
				System.out.println("- Database result processing error: " + resultsexception.getMessage());
			}
		}

		return songs;
	}

	public int getNumberOfSongs() {
		return getSongs().size();
	}

	public String getLength() {
		//TODO X hours, X mins
		/*
		 * int lengthSeconds;
		 * int lengthMinuites;
		 * int lengthHours;
		 * for (Song song : getSongs()) {
		 *	+= song.getLength();
		 * }
		 * //Calc
		 * String ret = "";
		 * lengthHours + " hour, " + lengthMinuites + " minuites";
		 * return ret;
		 */
		return "<TODO>";
	}

	public void addSong(Song song) {
		PreparedStatement statement = Main.database.createStatement(
			"INSERT INTO SongsToPlaylists (SongId, PlaylistId) VALUES (?, ?)"
		);
		try {
			statement.setInt(1, song.getId());
			statement.setInt(2, id);
		}  catch (SQLException ex) {
			System.out.println("- setting ? error");
		}

		Main.database.runUpdateStatement(statement);
	}

	public static ArrayList<Playlist> getAllFromDatabase() {
		ArrayList<Playlist> all = new ArrayList<Playlist>();

		PreparedStatement statement = Main.database.createStatement("SELECT * FROM PLAYLISTS");
		assert statement != null;

		ResultSet results = Main.database.runSelectStatement(statement);

		if (results != null) {
			try {
				while (results.next()) {
					all.add(
						new Playlist (
							results.getInt("Id"), 
							results.getString("Name")
						)
					);
				}
			} catch (SQLException resultsexception) {
				System.out.println("- Database result processing error: " + resultsexception.getMessage());
			}
		}

		return all;
	}

	public static void createNewPlaylist(String name) {
		PreparedStatement statement = Main.database.createStatement("INSERT INTO Playlists (Name) VALUES (?)");

		try {
			statement.setString(1, name);
		}  catch (SQLException ex) {
			System.out.println("- setting ? error");
		}

		Main.database.runUpdateStatement(statement);
	}

}
