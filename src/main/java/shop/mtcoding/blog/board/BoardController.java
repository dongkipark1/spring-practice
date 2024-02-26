package shop.mtcoding.blog.board;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import shop.mtcoding.blog.love.LoveRepository;
import shop.mtcoding.blog.love.LoveResponse;
import shop.mtcoding.blog.reply.ReplyRepostiory;
import shop.mtcoding.blog.user.User;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class BoardController {

    private final HttpSession session;
    private final BoardRepostiory boardRepostiory;
    private final ReplyRepostiory replyRepostiory;
    private final LoveRepository loveRepository;

    // title=제목1&content=내용1

    @PostMapping("/board/{id}/update")
    public String update(@PathVariable int id, BoardRequest.UpdateDTO requestDTO) {
        // @RequestBody를 쓰면 type이 string이면 텍스트로 받고 object이면 JSON으로 파싱을 해준다

        // 1. 인증 체크(부가 로직)
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "redirect:/loginForm";
        }
        // 조금이라도 다르면 패턴화 시켜야 함

        // 2. 권한 체크(부가 로직)
        Board board = boardRepostiory.findById(id);

        if (board.getUserId() != sessionUser.getId()) {
            return "error/403";
        }
        // 3. 핵심 로직 - 모델 위임
        // update board_tb set title = ? , content = ? where id = ?; 데이터 3건을 넘겨야 함 DTO, id
        boardRepostiory.update(requestDTO, id);

        return "redirect:/board/" + id;

    }

    // updateForm의 책임
    @GetMapping("/board/{id}/updateForm")
    public String updateForm(@PathVariable int id, HttpServletRequest request) {

        // 인증 체크 (로그인 안하면 못들어와!)
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "redirect:/loginForm";
        }

        // 모델 위임 (id로 board를 조회 핵심로직)
        // 권한 체크 (로그인한 id와 게시글을 쓴 user를 비교)
        Board board = boardRepostiory.findById(id);

        if (board.getUserId() != sessionUser.getId()) {
            return "error/403";
        }

        // 3. 가방에 담기(핵심로직)
        request.setAttribute("board", board);

        return "board/updateForm";
    }

    @PostMapping("/board/{id}/delete")
    public String delete(@PathVariable int id, HttpServletRequest request) {
        // 1. 인증 안되면 나가세요

        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) { // error 401
            return "redirect:/loginForm";
        }

        // 2. 권한 없으면 나가세요
        Board board = boardRepostiory.findById(id);
        if (board.getUserId() != sessionUser.getId()) {
            request.setAttribute("status", 403);
            request.setAttribute("msg", "게시글 삭제 권한이 없습니다");
            return "error/40x";
        }

        boardRepostiory.deleteById(id);

        return "redirect:/";

    }// 바디 데이터가 없기 때문에 유효성 검사 x 인증 체크는 해줘야한다.

    @PostMapping("/board/save")
    public String save(BoardRequest.SaveDTO requestDTO, HttpServletRequest request) { //DTO 만들어야 한다
        // 1. 인증 체크 인증 안되면 리다이렉션으로 로그인페이지로 보내야
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "redirect:/loginForm";
        }

        // 2. 바디 데이터 확인 및 유효성 검사
        System.out.println(requestDTO);

        if (requestDTO.getTitle().length() > 30) {
            request.setAttribute("status", 400);
            request.setAttribute("msg", "타이틀의 길이가 30자를 초과해서는 아니되오!");
            return "error/40x"; // BadRequest
        }

        // 3. 모델 위임
        // insert into board_tb(title, content, user_id, created_at) values(?,?,?,now()); title과 content를 request userId는 제이세션아이디의 user 객체에서 가지고 오면된
        boardRepostiory.save(requestDTO, sessionUser.getId());

        return "redirect:/";
    }

    //localhost:8080?page=1 -> page 값이 1
    //localhost:8080 -> page 값이 0
    @GetMapping("/")
    public String index(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "") String keyword) {

        // isEmpty -> null, 공백
        // isBlank -> null, 공백, 화이트 스페이스

        if(keyword.isBlank()){
            List<Board> boardList = boardRepostiory.findAll(page);
            // 전체 페이지 개수
            int count = boardRepostiory.count().intValue();
            // 5 -> 2page
            // 6 -> 2page
            // 7 -> 3page
            // 8 -> 3page
            int namerge = count % 3 == 0 ? 0 : 1;
            int allPageCount = count / 3 + namerge;

            request.setAttribute("boardList", boardList);
            request.setAttribute("first", page == 0);
            request.setAttribute("last", allPageCount == page + 1);
            request.setAttribute("prev", page - 1);
            request.setAttribute("next", page + 1);
            request.setAttribute("keyword", "");
        }else{
            List<Board> boardList = boardRepostiory.findAll(page, keyword);
            // 전체 페이지 개수
            int count = boardRepostiory.count(keyword).intValue();
            // 5 -> 2page
            // 6 -> 2page
            // 7 -> 3page
            // 8 -> 3page
            int namerge = count % 3 == 0 ? 0 : 1;
            int allPageCount = count / 3 + namerge;

            request.setAttribute("boardList", boardList);
            request.setAttribute("first", page == 0);
            request.setAttribute("last", allPageCount == page + 1);
            request.setAttribute("prev", page - 1);
            request.setAttribute("next", page + 1);
            request.setAttribute("keyword", keyword);
        }

        return "index";
    }

    // /board/saveForm 요청(get)이 온다.
    @GetMapping("/board/saveForm")
    public String saveForm() {
        //session 영역에 sessionUser 키값에 user 객체 있는지 체크
        User sessionUser = (User) session.getAttribute("sessionUser");

        // 값이 null 이면 login 페이지로 리다이렉션
        // 값이 null 이 아니면 /board/saveForm으로 이동

        if (sessionUser == null) {
            return "redirect:/loginForm";
        } else {
            return "board/saveForm";
        }
    }

    // URL에 테이블 명 뒤에 들어오는 값은 PK or UK
    // 나머지는 다 queryString
    @GetMapping("/board/{id}")
    public String detail(@PathVariable int id, HttpServletRequest request) {
        User sessionUser = (User) session.getAttribute("sessionUser");
        BoardResponse.DetailDTO boardDTO = boardRepostiory.findByIdWithUser(id);
        boardDTO.isBoardOwner(sessionUser);

        List<BoardResponse.ReplyDTO> replyDTOList = replyRepostiory.findByBoardId(id, sessionUser);
        request.setAttribute("board", boardDTO);
        request.setAttribute("replyList", replyDTOList);

        LoveResponse.DetailDTO loveDetailDTO = loveRepository.findLove(id, sessionUser.getId());
        request.setAttribute("love", loveDetailDTO);
        // fas fa-heart text-danger
        // far fa-heart
        // request.setAttribute("css", "far fa-heart");

        return "board/detail";
    }
}
