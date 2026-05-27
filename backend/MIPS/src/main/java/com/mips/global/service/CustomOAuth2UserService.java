package com.mips.global.service;

import com.mips.domain.user.entity.User;
import com.mips.domain.user.repository.UserRepository;
import com.mips.global.component.CustomOAuth2User;
import com.mips.global.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private  final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = (String) attributes.get("email");
        String username = (String) attributes.get("name");

        User user = userRepository.findByEmail(email)
                .map(entity -> entity.update(username))
                .orElse(User.builder()
                        .email(email)
                        .username(username)
                        .role(Role.ROLE_USER)
                        .build());

        userRepository.save(user);

        // Spring Security가 인식할 수 있는 Custom한 OAuth2User 객체로 래핑하여 반환
        return new CustomOAuth2User(user, attributes);

    }
}
