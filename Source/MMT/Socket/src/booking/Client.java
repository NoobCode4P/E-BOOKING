package booking;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import org.json.JSONObject;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

public class Client {
    private JPanel panelMain;
    private JButton button_login;
    private JTextField textField_username;
    private JPasswordField textField_password;
    private JButton button_signup;
    private JPanel JPanel_input;
    private JPanel JPanel_button;
    private JPanel Login;
    private JPanel Signup;
    private JTextField textField_manganhang;
    private JButton button_register;
    private JButton button_gotologin;
    private JPanel JPanel_buttonsignup;
    private JButton danhSachButton;
    private JButton traCuuButton;
    private JPanel Menu;
    private JPanel Danhsach;
    private JTable Khachsan;
    private JButton menuButton;
    private JButton donDatHangButton;
    private JTextField tenkhachsan;
    private JFormattedTextField ngayvaoo;
    private JFormattedTextField ngayroidi;
    private JPanel Tracuu;
    private JButton tracuu;
    private JButton menuButton1;
    private JTable bangtracuu;
    private JScrollPane Tracuuvadathang;
    private JTable bangdatphong;
    private JButton themphongButton;
    private JButton xoaphong;
    private JButton datphong;
    private JTextField ghichu;
    private JTextField madonhang;
    private JButton huydon;
    private JTable Donhang;
    private JPanel Huydon;
    private JButton menuButton3;

    String User;

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }


    public class Khachsan{
        String ma;
        String ten;
        public Khachsan(String ma,String ten){
            this.ma = ma;
            this.ten = ten;
        }
    };

    public class Phong{
        String makhachsan;
        int sophong;
        String loaiphong;
        int gia;
        public Phong(String makhachsn, int sophong, String loaiphong, int gia ){
            this.makhachsan = makhachsan;
            this.sophong = sophong;
            this.loaiphong = loaiphong;
            this.gia = gia;
        }
    };

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

    ArrayList<Khachsan> list = new ArrayList<>();

    public void fillDataToTable(){
        String[] columns = {"Ma khach san", "Ten khach san"};
        DefaultTableModel tblModel = (DefaultTableModel) Khachsan.getModel();

        tblModel.setColumnIdentifiers(columns);
        tblModel.setRowCount(0);
        for(int i = 0; i < list.size(); i++){
            tblModel.addRow(new Object[]{list.get(i).ma, list.get(i).ten});
        }
        Khachsan.setEnabled(false);
    }

    public void getAllDB() throws SQLException {
        //Connection
        Connection db = dbConn();
        try {
            String sql = "Select * from Khachsan";
            Statement st = db.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while(rs.next()){
                String makhachsan = rs.getString("Makhachsan");
                String tenkhachsan = rs.getString("Tenkhachsan");
                list.add(new Khachsan(makhachsan,tenkhachsan));
            }
            st.close();
            db.close();
        }catch(Exception ex){
            ex.printStackTrace();;
        }
    }

    public void traCuu(String tenkhachsan){

    }
    public Client() {
        Signup.setVisible(false);
        Menu.setVisible(false);
        Danhsach.setVisible(false);
        Tracuuvadathang.setVisible(false);
        Huydon.setVisible(false);
        button_login.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = textField_username.getText();
                String password = textField_password.getText();


                try {
                    Socket clientSocket = new Socket("127.0.0.1", 5000);
                    JSONObject json = new JSONObject();
                    json.put("username", username);
                    json.put("password", password);
                    json.put("type", "0");

                    BufferedReader input =
                            new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                    BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    outToServer.writeBytes(json.toString()+'\n');
                    String thongbao = inFromServer.readLine();
                    JSONObject receive = new JSONObject(thongbao);
                    boolean isSuccess = receive.getBoolean("isSuccess");
                    String message = receive.getString("thongbao");
                    if(isSuccess) {
                        User = username;
                        Login.setVisible(false);
                        Signup.setVisible(false);
                        Menu.setVisible(true);
                        try {
                            getAllDB();
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }
                        ;
                        fillDataToTable();
                    }
                    JOptionPane.showMessageDialog(null, message);
                    clientSocket.close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

            }
        });

        button_signup.addActionListener(new ActionListener() {
            @Override

            public void actionPerformed(ActionEvent e) {
               JPanel_button.setVisible(false);
               Signup.setVisible(true);
            }
        });
        button_gotologin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel_button.setVisible(true);
                Signup.setVisible(false);
            }
        });
        button_register.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = textField_username.getText();
                String password = textField_password.getText();
                String nganhang = textField_manganhang.getText();
                System.out.print(password.length());

               Socket clientSocket;
                try {
                    clientSocket = new Socket("127.0.0.1", 5000);
                    JSONObject json = new JSONObject();
                    json.put("username", username);
                    json.put("password", password);
                    json.put("manganhang", nganhang);
                    json.put("type", "1");
                   // InputStreamReader input = new InputStreamReader(clientSocket.getInputStream());

                    BufferedReader input =
                            new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                    BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    outToServer.writeBytes(json.toString()+'\n');
                    String thongbao = inFromServer.readLine();
                    JSONObject receive = new JSONObject(thongbao);
                    System.out.print(thongbao);
                    boolean isSuccess = receive.getBoolean("isSuccess");
                    String message = receive.getString("thongbao");
                    JOptionPane.showMessageDialog(null, message);
                    clientSocket.close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

            }
        });
        danhSachButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Menu.setVisible(false);
                Danhsach.setVisible(true);
            }
        });
        traCuuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Menu.setVisible(false);
                Tracuuvadathang.setVisible(true);
                Tracuu.setVisible(true);

            }
        });
        menuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Danhsach.setVisible(false);
                Menu.setVisible(true);
                Tracuuvadathang.setVisible(false);
            }
        });
        class Donhang{
            int madonhang;
            String makhachsan;
            int sophong;
            String loaiphong;
            String ngayvaoo;
            String ngayroidi;
            int tongtien;
            String ngaydat;
            public Donhang
                    (int madonhang, String makhachsan, int sophong, String loaiphong, String ngayvaoo, String ngayroidi, int tongtien, String ngaydat){
                this.madonhang = madonhang;
                this.makhachsan = makhachsan;
                this.sophong = sophong;
                this.loaiphong = loaiphong;
                this.ngayvaoo = ngayvaoo;
                this.ngayroidi = ngayroidi;
                this.tongtien = tongtien;
                this.ngaydat = ngaydat;
            }
        };

        ArrayList<Donhang> allBill = new ArrayList<>();
        donDatHangButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Menu.setVisible(false);
                Huydon.setVisible(true);
                allBill.removeAll(allBill);
                Connection db = null;
                try {
                    db = dbConn();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }

                try {
                    String sql = String.format("Select * " +
                            "From [E-booking].[dbo].[Donhang]" +
                            "INNER JOIN [E-booking].[dbo].[Danhsachphong]" +
                            "ON Donhang.Makhachsan = Danhsachphong.Makhachsan AND Donhang.Sophong = Danhsachphong.Sophong "+
                            "Where Username = '%s';",User);
                    Statement st = db.createStatement();
                    ResultSet rs = st.executeQuery(sql);
                    while(rs.next()){
                        int madonhang = rs.getInt("Madonhang");
                        String makhachsan = rs.getString("Makhachsan");
                        int sophong = rs.getInt("Sophong");
                        String loaiphong = rs.getString("Loaiphong");
                        String ngayvaoo = (rs.getDate("Ngayvaoo")).toString();
                        String ngayroidi = (rs.getDate("Ngayroidi")).toString();
                        Timestamp ngay = rs.getTimestamp("Ngaydat");
                        String ngaydat = (ngay.toString()).substring(0,19);
                        int tongtien = rs.getInt("Tongtien");
                        allBill.add(new Donhang(madonhang,makhachsan,sophong,loaiphong,ngayvaoo,ngayroidi,tongtien,ngaydat));
                    }
                    st.close();
                    db.close();
                }catch(Exception ex){
                    ex.printStackTrace();;
                }
                String[] columns = {"Ma don hang", "Ma khach san", "So phong","Loai phong","Ngay vao o","Ngay roi di","Tong tien","Ngay dat"};
                DefaultTableModel tblModel = (DefaultTableModel) Donhang.getModel();

                tblModel.setColumnIdentifiers(columns);
                tblModel.setRowCount(0);
                for(int i = 0; i < allBill.size(); i++){
                    tblModel.addRow(new Object[]
                            {
                                    allBill.get(i).madonhang,
                                    allBill.get(i).makhachsan,
                                    allBill.get(i).sophong,
                                    allBill.get(i).loaiphong,
                                    allBill.get(i).ngayvaoo,
                                    allBill.get(i).ngayroidi,
                                    allBill.get(i).tongtien,
                                    allBill.get(i).ngaydat,
                            });
                }
                Khachsan.setEnabled(false);
            }
        });


        ArrayList<Phong> List = new ArrayList<>();
        tracuu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                    String makhachsan = tenkhachsan.getText();
                    String ngayvao = ngayvaoo.getText();
                    String ngaydi = ngayroidi.getText();
                    List.removeAll(List);
                    if(ngayvao.length() == 0 || ngaydi.length() == 0 || ngayvao.compareTo(ngaydi) > 0){
                        JOptionPane.showMessageDialog(null, "Du lieu khong hop le");
                    }
                    else {
                        try {
                            Connection tracuu = dbConn();
                            try {
                                String sql = String.format("SELECT Danhsachphong.Makhachsan, Danhsachphong.Sophong, Danhsachphong.Loaiphong, Danhsachphong.Gia, Khachsan.Tenkhachsan\n" +
                                        "FROM [E-booking].[dbo].[Danhsachphong]\n" +
                                        " INNER JOIN [E-booking].[dbo].[Khachsan]\n" +
                                        " ON Danhsachphong.Makhachsan=Khachsan.Makhachsan\n" +
                                        "Where Danhsachphong.Makhachsan = '%1$s' OR Khachsan.Tenkhachsan = '%1$s'\n" +
                                        "EXCEPT\n" +
                                        "SELECT Danhsachphong.Makhachsan, Danhsachphong.Sophong,Danhsachphong.Loaiphong,Danhsachphong.Gia,Khachsan.Tenkhachsan\n" +
                                        " FROM [E-booking].[dbo].[Danhsachphong]\n" +
                                        " INNER JOIN [E-booking].[dbo].[Khachsan]\n" +
                                        " ON Danhsachphong.Makhachsan=Khachsan.Makhachsan\n" +
                                        " INNER JOIN [E-booking].[dbo].[Donhang]\n" +
                                        " ON Danhsachphong.Sophong = Donhang.Sophong And Danhsachphong.Makhachsan = Donhang.Makhachsan\n" +
                                        " Where (((Ngayvaoo < '%2$s' AND Ngayroidi > '%2$s') " +
                                        " AND  (Ngayvaoo < '%3$s' OR Ngayroidi > '%3$s'))" +
                                        " OR (Ngayvaoo > '%2$s' AND Ngayroidi < '%3$s'))" +
                                        " AND (Danhsachphong.Makhachsan = '%1$s' OR Khachsan.Tenkhachsan = '%1$s');", makhachsan, ngayvao, ngaydi);
                                Statement st = tracuu.createStatement();
                                ResultSet rs = st.executeQuery(sql);
                                while (rs.next()) {
                                    String Makhachsan = rs.getString("Makhachsan");
                                    int Sophong = rs.getInt("Sophong");
                                    String Loaiphong = rs.getString("Loaiphong");
                                    int Gia = rs.getInt("Gia");
                                    List.add(new Phong(Makhachsan, Sophong, Loaiphong, Gia));
                                }
                                st.close();
                                tracuu.close();
                                String[] columns = {"So phong", "Loaiphong", "Gia"};
                                DefaultTableModel tblModel = (DefaultTableModel) bangtracuu.getModel();
                                tblModel.setColumnIdentifiers(columns);

                                //bangtracuu.addRowSelectionInterval(1, 2);
                                tblModel.setRowCount(0);
                                tblModel.addRow
                                        (new Object[]{"So phong", "Loaiphong", "Gia"});
                                for (int i = 0; i < List.size(); i++) {
                                    tblModel.addRow
                                            (new Object[]{List.get(i).sophong, List.get(i).loaiphong, List.get(i).gia});
                                }
                            } catch (SQLServerException ex) {
                                JOptionPane.showMessageDialog(null, "Du lieu khong hop le");
                            }
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
            }
        });
        menuButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Tracuu.setVisible(false);
                Menu.setVisible(true);
                Tracuuvadathang.setVisible(false);
            }
        });

        final int[] Sophong = {0};
        themphongButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Sophong[0]++;
                String[] columns = {"So phong"};
                DefaultTableModel tblModel = (DefaultTableModel) bangdatphong.getModel();
                tblModel.setColumnIdentifiers(columns);
                tblModel.setRowCount(Sophong[0]);
                /*for(int count = 0; count < bangdatphong.getRowCount(); count++) {
                    bangdatphong.editCellAt(count,0);
                }*/

            }
        });
        xoaphong.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(Sophong[0] > 0) {
                    Sophong[0]--;
                }
                String[] columns = {"So phong"};
                DefaultTableModel tblModel = (DefaultTableModel) bangdatphong.getModel();
                tblModel.setColumnIdentifiers(columns);
                tblModel.setRowCount(Sophong[0]);
            }
        });
        datphong.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = User;
                String ngayvao = ngayvaoo.getText();
                String ngaydi = ngayroidi.getText();
                String makhachsan = tenkhachsan.getText();
                String gchu = ghichu.getText();
                ArrayList<String> bangsophong = new ArrayList<String>();

                for (int count = 0; count < bangdatphong.getRowCount(); count++) {

                        bangdatphong.editCellAt(count, 0);
                    try {
                        bangsophong.add(bangdatphong.getValueAt(count, 0).toString());
                    }catch (NullPointerException ex){
                        JOptionPane.showMessageDialog(null, "So phong khong hop le");
                    }
                }
                boolean check = false;
                int[] sophong = new int[bangsophong.size()];
                for (int i = 0; i < bangsophong.size(); i++) {
                    if (bangsophong.get(i).matches("^[0-9]*$")) {
                        sophong[i] = Integer.parseInt(bangsophong.get(i));
                    }
                    for (int j = 0; j < List.size(); j++){
                        if(sophong[i] == List.get(j).sophong){
                            check = true;
                        }
                    }
                }
                if(check) {
                    String Danhsachphong = Arrays.toString(sophong);
                    Danhsachphong = Danhsachphong.substring(1, Danhsachphong.length() - 1);
                    Danhsachphong = Danhsachphong.replaceAll(" ", "");

                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    LocalDateTime now = LocalDateTime.now();
                    String ngaydat = dtf.format(now);

                    System.out.print(Danhsachphong);
                    JSONObject json = new JSONObject();
                    json.put("username", User);
                    json.put("ngayvaoo", ngayvao);
                    json.put("ngaydi", ngaydi);
                    json.put("makhachsan", makhachsan);
                    json.put("ghichu", gchu);
                    json.put("sophong", Danhsachphong);
                    json.put("ngaydat", ngaydat);
                    json.put("type", "2");

                    try {
                        Socket clientSocket = new Socket("127.0.0.1", 5000);
                        BufferedReader input =
                                new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                        outToServer.writeBytes(json.toString() + '\n');
                        String thongbao = inFromServer.readLine();
                        JSONObject receive = new JSONObject(thongbao);
                        System.out.print(thongbao);
                        boolean isSuccess = receive.getBoolean("isSuccess");
                        String message = receive.getString("thongbao");
                        JOptionPane.showMessageDialog(null, message);
                        clientSocket.close();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "Du lieu khong hop le");
                    }
                }
                else{
                    JOptionPane.showMessageDialog(null, "So phong khong hop le");
                }
            }
        });
        menuButton3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Menu.setVisible(true);
                Huydon.setVisible(false);
            }
        });
        huydon.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Socket clientSocket;


                try {
                    String ma = madonhang.getText();
                    int donhang = Integer.parseInt(ma);
                    boolean check = false;
                    for (int i = 0; i < allBill.size(); i++) {
                        if (donhang == allBill.get(i).madonhang) {
                            check = true;
                        }
                    }
                    if (!check) {
                        JOptionPane.showMessageDialog(null, "Du lieu khong hop le");
                    }
                    else {
                        clientSocket = new Socket("127.0.0.1", 5000);
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        LocalDateTime now = LocalDateTime.now();
                        String ngayhuy = dtf.format(now);
                        JSONObject json = new JSONObject();
                        json.put("ngayhuy", ngayhuy);
                        json.put("username", User);
                        json.put("Madonhang", donhang);
                        json.put("type", "3");
                        // InputStreamReader input = new InputStreamReader(clientSocket.getInputStream());

                        BufferedReader input =
                                new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                        outToServer.writeBytes(json.toString() + '\n');
                        String thongbao = inFromServer.readLine();
                        JSONObject receive = new JSONObject(thongbao);
                        System.out.print(thongbao);
                        boolean isSuccess = receive.getBoolean("isSuccess");
                        String message = receive.getString("thongbao");
                        JOptionPane.showMessageDialog(null, message);
                        clientSocket.close();
                    }
                    }catch(IOException ex){
                        throw new RuntimeException(ex);
                    }catch(NumberFormatException ex){
                        JOptionPane.showMessageDialog(null, "Du lieu khong hop le");
                    }catch(Exception ex){
                        JOptionPane.showMessageDialog(null, "Du lieu khong hop le");
                    }
            }

        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("App");
        Client p = new Client();
        frame.setContentPane(p.panelMain);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(500, 500));
        frame.pack();
        frame.setVisible(true);
        }
    }


