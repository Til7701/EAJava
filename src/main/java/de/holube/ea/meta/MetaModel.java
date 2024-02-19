package de.holube.ea.meta;

import io.jenetics.util.RandomRegistry;

public record MetaModel(
        int population,
        double mutationRate,
        double crossoverRate
) {

    public MetaModel getRandom() {
        return new MetaModel(
                mutatePopulation(),
                mutateMutationRate(),
                mutateCrossoverRate()
        );
    }

    private int mutatePopulation() {
        int newPopulation;
        do {
            newPopulation = population + RandomRegistry.random().nextInt(-50, 50);
        } while (newPopulation < 1);
        return newPopulation;
    }

    private double mutateMutationRate() {
        double newMutationRate;
        do {
            newMutationRate = mutationRate + RandomRegistry.random().nextDouble(-1, 1);
        } while (newMutationRate < 0 || newMutationRate > 1);
        return newMutationRate;
    }

    private double mutateCrossoverRate() {
        double newCrossoverRate;
        do {
            newCrossoverRate = crossoverRate + RandomRegistry.random().nextDouble(-1, 1);
        } while (newCrossoverRate < 0 || newCrossoverRate > 1);
        return newCrossoverRate;
    }

}
