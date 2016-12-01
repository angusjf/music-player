 

import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ArtistFeaturedInSong{

	private int artistId, songId;

	public ArtistFeaturedInSong(int artistId, int songId) {
		this.artistId = artistId;
		this.songId = songId;
	}

	public int getArtistId() {
		return artistId;
	}

	public int getSongId() {
		return songId;
	}

	public static ArrayList<ArtistFeaturedInSong> getAllFromDatabase() {
		ArrayList<ArtistFeaturedInSong> all = new ArrayList<ArtistFeaturedInSong>();

		ResultSet results = Main.database.getResultOfQuery("SELECT ArtistId, SongId FROM ArtistsFeaturedInSongs");

		if (results != null)
			try {
				while (results.next()) all.add(new ArtistFeaturedInSong(
					results.getInt("ArtistId"), 
					results.getInt("SongId")
				));
			} catch (SQLException resultsexception) {
				System.out.println("- Database result processing error: " + resultsexception.getMessage());
			}

		return all;
	}

}
