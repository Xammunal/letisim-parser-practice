package com.letisim.mapper;

import com.letisim.BpsimMapper;
import com.letisim.dto.ElementParameterDto;
import com.letisim.dto.LetisimScenarioDto;
import org.bpsim.model.*;
import org.bpsim.model.Scenario;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Реализация маппера BPSim → LETISim DTO.
 *
 * <p>Преобразует JAXB-объекты {@link Scenario} в упрощённые
 * DTO {@link LetisimScenarioDto} для движка LETISim.</p>
 */
public class BpsimMapperImpl implements BpsimMapper {

    @Override
    public LetisimScenarioDto mapToDto(Scenario bpsimScenario) {
        if (bpsimScenario == null) {
            return null;
        }

        LetisimScenarioDto dto = new LetisimScenarioDto();

        // xs:attribute — JAXB отдаёт напрямую, без JAXBElement
        dto.setId(bpsimScenario.getId());
        dto.setName(bpsimScenario.getName());
        dto.setDescription(bpsimScenario.getDescription());
        dto.setAuthor(bpsimScenario.getAuthor());
        dto.setVersion(bpsimScenario.getVersion());

        Scenario.ScenarioParameters sp = bpsimScenario.getScenarioParameters();
        if (sp != null) {
            mapScenarioParameters(dto, sp);
        }

        // Calendar — simpleContent: имя через getName(), текст через getValue()
        List<Calendar> calendars = bpsimScenario.getCalendar();
        if (calendars != null && !calendars.isEmpty()) {
            dto.setCalendarNames(calendars.stream()
                    .map(cal -> cal.getName() != null ? cal.getName() : cal.getValue())
                    .collect(Collectors.toList()));
        } else {
            dto.setCalendarNames(Collections.emptyList());
        }

        // ElementParameters — маппим каждый элемент в ElementParameterDto
        List<Scenario.ElementParameters> elements = bpsimScenario.getElementParameters();
        dto.setElementParameterCount(elements != null ? elements.size() : 0);

        if (elements != null && !elements.isEmpty()) {
            List<ElementParameterDto> elementDtos = new ArrayList<>();
            for (Scenario.ElementParameters ep : elements) {
                elementDtos.add(mapElementParameter(ep));
            }
            dto.setElementParameters(elementDtos);
        } else {
            dto.setElementParameters(Collections.emptyList());
        }

        return dto;
    }

    private void mapScenarioParameters(LetisimScenarioDto dto, ScenarioParameters sp) {
        dto.setBaseCurrencyUnit(sp.getBaseCurrencyUnit());

        // TimeUnit — enum, конвертируем в строку через .value()
        TimeUnit timeUnit = sp.getBaseTimeUnit();
        dto.setBaseTimeUnit(timeUnit != null ? timeUnit.value() : "min");

        dto.setReplication(sp.getReplication());
        dto.setSeed(sp.getSeed());

        // JAXB генерирует Boolean (не boolean), поэтому нужна null-проверка
        dto.setTraceOutput(Boolean.TRUE.equals(sp.isTraceOutput()));
    }

    /**
     * Маппит один JAXB ElementParameters в ElementParameterDto.
     * Извлекает распределение из TimeParameters, стоимость из CostParameters
     * и вероятность из ControlParameters.
     */
    private ElementParameterDto mapElementParameter(Scenario.ElementParameters ep) {
        ElementParameterDto dto = new ElementParameterDto();

        dto.setId(ep.getId());
        dto.setElementRef(ep.getElementRef() != null ? ep.getElementRef().getLocalPart() : null);

        // TimeParameters → ProcessingTime → Distribution
        TimeParameters tp = ep.getTimeParameters();
        if (tp != null && tp.getProcessingTime() != null) {
            Parameter processingTime = tp.getProcessingTime();
            List<JAXBElement<? extends ParameterValue>> values = processingTime.getParameterValue();
            if (values != null && !values.isEmpty()) {
                ParameterValue pv = values.get(0).getValue();
                extractDistribution(dto, pv);
            }
        }

        // CostParameters → UnitCost → FloatingParameter
        CostParameters cp = ep.getCostParameters();
        if (cp != null && cp.getUnitCost() != null) {
            Parameter unitCost = cp.getUnitCost();
            List<JAXBElement<? extends ParameterValue>> values = unitCost.getParameterValue();
            if (values != null && !values.isEmpty()) {
                ParameterValue pv = values.get(0).getValue();
                if (pv instanceof FloatingParameter) {
                    dto.setUnitCost(((FloatingParameter) pv).getValue());
                }
            }
        }

        // ControlParameters → Probability → FloatingParameter
        ControlParameters ctrl = ep.getControlParameters();
        if (ctrl != null && ctrl.getProbability() != null) {
            Parameter prob = ctrl.getProbability();
            List<JAXBElement<? extends ParameterValue>> values = prob.getParameterValue();
            if (values != null && !values.isEmpty()) {
                ParameterValue pv = values.get(0).getValue();
                if (pv instanceof FloatingParameter) {
                    dto.setProbability(((FloatingParameter) pv).getValue());
                }
            }
        }

        // ResourceParameters → Quantity, Role
        ResourceParameters rp = ep.getResourceParameters();
        if (rp != null) {
            if (rp.getQuantity() != null) {
                Parameter q = rp.getQuantity();
                List<JAXBElement<? extends ParameterValue>> values = q.getParameterValue();
                if (values != null && !values.isEmpty()) {
                    ParameterValue pv = values.get(0).getValue();
                    if (pv instanceof FloatingParameter) {
                        dto.setResourceQuantity(((FloatingParameter) pv).getValue());
                    } else if (pv instanceof NumericParameter) {
                        dto.setResourceQuantity(Double.valueOf(((NumericParameter) pv).getValue()));
                    }
                }
            }

            if (rp.getRole() != null && !rp.getRole().isEmpty()) {
                Parameter roleParam = rp.getRole().get(0);
                List<JAXBElement<? extends ParameterValue>> values = roleParam.getParameterValue();
                if (values != null && !values.isEmpty()) {
                    ParameterValue pv = values.get(0).getValue();
                    if (pv instanceof StringParameter) {
                        dto.setResourceRole(((StringParameter) pv).getValue());
                    }
                }
            }
        }

        return dto;
    }

    /**
     * Извлекает параметры распределения из ParameterValue в DTO.
     * Поддерживает NormalDistribution, UniformDistribution и другие.
     */
    private void extractDistribution(ElementParameterDto dto, ParameterValue pv) {
        if (pv instanceof NormalDistribution) {
            NormalDistribution nd = (NormalDistribution) pv;
            dto.setDistributionType("NormalDistribution");
            dto.setDistributionMean(nd.getMean());
            dto.setDistributionStdDev(nd.getStandardDeviation());
        } else if (pv instanceof UniformDistribution) {
            UniformDistribution ud = (UniformDistribution) pv;
            dto.setDistributionType("UniformDistribution");
            dto.setDistributionMin(ud.getMin());
            dto.setDistributionMax(ud.getMax());
        } else if (pv instanceof TriangularDistribution) {
            TriangularDistribution td = (TriangularDistribution) pv;
            dto.setDistributionType("TriangularDistribution");
            dto.setDistributionMin(td.getMin());
            dto.setDistributionMax(td.getMax());
            dto.setDistributionMean(td.getMode());
        } else if (pv instanceof NegativeExponentialDistribution) {
            NegativeExponentialDistribution ned = (NegativeExponentialDistribution) pv;
            dto.setDistributionType("ExponentialDistribution");
            dto.setDistributionMean(ned.getMean());
        } else if (pv instanceof PoissonDistribution) {
            PoissonDistribution pd = (PoissonDistribution) pv;
            dto.setDistributionType("PoissonDistribution");
            dto.setDistributionMean(pd.getMean());
        } else {
            dto.setDistributionType(pv.getClass().getSimpleName());
        }
    }
}
