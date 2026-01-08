package com.gen.maximizemagic

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform