/*
 * Copyright [2021-present] [ahoo wang <ahoowang@qq.com> (https://github.com/Ahoo-Wang)].
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.ahoo.cosec.policy.condition

import io.mockk.every
import io.mockk.mockk
import me.ahoo.cosec.api.context.SecurityContext
import me.ahoo.cosec.configuration.JsonConfiguration.Companion.asConfiguration
import me.ahoo.cosec.policy.MATCHER_PATTERN_KEY
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Test

internal class SpelConditionMatcherTest {
    @Test
    fun simpleMatch() {
        val conditionMatcher = SpelConditionMatcher(mapOf(MATCHER_PATTERN_KEY to "1==1").asConfiguration())
        assertThat(conditionMatcher.match(mockk(), mockk()), `is`(true))
    }

    @Test
    fun match() {
        val conditionMatcher =
            SpelConditionMatcher(mapOf(MATCHER_PATTERN_KEY to "context.principal.id=='1'").asConfiguration())
        val securityContext = mockk<SecurityContext>() {
            every { principal } returns mockk {
                every { id } returns "1"
            }
        }
        assertThat(conditionMatcher.match(mockk(), securityContext), `is`(true))
    }
}
