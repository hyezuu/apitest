package hyezuu.test.repository;

import hyezuu.test.domain.Test;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestRepository extends JpaRepository<Test, Long> {
    Boolean existsByAnswer(String answer);
}
