package com.sangang.demoretry.demos.nacosdiscoveryconsumer;

import com.sangang.demoretry.common.CommonException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


/**
 * @Description:
 *
 * @author: sangang
 * @date: 2024-04-10
 */
@Slf4j
@Component
public class EchoServiceHystrix implements EchoService {

    @Override
    public String echo(String message) {
        log.info("EchoServiceHystrix-echo:message-{}",message);
        throw new CommonException("获取echo异常");
        //return "获取echo异常";
    }
}
