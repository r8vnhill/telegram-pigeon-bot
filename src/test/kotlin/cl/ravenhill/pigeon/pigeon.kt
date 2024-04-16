package cl.ravenhill.pigeon

import com.github.kotlintelegrambot.bot
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.stringPattern

fun arbBot() = arbitrary {
    val token = Arb.stringPattern("[a-zA-Z0-9]{45}").bind()
    bot {
        this.token = token
    }
}
