package chat.server;

import chat.Server_API;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler implements Server_API{

    private Server server;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String nick;

    public ClientHandler(Server server, Socket socket, int timeAuth){
        try{
            this.server = server;
            this.socket = socket;
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            nick = "undefined";
        }catch(IOException e){
            e.printStackTrace();
        }
        Thread auth = new Thread(() -> {
            try{
                // Авторизация клиента
                while(true){
                    String msg = in.readUTF();
                    if(msg.startsWith(AUTH)){
                        String[] elements = msg.split(" ");
                        String nick = null;
                        if(elements.length > 2)
                            nick = server.getAuthService().getNickByLoginPass(elements[1],elements[2]);
                        if(nick != null){
                            if(!server.isNickBusy(nick)){
                                server.addNickClients(nick,this);
                                sendMessage(AUTH_SUCCESSFUL + " " + nick);
                                this.nick = nick;
                                server.broadcastUsersList();
                                server.broadcast(this.nick + " has enteted the chat room!");
                                break;
                            }else sendMessage("This account is already in use!");
                        }else sendMessage("Wrong login/password!");
                    }else sendMessage("You should authorize first!");
                    if(msg.equalsIgnoreCase(CLOSE_CONNECTION)) disconnect();
                }
                // Прием сообщений
                while (true) {
                    String msg = in.readUTF();
                    if (msg.startsWith(SYSTEM_SYMBOL)) {
                        if (msg.equalsIgnoreCase(CLOSE_CONNECTION)) break;
                        else if (msg.startsWith(PRIVATE_MESSAGE)) {
                            String nameTo = msg.split(" ")[1];
                            String message = msg.substring(nameTo.length() + 4);
                            server.sendPrivateMessage(this, nameTo, message);
                        } else {
                            sendMessage("Command doesn't exist!");
                        }
                    } else {
                        System.out.println("client: " + msg);
                        server.broadcast(this.nick + " " + msg);
                    }
                }
            }catch(IOException e){
                e.printStackTrace();
            }finally{
                disconnect();
                try{
                    socket.close();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }

        });
        auth.start();
        // Отключение по таймеру
        new Thread(() -> {
            long timer = System.currentTimeMillis();
            while(isTimer(timer, timeAuth));
            if(isNickNull()) {
                sendMessage("Первышен лимит ожидания от сервера.");
                disconnect();
                auth.stop();
            }
        }).start();
    }
    // Отправка сообщения
    public void sendMessage(String msg){
        try{
            out.writeUTF(msg);
            out.flush();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    // Отключение клиента
    public void disconnect(){
        sendMessage("You have been disconnected!");
        server.unSubscribeMe(nick,this);
        try{
            socket.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    // Возврат Nick
    public String getNick(){
        return nick;
    }
    // Проверка таймера
    private Boolean isTimer(long timer, int timeAuth){
        if(timer + timeAuth*1000 < System.currentTimeMillis()) return false;
        else return true;
    }
    public Boolean isNickNull(){
        if(nick.equalsIgnoreCase("undefined")) return true;
        else return false;
    }
}
