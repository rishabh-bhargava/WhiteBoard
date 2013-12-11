package server;

/**
 * Thrown when a message needs to be sent back to the client.
 */
public class ClientException extends Exception {
    private static final long serialVersionUID = -3071553630549260955L;

    public ClientException(String message) {
        super(message);
    }
}
