import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

public class Genre implements HasAlbums {

	private int id;
	private String name;

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

	public ArrayList<Album> getAlbums() {
		ArrayList<Album> albums = new ArrayList<Album>();

		PreparedStatement statement = Main.database.createStatement("SELECT * FROM Albums WHERE genreId = ?");

		try {
			statement.setInt(1, id);
		} catch (SQLException ex) {
			System.out.println("- setting ? error");
		}

		ResultSet results = Main.database.runSelectStatement(statement);

		if (results != null) {
			try {
				while (results.next()) {
					albums.add(
						new Album(
							results.getInt("Id"), 
							results.getInt("artistId"),
							results.getInt("genreId"),
							results.getString("title"), 
							results.getString("year"),
							results.getString("picture")
						)
					);
				}
			} catch (SQLException resultsexception) {
				System.out.println("- Database result processing error: " + resultsexception.getMessage());
			}
		}
		return albums;
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
