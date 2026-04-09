package com.letisim;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Конфигурация асинхронного выполнения для Spring.
 *
 * <p>{@code @EnableAsync} активирует поддержку аннотации {@code @Async}.
 * При вызове метода с {@code @Async} Spring выполняет его в отдельном потоке
 * из настроенного пула.</p>
 *
 * <p>Пул настроен специально для задач парсинга BPSim:
 * <ul>
 *   <li>corePoolSize = 2 — базовые потоки</li>
 *   <li>maxPoolSize = 5 — максимум при нагрузке</li>
 *   <li>queueCapacity = 25 — очередь ожидания</li>
 * </ul>
 * </p>
 */
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    private static final Logger log = LoggerFactory.getLogger(AsyncConfig.class);

    /**
     * Пул потоков для асинхронного парсинга.
     */
    @Bean(name = "bpsimParserExecutor")
    public Executor bpsimParserExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(25);
        executor.setThreadNamePrefix("bpsim-parser-");
        executor.initialize();
        return executor;
    }

    @Override
    public Executor getAsyncExecutor() {
        return bpsimParserExecutor();
    }

    /**
     * Обработчик необработанных исключений в @Async-методах,
     * возвращающих void (для CompletableFuture исключения прокидываются
     * через сам future).
     */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (throwable, method, params) ->
                log.error("Необработанное исключение в @Async методе {}: {}",
                        method.getName(), throwable.getMessage(), throwable);
    }
}
