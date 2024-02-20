package de.holube.ea.other;

import de.holube.ea.DefaultEA;
import de.holube.ea.meta.MetaChromosome;
import de.holube.ea.meta.MetaGene;
import de.holube.ea.meta.MetaModel;
import de.holube.ea.plot.ResultPlot;
import de.holube.ea.util.AbstractEA;
import de.holube.ea.util.GenerationStatisticsList;
import io.jenetics.*;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.util.Factory;

import java.util.List;
import java.util.stream.Stream;

import static io.jenetics.engine.Limits.bySteadyFitness;

public class Meta extends AbstractEA {

    public static void main(String[] args) {
        new Meta().run();
    }

    private static int eval(Genotype<BitGene> gt) {
        return gt.chromosome()
                .as(BitChromosome.class)
                .bitCount();
    }

    private static int evalMeta(Genotype<MetaGene> gt) {
        final MetaModel metaModel = gt.gene().metaModel();
        return (int) Stream.generate(() -> 1)
                .limit(100)
                .parallel()
                .mapToInt(e -> {
                    DefaultEA ea = new DefaultEA();
                    int best = ea.run(metaModel.population(), metaModel.crossoverRate(), metaModel.mutationRate(), 32);
                    return ea.getResults().size() - best;
                })
                .summaryStatistics().getAverage();
    }

    public void run() {
        Factory<Genotype<MetaGene>> gtf = Genotype.of(MetaChromosome.of(new MetaGene(new MetaModel(10, 0.1, 0.5))));

        Engine<MetaGene, Integer> engine = Engine
                .builder(Meta::evalMeta, gtf)
                .minimizing()
                .populationSize(20)
                .offspringSelector(new TournamentSelector<>(2))
                .survivorsSelector(new EliteSelector<>())
                .alterers(
                        new Mutator<>(0.1)
                )
                .build();

        List<EvolutionResult<MetaGene, Integer>> results = engine.stream()
                .limit(bySteadyFitness(50))
                .limit(500)
                .toList();
        Genotype<MetaGene> best = results.stream().collect(EvolutionResult.toBestGenotype());
        System.out.println(best);

        List<EvolutionResult<? extends Gene<?, ?>, Integer>> genericResults = results.stream()
                .<EvolutionResult<? extends Gene<?, ?>, Integer>>map(e -> e)
                .toList();
        new ResultPlot(new GenerationStatisticsList(genericResults)).plotAll();
    }

}