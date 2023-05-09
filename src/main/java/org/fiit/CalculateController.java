package org.fiit;

import org.apache.commons.math3.util.ArithmeticUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CalculateController {
    private long maximumDegree;
    private boolean selfInverseElement;

    //function tries to find cayley graph with the lowest degree and generated group size set from outside
    public List<Integer> calculate(int groupSize){
        calculateBounds(groupSize);

        for (int currentGraphDegree = 2; currentGraphDegree <= maximumDegree; currentGraphDegree++){
            int modifiedGraphDegree = isOdd(currentGraphDegree)? currentGraphDegree - 1: currentGraphDegree;
            int modifiedGroupSize = !isOdd(groupSize)? groupSize - 2: groupSize - 1;
            double combinations = ArithmeticUtils.binomialCoefficientDouble(
                    modifiedGroupSize,
                    (int) Math.ceil(modifiedGraphDegree / 2.0)
            ) / 2;
            List<List<Integer>> generatorsList = new ArrayList<>();
            //bigger than a billion
            if (combinations > 1000000000){
                combinations = combinations * 0.05;
            }
            combinations = Math.floor(combinations);
            
            int missedCombinationsCounter = 0;
            //search for all combinations
            while (generatorsList.size() < combinations) {
                //if currentGraphDegree is odd and there is no selfInverseElement skip iteration,
                //because there is no way to generate that graph degree
                if (!(isOdd(currentGraphDegree) && !selfInverseElement)) {
                    List<Integer> generators = createGenerators(currentGraphDegree, groupSize);
                    Collections.sort(generators);
                    if (!generatorsList.contains(generators)) {
                        generatorsList.add(generators);

                        if (checkGraphRequirements(generators, groupSize)) {
                            System.out.println("Graph can be created with degree: " + currentGraphDegree);
                            return generators;
                        }
                    }
                    else {
                        missedCombinationsCounter++;
                    }
                }
                else {
                    break;
                }
                //TODO: safeguard against endless loop
                if (missedCombinationsCounter > 1000) {
                    break;
                }
            }
        }

        return new ArrayList<>();
    }

    //calculate maximum bounds for graph
    private void calculateBounds(int currentN){
        //calculate maximum degree by moore bound
        int bound = 0;
        int newDegree = 1;
        while (bound < currentN){
            newDegree++;
            bound = (int) Math.ceil(1 + ((Math.pow(newDegree, 2)) / 2.0));
        }
        this.maximumDegree = newDegree;
        System.out.println("Maximum graph degree: " + this.maximumDegree);
        System.out.println("Moore bound: " + bound);
        //set true if group has self-inverse generator
        this.selfInverseElement = currentN % 2 == 0;
    }

    //basic is odd function
    private boolean isOdd(int number){
        return number % 2 == 1;
    }

    //create random generators
    private List<Integer> createGenerators(int vertexDegree, int groupSize){
        List<Integer> generators = new ArrayList<>();

        //if degree is odd add self-inverse element
        if (isOdd(vertexDegree)){
            generators.add(groupSize / 2);
        }
        //divide required degree by 2 because every generator needs inverse generator
        for (int x = 0; x < Math.floorDiv(vertexDegree, 2); x++){
            int min = 1;
            int max = groupSize - 1;
            int generator = (int) Math.floor(Math.random() *(max - min + 1) + min);
            //if generator is already picked or generator is self-inverse repeat
            //else add generator with inverse
            if (generators.contains(generator) || generator == groupSize / 2){
                x--;
            }
            else {
                generators.add(generator);
                generators.add(groupSize - generator);
            }
        }

        return generators;
    }

    //check for every vertex of cyclic group if diameter 2 requirement is satisfied
    private boolean checkGraphRequirements(List<Integer> generators, int groupSize){
        for (int vertex = 1; vertex < groupSize; vertex++){
            boolean vertexReachable = false;
            if (generators.contains(vertex)){
                vertexReachable = true;
            }
            else {
                for (Integer generator: generators) {
                    for (int generator2Index = generators.indexOf(generator); generator2Index < generators.size(); generator2Index++){
                        if ((generator + generators.get(generator2Index)) % groupSize == vertex) {
                            vertexReachable = true;
                            break;
                        }
                    }
                    if (vertexReachable){
                        break;
                    }
                }
            }
            //if vertex is not reachable quit checking graph requirements, graph is BAD
            if (!vertexReachable){
                return false;
            }
        }
        return true;
    }
}
