package com.letisim.model;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * Сегмент расписания: {@code <bpsim:TimeSegment>}.
 *
 * <p>Определяет рабочий интервал для конкретных дней недели.</p>
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class TimeSegment {

    @XmlAttribute
    private String startTime;

    @XmlAttribute
    private String duration;

    @XmlElement(name = "WeekDay", namespace = Scenario.BPSIM_NS)
    private List<String> weekDays;

    // ── Getters ──

    public String getStartTime() { return startTime; }
    public String getDuration() { return duration; }
    public List<String> getWeekDays() { return weekDays; }

    @Override
    public String toString() {
        return "TimeSegment{start=" + startTime + ", duration=" + duration +
                ", days=" + weekDays + "}";
    }
}
