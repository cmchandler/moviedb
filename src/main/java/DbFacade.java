/**
 * Prov
 */

import javax.imageio.ImageIO;
import javax.sound.midi.Soundbank;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

public class DbFacade implements AutoCloseable {

    private Connection conn;

    /**
     * Constructor. Opens connection to DBMS with the username and password hardcoded here.
     * @throws SQLException
     */
    public DbFacade() throws SQLException {
        String url = "jdbc:mariadb://mal.cs.plu.edu:3306/367_2018_company";
        String username = "cyan_2018";
        String password = "367rocks!";

        conn = DriverManager.getConnection(url, username, password);

        System.out.println("Connection opened.");
    }

    /**
    * Closes the connection to the DBMS. Prints message to terminal if the closing operation was
     * successful.
     */
    @Override
    public void close() throws SQLException {
        if( conn != null )
            conn.close();
        conn = null;

        System.out.println("Connection closed.");

    }

    /**
     * Creates a new account and places the new tuple with the parameters passed into the
     * database.
     * @param username The desired username for the new user
     * @param password The desired password for the new user
     * @param email The user's email address
     * @return ResultSet containing the
     */
    public ResultSet accountCreation(String username, String password, String email) {
		ResultSet rset = null;
		String sql = null;

		try {
			// create a Statement and an SQL string for the statement

			sql = "INSERT INTO user VALUES(?, ?, ?)";
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
     * @return ResultSet containing
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
     * @return
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
     * @return
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
     * Deletes a comment with the provided comment ID
     * @param comment_id the id of the comment to delete
     * @return
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
     * @param movie The name of the movie the review is about
     * @param image An image associated with the movie
     * @param rating The rating of the movie given by the reviewer
     * @return
     */
    // NOT SURE HOW TO DO IMAGE, have it as a string thats a link maybe?
    public ResultSet postReview(String movie, String image, String rating) {
        ResultSet rset = null;
        String sql = null;

        try {
            // create a Statement and an SQL string for the statement

            sql = "INSERT INTO review VALUES(review_id, ?, ?, ?, post_date, critic)";
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.clearParameters();
            pstmt.setString(1, movie); // set the 1 parameter
            pstmt.setString(1, image); // set the 2 parameter
            pstmt.setString(1, rating); // set the 3 parameter

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

            sql = "SELECT post_date, movie, rating FROM movie WHERE movie=?";
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
     * @return
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
            BufferedImage image =ImageIO.read(input);

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
