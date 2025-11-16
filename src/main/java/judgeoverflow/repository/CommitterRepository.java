package judgeoverflow.repository;

import java.util.Optional;
import judgeoverflow.entity.Committer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommitterRepository extends JpaRepository<Committer, Long> {
    Optional<Committer> findByEmail(String email);
}
