package com.letisim;

import com.letisim.model.Scenario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import java.io.InputStream;

/**
 * Парсер BPMN-файлов с извлечением симуляционных данных BPSim.
 *
 * <p>Читает стандартный BPMN 2.0 XML-файл, находит встроенный блок
 * {@code <bpsim:Scenario>} и десериализует его
 * в объект {@link Scenario}.</p>
 *
 * <p>Особенности реализации:</p>
 * <ul>
 *   <li>JAXBContext создаётся один раз (thread-safe, тяжёлая операция).</li>
 *   <li>Unmarshaller создаётся на каждый вызов parse() — он НЕ thread-safe.</li>
 *   <li>DOM-парсинг используется для навигации по BPMN-документу и
 *       выделения BPSim-фрагмента для JAXB.</li>
 * </ul>
 */
public class BpsimParser {

    private static final Logger log = LoggerFactory.getLogger(BpsimParser.class);

    /** Namespace BPSim 1.0 */
    private static final String BPSIM_NAMESPACE = "http://www.bpsim.org/schemas/1.0";

    /** Имя корневого элемента BPSim внутри BPMN */
    private static final String BPSIM_ROOT_ELEMENT = "Scenario";

    private final JAXBContext jaxbContext;

    public BpsimParser() {
        log.info("Инициализация BpsimParser...");
        try {
            this.jaxbContext = JAXBContext.newInstance(Scenario.class);
            log.info("JAXBContext создан для {}", Scenario.class.getName());
        } catch (JAXBException e) {
            throw new IllegalStateException(
                    "Не удалось создать JAXBContext для модели BPSim", e);
        }
        log.info("BpsimParser инициализирован успешно");
    }

    /**
     * Парсит BPMN-файл и извлекает блок BPSim-симуляции.
     *
     * <p>Алгоритм:
     * <ol>
     *   <li>Парсит весь BPMN XML в DOM-дерево.</li>
     *   <li>Ищет элемент {@code <bpsim:Scenario>}
     *       по namespace {@value #BPSIM_NAMESPACE}.</li>
     *   <li>Десериализует найденный DOM-узел через JAXB
     *       в {@link Scenario}.</li>
     * </ol>
     *
     * @param bpmnStream входной поток с BPMN XML-документом
     * @return извлечённый сценарий BPSim {@link Scenario}
     * @throws BpsimValidationException если файл не содержит BPSim-данных
     *         или имеет неверный формат
     */
    public Scenario parse(InputStream bpmnStream) {
        log.info("Начинаем парсинг BPMN-файла...");

        try {
            // 1. Парсим весь BPMN в DOM
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(bpmnStream);
            log.info("BPMN XML загружен в DOM");

            // 2. Ищем <bpsim:Scenario>
            NodeList simNodes = doc.getElementsByTagNameNS(BPSIM_NAMESPACE, BPSIM_ROOT_ELEMENT);

            if (simNodes.getLength() == 0) {
                throw new BpsimValidationException(
                        "В BPMN-файле не найден блок <bpsim:Scenario>. " +
                        "Убедитесь, что файл содержит BPSim-расширение симуляции " +
                        "(namespace: " + BPSIM_NAMESPACE + ").");
            }

            Element simElement = (Element) simNodes.item(0);
            log.info("Найден блок <bpsim:Scenario>");

            // 3. Десериализуем DOM-узел через JAXB
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            Scenario result = (Scenario) unmarshaller.unmarshal(simElement);

            log.info("Парсинг завершён успешно. {}", result);
            return result;

        } catch (BpsimValidationException e) {
            throw e;
        } catch (Exception e) {
            String msg = "Ошибка при парсинге BPMN/BPSim";
            if (e.getMessage() != null) {
                msg += ": " + e.getMessage();
            }
            log.error("Ошибка парсинга: {}", msg);
            throw new BpsimValidationException(msg, e);
        }
    }
}
