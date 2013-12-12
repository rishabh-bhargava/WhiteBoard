package tests;

import static org.junit.Assert.*;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import server.Client;
import server.ClientException;
import server.Whiteboard;
import server.WhiteboardManager;

public class ServerWhiteboardManagerTest 
{
    
	@Test(expected=ClientException.class)
    public void testDuplicateClientException() throws ClientException {
        WhiteboardManager manager = new WhiteboardManager();

        Client client1 = new Client(manager, new Socket());
        Client client2 = new Client(manager, new Socket());
        client1.handleMessage("HELLO sam");
        client2.handleMessage("HELLO sam");
    }

    @Test
    public void testClientRemoval() throws ClientException 
    {
        WhiteboardManager manager = new WhiteboardManager();

        Client client1 = new Client(manager, new Socket());
        Client client2 = new Client(manager, new Socket());
        client1.handleMessage("HELLO sam");
        manager.removeClient(client1);
        client2.handleMessage("HELLO sam");
        List<String> names = new ArrayList<String>(); 
        names.add("sam");
        assertTrue(manager.getClientNames().equals(names));
    }

    @Test
    public void testDifferentClients() throws ClientException {
        WhiteboardManager manager = new WhiteboardManager();

        Client client1 = new Client(manager, new Socket());
        Client client2 = new Client(manager, new Socket());
        client1.handleMessage("HELLO sam");
        client2.handleMessage("HELLO kate");
        List<String> names = new ArrayList<String>();
        names.add("kate");
        names.add("sam");
        assertTrue(names.equals(manager.getClientNames()));
    }

    @Test
    public void singleWhiteboard() throws ClientException {
        WhiteboardManager manager = new WhiteboardManager();

        Whiteboard someboard = manager.createWhiteboard("someboard");
        assertEquals(true, manager.hasWhiteboard("someboard"));
        assertArrayEquals(new String[]{"someboard"}, manager.getWhiteboardNames());
        assertSame(someboard, manager.getWhiteboard("someboard"));
    }

    @Test(expected=ClientException.class)
    public void duplicateWhiteboards() throws ClientException {
        WhiteboardManager manager = new WhiteboardManager();

        manager.createWhiteboard("someboard");
        manager.createWhiteboard("someboard");
        assertArrayEquals(new String[]{"someboard"}, manager.getWhiteboardNames());
    }

    @Test
    public void multipleWhiteboards() throws ClientException {
        WhiteboardManager manager = new WhiteboardManager();

        Whiteboard fooboard = manager.createWhiteboard("fooboard");
        Whiteboard barboard = manager.createWhiteboard("barboard");
        assertEquals(true, manager.hasWhiteboard("fooboard"));
        assertEquals(true, manager.hasWhiteboard("barboard"));
        assertArrayEquals(new String[]{"barboard", "fooboard"}, manager.getWhiteboardNames());
        assertSame(fooboard, manager.getWhiteboard("fooboard"));
        assertSame(barboard, manager.getWhiteboard("barboard"));
    }
}
