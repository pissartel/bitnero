package org.company.app.platform

expect fun getPlatform(): Platform

enum class Platform {
    ANDROID,
    IOS,
    JVM,
    JS
}
