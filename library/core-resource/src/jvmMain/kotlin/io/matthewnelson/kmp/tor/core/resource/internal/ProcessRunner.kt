/*
 * Copyright (c) 2023 Matthew Nelson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/
package io.matthewnelson.kmp.tor.core.resource.internal

import io.matthewnelson.kmp.tor.core.api.annotation.InternalKmpTorApi
import io.matthewnelson.kmp.tor.core.resource.waitFor
import java.io.ByteArrayOutputStream
import java.io.IOException
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

internal fun interface ProcessRunner {

    @Throws(IOException::class, InterruptedException::class)
    fun runAndWait(commands: List<String>, timeout: Duration): String

    object Default: ProcessRunner {

        @Throws(IOException::class, InterruptedException::class)
        override fun runAndWait(commands: List<String>, timeout: Duration): String {
            val p = Runtime.getRuntime().exec(commands.toTypedArray())
            @OptIn(InternalKmpTorApi::class)
            p.waitFor(timeout)

            return p.inputStream.use { iStream ->
                ByteArrayOutputStream().use { oStream ->
                    val buf = ByteArray(4096)

                    while (true) {
                        val read = iStream.read(buf)
                        if (read == -1) break
                        oStream.write(buf, 0, read)
                    }

                    oStream.toString()
                }
            }
        }
    }

    companion object {

        @JvmStatic
        @Suppress("NOTHING_TO_INLINE")
        @Throws(IOException::class, InterruptedException::class)
        inline fun ProcessRunner.runAndWait(
            commands: List<String>
        ): String = runAndWait(commands, 250.milliseconds)
    }
}
