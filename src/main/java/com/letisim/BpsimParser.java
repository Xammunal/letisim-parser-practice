package com.letisim;

import org.bpsim.model.BPSimData;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

public interface BpsimParser {
    // Асинхронный метод парсинга (используется API)
    CompletableFuture<BPSimData> parseBpsim(InputStream xmlStream);

    // Синхронный метод парсинга (для обратной совместимости)
    Object parse(InputStream xmlStream);
}