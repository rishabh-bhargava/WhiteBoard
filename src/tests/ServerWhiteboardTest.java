package tests;

import org.junit.Test;
import static org.junit.Assert.*;

import server.*;
import shared.*;

import java.awt.*;
import java.util.List;
import java.net.Socket;
import java.util.ArrayList;


public class ServerWhiteboardTest {
    /**
     * Implements a fake Client that runs assertions on the received messages.
     */
    private class TestClient extends Client {
        private String[] expected;
        private int pointer = 0;

        TestClient(String username, String[] expected) {
            super(new WhiteboardManager(), new Socket());
            this.expected = expected;
            this.username = username;
        }

        @Override
        public void sendMessage(String line) {
            assertFalse("Client received more messages than expected.", pointer >= expected.length);
            assertEquals(expected[pointer++], line);
        }

        @Override
        public String handleMessage(String message) {
            // do nothing.
            return "";
        }

        public void finishTest() {
            assertEquals(expected.length, pointer);
        }
    }


    @Test
    public void testUserManipulation() {
        TestClient client1 = new TestClient("foo", new String[]{"JOIN bar"});
        TestClient client2 = new TestClient("bar", new String[]{"PART foo"});

        Whiteboard whiteboard = new Whiteboard("someboard");
        whiteboard.addUser(client1);
        whiteboard.addUser(client2);
        assertArrayEquals(new String[]{"bar", "foo"}, whiteboard.getUserNames());
        assertArrayEquals(new Client[]{client2, client1}, whiteboard.getUsers());
        whiteboard.removeUser(client1);
        assertArrayEquals(new String[]{"bar"}, whiteboard.getUserNames());
        assertArrayEquals(new Client[]{client2}, whiteboard.getUsers());
        whiteboard.removeUser(client2);
        assertArrayEquals(new String[]{}, whiteboard.getUserNames());
        assertArrayEquals(new Client[]{}, whiteboard.getUsers());

        client1.finishTest();
        client2.finishTest();
    }

    @Test
    public void testDrawing() {
        TestClient client1 = new TestClient("foo", new String[]{"JOIN bar", "DRAW -16711681 5.0 10 20 30 40"});
        TestClient client2 = new TestClient("bar", new String[]{"DRAW -16711681 5.0 10 20 30 40"});

        Whiteboard whiteboard = new Whiteboard("someboard");
        whiteboard.addUser(client1);
        whiteboard.addUser(client2);

        LineSegment line = new LineSegment(10, 20, 30, 40);
        List<LineSegment> list = new ArrayList<>();
        list.add(line);

        String initial = whiteboard.getSerializedImage();

        whiteboard.draw(Color.CYAN, 5, list);

        assertFalse("Image not changed by drawing", initial.equals(whiteboard.getSerializedImage()));
    }

    @Test
    public void testName() {
        Whiteboard whiteboard = new Whiteboard("someboard");
        assertEquals("someboard", whiteboard.getName());
    }
}
