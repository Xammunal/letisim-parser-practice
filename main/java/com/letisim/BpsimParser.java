package com.letisim;

import org.bpsim.model.BPSimData;
import java.io.InputStream;

/**
 * Контракт для парсинга BPSim XML-документов.
 *
 * <p>Реализация должна выполнять XSD-валидацию и JAXB-десериализацию,
 * возвращая типизированный {@link BPSimData}.</p>
 */
public interface BpsimParser {

    /**
     * Парсит входящий XML-поток BPSim с XSD-валидацией.
     *
     * @param xmlStream входной поток с XML-документом BPSim
     * @return десериализованный {@link BPSimData}
     * @throws BpsimValidationException если XML не прошёл XSD-валидацию
     */
    BPSimData parseBpsim(InputStream xmlStream);
}