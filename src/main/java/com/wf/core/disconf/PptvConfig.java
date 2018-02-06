package com.wf.core.disconf;

import com.baidu.disconf.client.common.annotations.DisconfFile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * @author fxy
 */
@Service
@Scope("singleton")
@DisconfFile(filename = "pptv.properties",app="wf_common")
public class PptvConfig {
}
