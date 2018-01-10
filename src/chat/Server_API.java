package chat;

public interface Server_API {
    String CLOSE_CONNECTION = "/end";
    String AUTH = "/auth";
    String AUTH_SUCCESSFUL = "/authok";
    String MSG_TO_NICK = "/w";
}
