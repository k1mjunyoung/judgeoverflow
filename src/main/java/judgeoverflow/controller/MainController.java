package judgeoverflow.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String home(@AuthenticationPrincipal OAuth2User principal, Model model) {
        // principal이 null인 경우 (미인증 사용자) 빈 맵을 반환합니다.
        if (principal == null) {
            return "index";
        }
        // principal에서 사용자 속성을 가져올 수 있습니다.
//        return principal.getAttributes();

        // 필요한 정보만 모델에 추가
        model.addAttribute("name", principal.getAttribute("name"));
        model.addAttribute("email", principal.getAttribute("email"));
        return "index";
    }
}
