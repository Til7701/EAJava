package de.holube.ea.meta;

import io.jenetics.Chromosome;
import io.jenetics.util.ISeq;

import java.util.Iterator;

public class MetaChromosome implements Chromosome<MetaGene> {

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

    @Override
    public String toString() {
        return "MetaChromosome{" +
                "iSeq=" + iSeq +
                '}';
    }

}
