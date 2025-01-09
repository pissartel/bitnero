package org.company.app

expect fun getPlatform(): Platform

enum class Platform {
    ANDROID,
    IOS,
    JVM,
    JS
}
