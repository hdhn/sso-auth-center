package com.sso.security;

import com.sso.common.model.login.LoginResultVO;
import com.sso.common.model.login.LoginUserVO;
import com.sso.common.utils.bean.BeanCopierUtil;
import com.sso.dao.entity.SsoUser;
import com.sso.dao.mapper.SsoUserMapper;
import com.sso.service.admin.login.SysPermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 自定义LDAP用户详情映射器，用于将LDAP属性映射到Spring Security的UserDetails对象
 */
@Slf4j
@Component
public class CustomLdapUserDetailsMapper implements UserDetailsContextMapper {

    @Resource
    private SsoUserMapper ssoUserMapper;
    
    @Autowired
    private SysPermissionService sysPermissionService;

    @Override
    public UserDetails mapUserFromContext(DirContextOperations ctx, String username, Collection<? extends GrantedAuthority> authorities) {
        log.info("LDAP user {} authenticated successfully, mapping authorities", username);
        
        // 获取本地用户信息
        SsoUser user = ssoUserMapper.getByUserName(username);
        if (user == null) {
            log.error("LDAP user {} not found in local database", username);
            throw new RuntimeException("User not found in local database");
        }

        // 创建登录用户对象
        LoginUserVO loginUserVO = BeanCopierUtil.copy(user, LoginUserVO.class);
        
        // 获取本地权限
        LoginResultVO loginResultVO = new LoginResultVO(loginUserVO, new HashSet<>());
        loginResultVO.setRoleKeyList(sysPermissionService.getRolePermission(loginUserVO));
        
        // 获取本地菜单权限
        loginResultVO.setPermissionList(sysPermissionService.getMenuPermission(loginUserVO).getPermissionList());
        
//        // 合并LDAP权限到本地权限列表
//        Set<String> mergedPermissions = new HashSet<>();
//
//        // 添加LDAP权限
//        authorities.stream()
//            .map(GrantedAuthority::getAuthority)
//            .forEach(mergedPermissions::add);
//
//        // 添加本地角色权限
//        if (loginResultVO.getRoleKeyList() != null) {
//            loginResultVO.getRoleKeyList().forEach(role ->
//                mergedPermissions.add("ROLE_" + role));
//        }
//
//        // 添加本地菜单权限
//        if (loginResultVO.getPermissionList() != null) {
//            mergedPermissions.addAll(loginResultVO.getPermissionList());
//        }
//
//        // 更新权限列表
//        loginResultVO.setPermissionList(mergedPermissions);
        
        log.info("Successfully mapped LDAP user {} with merged permissions", username);
        return loginResultVO;
    }

    @Override
    public void mapUserToContext(UserDetails user, DirContextAdapter ctx) {
        // 不需要实现，因为我们不需要将用户信息写回LDAP
    }
}
