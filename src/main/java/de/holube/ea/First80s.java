package de.holube.ea;

import de.holube.ea.util.AbstractEA;
import io.jenetics.*;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.util.Factory;
import lombok.Getter;

import java.util.List;

@Getter
public class First80s extends AbstractEA {

    private List<EvolutionResult<BitGene, Integer>> results = null;

    public static void main(String[] args) {
        First80s algo = new First80s();
        int best = algo.run(50, 0.6, 0.001);
        System.out.println(best);

        List<EvolutionResult<? extends Gene<?, ?>, Integer>> genericResults = algo.getResults().stream()
                .<EvolutionResult<? extends Gene<?, ?>, Integer>>map(e -> e)
                .toList();
        plot(genericResults);
    }

    private static int eval(Genotype<BitGene> gt) {
        return gt.chromosome()
                .as(BitChromosome.class)
                .bitCount();
    }

    public int run(int population, double crossoverProbability, double mutationRate) {
        Factory<Genotype<BitGene>> gtf = Genotype.of(BitChromosome.of(32, 0.1));

        Engine<BitGene, Integer> engine = Engine
                .builder(First80s::eval, gtf)
                .populationSize(population)
                .offspringSelector(new RouletteWheelSelector<>())
                .survivorsSelector(new EliteSelector<>())
                .alterers(
                        new SinglePointCrossover<>(crossoverProbability),
                        new Mutator<>(mutationRate)
                )
                .build();

        results = engine.stream()
                //.limit(bySteadyFitness(50))
                .limit(1000)
                .toList();
        final Phenotype<BitGene, Integer> best = results.stream().collect(EvolutionResult.toBestPhenotype());
        return best.fitness();
    }

}