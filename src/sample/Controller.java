package sample;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class Controller {
    @FXML
    public TextArea jta;
    @FXML
    public TextField jtf;

    public void sendMsg(){
        String str = jtf.getText();
        jtf.clear();
        jta.appendText(str + "\n");
    }
}
