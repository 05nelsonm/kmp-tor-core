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
package io.matthewnelson.kmp.tor.core.resource

import io.matthewnelson.kmp.tor.core.api.annotation.InternalKmpTorApi
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.measureTime

@OptIn(InternalKmpTorApi::class)
class ProcessExtUnitTest {

    @Test
    fun givenProcess_whenWaitFor_thenBlocksUntilCompletion() {
        when (OSInfo.INSTANCE.osHost) {
            is OSHost.Unknown,
            is OSHost.Windows -> return
            else -> { /* run */ }
        }

        val runTime = measureTime {
            val p = Runtime.getRuntime().exec(arrayOf("sleep", "0.25"))
            assertFalse(p.waitFor(100.milliseconds, destroyOnTimeout = false))
            assertTrue(p.waitFor(2.seconds))
        }

        assertTrue(runTime < 1.seconds)
    }
}
