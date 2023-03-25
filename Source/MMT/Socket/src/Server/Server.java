package Server;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import org.json.JSONObject;
import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.sql.*;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class Server {
    Connection db;
    private JPanel panel1;


    public static Connection dbConn() throws SQLException {
        Connection conn = null;
        //Nap driver
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        //Tao chuoi connection
        String connectionUrl = "jdbc:sqlserver://DESKTOP-ASQVJCR:1433;" + "databaseName=E-booking;user=binh;password=123;encrypt = true;trustServerCertificate=true";
        //Connect db
        conn = DriverManager.getConnection(connectionUrl);
        return conn;
    }

    public static boolean checkDangki(String username, String password, String nganhang, Connection conn) throws SQLException {
        if (username.length() < 5 || !(username != null && username.matches("^[a-zA-Z0-9]*$"))) {
            return false;
        }

        if (password.length() < 3) {
            return false;
        }

        if (nganhang.length() != 10 || !(nganhang.matches("^[0-9]*$"))) {
            return false;
        }

        String sql = String.format("SELECT COUNT(username) as total FROM Taikhoan WHERE username = '%s'", username);
        Statement statement = conn.createStatement();
        ResultSet rs = statement.executeQuery(sql);
        rs.next();
        int count = rs.getInt("total");
        System.out.print(count);
        if (count != 0) {
            return false;
        }

        return true;
    }

    public static boolean checkDangnhap(String username, String password, Connection conn) throws SQLException {
        String sql1 = String.format("SELECT COUNT(username) as total FROM Taikhoan WHERE username = '%s'", username);
        Statement statement1 = conn.createStatement();
        ResultSet rs = statement1.executeQuery(sql1);
        rs.next();
        int count = rs.getInt("total");
        if (count == 0) {
            return false;
        }
        String sql2 = String.format("SELECT * FROM Taikhoan WHERE username = '%s'", username);
        Statement statement2 = conn.createStatement();
        ResultSet pass = statement2.executeQuery(sql2);
        pass.next();
        String data = pass.getString("password");
        data = data.replaceAll("\\s+", "");
        if (Objects.equals(password, data)) {
            return true;
        }
        return false;
    }

    public static void main(String[] args) throws IOException, SQLException, ParseException {
        JFrame frame = new JFrame("Server");
        Server p = new Server();
        frame.setContentPane(p.panel1);
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(500, 500));
        frame.pack();
        frame.setVisible(true);


        ServerSocket welcomeSocket = new ServerSocket(5000);

        while (true) {
            try {
                //ket noi data base
                Connection conn = dbConn();
                //
                JSONObject send = new JSONObject();
                //chờ yêu cầu từ client
                Socket connectionSocket = welcomeSocket.accept();


                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                String data = inFromClient.readLine();
                System.out.println(data);
                String thongbao = "";
                boolean dangki = false;
                boolean dangnhap = false;

                JSONObject json = new JSONObject(data);
                if (Objects.equals(json.getString("type"), "0")) {
                    String username = json.getString("username");
                    String password = json.getString("password");
                    dangnhap = checkDangnhap(username, password, conn);
                    if (dangnhap) {
                        thongbao = "Dang nhap thanh cong";
                    } else {
                        thongbao = "Dang nhap that bai";
                    }
                    send.put("isSuccess", dangnhap);
                    send.put("thongbao", thongbao);
                }
                else if (Objects.equals(json.getString("type"), "1")) {
                    String username = json.getString("username");
                    String password = json.getString("password");
                    String nganhang = json.getString("manganhang");
                    dangki = checkDangki(username, password, nganhang, conn);

                    if (dangki) {
                        String sql = String.format("INSERT INTO Taikhoan (username, password, manganhang) VALUES ('%1$s', '%2$s', '%3$s')", username, password, nganhang);
                        Statement statement = conn.createStatement();
                        statement.execute(sql);
                        thongbao = "Dang ki thanh cong";
                    } else {
                        thongbao = "Dang ki khong thanh cong";
                    }
                    send.put("isSuccess", dangki);
                    send.put("thongbao", thongbao);
                } else if (Objects.equals(json.getString("type"), "2")) {
                    String username = json.getString("username");
                    String ngayvaoo = json.getString("ngayvaoo");
                    String ngayroidi = json.getString("ngaydi");
                    String makhachsan = json.getString("makhachsan");
                    String ghichu = json.getString("ghichu");
                    String danhsachphong = json.getString("sophong");
                    String ngaydat = json.getString("ngaydat");
                    String[] splitArray = danhsachphong.split(",");
                    if(ngayvaoo.compareTo(ngayroidi) <= 0) {
                        try {
                            int[] sophong = new int[splitArray.length];
                            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                            LocalDate date1 = LocalDate.parse(ngayvaoo, dtf);
                            LocalDate date2 = LocalDate.parse(ngayroidi, dtf);
                            long days = ChronoUnit.DAYS.between(date1, date2);

                            if (days == 0) {
                                days = 1;
                            }


                            for (int i = 0; i < splitArray.length; i++) {
                                sophong[i] = Integer.parseInt(splitArray[i]);
                                String sql1 = String.format("SELECT * from Danhsachphong WHERE Makhachsan = '%1$s' AND Sophong = '%2$d'",
                                        makhachsan, sophong[i]);
                                Statement statement1 = conn.createStatement();
                                statement1.execute(sql1);
                                ResultSet result = statement1.executeQuery(sql1);
                                result.next();
                                long gia = days * result.getInt("Gia");
                                result.close();
                                String sql2 = String.format
                                        ("INSERT INTO Donhang (Username, Ngayvaoo, Ngayroidi,Makhachsan,Ghichu,Sophong,Ngaydat,Tongtien) " +
                                                        "VALUES ('%1$s', '%2$s', '%3$s','%4$s','%5$s','%6$d','%7$s','%8$d')",
                                                username, ngayvaoo, ngayroidi, makhachsan, ghichu, sophong[i], ngaydat, gia);
                                Statement statement2 = conn.createStatement();
                                statement2.execute(sql2);
                                statement2.close();
                                thongbao = "Dat phong thanh cong";
                                send.put("isSuccess", true);
                                send.put("thongbao", thongbao);
                            }
                        } catch (NumberFormatException | DateTimeParseException ex) {
                            thongbao = "Dat phong khong thanh cong";
                            send.put("isSuccess", false);
                            send.put("thongbao", thongbao);
                        }
                    }
                    else {
                        thongbao = "Dat phong khong thanh cong";
                        send.put("isSuccess", false);
                        send.put("thongbao", thongbao);
                    }
                } else if (Objects.equals(json.getString("type"), "3")) {
                    String username = json.getString("username");
                    int madonhang = json.getInt("Madonhang");
                    String huy = json.getString("ngayhuy");
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    LocalDateTime ngayhuy = LocalDateTime.parse(huy, dtf);

                    String sql3 = String.format("Select * " +
                            "From [E-booking].[dbo].[Donhang] " +
                            "INNER JOIN [E-booking].[dbo].[Danhsachphong] " +
                            "ON Donhang.Makhachsan = Danhsachphong.Makhachsan AND Donhang.Sophong = Danhsachphong.Sophong " +
                            "Where Username = '%1$s' AND Madonhang = '%2$d';", username, madonhang);

                    Statement statement2 = conn.createStatement();
                    statement2.execute(sql3);
                    ResultSet rs = statement2.executeQuery(sql3);
                    rs.next();
                    Timestamp ngaydat = rs.getTimestamp("Ngaydat");
                    rs.close();
                    statement2.close();
                    LocalDateTime ngaydatphong = LocalDateTime.parse((ngaydat.toString()).substring(0, 19), dtf);
                    long second = ChronoUnit.SECONDS.between(ngaydatphong, ngayhuy);
                    if (second < 86400) {
                        thongbao = "Huy thanh cong";
                        send.put("isSuccess", true);
                        send.put("thongbao", thongbao);
                        String sql4 = String.format("Delete From [E-booking].[dbo].[Donhang]" +
                                "Where Username = '%1$s' AND Madonhang = '%2$d';", username, madonhang);
                        Statement statement3 = conn.createStatement();
                        statement3.execute(sql4);
                        statement3.close();
                    } else {
                        thongbao = "Huy khong thanh cong";
                        send.put("isSuccess", false);
                        send.put("thongbao", thongbao);
                    }
                }

                System.out.println(send);
                DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                outToClient.writeBytes(send.toString() + '\n');
            }catch(SocketException | SQLServerException ignored){

            }


        }
    }
}



