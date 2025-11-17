package judgeoverflow.service;

import java.util.Collections;
import judgeoverflow.dto.OAuthAttributes;
import judgeoverflow.entity.Committer;
import judgeoverflow.repository.CommitterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final CommitterRepository committerRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        Committer user = saveOrUpdate(attributes);

        // 세션에 사용자 정보를 저장하는 로직은 여기에 추가할 수 있습니다.
        // httpSession.setAttribute("user", new SessionUser(user));

        String authority = user.getRole().name();
        if (!authority.startsWith("ROLE_")) {
            authority = "ROLE_" + authority;
        }
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(authority)),
                attributes.getAttributes(),
                attributes.getNameAttributeKey());
    }

    private Committer saveOrUpdate(OAuthAttributes attributes) {
        String email = attributes.getEmail();
        if (!StringUtils.hasText(email)) {
            throw new OAuth2AuthenticationException("GitHub에서 이메일을 반환하지 않았습니다. user:email scope를 요청하고 /user/emails에서 primary 이메일을 조회하는 보강이 필요합니다.");
        }
        Committer committer = committerRepository.findByEmail(email)
                .map(entity -> entity.update(attributes.getName(), attributes.getPicture(), attributes.getEmail()))
                .orElse(attributes.toEntity());

        return committerRepository.save(committer);
    }
}
