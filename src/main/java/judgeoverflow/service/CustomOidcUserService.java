package judgeoverflow.service;

import java.util.Collections;
import judgeoverflow.dto.OAuthAttributes;
import judgeoverflow.entity.Committer;
import judgeoverflow.repository.CommitterRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class CustomOidcUserService implements OAuth2UserService<OidcUserRequest, OidcUser> {

    private static final Logger log = LoggerFactory.getLogger(CustomOidcUserService.class);
    private final CommitterRepository committerRepository;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("CustomOidcUserService.loadUser: clientRegistrationId={}",
                userRequest.getClientRegistration().getRegistrationId());

        // 기본 OidcUserService에 위임해서 사용자 정보(ID 토큰 + UserInfo) 가져오기
        OidcUserService delegate = new OidcUserService();
        OidcUser oidcUser = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName =
                userRequest.getClientRegistration().getProviderDetails()
                        .getUserInfoEndpoint()
                        .getUserNameAttributeName();

        OAuthAttributes attributes = OAuthAttributes.of(
                registrationId,
                userNameAttributeName,
                oidcUser.getAttributes()
        );

        Committer user = saveOrUpdate(attributes);

        String authority = user.getRole().name();
        if (!authority.startsWith("ROLE_")) {
            authority = "ROLE_" + authority;
        }

        return new DefaultOidcUser(
                Collections.singleton(new SimpleGrantedAuthority(authority)),
                oidcUser.getIdToken(),
                oidcUser.getUserInfo(),
                attributes.getNameAttributeKey()
        );
    }

    private Committer saveOrUpdate(OAuthAttributes attributes) {
        log.info("CustomOidcUserService.saveOrUpdate: email={}", attributes.getEmail());

        String email = attributes.getEmail();
        if (!StringUtils.hasText(email)) {
            throw new OAuth2AuthenticationException("OAuth 제공자에서 이메일을 반환하지 않았습니다. OAuth 앱 설정에서 user:email scope를 요청해야 합니다.");
        }

        Committer committer = committerRepository.findByEmail(email)
                .map(entity -> entity.update(attributes.getName(), attributes.getPicture(), attributes.getEmail()))
                .orElse(attributes.toEntity());

        Committer saved = committerRepository.save(committer);
        log.info("CustomOidcUserService.saveOrUpdate: committer saved id={}", saved.getId());
        return saved;
    }
}
