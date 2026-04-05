package com.example.hello.enums;

/**
 * 授权码类型枚举
 */
public enum LicenseType {
    /**
     * 体验授权码
     */
    TRIAL("体验授权码"),
    
    /**
     * 普通授权码
     */
    STANDARD("普通授权码"),
    
    /**
     * 高级授权码
     */
    PREMIUM("高级授权码");

    private final String displayName;

    LicenseType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * 根据显示名称获取枚举值
     */
    public static LicenseType fromDisplayName(String displayName) {
        for (LicenseType type : values()) {
            if (type.displayName.equals(displayName)) {
                return type;
            }
        }
        return STANDARD; // 默认返回普通授权码
    }
}

