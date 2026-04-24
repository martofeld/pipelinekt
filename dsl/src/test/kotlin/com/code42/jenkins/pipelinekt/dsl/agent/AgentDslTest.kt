package com.code42.jenkins.pipelinekt.dsl.agent

import com.code42.jenkins.pipelinekt.core.agent.KubernetesAgent
import com.code42.jenkins.pipelinekt.core.vars.ext.strDouble
import com.code42.jenkins.pipelinekt.dsl.PipelineDsl
import com.code42.jenkins.pipelinekt.dsl.step.declarative.echo
import com.code42.jenkins.pipelinekt.internal.agent.None
import org.junit.Test
import kotlin.test.assertEquals

class AgentDslTest {
    @Test
    fun pipeline_agentNone() {
        val dsl = PipelineDsl(defaultEnvironment = {}, defaultBuildOptions = {})
        val pipeline = dsl.pipeline {
            agent { none() }
            stages {
                stage("Build") {
                    steps {
                        echo("ok")
                    }
                }
            }
        }
        assertEquals(None, pipeline.agent)
    }

    @Test
    fun pipeline_kubernetesYamlFile() {
        val dsl = PipelineDsl(defaultEnvironment = {}, defaultBuildOptions = {})
        val pipeline = dsl.pipeline {
            agent {
                kubernetesYamlFile(
                    file = "k8s/agent.yaml",
                    label = "ci",
                    defaultContainer = "jnlp",
                )
            }
            stages {
                stage("Build") {
                    steps {
                        echo("ok")
                    }
                }
            }
        }
        val expected = KubernetesAgent(
            yamlFile = "k8s/agent.yaml".strDouble(),
            label = "ci".strDouble(),
            defaultContainer = "jnlp".strDouble(),
        )
        assertEquals(expected, pipeline.agent)
    }
}
