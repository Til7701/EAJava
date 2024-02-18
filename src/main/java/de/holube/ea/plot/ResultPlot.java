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
    private List<Integer> xGenerations;
    private GenerationStatisticsList generationStatistics;
    private boolean legend = false;
    private int iteration = 0;

    public ResultPlot(GenerationStatisticsList generationStatistics) {
        this.generationStatistics = generationStatistics;
        xGenerations = generationStatistics.getGenerationList();
        chart = new XYChartBuilder()
                .width(400).height(300)
                .theme(Styler.ChartTheme.Matlab)
                .xAxisTitle("Generations")
                .yAxisTitle("Fitness")
                .title("Fitness")
                .build();
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
        return plotAverageFitness("Average Fitness" + getIteration());
    }

    public ResultPlot plotAverageFitness(String label) {
        List<Double> yAverageFitness = generationStatistics.averageFitness();
        XYSeries series = chart.addSeries(label, xGenerations, yAverageFitness);
        series.setMarker(SeriesMarkers.NONE);
        return this;
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

    private String getIteration() {
        return iteration == 0 ? "" : String.valueOf(iteration);
    }

}
