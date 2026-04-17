package com.letisim.model;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * Корневой элемент BPSim-симуляции: {@code <bpsim:Scenario>}.
 *
 * <p>Содержит полную конфигурацию сценария симуляции,
 * встроенного в BPMN-файл. Включает глобальные параметры,
 * календари, ресурсы и параметры отдельных элементов процесса.</p>
 */
@XmlRootElement(name = "Scenario", namespace = Scenario.BPSIM_NS)
@XmlAccessorType(XmlAccessType.FIELD)
public class Scenario {

    public static final String BPSIM_NS = "http://www.bpsim.org/schemas/1.0";

    @XmlAttribute
    private String id;

    @XmlAttribute
    private String name;

    @XmlElement(name = "ScenarioParameters", namespace = BPSIM_NS)
    private ScenarioParameters scenarioParameters;

    @XmlElement(name = "CalendarParameters", namespace = BPSIM_NS)
    private List<CalendarParameters> calendarParameters;

    @XmlElement(name = "ResourceParameters", namespace = BPSIM_NS)
    private List<ResourceParameters> resourceParameters;

    @XmlElement(name = "ElementParameters", namespace = BPSIM_NS)
    private List<ElementParameters> elementParameters;

    // ── Getters ──

    public String getId() { return id; }
    public String getName() { return name; }
    public ScenarioParameters getScenarioParameters() { return scenarioParameters; }
    public List<CalendarParameters> getCalendarParameters() { return calendarParameters; }
    public List<ResourceParameters> getResourceParameters() { return resourceParameters; }
    public List<ElementParameters> getElementParameters() { return elementParameters; }

    @Override
    public String toString() {
        return "Scenario{id='" + id + "', name='" + name + "'}";
    }
}
