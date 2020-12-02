package nl.juraji.biliomi

import nl.juraji.biliomi.utils.DatabaseTest
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class BiliomiApplicationTests : DatabaseTest() {

    @Test
    fun contextLoads() {
    }

}
