package com.letisim;

import org.bpsim.model.BPSimData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.InputStream;
import java.net.URL;

/**
 * Сервис для безопасного парсинга файлов BPSim 2.0.
 *
 * <p>ВНИМАНИЕ (Вариант Б): Строгая XSD-валидация временно отключена
 * для обеспечения совместимости с некорректными (но одобренными)
 * файлами из сторонних редакторов (мягкий парсинг).</p>
 */
public class BpsimParserService implements BpsimParser {

    private static final Logger log = LoggerFactory.getLogger(BpsimParserService.class);

    /** XSD-файл внутри classpath (src/main/resources/xsd/BPSim.xsd) */
    private static final String XSD_RESOURCE_PATH = "xsd/BPSim.xsd";

    private final JAXBContext jaxbContext;
    private final Schema bpsimSchema;

    public BpsimParserService() {
        log.info("Инициализация BpsimParserService...");

        try {
            this.jaxbContext = JAXBContext.newInstance(BPSimData.class);
            log.info("JAXBContext создан для {}", BPSimData.class.getName());
        } catch (JAXBException e) {
            throw new IllegalStateException(
                    "Не удалось создать JAXBContext для org.bpsim.model", e);
        }

        try {
            URL xsdUrl = getClass().getClassLoader().getResource(XSD_RESOURCE_PATH);
            if (xsdUrl == null) {
                throw new IllegalStateException(
                        "XSD-схема не найдена в classpath: " + XSD_RESOURCE_PATH);
            }

            SchemaFactory schemaFactory =
                    SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            this.bpsimSchema = schemaFactory.newSchema(xsdUrl);
            log.info("XSD-схема BPSim скомпилирована: {}", xsdUrl);
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Не удалось скомпилировать XSD-схему BPSim", e);
        }

        log.info("BpsimParserService инициализирован успешно");
    }

    @Override
    public BPSimData parseBpsim(InputStream xmlStream) {
        log.info("Начинаем парсинг BPSim XML (МЯГКИЙ РЕЖИМ)...");

        try {
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            // unmarshaller.setSchema(bpsimSchema);
            log.warn("ВНИМАНИЕ: Строгая XSD-валидация отключена! Парсер игнорирует некритичные отклонения от стандарта.");

            // Парсим без валидации (сработает как и на стороннем ресурсе)
            Object result = unmarshaller.unmarshal(xmlStream);

            if (!(result instanceof BPSimData)) {
                throw new BpsimValidationException(
                        "Корневой элемент XML не является BPSimData. "
                                + "Получен: " + result.getClass().getName());
            }

            BPSimData data = (BPSimData) result;
            log.info("Парсинг завершён успешно Сценариев: {}",
                    data.getScenario().size());

            return data;

        } catch (JAXBException e) {
            String msg = "Ошибка при десериализации BPSim XML";
            if (e.getLinkedException() != null) {
                msg += ": " + e.getLinkedException().getMessage();
            }
            log.error("Ошибка парсинга {}", msg);
            throw new BpsimValidationException(msg, e);
        }
    }
}