import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnector {

    public static Connection getCon () throws ClassNotFoundException, SQLException {
        Connection con = null;

        System.out.println("Loading the driver");
        Class.forName("com.mysql.jdbc.Driver");  // loading the MySQL driver
        System.out.println("Connecting");

        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/invoicejava","root","");

        // odbc_ex is the data source name (DSN) -- Access related

        System.out.println("Connected");
        return con;
    }

}
