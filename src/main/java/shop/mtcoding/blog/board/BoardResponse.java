package shop.mtcoding.blog.board;

import jakarta.servlet.http.HttpSession;
import lombok.Data;
import org.springframework.web.bind.annotation.PathVariable;
import shop.mtcoding.blog.user.User;

public class BoardResponse {

    @Data
    public static class BoardDTO{
        private Integer id;
        private String title;
        private Boolean first;
        private Boolean last;
        private Integer prev;
        private Integer next;

        public BoardDTO(Integer id, String title, Boolean first, Boolean last, Integer prev, Integer next) {
            this.id = id;
            this.title = title;
            this.first = first;
            this.last = last;
            this.prev = prev;
            this.next = next;
        }
    }

    @Data
    public static class DetailDTO{
        // FLAT한 데이터
        private int id;
        private String title;
        private String content;
        private int userId; // 게시글 작성자 ID
        private String username;
        private Boolean boardOwner;

        public void isBoardOwner(User sessionUser){ // int나 double은 값이 안들어가면 null이 나온다
            if (sessionUser == null) boardOwner = false;
            else boardOwner = sessionUser.getId() == userId;
        }

    }
    // 댓글 컬렉션을 들고 가야한다
    @Data
    public static class ReplyDTO{
        private Integer id; // PK는 무조건 들고 가야함
        private Integer userId; // 댓글 쓴 사람 = SESSION ID랑 비교
        private String comment;
        private String username;
        private Boolean replyOwner; // 게시글 주인 여부 (세션값과 비교)

        public ReplyDTO(Object[] ob, User sessionUser) {
            this.id = (Integer) ob[0];
            this.userId = (Integer) ob[1];;
            this.comment = (String) ob[2];;
            this.username = (String) ob[3];;

            if (sessionUser == null){
                replyOwner = false;
            }else {
                replyOwner = sessionUser.getId() == userId;
            }

        }
    }

}
