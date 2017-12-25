package client;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Controller {
    @FXML
    public TextArea jta;
    @FXML
    public TextField jtf;

    private final int SERVER_PORT = 8189;
    private final String SERVER_ADDRESS = "localhost";
    private Socket socket;
    private Scanner in;
    private PrintWriter out;

    public Controller(){
        try {
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    if(in.hasNext()){
                        jta.appendText(in.nextLine() + "\n");
                    }
                }
            }
        }).start();
    }

    public void sendMsg(){
        out.println(jtf.getText());
        out.flush();
        jtf.setText("");
    }
}
