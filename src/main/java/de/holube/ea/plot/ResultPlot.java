package de.holube.ea.plot;

import de.holube.ea.util.GenerationStatisticsList;
import org.knowm.xchart.*;
import org.knowm.xchart.internal.chartpart.Chart;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class ResultPlot {

    private final XYChart chart;
    private final List<Color> colors = List.of(new Color(0, 0, 200), new Color(100, 200, 0), Color.RED);
    private List<Integer> xGenerations;
    private GenerationStatisticsList generationStatistics;
    private boolean legend = false;
    private int iteration = 0;
    private int colorIndex = 0;

    public ResultPlot(String title) {
        chart = new XYChartBuilder()
                .width(600).height(400)
                .theme(Styler.ChartTheme.Matlab)
                .xAxisTitle("Generations")
                .yAxisTitle("Fitness")
                .title(title)
                .build();
        //chart.getStyler().setYAxisMin(1D);
        chart.getStyler().setYAxisTickMarkSpacingHint(100);
        chart.getStyler().setXAxisMin(32D);
        chart.getStyler().setXAxisTickMarkSpacingHint(200);
        chart.getStyler().setXAxisLabelRotation(30);
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
            BitmapEncoder.saveBitmapWithDPI(chart, "output/" + chart.getTitle(), BitmapEncoder.BitmapFormat.JPG, 200);
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
                .nextColor()
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
        prepareSeries(series, transparent);
        return this;
    }

    public ResultPlot plotBestFitness() {
        return plotBestFitness("Best Fitness" + getIteration(), false);
    }

    public ResultPlot plotBestFitness(boolean transparent) {
        return plotBestFitness("Best Fitness" + getIteration(), transparent);
    }

    public ResultPlot plotBestFitness(String label, boolean transparent) {
        if (generationStatistics instanceof ResultCombiner.CombinedResults combinedResults && combinedResults.getSdBestFitnessPos() != null) {
            List<Double> yBestFitnessSDPos = combinedResults.getSdBestFitnessPos();
            XYSeries series = chart.addSeries(label + "SD Pos", xGenerations, yBestFitnessSDPos);
            prepareSeries(series, true);
            List<Double> yBestFitnessSDNeg = combinedResults.getSdBestFitnessNeg();
            XYSeries series1 = chart.addSeries(label + "SD Neg", xGenerations, yBestFitnessSDNeg);
            prepareSeries(series1, true);
        }
        List<Double> yBestFitness = generationStatistics.bestFitness();
        XYSeries series = chart.addSeries(label, xGenerations, yBestFitness);
        prepareSeries(series, transparent);
        return this;
    }

    private void prepareSeries(XYSeries series, boolean transparent) {
        series.setMarker(SeriesMarkers.NONE);
        Color color = colors.get(colorIndex);
        if (transparent) {
            color = new Color(color.getRed(), color.getGreen(), color.getBlue(), 255).brighter();
            series.setLineWidth(0.5f);
            series.setShowInLegend(false);
        }
        series.setLineColor(color);
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
