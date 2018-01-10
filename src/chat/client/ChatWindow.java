package chat.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ChatWindow extends JFrame{
    private JTextArea jTextArea;
    private JTextField jTextField;
    private JTextField jtfLogin;
    private JPasswordField jtfPassword;
    private JPanel top;
    private JPanel jPanelBottom;
    private ClientConnection client;

    public ChatWindow(){

        client = new ClientConnection();
        client.init(this);
        setTitle("Chat Window");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(400, 400);
        jTextArea = new JTextArea();
        jTextField = new JTextField();
        jTextField.setPreferredSize(new Dimension(200, 20));
        JScrollPane jScrollPane = new JScrollPane(jTextArea);
        jTextArea.setEditable(false);
        jTextArea.setLineWrap(true);
        JButton jButtonSend = new JButton("Send");

        jPanelBottom = new JPanel();
        jPanelBottom.add(jTextField, BorderLayout.CENTER);
        jPanelBottom.add(jButtonSend, BorderLayout.EAST);

        jtfLogin = new JTextField();
        jtfPassword = new JPasswordField();
        JButton jbAuth = new JButton("Login");
        top = new JPanel(new GridLayout(1,3));
        top.add(jtfLogin);
        top.add(jtfPassword);
        top.add(jbAuth);

        add(jScrollPane, BorderLayout.CENTER);
        add(jPanelBottom, BorderLayout.SOUTH);
        add(top, BorderLayout.NORTH);
        jButtonSend.addActionListener(e -> sendMessage());
        jTextField.addActionListener(e -> sendMessage());
        jbAuth.addActionListener(e -> auth());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e){
                super.windowClosing(e);
                client.disconnect();
            }
        });
        switchWindows();
        setVisible(true);
    }
    public void sendMessage(){
        String msg = jTextField.getText();
        jTextField.setText("");
        client.sendMessage(msg);
    }
    public void showMessage(String msg){
        jTextArea.append(msg + "\n");
        jTextArea.setCaretPosition(jTextArea.getDocument().getLength());
    }
    public void auth(){
        client.auth(jtfLogin.getText(), new String(jtfPassword.getPassword()));
        jtfLogin.setText("");
        jtfPassword.setText("");
    }
    public void switchWindows(){
        top.setVisible(!client.isAuthorized());
        jPanelBottom.setVisible(client.isAuthorized());
    }

}
