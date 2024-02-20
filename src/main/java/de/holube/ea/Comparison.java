package de.holube.ea;

import de.holube.ea.plot.ResultCombiner;
import de.holube.ea.plot.ResultPlot;
import de.holube.ea.util.GenerationStatisticsList;
import io.jenetics.Gene;
import io.jenetics.engine.EvolutionResult;

import java.util.stream.Stream;

public class Comparison {

    private final ResultPlot averagePlot = new ResultPlot("Average Fitness").setLegend(true);
    private final ResultPlot bestPlot = new ResultPlot("Best Fitness").setLegend(true);
    private final ResultPlot averageAllPlot = new ResultPlot("Average Fitness with all Runs").setLegend(true);
    private final ResultPlot bestAllPlot = new ResultPlot("Best Fitness with all Runs").setLegend(true);

    public static void main(String[] args) {
        new Comparison().run(1000);
    }

    public void run(final int runs) {
        runSingle(runs, 50, 0.5, 0.001, "Algorithm 1");
        runSingle(runs, 50, 0.5, 0.9, "Algorithm 2");

        averageAllPlot.plot();
        bestAllPlot.plot();
        averagePlot.plot();
        bestPlot.plot();
    }

    private void runSingle(final int runs,
                           final int population, final double crossoverRate, final double mutationRate,
                           String name
    ) {
        final ResultCombiner resultCombiner = new ResultCombiner();
        Stream.generate(() -> 0).limit(runs).parallel()
                .map(i -> {
                    First80s ea = new First80s();
                    ea.run(population, crossoverRate, mutationRate);
                    var genericResults = ea.getResults().stream()
                            .<EvolutionResult<? extends Gene<?, ?>, Integer>>map(e -> e)
                            .toList();
                    resultCombiner.accept(new GenerationStatisticsList(genericResults));
                    return new GenerationStatisticsList(genericResults);
                }).toList().forEach(list -> {
                    averageAllPlot.other(list).plotAverageFitness(true);
                    bestAllPlot.other(list).plotBestFitness(true);
                });
        averageAllPlot.other(resultCombiner.getAverage())
                .plotAverageFitness(name + " Average of " + runs + " Runs", false)
                .nextColor();
        bestAllPlot.other(resultCombiner.getAverage())
                .plotBestFitness(name + " Average of " + runs + " Runs", false)
                .nextColor();
        averagePlot.other(resultCombiner.getAverage())
                .plotAverageFitness(name + " Average of " + runs + " Runs", false)
                .nextColor();
        bestPlot.other(resultCombiner.getAverage())
                .plotBestFitness(name + " Average of " + runs + " Runs", false)
                .nextColor();
    }
}
