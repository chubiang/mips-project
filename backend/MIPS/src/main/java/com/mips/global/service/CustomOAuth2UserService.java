package com.mips.global.service;

import com.mips.domain.account.entity.Account;
import com.mips.domain.account.entity.AccountBalance;
import com.mips.domain.account.enums.AccountStatus;
import com.mips.domain.account.enums.AccountType;
import com.mips.domain.account.repository.AccountBalanceRepository;
import com.mips.domain.account.repository.AccountRepository;
import com.mips.domain.comm.enums.Currency;
import com.mips.domain.user.entity.User;
import com.mips.domain.user.repository.UserRepository;
import com.mips.global.component.CustomOAuth2User;
import com.mips.global.enums.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final AccountBalanceRepository accountBalanceRepository;
    private final AccountRepository accountRepository;
    public static final String ACC_PROD_CD = "010";
    public static final String PROJ_NUM = "27";

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        Map<String, Object> attributes = oAuth2User.getAttributes();
        // 구글용!
        String email = (String) attributes.get("email");
        String username = (String) attributes.get("name");
        // 카카오는...저렇게줘서 바꿈
        if (email == null) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            log.info("kakaoAccount {}", kakaoAccount);
            email = (String) kakaoAccount.get("email");

            Map<String, Object> kakaoProfile=  (Map<String, Object>) kakaoAccount.get("profile");
            username = (String) kakaoProfile.get("nickname");
        }

        String finalEmail = email;
        String finalUsername = username;
        log.info("email {} username {}", finalEmail, finalUsername);

        User user = userRepository.findByEmail(finalEmail)
                .map(entity -> entity.update(finalUsername))
                .orElse(User.builder()
                        .email(finalEmail)
                        .username(username)
                        .role(Role.ROLE_USER)
                        .build());

        userRepository.save(user);

        // 1. 고객 계좌 생성
        Optional<Account> acc = accountRepository.findByUserId(user.getId());
        if (acc.isEmpty()) {
            // 계좌번호 시퀀스 조회
            Long accSeq = accountRepository.getNextAccountNumberSequence();
            // 계좌번호 생성
            String accNumber = ACC_PROD_CD +
                    "-" +
                    PROJ_NUM +
                    "-" +
                    accSeq;
            // Account 신규생성
            Account account = Account.builder()
                                    .accountType(AccountType.CASH)
                                    .status(AccountStatus.ACTIVE)
                                    .baseCurrency(Currency.KRW)
                                    .accountNumber(accNumber)
                                    .build()
                                    ;
            accountRepository.save(account);
        }

        // 2. 고객 계좌 - 잔액정보 생성
        boolean isExistAcc = accountBalanceRepository.findByAccountId(user.getId()).isPresent();
        if (!isExistAcc) {
            AccountBalance balance = AccountBalance.builder()
                                .user(user)
                                .availableCash(BigDecimal.ZERO)
                                .lockedCash(BigDecimal.ZERO)
                                .build();
            accountBalanceRepository.save(balance);
        }



        // Spring Security가 인식할 수 있는 Custom한 OAuth2User 객체로 래핑하여 반환
        return new CustomOAuth2User(user, attributes);

    }
}
