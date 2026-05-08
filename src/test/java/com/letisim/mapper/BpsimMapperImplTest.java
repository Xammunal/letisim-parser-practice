package com.letisim.mapper;

import com.letisim.dto.LetisimScenarioDto;
import org.bpsim.model.Calendar;
import org.bpsim.model.Scenario;
import org.bpsim.model.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BpsimMapperImplTest {

    private BpsimMapperImpl mapper;

    @BeforeEach
    void setUp() {
        mapper = new BpsimMapperImpl();
    }

    private Scenario buildFullScenario() {
        Scenario.ScenarioParameters sp = new Scenario.ScenarioParameters();
        sp.setBaseCurrencyUnit("EUR");
        sp.setBaseTimeUnit(TimeUnit.HOUR);
        sp.setReplication(10);
        sp.setSeed(42L);
        sp.setTraceOutput(true);

        Calendar cal = new Calendar();
        cal.setName("WorkingHours");
        cal.setValue("MON-FRI 09:00-18:00");

        Scenario.ElementParameters ep1 = new Scenario.ElementParameters();
        ep1.setId("EP_Task1");
        Scenario.ElementParameters ep2 = new Scenario.ElementParameters();
        ep2.setId("EP_Task2");

        Scenario scenario = new Scenario();
        scenario.setId("Scenario_1");
        scenario.setName("Test Scenario");
        scenario.setDescription("Тестовый сценарий");
        scenario.setAuthor("Vanya");
        scenario.setVersion("1.0");
        scenario.setScenarioParameters(sp);
        scenario.getCalendar().add(cal);
        scenario.getElementParameters().add(ep1);
        scenario.getElementParameters().add(ep2);

        return scenario;
    }

    @Test
    @DisplayName("Базовые атрибуты сценария маппятся корректно")
    void testScenarioAttributes() {
        LetisimScenarioDto dto = mapper.mapToDto(buildFullScenario());

        assertNotNull(dto);
        assertEquals("Scenario_1",       dto.getId());
        assertEquals("Test Scenario",    dto.getName());
        assertEquals("Тестовый сценарий", dto.getDescription());
        assertEquals("Vanya",            dto.getAuthor());
        assertEquals("1.0",              dto.getVersion());
    }

    @Test
    @DisplayName("ScenarioParameters маппятся корректно")
    void testScenarioParameters() {
        LetisimScenarioDto dto = mapper.mapToDto(buildFullScenario());

        assertEquals("EUR",  dto.getBaseCurrencyUnit());
        assertEquals("hour", dto.getBaseTimeUnit());
        assertEquals(10,     dto.getReplication());
        assertEquals(42L,    dto.getSeed());
        assertTrue(dto.isTraceOutput());
    }

    @Test
    @DisplayName("Имена календарей извлекаются корректно")
    void testCalendars() {
        LetisimScenarioDto dto = mapper.mapToDto(buildFullScenario());

        List<String> names = dto.getCalendarNames();
        assertEquals(1, names.size());
        assertEquals("WorkingHours", names.get(0));
    }

    @Test
    @DisplayName("Счётчик ElementParameters совпадает с числом элементов")
    void testElementParameterCount() {
        LetisimScenarioDto dto = mapper.mapToDto(buildFullScenario());

        assertEquals(2, dto.getElementParameterCount());
    }

    @Test
    @DisplayName("null на входе → null на выходе")
    void testNullInput() {
        assertNull(mapper.mapToDto(null));
    }

    @Test
    @DisplayName("Сценарий без ScenarioParameters — поля остаются null/дефолт")
    void testScenarioWithoutParameters() {
        Scenario scenario = new Scenario();
        scenario.setId("Scenario_Min");
        scenario.setName("Minimal");

        LetisimScenarioDto dto = mapper.mapToDto(scenario);

        assertNotNull(dto);
        assertNull(dto.getBaseCurrencyUnit());
        assertFalse(dto.isTraceOutput());
        assertEquals(0, dto.getElementParameterCount());
    }

    @Test
    @DisplayName("Calendar без name — используется value как запасное значение")
    void testCalendarFallbackToValue() {
        Calendar cal = new Calendar();
        cal.setValue("MON-FRI 09:00-17:00");

        Scenario scenario = new Scenario();
        scenario.setId("S1");
        scenario.getCalendar().add(cal);

        LetisimScenarioDto dto = mapper.mapToDto(scenario);

        assertEquals("MON-FRI 09:00-17:00", dto.getCalendarNames().get(0));
    }
}
