package com.letisim.model;

import javax.xml.bind.annotation.*;

/**
 * Параметры ресурса: {@code <bpsim:ResourceParameters>}.
 *
 * <p>Описывает исполнителя процесса с количеством,
 * стоимостью и привязкой к расписанию.</p>
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ResourceParameters {

    @XmlAttribute
    private String id;

    @XmlElement(name = "ResourceId", namespace = Scenario.BPSIM_NS)
    private String resourceId;

    @XmlElement(name = "ResourceName", namespace = Scenario.BPSIM_NS)
    private String resourceName;

    @XmlElement(name = "Amount", namespace = Scenario.BPSIM_NS)
    private ParameterValueHolder amount;

    @XmlElement(name = "CostPerHour", namespace = Scenario.BPSIM_NS)
    private ParameterValueHolder costPerHour;

    @XmlElement(name = "CalendarRef", namespace = Scenario.BPSIM_NS)
    private String calendarRef;

    // ── Getters ──

    public String getId() { return id; }
    public String getResourceId() { return resourceId; }
    public String getResourceName() { return resourceName; }
    public ParameterValueHolder getAmount() { return amount; }
    public ParameterValueHolder getCostPerHour() { return costPerHour; }
    public String getCalendarRef() { return calendarRef; }

    @Override
    public String toString() {
        return "ResourceParameters{resourceId='" + resourceId +
                "', name='" + resourceName +
                "', amount=" + (amount != null ? amount.getDisplayValue() : "?") +
                ", costPerHour=" + (costPerHour != null ? costPerHour.getDisplayValue() : "?") +
                ", calendar='" + calendarRef + "'}";
    }
}
