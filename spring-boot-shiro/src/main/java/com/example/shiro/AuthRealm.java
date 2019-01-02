package com.example.shiro;

import com.example.entity.Permission;
import com.example.entity.Role;
import com.example.entity.User;
import com.example.service.UserService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AuthRealm extends AuthorizingRealm {
    @Autowired
    private UserService userService;
    //授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        //从session获取用户
        User user = (User) principalCollection.fromRealm(this.getClass().getName()).iterator().next();
        List<String> permissonList = new ArrayList<>();
        Set<Role> roles = user.getRoles();
        if(!roles.isEmpty()){
            for(Role role:roles){
                Set<Permission> permissions = role.getPermissions();
                if(!permissions.isEmpty()){
                    for(Permission permission:permissions){
                        permissonList.add(permission.getName());
                    }

                }
            }
        }
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.addStringPermissions(permissonList);
        return info;
    }

    //认证登录
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken)token;
        String username = usernamePasswordToken.getUsername();
        User user = userService.getUserByUsername(username);
        return new SimpleAuthenticationInfo(user,user.getPassword(),this.getClass().getName());
    }
}