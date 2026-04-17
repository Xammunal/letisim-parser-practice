package com.letisim.model;

import javax.xml.bind.annotation.*;

/**
 * Параметры элемента BPMN-процесса: {@code <bpsim:ElementParameters>}.
 *
 * <p>Связывает конкретный BPMN-элемент (task, event, sequenceFlow)
 * с параметрами симуляции: время, стоимость, ресурсы, вероятности.</p>
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ElementParameters {

    @XmlAttribute
    private String id;

    @XmlAttribute
    private String elementRef;

    @XmlElement(name = "TimeParameters", namespace = Scenario.BPSIM_NS)
    private TimeParameters timeParameters;

    @XmlElement(name = "CostParameters", namespace = Scenario.BPSIM_NS)
    private CostParameters costParameters;

    @XmlElement(name = "ControlParameters", namespace = Scenario.BPSIM_NS)
    private ControlParameters controlParameters;

    @XmlElement(name = "ResourceAssignmentExpression", namespace = Scenario.BPSIM_NS)
    private ResourceAssignment resourceAssignment;

    // ── Getters ──

    public String getId() { return id; }
    public String getElementRef() { return elementRef; }
    public TimeParameters getTimeParameters() { return timeParameters; }
    public CostParameters getCostParameters() { return costParameters; }
    public ControlParameters getControlParameters() { return controlParameters; }
    public ResourceAssignment getResourceAssignment() { return resourceAssignment; }

    @Override
    public String toString() {
        return "ElementParameters{id='" + id + "', elementRef='" + elementRef + "'}";
    }
}
