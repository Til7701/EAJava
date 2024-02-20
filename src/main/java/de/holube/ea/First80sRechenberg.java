package de.holube.ea;

import de.holube.ea.plot.ResultPlot;
import de.holube.ea.util.AbstractEA;
import de.holube.ea.util.GenerationStatisticsList;
import de.holube.ea.util.RechenbergMutator;
import io.jenetics.*;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.util.Factory;
import lombok.Getter;
import org.knowm.xchart.*;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Eiben: Parameter Control in Evolutionary Algorithms p.2
 */
@Getter
public class First80sRechenberg extends AbstractEA {

    private List<EvolutionResult<BitGene, Integer>> results = null;
    @Getter
    private List<Double> ps = null;


    private static int eval(Genotype<BitGene> gt) {
        return gt.chromosome()
                .as(BitChromosome.class)
                .bitCount();
    }

    public static void main(String[] args) {
        First80sRechenberg ea = new First80sRechenberg();
        ea.run(2, 0, 0.5, 32);
        var genericResults = ea.getResults().stream()
                .<EvolutionResult<? extends Gene<?, ?>, Integer>>map(e -> e)
                .toList();
        ResultPlot resultPlot = new ResultPlot(new GenerationStatisticsList(genericResults));
        resultPlot.plotBestFitness()
                .nextColor()
                .plotAverageFitness()
                .plot();
    }

    public int run(int population, double crossoverProbability, double mutationRate, int bits) {
        Factory<Genotype<BitGene>> gtf = Genotype.of(BitChromosome.of(bits, 0.5));
        final RechenbergMutator<BitGene, Integer> rechenbergMutator = new RechenbergMutator<>(mutationRate);

        Engine<BitGene, Integer> engine = Engine
                .builder(First80sRechenberg::eval, gtf)
                .populationSize(population)
                .offspringSelector(new TournamentSelector<>(3))
                .survivorsSelector(new EliteSelector<>())
                .alterers(
                        rechenbergMutator,
                        new SinglePointCrossover<>(crossoverProbability)
                )
                .build();

        results = engine.stream()
                //.limit(bySteadyFitness(50))
                .limit(1500)
                .toList();

        ps = rechenbergMutator.getPs();
        final Phenotype<BitGene, Integer> best = results.stream().collect(EvolutionResult.toBestPhenotype());
        return best.fitness();
    }

}