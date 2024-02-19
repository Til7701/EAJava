package de.holube.ea.meta;

import io.jenetics.Chromosome;
import io.jenetics.util.ISeq;

import java.util.Collections;
import java.util.Iterator;

public class MetaChromosome implements Chromosome<MetaGene> {

    private final MetaGene gene;

    public MetaChromosome(MetaGene gene) {
        this.gene = gene;
    }

    public static MetaChromosome of(MetaGene gene) {
        return new MetaChromosome(gene);
    }

    @Override
    public Chromosome<MetaGene> newInstance(ISeq<MetaGene> iSeq) {
        return new MetaChromosome(iSeq.get(0));
    }

    @Override
    public MetaGene get(int index) {
        if (index > 0)
            throw new IllegalArgumentException();
        return gene;
    }

    @Override
    public int length() {
        return 1;
    }

    @Override
    public Chromosome<MetaGene> newInstance() {
        return new MetaChromosome(gene.newInstance());
    }

    @Override
    public Iterator<MetaGene> iterator() {
        return Collections.singleton(gene).iterator();
    }

    @Override
    public boolean isValid() {
        return gene.isValid();
    }

    @Override
    public String toString() {
        return "MetaChromosome{" +
                "gene=" + gene +
                '}';
    }

}
