package shop.mtcoding.blog.board;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import shop.mtcoding.blog._core.config.security.MyLoginUser;
import shop.mtcoding.blog.user.User;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class BoardController {

    private final HttpSession session;
    private final BoardRepostiory boardRepostiory;

    // title=제목1&content=내용1

    @PostMapping("/board/{id}/update")
    public String update(@PathVariable int id, BoardRequest.UpdateDTO requestDTO, @AuthenticationPrincipal MyLoginUser myLoginUser) {

        // 2. 권한 체크(부가 로직)
        Board board = boardRepostiory.findById(id);

        if (board.getUserId() != myLoginUser.getUser().getId()) {
            return "error/403";
        }
        // 3. 핵심 로직 - 모델 위임
        // update board_tb set title = ? , content = ? where id = ?; 데이터 3건을 넘겨야 함 DTO, id
        boardRepostiory.update(requestDTO, id);

        return "redirect:/board/" + id;

    }

    // updateForm의 책임
    @GetMapping("/board/{id}/updateForm")
    public String updateForm(@PathVariable int id, HttpServletRequest request, @AuthenticationPrincipal MyLoginUser myLoginUser) {

        // 모델 위임 (id로 board를 조회 핵심로직)
        // 권한 체크 (로그인한 id와 게시글을 쓴 user를 비교)
        Board board = boardRepostiory.findById(id);

        if (board.getUserId() != myLoginUser.getUser().getId()) {
            return "error/403";
        }

        // 3. 가방에 담기(핵심로직)
        request.setAttribute("board", board);

        return "board/updateForm";
    }

    @PostMapping("/board/{id}/delete")
    public String delete(@PathVariable int id, HttpServletRequest request , @AuthenticationPrincipal MyLoginUser myLoginUser) {

        // 2. 권한 없으면 나가세요
        Board board = boardRepostiory.findById(id);
        if (board.getUserId() != myLoginUser.getUser().getId()) {
            request.setAttribute("status", 403);
            request.setAttribute("msg", "게시글 삭제 권한이 없습니다");
            return "error/40x";
        }

        boardRepostiory.deleteById(id);

        return "redirect:/";

    }// 바디 데이터가 없기 때문에 유효성 검사 x 인증 체크는 해줘야한다.

    @PostMapping("/board/save")
    public String save(BoardRequest.SaveDTO requestDTO, HttpServletRequest request, @AuthenticationPrincipal MyLoginUser myLoginUser) { //DTO 만들어야 한다


        // 2. 바디 데이터 확인 및 유효성 검사
        System.out.println(requestDTO);

        if (requestDTO.getTitle().length() > 30) {
            request.setAttribute("status", 400);
            request.setAttribute("msg", "타이틀의 길이가 30자를 초과해서는 아니되오!");
            return "error/40x"; // BadRequest
        }

        // 3. 모델 위임
        // insert into board_tb(title, content, user_id, created_at) values(?,?,?,now()); title과 content를 request userId는 제이세션아이디의 user 객체에서 가지고 오면된
        boardRepostiory.save(requestDTO, myLoginUser.getUser().getId());

        return "redirect:/";
    }

    @GetMapping("/")
    public String index(HttpServletRequest request, @AuthenticationPrincipal MyLoginUser myLoginUser) {
        System.out.println("로그인 되었나? : " +myLoginUser.getUsername() );
        List<Board> boardList = boardRepostiory.findAll();
        request.setAttribute("boardList", boardList);

        return "index";
    }

    // /board/saveForm 요청(get)이 온다.
    @GetMapping("/board/saveForm")
    public String saveForm() {
            return "board/saveForm";

    }

    // URL에 테이블 명 뒤에 들어오는 값은 PK or UK
    // 나머지는 다 queryString
    @GetMapping("/board/{id}")
    public String detail(@PathVariable int id, HttpServletRequest request, @AuthenticationPrincipal MyLoginUser myLoginUser) {
        // 1. 모델 진입 - 상세보기 데이터 가져오기
        BoardResponse.DetailDTO responseDTO = boardRepostiory.findByIdWithUser(id);

        // 2. 페이지 주인 여부 체크 (board의 userId와 sessionUser의 id를 비교)

//        User sessionUser = (User) session.getAttribute("sessionUser"); // 열려라 참깨
//        int boardUserId = responseDTO.getUserId(); // board의 user id
//        // int boardUserId = 게시글작성자번호
//
//        boolean pageOwner = false; // 게시글작성자번호 == 로그인한사람의 번호;
//
//        if (sessionUser != null) {
//            if (boardUserId == sessionUser.getId()) {
//                pageOwner = true;
//            }
//        }

//        방법 2
        boolean pageOwner;
        if(myLoginUser == null){
            pageOwner = false;
        }else{
            int 게시글작성자번호 = responseDTO.getUserId();
            int 로그인한사람의번호 = myLoginUser.getUser().getId();
            pageOwner = 게시글작성자번호 == 로그인한사람의번호;
        }


        request.setAttribute("board", responseDTO);
        request.setAttribute("pageOwner", pageOwner);
        return "board/detail";
    }
}
