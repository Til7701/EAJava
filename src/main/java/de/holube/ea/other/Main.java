package de.holube.ea.other;

import de.holube.ea.plot.ResultPlot;
import de.holube.ea.util.AbstractEA;
import de.holube.ea.util.GenerationStatisticsList;
import io.jenetics.*;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.util.Factory;

import java.util.List;

import static io.jenetics.engine.Limits.bySteadyFitness;

public class Main extends AbstractEA {

    public static void main(String[] args) {
        new Main().run();
    }

    private static int eval(Genotype<BitGene> gt) {
        return gt.chromosome()
                .as(BitChromosome.class)
                .bitCount();
    }

    private void run() {
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

        final List<EvolutionResult<BitGene, Integer>> results = engine.stream()
                .limit(bySteadyFitness(50))
                .limit(500)
                .toList();
        final Genotype<BitGene> best = results.stream().collect(EvolutionResult.toBestGenotype());
        System.out.println(best);

        List<EvolutionResult<? extends Gene<?, ?>, Integer>> genericResults = results.stream()
                .<EvolutionResult<? extends Gene<?, ?>, Integer>>map(e -> e)
                .toList();
        new ResultPlot(new GenerationStatisticsList(genericResults)).plotAll();
    }

}