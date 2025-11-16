package judgeoverflow.controller;

import java.util.Collections;
import java.util.Map;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

    @GetMapping("/")
    public Map<String, Object> home(@AuthenticationPrincipal OAuth2User principal) {
        // principal이 null인 경우 (미인증 사용자) 빈 맵을 반환합니다.
        if (principal == null) {
            return Collections.emptyMap();
        }
        // principal에서 사용자 속성을 가져올 수 있습니다.
        return principal.getAttributes();
    }
}
