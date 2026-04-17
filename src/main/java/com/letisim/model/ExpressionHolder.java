package com.letisim.model;

import javax.xml.bind.annotation.*;

/**
 * Выражение-обёртка: {@code <bpsim:Expression>}.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ExpressionHolder {

    @XmlElement(name = "ParameterValue", namespace = Scenario.BPSIM_NS)
    private ParameterValue parameterValue;

    public ParameterValue getParameterValue() { return parameterValue; }
}
