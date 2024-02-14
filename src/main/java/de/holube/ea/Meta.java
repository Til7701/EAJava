package de.holube.ea;

import de.holube.ea.meta.MetaChromosome;
import de.holube.ea.meta.MetaGene;
import de.holube.ea.meta.MetaModel;
import de.holube.ea.plot.ResultPlot;
import de.holube.ea.util.AbstractEA;
import io.jenetics.*;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.util.Factory;
import io.jenetics.util.ISeq;

import java.util.IntSummaryStatistics;
import java.util.List;

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
        final int limit = 500;
        final MetaModel metaModel = gt.gene().metaModel();
        final IntSummaryStatistics summaryStatistics = new IntSummaryStatistics();

        for (int i = 0; i < 10; i++) {
            First80s algo = new First80s();
            int best = algo.run(metaModel.population(), metaModel.mutationRate(), metaModel.mutationRate());
            summaryStatistics.accept(limit - algo.getResults().size() + best);
        }
        return (int) summaryStatistics.getAverage();
    }

    public void run() {
        Factory<Genotype<MetaGene>> gtf = Genotype.of(MetaChromosome.of(ISeq.of(new MetaGene(MetaModel.getRandom()))));

        Engine<MetaGene, Integer> engine = Engine
                .builder(Meta::evalMeta, gtf)
                .populationSize(4)
                //.survivorsSelector(new TournamentSelector<>(5))
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
        ResultPlot.plot(genericResults);
    }

}