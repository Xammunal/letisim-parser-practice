package com.letisim.model;

import javax.xml.bind.annotation.*;

/**
 * Привязка ресурса к элементу: {@code <bpsim:ResourceAssignmentExpression>}.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ResourceAssignment {

    @XmlElement(name = "Expression", namespace = Scenario.BPSIM_NS)
    private ExpressionHolder expression;

    public ExpressionHolder getExpression() { return expression; }

    /**
     * Возвращает ID назначенного ресурса.
     */
    public String getResourceId() {
        if (expression != null && expression.getParameterValue() != null) {
            return expression.getParameterValue().getFixedValue();
        }
        return null;
    }
}
