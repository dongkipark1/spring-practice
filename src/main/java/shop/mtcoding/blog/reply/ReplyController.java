package shop.mtcoding.blog.reply;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import shop.mtcoding.blog.user.User;

//댓글 쓰기, 댓글 삭제, 댓글 목록 보기
@RequiredArgsConstructor
@Controller
public class ReplyController {

    private final HttpSession session;
    private final ReplyRepository replyRepository;

    @PostMapping("/reply/{id}/delete")
    private String delete(@PathVariable int id){
        // 인증 체크
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "redirect:/loginForm";
        }
        //권한 체크

        Reply reply = replyRepository.findById(id);

        // 댓글이 없거나, 댓글 주인이 아니거나, 댓글 주인이거나
        if (reply == null){
            return "error/404";
        }
        if (reply.getUserId() != sessionUser.getId()){
            return "error/403";
        }

        replyRepository.deleteById(id);

        return "redirect:/board/" + reply.getBoardId();
    }

    @PostMapping("/reply/save")
    public String write(ReplyRequest.WriteDTO requestDTO) {
        System.out.println(requestDTO);
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "redirect:/loginForm";
        }
        // 유효성 검사


        // 핵심 코드
        replyRepository.save(requestDTO, sessionUser.getId());

        return "redirect:/board/" +requestDTO.getBoardId();
    }
}
