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

package me.ahoo.cosec.policy.condition.limiter

import com.google.common.util.concurrent.RateLimiter
import me.ahoo.cosec.api.configuration.Configuration
import me.ahoo.cosec.api.context.SecurityContext
import me.ahoo.cosec.api.context.request.Request
import me.ahoo.cosec.api.policy.ConditionMatcher
import me.ahoo.cosec.policy.condition.ConditionMatcherFactory

const val RATE_LIMITER_CONDITION_MATCHER_PERMITS_PER_SECOND_KEY = "permitsPerSecond"

class RateLimiterConditionMatcher(
    override val configuration: Configuration
) : ConditionMatcher {
    override val type: String
        get() = RateLimiterConditionMatcherFactory.TYPE
    private val permitsPerSecond: Double =
        requireNotNull(configuration.get(RATE_LIMITER_CONDITION_MATCHER_PERMITS_PER_SECOND_KEY)) {
            "permitsPerSecond is required!"
        }.asDouble()
    private val rateLimiter = RateLimiter.create(permitsPerSecond)
    override fun match(request: Request, securityContext: SecurityContext): Boolean {
        if (rateLimiter.tryAcquire()) {
            return true
        }
        throw TooManyRequestsException()
    }
}

class RateLimiterConditionMatcherFactory : ConditionMatcherFactory {
    companion object {
        const val TYPE = "rateLimiter"
    }

    override val type: String
        get() = TYPE

    override fun create(configuration: Configuration): ConditionMatcher {
        return RateLimiterConditionMatcher(configuration)
    }
}
