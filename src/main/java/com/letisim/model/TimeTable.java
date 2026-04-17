package com.letisim.model;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * Таблица расписания: {@code <bpsim:TimeTable>}.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class TimeTable {

    @XmlElement(name = "TimeSegment", namespace = Scenario.BPSIM_NS)
    private List<TimeSegment> timeSegments;

    public List<TimeSegment> getTimeSegments() { return timeSegments; }
}
