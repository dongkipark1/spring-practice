package shop.mtcoding.blog.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import shop.mtcoding.blog._core.util.Script;

@RequiredArgsConstructor // final이 붙은 애들에 대한 생성자
@Controller
public class UserController {

    // 자바의 final 변수는 반드시 초기화가 되어야 한다.
    private final UserRepository userRepository;
    private final HttpSession session;

    // 방법 1
//    @PostMapping("/login")
//    public String login(String username, String password){
//        return null;
//    }

    // 방법 2
//    public String login(HttpServletRequest request){
//        String username = request.getParameter("username");
//        String password = request.getParameter("password");
//        return null;
//    }
    // 왜 조회인다 Get이 아닌 Post인가? 민감한 정보는 body로 보낸다.
    // 로그인만 예외롤 select이지만 post 사용
    // select * from user_tb where username=? and password = ?

    // 방법 3

    @PostMapping("/login")
    public String login(UserRequest.LoginDTO requestDTO, HttpServletRequest request){
        HttpSession s = request.getSession();

        System.out.println(requestDTO); // toString -> @Data

        if (requestDTO.getUsername().length() < 3){
            throw new RuntimeException("유저네임 길이가 너무 짧아요");
        }

        User user = userRepository.findByUsername(requestDTO.getUsername());

        if (!BCrypt.checkpw(requestDTO.getPassword(), user.getPassword())){ // 패스워드 검증을 실패했다면
                throw new RuntimeException("패스워드가 틀렸습니다");
        }
        session.setAttribute("sessionUser", user);
        return "redirect:/";
    }

    @PostMapping("/join")
    public String join(UserRequest.joinDTO requestDTO){ //@ResponseBody 메시지 자체가 리턴된다. ViewResolver 동작 안함
        System.out.println(requestDTO);

        String rawPassword = requestDTO.getPassword();
        String encPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
        requestDTO.setPassword(encPassword);

        //ssar을 조회해보고 있으면, 없으면

        try {
            userRepository.save(requestDTO); // 모델에 위임하기
        } catch (Exception e) {
            throw new RuntimeException("아이디가 중복되었습니다.");
        }
        return "redirect:/loginForm";
    }

    @GetMapping("/joinForm")
    public String joinForm() {
        return "user/joinForm";
    }

    @GetMapping("/loginForm")
    public String loginForm() {
        return "user/loginForm";
    }

    @GetMapping("/user/updateForm")
    public String updateForm() {
        return "user/updateForm";
    }

    @GetMapping("/logout")
    public String logout() {
        session.invalidate(); // 서랍 비우기
        return "redirect:/";
    }
}
