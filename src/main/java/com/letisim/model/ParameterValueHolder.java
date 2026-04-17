package com.letisim.model;

import javax.xml.bind.annotation.*;

/**
 * Обёртка для {@code <bpsim:ParameterValue>} внутри
 * Amount, CostPerHour, Duration, UnitCost, Probability и т.д.
 *
 * <p>Универсальный класс, переиспользуемый во всех местах BPSim,
 * где значение задаётся через ParameterValue.</p>
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ParameterValueHolder {

    @XmlElement(name = "ParameterValue", namespace = Scenario.BPSIM_NS)
    private ParameterValue parameterValue;

    public ParameterValue getParameterValue() { return parameterValue; }

    /**
     * Возвращает человекочитаемое значение для вывода.
     */
    public String getDisplayValue() {
        if (parameterValue == null) return "N/A";
        if (parameterValue.getNormalDistribution() != null) {
            NormalDistribution nd = parameterValue.getNormalDistribution();
            return "Normal(mean=" + nd.getMean() + ", std=" + nd.getStandardDeviation() + ")";
        }
        if (parameterValue.getFixedValue() != null) {
            return parameterValue.getFixedValue();
        }
        return "N/A";
    }
}
