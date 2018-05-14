/**
 * This class provides an interface (Facade) for a database.  It should be used in the 
 * following way:
 * 
 * try( DbFacade db = new DbFacade() ) {
 * 	  // Use db to execute appropriate methods.  The close method will automatically 
 *    // be called when we exit the try block.
 * }
 * 
 */

// Import the java.sql package to use JDBC methods and classes
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.time.Instant;

import static java.lang.Math.toIntExact;

public class DbFacade implements AutoCloseable {

	private Connection conn = null; // Connection object

	/**
	 * The default constructor will load and register the MySQL driver
	 */
	public DbFacade() throws SQLException {
		openDB();
	}

	/**
	 * @return the conn
	 */
	public Connection getConn() {
		return conn;
	}

	/**
	 * Open a MySQL DB connection where user name and password are predefined
	 * (hardwired) within the method.
	 */
	private void openDB() throws SQLException {
		// Connect to the database
		String url = "jdbc:mysql://mal.cs.plu.edu:3306/367_2018_cyan";
		String username = "cyan_2018";
		String password = "367rocks!";

		conn = DriverManager.getConnection(url, username, password);
	}

	/**
	 * Close the connection to the DB
	 */
	public void close() {
		try {
			if(conn != null) conn.close();
		} catch (SQLException e) {
			System.err.println("Failed to close database connection: " + e);
		}
		conn = null;
	}

	public boolean authenticateUser( String username, String password ) throws SQLException {
		String sql = "SELECT username FROM user WHERE " +
				" username = ? AND " +
				" password = SHA2(?, 256)";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.clearParameters();
		pstmt.setString(1, username);
		pstmt.setString(2, password);
		ResultSet rset = pstmt.executeQuery();
		return rset.first();
	}


	/**
	 * Creates a new account and places the new tuple with the parameters passed into the
	 * database.
	 * @param username The desired username for the new user
	 * @param password The desired password for the new user
	 * @param email The user's email address
	 * @return ResultSet containing the created row
	 */
	public ResultSet accountCreation(String username, String password, String email) {
		ResultSet rset = null;
		String sql = null;

		try {
			// create a Statement and an SQL string for the statement

			sql = "INSERT INTO user VALUES(?, SHA2(?, 256), ?)";
			PreparedStatement pstmt = conn.prepareStatement(sql);

			pstmt.clearParameters();
			pstmt.setString(1, username); // set the 1 parameter
			pstmt.setString(2, password); // set the 2 parameter
			pstmt.setString(3, email); // set the 3 parameter

			rset = pstmt.executeQuery();
		} catch (SQLException e) {
			System.out.println("accountCreation failed: " + e.getMessage());
		}

		return rset;
	}

	/**
	 * Adds administrator permissions to an account.
	 * @param username The username associated with the account to grant Administrator
	 * @return ResultSet containing the modified row
	 */
	public ResultSet grantAdministrator(String username) {
		ResultSet rset = null;
		String sql = null;

		try {
			// create a Statement and an SQL string for the statement

			sql = "INSERT INTO administator VALUES(?)";
			PreparedStatement pstmt = conn.prepareStatement(sql);

			pstmt.clearParameters();
			pstmt.setString(1, username); // set the 1 parameter

			rset = pstmt.executeQuery();
		} catch (SQLException e) {
			System.out.println("grantAdministrator failed: " + e.getMessage());
		}

		return rset;
	}

	/**
	 * Adds Critic permissions to an account.
	 * @param username The username associated with the account to grant Critic
	 * @return the modified row
	 */
	public ResultSet grantCritic(String username) {
		ResultSet rset = null;
		String sql = null;

		try {
			// create a Statement and an SQL string for the statement

			sql = "INSERT INTO critic VALUES(?)";
			PreparedStatement pstmt = conn.prepareStatement(sql);

			pstmt.clearParameters();
			pstmt.setString(1, username); // set the 1 parameter

			rset = pstmt.executeQuery();
		} catch (SQLException e) {
			System.out.println("grantCritic failed: " + e.getMessage());
		}

		return rset;
	}

	/**
	 * Adds a comment.
	 * @param content The comment to add.
	 * @return the row containing the added comment.
	 */
	public ResultSet addComment(String content) {
		ResultSet rset = null;
		String sql = null;

		try {
			// create a Statement and an SQL string for the statement

			sql = "INSERT INTO comment VALUES(comment_id, ?, false, review, post_date, user)";
			PreparedStatement pstmt = conn.prepareStatement(sql);

			pstmt.clearParameters();
			pstmt.setString(1, content); // set the 1 parameter

			rset = pstmt.executeQuery();
		} catch (SQLException e) {
			System.out.println("addComment failed: " + e.getMessage());
		}

		return rset;
	}

	/**
	 * Deletes a comment with the provided comment ID.
	 * @param comment_id the id of the comment to delete
	 * @return the deleted row.
	 */
	public ResultSet deleteComment(String comment_id) {
		ResultSet rset = null;
		String sql = null;

		try {
			// create a Statement and an SQL string for the statement

			sql = "DELETE comment_id FROM comment WHERE comment_id=?";
			PreparedStatement pstmt = conn.prepareStatement(sql);

			pstmt.clearParameters();
			pstmt.setString(1, comment_id); // set the 1 parameter

			rset = pstmt.executeQuery();
		} catch (SQLException e) {
			System.out.println("deleteComment failed: " + e.getMessage());
		}

		return rset;
	}

	/**
	 * Adds a new review to the database, taking information from user.
	 *
	 * Image is currently in database as a BLOB, but the intent is to
	 * store the name of the image file, kept outside the database on the
	 * server.
	 *
	 * Image filename format: XXXXXX_YYYYYYYYYYYYYYYY
	 *
	 * Where XXXX = username
	 *       YYYYYYYYYYYYYY = hash of the image data from hashImage()
	 *
	 * @param movie The name of the movie the review is about
	 * @param review An image associated with the movie
	 * @param rating The rating of the movie given by the reviewer
	 * @return the created review row
	 */
	public ResultSet postReview(String movie, String review, String rating) {
		ResultSet rset = null;
		String sql = null;

		try {
			// create a Statement and an SQL string for the statement


			sql = "INSERT INTO review VALUES(?, ?, ?, ?, post_date, critic)";
			PreparedStatement pstmt = conn.prepareStatement(sql);

			pstmt.clearParameters();
			//Uses mseconds from epoch as reviewID
			pstmt.setInt(1, toIntExact(Instant.now().toEpochMilli() ));
			pstmt.setString(2, movie);
			pstmt.setString(3, review);
			pstmt.setString(4, rating);
			//pstmt.setTimestamp(5, new Timestamp(System.currentTimeMillis()));



			rset = pstmt.executeQuery();
		} catch (SQLException e) {
			System.out.println("postReview failed: " + e.getMessage());
		}

		return rset;
	}

	/**
	 * Gets a ResultSet containing all of the movie reviews related to a movie with
	 * a provided title.
	 * @param movie The name of the movie to search for reviews for
	 * @return ResultSet containing all of the associated rows with the title
	 */
	public ResultSet listMovieReviews(String movie) {
		ResultSet rset = null;
		String sql = null;

		try {
			// create a Statement and an SQL string for the statement

			sql = "SELECT post_date, movie, rating FROM review WHERE movie = ?";
			PreparedStatement pstmt = conn.prepareStatement(sql);

			pstmt.clearParameters();

			pstmt.setString(1, movie); // set the 1 parameter

			rset = pstmt.executeQuery();

		} catch (SQLException e) {
			System.out.println("listMovieReview failed: " + e.getMessage());
		}

		return rset;
	}

	/**
	 * Deletes a user with given username from the database.
	 * @param username the username of the user to delete.
	 * @return a row containing the deleted user
	 */
	public ResultSet deleteUser(String username) {
		ResultSet rset = null;
		String sql = null;

		try {
			// create a Statement and an SQL string for the statement

			sql = "DELETE username FROM user WHERE username=?";
			PreparedStatement pstmt = conn.prepareStatement(sql);

			pstmt.clearParameters();
			pstmt.setString(1, username); // set the 1 parameter

			rset = pstmt.executeQuery();
		} catch (SQLException e) {
			System.out.println("deleteUser failed: " + e.getMessage());
		}

		return rset;
	}

	/**
	 * Hashes an image to an MD5 hash.
	 * @param path the path to the image file to hash.
	 * @return a hash string unique to the image.
	 */
	public static String hashImage(String path) {

		try {
			// Open file as BufferedImage
			File input = new File(path);
			BufferedImage image = ImageIO.read(input);

			// Convert image to bytearray
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			ImageIO.write(image, "jpg", os);
			byte[] byteArray = os.toByteArray();

			// Hash bytearray
			MessageDigest m = MessageDigest.getInstance("MD5");
			m.update(byteArray);
			byte[] hash = m.digest();

			return returnHex(hash);


		} catch (IOException e) {
			System.out.println("Error getting image hash. IO exception.");
		} catch (NoSuchAlgorithmException e) {
			System.out.println("Error finding hash algorithm. NoSuchAlgorithm exception.");
		}
		return "ERROR";
	}

	/**
	 * Converts a byte array to a hex string.
	 * From http://www.rgagnon.com/javadetails/java-0596.html
	 * @param inBytes
	 * @return a string containing the hash.
	 */
	private static String returnHex(byte[] inBytes)  {
		String hexString = "";
		for (int i=0; i < inBytes.length; i++) {
			hexString +=
					Integer.toString( ( inBytes[i] & 0xff ) + 0x100, 16).substring( 1 );
		}
		return hexString;
	}

}
