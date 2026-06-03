package com.code42.jenkins.pipelinekt.core.agent

import com.code42.jenkins.pipelinekt.core.GroovyScriptTest
import com.code42.jenkins.pipelinekt.core.vars.ext.multline
import com.code42.jenkins.pipelinekt.core.vars.ext.strDouble
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class KubernetesAgentTest : GroovyScriptTest() {
    /** Matches [GroovyWriter.writeln] indentation for a line emitted at [indentLevel]. */
    private fun indentedLine(line: String, indentLevel: Int = 2): String {
        val prefix = indentStr.repeat(indentLevel)
        return prefix + line.replace("\n", "\n$prefix")
    }

    private fun kubernetesAgentBlock(vararg kubernetesLines: String): String = buildList {
        add("agent {")
        add("${indentStr}kubernetes {")
        addAll(kubernetesLines)
        add("$indentStr}")
        add("}")
    }.joinToString("\n")

    @Test
    fun kubernetes_Serializes_yamlFileOnly() {
        val agent = KubernetesAgent(yamlFile = "k8s/pod.yaml".strDouble())

        val expected = """
            agent {
            ${indentStr}kubernetes {
            ${indentStr}${indentStr}yamlFile ${agent.yamlFile!!.toGroovy()}
            $indentStr}
            }
        """.trimIndent()

        agent.toGroovy(writer)
        assertEquals(expected, out.toString().trim())
    }

    @Test
    fun kubernetes_Serializes_yamlOnly() {
        val agent = KubernetesAgent(yaml = "apiVersion: v1".multline())

        val expected = """
            agent {
            ${indentStr}kubernetes {
            ${indentStr}${indentStr}yaml ${agent.yaml!!.toGroovy()}
            $indentStr}
            }
        """.trimIndent()

        agent.toGroovy(writer)
        assertEquals(expected, out.toString().trim())
    }

    @Test
    fun kubernetes_Serializes_multilineYaml() {
        val yamlContent = """
            apiVersion: v1
            kind: Pod
            metadata:
              labels:
                jenkins: agent
            spec:
              containers:
              - name: jnlp
                image: jenkins/inbound-agent
        """.trimIndent()
        val agent = KubernetesAgent(yaml = yamlContent.multline())

        val expected = kubernetesAgentBlock(indentedLine("yaml ${agent.yaml!!.toGroovy()}"))

        agent.toGroovy(writer)
        assertEquals(expected, out.toString().trim())
    }

    @Test
    fun kubernetes_Serializes_yamlWithOptionalFields() {
        val yamlContent = """
            apiVersion: v1
            kind: Pod
        """.trimIndent()
        val agent = KubernetesAgent(
            yaml = yamlContent.multline(),
            label = "ci-pod".strDouble(),
            defaultContainer = "jnlp".strDouble(),
            cloud = "eks".strDouble(),
        )

        val expected = kubernetesAgentBlock(
            indentedLine("yaml ${agent.yaml!!.toGroovy()}"),
            "${indentStr.repeat(2)}label ${agent.label!!.toGroovy()}",
            "${indentStr.repeat(2)}defaultContainer ${agent.defaultContainer!!.toGroovy()}",
            "${indentStr.repeat(2)}cloud ${agent.cloud!!.toGroovy()}",
        )

        agent.toGroovy(writer)
        assertEquals(expected, out.toString().trim())
    }

    @Test
    fun kubernetes_throwsWhenNeitherYamlNorYamlFile() {
        val agent = KubernetesAgent()
        assertFailsWith<IllegalArgumentException> {
            agent.toGroovy(writer)
        }
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
            ${indentStr}${indentStr}yamlFile ${agent.yamlFile!!.toGroovy()}
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
            ${indentStr}${indentStr}yamlFile ${agent.yamlFile!!.toGroovy()}
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
        val base = KubernetesAgent(yamlFile = "pod.yaml".strDouble())
        val updated = base.withCustomWorkspaceVariable()
        assertEquals(true, updated.useCustomWorkspaceVariable)
        assertEquals(false, base.useCustomWorkspaceVariable)
    }
}
