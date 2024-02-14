package de.holube.ea.util;

import io.jenetics.Gene;
import io.jenetics.engine.EvolutionResult;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.knowm.xchart.VectorGraphicsEncoder;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.internal.chartpart.Chart;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.io.File;
import java.io.IOException;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.NONE)
public abstract class AbstractEA {

    public static void plot(List<EvolutionResult<? extends Gene<?, ?>, Integer>> results) {
        GenerationStatisticsList generationStatistics = new GenerationStatisticsList(results);

        // Curate Data
        List<Integer> xGenerations = generationStatistics.getGenerationList();
        List<Double> yAverageFitness = generationStatistics.averageFitness();
        List<Double> yBestFitness = generationStatistics.bestFitness();

        // Prepare Chart
        XYChart chart = create()
                .title("Average Fitness")
                .xAxisTitle("Generations")
                .yAxisTitle("Average Fitness")
                .build();
        chart.getStyler().setLegendVisible(false);
        // Setting Series
        XYSeries series = chart.addSeries("Average Fitness", xGenerations, yAverageFitness);
        series.setMarker(SeriesMarkers.NONE);
        saveChart(chart);

        // Prepare Chart
        chart = create()
                .title("Best Fitness")
                .xAxisTitle("Generations")
                .yAxisTitle("Best Fitness")
                .build();
        chart.getStyler().setLegendVisible(false);
        // Setting Series
        series = chart.addSeries("Best Fitness", xGenerations, yBestFitness);
        series.setMarker(SeriesMarkers.NONE);
        saveChart(chart);
    }

    private static XYChartBuilder create() {
        return new XYChartBuilder()
                .width(400).height(300)
                .theme(Styler.ChartTheme.Matlab);
    }

    private static void saveChart(Chart<?, ?> chart) {
        try {
            File outputDir = new File("./output");
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }
            VectorGraphicsEncoder.saveVectorGraphic(chart, "output/" + chart.getTitle(), VectorGraphicsEncoder.VectorGraphicsFormat.SVG);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
