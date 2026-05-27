package com.mips.global.component;

import com.mips.domain.user.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;


public class CustomOAuth2User implements OAuth2User {

    private final User user; // 우리 DB의 엔티티
    private final Map<String, Object> attributes;

    // 생성자
    public CustomOAuth2User(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name()));
    }

    @Override
    public String getName() {
        return user.getEmail();
    }

    public String getUsername() {
        return user.getUsername();
    }

    public String getRole() {
        return user.getRole().name();
    }
}
