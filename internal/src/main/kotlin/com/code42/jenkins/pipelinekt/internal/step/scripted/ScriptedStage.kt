package com.code42.jenkins.pipelinekt.internal.step.scripted

import com.code42.jenkins.pipelinekt.core.step.NestedStep
import com.code42.jenkins.pipelinekt.core.step.ScriptedStep
import com.code42.jenkins.pipelinekt.core.step.Step
import com.code42.jenkins.pipelinekt.core.vars.Var
import com.code42.jenkins.pipelinekt.core.writer.GroovyWriter

/**
 * Scripted Pipeline [stage](https://www.jenkins.io/doc/pipeline/steps/pipeline-stage-step/) step:
 * block-scoped `stage(name) { ... }` or `stage(name: ..., concurrency: ...) { ... }`.
 *
 * Emit this inside declarative `script { }` or any [com.code42.jenkins.pipelinekt.core.writer.Context.Scripted]
 * context so nested bodies are not wrapped in an extra `script { }`. Arbitrary nesting of `stage` blocks
 * matches Jenkins' scripted pipeline behavior.
 *
 * @param name Stage label ([Var] allows static literals, double-quoted interpolation, or arbitrary Groovy).
 * @param steps Steps executed inside the stage closure.
 * @param concurrency Optional concurrency limit for this stage name (Jenkins `stage(name: '…', concurrency: n)`).
 */
data class ScriptedStage(
    val name: Var,
    override val steps: Step,
    val concurrency: Int? = null,
) : ScriptedStep, NestedStep {

    override fun toGroovy(writer: GroovyWriter) {
        if (steps.isEmpty()) {
            return
        }
        writer.scripted(this::scriptedGroovy)
    }

    override fun scriptedGroovy(writer: GroovyWriter) {
        val prefix = if (concurrency != null) {
            "stage(name: ${name.toGroovy()}, concurrency: $concurrency)"
        } else {
            "stage(${name.toGroovy()})"
        }
        writer.closure(prefix, steps::toGroovy)
    }
}
