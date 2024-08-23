package hyezuu.test.repository;

import hyezuu.test.domain.Vocabulary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VocabularyRepository extends JpaRepository<Vocabulary, Long> {
	Boolean existsByVocabulary(String vocabulary);
}
