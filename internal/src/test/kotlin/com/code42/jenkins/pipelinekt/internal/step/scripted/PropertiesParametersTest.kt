package com.code42.jenkins.pipelinekt.internal.step.scripted

import com.code42.jenkins.pipelinekt.GroovyScriptTest
import com.code42.jenkins.pipelinekt.core.vars.ext.strDouble
import com.code42.jenkins.pipelinekt.internal.parameters.BooleanParam
import com.code42.jenkins.pipelinekt.internal.parameters.Choice
import com.code42.jenkins.pipelinekt.internal.parameters.StringParam
import org.junit.Test
import kotlin.test.assertEquals

class PropertiesParametersTest : GroovyScriptTest() {
    @Test
    fun emitsPropertiesParametersBlock() {
        val params = listOf(
            Choice(
                name = "product".strDouble(),
                choices = listOf("sportybet".strDouble()),
                description = "".strDouble(),
            ),
            BooleanParam(
                defaultValue = false,
                description = "".strDouble(),
                name = "is_google_play_version".strDouble(),
            ),
            StringParam(
                defaultValue = " ".strDouble(),
                description = "".strDouble(),
                name = "server_replica".strDouble(),
                trim = true,
            ),
        )

        PropertiesParameters(params).toGroovy(writer)

        val expected =
            "script {\n" +
                "\tproperties([\n" +
                "\t\tparameters([\n" +
                "\t\t\tchoice(\n" +
                "\t\t\t\tname: \"product\",\n" +
                "\t\t\t\tdescription: \"\",\n" +
                "\t\t\t\tchoices: [\n" +
                "\t\t\t\t\t\"sportybet\"\n" +
                "\t\t\t\t]),\n" +
                "\t\t\tbooleanParam(defaultValue: false, description: \"\", name: \"is_google_play_version\"),\n" +
                "\t\t\tstring(defaultValue: \" \", description: \"\", name: \"server_replica\", trim: true)\n" +
                "\t\t])\n" +
                "\t])\n" +
                "}\n"

        assertEquals(expected, out.toString())
    }
}
