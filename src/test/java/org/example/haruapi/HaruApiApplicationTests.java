package org.example.haruapi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "security.jwt.secret-key=VGhpcy1pcy1hLXRlc3Qta2V5LXRoYXQtaXMtYXQtbGVhc3QtMzItYnl0ZXM=",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class HaruApiApplicationTests {

    @Test
    void contextLoads() {
    }

}
