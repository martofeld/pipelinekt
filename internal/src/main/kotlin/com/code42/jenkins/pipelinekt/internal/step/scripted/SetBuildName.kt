package com.code42.jenkins.pipelinekt.internal.step.scripted

import com.code42.jenkins.pipelinekt.core.step.ScriptedStep
import com.code42.jenkins.pipelinekt.core.step.SingletonStep
import com.code42.jenkins.pipelinekt.core.writer.GroovyWriter

/**
 * Set the description of a build.
 *
 * @param name The display name of this build.
 */
data class SetBuildName(val name: String) : ScriptedStep, SingletonStep {
    override fun scriptedGroovy(writer: GroovyWriter) {
        writer.writeln("currentBuild.displayName = \"${name}\"")
    }
}
