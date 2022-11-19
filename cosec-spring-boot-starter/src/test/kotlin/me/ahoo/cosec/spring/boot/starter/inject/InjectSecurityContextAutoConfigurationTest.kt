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

package me.ahoo.cosec.spring.boot.starter.inject

import me.ahoo.cosec.servlet.InjectSecurityContextFilter
import me.ahoo.cosec.servlet.InjectSecurityContextParser
import me.ahoo.cosec.webflux.ReactiveInjectSecurityContextParser
import me.ahoo.cosec.webflux.ReactiveInjectSecurityContextWebFilter
import org.assertj.core.api.AssertionsForInterfaceTypes
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.assertj.AssertableApplicationContext
import org.springframework.boot.test.context.runner.ApplicationContextRunner

internal class InjectSecurityContextAutoConfigurationTest {
    private val contextRunner = ApplicationContextRunner()

    @Test
    fun contextLoadsWhenDefault() {
        contextRunner
            .withUserConfiguration(InjectSecurityContextAutoConfiguration::class.java)
            .run { context: AssertableApplicationContext ->
                AssertionsForInterfaceTypes.assertThat(context)
                    .doesNotHaveBean(InjectSecurityContextProperties::class.java)
                    .doesNotHaveBean(InjectSecurityContextAutoConfiguration::class.java)
            }
    }

    @Test
    fun contextLoadsWhenEnabled() {
        contextRunner
            .withPropertyValues("${ConditionalOnInjectSecurityEnabled.ENABLED_KEY}=true")
            .withUserConfiguration(InjectSecurityContextAutoConfiguration::class.java)
            .run { context: AssertableApplicationContext ->
                AssertionsForInterfaceTypes.assertThat(context)
                    .hasSingleBean(InjectSecurityContextProperties::class.java)
                    .hasSingleBean(InjectSecurityContextAutoConfiguration::class.java)
                    .hasSingleBean(InjectSecurityContextParser::class.java)
                    .hasSingleBean(InjectSecurityContextFilter::class.java)
                    .hasSingleBean(ReactiveInjectSecurityContextParser::class.java)
                    .hasSingleBean(ReactiveInjectSecurityContextWebFilter::class.java)
            }
    }
}
