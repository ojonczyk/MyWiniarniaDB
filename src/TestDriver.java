import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JOptionPane;

public class TestDriver {
	public void Test(String[] args) {
		Statement stmt = null;
		ResultSet rs = null;
		try {
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/winiarnia", "root", "password");
			stmt = con.createStatement(); 
			rs = stmt.executeQuery("SELECT PRACOWNICY.IMIE FROM PRACOWNICY");
			while (rs.next()) {
			  System.out.println(rs.getString("IMIE"));
			}

		} catch (SQLException e) {
			System.out.println("Connection Error");
			e.printStackTrace();
		}
	}
	public static Connection getConnection(){
		try {
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/winiarnia?autoReconnect=true&useSSL=false", "root", "password");
			return con;
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null,"Cannot connect to the database. Check your connection.","DB Error",JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		} 
		return null;
	}
}