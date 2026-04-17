package com.letisim.model;

import javax.xml.bind.annotation.*;

/**
 * Значение параметра: {@code <bpsim:ParameterValue>}.
 *
 * <p>Может содержать либо фиксированное значение ({@code <bpsim:FixedValue>}),
 * либо нормальное распределение ({@code <bpsim:NormalDistribution>}).</p>
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ParameterValue {

    @XmlElement(name = "FixedValue", namespace = Scenario.BPSIM_NS)
    private String fixedValue;

    @XmlElement(name = "NormalDistribution", namespace = Scenario.BPSIM_NS)
    private NormalDistribution normalDistribution;

    // ── Getters ──

    public String getFixedValue() { return fixedValue; }
    public NormalDistribution getNormalDistribution() { return normalDistribution; }

    @Override
    public String toString() {
        if (normalDistribution != null) return normalDistribution.toString();
        return "FixedValue=" + fixedValue;
    }
}
