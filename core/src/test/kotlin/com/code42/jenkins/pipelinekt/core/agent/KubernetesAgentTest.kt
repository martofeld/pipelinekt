package com.code42.jenkins.pipelinekt.core.agent

import com.code42.jenkins.pipelinekt.core.GroovyScriptTest
import com.code42.jenkins.pipelinekt.core.vars.ext.strDouble
import org.junit.Test
import kotlin.test.assertEquals

class KubernetesAgentTest : GroovyScriptTest() {
    @Test
    fun kubernetes_Serializes_yamlFileOnly() {
        val agent = KubernetesAgent("k8s/pod.yaml".strDouble())

        val expected = """
            agent {
            ${indentStr}kubernetes {
            ${indentStr}${indentStr}yamlFile ${agent.yamlFile.toGroovy()}
            $indentStr}
            }
        """.trimIndent()

        agent.toGroovy(writer)
        assertEquals(expected, out.toString().trim())
    }

    @Test
    fun kubernetes_Serializes_optionalFields() {
        val agent = KubernetesAgent(
            yamlFile = "k8s/pod.yaml".strDouble(),
            label = "ci-pod".strDouble(),
            defaultContainer = "jnlp".strDouble(),
            customWorkspace = "/tmp/ws".strDouble(),
        )

        val expected = """
            agent {
            ${indentStr}kubernetes {
            ${indentStr}${indentStr}yamlFile ${agent.yamlFile.toGroovy()}
            ${indentStr}${indentStr}label ${agent.label!!.toGroovy()}
            ${indentStr}${indentStr}defaultContainer ${agent.defaultContainer!!.toGroovy()}
            ${indentStr}${indentStr}customWorkspace ${agent.customWorkspace!!.toGroovy()}
            $indentStr}
            }
        """.trimIndent()

        agent.toGroovy(writer)
        assertEquals(expected, out.toString().trim())
    }

    @Test
    fun kubernetes_withCustomWorkspaceVariable() {
        val agent = KubernetesAgent(
            yamlFile = "k8s/pod.yaml".strDouble(),
            label = "ci".strDouble(),
            useCustomWorkspaceVariable = true,
        )

        val expected = """
            agent {
            ${indentStr}kubernetes {
            ${indentStr}${indentStr}yamlFile ${agent.yamlFile.toGroovy()}
            ${indentStr}${indentStr}label ${agent.label!!.toGroovy()}
            ${indentStr}${indentStr}customWorkspace customWorkspacePath
            $indentStr}
            }
        """.trimIndent()

        agent.toGroovy(writer)
        assertEquals(expected, out.toString().trim())
    }

    @Test
    fun withCustomWorkspaceVariable_returnsCopyWithFlag() {
        val base = KubernetesAgent("pod.yaml".strDouble())
        val updated = base.withCustomWorkspaceVariable()
        assertEquals(true, updated.useCustomWorkspaceVariable)
        assertEquals(false, base.useCustomWorkspaceVariable)
    }
}
