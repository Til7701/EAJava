package de.holube.ea;

import de.holube.ea.util.AbstractEA;
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
public class First80s extends AbstractEA {

    private List<EvolutionResult<BitGene, Integer>> results = null;

    private static int eval(Genotype<BitGene> gt) {
        return gt.chromosome()
                .as(BitChromosome.class)
                .bitCount();
    }

    public int run(int population, double crossoverProbability, double mutationRate, int bits) {
        Factory<Genotype<BitGene>> gtf = Genotype.of(BitChromosome.of(bits, 0.5));

        Engine<BitGene, Integer> engine = Engine
                .builder(First80s::eval, gtf)
                .populationSize(population)
                .offspringSelector(new TournamentSelector<>(3))
                .survivorsSelector(new EliteSelector<>())
                .alterers(
                        new Mutator<>(mutationRate),
                        new SinglePointCrossover<>(crossoverProbability)
                )
                .build();

        results = engine.stream()
                //.limit(bySteadyFitness(50))
                .limit(1500)
                .toList();
        final Phenotype<BitGene, Integer> best = results.stream().collect(EvolutionResult.toBestPhenotype());
        return best.fitness();
    }

}