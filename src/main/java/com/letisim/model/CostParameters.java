package com.letisim.model;

import javax.xml.bind.annotation.*;

/**
 * Параметры стоимости: {@code <bpsim:CostParameters>}.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class CostParameters {

    @XmlElement(name = "UnitCost", namespace = Scenario.BPSIM_NS)
    private ParameterValueHolder unitCost;

    public ParameterValueHolder getUnitCost() { return unitCost; }
}
