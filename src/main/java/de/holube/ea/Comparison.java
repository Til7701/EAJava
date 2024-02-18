package de.holube.ea;

import de.holube.ea.plot.ResultCombiner;
import de.holube.ea.plot.ResultPlot;
import de.holube.ea.util.GenerationStatisticsList;
import io.jenetics.Gene;
import io.jenetics.engine.EvolutionResult;

public class Comparison {

    public static void main(String[] args) {
        ResultPlot averagePlot = new ResultPlot("Average Fitness");
        ResultPlot bestPlot = new ResultPlot("Best Fitness");
        final int runs = 100;

        ResultCombiner resultCombiner = new ResultCombiner();
        for (int i = 0; i < runs; i++) {
            First80s ea = new First80s();
            ea.run(50, 0.6, 0.001);
            var genericResults = ea.getResults().stream()
                    .<EvolutionResult<? extends Gene<?, ?>, Integer>>map(e -> e)
                    .toList();
            resultCombiner.accept(new GenerationStatisticsList(genericResults));
            averagePlot.other(new GenerationStatisticsList(genericResults)).plotAverageFitness(true);
        }
        averagePlot.other(resultCombiner.getAverage())
                .plotAverageFitness()
                .nextColor();
        bestPlot.other(resultCombiner.getAverage())
                .plotBestFitness()
                .nextColor();

        resultCombiner = new ResultCombiner();
        for (int i = 0; i < runs; i++) {
            First80s ea = new First80s();
            ea.run(50, 0.6, 0.1);
            var genericResults = ea.getResults().stream()
                    .<EvolutionResult<? extends Gene<?, ?>, Integer>>map(e -> e)
                    .toList();
            resultCombiner.accept(new GenerationStatisticsList(genericResults));
            averagePlot.other(new GenerationStatisticsList(genericResults)).plotAverageFitness(true);
        }
        averagePlot.other(resultCombiner.getAverage())
                .plotAverageFitness()
                .plot();
        bestPlot.other(resultCombiner.getAverage())
                .plotBestFitness()
                .plot();
    }

}
