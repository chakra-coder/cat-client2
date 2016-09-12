package com.patterncat.apm.controller;

import com.patterncat.apm.message.Transaction;
import com.patterncat.apm.Cat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

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

    @RequestMapping("/t2")
    public Object transaction(){
        Transaction t = Cat.newTransaction("order", "order");
        int nextInt = new Random().nextInt(3);
        if (nextInt % 2 == 0) {
            t.setStatus(Transaction.SUCCESS);
        } else {
            t.setStatus(String.valueOf(nextInt));
        }
        t.complete();
        return "ok";
    }
}
