package de.holube.ea;

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
        return Stream.generate(() -> 1)
                .limit(10)
                .parallel()
                .mapToInt(e -> {
                    First80s ea = new First80s();
                    int best = ea.run(metaModel.population(), metaModel.mutationRate(), metaModel.mutationRate());
                    return ea.getResults().size() - best;
                })
                .summaryStatistics().getMin();
    }

    public void run() {
        Factory<Genotype<MetaGene>> gtf = Genotype.of(MetaChromosome.of(new MetaGene(new MetaModel(348, 0.9))));

        Engine<MetaGene, Integer> engine = Engine
                .builder(Meta::evalMeta, gtf)
                .minimizing()
                .populationSize(4)
                .survivorsSelector(new TournamentSelector<>(2))
                //.offspringSelector(new RouletteWheelSelector<>())
                .alterers(
                        new Mutator<>(1)
                )
                .build();

        List<EvolutionResult<MetaGene, Integer>> results = engine.stream()
                //.limit(bySteadyFitness(50))
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