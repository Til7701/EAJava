package de.holube.ea;

import de.holube.ea.meta.MetaChromosome;
import de.holube.ea.meta.MetaGene;
import de.holube.ea.meta.MetaModel;
import de.holube.ea.util.AbstractEA;
import io.jenetics.*;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.util.Factory;
import io.jenetics.util.ISeq;
import io.jenetics.util.RandomRegistry;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import static io.jenetics.engine.Limits.bySteadyFitness;

public class Meta extends AbstractEA {

    private static final int META_RANDOM_SEED = 245698;

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
        RandomRegistry.random(new Random(META_RANDOM_SEED));

        MetaModel metaModel = gt.gene().metaModel();
        Factory<Genotype<BitGene>> gtf = Genotype.of(BitChromosome.of(8, 0.1));

        Engine<BitGene, Integer> engine = Engine
                .builder(Meta::eval, gtf)
                .populationSize(metaModel.population())
                .alterers(
                        new Mutator<>(metaModel.mutationRate())
                )
                .build();

        List<EvolutionResult<BitGene, Integer>> results = engine.stream()
                .limit(bySteadyFitness(50))
                .limit(limit)
                .toList();

        final Optional<Integer> bestFitness = results.stream()
                .map(EvolutionResult::bestFitness)
                .max(Integer::compare);
        return limit - results.size() + bestFitness.orElse(0);
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
                .limit(bySteadyFitness(50))
                .limit(500)
                .toList();
        Genotype<MetaGene> best = results.stream().collect(EvolutionResult.toBestGenotype());
        System.out.println(best);

        List<EvolutionResult<? extends Gene<?, ?>, Integer>> genericResults = results.stream()
                .<EvolutionResult<? extends Gene<?, ?>, Integer>>map(e -> e)
                .toList();
        plot(genericResults);
    }

}