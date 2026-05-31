package com.letisim;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Утилита для извлечения блока <BPSimData> из файлов .bpmn
 * и "лечения" некорректного XML на лету.
 */
public class BpsimExtractor {

    public static InputStream extractFromBpmn(File bpmnFile) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        Document doc = factory.newDocumentBuilder().parse(bpmnFile);

        NodeList bpsimNodes = doc.getElementsByTagNameNS("http://www.bpsim.org/schemas/2.0", "BPSimData");

        if (bpsimNodes.getLength() == 0) {
            bpsimNodes = doc.getElementsByTagName("bpsim:BPSimData");
            if (bpsimNodes.getLength() == 0) {
                throw new IllegalArgumentException("В файле BPMN не найден блок BPSimData");
            }
        }

        Node bpsimNode = bpsimNodes.item(0);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.transform(new DOMSource(bpsimNode), new StreamResult(outputStream));

        // Превращаем байты в строку, чтобы быстро поправить ошибки стороннего генератора
        String rawXml = outputStream.toString(StandardCharsets.UTF_8);

        // 1. Исправляем ошибку генератора: ElementParameter -> ElementParameters
        rawXml = rawXml.replace("<bpsim:ElementParameter", "<bpsim:ElementParameters");
        rawXml = rawXml.replace("</bpsim:ElementParameter>", "</bpsim:ElementParameters>");

        // 2. Заодно исправляем ту самую опечатку с валютой
        rawXml = rawXml.replace("baseCurrency=", "baseCurrencyUnit=");

        // Возвращаем вылеченный поток обратно
        return new ByteArrayInputStream(rawXml.getBytes(StandardCharsets.UTF_8));
    }
}