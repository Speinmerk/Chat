package chat.server;

import chat.ServerConst;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Vector;

public class Server implements ServerConst{
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
                clients.add(new ClientHandler(this, socket));
                System.out.println("Client has connected!");
            }
        }catch(IOException e){
        }finally{
        }
    }
    public void addNickClients(String login, ClientHandler clientHandler) {
        nickClients.put(login, clientHandler);
    }
    public void broadcast(String nick, String msg){
        if(nick == null)
            for(ClientHandler client : clients)
                client.sendMessage(msg);
        else nickClients.get(nick).sendMessage(msg);
    }

    public void unSubscribeMe(String login, ClientHandler c){
        clients.remove(c);
        nickClients.remove(login, c);
    }
    public boolean isNickBusy(String nick){
        if(nickClients.containsKey(nick)) return true;
        else return false;
    }
}
