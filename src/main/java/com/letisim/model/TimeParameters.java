package com.letisim.model;

import javax.xml.bind.annotation.*;

/**
 * Временные параметры: {@code <bpsim:TimeParameters>}.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class TimeParameters {

    @XmlElement(name = "Duration", namespace = Scenario.BPSIM_NS)
    private ParameterValueHolder duration;

    public ParameterValueHolder getDuration() { return duration; }
}
