package nl.juraji.biliomi.api


import nl.juraji.biliomi.domain.user.UserId
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component

@Component
class DirectoryIdParameterConverter : Converter<String, UserId> {
    override fun convert(value: String): UserId = UserId(value)
}
