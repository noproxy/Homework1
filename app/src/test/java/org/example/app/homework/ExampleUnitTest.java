package org.example.app.homework;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Mock
    DemoInterface demoInterface;

    @Before
    public void initMocks() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testComputeLengthMethod() {
        // can compute the length of 'hhh' is 3
        Mockito.when(demoInterface.getString()).thenReturn("hhh");
        assertEquals(3, computeLength(demoInterface));


        // can compute the length of '' is 0
        Mockito.when(demoInterface.getString()).thenReturn("");
        assertEquals(0, computeLength(demoInterface));

        // can compute the length of null is 0
        Mockito.when(demoInterface.getString()).thenReturn(null);
        assertEquals(0, computeLength(demoInterface));
    }

    private int computeLength(DemoInterface demoInterface) {
        final String string = demoInterface.getString();
        if (string == null) {
            return 0;
        }

        return string.length();
    }

    interface DemoInterface {
        String getString();
    }
}