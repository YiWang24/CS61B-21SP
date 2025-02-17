package tester;

import static org.junit.Assert.*;

import edu.princeton.cs.introcs.StdRandom;
import org.junit.Test;
import student.StudentArrayDeque;

/**
 * @author WY
 * @version 1.0
 **/

public class TestArrayDequeEC {
    @Test
    public void test() {
        StudentArrayDeque<Integer> studentDeque = new StudentArrayDeque<>();
        ArrayDequeSolution<Integer> solutionDeque = new ArrayDequeSolution<>();

        StringBuilder message = new StringBuilder();
        int numberOfOperations = 1000;
        for (int i = 0; i < numberOfOperations; i++) {

            int operation = StdRandom.uniform(4);

            if (operation == 0) {
                // addFirst
                Integer randVal = StdRandom.uniform(1000);
                studentDeque.addFirst(randVal);
                solutionDeque.addFirst(randVal);
                message.append("addFirst(").append(randVal).append(")\n");
            } else if (operation == 1) {
                // addLast
                Integer randVal = StdRandom.uniform(1000);
                studentDeque.addLast(randVal);
                solutionDeque.addLast(randVal);
                message.append("addLast(").append(randVal).append(")\n");

            } else if (operation == 2) {

                // removeFirst
                if (!studentDeque.isEmpty() && !solutionDeque.isEmpty()) {
                    Integer studVal = studentDeque.removeFirst();
                    Integer solVal = solutionDeque.removeFirst();
                    message.append("removeFirst()\n");
                    assertEquals(
                            message.toString(),
                            solVal,
                            studVal
                    );
                }
            } else if (operation == 3) {
                // removeLast
                if (!studentDeque.isEmpty() && !solutionDeque.isEmpty()) {
                    Integer studVal = studentDeque.removeLast();
                    Integer solVal = solutionDeque.removeLast();
                    message.append("removeLast()\n");
                    assertEquals(
                            message.toString(),
                            solVal,
                            studVal
                    );
                }
            }
        }
    }
}
