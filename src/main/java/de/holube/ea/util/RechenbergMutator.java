package de.holube.ea.util;

import io.jenetics.*;
import io.jenetics.internal.math.Probabilities;
import io.jenetics.util.RandomRegistry;
import io.jenetics.util.Seq;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.lang.Math.pow;

public class RechenbergMutator<G extends Gene<?, G>, C extends Comparable<? super C>> extends Mutator<G, C> {

    public static final int MAX = 5;
    private final List<C> generationsSinceLast = new ArrayList<>();
    @Getter
    List<Double> ps = new ArrayList<>(500);
    private C lastBest = null;
    private int generationCount = 0;
    private double probability;

    public RechenbergMutator(double probability) {
        super(0);
        this.probability = probability;
    }

    @Override
    public AltererResult<G, C> alter(final Seq<Phenotype<G, C>> population, final long generation) {
        // prepare
        generationCount++;
        Optional<Phenotype<G, C>> best = population.stream().sorted().findFirst();
        best.ifPresent(b -> generationsSinceLast.add(b.fitness()));

        // rechenberg
        if (generationCount == MAX) {
            if (lastBest != null) {
                final double successRate = generationsSinceLast.stream()
                        .filter(c -> c.compareTo(lastBest) > 0)
                        .count() / ((double) generationCount);

                final double factor = 1.01;
                if (successRate > 1.0 / 5) {
                    probability *= factor;
                    if (probability > 1) probability = 1;
                } else {
                    probability /= factor;
                }
                ps.add(probability);
            }

            C lastGenBest = generationsSinceLast.stream().sorted().findFirst().orElse(null);
            if (lastBest == null || (lastGenBest == null || lastGenBest.compareTo(lastBest) > 0))
                lastBest = lastGenBest;
            generationsSinceLast.clear();
            generationCount = 0;
        }

        // mutate normally
        final var random = RandomRegistry.random();
        final double p = pow(probability, 1.0 / 3.0);
        final int P = Probabilities.toInt(p);

        final Seq<MutatorResult<Phenotype<G, C>>> result = population
                .map(pt -> random.nextInt() < P
                        ? mutate(pt, generation, p, random)
                        : new MutatorResult<>(pt, 0));

        return new AltererResult<>(
                result.map(MutatorResult::result).asISeq(),
                result.stream().mapToInt(MutatorResult::mutations).sum()
        );
    }

}
