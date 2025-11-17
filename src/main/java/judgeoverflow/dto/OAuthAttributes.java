package judgeoverflow.dto;

import java.util.Map;
import judgeoverflow.entity.Committer;
import judgeoverflow.entity.CommitterRole;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OAuthAttributes {
    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String name;
    private String email;
    private String picture;

    @Builder
    public OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey, String name, String email, String picture) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.name = name;
        this.email = email;
        this.picture = picture;
    }

    public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        if ("github".equals(registrationId)) {
            return ofGithub(userNameAttributeName, attributes);
        }
        return ofGoogle(userNameAttributeName, attributes);
    }

    private static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        String email = (String) attributes.get("email");
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("GitHub에서 이메일을 제공하지 않았습니다. OAuth 앱 설정에서 user:email scope를 요청해야 합니다.");
        }
        return OAuthAttributes.builder()
                .name((String) attributes.get("name"))
                .email(email)
                .picture((String) attributes.get("picture"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    private static OAuthAttributes ofGithub(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .picture((String) attributes.get("avatar_url"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }


    public Committer toEntity() {
        if (email == null || email.isBlank()) {
            throw new IllegalStateException("이메일은 필수 항목입니다. OAuth 제공자로부터 이메일을 받지 못했습니다.");
        }
        return Committer.builder()
                .name(name)
                .email(email)
                .profileImage(picture)
                .role(CommitterRole.USER) // 기본 권한을 USER로 설정
                .build();
    }
}
