package shop.mtcoding.blog.board;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Table(name = "board_tb")
@Data
@Entity // 테이블 생성 위해 필요한 어노테이션
public class Board {
    @Id // PK
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto_increment
    private Integer id;

    @Column(length = 30) // 30자 제한
    private String title;
    private String content;

    private Integer userId; // 테이블 만들어 질 때 user_id
    private LocalDateTime createdAt;
}
