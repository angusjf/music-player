 

import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.SQLException;

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
		return picture;
	}

	public static ArrayList<Artist> getAllFromDatabase() {
		ArrayList<Artist> all = new ArrayList<Artist>();

		ResultSet results = Main.database.getResultOfQuery("SELECT Id, Name, Picture FROM Artists");

		if (results != null)
			try {
				while (results.next()) all.add(new Artist(
					results.getInt("Id"), 
					results.getString("Name"), 
					results.getString("Picture")
				));
			} catch (SQLException resultsexception) {
				System.out.println("- Database result processing error: " + resultsexception.getMessage());
			}

		return all;
	}
}
