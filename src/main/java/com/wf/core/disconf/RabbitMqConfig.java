package com.wf.core.disconf;

import com.baidu.disconf.client.common.annotations.DisconfFile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("singleton")
@DisconfFile(filename = "prop-mq-rabbitmq.properties",app="wf_common")
public class RabbitMqConfig {

}
