package com.letisim.model;

import javax.xml.bind.annotation.*;

/**
 * Нормальное распределение: {@code <bpsim:NormalDistribution>}.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class NormalDistribution {

    @XmlAttribute
    private double mean;

    @XmlAttribute
    private double standardDeviation;

    // ── Getters ──

    public double getMean() { return mean; }
    public double getStandardDeviation() { return standardDeviation; }

    @Override
    public String toString() {
        return "NormalDistribution{mean=" + mean + ", std=" + standardDeviation + "}";
    }
}
