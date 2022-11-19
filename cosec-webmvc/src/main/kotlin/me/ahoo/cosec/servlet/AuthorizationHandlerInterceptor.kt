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
package me.ahoo.cosec.servlet

import me.ahoo.cosec.authorization.Authorization
import me.ahoo.cosec.context.SecurityContextParser
import me.ahoo.cosec.context.request.RequestParser
import org.springframework.web.servlet.HandlerInterceptor
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Authorization Handler Interceptor .
 *
 * @author ahoo wang
 */
class AuthorizationHandlerInterceptor(
    securityContextParser: SecurityContextParser<HttpServletRequest>,
    authorization: Authorization,
    requestParser: RequestParser<HttpServletRequest>
) : AbstractAuthorizationInterceptor(requestParser, securityContextParser, authorization), HandlerInterceptor {
    @Throws(Exception::class)
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        return authorize(request, response)
    }
}
