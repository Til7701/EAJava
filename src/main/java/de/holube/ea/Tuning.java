package de.holube.ea;

import de.holube.ea.plot.ResultPlot;
import de.holube.ea.util.GenerationStatisticsList;
import io.jenetics.Gene;
import io.jenetics.engine.EvolutionResult;

public class Tuning {

    public static void main(String[] args) {
        First80s ea = new First80s();
        int best = ea.run(50, 0.6, 0.001, 32);
        System.out.println(best);

        var genericResults = ea.getResults().stream()
                .<EvolutionResult<? extends Gene<?, ?>, Integer>>map(e -> e)
                .toList();
        new ResultPlot(new GenerationStatisticsList(genericResults)).plotAll();
    }

}
