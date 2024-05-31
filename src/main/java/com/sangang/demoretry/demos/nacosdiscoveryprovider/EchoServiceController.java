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
package com.sangang.demoretry.demos.nacosdiscoveryprovider;

import com.sangang.demoretry.common.CommonException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
public class EchoServiceController {

    /**
     * @Description: 1正常、2熔断、3重试、5超时
     *
     * @author: sangang
     * @date: 2024-05-30
     */
    //@RateLimiter(name = "productRateLimiter", fallbackMethod = "getRateLimiterFallback")
    @GetMapping("/echo/{message}")
    public String echo(@PathVariable Integer message) throws InterruptedException {
        int second = ThreadLocalRandom.current().nextInt(2, 4);
        log.info("[服务端]-[echo]:message-{}",message);
        if(2 == message){
            log.info("[服务端]-[熔断] {} ", message);
            throw new CommonException("message not = 2");
        }
        if(3 == message && second == message){
            log.info("[服务端]-[重试] {} ", message);
            throw new CommonException("异常重试 message："+second);
        }
        if(5 == message){
            log.info("[服务端]-[超时] {} 秒", message);
            TimeUnit.SECONDS.sleep(message);
        }
        return "[ECHO] : " + message;
    }

    private String getRateLimiterFallback(Integer message, Throwable throwable) {
        log.info("[服务端]-[进入RateLimiter回调方法 ]===");
        return "[限流]当前用户较多，请稍后再试。";
    }

}
