/**
 * http://www.lbanma.com
 */
package com.jdd.lkcz.fm.core.utils.excel;

import com.google.common.collect.Lists;
import com.jdd.lkcz.framework.core.SpringContextHolder;
import com.jdd.lkcz.game.common.utils.type.Collections3;
import com.jdd.lkcz.game.common.utils.type.StringUtils;
import com.jdd.lkcz.game.modules.sys.entity.SysRole;
import com.jdd.lkcz.game.modules.sys.service.SystemService;

import java.util.List;

/**
 * 字段类型转换
 *
 * @author www.lbanma.com
 * @version 2013-5-29
 */
public class RoleListType {

    private static SystemService systemService = SpringContextHolder.getBean(SystemService.class);

    /**
     * 获取对象值（导入）
     */
    public static Object getValue(String val) {
        List<SysRole> roleList = Lists.newArrayList();
        List<SysRole> allRoleList = systemService.findAllRole();
        for (String s : StringUtils.split(val, ",")) {
            for (SysRole e : allRoleList) {
                if (StringUtils.trimToEmpty(s).equals(e.getName())) {
                    roleList.add(e);
                }
            }
        }
        return roleList.size() > 0 ? roleList : null;
    }

    /**
     * 设置对象值（导出）
     */
    public static String setValue(Object val) {
        if (val != null) {
            @SuppressWarnings("unchecked")
            List<SysRole> roleList = (List<SysRole>) val;
            return Collections3.extractToString(roleList, "name", ", ");
        }
        return "";
    }

}
