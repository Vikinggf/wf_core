/**
 * http://www.lbanma.com
 */
package com.jdd.lkcz.fm.core.utils.excel;

import com.jdd.lkcz.game.common.utils.type.StringUtils;
import com.jdd.lkcz.game.modules.sys.entity.SysArea;
import com.jdd.lkcz.game.modules.sys.utils.UserUtils;

/**
 * 字段类型转换
 *
 * @author www.lbanma.com
 * @version 2013-03-10
 */
public class AreaType {

    /**
     * 获取对象值（导入）
     */
    public static Object getValue(String val) {
        for (SysArea e : UserUtils.getAreaList()) {
            if (StringUtils.trimToEmpty(val).equals(e.getName())) {
                return e;
            }
        }
        return null;
    }

    /**
     * 获取对象值（导出）
     */
    public static String setValue(Object val) {
        if (val != null && ((SysArea) val).getName() != null) {
            return ((SysArea) val).getName();
        }
        return "";
    }
}
