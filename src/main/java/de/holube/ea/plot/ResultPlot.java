package de.holube.ea.plot;

import de.holube.ea.util.GenerationStatisticsList;
import org.knowm.xchart.VectorGraphicsEncoder;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.internal.chartpart.Chart;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class ResultPlot {

    private final XYChart chart;
    private final List<Color> colors = List.of(Color.BLUE, new Color(0, 150, 0), Color.RED);
    private List<Integer> xGenerations;
    private GenerationStatisticsList generationStatistics;
    private boolean legend = false;
    private int iteration = 0;
    private int colorIndex = 0;

    public ResultPlot(String title) {
        chart = new XYChartBuilder()
                .width(400).height(300)
                .theme(Styler.ChartTheme.Matlab)
                .xAxisTitle("Generations")
                .yAxisTitle("Fitness")
                .title(title)
                .build();
    }

    public ResultPlot(GenerationStatisticsList generationStatistics) {
        this("Fitness");
        this.generationStatistics = generationStatistics;
        xGenerations = generationStatistics.getGenerationList();
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
        return plotAverageFitness("Average Fitness" + getIteration(), false);
    }

    public ResultPlot plotAverageFitness(boolean transparent) {
        return plotAverageFitness("Average Fitness" + getIteration(), transparent);
    }

    public ResultPlot plotAverageFitness(String label, boolean transparent) {
        List<Double> yAverageFitness = generationStatistics.averageFitness();
        XYSeries series = chart.addSeries(label, xGenerations, yAverageFitness);
        series.setMarker(SeriesMarkers.NONE);
        Color color = colors.get(colorIndex);
        if (transparent) color = getMoreTransparent(color);
        series.setLineColor(color);
        return this;
    }

    private Color getMoreTransparent(Color color) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), 2);
    }

    public ResultPlot plotBestFitness() {
        return plotBestFitness("Best Fitness" + getIteration());
    }

    public ResultPlot plotBestFitness(String label) {
        List<Double> yBestFitness = generationStatistics.bestFitness();
        XYSeries series = chart.addSeries(label, xGenerations, yBestFitness);
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

    public ResultPlot other(GenerationStatisticsList generationStatistics) {
        this.generationStatistics = generationStatistics;
        xGenerations = generationStatistics.getGenerationList();
        iteration++;
        return this;
    }

    public ResultPlot nextColor() {
        colorIndex++;
        return this;
    }

    private String getIteration() {
        return iteration == 0 ? "" : String.valueOf(iteration);
    }

}
