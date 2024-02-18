package de.holube.ea;

import de.holube.ea.plot.ResultCombiner;
import de.holube.ea.plot.ResultPlot;
import de.holube.ea.util.GenerationStatisticsList;
import io.jenetics.Gene;
import io.jenetics.engine.EvolutionResult;

import java.util.stream.Stream;

public class Comparison {

    public static void main(String[] args) {
        ResultPlot averagePlot = new ResultPlot("Average Fitness");
        ResultPlot bestPlot = new ResultPlot("Best Fitness");
        final int runs = 100;

        final ResultCombiner resultCombiner = new ResultCombiner();
        Stream.generate(() -> 0).limit(runs).parallel()
                .map(i -> {
                    First80s ea = new First80s();
                    ea.run(50, 0.9, 0.001);
                    var genericResults = ea.getResults().stream()
                            .<EvolutionResult<? extends Gene<?, ?>, Integer>>map(e -> e)
                            .toList();
                    resultCombiner.accept(new GenerationStatisticsList(genericResults));
                    return new GenerationStatisticsList(genericResults);

                }).toList().forEach(list -> averagePlot.other(list).plotAverageFitness(true));
        averagePlot.other(resultCombiner.getAverage())
                .plotAverageFitness()
                .nextColor();
        bestPlot.other(resultCombiner.getAverage())
                .plotBestFitness()
                .nextColor();

        final ResultCombiner resultCombiner1 = new ResultCombiner();
        Stream.generate(() -> 0).limit(runs).parallel()
                .map(i -> {
                    First80s ea = new First80s();
                    ea.run(25, 0.2, 0.5);
                    var genericResults = ea.getResults().stream()
                            .<EvolutionResult<? extends Gene<?, ?>, Integer>>map(e -> e)
                            .toList();
                    resultCombiner1.accept(new GenerationStatisticsList(genericResults));
                    return new GenerationStatisticsList(genericResults);

                }).toList().forEach(list -> averagePlot.other(list).plotAverageFitness(true));
        averagePlot.other(resultCombiner1.getAverage())
                .plotAverageFitness()
                .plot();
        bestPlot.other(resultCombiner1.getAverage())
                .plotBestFitness()
                .plot();
    }

}
