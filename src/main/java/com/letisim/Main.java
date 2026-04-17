package com.letisim;

import com.letisim.model.*;

import java.io.InputStream;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   LETISim BPSim Parser v2.0");
        System.out.println("========================================");
        System.out.println();

        try {
            // 1. Инициализируем парсер
            BpsimParser parser = new BpsimParser();

            // 2. Открываем BPMN-файл из classpath
            InputStream bpmnStream = Main.class.getClassLoader()
                    .getResourceAsStream("credit_card_application.bpmn");

            if (bpmnStream == null) {
                System.out.println("Ошибка: Файл credit_card_application.bpmn не найден в classpath!");
                return;
            }

            // 3. Парсим BPMN -> извлекаем BPSim Scenario
            Scenario scenario = parser.parse(bpmnStream);

            // 4. Выводим результат
            System.out.println();
            System.out.println("Парсинг прошёл успешно!");
            System.out.println("========================================");
            System.out.println("Сценарий:  " + scenario.getName() + " (id=" + scenario.getId() + ")");

            // -- Глобальные параметры --
            ScenarioParameters sp = scenario.getScenarioParameters();
            if (sp != null) {
                System.out.println();
                System.out.println("[Глобальные параметры]");
                System.out.println("  Кол-во запусков:   " + sp.getProcessInstances());
                System.out.println("  Начальное событие: " + sp.getBeginEvent());
                System.out.println("  Валюта:            " + sp.getBaseCurrency());
                System.out.println("  Random Seed:       " + sp.getRandomSeed());
            }

            // -- Календари --
            List<CalendarParameters> calendars = scenario.getCalendarParameters();
            if (calendars != null && !calendars.isEmpty()) {
                System.out.println();
                System.out.println("[Календари] (" + calendars.size() + "):");
                for (CalendarParameters cal : calendars) {
                    System.out.println("  * " + cal.getCalendarId());
                    if (cal.getAvailableTime() != null && cal.getAvailableTime().getTimeTable() != null) {
                        for (TimeSegment seg : cal.getAvailableTime().getTimeTable().getTimeSegments()) {
                            System.out.println("    start=" + seg.getStartTime()
                                    + ", duration=" + seg.getDuration()
                                    + ", days=" + seg.getWeekDays());
                        }
                    }
                }
            }

            // -- Ресурсы --
            List<ResourceParameters> resources = scenario.getResourceParameters();
            if (resources != null && !resources.isEmpty()) {
                System.out.println();
                System.out.println("[Ресурсы] (" + resources.size() + "):");
                for (ResourceParameters r : resources) {
                    System.out.println("  * " + r.getResourceName()
                            + " (id=" + r.getResourceId()
                            + ", кол-во=" + r.getAmount().getDisplayValue()
                            + ", стоимость/ч=" + r.getCostPerHour().getDisplayValue()
                            + ", календарь=" + r.getCalendarRef() + ")");
                }
            }

            // -- Элементы процесса --
            List<ElementParameters> elements = scenario.getElementParameters();
            if (elements != null && !elements.isEmpty()) {
                System.out.println();
                System.out.println("[Элементы процесса] (" + elements.size() + "):");
                for (ElementParameters el : elements) {
                    System.out.println("  * " + el.getElementRef());

                    if (el.getTimeParameters() != null && el.getTimeParameters().getDuration() != null) {
                        System.out.println("    Длительность: " + el.getTimeParameters().getDuration().getDisplayValue());
                    }
                    if (el.getCostParameters() != null && el.getCostParameters().getUnitCost() != null) {
                        System.out.println("    Стоимость:    " + el.getCostParameters().getUnitCost().getDisplayValue());
                    }
                    if (el.getResourceAssignment() != null) {
                        System.out.println("    Ресурс:       " + el.getResourceAssignment().getResourceId());
                    }
                    if (el.getControlParameters() != null && el.getControlParameters().getProbability() != null) {
                        System.out.println("    Вероятность:  " + el.getControlParameters().getProbability().getDisplayValue());
                    }
                }
            }

            System.out.println();
            System.out.println("========================================");

        } catch (Exception e) {
            System.out.println("Ошибка при парсинге:");
            e.printStackTrace();
        }
    }
}