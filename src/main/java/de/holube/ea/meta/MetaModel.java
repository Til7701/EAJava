package de.holube.ea.meta;

import io.jenetics.util.RandomRegistry;

public record MetaModel(
        int population,
        double mutationRate
) {

    public static MetaModel getRandom() {
        return new MetaModel(
                RandomRegistry.random().nextInt(1, 50),
                RandomRegistry.random().nextDouble()
        );
    }

    @Override
    public String toString() {
        return "MetaModel{" +
                "population=" + population +
                ", mutationRate=" + mutationRate +
                '}';
    }

}
