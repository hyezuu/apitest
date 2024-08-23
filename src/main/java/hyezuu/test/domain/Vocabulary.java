package hyezuu.test.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class Vocabulary {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String vocabulary;
	private String meaning;
	private String example;

	public Vocabulary(String vocabulary, String meaning, String example) {
		this.vocabulary = vocabulary;
		this.meaning = meaning;
		this.example = example;
	}
}
