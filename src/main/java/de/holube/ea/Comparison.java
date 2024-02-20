package de.holube.ea;

import de.holube.ea.plot.ResultCombiner;
import de.holube.ea.plot.ResultPlot;
import de.holube.ea.util.GenerationStatisticsList;
import de.holube.ea.util.RechenbergMutator;
import io.jenetics.Gene;
import io.jenetics.engine.EvolutionResult;
import org.knowm.xchart.*;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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
        final List<List<Double>> pLists = Collections.synchronizedList(new ArrayList<>());
        final ResultCombiner resultCombiner = new ResultCombiner();
        List<Callable<GenerationStatisticsList>> runnables = IntStream.iterate(runs, count -> count > 0, count -> count - 1)
                .mapToObj(count -> 0).<Callable<GenerationStatisticsList>>map(i -> () -> {
                    First80sRechenberg ea = new First80sRechenberg();
                    ea.run(population, crossoverRate, mutationRate, bits);
                    var genericResults = ea.getResults().stream()
                            .<EvolutionResult<? extends Gene<?, ?>, Integer>>map(e -> e)
                            .toList();
                    resultCombiner.accept(new GenerationStatisticsList(genericResults));
                    pLists.add(ea.getPs());
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

        List<Double> ps = new ArrayList<>();
        for (int i = 0; i < pLists.getFirst().size(); i++) {
            int finalI = i;
            ps.add(i, pLists.stream()
                    .mapToDouble(l -> l.get(finalI))
                    .average().orElse(-1)
            );
        }
        plot(ps);
    }

    private void plot(List<Double> ps) {
        XYChart chart = new XYChartBuilder()
                .width(600).height(400)
                .theme(Styler.ChartTheme.Matlab)
                .xAxisTitle("Generations")
                .yAxisTitle("Mutation Rate")
                .title("Mutation Rate with Rechenberg Rule")
                .build();
        //chart.getStyler().setYAxisMin(0D);
        //chart.getStyler().setYAxisMin(1D);
        chart.getStyler().setYAxisTickMarkSpacingHint(100);
        chart.getStyler().setXAxisTickMarkSpacingHint(200);
        chart.getStyler().setXAxisLabelRotation(30);
        chart.getStyler().setLegendVisible(false);

        List<Integer> xGenerations = IntStream.iterate(0, i -> i + RechenbergMutator.MAX)
                .limit(ps.size())
                .boxed()
                .toList();

        XYSeries series = chart.addSeries("rate", xGenerations, ps);
        series.setMarker(SeriesMarkers.NONE);

        try {
            File outputDir = new File("./output");
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }
            VectorGraphicsEncoder.saveVectorGraphic(chart, "output/" + chart.getTitle(), VectorGraphicsEncoder.VectorGraphicsFormat.SVG);
            BitmapEncoder.saveBitmapWithDPI(chart, "output/" + chart.getTitle(), BitmapEncoder.BitmapFormat.JPG, 200);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
