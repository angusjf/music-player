import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

class DatabaseController {

	public Connection connection;
	public static final String[] tableStatements = {
		"PRAGMA foreign_keys = off;",
		"BEGIN TRANSACTION;",
		"CREATE TABLE Songs (Id INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE NOT NULL, AlbumId INTEGER REFERENCES Albums (Id), TrackNumber INTEGER, Name VARCHAR, File VARCHAR, Length VARCHAR);",
		"CREATE TABLE Playlists (Id INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE NOT NULL, Name VARCHAR);",
		"CREATE TABLE SongsToPlaylists (SongId INTEGER REFERENCES Songs (Id), PlaylistId INTEGER REFERENCES Playlists (Id));",
		"CREATE TABLE Genres (Id INTEGER PRIMARY KEY AUTOINCREMENT, Name VARCHAR);",
		"CREATE TABLE Artists (Id INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE NOT NULL, Name VARCHAR, Picture VARCHAR);",
		"CREATE TABLE Albums (Id INTEGER NOT NULL UNIQUE PRIMARY KEY AUTOINCREMENT, ArtistId INTEGER REFERENCES Artists (Id), GenreId INTEGER REFERENCES Genres (Id), Title VARCHAR, Year VARCHAR, Picture VARCHAR);",
		"CREATE TABLE ArtistsFeaturedInSongs (ArtistId INTEGER REFERENCES Artists (Id), SongId INTEGER REFERENCES Songs (Id));",
		"COMMIT TRANSACTION;",
		"PRAGMA foreign_keys = on;"
	};

	public DatabaseController(String file) {
		connect(file);
	}

	public void connect(String databaseFile) {
		try {
			Class.forName("org.sqlite.JDBC"); // checks for a missing driver class
			connection = DriverManager.getConnection("jdbc:sqlite:" + databaseFile); // checks file
			System.out.println("+ Database connection successfully established.");
		} catch (ClassNotFoundException cnfex) {
			System.out.println("- (Database driver error) Class not found exception: " + cnfex.getMessage());
		} catch (SQLException exception) {
			System.out.println("- Database file error: " + exception.getMessage());
		}
	}

	public void disconnect() {
		System.out.println("+ Disconnecting from database.");
		try {
			if (connection != null) connection.close();
		} catch (SQLException finalexception) {
			System.out.println("- Database disconnection error: " + finalexception.getMessage());
		}
	}

	public void createDatabase(String databaseFile) {
		for (String statementString : tableStatements) {
			PreparedStatement ps = createStatement(statementString);
			runUpdateStatement(ps);
		}
	}

	// STATEMENTS

	public PreparedStatement createStatement(String query) {
		PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement(query);
		} catch (SQLException resultsException) {
			System.out.println("Database statement error: " + resultsException.getMessage());
		}
		return statement;
	}

	public ResultSet runSelectStatement(PreparedStatement statement) {
		try {
			return statement.executeQuery();
		} catch (SQLException queryException) {
			System.out.println("Database query error: " + queryException.getMessage());
			return null;
		}
	}

	//just for compatibility
	public ResultSet getResultOfQuery(String query) { return runSelectStatement(createStatement(query)); }

	public void runUpdateStatement(PreparedStatement statement) {
		try {
			statement.executeUpdate();
		} catch (SQLException queryException) {
			System.out.println("Database update error: " + queryException.getMessage());
		}
	}
}
