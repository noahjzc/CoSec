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

package me.ahoo.cosec.policy.action

import me.ahoo.cosec.api.configuration.Configuration
import me.ahoo.cosec.api.context.SecurityContext
import me.ahoo.cosec.api.context.request.Request
import me.ahoo.cosec.api.policy.ActionMatcher
import me.ahoo.cosec.configuration.JsonConfiguration.Companion.asConfiguration
import me.ahoo.cosec.policy.action.PathPatternParsers.asPathPatternParser
import org.springframework.http.server.PathContainer
import org.springframework.web.util.pattern.PathPattern
import org.springframework.web.util.pattern.PathPatternParser

class PathActionMatcher(
    private val patternParser: PathPatternParser,
    private val pathPattern: PathPattern,
    override val configuration: Configuration
) :
    ActionMatcher {
    override val type: String
        get() = PathActionMatcherFactory.TYPE

    override fun match(request: Request, securityContext: SecurityContext): Boolean {
        PathContainer.parsePath(request.path, patternParser.pathOptions)
            .let { pathContainer ->
                return pathPattern.matches(pathContainer)
            }
    }
}

class ReplaceablePathActionMatcher(
    private val patternParser: PathPatternParser,
    private val pattern: String,
    override val configuration: Configuration
) : ActionMatcher {
    override val type: String
        get() = PathActionMatcherFactory.TYPE

    override fun match(request: Request, securityContext: SecurityContext): Boolean {
        val pathPattern = ActionPatternReplacer.replace(pattern, securityContext)
        val pathContainer = PathContainer.parsePath(request.path)
        patternParser.parse(pathPattern).let {
            return it.matches(pathContainer)
        }
    }
}

class CompositePathActionMatcher(
    private val actionMatchers: List<ActionMatcher>,
    override val configuration: Configuration
) : ActionMatcher {
    override val type: String
        get() = PathActionMatcherFactory.TYPE

    override fun match(request: Request, securityContext: SecurityContext): Boolean {
        return actionMatchers.any { pathActionMatcher ->
            pathActionMatcher.match(request, securityContext)
        }
    }
}

class PathActionMatcherFactory : ActionMatcherFactory {
    companion object {
        const val TYPE = "path"
        private const val PATTERN_KEY = "pattern"
        val INSTANCE = PathActionMatcherFactory()

        fun String.asPathActionMatcher(
            configuration: Configuration = this.asConfiguration(),
            patternParser: PathPatternParser = PathPatternParser.defaultInstance
        ): ActionMatcher {
            return if (ActionPatternReplacer.isTemplate(this)) {
                ReplaceablePathActionMatcher(
                    patternParser = patternParser,
                    pattern = this,
                    configuration = configuration
                )
            } else {
                PathActionMatcher(
                    patternParser = patternParser,
                    pathPattern = patternParser.parse(this),
                    configuration = configuration
                )
            }
        }

        fun Configuration.stringAsActionMatcher(): ActionMatcher {
            return this.asString().asPathActionMatcher(this)
        }

        fun Configuration.arrayAsActionMatcher(): ActionMatcher {
            asList()
                .map { it.stringAsActionMatcher() }
                .let { actionMatchers ->
                    return CompositePathActionMatcher(
                        actionMatchers = actionMatchers,
                        configuration = this
                    )
                }
        }

        fun Configuration.objectAsActionMatcher(): ActionMatcher {
            val patternParser = asPathPatternParser()
            val patternConfiguration = getRequired(PATTERN_KEY)
            if (patternConfiguration.isString) {
                return patternConfiguration.asString().asPathActionMatcher(this, patternParser)
            }
            patternConfiguration.asList()
                .map { it.asString().asPathActionMatcher(this, patternParser) }
                .let { actionMatchers ->
                    return CompositePathActionMatcher(
                        actionMatchers = actionMatchers,
                        configuration = this
                    )
                }
        }
    }

    override val type: String
        get() = TYPE

    override fun create(configuration: Configuration): ActionMatcher {
        if (configuration.isString) {
            return configuration.stringAsActionMatcher()
        }

        if (configuration.isArray) {
            if (configuration.asStringList().contains(AllActionMatcher.ALL)) {
                return AllActionMatcher
            }
            return configuration.arrayAsActionMatcher()
        }
        return configuration.objectAsActionMatcher()
    }
}
