import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

class Artist {

	private int id;
	private String name;
	private String picture;

	public Artist(int id, String name, String picture) {
		this.id = id;
		this.name = name;
		this.picture = picture;
	}

	public int getId() {
		return id;
	}

	public String toString() {
		return name;
	}

	public String getPicture() {
		return picture != null ? "resources/images/artists/" + picture : "resources/images/error.png";
	}

	public ArrayList<Album> getAlbums() {
		ArrayList<Album> albums = new ArrayList<Album>();

		PreparedStatement statement = Main.database.createStatement("select * from albums where artistId = ? order by title asc");

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

	public static ArrayList<Artist> getAllFromDatabase() {
		ArrayList<Artist> all = new ArrayList<Artist>();

		ResultSet results = Main.database.getResultOfQuery("SELECT Id, Name, Picture FROM Artists");

		if (results != null)
			try {
				while (results.next()) {
					all.add(
						new Artist(
							results.getInt("Id"), 
							results.getString("Name"), 
							results.getString("Picture")
						)
					);
				}
			} catch (SQLException resultsexception) {
				System.out.println("- Database result processing error: " + resultsexception.getMessage());
			}

		return all;
	}
}
