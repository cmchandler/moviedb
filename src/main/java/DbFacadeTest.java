
import java.sql.*; 
import org.junit.*;
import static org.junit.Assert.*;
import java.util.*;

public class DbFacadeTest {

	@Test 
	public void testOpenClose() throws SQLException {

		// This will open and close the connection.  It will be opened by the constructor,
		// and the close() method will automatically be called when exiting the try block.
		try( DbFacade db = new DbFacade() ) {

		}
		
	}
	
	/*
	CODE FOR PROJECT GOES HERE
	*/
	@Test
	public void testAccountCreation() throws SQLException {
		ArrayList<String> username = new ArrayList<>(Arrays.asList("user1"));
		ArrayList<String> password = new ArrayList<>(Arrays.asList( "password1" ));
		ArrayList<String> email = new ArrayList<>(Arrays.asList( "test@whatever.com" ));

		try( DbFacade db = new DbFacade() ) {
			ResultSet rset = db.accountCreation("user1", "password1", "test@whatever.com");

			// Use a loop to read all rows
			List<String> actualUsername = new ArrayList<>();
			List<String> actualPassword = new ArrayList<>();
			List<String> actualEmail = new ArrayList<>();
			while( rset.next() ) {
				actualUsername.add(rset.getString(1) );
				actualPassword.add(rset.getString(2) );
				actualEmail.add(rset.getString(3) );
			}

			// Compare the lists to the expected results
			assertTrue(actualUsername.contains(username.get(0)));
			assertTrue(actualPassword.contains(password.get(0)));
			assertTrue(actualEmail.contains(email.get(0)));

		}
	}

	@Test
	public void testGrantAdministrator() throws SQLException {
		ArrayList<String> username = new ArrayList<>(Arrays.asList("user1"));

		try( DbFacade db = new DbFacade() ) {
			ResultSet rset = db.grantAdministrator("user1");

			// Use a loop to read all rows
			List<String> actualUsername = new ArrayList<>();
			while( rset.next() ) {
				actualUsername.add(rset.getString(1) );
			}

			// Compare the lists to the expected results
			assertTrue(actualUsername.contains(username.get(0)));

		}
	}

	@Test
	public void testGrantCritic() throws SQLException {
		ArrayList<String> username = new ArrayList<>(Arrays.asList("cbryers12"));

		try( DbFacade db = new DbFacade() ) {
			ResultSet rset = db.grantAdministrator("cbryers12");

			// Use a loop to read all rows
			List<String> actualUsername = new ArrayList<>();
			while( rset.next() ) {
				actualUsername.add(rset.getString(1) );
			}

			// Compare the lists to the expected results
			assertTrue(actualUsername.contains(username.get(0)));

		}
	}

	// not sure about this one or what strings are added in the addComment method
	@Test
	public void testAddComment() throws SQLException {
		ArrayList<String> content = new ArrayList<>(Arrays.asList("whatever the comment is here"));

		try( DbFacade db = new DbFacade() ) {
			ResultSet rset = db.addComment("whatever the comment is here");

			// Use a loop to read all rows
			List<String> actualContent = new ArrayList<>();
			while( rset.next() ) {
				actualContent.add(rset.getString(2) );
			}

			// Compare the lists to the expected results
			assertTrue(actualContent.contains(content.get(0)));

		}
	}

	@Test
	public void testDeleteComment() throws SQLException {
		ArrayList<String> comment_id = new ArrayList<>(Arrays.asList("1"));

		try( DbFacade db = new DbFacade() ) {
			ResultSet rset = db.deleteComment("1");

			// Use a loop to read all rows
			List<String> actualComment_id = new ArrayList<>();
			while( rset.next() ) {
				actualComment_id.add(rset.getString(1) );
			}

			// Compare the lists to the expected results
			// make sure the actual doesn't contain that id because it got removed
			assertTrue(!actualComment_id.contains(comment_id.get(0)));

		}
	}

	@Test
	public void testPostReview() throws SQLException {
		ArrayList<String> movie = new ArrayList<>(Arrays.asList("movie"));
		ArrayList<String> image = new ArrayList<>(Arrays.asList("image"));
		ArrayList<String> rating = new ArrayList<>(Arrays.asList("rating"));

		try( DbFacade db = new DbFacade() ) {
			ResultSet rset = db.postReview("movie", "image", "rating");

			// Use a loop to read all rows
			List<String> actualMovie = new ArrayList<>();
			List<String> actualImage = new ArrayList<>();
			List<String> actualRating = new ArrayList<>();
			while( rset.next() ) {
				actualMovie.add(rset.getString(2) );
				actualImage.add(rset.getString(3) );
				actualRating.add(rset.getString(4) );
			}

			// Compare the lists to the expected results
			assertTrue(actualMovie.contains(movie.get(0)));
			assertTrue(actualImage.contains(movie.get(0)));
			assertTrue(actualRating.contains(movie.get(0)));

		}
	}

	@Test
	public void testListMovieReviews() throws SQLException {
		ArrayList<String> movies = new ArrayList<>(Arrays.asList("movie"));

		try( DbFacade db = new DbFacade() ) {
			ResultSet rset = db.listMovieReviews("movie");

			// Use a loop to read all rows
			List<String> actualMovies = new ArrayList<>();
			while( rset.next() ) {
				actualMovies.add(rset.getString(1) );
			}

			// Compare the lists to the expected results
			assertTrue(actualMovies.contains(movies.get(0)));

		}
	}

	@Test
	public void testDeleteUser() throws SQLException {
		ArrayList<String> username = new ArrayList<>(Arrays.asList("user1"));

		try( DbFacade db = new DbFacade() ) {
			ResultSet rset = db.deleteUser("user1");

			// Use a loop to read all rows
			List<String> actualUsername = new ArrayList<>();
			while( rset.next() ) {
				actualUsername.add(rset.getString(1) );
			}

			// Compare the lists to the expected results
			// actualUser doesn't contain username
			assertTrue(!actualUsername.contains(username.get(0)));

		}
	}
	
}
