package com.letisim.model;

import javax.xml.bind.annotation.*;

/**
 * Глобальные параметры сценария: {@code <bpsim:ScenarioParameters>}.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ScenarioParameters {

    @XmlAttribute
    private String id;

    @XmlAttribute
    private int processInstances;

    @XmlAttribute
    private String beginEvent;

    @XmlAttribute
    private String baseCurrency;

    @XmlElement(name = "RandomSeed", namespace = Scenario.BPSIM_NS)
    private String randomSeed;

    // ── Getters ──

    public String getId() { return id; }
    public int getProcessInstances() { return processInstances; }
    public String getBeginEvent() { return beginEvent; }
    public String getBaseCurrency() { return baseCurrency; }
    public String getRandomSeed() { return randomSeed; }

    @Override
    public String toString() {
        return "ScenarioParameters{processInstances=" + processInstances +
                ", beginEvent='" + beginEvent + "', baseCurrency='" + baseCurrency +
                "', seed=" + randomSeed + "}";
    }
}
