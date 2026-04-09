package com.letisim.mapper;

import com.letisim.dto.LetisimScenarioDto;
import org.bpsim.model.Calendar;
import org.bpsim.model.Scenario;
import org.bpsim.model.ScenarioParameters;
import org.bpsim.model.TimeUnit;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BpsimMapperImpl implements com.letisim.BpsimMapper {

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

        List<Scenario.ElementParameters> elements = bpsimScenario.getElementParameters();
        dto.setElementParameterCount(elements != null ? elements.size() : 0);

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
}
