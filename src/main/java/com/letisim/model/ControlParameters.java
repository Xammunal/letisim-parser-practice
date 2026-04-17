package com.letisim.model;

import javax.xml.bind.annotation.*;

/**
 * Параметры управления: {@code <bpsim:ControlParameters>}.
 *
 * <p>Содержит вероятность прохождения по ветке (для XOR-шлюзов).</p>
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ControlParameters {

    @XmlElement(name = "Probability", namespace = Scenario.BPSIM_NS)
    private ParameterValueHolder probability;

    public ParameterValueHolder getProbability() { return probability; }
}
