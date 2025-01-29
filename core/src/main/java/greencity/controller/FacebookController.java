package greencity.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/facebookSecurity")
public class FacebookController {

    @GetMapping("/login")
    public String loginFaceBookPage() {
        return "core/login";
    }
}
