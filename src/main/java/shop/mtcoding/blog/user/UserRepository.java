package shop.mtcoding.blog.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository // IoC에 new하는 방법
public class UserRepository {

    // DB에 접근할 수 있는 매니저 객체
    // 스프링이 만들어서 IoC에 넣어둔다.
    // DI에서 꺼내 쓰기만 하면된다

    private EntityManager em;

    public UserRepository(EntityManager em) {
        this.em = em;
    }

    @Transactional // DB의 write할 때는 필수이다 왜? 다른 사람이 나와 같은 DB에 동시에 접근 할 때 불가피하게 데이터가 바뀌는 것을 막아준다.
    public void save(UserRequest.joinDTO requestDTO){
         Query query = em.createNativeQuery("insert into user_tb(username, password, email, created_at) values (?,?,?,now())");
         query.setParameter(1, requestDTO.getUsername());
         query.setParameter(2, requestDTO.getPassword());
         query.setParameter(3, requestDTO.getEmail());

         query.executeUpdate();
    }
}
