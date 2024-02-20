package shop.mtcoding.blog.user;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

// 서버를 열면 아직 테이블이 만들어지지 않았다
// 왜 테이블 생성쿼리가 없는 거지? 테이블이 만들어 지지 않았다.
@Table(name = "user_tb") // 테이블을 생성할 때 자바 객체로 만들 수 있다. 이때는 @Entity를 사용한다.
@Data
@Entity
public class User {
    @Id // PK 설정
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO_INCREMENT 전략
    private Integer id;

    @Column(unique = true)
    private String username;
    private String password;
    private String email;
    // 카멜 표기법으로 만들면 DB는 created_at으로 만들어진다. (언더스코어 기법)
    private LocalDateTime createdAt;

}


