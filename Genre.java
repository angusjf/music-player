import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

public class Genre {

	private int id;
	private String name;

	public Genre() { //TODO REMOVE
		this(0, "unnamed genre");
	}

	public Genre(int id, String name) {
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

		PreparedStatement statement = Main.database.createStatement("SELECT Songs.Id, Songs.AlbumId, Songs.TrackNumber, Songs.Name, Songs.File, Songs.Length FROM Songs Inner join albums on Songs.AlbumId = albums.id WHERE albums.GenreId = ? ORDER BY Albums.Title, AlbumId, tracknumber ASC");

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

	public static ArrayList<Genre> getAllFromDatabase() {
		ArrayList<Genre> all = new ArrayList<Genre>();

		ResultSet results = Main.database.getResultOfQuery("SELECT Id, Name FROM Genres");

		if (results != null)
			try {
				while (results.next()) all.add(new Genre(
					results.getInt("Id"), 
					results.getString("Name")
				));
			} catch (SQLException resultsexception) {
				System.out.println("- Database result processing error: " + resultsexception.getMessage());
			}

		return all;
	}
}
