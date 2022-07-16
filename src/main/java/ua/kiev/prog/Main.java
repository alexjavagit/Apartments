package ua.kiev.prog;

import java.sql.*;
import java.util.Scanner;

public class Main {
    static final String DB_CONNECTION = "jdbc:mysql://localhost:3306/my_db?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
    static final String DB_USER = "your_user";
    static final String DB_PASSWORD = "your_password";

    static Connection conn;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        try {
            try {
                // create connection
                conn = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
                initDB();
                addApartments();

                while (true) {
                    System.out.println("1: view all apartments");
                    System.out.println("2: filter apartments by square");
                    System.out.println("3: filter apartments by rooms count");
                    System.out.println("4: filter apartments by price");
                    System.out.print("-> ");

                    String s = sc.nextLine();
                    switch (s) {
                        case "1":
                            viewApartments();
                            break;
                        case "2":
                            viewApartmentsBySquare();
                            break;
                        case "3":
                            viewApartmentsByRCount();
                            break;
                        case "4":
                            viewApartmentsByPrice();
                            break;
                        default:
                            return;
                    }
                }
            } finally {
                sc.close();
                if (conn != null) conn.close();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return;
        }
    }

    private static void initDB() throws SQLException {
        Statement st = conn.createStatement();
        try {
            st.execute("DROP TABLE IF EXISTS Apartments");
            st.execute("CREATE TABLE Apartments (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, district VARCHAR(200), address VARCHAR(250), square INT(3), rcount int(2), price int(12))");
        } finally {
            st.close();
        }
    }

    private static void addApartments() throws SQLException {
        PreparedStatement ps = conn.prepareStatement("INSERT INTO Apartments (district, address, square, rcount, price) VALUES(?, ?, ?, ?, ?)");
        try {
            for (int i = 1; i<=100; i++) {
                ps.setString(1, "District " + i);
                ps.setString(2, "Address " + i);
                ps.setInt( 3, i + 50);
                ps.setInt(4, i);
                ps.setInt(5, Integer.parseInt(Integer.toString(i)+"0000"));
                ps.executeUpdate();
            }
        } finally {
            ps.close();
        }
    }


    private static void viewApartments() throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM Apartments");
        showAppartments(ps);
    }

    private static void viewApartmentsBySquare() throws SQLException {
        Scanner sc = new Scanner(System.in);
        System.out.print("Show Apartments with square from:");
        int from = Integer.parseInt(sc.nextLine());
        System.out.print("to:");
        int to = Integer.parseInt(sc.nextLine());
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM Apartments WHERE square>=? and square <=?");
        ps.setInt(1, from);
        ps.setInt(2, to);

        showAppartments(ps);
    }

    private static void viewApartmentsByRCount() throws SQLException {
        Scanner sc = new Scanner(System.in);
        System.out.print("Show Apartments with rooms count from:");
        int from = Integer.parseInt(sc.nextLine());
        System.out.print("to:");
        int to = Integer.parseInt(sc.nextLine());
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM Apartments WHERE rcount>=? and rcount <=?");
        ps.setInt(1, from);
        ps.setInt(2, to);
        showAppartments(ps);
    }

    private static void viewApartmentsByPrice() throws SQLException {
        Scanner sc = new Scanner(System.in);
        System.out.print("Show Apartments with price from:");
        int from = Integer.parseInt(sc.nextLine());
        System.out.print("to:");
        int to = Integer.parseInt(sc.nextLine());
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM Apartments WHERE price>=? and price <=?");
        ps.setInt(1, from);
        ps.setInt(2, to);
        showAppartments(ps);
    }

    private static void showAppartments(PreparedStatement ps) throws SQLException {
        try {
            // table of data representing a database result set,
            ResultSet rs = ps.executeQuery();
            try {
                // can be used to get information about the types and properties of the columns in a ResultSet object
                ResultSetMetaData md = rs.getMetaData();

                for (int i = 1; i <= md.getColumnCount(); i++)
                    System.out.print(md.getColumnName(i) + "\t\t");
                System.out.println();

                while (rs.next()) {
                    for (int i = 1; i <= md.getColumnCount(); i++) {
                        System.out.print(rs.getString(i) + "\t\t");
                    }
                    System.out.println();
                }
            } finally {
                rs.close(); // rs can't be null according to the docs
            }
        } finally {
            ps.close();
        }
    }
}
