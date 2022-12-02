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
package me.ahoo.cosec.oauth.client

import me.ahoo.cosec.api.principal.CoSecPrincipal
import me.ahoo.cosec.oauth.OAuthUser
import me.ahoo.cosec.principal.SimplePrincipal
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

/**
 * DirectOAuthPrincipalConverter .
 *
 * @author ahoo wang
 */
object DirectOAuthClientPrincipalConverter : OAuthClientPrincipalConverter {

    override fun convert(client: String, authUser: OAuthUser): Mono<CoSecPrincipal> {
        authUser.rawInfo[OAuthClientPrincipalConverter.OAUTH_CLIENT] = client
        return SimplePrincipal(
            id = asClientUserId(client, authUser),
            name = authUser.username,
            policies = emptySet(),
            roles = emptySet(),
            attrs = authUser.rawInfo
        ).toMono()
    }

    /**
     * format: [OAuthUser.id]@[client] as unique id.
     *
     * @param client client
     * @param authUser authUser
     * @return unique id
     */
    private fun asClientUserId(client: String, authUser: OAuthUser): String {
        return authUser.id + "@" + client
    }
}
