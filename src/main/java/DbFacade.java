import javax.sound.midi.Soundbank;
import java.sql.*;

public class DbFacade implements AutoCloseable {

    private Connection conn;

    public DbFacade() throws SQLException {
        String url = "jdbc:mariadb://mal.cs.plu.edu:3306/367_2018_company";
        String username = "cyan_2018";
        String password = "367rocks!";

        conn = DriverManager.getConnection(url, username, password);

        System.out.println("Connection opened.");
    }
    
    
/*
    public void printEmployees() throws SQLException {
        String sql = "SELECT * FROM employee";
        Statement stmt = conn.createStatement();
        ResultSet rset = stmt.executeQuery(sql);
        while(rset.next()) {
            System.out.printf("%s %s\n",
                    rset.getString(1), rset.getString(3));
        }
    }
*/
    @Override
    public void close() throws SQLException {
        if( conn != null )
            conn.close();
        conn = null;

        System.out.println("Connection closed.");

    }
    
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
}
