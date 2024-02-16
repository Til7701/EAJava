package de.holube.ea.meta;

import io.jenetics.util.RandomRegistry;

public record MetaModel(

        int population,
        double mutationRate
) {

    public MetaModel getRandom() {
        return new MetaModel(
                this.mutatePopulation(),
                mutateMutationRate()
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

    @Override
    public String toString() {
        return "MetaModel{" +
                "population=" + population +
                ", mutationRate=" + mutationRate +
                '}';
    }

}
