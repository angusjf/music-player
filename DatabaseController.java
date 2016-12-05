import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.io.File;

class DatabaseController { //like 70% Steve's work

	public Connection connection;

	public DatabaseController(String file) {
		File databaseFile = new File(file);
		System.out.println(databaseFile.exists() ? "yep that database is real" : "noooooooo database");
		connect(file);
	}

	public PreparedStatement createStatement(String query) {
		PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement(query);
		} catch (SQLException resultsException) {
			System.out.println("Database statement error: " + resultsException.getMessage());
		}
		return statement;
	}

	public ResultSet runStatement(PreparedStatement statement) {
		try {
			return statement.executeQuery();
		} catch (SQLException queryException) {
			System.out.println("Database query error: " + queryException.getMessage());
			return null;
		}
	}

	//TODO just for compatibility
	public ResultSet getResultOfQuery(String query) {
		return runStatement(createStatement(query));
	}

	private void connect(String databaseFile) {
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
}
