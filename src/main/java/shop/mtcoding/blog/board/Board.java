package shop.mtcoding.blog.board;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Table
@Data
@Entity // 테이블 생성 위해 필요한 어노테이션
public class Board {
    @Id // PK
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto_increment
    private int id;
    private String title;
    private String content;

    private int userId; // 테이블 만들어 질 때 user_id
    private LocalDateTime createdAt;
}
