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

    public void printEmployees() throws SQLException {
        String sql = "SELECT * FROM employee";
        Statement stmt = conn.createStatement();
        ResultSet rset = stmt.executeQuery(sql);
        while(rset.next()) {
            System.out.printf("%s %s\n",
                    rset.getString(1), rset.getString(3));
        }
    }

    @Override
    public void close() throws SQLException {
        if( conn != null )
            conn.close();
        conn = null;

        System.out.println("Connection closed.");

    }
}
