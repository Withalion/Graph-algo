package org.fiit;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        CalculateController calculateController = new CalculateController();
        System.out.println("Generating Cayley graphs:");

        for (int n = 5; n <= 50; n++) {
            System.out.println(">--------------------------------------<");
            System.out.println("Calculating smallest d for n = " + n);
            List<Integer> generators = calculateController.calculate(n);
            if (!generators.isEmpty()) {
                System.out.println("Generators for this graph are: " + generators);
            }
            else {
                System.out.println("Graph couldn't be constructed");
            }
        }
    }

    private Main() {}
}