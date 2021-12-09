package org.example.app.homework;

import static org.junit.Assert.*;

import org.junit.Test;

import java.io.IOException;

public class LoaderTest {
    @Test
    public void testLoad() throws IOException {
        final SimpleMessageNetworkLoader simpleMessageNetworkLoader = new SimpleMessageNetworkLoader("http://localhost:8000/");

        final String s = simpleMessageNetworkLoader.loadMessages();

        assertEquals("{}", s);
    }
}
