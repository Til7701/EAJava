package de.holube.ea.util;

import io.jenetics.Gene;
import io.jenetics.Phenotype;
import io.jenetics.engine.EvolutionResult;
import lombok.Getter;

import java.util.ArrayList;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.stream.IntStream;


public class GenerationStatisticsList {


    @Getter
    private final List<Integer> generationList;
    private final List<GenerationStatistics> generationStatistics;

    public GenerationStatisticsList(List<EvolutionResult<? extends Gene<?, ?>, Integer>> results) {
        if (results == null) {
            generationList = null;
            generationStatistics = null;
            return;
        }
        generationList = new ArrayList<>(results.size());
        for (int i = 1; i <= results.size(); i++)
            generationList.add(i);
        generationStatistics = new ArrayList<>(results.size());

        generationStatistics.addAll(results.stream()
                .map(GenerationStatistics::new)
                .toList()
        );
    }

    public List<Double> averageFitness() {
        return generationStatistics.stream()
                .map(stats -> stats.fitnessStats.getAverage())
                .toList();
    }

    public List<Double> bestFitness() {
        return generationStatistics.stream()
                .map(stats -> (double) stats.fitnessStats.getMax())
                .toList();
    }


    private static class GenerationStatistics {

        IntSummaryStatistics fitnessStats;

        GenerationStatistics(EvolutionResult<?, Integer> evolutionResult) {
            fitnessStats = evolutionResult.population().stream()
                    .map(Phenotype::fitness)
                    .flatMapToInt(IntStream::of)
                    .summaryStatistics();
        }
    }

}
