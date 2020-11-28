package nl.juraji.biliomi.security

import org.springframework.security.core.GrantedAuthority
import javax.persistence.AttributeConverter

class GrantedAuthorityConverter : AttributeConverter<GrantedAuthority, String> {
    override fun convertToDatabaseColumn(attribute: GrantedAuthority?): String? = attribute?.authority
    override fun convertToEntityAttribute(dbData: String?): GrantedAuthority? = dbData?.let { GrantedAuthority { it } }
}
