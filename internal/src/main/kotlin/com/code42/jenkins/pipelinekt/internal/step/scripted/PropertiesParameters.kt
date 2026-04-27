package com.code42.jenkins.pipelinekt.internal.step.scripted

import com.code42.jenkins.pipelinekt.core.Parameter
import com.code42.jenkins.pipelinekt.core.step.ScriptedStep
import com.code42.jenkins.pipelinekt.core.step.SingletonStep
import com.code42.jenkins.pipelinekt.core.writer.Context
import com.code42.jenkins.pipelinekt.core.writer.GroovyWriter
import com.code42.jenkins.pipelinekt.core.writer.ext.allButLast
import java.io.PrintWriter
import java.io.StringWriter

/**
 * Emits `properties([ parameters([ ... ]) ])` using [Parameter.toGroovy] lines (comma-separated).
 */
data class PropertiesParameters(val parameters: List<Parameter>) : ScriptedStep, SingletonStep {
    override fun scriptedGroovy(writer: GroovyWriter) {
        writer.writeln("properties([")
        val inner = writer.inner()
        inner.writeln("parameters([")
        val paramWriter = inner.inner()
        val lines = parameters.map { it.toPropertiesParameterInvocationLine(writer.indentStr) }
        lines.allButLast { "$it," }.forEach { line -> paramWriter.writeln(line) }
        inner.writeln("])")
        writer.writeln("])")
    }
}

private fun Parameter.toPropertiesParameterInvocationLine(indentStr: String): String {
    val sw = StringWriter()
    val capture = GroovyWriter(PrintWriter(sw), indent = 0, context = Context.Scripted, indentStr = indentStr)
    toGroovy(capture)
    return sw.toString().trim()
}
