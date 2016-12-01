import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.SQLException;

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
