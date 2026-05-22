package com.letisim;

import com.letisim.dto.LetisimScenarioDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        log.info("  LETISim: Модуль генерации аналитики Heatmap         ");

        LetisimEngineMock engineMock = new LetisimEngineMock();

        try {
            // ВАРИАНТ 1: Анализ внешнего файла конфигурации
            String externalPath = "src/main/resources/strategy_10_fast_track.bpmn";
            BpsimXmlAdapter adapter = new BpsimXmlAdapter();
            LetisimScenarioDto externalScenario = adapter.parse(externalPath);
            engineMock.simulateExecution(externalScenario);



//            // ВАРИАНТ 2: Анализ собственного локального BPMN-файла процесса
//            String localPath = "src/main/resources/credit_card_application.bpmn";
//            BpsimParserFacade facade = new BpsimParserFacade();
//
//            List<LetisimScenarioDto> localScenarios = facade.parse(new File(localPath));
//            for (LetisimScenarioDto scenario : localScenarios) {
//                engineMock.simulateExecution(scenario);
//            }


            log.info("\nПайплайн симуляции успешно отработал.");

        } catch (Exception e) {
            log.error("Критическая ошибка выполнения: " + e.getMessage(), e);
        }
    }
}