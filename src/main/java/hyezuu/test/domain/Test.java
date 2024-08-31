package hyezuu.test.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Table(name = "tests")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Test {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Grade grade;

    @Column(nullable = false)
    private String question;

    @Column(nullable = false)
    private String answer;

    @Override
    public String toString() {
        return "Test{" +
            "id=" + id +
            ", grade=" + grade +
            ", question='" + question + '\'' +
            ", answer='" + answer + '\'' +
            '}';
    }
}

