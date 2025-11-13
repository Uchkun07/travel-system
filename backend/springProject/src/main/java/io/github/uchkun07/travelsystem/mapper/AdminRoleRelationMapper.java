package io.github.uchkun07.travelsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.uchkun07.travelsystem.entity.AdminRoleRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 管理员-角色关联Mapper接口
 */
@Mapper
public interface AdminRoleRelationMapper extends BaseMapper<AdminRoleRelation> {
    
    /**
     * 查询管理员的所有角色ID
     * @param adminId 管理员ID
     * @return 角色ID列表
     */
    @Select("SELECT role_id FROM admin_role_relation WHERE admin_id = #{adminId}")
    List<Integer> selectRoleIdsByAdminId(Long adminId);
}
