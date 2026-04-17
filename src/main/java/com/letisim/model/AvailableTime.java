package com.letisim.model;

import javax.xml.bind.annotation.*;

/**
 * Доступное время: {@code <bpsim:AvailableTime>}.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class AvailableTime {

    @XmlElement(name = "Duration", namespace = Scenario.BPSIM_NS)
    private String duration;

    @XmlElement(name = "TimeTable", namespace = Scenario.BPSIM_NS)
    private TimeTable timeTable;

    // ── Getters ──

    public String getDuration() { return duration; }
    public TimeTable getTimeTable() { return timeTable; }
}
