package shop.mtcoding.blog.love;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Table(name = "love_tb")
@Data
@Entity // 테이블 생성 위해 필요한 어노테이션
public class Love {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer boardId;
    private Integer userId;
    private Timestamp createdAt;
}
