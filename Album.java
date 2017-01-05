import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Album implements HasSongs {

	private	int id, artistId, genreId;
	private String title, year, picture;

	public Album(int id, int artistId, int genreId, String title, String year, String picture) {
		this.id = id;
		this.artistId = artistId;
		this.genreId = genreId;
		this.title = title;
		this.year = year;
		this.picture = picture;
	}

	public int getId() {
		return id;
	}

	public String toString() {
		return title;
	}

	public String getYear() {
		return year;
	}

	public String getPicture() {
		return picture != null ? picture : "data/error.png";
	}

	public ArrayList<Song> getSongs() {
		ArrayList<Song> songs = new ArrayList<Song>();

		PreparedStatement statement = Main.database.createStatement("SELECT Id, AlbumId, TrackNumber, Name, File, Length FROM Songs WHERE AlbumId = ? ORDER BY TrackNumber ASC");

		try {
			statement.setInt(1, id);
		} catch (SQLException ex) {
			System.out.println("- setting ? error");
		}

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

	public Artist getArtist() {
		PreparedStatement statement = Main.database.createStatement("SELECT * FROM ARTISTS WHERE ID = ?");
		try {
			statement.setInt(1, artistId);
		} catch (SQLException ex) {
			System.out.println("- setting ? error");
		}
		assert statement != null;

		ResultSet results = Main.database.runSelectStatement(statement);

		if (results != null) {
			try {
				while (results.next()) {
					return new Artist (
						results.getInt("Id"), 
						results.getString("Name"),
						results.getString("Picture")
					);
				}
			} catch (SQLException resultsexception) {
				System.out.println("- Database result processing error: " + resultsexception.getMessage());
			}
		}

		return null;
	}
	
	Genre getGenre() {
		PreparedStatement statement = Main.database.createStatement("SELECT Name FROM Genres WHERE ID = ?");
		try {
			statement.setInt(1, genreId);
		} catch (SQLException ex) {
			System.out.println("- setting ? error");
		}
		assert statement != null;

		ResultSet results = Main.database.runSelectStatement(statement);

		if (results != null) {
			try {
				while (results.next()) {
					return new Genre (
						results.getInt("Id"), 
						results.getString("Name")
					);
				}
			} catch (SQLException resultsexception) {
				System.out.println("- Database result processing error: " + resultsexception.getMessage());
			}
		}

		return null;
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

	public void removeFromDatabase() {
		for (Song song : getSongs()) {
			song.removeFromDatabase();
		}
	}

	public static ArrayList<Album> getAllFromDatabase() {
		ArrayList<Album> all = new ArrayList<Album>();

		PreparedStatement statement = Main.database.createStatement("SELECT * FROM ALBUMS");
		assert statement != null;

		ResultSet results = Main.database.runSelectStatement(statement);
		if (results != null) {
			try {
				while (results.next()) {
					all.add(
						new Album(
							results.getInt("Id"), 
							results.getInt("ArtistId"), 
							results.getInt("GenreId"),
							results.getString("Title"),
							results.getString("Year"),
							results.getString("Picture")
						)
					);
				}
			} catch (SQLException resultsexception) {
				System.out.println("- Database result processing error: " + resultsexception.getMessage());
			}
		}

		return all;
	}
}
