import java.util.List;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Song {

	private int id;
	private int albumId;
	private int trackNumber;
	private String name, file, length;

	public Song(int id, int albumId, int trackNumber, String name, String file, String length) {
		this.id = id;
		this.albumId = albumId;
		this.trackNumber = trackNumber;
		this.name = name;
		this.file = file;
		this.length = length;
	}

	public int getId() {
		return id;
	}

	public String toString() {
		return name;
	}

	public String getFile() {
		return file;
	}

	public int getTrackNumber() {
		return trackNumber;
	}

	public String getLength() {
		return length == null || length.length() < 1 ? "0:00" : length;
	}

	public Artist getArtist() {
		PreparedStatement statement = Main.database.createStatement("SELECT ARTISTS.ID, ARTISTS.NAME, ARTISTS.PICTURE FROM SONGS INNER JOIN ALBUMS ON SONGS.ALBUMID = ALBUMS.ID INNER JOIN ARTISTS ON ARTISTS.ID = ALBUMS.ARTISTID WHERE SONGS.ID = ?");
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

	public ArrayList<Artist> getFeatures() { //TODO
		ArrayList<Artist> features = new ArrayList<Artist>();
		ResultSet results = Main.database.getResultOfQuery("SELECT ArtistId FROM ArtistsFeaturedInSongs WHERE SongId = " + id);

		if (results != null) {
			try {
				while (results.next())
					for (Artist artist : Artist.getAllFromDatabase())
						if (artist.getId() == results.getInt("ArtistId"))
							features.add(artist);
			} catch (SQLException resultsexception) {
				System.out.println("- Database result processing error: " + resultsexception.getMessage());
			}
		}
		return features;
	}

	public Album getAlbum() {
		PreparedStatement statement = Main.database.createStatement("SELECT ALBUMS.ID, ALBUMS.ARTISTID, ALBUMS.GENREID, ALBUMS.TITLE, ALBUMS.YEAR, ALBUMS.PICTURE FROM SONGS INNER JOIN ALBUMS ON SONGS.ALBUMID = ALBUMS.ID WHERE ALBUMS.ID = ?");
		try {
			statement.setInt(1, albumId);
		} catch (SQLException ex) {
			System.out.println("- setting ? error");
		}
		assert statement != null;

		ResultSet results = Main.database.runSelectStatement(statement);

		if (results != null) {
			try {
				while (results.next()) {
					return new Album(
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

	public void removeFromDatabase() {
		//TODO
		PreparedStatement statement = Main.database.createStatement("DELETE FROM SONGS WHERE Id = ?");
		try {
			statement.setInt(1, id);
		} catch (SQLException ex) {
			System.out.println("- setting ? error");
		}
		assert statement != null;

		Main.database.runUpdateStatement(statement);
	}

	public static ArrayList<Song> getAllFromDatabase() {
		ArrayList<Song> all = new ArrayList<Song>();

		ResultSet results = Main.database.getResultOfQuery("SELECT Id, AlbumId, TrackNumber, Name, File, Length FROM Songs");

		if (results != null) {
			try {
				while (results.next()) all.add(new Song(
					results.getInt("Id"),
					results.getInt("AlbumId"),
					results.getInt("TrackNumber"),
					results.getString("Name"),
					results.getString("File"),
					results.getString("Length")
				));
			} catch (SQLException resultsexception) {
				System.out.println("- Database result processing error: " + resultsexception.getMessage());
			}
		}
		return all;
	}
}
