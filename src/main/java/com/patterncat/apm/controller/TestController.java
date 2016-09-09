package com.patterncat.apm.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by patterncat on 2016-09-09.
 */
@RequestMapping("/test")
@RestController
public class TestController {

    private static final Logger logger = LoggerFactory.getLogger(TestController.class);

    @RequestMapping("/t1")
    public Object test(){
        try{
            int i =1/0;
        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }
        return "finish";
    }
}
