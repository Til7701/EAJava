package de.holube.ea;

import de.holube.ea.util.AbstractEA;
import io.jenetics.*;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.util.Factory;
import io.jenetics.util.ISeq;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

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
        MetaModel metaModel = gt.gene().metaModel();
        Factory<Genotype<BitGene>> gtf = Genotype.of(BitChromosome.of(32, 0.1));

        Engine<BitGene, Integer> engine = Engine
                .builder(Meta::eval, gtf)
                .populationSize(metaModel.population())
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
        return results.size();
    }

    public void run() {
        Factory<Genotype<MetaGene>> gtf = Genotype.of(MetaChromosome.of(ISeq.of(new MetaGene(new MetaModel(20)))));

        Engine<MetaGene, Integer> engine = Engine
                .builder(Meta::evalMeta, gtf)
                .populationSize(4)
                //.survivorsSelector(new TournamentSelector<>(5))
                //.offspringSelector(new RouletteWheelSelector<>())
                .alterers(
                        new Mutator<>(0.15)
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
        plot(genericResults);
    }

    private record MetaModel(int population) {
        public static MetaModel getRandom() {
            return new MetaModel(
                    ThreadLocalRandom.current().nextInt(5000)
            );
        }

        @Override
        public String toString() {
            return "MetaModel{" +
                    "population=" + population +
                    '}';
        }
    }

    private record MetaGene(MetaModel metaModel) implements Gene<MetaModel, MetaGene> {
        public static MetaGene getRandom() {
            return new MetaGene(MetaModel.getRandom());
        }

        @Override
        public MetaModel allele() {
            return metaModel;
        }

        @Override
        public MetaGene newInstance() {
            return new MetaGene(MetaModel.getRandom());
        }

        @Override
        public MetaGene newInstance(MetaModel value) {
            return new MetaGene(value);
        }

        @Override
        public boolean isValid() {
            return true;
        }
    }

    private static class MetaChromosome implements Chromosome<MetaGene> {

        private final int length;
        private ISeq<MetaGene> iSeq;

        public MetaChromosome(ISeq<MetaGene> genes) {
            this.iSeq = genes;
            this.length = iSeq.length();
        }

        public static MetaChromosome of(ISeq<MetaGene> genes) {
            return new MetaChromosome(genes);
        }

        @Override
        public Chromosome<MetaGene> newInstance(ISeq<MetaGene> iSeq) {
            this.iSeq = iSeq;
            return this;
        }

        @Override
        public MetaGene get(int index) {
            return iSeq.get(index);
        }

        @Override
        public int length() {
            return iSeq.length();
        }

        @Override
        public Chromosome<MetaGene> newInstance() {
            ISeq<MetaGene> genes = ISeq.empty();
            for (int i = 0; i < length; i++) {
                genes = genes.append(MetaGene.getRandom());
            }
            return new MetaChromosome(genes);
        }

        @Override
        public Iterator<MetaGene> iterator() {
            return iSeq.iterator();
        }

        @Override
        public boolean isValid() {
            return iSeq.stream().allMatch(MetaGene::isValid);
        }
    }

}