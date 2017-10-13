/**
 * http://www.lbanma.com
 */
package com.wf.core.utils.supcan;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * 硕正FreeForm
 *
 * @author WangZhen
 * @version 2013-11-04
 */
@XStreamAlias("FreeForm")
public class FreeForm extends Common {

    public FreeForm() {
        super();
    }

    public FreeForm(Properties properties) {
        this();
        this.properties = properties;
    }

}
