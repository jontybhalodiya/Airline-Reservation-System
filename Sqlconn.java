import java.sql.*;

public class Sqlconn{
    public static void main(String[] args){
        String url = "jdbc:mysql://localhost:3307/placement";
        String user = "root";
        String password = "root";

        try{

            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connection Successful");

            Statement st = conn.createStatement();
            String sql = "select * from user";

            ResultSet result = st.executeQuery(sql);

            while(result.next()){
                System.out.println(result.getString("name"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}