package de.holube.ea;

import de.holube.ea.util.AbstractEA;
import io.jenetics.*;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.util.Factory;

import java.util.List;

import static io.jenetics.engine.Limits.bySteadyFitness;

public class Main extends AbstractEA {

    private static int eval(Genotype<BitGene> gt) {
        return gt.chromosome()
                .as(BitChromosome.class)
                .bitCount();
    }

    public static void main(String[] args) {
        Factory<Genotype<BitGene>> gtf = Genotype.of(BitChromosome.of(32, 0.1));

        Engine<BitGene, Integer> engine = Engine
                .builder(Main::eval, gtf)
                .populationSize(400)
                //.survivorsSelector(new TournamentSelector<>(5))
                //.offspringSelector(new RouletteWheelSelector<>())
                .alterers(
                        new Mutator<>(0.15)
                )
                .build();

        List<EvolutionResult<BitGene, Integer>> results = engine.stream()
                .limit(bySteadyFitness(50))
                .limit(500)
                .toList();
        Genotype<BitGene> best = results.stream().collect(EvolutionResult.toBestGenotype());

        System.out.println(best);
        plot(results);
    }

}