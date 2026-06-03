package com.code42.jenkins.pipelinekt.core.agent

import com.code42.jenkins.pipelinekt.core.Agent
import com.code42.jenkins.pipelinekt.core.vars.Var
import com.code42.jenkins.pipelinekt.core.writer.GroovyWriter

/**
 * Declarative Jenkins agent running on Kubernetes (Kubernetes plugin), using a Pod spec from [yamlFile].
 *
 * @see <a href="https://www.jenkins.io/doc/book/pipeline/syntax/#agent">Pipeline agent syntax</a>
 */
data class KubernetesAgent(
    val yamlFile: Var.Literal.Str? = null,
    val yaml: Var.Literal.Str.Multiline? = null,
    val label: Var.Literal.Str? = null,
    val defaultContainer: Var.Literal.Str? = null,
    val customWorkspace: Var.Literal.Str? = null,
    val cloud: Var.Literal.Str? = null,
    val useCustomWorkspaceVariable: Boolean = false,
) : Agent {
    override fun toGroovy(writer: GroovyWriter) {
        if (yaml == null && yamlFile == null) {
            throw IllegalArgumentException("One of YAML file OR YAML content must not be null")
        }
        writer.closure("agent") { w ->
            w.closure("kubernetes") { k ->
                yamlFile?.let { k.writeln("yamlFile ${it.toGroovy()}") }
                yaml?.let { k.writeln("yaml ${it.toGroovy()}") }
                label?.let { k.writeln("label ${it.toGroovy()}") }
                defaultContainer?.let { k.writeln("defaultContainer ${it.toGroovy()}") }
                if (useCustomWorkspaceVariable) {
                    k.writeln("customWorkspace customWorkspacePath")
                } else {
                    customWorkspace?.let { k.writeln("customWorkspace ${it.toGroovy()}") }
                }
                cloud?.let { k.writeln("cloud ${it.toGroovy()}") }
            }
        }
    }

    override fun withCustomWorkspaceVariable() = copy(useCustomWorkspaceVariable = true)
}
