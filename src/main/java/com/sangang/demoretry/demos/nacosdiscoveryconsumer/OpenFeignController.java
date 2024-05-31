/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 package com.sangang.demoretry.demos.nacosdiscoveryconsumer;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Slf4j
@RestController
public class OpenFeignController {

    @Autowired
    private EchoService echoService;

    //@CircuitBreaker(name = "ratingCircuitBreakService", fallbackMethod = "getCircuitBreaker")
    //@TimeLimiter(name = "ratingTimeoutService", fallbackMethod = "getDefaultTimeout")
    //@Bulkhead(name = "ratingBulkheadService", type = Bulkhead.Type.SEMAPHORE, fallbackMethod = "getBulkhead")
    @Retry(name = "ratingCircuitBreakService", fallbackMethod = "getRetry")
    @GetMapping(value = "/feign/echo/{message}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletionStage<String> feignEcho(@PathVariable String message) {

        return CompletableFuture.supplyAsync(() -> echoService.echo(message));
        //return echoService.echo(message);
    }

    private static int retryCount; // 记录重试次数，进行验证
    private CompletionStage<String> getRetry(String message,Throwable exception) {
        retryCount = 0;
        log.info("[客户端]-[进入Retry回调方法 {} ]===", retryCount);
        return CompletableFuture.supplyAsync(() -> "Retry Default");
        //return "Retry Default";
    }

    private CompletionStage<String> getBulkhead(String message, Throwable throwable) {
        log.info("[客户端]-[进入Bulkhead回调方法 ]===");
        return CompletableFuture.supplyAsync(() -> "Bulkhead Default");
        //return "Bulkhead Default";
    }

    private CompletionStage<String> getDefaultTimeout(String message, Throwable throwable){
        log.info("[客户端]-[进入Timeout回调方法 ]===");
        return CompletableFuture.supplyAsync(() -> "Timeout Default");
        //return "Timeout Default";
    }

    private CompletionStage<String> getCircuitBreaker(String message, Throwable throwable){
        log.info("[客户端]-[进入CircuitBreaker回调方法 ]===");
        return CompletableFuture.supplyAsync(() -> "CircuitBreaker Default");
        //return "CircuitBreaker Default";
    }


}
