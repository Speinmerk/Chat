package chat.server;

import chat.ServerConst;
import chat.Server_API;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Vector;

public class Server implements ServerConst, Server_API{

    private Vector<ClientHandler> clients;
    private HashMap<String, ClientHandler> nickClients;
    private AuthService authService;

    public AuthService getAuthService(){
        return authService;
    }

    public Server(){
        ServerSocket server;
        Socket socket;
        clients = new Vector<>();
        nickClients = new HashMap<>();
        try{
            server = new ServerSocket(PORT);
            authService = new BaseAuthService();
            authService.start(); //placeholder
            System.out.println("Server is up and running! Awaiting for connections");
            while(true){
                socket = server.accept();
                clients.add(new ClientHandler(this, socket, TIME_OUT_SECOND));
                System.out.println("Client has connected!");
            }
        }catch(IOException e){
        }finally{
        }
    }
    // Добавление нового клиента в обратный список
    public void addNickClients(String nick, ClientHandler clientHandler) {
        nickClients.put(nick, clientHandler);
    }
    // Отправка сообщения всем подключенным клиентам
    public void broadcast(String msg){
        for(ClientHandler client : clients)
            if(!client.isNickNull())
                client.sendMessage(msg);
    }
    // Отправка приватного сообщения
    public void sendPrivateMessage(ClientHandler from, String to, String msg){
        if(isNickBusy(to)){
            nickClients.get(to).sendMessage("from " + from.getNick() + ": " + msg);
            from.sendMessage("to " + to + ": " + msg);
        } else from.sendMessage("User not found");
    }
    // Отправка списка подключенных клиентов
    public void broadcastUsersList(){
        StringBuffer sb = new StringBuffer(USERS_LIST);
        for(ClientHandler client : clients){
            if(!client.isNickNull())
                sb.append(" " + client.getNick());
        }
        for(ClientHandler client : clients){
            if(!client.isNickNull())
                client.sendMessage(sb.toString());
        }
    }
    // Удаление отключенного клиента
    public void unSubscribeMe(String nick, ClientHandler c){
        clients.remove(c);
        nickClients.remove(nick, c);
        broadcastUsersList();
    }

    public Boolean isNickBusy(String nick){
        return nickClients.containsKey(nick);
    }
}
