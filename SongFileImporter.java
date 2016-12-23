import java.io.File;
import javafx.scene.image.Image;
import javafx.embed.swing.SwingFXUtils;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.collections.ObservableMap;
import javafx.collections.MapChangeListener;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SongFileImporter {

	boolean gotImage, gotAlbum, gotArtist, gotYear = true /*TODO*/, gotGenre, gotTitle, gotTrackNumber = true /*TODO*/, gotLength = true /*TODO*/;
	boolean done = false;
	private MediaPlayer mediaPlayer;
	private File file;

	public SongFileImporter(File file) {
		System.out.println("£ importing file " + file.getPath());

		this.file = file;
		mediaPlayer = new MediaPlayer(new Media(file.toURI().toString()));

		mediaPlayer.getMedia().getMetadata().addListener((MapChangeListener<String, Object>) change -> {
			if (change.wasAdded() && !done) {
				if (change.getKey().equals("image")) {
					gotImage = true;
				} else if (change.getKey().equals("album")) {
					gotAlbum = true;
				} else if (change.getKey().equals("artist")) {
					gotArtist = true;
				} else if (change.getKey().equals("year")) {
					gotYear = true;
				} else if (change.getKey().equals("genre")) {
					gotGenre = true;
				} else if (change.getKey().equals("title")) {
					gotTitle = true;
				} else if (change.getKey().equals("track number")) {
					gotTrackNumber = true;
				} else if (change.getKey().equals("length")) {
					gotLength = true;
				}

				System.out.println(mediaPlayer.getMedia().getMetadata());

				if (gotImage && gotAlbum && gotArtist && gotYear && gotGenre && gotTitle && gotTrackNumber && gotLength) {
					metadataExtractionComplete();
				}
			}
		});
	}

	public void metadataExtractionComplete() {
		done = true;

		System.out.println("£ metatdata collection for file " + file.getPath() + " successful");

		ObservableMap<String, Object> metaData = mediaPlayer.getMedia().getMetadata();

		int fileTrackNumber = -99; //*Integer.parseInt(*/(Integer)metaData.get("track number")/*)*/;
		String fileName = (String)metaData.get("title");
		String fileFile = (String)file.getAbsolutePath();
		String fileLength = (String)metaData.get("length");
		String fileYear = "2345";//((Integer)metaData.get("year")).toString();

		//check for new genre
		int fileGenreId = getGenreId((String)metaData.get("genre"));

		System.out.println("£ got the genreId");

		//check for new artist
		int fileArtistId = getArtistId((String)metaData.get("artist"));

		System.out.println("£ got the artistId");

		//check for new album
		int fileAlbumId = getAlbumId((String)metaData.get("album"), fileArtistId, fileGenreId, fileYear, (Image)metaData.get("image"));

		System.out.println("£ got the albumId");

		//add to database
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

		System.out.println("£ done !");
		Main.mainSceneController.updateLibraryPane();
	}

	private int getGenreId(String genreString) {
		if (genreString.startsWith("(")) {
			genreString = getGenreFromNumber(Integer.parseInt(genreString.substring(1, genreString.length() - 2)));
		}
		
		PreparedStatement statement1 = Main.database.createStatement("SELECT Id FROM Genres WHERE Name = ?");
		try {
			statement1.setString(1, genreString);
		}  catch (SQLException ex) {
			System.out.println("- setting ? error");
		}

		ResultSet results = Main.database.runSelectStatement(statement1);
		if (results != null) {
			try {
				if (results.next()) {
					return results.getInt("Id");
				} else {
					PreparedStatement statement2 = Main.database.createStatement("INSERT INTO Genres (Name) VALUES (?)");
					try {
						statement2.setString(1, genreString);
					} catch (SQLException ex) {
						System.out.println("- setting ? error");
					}
					Main.database.runUpdateStatement(statement2);
					
					PreparedStatement statement3 = Main.database.createStatement("SELECT Id FROM Genres WHERE Name = ?");
					try {
						statement3.setString(1, genreString);
					}  catch (SQLException ex) {
						System.out.println("- setting ? error");
					}

					ResultSet results3 = Main.database.runSelectStatement(statement3);
					if (results3.next()) return results3.getInt("Id");
				}
			} catch (SQLException resultsexception) {
				System.out.println("- Database result processing error: " + resultsexception.getMessage());
			}
		}
		return -401;
	}

	private int getArtistId(String artistName) {
		PreparedStatement statement1 = Main.database.createStatement("SELECT Id FROM Artists WHERE Name = ?");
		try {
			statement1.setString(1, artistName);
		}  catch (SQLException ex) {
			System.out.println("- setting ? error");
		}

		ResultSet results = Main.database.runSelectStatement(statement1);
		if (results != null) {
			try {
				if (results.next()) {
					return results.getInt("Id");
				} else {
					PreparedStatement statement2 = Main.database.createStatement("INSERT INTO Artists (Name) VALUES (?)");
					try {
						statement2.setString(1, artistName);
					} catch (SQLException ex) {
						System.out.println("- setting ? error");
					}
					Main.database.runUpdateStatement(statement2);

					PreparedStatement statement3 = Main.database.createStatement("SELECT Id FROM Artists WHERE Name = ?");
					try {
						statement3.setString(1, artistName);
					}  catch (SQLException ex) {
						System.out.println("- setting ? error");
					}

					ResultSet results3 = Main.database.runSelectStatement(statement3);
					if (results3.next()) return results3.getInt("Id");
				}
			} catch (SQLException resultsexception) {
				System.out.println("- Database result processing error: " + resultsexception.getMessage());
			}
		}
		return -403;
	}

	private int getAlbumId(String albumTitle, int artistId, int genreId, String albumYear, Image art) {

		PreparedStatement statement1 = Main.database.createStatement("SELECT Id FROM Albums WHERE Title = ? AND ArtistId = ?");
		try {
			statement1.setString(1, albumTitle);
			statement1.setInt(2, artistId);
		}  catch (SQLException ex) {
			System.out.println("- setting ? error");
		}

		ResultSet results = Main.database.runSelectStatement(statement1);
		if (results != null) {
			try {
				if (results.next()) {
					return results.getInt("Id");
				} else {

					String imageUrl = "./resources/images/albums/" + albumTitle.replaceAll("[^a-zA-Z0-9.-]", "_") + ".png";
					File outputFile = new File(imageUrl);

					try {
						outputFile.createNewFile();
					} catch (IOException e) {
						System.out.println("! album art file error: " + imageUrl + "; " + e.getMessage());
					}

					BufferedImage bi = SwingFXUtils.fromFXImage(art, null);
					try {
						ImageIO.write(bi, "png", outputFile);
					} catch (IOException e) {
						System.out.println("error writing song image to file");
						throw new RuntimeException(e);
					}

					System.out.println(" !!! no albums found for that statemnt");
					PreparedStatement statement2 = Main.database.createStatement(
						"INSERT INTO Albums (ArtistId, GenreId, Title, Year, Picture) VALUES (?, ?, ?, ?, ?)");
					try {
						statement2.setInt(1, artistId);
						statement2.setInt(2, genreId);
						statement2.setString(3, albumTitle);
						statement2.setString(4, albumYear);
						statement2.setString(5, imageUrl);
					} catch (SQLException ex) {
						System.out.println("- setting ? error");
					}
					Main.database.runUpdateStatement(statement2);
					System.out.println(" !!! making new album with title=" + albumTitle);

					PreparedStatement statement3 = Main.database.createStatement("SELECT Id FROM Albums WHERE Title = ? AND ArtistId = ?");
					try {
						statement3.setString(1, albumTitle);
						statement3.setInt(2, artistId);
					}  catch (SQLException ex) {
						System.out.println("- setting ? error");
					}
					ResultSet results2 = Main.database.runSelectStatement(statement3);
					if (results2.next()) return results2.getInt("Id");
				}
			} catch (SQLException resultsexception) {
				System.out.println("- Database result processing error: " + resultsexception.getMessage());
			}
		} else {
			System.out.println("¿¿¿ results was null ????");
			return -404;
		}
		return -405;
	}

	private String getGenreFromNumber(int fileGenreNumber) {
		String fileGenre;
		switch (fileGenreNumber) {
			case 0:
				fileGenre = "Blues";
				break;
			case 1:
				fileGenre = "Classic Rock";
				break;
			case 2:
				fileGenre = "Country";
				break;
			case 3:
				fileGenre = "Dance";
				break;
			case 4:
				fileGenre = "Disco";
				break;
			case 5:
				fileGenre = "Funk";
				break;
			case 6:
				fileGenre = "Grunge";
				break;
			case 7:
				fileGenre = "Hip-Hop";
				break;
			case 8:
				fileGenre = "Jazz";
				break;
			case 9:
				fileGenre = "Metal";
				break;
			case 10:
				fileGenre = "New Age";
				break;
			case 11:
				fileGenre = "Oldies";
				break;
			case 12:
				fileGenre = "Other";
				break;
			case 13:
				fileGenre = "Pop";
				break;
			case 14:
				fileGenre = "R&B";
				break;
			case 15:
				fileGenre = "Rap";
				break;
			case 16:
				fileGenre = "Reggae";
				break;
			case 17:
				fileGenre = "Rock";
				break;
			case 18:
				fileGenre = "Techno";
				break;
			case 19:
				fileGenre = "Industrial";
				break;
			case 20:
				fileGenre = "Alternative";
				break;
			case 21:
				fileGenre = "Ska";
				break;
			case 22:
				fileGenre = "Death Metal";
				break;
			case 23:
				fileGenre = "Pranks";
				break;
			case 24:
				fileGenre = "Soundtrack";
				break;
			case 25:
				fileGenre = "Euro-Techno";
				break;
			case 26:
				fileGenre = "Ambient";
				break;
			case 27:
				fileGenre = "Trip-Hop";
				break;
			case 28:
				fileGenre = "Vocal";
				break;
			case 29:
				fileGenre = "Jazz+Funk";
				break;
			case 30:
				fileGenre = "Fusion";
				break;
			case 31:
				fileGenre = "Trance";
				break;
			case 32:
				fileGenre = "Classical";
				break;
			case 33:
				fileGenre = "Instrumental";
				break;
			case 34:
				fileGenre = "Acid";
				break;
			case 35:
				fileGenre = "House";
				break;
			case 36:
				fileGenre = "Game";
				break;
			case 37:
				fileGenre = "Sound Clip";
				break;
			case 38:
				fileGenre = "Gospel";
				break;
			case 39:
				fileGenre = "Noise";
				break;
			case 40:
				fileGenre = "Alternative Rock";
				break;
			case 41:
				fileGenre = "Bass";
				break;
			case 42:
				fileGenre = "Soul";
				break;
			case 43:
				fileGenre = "Punk";
				break;
			case 44:
				fileGenre = "Space";
				break;
			case 45:
				fileGenre = "Meditative";
				break;
			case 46:
				fileGenre = "Instrumental Pop";
				break;
			case 47:
				fileGenre = "Instrumental Rock";
				break;
			case 48:
				fileGenre = "Ethnic";
				break;
			case 49:
				fileGenre = "Gothic";
				break;
			case 50:
				fileGenre = "Darkwave";
				break;
			case 51:
				fileGenre = "Techno-Industrial";
				break;
			case 52:
				fileGenre = "Electronic";
				break;
			case 53:
				fileGenre = "Pop-Folk";
				break;
			case 54:
				fileGenre = "Eurodance";
				break;
			case 55:
				fileGenre = "Dream";
				break;
			case 56:
				fileGenre = "Southern Rock";
				break;
			case 57:
				fileGenre = "Comedy";
				break;
			case 58:
				fileGenre = "Cult";
				break;
			case 59:
				fileGenre = "Gangsta";
				break;
			case 60:
				fileGenre = "Top 40";
				break;
			case 61:
				fileGenre = "Christian Rap";
				break;
			case 62:
				fileGenre = "Pop/Funk";
				break;
			case 63:
				fileGenre = "Jungle";
				break;
			case 64:
				fileGenre = "Native US";
				break;
			case 65:
				fileGenre = "Cabaret";
				break;
			case 66:
				fileGenre = "New Wave";
				break;
			case 67:
				fileGenre = "Psychadelic";
				break;
			case 68:
				fileGenre = "Rave";
				break;
			case 69:
				fileGenre = "Showtunes";
				break;
			case 70:
				fileGenre = "Trailer";
				break;
			case 71:
				fileGenre = "Lo-Fi";
				break;
			case 72:
				fileGenre = "Tribal";
				break;
			case 73:
				fileGenre = "Acid Punk";
				break;
			case 74:
				fileGenre = "Acid Jazz";
				break;
			case 75:
				fileGenre = "Polka";
				break;
			case 76:
				fileGenre = "Retro";
				break;
			case 77:
				fileGenre = "Musical";
				break;
			case 78:
				fileGenre = "Rock & Roll";
				break;
			case 79:
				fileGenre = "Hard Rock";
				break;
			case 80:
				fileGenre = "Folk";
				break;
			case 81:
				fileGenre = "Folk-Rock";
				break;
			case 82:
				fileGenre = "National Folk";
				break;
			case 83:
				fileGenre = "Swing";
				break;
			case 84:
				fileGenre = "Fast Fusion";
				break;
			case 85:
				fileGenre = "Bebob";
				break;
			case 86:
				fileGenre = "Latin";
				break;
			case 87:
				fileGenre = "Revival";
				break;
			case 88:
				fileGenre = "Celtic";
				break;
			case 89:
				fileGenre = "Bluegrass";
				break;
			case 90:
				fileGenre = "Avantgarde";
				break;
			case 91:
				fileGenre = "Gothic Rock";
				break;
			case 92:
				fileGenre = "Progressive Rock";
				break;
			case 93:
				fileGenre = "Psychedelic Rock";
				break;
			case 94:
				fileGenre = "Symphonic Rock";
				break;
			case 95:
				fileGenre = "Slow Rock";
				break;
			case 96:
				fileGenre = "Big Band";
				break;
			case 97:
				fileGenre = "Chorus";
				break;
			case 98:
				fileGenre = "Easy Listening";
				break;
			case 99:
				fileGenre = "Acoustic";
				break;
			case 100:
				fileGenre = "Humour";
				break;
			case 101:
				fileGenre = "Speech";
				break;
			case 102:
				fileGenre = "Chanson";
				break;
			case 103:
				fileGenre = "Opera";
				break;
			case 104:
				fileGenre = "Chamber Music";
				break;
			case 105:
				fileGenre = "Sonata";
				break;
			case 106:
				fileGenre = "Symphony";
				break;
			case 107:
				fileGenre = "Booty Bass";
				break;
			case 108:
				fileGenre = "Primus";
				break;
			case 109:
				fileGenre = "Porn Groove";
				break;
			case 110:
				fileGenre = "Satire";
				break;
			case 111:
				fileGenre = "Slow Jam";
				break;
			case 112:
				fileGenre = "Club";
				break;
			case 113:
				fileGenre = "Tango";
				break;
			case 114:
				fileGenre = "Samba";
				break;
			case 115:
				fileGenre = "Folklore";
				break;
			case 116:
				fileGenre = "Ballad";
				break;
			case 117:
				fileGenre = "Power Ballad";
				break;
			case 118:
				fileGenre = "Rhytmic Soul";
				break;
			case 119:
				fileGenre = "Freestyle";
				break;
			case 120:
				fileGenre = "Duet";
				break;
			case 121:
				fileGenre = "Punk Rock";
				break;
			case 122:
				fileGenre = "Drum Solo";
				break;
			case 123:
				fileGenre = "Acapella";
				break;
			case 124:
				fileGenre = "Euro-House";
				break;
			case 125:
				fileGenre = "Dance Hall";
				break;
			case 126:
				fileGenre = "Goa";
				break;
			case 127:
				fileGenre = "Drum & Bass";
				break;
			case 128:
				fileGenre = "Club-House";
				break;
			case 129:
				fileGenre = "Hardcore";
				break;
			case 130:
				fileGenre = "Terror";
				break;
			case 131:
				fileGenre = "Indie";
				break;
			case 132:
				fileGenre = "BritPop";
				break;
			case 133:
				fileGenre = "Negerpunk";
				break;
			case 134:
				fileGenre = "Polsk Punk";
				break;
			case 135:
				fileGenre = "Beat";
				break;
			case 136:
				fileGenre = "Christian Gangsta";
				break;
			case 137:
				fileGenre = "Heavy Metal";
				break;
			case 138:
				fileGenre = "Black Metal";
				break;
			case 139:
				fileGenre = "Crossover";
				break;
			case 140:
				fileGenre = "Contemporary C";
				break;
			case 141:
				fileGenre = "Christian Rock";
				break;
			case 142:
				fileGenre = "Merengue";
				break;
			case 143:
				fileGenre = "Salsa";
				break;
			case 144:
				fileGenre = "Thrash Metal";
				break;
			case 145:
				fileGenre = "Anime";
				break;
			case 146:
				fileGenre = "JPop";
				break;
			case 147:
				fileGenre = "SynthPop";
				break;
			default:
				fileGenre = "Unknown Genre";
				break;
		}
		return fileGenre;
	}

}
