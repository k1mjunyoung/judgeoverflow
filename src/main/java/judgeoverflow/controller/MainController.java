package judgeoverflow.controller;

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
        // principal에서 사용자 속성을 가져올 수 있습니다.
        return principal.getAttributes();
    }
}
