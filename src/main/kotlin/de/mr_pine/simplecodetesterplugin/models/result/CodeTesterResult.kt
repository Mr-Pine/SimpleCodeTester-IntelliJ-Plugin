package de.mr_pine.simplecodetesterplugin.models.result

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.time.Duration

@Serializable(with = ResultSerializer::class)
data class CodeTesterResult(
    val fileResults: Map<String, List<CheckResult>>? = null,
    val compilationOutput: CompilationOutput
) {
    @Transient
    var duration: Duration? = null
}

@Serializable
private data class CorrectResult(
    val fileResults: Map<String, List<CheckResult>>? = null,
    val compilationOutput: CompilationOutput
)

private class ResultSerializer: KSerializer<CodeTesterResult> {
    val correctResultSerializer = CorrectResult.serializer()
    val compilationOutputSerializer = CompilationOutput.serializer()

    @OptIn(ExperimentalSerializationApi::class)
    override val descriptor = SerialDescriptor("Robust", correctResultSerializer.descriptor)

    override fun deserialize(decoder: Decoder): CodeTesterResult {
        return try {
            correctResultSerializer.deserialize(decoder).let { CodeTesterResult(it.fileResults, it.compilationOutput) }
        } catch (e: IllegalArgumentException) {
            CodeTesterResult(compilationOutput = compilationOutputSerializer.deserialize(decoder))
        } catch (e: SerializationException) {
            CodeTesterResult(compilationOutput = compilationOutputSerializer.deserialize(decoder))
        }
    }

    override fun serialize(encoder: Encoder, value: CodeTesterResult) = correctResultSerializer.serialize(encoder, value.let { CorrectResult(it.fileResults, it.compilationOutput) })

}