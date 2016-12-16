import java.util.List;
import java.io.File;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.collections.ObservableMap;
import javafx.collections.MapChangeListener;


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

	public Song(File file) {
		
		/*
		 * image = javafx.scene.image.Image@13cc7fe0
		 * comment-0 = [eng]=Visit http://kozilek.bandcamp.com
		 * album artist = KOZILEK
		 * artist = KOZILEK
		 * year = 2015
		 * raw metadata = {ID3=java.nio.HeapByteBufferR[pos=494597 lim=494602 cap=494602]}
		 * album = GUN GODZ OST
		 * genre = (24)
		 * title = I Don't Need No Tricks To Kill
		 */


		int fileAlbumId = 0;
		int fileTrackNumber = 5;
		String fileName = file.getName();
		String fileArtist = file.getName();
		String albumName = file.getName();
		String fileFile = file.getAbsolutePath();
		String fileYear = "2100";
		String fileLength = "-1";

		Media media = new Media(file.toURI().toString());
		MediaPlayer mp = new MediaPlayer(media);
		mp.play();
		ObservableMap<String, Object> metaData = media.getMetadata();
		
		System.out.println("*****\n" + metaData);

		mp.getMedia().getMetadata().addListener( (MapChangeListener<String, Object>) change -> {
			if(change.wasAdded()) {
				System.out.println(change.getKey() + " = " + change.getValueAdded().toString());
			/*
				if (change.getKey().equals("artist"))
					fileArtist = change.getValueAdded().toString();
				else if (change.getKey().equals("title"))
					fileName = change.getValueAdded().toString();
				else if (change.getKey().equals("year"))
					fileYear = change.getValueAdded().toString();
					*/
			}
		});

		switch (0) {
			case 0:
				zzz = "Blues";
				break;
			case 1:
				zzz = "Classic Rock";
				break;
			case 2:
				zzz = "Country";
				break;
			case 3:
				zzz = "Dance";
				break;
			case 4:
				zzz = "Disco";
				break;
			case 5:
				zzz = "Funk";
				break;
			case 6:
				zzz = "Grunge";
				break;
			case 7:
				zzz = "Hip-Hop";
				break;
			case 8:
				zzz = "Jazz";
				break;
			case 9:
				zzz = "Metal";
				break;
			case 10:
				zzz = "New Age";
				break;
			case 11:
				zzz = "Oldies";
				break;
			case 12:
				zzz = "Other";
				break;
			case 13:
				zzz = "Pop";
				break;
			case 14:
				zzz = "R&B";
				break;
			case 15:
				zzz = "Rap";
				break;
			case 16:
				zzz = "Reggae";
				break;
			case 17:
				zzz = "Rock";
				break;
			case 18:
				zzz = "Techno";
				break;
			case 19:
				zzz = "Industrial";
				break;
			case 20:
				zzz = "Alternative";
				break;
			case 21:
				zzz = "Ska";
				break;
			case 22:
				zzz = "Death Metal";
				break;
			case 23:
				zzz = "Pranks";
				break;
			case 24:
				zzz = "Soundtrack";
				break;
			case 25:
				zzz = "Euro-Techno";
				break;
			case 26:
				zzz = "Ambient";
				break;
			case 27:
				zzz = "Trip-Hop";
				break;
			case 28:
				zzz = "Vocal";
				break;
			case 29:
				zzz = "Jazz+Funk";
				break;
			case 30:
				zzz = "Fusion";
				break;
			case 31:
				zzz = "Trance";
				break;
			case 32:
				zzz = "Classical";
				break;
			case 33:
				zzz = "Instrumental";
				break;
			case 34:
				zzz = "Acid";
				break;
			case 35:
				zzz = "House";
				break;
			case 36:
				zzz = "Game";
				break;
			case 37:
				zzz = "Sound Clip";
				break;
			case 38:
				zzz = "Gospel";
				break;
			case 39:
				zzz = "Noise";
				break;
			case 40:
				zzz = "Alternative Rock";
				break;
			case 41:
				zzz = "Bass";
				break;
			case 42:
				zzz = "Soul";
				break;
			case 43:
				zzz = "Punk";
				break;
			case 44:
				zzz = "Space";
				break;
			case 45:
				zzz = "Meditative";
				break;
			case 46:
				zzz = "Instrumental Pop";
				break;
			case 47:
				zzz = "Instrumental Rock";
				break;
			case 48:
				zzz = "Ethnic";
				break;
			case 49:
				zzz = "Gothic";
				break;
			case 50:
				zzz = "Darkwave";
				break;
			case 51:
				zzz = "Techno-Industrial";
				break;
			case 52:
				zzz = "Electronic";
				break;
			case 53:
				zzz = "Pop-Folk";
				break;
			case 54:
				zzz = "Eurodance";
				break;
			case 55:
				zzz = "Dream";
				break;
			case 56:
				zzz = "Southern Rock";
				break;
			case 57:
				zzz = "Comedy";
				break;
			case 58:
				zzz = "Cult";
				break;
			case 59:
				zzz = "Gangsta";
				break;
			case 60:
				zzz = "Top 40";
				break;
			case 61:
				zzz = "Christian Rap";
				break;
			case 62:
				zzz = "Pop/Funk";
				break;
			case 63:
				zzz = "Jungle";
				break;
			case 64:
				zzz = "Native US";
				break;
			case 65:
				zzz = "Cabaret";
				break;
			case 66:
				zzz = "New Wave";
				break;
			case 67:
				zzz = "Psychadelic";
				break;
			case 68:
				zzz = "Rave";
				break;
			case 69:
				zzz = "Showtunes";
				break;
			case 70:
				zzz = "Trailer";
				break;
			case 71:
				zzz = "Lo-Fi";
				break;
			case 72:
				zzz = "Tribal";
				break;
			case 73:
				zzz = "Acid Punk";
				break;
			case 74:
				zzz = "Acid Jazz";
				break;
			case 75:
				zzz = "Polka";
				break;
			case 76:
				zzz = "Retro";
				break;
			case 77:
				zzz = "Musical";
				break;
			case 78:
				zzz = "Rock & Roll";
				break;
			case 79:
				zzz = "Hard Rock";
				break;
			case 80:
				zzz = "Folk";
				break;
			case 81:
				zzz = "Folk-Rock";
				break;
			case 82:
				zzz = "National Folk";
				break;
			case 83:
				zzz = "Swing";
				break;
			case 84:
				zzz = "Fast Fusion";
				break;
			case 85:
				zzz = "Bebob";
				break;
			case 86:
				zzz = "Latin";
				break;
			case 87:
				zzz = "Revival";
				break;
			case 88:
				zzz = "Celtic";
				break;
			case 89:
				zzz = "Bluegrass";
				break;
			case 90:
				zzz = "Avantgarde";
				break;
			case 91:
				zzz = "Gothic Rock";
				break;
			case 92:
				zzz = "Progressive Rock";
				break;
			case 93:
				zzz = "Psychedelic Rock";
				break;
			case 94:
				zzz = "Symphonic Rock";
				break;
			case 95:
				zzz = "Slow Rock";
				break;
			case 96:
				zzz = "Big Band";
				break;
			case 97:
				zzz = "Chorus";
				break;
			case 98:
				zzz = "Easy Listening";
				break;
			case 99:
				zzz = "Acoustic";
				break;
			case 100:
				zzz = "Humour";
				break;
			case 101:
				zzz = "Speech";
				break;
			case 102:
				zzz = "Chanson";
				break;
			case 103:
				zzz = "Opera";
				break;
			case 104:
				zzz = "Chamber Music";
				break;
			case 105:
				zzz = "Sonata";
				break;
			case 106:
				zzz = "Symphony";
				break;
			case 107:
				zzz = "Booty Bass";
				break;
			case 108:
				zzz = "Primus";
				break;
			case 109:
				zzz = "Porn Groove";
				break;
			case 110:
				zzz = "Satire";
				break;
			case 111:
				zzz = "Slow Jam";
				break;
			case 112:
				zzz = "Club";
				break;
			case 113:
				zzz = "Tango";
				break;
			case 114:
				zzz = "Samba";
				break;
			case 115:
				zzz = "Folklore";
				break;
			case 116:
				zzz = "Ballad";
				break;
			case 117:
				zzz = "Power Ballad";
				break;
			case 118:
				zzz = "Rhytmic Soul";
				break;
			case 119:
				zzz = "Freestyle";
				break;
			case 120:
				zzz = "Duet";
				break;
			case 121:
				zzz = "Punk Rock";
				break;
			case 122:
				zzz = "Drum Solo";
				break;
			case 123:
				zzz = "Acapella";
				break;
			case 124:
				zzz = "Euro-House";
				break;
			case 125:
				zzz = "Dance Hall";
				break;
			case 126:
				zzz = "Goa";
				break;
			case 127:
				zzz = "Drum & Bass";
				break;
			case 128:
				zzz = "Club-House";
				break;
			case 129:
				zzz = "Hardcore";
				break;
			case 130:
				zzz = "Terror";
				break;
			case 131:
				zzz = "Indie";
				break;
			case 132:
				zzz = "BritPop";
				break;
			case 133:
				zzz = "Negerpunk";
				break;
			case 134:
				zzz = "Polsk Punk";
				break;
			case 135:
				zzz = "Beat";
				break;
			case 136:
				zzz = "Christian Gangsta";
				break;
			case 137:
				zzz = "Heavy Metal";
				break;
			case 138:
				zzz = "Black Metal";
				break;
			case 139:
				zzz = "Crossover";
				break;
			case 140:
				zzz = "Contemporary C";
				break;
			case 141:
				zzz = "Christian Rock";
				break;
			case 142:
				zzz = "Merengue";
				break;
			case 143:
				zzz = "Salsa";
				break;
			case 144:
				zzz = "Thrash Metal";
				break;
			case 145:
				zzz = "Anime";
				break;
			case 146:
				zzz = "JPop";
				break;
			case 147:
				zzz = "SynthPop";
				break;
		}

		/*
		// CHECK FOR ALBUM WITH THAT NAME
		ResultSet results = Main.database.getResultOfQuery("SELECT Id FROM Albums where title = ?");
		if (results.next()) {
			fileAlbumId = results.getInt(id);
		} else {
			//new album
			PreparedStatement statement = Main.database.createStatement(
				"INSERT INTO Albums (ArtistId, GenreId, Title, Year, Picture) VALUES (?, ?, ?, ?, ?)"
			);

			try {
				statement.setInt(1, fileAlbumId);
				statement.setInt(2, fileTrackNumber);
				statement.setString(3, fileAlbumTitle);
				statement.setString(4, fileFile);
				statement.setString(5, filePicture);
			}  catch (SQLException ex) {
				System.out.println("- setting ? error");
			}
		}

		//ADD TO DATABASE
*/
		PreparedStatement statement = Main.database.createStatement(
			"INSERT INTO SONGS (AlbumId, TrackNumber, Name, File, Length) VALUES (?, ?, ?, ?, ?)"
		);

		try {
			statement.setInt(1, fileAlbumId);
			statement.setInt(2, fileTrackNumber);
			statement.setString(3, fileName);
			statement.setString(4, fileFile);
			statement.setString(5, fileLength);
		}  catch (SQLException ex) {
			System.out.println("- setting ? error");
		}
		
		Main.database.runUpdateStatement(statement);
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
