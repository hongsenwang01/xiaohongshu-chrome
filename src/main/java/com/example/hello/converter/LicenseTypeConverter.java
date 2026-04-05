package com.example.hello.converter;

import com.example.hello.enums.LicenseType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * 授权码类型转换器
 * 将枚举值与数据库中文值进行转换
 */
@Converter(autoApply = true)
public class LicenseTypeConverter implements AttributeConverter<LicenseType, String> {

    /**
     * 将枚举转换为数据库列值（中文）
     */
    @Override
    public String convertToDatabaseColumn(LicenseType attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getDisplayName();
    }

    /**
     * 将数据库列值（中文）转换为枚举
     */
    @Override
    public LicenseType convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return null;
        }
        return LicenseType.fromDisplayName(dbData);
    }
}

