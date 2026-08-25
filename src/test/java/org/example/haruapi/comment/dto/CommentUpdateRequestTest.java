package org.example.haruapi.comment.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CommentUpdateRequestTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        validatorFactory.close();
    }

    @Test
    void 댓글_내용이_100자_이내이면_정상() {
        // given
        String content = "a".repeat(100);
        CommentUpdateRequest request =
                new CommentUpdateRequest(content);

        // when
        Set<ConstraintViolation<CommentUpdateRequest>> violations =
                validator.validate(request);

        // then
        assertThat(violations).isEmpty();
    }

    @Test
    void 댓글_내용이_100자를_초과하면_검증_실패() {
        // given
        String content = "a".repeat(101);
        CommentUpdateRequest request =
                new CommentUpdateRequest(content);

        // when
        Set<jakarta.validation.ConstraintViolation<CommentUpdateRequest>> violations =
                validator.validate(request);

        // then
        assertThat(violations).hasSize(1);

        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("내용은 100자 이내로 입력해주세요.");
    }
}