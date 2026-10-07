/*
 * Copyright (c) 2023-2026 European Commission
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.europa.ec.eudi.openid4vci.internal

import eu.europa.ec.eudi.openid4vci.SubmissionOutcome
import eu.europa.ec.eudi.openid4vci.internal.http.CredentialResponseSuccessTO
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

// GRNET fork: the credential response's display array, as the WE BUILD rulebook for SCA-Card (DPC)
// attestations delivers the card's display meta-data.
class CredentialResponseDisplayTest {

    @Test
    fun `display array of a credential response is passed through`() {
        val response = JsonSupport.decodeFromString<CredentialResponseSuccessTO>(
            """
            {
              "credentials": [{ "credential": "eyJhbGciOiJFUzI1NiJ9.e30.c2ln~" }],
              "notification_id": "n-1",
              "display": [
                {
                  "card": {
                    "alias": "Gold Mastercard",
                    "last_four": "1234",
                    "card_art": [{ "theme": "DEFAULT", "image_url": "https://bank.example/card.png" }],
                    "network_branding": { "network": "mastercard", "branding": { "name": "Mastercard" } }
                  }
                }
              ]
            }
            """.trimIndent(),
        )

        val outcome = assertIs<SubmissionOutcome.Success>(response.toDomain().toPub())
        val display = checkNotNull(outcome.display).single()
        val card = checkNotNull(display["card"]).jsonObject
        assertEquals("Gold Mastercard", card["alias"]?.jsonPrimitive?.content)
        assertEquals("1234", card["last_four"]?.jsonPrimitive?.content)
    }

    @Test
    fun `display is kept when the reuse policy is selected`() {
        val response = JsonSupport.decodeFromString<CredentialResponseSuccessTO>(
            """{ "credentials": [{ "credential": "c" }], "display": [{ "card": { "last_four": "1234" } }] }""",
        )

        val outcome = assertIs<SubmissionOutcome.Success>(
            response.toDomain().withSelectedCredentialReusePolicy(null).toPub(),
        )
        assertEquals(1, outcome.display?.size)
    }

    @Test
    fun `a credential response without display has none`() {
        val response = JsonSupport.decodeFromString<CredentialResponseSuccessTO>(
            """{ "credentials": [{ "credential": "c" }] }""",
        )

        val outcome = assertIs<SubmissionOutcome.Success>(response.toDomain().toPub())
        assertNull(outcome.display)
    }
}
