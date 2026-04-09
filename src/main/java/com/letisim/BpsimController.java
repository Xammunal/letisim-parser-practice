package com.letisim;

import org.bpsim.model.BPSimData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/bpsim")
public class BpsimController {

    private static final Logger log = LoggerFactory.getLogger(BpsimController.class);
    private final BpsimParser parserService;

    public BpsimController(BpsimParser parserService) {
        this.parserService = parserService;
    }

    @PostMapping("/upload")
    public CompletableFuture<ResponseEntity<Map<String, Object>>> uploadBpsimFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return CompletableFuture.completedFuture(
                    ResponseEntity.badRequest().body(Map.of("error", "Файл не прикреплен или пуст"))
            );
        }

        log.info("Получен файл для парсинга: {}, размер: {} байт", file.getOriginalFilename(), file.getSize());

        try {
            // Открываем поток (должен быть закрыт либо внутри сервиса, либо после чтения)
            InputStream is = file.getInputStream();
            
            // Вызываем асинхронный сервис
            return parserService.parseBpsim(is).handle((data, ex) -> {
                if (ex != null) {
                    log.error("Ошибка при асинхронном парсинге", ex);
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(Map.of("error", "Ошибка парсинга: " + ex.getMessage()));
                }
                
                // Временный ответ, пока нет маппера
                int scenarioCount = data.getScenario() != null ? data.getScenario().size() : 0;
                log.info("Успешно распарсен XML. Найдено сценариев: {}", scenarioCount);
                
                return ResponseEntity.ok(Map.of(
                        "status", "success",
                        "message", "Файл успешно распарсен",
                        "scenarios_found", scenarioCount
                ));
            });
            
        } catch (Exception e) {
            log.error("Ошибка при чтении файла", e);
            return CompletableFuture.completedFuture(
                    ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(Map.of("error", "Ошибка при обработке файла: " + e.getMessage()))
            );
        }
    }
}