//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.jdd.lkcz.fm.core.event.init;

import com.jdd.lkcz.fm.core.event.TaskEvent;
import com.jdd.lkcz.fm.core.utils.core.SpringContextHolder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.InitializingBean;

public class EventHander implements InitializingBean {
    private static RabbitTemplate rabbitTemplate;

    public EventHander() {
    }

    public static void publishEvent(TaskEvent event) {
        rabbitTemplate.convertAndSend(event.getMqQueue(), event);
    }

    public void afterPropertiesSet() throws Exception {
        rabbitTemplate = (RabbitTemplate) SpringContextHolder.getBean(RabbitTemplate.class);
    }
}
