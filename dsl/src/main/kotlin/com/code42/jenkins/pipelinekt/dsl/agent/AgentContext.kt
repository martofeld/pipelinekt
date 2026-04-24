package com.code42.jenkins.pipelinekt.dsl.agent

import com.code42.jenkins.pipelinekt.core.Agent
import com.code42.jenkins.pipelinekt.core.agent.DockerAgent
import com.code42.jenkins.pipelinekt.core.agent.KubernetesAgent
import com.code42.jenkins.pipelinekt.core.vars.Var
import com.code42.jenkins.pipelinekt.core.vars.ext.boolVar
import com.code42.jenkins.pipelinekt.core.vars.ext.strDouble
import com.code42.jenkins.pipelinekt.dsl.SingletonDslContext
import com.code42.jenkins.pipelinekt.internal.agent.Label
import com.code42.jenkins.pipelinekt.internal.agent.None

fun SingletonDslContext<in DockerAgent>.dockerImage(
    image: String,
    args: String? = null,
    label: String? = null,
    customWorkspace: String? = null,
    registryUrl: String? = null,
    registryCredentialsId: String? = null,
    reuseNode: Boolean? = null,
) {
    dockerImage(
        image.strDouble(),
        args?.strDouble(),
        label?.strDouble(),
        customWorkspace?.strDouble(),
        registryUrl?.strDouble(),
        registryCredentialsId?.strDouble(),
        reuseNode?.boolVar(),
    )
}

fun SingletonDslContext<in DockerAgent>.dockerImage(
    image: Var.Literal.Str,
    args: Var.Literal.Str? = null,
    label: Var.Literal.Str? = null,
    customWorkspace: Var.Literal.Str? = null,
    registryUrl: Var.Literal.Str? = null,
    registryCredentialsId: Var.Literal.Str? = null,
    reuseNode: Var.Literal.Bool? = null,
) {
    add(DockerAgent.Image(image, args, label, customWorkspace, registryUrl, registryCredentialsId, reuseNode))
}

fun SingletonDslContext<in DockerAgent>.dockerFile(
    filename: String,
    dir: String? = null,
    additionalBuildArgs: String? = null,
    args: String? = null,
    label: String? = null,
    customWorkspace: String? = null,
    registryUrl: String? = null,
    registryCredentialsId: String? = null,
    reuseNode: Boolean? = null,
) {
    dockerFile(
        filename.strDouble(), dir?.strDouble(), additionalBuildArgs?.strDouble(), args?.strDouble(),
        label?.strDouble(), customWorkspace?.strDouble(), registryUrl?.strDouble(), registryCredentialsId?.strDouble(), reuseNode?.boolVar(),
    )
}

fun SingletonDslContext<in DockerAgent>.dockerFile(
    filename: Var.Literal.Str,
    dir: Var.Literal.Str? = null,
    additionalBuildArgs: Var.Literal.Str? = null,
    args: Var.Literal.Str? = null,
    label: Var.Literal.Str? = null,
    customWorkspace: Var.Literal.Str? = null,
    registryUrl: Var.Literal.Str? = null,
    registryCredentialsId: Var.Literal.Str? = null,
    reuseNode: Var.Literal.Bool? = null,
) {
    add(DockerAgent.File(filename, dir, additionalBuildArgs, args, label, customWorkspace, registryUrl, registryCredentialsId, reuseNode))
}

fun SingletonDslContext<Agent>.label(name: String) {
    label(name.strDouble())
}

fun SingletonDslContext<Agent>.label(name: Var.Literal.Str) {
    add(Label(name))
}

fun SingletonDslContext<Agent>.none() {
    add(None)
}

fun SingletonDslContext<Agent>.any() {
    add(com.code42.jenkins.pipelinekt.internal.agent.Any)
}

fun SingletonDslContext<in KubernetesAgent>.kubernetesYamlFile(
    file: String,
    label: String? = null,
    defaultContainer: String? = null,
    customWorkspace: String? = null,
    cloud: String? = null,
) {
    kubernetesYamlFile(
        file.strDouble(),
        label?.strDouble(),
        defaultContainer?.strDouble(),
        customWorkspace?.strDouble(),
        cloud?.strDouble()
    )
}

fun SingletonDslContext<in KubernetesAgent>.kubernetesYamlFile(
    file: Var.Literal.Str,
    label: Var.Literal.Str? = null,
    defaultContainer: Var.Literal.Str? = null,
    customWorkspace: Var.Literal.Str? = null,
    cloud: Var.Literal.Str? = null,
) {
    add(KubernetesAgent(file, label, defaultContainer, customWorkspace, cloud))
}