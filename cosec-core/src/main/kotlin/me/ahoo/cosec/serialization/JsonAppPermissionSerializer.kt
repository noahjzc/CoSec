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

package me.ahoo.cosec.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import me.ahoo.cosec.api.permission.AppPermission
import me.ahoo.cosec.api.permission.PermissionGroup
import me.ahoo.cosec.api.policy.ConditionMatcher
import me.ahoo.cosec.permission.AppPermissionData
import me.ahoo.cosec.policy.condition.AllConditionMatcher

const val APP_PERMISSION_ID_KEY = "id"
const val APP_PERMISSION_GROUPS_KEY = "groups"

object JsonAppPermissionSerializer : StdSerializer<AppPermission>(AppPermission::class.java) {
    override fun serialize(value: AppPermission, gen: JsonGenerator, provider: SerializerProvider) {
        gen.writeStartObject()
        gen.writeStringField(APP_PERMISSION_ID_KEY, value.id)
        gen.writePOJOField(STATEMENT_CONDITION_KEY, value.condition)
        if (value.groups.isNotEmpty()) {
            gen.writeArrayFieldStart(APP_PERMISSION_GROUPS_KEY)
            value.groups.forEach {
                gen.writeObject(it)
            }
            gen.writeEndArray()
        }
        gen.writeEndObject()
    }
}

object JsonAppPermissionDeserializer : StdDeserializer<AppPermission>(AppPermission::class.java) {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): AppPermission {
        val jsonNode = p.codec.readTree<JsonNode>(p)
        val condition =
            jsonNode.get(STATEMENT_CONDITION_KEY)?.traverse(p.codec)?.readValueAs(ConditionMatcher::class.java)
                ?: AllConditionMatcher.INSTANCE
        val groups = jsonNode.get(APP_PERMISSION_GROUPS_KEY)?.map {
            it.traverse(p.codec).readValueAs(PermissionGroup::class.java)
        }.orEmpty()
        return AppPermissionData(
            id = requireNotNull(jsonNode.get(APP_PERMISSION_ID_KEY)) {
                "$APP_PERMISSION_ID_KEY is required!"
            }.asText(),
            condition = condition,
            groups = groups,
        )
    }
}
