package com.code42.jenkins.pipelinekt.internal.step.scripted

import com.code42.jenkins.pipelinekt.GroovyScriptTest
import com.code42.jenkins.pipelinekt.core.step.Void
import com.code42.jenkins.pipelinekt.core.vars.ext.strDouble
import com.code42.jenkins.pipelinekt.core.vars.ext.strSingle
import com.code42.jenkins.pipelinekt.internal.step.declarative.Echo
import org.junit.Test
import kotlin.test.assertEquals

class ScriptedStageTest : GroovyScriptTest() {

    @Test
    fun emptyBodyEmitsNothingAtTopLevel() {
        ScriptedStage("x".strSingle(), Void).toGroovy(writer)
        assertEquals("", out.toString())
    }

    @Test
    fun stageWithLiteralNameWrapsInScriptWhenDeclarativeWriter() {
        ScriptedStage("Build".strSingle(), Echo("ok".strDouble())).toGroovy(writer)
        val expected = "script {\n" +
            "\tstage('Build') {\n" +
            "\t\techo \"ok\"\n" +
            "\t}\n" +
            "}\n"
        assertEquals(expected, out.toString())
    }

    @Test
    fun stageWithConcurrencyUsesMapForm() {
        ScriptedStage("Deploy".strSingle(), Echo("x".strDouble()), concurrency = 1).toGroovy(writer)
        val expected = "script {\n" +
            "\tstage(name: 'Deploy', concurrency: 1) {\n" +
            "\t\techo \"x\"\n" +
            "\t}\n" +
            "}\n"
        assertEquals(expected, out.toString())
    }

    @Test
    fun nestedStagesInsideScriptBlockNoDoubleScriptWrapper() {
        val inner = ScriptedStage(
            "Inner".strSingle(),
            Echo("in".strDouble()),
        )
        ScriptedStage(
            "Outer".strSingle(),
            inner,
        ).toGroovy(writer)
        val expected = "script {\n" +
            "\tstage('Outer') {\n" +
            "\t\tstage('Inner') {\n" +
            "\t\t\techo \"in\"\n" +
            "\t\t}\n" +
            "\t}\n" +
            "}\n"
        assertEquals(expected, out.toString())
    }
}
