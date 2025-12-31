package com.filkond.paperktlib.event.ksp

import com.filkond.paperktlib.event.GenerateEvent
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.writeTo

private val BUKKIT_EVENT = ClassName("org.bukkit.event", "Event")
private val BUKKIT_CANCELLABLE = ClassName("org.bukkit.event", "Cancellable")
private val BUKKIT_HANDLER_LIST = ClassName("org.bukkit.event", "HandlerList")

class EventSymbolProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) : SymbolProcessor {

    constructor(env: com.google.devtools.ksp.processing.SymbolProcessorEnvironment) : this(
        codeGenerator = env.codeGenerator,
        logger = env.logger,
    )

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation(GenerateEvent::class.qualifiedName!!)
        val invalid = symbols.filterNot { it.validate() }.toList()

        symbols.filter { it is KSClassDeclaration && it.validate() }
            .map { it as KSClassDeclaration }
            .forEach { generateEvent(it) }

        return invalid
    }

    private fun generateEvent(specDecl: KSClassDeclaration) {
        if (specDecl.classKind != ClassKind.CLASS) {
            logger.error("@GenerateEvent is only for class", specDecl)
            return
        }

        val pkg = specDecl.packageName.asString()
        val specName = specDecl.simpleName.asString()

        val annotation = specDecl.annotations.first {
            it.annotationType.resolve().declaration.qualifiedName?.asString() == GenerateEvent::class.qualifiedName
        }

        val cancellable =
            annotation.arguments.firstOrNull { it.name?.asString() == "cancellable" }?.value as? Boolean ?: false
        val explicitName = annotation.arguments.firstOrNull { it.name?.asString() == "name" }?.value as? String ?: ""

        val generatedName = explicitName.takeIf { it.isNotBlank() }
            ?: specName.removeSuffix("Spec")

        if (generatedName == specName) {
            logger.warn(
                "@GenerateEvent: the spec class name is the same as the generated Event name ($generatedName). This may cause a conflict.",
                specDecl
            )
        }

        val primaryCtor = specDecl.primaryConstructor
        if (primaryCtor == null) {
            logger.error(
                "@GenerateEvent: the spec class must have a primary constructor; its parameters will be used to generate Event fields.",
                specDecl
            )
            return
        }

        val ctorParams = primaryCtor.parameters
        val ctorFun = FunSpec.constructorBuilder().apply {
            ctorParams.forEach { p ->
                val name = p.name?.asString()
                if (name == null) {
                    logger.error("@GenerateEvent: constructor parameter has no name.", p)
                    return@forEach
                }
                val type = p.type.resolve().toClassName()
                addParameter(name, type)
            }
        }.build()

        val eventTypeBuilder = TypeSpec.classBuilder(generatedName)
            .superclass(BUKKIT_EVENT)
            .addModifiers(KModifier.PUBLIC)
            .primaryConstructor(ctorFun)

        ctorParams.forEach { p ->
            val name = p.name!!.asString()
            val type = p.type.resolve().toClassName()
            eventTypeBuilder.addProperty(
                PropertySpec.builder(name, type)
                    .initializer(name)
                    .addModifiers(KModifier.PUBLIC)
                    .build()
            )
        }

        eventTypeBuilder.addType(
            TypeSpec.companionObjectBuilder()
                .addProperty(
                    PropertySpec.builder("handlers", BUKKIT_HANDLER_LIST)
                        .addModifiers(KModifier.PRIVATE)
                        .initializer("%T()", BUKKIT_HANDLER_LIST)
                        .build()
                )
                .addFunction(
                    FunSpec.builder("getHandlerList")
                        .addAnnotation(JvmStatic::class)
                        .returns(BUKKIT_HANDLER_LIST)
                        .addStatement("return handlers")
                        .addModifiers(KModifier.PUBLIC)
                        .build()
                )
                .build()
        )

        eventTypeBuilder.addFunction(
            FunSpec.builder("getHandlers")
                .addModifiers(KModifier.OVERRIDE, KModifier.PUBLIC)
                .returns(BUKKIT_HANDLER_LIST)
                .addStatement("return getHandlerList()")
                .build()
        )

        if (cancellable) {
            eventTypeBuilder.addSuperinterface(BUKKIT_CANCELLABLE)
            eventTypeBuilder.addProperty(
                PropertySpec.builder("cancelled", Boolean::class.asClassName())
                    .mutable(true)
                    .addModifiers(KModifier.PRIVATE)
                    .initializer("false")
                    .build()
            )
            eventTypeBuilder.addFunction(
                FunSpec.builder("isCancelled")
                    .addModifiers(KModifier.OVERRIDE, KModifier.PUBLIC)
                    .returns(Boolean::class.asClassName())
                    .addStatement("return cancelled")
                    .build()
            )
            eventTypeBuilder.addFunction(
                FunSpec.builder("setCancelled")
                    .addModifiers(KModifier.OVERRIDE, KModifier.PUBLIC)
                    .addParameter("cancel", Boolean::class)
                    .addStatement("this.cancelled = cancel")
                    .build()
            )
        }

        val file = FileSpec.builder(pkg, generatedName)
            .addType(eventTypeBuilder.build())
            .build()

        file.writeTo(
            codeGenerator = codeGenerator,
            dependencies = com.google.devtools.ksp.processing.Dependencies(
                aggregating = false,
                sources = listOfNotNull(specDecl.containingFile).toTypedArray()
            )
        )
    }
}

