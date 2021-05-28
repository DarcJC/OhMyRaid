package pro.darc.raid.utils

import br.com.devsrsouza.kotlinbukkitapi.architecture.KotlinPlugin
import org.yaml.snakeyaml.Yaml
import pro.darc.raid.OhMyRaid
import java.io.*
import java.lang.IllegalArgumentException
import java.util.*
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

/**
 * @param source yml file path in `jar`
 * @param
 */
fun KotlinPlugin.mergeYAML(
    source: String = "config.yml",
) {
    val target = File(this.dataFolder, source)
    if (!target.exists()) {
        OhMyRaid.plugin.saveResource(source, false)
        return
    }
    if (!target.isFile) throw IllegalArgumentException("${target.absolutePath} is not a file!")
    val yaml = Yaml()
    val srcYamlStream = javaClass.classLoader.getResourceAsStream(source)
    val targetStream = FileInputStream(target)
    val fullYamlStream = SequenceInputStream(Collections.enumeration(listOf(targetStream, "\n".byteInputStream(), srcYamlStream)))
    val mergedYamlObj: Map<String, JvmType.Object> = yaml.load(fullYamlStream)
    val writer = StringWriter()
    yaml.dump(mergedYamlObj, writer)
    target.deleteOnExit()
    target.writeText(writer.toString())
}
