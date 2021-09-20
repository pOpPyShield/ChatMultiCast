
package Client;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class client extends JFrame implements ActionListener{

    // IP server: localhost

    private JButton send,clear,exit,login,logout,Connect;
    private JPanel p_login,p_chat,p_connect, Info;
    private JTextField nick,nick1,message,IP;
    private JTextArea msg,online;
    private Socket client;
    private ThreadClien dataStream;
    private DataOutputStream dos;
    private DataInputStream dis;
    private BufferedImage ImageToken;

    public client (){

        super("Nhắn tin");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(600, 400);
        addItem();
        setVisible(true);
    }
    //-----[ Tạo giao diện ]--------//
    private void addItem() {
        setLayout(new BorderLayout());

        exit = new JButton("Thoát");
        exit.addActionListener(this);
        send = new JButton("Gửi");
        send.addActionListener(this);
        clear = new JButton("Xóa");
        clear.addActionListener(this);
        login= new JButton("Đăng nhập");
        login.addActionListener(this);
        logout= new JButton("Thoát");
        logout.addActionListener(this);

        p_chat = new JPanel();
        p_chat.setLayout(new BorderLayout());

        JPanel p1 = new JPanel();
        p1.setLayout(new FlowLayout(FlowLayout.LEFT));
        nick = new JTextField(20);
        p1.add(new JLabel("Níck chát : "));
        p1.add(nick);
        p1.add(exit);

        JPanel p2 = new JPanel();
        p2.setLayout(new BorderLayout());

        JPanel p22 = new JPanel();
        p22.setLayout(new FlowLayout(FlowLayout.CENTER));
        p22.add(new JLabel("Danh sách online"));
        p2.add(p22,BorderLayout.NORTH);

        online = new JTextArea(10,10);
        online.setEditable(false);
        p2.add(new JScrollPane(online),BorderLayout.CENTER);
        p2.add(new JLabel("     "),BorderLayout.SOUTH);
        p2.add(new JLabel("     "),BorderLayout.EAST);
        p2.add(new JLabel("     "),BorderLayout.WEST);

        msg = new JTextArea(10,20);
        msg.setEditable(false);

        JPanel p3 = new JPanel();
        p3.setLayout(new FlowLayout(FlowLayout.LEFT));
        p3.add(new JLabel("Tin nhắn"));
        message = new JTextField(30);
        p3.add(message);
        p3.add(send);
        p3.add(clear);

        p_chat.add(new JScrollPane(msg),BorderLayout.CENTER);
        p_chat.add(p1,BorderLayout.NORTH);
        p_chat.add(p2,BorderLayout.EAST);
        p_chat.add(p3,BorderLayout.SOUTH);
        p_chat.add(new JLabel("     "),BorderLayout.WEST);

        p_chat.setVisible(false);
        add(p_chat,BorderLayout.SOUTH);
        //-------------------------


        p_login = new JPanel();
        p_login.setLayout(new FlowLayout(FlowLayout.CENTER));
        p_login.add(new JLabel("Tên để chat : "));
        nick1=new JTextField(20);
        p_login.add(nick1);
        p_login.add(login);
        p_login.add(logout);
        p_login.setVisible(false);
        add(p_login,BorderLayout.NORTH);

        //----------Connect-----------//
        p_connect = new JPanel();
        p_connect.setLayout(new FlowLayout(FlowLayout.CENTER));
        p_connect.add(new JLabel("IP Sever : "));
        IP = new JTextField(20);
        p_connect.add(IP);
        Connect = new JButton("Kết Nối");
        Connect.addActionListener(this);
        p_connect.add(Connect);
        add(p_connect,BorderLayout.LINE_START);
        //---------------
        Info = new JPanel();
        Info.setLayout(new FlowLayout(FlowLayout.CENTER));
        JTextArea T_info = new JTextArea();
        String a = "\tTrường Đại học công nghệ thông tin và truyền thông Việt-Hàn\t\n";
        T_info.setText(a);
        Info.add(T_info);
        Info.setVisible(false);
        add(Info, BorderLayout.CENTER);

    }
    //---------[ Socket ]-----------//
    private void go(String IP) {
        try {

            client = new Socket(IP,2207);
            dos=new DataOutputStream(client.getOutputStream());
            dis=new DataInputStream(client.getInputStream());

            //client.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,"Kết nối thât bại, xin hãy kiểm tra lại ip.","Message Dialog",JOptionPane.WARNING_MESSAGE);
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        new client();
    }
    private void sendMSG(String data){
        try {
            dos.writeUTF(data);
            
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private String getMSG(){
        String data=null;
        try {
            data=dis.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    public void getMSG(String msg1, String msg2){
        int stt = Integer.parseInt(msg1);
        switch (stt) {
            // tin nhắn của những người khác
            case 3:
                this.msg.append(msg2);

                break;
            // update danh sách online
            case 4:
                this.online.setText(msg2);

                break;
            // server đóng cửa
            case 5:
                dataStream.stopThread();
                exit();
                break;
            // bổ sung sau
            default:
                break;
        }
    }
    //----------------------------------------------
    private void checkSend(String msg){
        if(msg.compareTo("\n")!=0){
            this.msg.append("Tôi : "+msg);
            sendMSG("1");
            sendMSG(msg);
        }
    }
    private boolean checkLogin(String nick){
        if(nick.compareTo("")==0)
            return false;
        else if(nick.compareTo("0")==0){
            return false;
        }
        else{
            sendMSG(nick);
            int sst = Integer.parseInt(getMSG());
            return sst != 0;
        }
    }

    private void exit(){
        try {
            sendMSG("0");
            dos.close();
            dis.close();
            client.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        System.exit(0);
    }
    public void actionPerformed(ActionEvent e) {


        if(e.getSource()==exit){
            dataStream.stopThread();
            exit();
        }
        else if(e.getSource()==clear){
            message.setText("");
        }
        else if(e.getSource()==send){
            checkSend(message.getText()+"\n");
            message.setText("");
        }
        else if(e.getSource()==login){
            if(checkLogin(nick1.getText())){
                p_chat.setVisible(true);
                p_login.setVisible(false);
                Info.setVisible(false);
                nick.setText(nick1.getText());
                nick.setEditable(false);
                this.setTitle(nick1.getText());
                msg.append("Đã đăng nhập thành công\n");
                dataStream = new ThreadClien(this, this.dis);
            }
            else{
                JOptionPane.showMessageDialog(this,"Đã tồn tại níck này trong room, bạn vui lòng nhập lại.","Message Dialog",JOptionPane.WARNING_MESSAGE);
            }

        }
        else if(e.getSource() == Connect){
            go(IP.getText());
            p_login.setVisible(true);
            Info.setVisible(true);
            p_connect.setVisible(false);
        }
        else if(e.getSource()==logout){
            exit();
        }
    }

}
