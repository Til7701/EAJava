package de.holube.ea.plot;

import de.holube.ea.util.GenerationStatisticsList;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ResultCombiner {

    private final List<GenerationStatisticsList> results = new ArrayList<>();

    private final Lock lock = new ReentrantLock();

    public void accept(GenerationStatisticsList generationStatisticsList) {
        lock.lock();
        try {
            results.add(generationStatisticsList);
        } finally {
            lock.unlock();
        }
    }

    public GenerationStatisticsList getAverage() {
        return combine();
    }

    private CombinedResults combine() {
        results.sort(Comparator.comparingInt(a -> Integer.MAX_VALUE - a.getGenerationList().size()));

        final List<List<Double>> average = new ArrayList<>();
        final List<List<Double>> best = new ArrayList<>();
        for (int i = 0; i < results.getFirst().getGenerationList().size(); i++) {
            final List<Double> generationAverage = new ArrayList<>();
            final List<Double> generationBest = new ArrayList<>();
            for (final GenerationStatisticsList generationStatisticsList : results) {
                try {
                    generationAverage.add(generationStatisticsList.averageFitness().get(i));
                } catch (Exception ignored) {
                }
                try {
                    generationBest.add(generationStatisticsList.bestFitness().get(i));
                } catch (Exception ignored) {
                }
            }
            average.add(i, generationAverage);
            best.add(i, generationBest);
        }

        List<Double> averageAverageFitness = average.stream()
                .map(e -> e.stream().mapToDouble(d -> d).average().getAsDouble())
                .toList();
        List<Double> averageBestFitness = best.stream()
                .map(e -> e.stream().mapToDouble(d -> d).average().getAsDouble())
                .toList();

        return new CombinedResults(averageAverageFitness, averageBestFitness);
    }

    public static class CombinedResults extends GenerationStatisticsList {

        @Getter
        private final List<Integer> generationList;
        private final List<Double> averageFitness;
        private final List<Double> bestFitness;

        public CombinedResults(List<Double> averageFitness, List<Double> bestFitness) {
            super(null);
            generationList = new ArrayList<>(averageFitness.size());
            for (int i = 1; i <= averageFitness.size(); i++)
                generationList.add(i);
            this.averageFitness = averageFitness;
            this.bestFitness = bestFitness;
        }

        @Override
        public List<Double> averageFitness() {
            return averageFitness;
        }

        @Override
        public List<Double> bestFitness() {
            return bestFitness;
        }

    }

}
