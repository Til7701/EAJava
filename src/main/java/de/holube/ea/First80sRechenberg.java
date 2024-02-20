package de.holube.ea;

import de.holube.ea.util.AbstractEA;
import de.holube.ea.util.RechenbergMutator;
import io.jenetics.*;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.util.Factory;
import lombok.Getter;

import java.util.List;

/**
 * Eiben: Parameter Control in Evolutionary Algorithms p.2
 */
@Getter
public class First80sRechenberg extends AbstractEA {

    private List<EvolutionResult<BitGene, Integer>> results = null;

    private static int eval(Genotype<BitGene> gt) {
        return gt.chromosome()
                .as(BitChromosome.class)
                .bitCount();
    }

    public static void main(String[] args) {
        new First80sRechenberg().run(50, 0, 0.1);
    }

    public int run(int population, double crossoverProbability, double mutationRate) {
        Factory<Genotype<BitGene>> gtf = Genotype.of(BitChromosome.of(32, 0.5));

        Engine<BitGene, Integer> engine = Engine
                .builder(First80sRechenberg::eval, gtf)
                .populationSize(population)
                .offspringSelector(new RouletteWheelSelector<>())
                .survivorsSelector(new EliteSelector<>())
                .alterers(
                        //new SinglePointCrossover<>(crossoverProbability),
                        new RechenbergMutator<>(mutationRate)
                )
                .build();

        results = engine.stream()
                //.limit(bySteadyFitness(50))
                .limit(500)
                .toList();
        final Phenotype<BitGene, Integer> best = results.stream().collect(EvolutionResult.toBestPhenotype());
        return best.fitness();
    }

}