package hyezuu.test.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "vocabularies")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Vocabulary {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String word;

	@Column(nullable = false)
	private String meaning;

	@Column(nullable = false)
	private String exampleSentence;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Grade grade;

	@Builder
	private Vocabulary(String word, String meaning, Grade grade, String exampleSentence) {
		this.word = word;
		this.meaning = meaning;
		this.exampleSentence = exampleSentence;
		this.grade = grade;
	}

	@Override
	public String toString() {
		return "Vocabulary{" +
			"id=" + id +
			", word='" + word + '\'' +
			", meaning='" + meaning + '\'' +
			", exampleSentence='" + exampleSentence + '\'' +
			", grade=" + grade +
			'}';
	}
}