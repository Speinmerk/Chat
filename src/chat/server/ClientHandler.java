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
    private String login;

    public ClientHandler(Server server, Socket socket){
        try{
            this.server = server;
            this.socket = socket;
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            nick = "undefined";
        }catch(IOException e){
            e.printStackTrace();
        }
        new Thread(() -> {
            try{
                //Authorization
                while(true){
                    String msg = in.readUTF();
                    if(msg.startsWith(AUTH)){
                        String[] elements = msg.split(" ");
                        String nick = server.getAuthService().getNickByLoginPass(elements[1],elements[2]);
                        if(nick != null){
                            if(!server.isNickBusy(nick)){
                                server.addNickClients(nick,this);
                                sendMessage(AUTH_SUCCESSFUL + " " + nick);
                                this.nick = nick;
                                server.broadcast(null, this.nick + " has enteted the chat room!");
                                break;
                            }else sendMessage("This account is already in use!");
                        }else sendMessage("Wrong login/password!");
                    }else sendMessage("You should authorize first!");
                    if(msg.equalsIgnoreCase(CLOSE_CONNECTION)) disconnect();
                }
                //отвечает за прием обычных сообщений
                while(true){
                    String msg = in.readUTF();
                    if(msg.equalsIgnoreCase(CLOSE_CONNECTION)) break;
                    String[] arrMsg = msg.split(" ");
                    if(arrMsg[0].equalsIgnoreCase(MSG_TO_NICK) && arrMsg.length>2){
                        msg = arrMsg[2];
                        for (int i = 3; i < arrMsg.length; i++)
                            msg+=" "+arrMsg[i];
                        server.broadcast(arrMsg[1], this.nick + " " + msg);
                    }
                    else server.broadcast(null, this.nick + " " + msg);
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
        }).start();

    }
    public void sendMessage(String msg){
        try{
            out.writeUTF(msg);
            out.flush();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    public void disconnect(){
        sendMessage("You have been disconnected!");
        server.unSubscribeMe(nick,this);
        try{
            socket.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
