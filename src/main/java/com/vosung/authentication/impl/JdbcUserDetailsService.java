package com.vosung.authentication.impl;

import com.vosung.authentication.authrizationmanage.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.oauth2.common.exceptions.InvalidClientException;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * @description:
 **/
@Repository
public class JdbcUserDetailsService {

    private String selectUserDetailsSql;

    private String selectUserDetailsRoleSql;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 暂无用户组概念，或者其他与角色用户三者联系的表，selectUserDetailsRoleSql语句修改部分
     * union all select role_id from sys_user_group_role_pk where group_id in (select group_id from sys_user_group_pk where user_id = ?)
     */
    public JdbcUserDetailsService() {
        this.selectUserDetailsSql = "select id, username, `password`, `data_state`, `name` from T_AU_USER where username = ?";
        this.selectUserDetailsRoleSql = "select role_id from T_AU_USER_ORG_ROLE where user_id = ?";
    }

    public CustomUserDetails loadUserDetailsByUserName(String username) throws InvalidClientException {
        try {
            CustomUserDetails details = this.jdbcTemplate.queryForObject(this.selectUserDetailsSql, new JdbcUserDetailsService.UserDetailsRowMapper(), new Object[]{username});
            return details;
        } catch (EmptyResultDataAccessException var4) {
            throw new NoSuchClientException("No client with requested username: " + username);
        }
    }

    /**
     * 组织用户角色，会有重复角色id，去重处理
     * @param userId
     * @return
     */
    public List<Integer> loadUserRolesByUserId(Long userId) {
        List<Integer> roles = this.jdbcTemplate.query(this.selectUserDetailsRoleSql, new JdbcUserDetailsService.RoleRowMapper(), new Object[]{userId});
        List<Integer> roleIds = new ArrayList<>();
        for (Integer roleId: roles) {
            if(!roleIds.contains(roleId)){
                roleIds.add(roleId);
            }
        }
        return roleIds;
    }

    private static class UserDetailsRowMapper implements RowMapper<CustomUserDetails> {

        @Override
        public CustomUserDetails mapRow(ResultSet resultSet, int i) throws SQLException {
            CustomUserDetails customUserDetails = new CustomUserDetails();
            customUserDetails.setUserId(resultSet.getString(1));
            customUserDetails.setUsername(resultSet.getString(2));
            customUserDetails.setPassword(resultSet.getString(3));
            String data_state = resultSet.getString(4);
            //todo 根据用户状态设置用户是否可用
            if(!"AUDIT".equals(data_state)){
                customUserDetails.setEnabled(false);
            }
            customUserDetails.setAuthorities(new HashSet<>());
            return customUserDetails;
        }

    }

    private static class RoleRowMapper implements RowMapper<Integer> {
        @Override
        public Integer mapRow(ResultSet resultSet, int i) throws SQLException {
            return resultSet.getInt(1);
        }
    }

}
