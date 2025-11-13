package io.github.uchkun07.travelsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.uchkun07.travelsystem.entity.AdminRolePermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 角色-权限关联Mapper接口
 */
@Mapper
public interface AdminRolePermissionMapper extends BaseMapper<AdminRolePermission> {
    
    /**
     * 根据角色ID列表查询权限编码
     * @param roleIds 角色ID列表
     * @return 权限编码列表
     */
    @Select({
        "<script>",
        "SELECT DISTINCT ap.permission_code ",
        "FROM admin_role_permission arp ",
        "INNER JOIN admin_permission ap ON arp.permission_id = ap.permission_id ",
        "WHERE arp.role_id IN ",
        "<foreach item='roleId' collection='roleIds' open='(' separator=',' close=')'>",
        "#{roleId}",
        "</foreach>",
        "</script>"
    })
    List<String> selectPermissionCodesByRoleIds(@Param("roleIds") List<Integer> roleIds);
}
