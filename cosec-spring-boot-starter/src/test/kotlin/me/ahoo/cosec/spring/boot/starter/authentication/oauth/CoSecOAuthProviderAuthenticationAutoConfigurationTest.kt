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

package me.ahoo.cosec.spring.boot.starter.authentication.oauth

import me.ahoo.cosec.api.authentication.AuthenticationProvider
import me.ahoo.cosec.api.principal.CoSecPrincipal
import me.ahoo.cosec.oauth.OAuthAuthentication
import me.ahoo.cosec.oauth.OAuthCredentials
import me.ahoo.cosec.oauth.OAuthProviderManager
import me.ahoo.cosec.oauth.OAuthUserPrincipalConverter
import me.ahoo.cosec.spring.boot.starter.authentication.CoSecAuthenticationAutoConfiguration
import me.ahoo.cosec.spring.boot.starter.authentication.ConditionalOnAuthenticationEnabled
import me.ahoo.cosid.IdGenerator
import me.ahoo.cosid.test.MockIdGenerator
import me.zhyd.oauth.cache.AuthStateCache
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration
import org.springframework.boot.test.context.assertj.AssertableApplicationContext
import org.springframework.boot.test.context.runner.ApplicationContextRunner

internal class CoSecOAuthProviderAuthenticationAutoConfigurationTest {
    private val contextRunner = ApplicationContextRunner()

    @Test
    fun contextLoads() {
        contextRunner
            .withPropertyValues(
                "${OAuthAuthenticationProperties.PREFIX}.registration.google.type=google",
                "${OAuthAuthenticationProperties.PREFIX}.registration.google.client-id=client-id",
                "${OAuthAuthenticationProperties.PREFIX}.registration.google.client-secret=client-secret",
                "${OAuthAuthenticationProperties.PREFIX}.registration.google.redirect-uri=https://github.com/Ahoo-Wang/CoCache/oauth-client/callback/google",
                "${OAuthAuthenticationProperties.PREFIX}.registration.github.type=github",
                "${OAuthAuthenticationProperties.PREFIX}.registration.github.client-id=client-id",
                "${OAuthAuthenticationProperties.PREFIX}.registration.github.client-secret=client-secret",
                "${OAuthAuthenticationProperties.PREFIX}.registration.github.redirect-uri=https://github.com/Ahoo-Wang/CoCache/oauth-client/callback/github",
            )
            .withBean(IdGenerator::class.java, { MockIdGenerator.INSTANCE })
            .withUserConfiguration(
                RedisAutoConfiguration::class.java,
                CoSecAuthenticationAutoConfiguration::class.java,
                CoSecOAuthAuthenticationAutoConfiguration::class.java,
            )
            .run { context: AssertableApplicationContext ->
                assertThat(context)
                    .hasSingleBean(OAuthAuthenticationProperties::class.java)
                    .hasSingleBean(CoSecOAuthAuthenticationAutoConfiguration::class.java)
                    .hasSingleBean(AuthenticationProvider::class.java)
                    .hasSingleBean(AuthStateCache::class.java)
                    .hasSingleBean(OAuthProviderManager::class.java)
                    .hasSingleBean(OAuthUserPrincipalConverter::class.java)
                    .hasSingleBean(OAuthAuthentication::class.java)
                    .getBean(AuthenticationProvider::class.java)
                    .extracting {
                        it.getRequired<OAuthCredentials, CoSecPrincipal, OAuthAuthentication>(
                            OAuthCredentials::class.java,
                        )
                    }

                assertThat(context)
                    .getBean(OAuthProviderManager::class.java)
                    .extracting {
                        it.getRequired("google")
                        it.getRequired("github")
                    }
            }
    }

    @Test
    fun contextLoadsDisable() {
        contextRunner
            .withPropertyValues("${ConditionalOnAuthenticationEnabled.ENABLED_KEY}=false")
            .withUserConfiguration(
                CoSecAuthenticationAutoConfiguration::class.java,
                CoSecOAuthAuthenticationAutoConfiguration::class.java,
            )
            .run { context: AssertableApplicationContext ->
                assertThat(context)
                    .doesNotHaveBean(OAuthAuthenticationProperties::class.java)
                    .doesNotHaveBean(CoSecOAuthAuthenticationAutoConfiguration::class.java)
            }
    }
}
