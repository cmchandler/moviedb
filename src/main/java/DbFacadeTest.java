
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
	
	
	/*
	EVERYTHING BELOW THIS IS EXAMPLES
	*/

	@Test 
	public void testGetNameSalaryResearch() throws SQLException {
		List<String> lastNames = Arrays.asList( "English", "Narayan", "Smith", "Wong" );
		List<String> firstNames = Arrays.asList( "Joyce", "Ramesh", "John", "Franklin" );
		List<String> salaries = Arrays.asList( "25000.00", "38000.00", "30000.00", "40000.00" );

		try( DbFacade db = new DbFacade() ) {
			ResultSet rset = db.getNameSalaryResearch();

			// Use a loop to read all rows
			List<String> actualFirstNames = new ArrayList<>();
			List<String> actualLastNames = new ArrayList<>();
			List<String> actualSalaries = new ArrayList<>();
			while( rset.next() ) {
				actualLastNames.add(rset.getString(1) );
				actualFirstNames.add(rset.getString(2) );
				actualSalaries.add(rset.getString(3) );
			}

			// Compare the lists to the expected results
			assertEquals(salaries, actualSalaries);
			assertEquals(lastNames, actualLastNames);
			assertEquals(firstNames, actualFirstNames);
		}
	}

	@Test 
	public void testLastNameStartsWithW() throws SQLException {
		List<String> lastNames = Arrays.asList( "Wallace", "Wong", "Wallis" );
		List<String> firstNames = Arrays.asList( "Jennifer", "Franklin", "Evan" );
		List<Integer> dnos = Arrays.asList( 4,5,7 );

		try( DbFacade db = new DbFacade() ) {
			ResultSet rset = db.lastNameStartsWithW();

			// Use a loop to read all rows
			List<String> actualFirstNames = new ArrayList<>();
			List<String> actualLastNames = new ArrayList<>();
			List<Integer> actualDnos = new ArrayList<>();
			while( rset.next() ) {
				actualDnos.add(rset.getInt(1) );
				actualLastNames.add(rset.getString(2) );
				actualFirstNames.add( rset.getString(3) );
			}

			// Compare the lists to the expected results
			assertEquals(dnos, actualDnos);
			assertEquals(lastNames, actualLastNames);
			assertEquals(firstNames, actualFirstNames);
		}
	}
	
	@Test 
	public void testEmployeesByDno() throws SQLException {
		List<String> lastNames = Arrays.asList("Jabbar", "Wallace", "Zelaya");
		List<String> firstNames = Arrays.asList("Ahmad", "Jennifer", "Alicia");

		try( DbFacade db = new DbFacade() ) {
			ResultSet rset = db.employeesByDNO(4);

			// Use a loop to read all rows and store in lists
			ArrayList<String> actualFirstNames = new ArrayList<>();
			ArrayList<String> actualLastNames = new ArrayList<>();
			while( rset.next() ) {
				actualLastNames.add(rset.getString(1));
				actualFirstNames.add(rset.getString(2));
			}
			
			// Compare the lists to the expected results
			assertEquals(lastNames, actualLastNames);
			assertEquals(firstNames, actualFirstNames);
		}
	}

	@Test
	public void testNameProjectHours() throws SQLException {
	    // need to populate lists with correct data!! this is for comparison
		List<String> lastNames = Arrays.asList();
        List<String> firstNames = Arrays.asList();
        List<String> projectNumber = Arrays.asList();
        List<String> hours = Arrays.asList();

        try( DbFacade db = new DbFacade() ) {
            ResultSet rset = db.nameProjectHours("1");

            // Use a loop to read all rows and store in lists
            ArrayList<String> actualFirstNames = new ArrayList<>();
            ArrayList<String> actualLastNames = new ArrayList<>();
            ArrayList<String> actualProjectNumber = new ArrayList<>();
            ArrayList<String> actualHours = new ArrayList<>();

            while( rset.next() ) {
                actualLastNames.add(rset.getString(1));
                actualFirstNames.add(rset.getString(2));
                actualProjectNumber.add(rset.getString(3));
                actualHours.add(rset.getString(4));
            }

            // Compare the lists to the expected results
            assertEquals(lastNames, actualLastNames);
            assertEquals(firstNames, actualFirstNames);
            assertEquals(projectNumber, actualProjectNumber);
            assertEquals(hours, actualHours);
        }
	}
}
