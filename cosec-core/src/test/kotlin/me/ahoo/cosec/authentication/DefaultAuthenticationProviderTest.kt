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

package me.ahoo.cosec.authentication

import io.mockk.mockk
import me.ahoo.cosec.api.token.TokenPrincipal
import me.ahoo.cosec.authentication.token.RefreshTokenCredentials
import me.ahoo.cosec.authentication.token.SimpleRefreshTokenAuthentication
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class DefaultAuthenticationProviderTest {

    @Test
    fun register() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            DefaultAuthenticationProvider.getRequired<RefreshTokenCredentials, TokenPrincipal, SimpleRefreshTokenAuthentication>(
                RefreshTokenCredentials::class.java
            )
        }
        val refreshTokenAuthentication = SimpleRefreshTokenAuthentication(mockk())
        DefaultAuthenticationProvider.register(refreshTokenAuthentication)
        assertThat(
            DefaultAuthenticationProvider.getRequired<RefreshTokenCredentials, TokenPrincipal, SimpleRefreshTokenAuthentication>(
                RefreshTokenCredentials::class.java
            ),
            `is`(refreshTokenAuthentication)
        )
    }
}