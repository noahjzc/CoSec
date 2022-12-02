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

package me.ahoo.cosec.webflux

import io.mockk.every
import io.mockk.mockk
import me.ahoo.cosec.api.tenant.Tenant
import me.ahoo.cosec.context.request.RequestTenantIdParser
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.server.ServerWebExchange

internal class ReactiveRequestTenantIdParserTest {

    @Test
    fun parse() {
        val requestTenantIdParser = ReactiveRequestTenantIdParser()
        val request = mockk<ServerWebExchange> {
            every { request.headers.getFirst(RequestTenantIdParser.TENANT_ID_KEY) } returns "tenantId"
        }
        val tenantId = requestTenantIdParser.parse(request)
        assertThat(tenantId, `is`("tenantId"))
    }

    @Test
    fun parseFromQueryString() {
        val requestTenantIdParser = ReactiveRequestTenantIdParser()
        val request = mockk<ServerWebExchange> {
            every { request.headers.getFirst(RequestTenantIdParser.TENANT_ID_KEY) } returns ""
            every { request.queryParams } returns LinkedMultiValueMap(
                mapOf(
                    RequestTenantIdParser.TENANT_ID_KEY to listOf(
                        "tenantId"
                    )
                )
            )
        }
        val tenantId = requestTenantIdParser.parse(request)
        assertThat(tenantId, equalTo("tenantId"))
    }

    @Test
    fun parseNone() {
        val requestTenantIdParser = ReactiveRequestTenantIdParser()
        val request = mockk<ServerWebExchange> {
            every { request.headers.getFirst(RequestTenantIdParser.TENANT_ID_KEY) } returns ""
            every { request.queryParams } returns LinkedMultiValueMap()
        }
        val tenantId = requestTenantIdParser.parse(request)
        assertThat(tenantId, `is`(Tenant.DEFAULT_TENANT_ID))
    }
}
