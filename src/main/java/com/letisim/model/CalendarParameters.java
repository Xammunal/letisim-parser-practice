package com.letisim.model;

import javax.xml.bind.annotation.*;

/**
 * Параметры календаря: {@code <bpsim:CalendarParameters>}.
 *
 * <p>Определяет расписание доступности ресурсов.</p>
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class CalendarParameters {

    @XmlAttribute
    private String id;

    @XmlElement(name = "CalendarId", namespace = Scenario.BPSIM_NS)
    private String calendarId;

    @XmlElement(name = "AvailableTime", namespace = Scenario.BPSIM_NS)
    private AvailableTime availableTime;

    // ── Getters ──

    public String getId() { return id; }
    public String getCalendarId() { return calendarId; }
    public AvailableTime getAvailableTime() { return availableTime; }

    @Override
    public String toString() {
        return "CalendarParameters{id='" + id + "', calendarId='" + calendarId + "'}";
    }
}
