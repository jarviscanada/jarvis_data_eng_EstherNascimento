package ca.jrvs.apps.practice;

import org.junit.Test;
import static org.junit.Assert.*; // For assertEquals
import java.util.Arrays;         // This fixes your "Arrays" error
import java.util.List;           // For the List return types
import java.util.stream.IntStream; // For testing your IntStream methods

public class LambdaStreamImpTest {
    @Test
    public void toUpperCase() {
        LambdaStreamExc lse = new LambdaStreamImp();
        List<String> result = lse.toList(lse.toUpperCase("a", "b", "c"));
        // Assert verifies the actual result matches your expectation
        assertEquals(Arrays.asList("A", "B", "C"), result);
    }

    @Test
    public void getOdd() {
        LambdaStreamExc lse = new LambdaStreamImp();
        IntStream stream = lse.createIntStream(1, 5);
        List<Integer> result = lse.toList(lse.getOdd(stream));
        assertEquals(Arrays.asList(1, 3, 5), result);
    }
}