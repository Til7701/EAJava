package de.holube.ea.plot;

import de.holube.ea.util.GenerationStatisticsList;
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

public class ResultPlot {

    private final XYChart chart;
    private final GenerationStatisticsList generationStatistics;
    private final List<Integer> xGenerations;
    private boolean legend = false;

    public ResultPlot(GenerationStatisticsList generationStatistics) {
        this.generationStatistics = generationStatistics;
        xGenerations = generationStatistics.getGenerationList();
        chart = new XYChartBuilder()
                .width(600).height(400)
                .theme(Styler.ChartTheme.Matlab)
                .xAxisTitle("Generations")
                .yAxisTitle("Fitness")
                .title("Fitness")
                .build();
        chart.getStyler().setYAxisMin(1D);
        chart.getStyler().setYAxisTickMarkSpacingHint(100);
        chart.getStyler().setXAxisMin(1D);
        chart.getStyler().setXAxisTickMarkSpacingHint(200);
        chart.getStyler().setXAxisLabelRotation(30);
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

    public void plotAllSeparately() {
        new ResultPlot(generationStatistics)
                .plotBestFitness()
                .setTitle("Best Fitness")
                .plot();

        new ResultPlot(generationStatistics)
                .plotAverageFitness()
                .setTitle("Average Fitness")
                .plot();
    }

    public void plotAll() {
        this.plotBestFitness()
                .plotAverageFitness()
                .setLegend(true)
                .plot();

        this.plotAllSeparately();
    }

    public void plot() {
        chart.getStyler().setLegendVisible(legend);
        saveChart(chart);
    }

    public ResultPlot plotAverageFitness() {
        List<Double> yAverageFitness = generationStatistics.averageFitness();
        XYSeries series = chart.addSeries("Average Fitness", xGenerations, yAverageFitness);
        series.setMarker(SeriesMarkers.NONE);
        return this;
    }

    public ResultPlot plotBestFitness() {
        List<Double> yBestFitness = generationStatistics.bestFitness();
        XYSeries series = chart.addSeries("Best Fitness", xGenerations, yBestFitness);
        series.setMarker(SeriesMarkers.NONE);
        return this;
    }

    public ResultPlot setLegend(boolean value) {
        this.legend = value;
        if (legend) {
            chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideSE);
        }
        return this;
    }

    public ResultPlot setTitle(String title) {
        chart.setTitle(title);
        return this;
    }

}
