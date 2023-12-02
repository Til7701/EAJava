package de.holube.ea.util;

import io.jenetics.BitGene;
import io.jenetics.engine.EvolutionResult;
import org.knowm.xchart.VectorGraphicsEncoder;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.internal.chartpart.Chart;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.io.IOException;
import java.util.List;

public abstract class AbstractEA {

    public static void plot(List<EvolutionResult<BitGene, Integer>> results) {
        GenerationStatisticsList generationStatistics = new GenerationStatisticsList(results);

        // Curate Data
        List<Integer> xGenerations = generationStatistics.getGenerationList();
        List<Double> yMedianFitness = generationStatistics.medianFitness();

        // Prepare Chart
        XYChart chart = create()
                .title("Median Fitness")
                .xAxisTitle("Generations")
                .yAxisTitle("Median Fitness")
                .build();
        chart.getStyler().setLegendVisible(false);

        // Setting Series
        XYSeries series = chart.addSeries("Median Fitness", xGenerations, yMedianFitness);
        series.setMarker(SeriesMarkers.NONE);

        saveChart(chart);
    }

    private static XYChartBuilder create() {
        return new XYChartBuilder()
                .width(400).height(300)
                .theme(Styler.ChartTheme.Matlab);
    }

    private static void saveChart(Chart chart) {
        try {
            VectorGraphicsEncoder.saveVectorGraphic(chart, "./output", VectorGraphicsEncoder.VectorGraphicsFormat.SVG);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
