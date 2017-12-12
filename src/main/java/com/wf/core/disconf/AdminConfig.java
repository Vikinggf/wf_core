package com.wf.core.disconf;

import com.baidu.disconf.client.common.annotations.DisconfFile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * @author zwf
 */
@Service
@Scope("singleton")
@DisconfFile(filename = "admin.properties",app="wf_common")
public class AdminConfig {
}
