package hyezuu.test.constant;

public class Constant {

    public static final String DEFAULT_PROMPT =
        "일년전 오늘 날짜의 기사들 중에서 %s 레벨(A1, A2, B1, B2, C1, C2 중 하나)의 영어 단어 15개를 word : , meaning : ,exampleSentence : , question "
            + ": 형태로 작성해줘, 문제는 주어진 단어를 정답으로 사용하고, 3개의 오답도 함께 작성해줘. 오답들은 예문에서 의미상 어울리지 않도록 해줘 .예를 들면 “After winning the "
            + "championship, the team was overwhelmed with a sense of _________. (Melancholy, "
            + "Euphoria, Anxiety, Apathy)” 처럼 해줘 . 줄바꿈은 가능하지만 그외의 마크다운 언어는 지양해줘. 파싱해서 데이터베이스에 각각 "
            + "넣을거야. 기사 제목은 필요없어, 이전에 응답한 단어가 아닌 매번 새로운 단어로 제공해줘. 적합한 단어가 없다면 그 전해의 기사도 좋아.";
}
