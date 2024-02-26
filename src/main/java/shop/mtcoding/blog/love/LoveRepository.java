package shop.mtcoding.blog.love;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import shop.mtcoding.blog.board.BoardRequest;

@RequiredArgsConstructor
@Repository
public class LoveRepository {
    public final EntityManager em;

    public LoveResponse.DetailDTO findLove(int boardId, int sessionUserId){
        String q = """
                SELECT
                    id,
                    CASE
                        WHEN user_id IS NULL THEN FALSE
                        ELSE TRUE
                    END AS isLove,
                    (SELECT COUNT(*) FROM love_tb WHERE board_id = ?) AS loveCount
                FROM
                    love_tb
                WHERE
                    board_id = ? AND user_id = ?;
                """;
        Query query = em.createNativeQuery(q);
        query.setParameter(1, boardId);
        query.setParameter(2, boardId);
        query.setParameter(3, sessionUserId);

        Object[] row = (Object[]) query.getSingleResult();
        Integer id = (Integer) row[0];
        Boolean isLove = (Boolean) row[1];
        Long loveCount = (Long) row[2];

        System.out.println("id : "+id);
        System.out.println("isLove : "+isLove);
        System.out.println("loveCount : "+loveCount);

        LoveResponse.DetailDTO responseDTO = new LoveResponse.DetailDTO(
                id, isLove, loveCount
        );
        return responseDTO;
    }

}