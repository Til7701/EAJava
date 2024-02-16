package de.holube.ea.meta;

import io.jenetics.Gene;

public record MetaGene(MetaModel metaModel) implements Gene<MetaModel, MetaGene> {

    public MetaGene getRandom() {
        return new MetaGene(metaModel.getRandom());
    }

    @Override
    public MetaModel allele() {
        return metaModel;
    }

    @Override
    public MetaGene newInstance() {
        return new MetaGene(metaModel.getRandom());
    }

    @Override
    public MetaGene newInstance(MetaModel value) {
        return new MetaGene(value);
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public String toString() {
        return "MetaGene{" +
                "metaModel=" + metaModel +
                '}';
    }

}
