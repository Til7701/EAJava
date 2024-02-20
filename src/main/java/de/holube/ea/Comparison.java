package de.holube.ea;

import de.holube.ea.plot.ResultCombiner;
import de.holube.ea.plot.ResultPlot;
import de.holube.ea.util.GenerationStatisticsList;
import io.jenetics.Gene;
import io.jenetics.engine.EvolutionResult;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class Comparison {

    private final ResultPlot averagePlot = new ResultPlot("Average Fitness").setLegend(true);
    private final ResultPlot bestPlot = new ResultPlot("Best Fitness").setLegend(true);

    public static void main(String[] args) {
        new Comparison().run(1000);
    }

    public void run(final int runs) {
        final double mutationRate = 0.35;
        final int bits = 32;
        runSingle(runs, 10, 0.1, mutationRate, "Algorithm 1", bits);
        runSingle(runs, 10, 0.1, 0.9, "Algorithm 2", bits);
        bestPlot.setTitle("Best Fitness Average of " + runs + " Runs")
                .plot();
        runSingleR(runs, 10, 0.1, mutationRate, "Algorithm 3", bits);

        averagePlot.plot();
        bestPlot.setTitle("Best Fitness Average of " + runs + " Runs with Rechenberg Mutation")
                .plot();
    }

    private void runSingle(final int runs,
                           final int population, final double crossoverRate, final double mutationRate,
                           String name, int bits
    ) {
        final ResultCombiner resultCombiner = new ResultCombiner();
        List<Callable<GenerationStatisticsList>> runnables = IntStream.iterate(runs, count -> count > 0, count -> count - 1)
                .mapToObj(count -> 0).<Callable<GenerationStatisticsList>>map(i -> () -> {
                    First80s ea = new First80s();
                    ea.run(population, crossoverRate, mutationRate, bits);
                    var genericResults = ea.getResults().stream()
                            .<EvolutionResult<? extends Gene<?, ?>, Integer>>map(e -> e)
                            .toList();
                    resultCombiner.accept(new GenerationStatisticsList(genericResults));
                    return new GenerationStatisticsList(genericResults);
                }).toList();

        try (ExecutorService pool = Executors.newFixedThreadPool(10)) {
            pool.invokeAll(runnables);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        averagePlot.other(resultCombiner.getAverage())
                .plotAverageFitness(name, false)
                .nextColor();
        bestPlot.other(resultCombiner.getAverage())
                .plotBestFitness(name, false)
                .nextColor();
    }

    private void runSingleR(final int runs,
                            final int population, final double crossoverRate, final double mutationRate,
                            String name, int bits
    ) {
        final ResultCombiner resultCombiner = new ResultCombiner();
        List<Callable<GenerationStatisticsList>> runnables = IntStream.iterate(runs, count -> count > 0, count -> count - 1)
                .mapToObj(count -> 0).<Callable<GenerationStatisticsList>>map(i -> () -> {
                    First80sRechenberg ea = new First80sRechenberg();
                    ea.run(population, crossoverRate, mutationRate, bits);
                    var genericResults = ea.getResults().stream()
                            .<EvolutionResult<? extends Gene<?, ?>, Integer>>map(e -> e)
                            .toList();
                    resultCombiner.accept(new GenerationStatisticsList(genericResults));
                    return new GenerationStatisticsList(genericResults);
                }).toList();

        try (ExecutorService pool = Executors.newFixedThreadPool(10)) {
            pool.invokeAll(runnables);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        averagePlot.other(resultCombiner.getAverage())
                .plotAverageFitness(name, false)
                .nextColor();
        bestPlot.other(resultCombiner.getAverage())
                .plotBestFitness(name, false)
                .nextColor();
    }
}
