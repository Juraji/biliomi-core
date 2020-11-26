package nl.juraji.biliomi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan("nl.juraji.biliomi.configuration")
class BiliomiApplication

fun main(args: Array<String>) {
    runApplication<BiliomiApplication>(*args)
}
