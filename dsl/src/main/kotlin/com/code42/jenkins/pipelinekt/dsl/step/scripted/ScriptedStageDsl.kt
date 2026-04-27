package com.code42.jenkins.pipelinekt.dsl.step.scripted

import com.code42.jenkins.pipelinekt.core.step.Step
import com.code42.jenkins.pipelinekt.core.vars.Var
import com.code42.jenkins.pipelinekt.core.vars.ext.strSingle
import com.code42.jenkins.pipelinekt.core.writer.ext.toStep
import com.code42.jenkins.pipelinekt.dsl.DslContext
import com.code42.jenkins.pipelinekt.internal.step.scripted.ScriptedStage

/**
 * Scripted `stage('Name') { ... }` or `stage(name: 'Name', concurrency: n) { ... }` (see [ScriptedStage]).
 */
fun DslContext<Step>.scriptedStage(
    name: String,
    concurrency: Int? = null,
    block: DslContext<Step>.() -> Unit,
) {
    scriptedStage(name.strSingle(), concurrency, block)
}

/**
 * Scripted stage with a [Var] name (e.g. [com.code42.jenkins.pipelinekt.core.vars.ext.strDouble] for
 * `"[${env.MATRIX_CHANNEL}] Build"`-style labels, or [Var.Literal.Inline] for raw Groovy).
 */
fun DslContext<Step>.scriptedStage(name: Var, concurrency: Int? = null, block: DslContext<Step>.() -> Unit) {
    add(ScriptedStage(name, DslContext.into(block).toStep(), concurrency))
}
