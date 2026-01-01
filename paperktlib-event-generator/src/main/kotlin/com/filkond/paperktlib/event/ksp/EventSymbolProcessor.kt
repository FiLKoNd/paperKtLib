package com.filkond.paperktlib.event.ksp

import com.filkond.paperktlib.event.GenerateEvent
import com.google.devtools.ksp.getDeclaredProperties
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSValueParameter
import com.google.devtools.ksp.symbol.Modifier
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.WildcardTypeName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.writeTo

private val BUKKIT_EVENT = ClassName("org.bukkit.event", "Event")
private val BUKKIT_CANCELLABLE = ClassName("org.bukkit.event", "Cancellable")
private val BUKKIT_HANDLER_LIST = ClassName("org.bukkit.event", "HandlerList")

/**
 * Mapping from KSP Modifier to KotlinPoet KModifier.
 * Not all modifiers have a direct mapping.
 */
private fun Modifier.toKModifier(): KModifier? = when (this) {
    // Visibility modifiers
    Modifier.PUBLIC -> KModifier.PUBLIC
    Modifier.PRIVATE -> KModifier.PRIVATE
    Modifier.PROTECTED -> KModifier.PROTECTED
    Modifier.INTERNAL -> KModifier.INTERNAL

    // Inheritance modifiers
    Modifier.OPEN -> KModifier.OPEN
    Modifier.FINAL -> KModifier.FINAL
    Modifier.ABSTRACT -> KModifier.ABSTRACT
    Modifier.SEALED -> KModifier.SEALED

    // Function modifiers
    Modifier.OVERRIDE -> KModifier.OVERRIDE
    Modifier.SUSPEND -> KModifier.SUSPEND
    Modifier.INLINE -> KModifier.INLINE
    Modifier.EXTERNAL -> KModifier.EXTERNAL
    Modifier.OPERATOR -> KModifier.OPERATOR
    Modifier.INFIX -> KModifier.INFIX
    Modifier.TAILREC -> KModifier.TAILREC

    // Property modifiers
    Modifier.CONST -> KModifier.CONST
    Modifier.LATEINIT -> KModifier.LATEINIT

    // Class modifiers
    Modifier.DATA -> KModifier.DATA
    Modifier.INNER -> KModifier.INNER
    Modifier.ENUM -> KModifier.ENUM
    Modifier.ANNOTATION -> KModifier.ANNOTATION
    Modifier.VALUE -> KModifier.VALUE
    Modifier.FUN -> KModifier.FUN

    // Other modifiers
    Modifier.EXPECT -> KModifier.EXPECT
    Modifier.ACTUAL -> KModifier.ACTUAL
    Modifier.VARARG -> KModifier.VARARG
    Modifier.NOINLINE -> KModifier.NOINLINE
    Modifier.CROSSINLINE -> KModifier.CROSSINLINE

    // Type parameter modifiers
    Modifier.IN -> KModifier.IN
    Modifier.OUT -> KModifier.OUT
    Modifier.REIFIED -> KModifier.REIFIED

    // Modifiers without direct KotlinPoet equivalent
    Modifier.JAVA_DEFAULT,
    Modifier.JAVA_NATIVE,
    Modifier.JAVA_STATIC,
    Modifier.JAVA_STRICT,
    Modifier.JAVA_SYNCHRONIZED,
    Modifier.JAVA_TRANSIENT,
    Modifier.JAVA_VOLATILE -> null
}

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

    /**
     * Resolves the superclass for the generated event.
     * - If spec class extends another @GenerateEvent spec, returns the generated event name
     * - If spec class extends a Bukkit Event directly, returns that class
     * - Otherwise returns org.bukkit.event.Event
     */
    private fun resolveSuperclass(specDecl: KSClassDeclaration): Pair<ClassName, List<KSValueParameter>> {
        val superTypes = specDecl.superTypes.toList()

        for (superTypeRef in superTypes) {
            val superType = superTypeRef.resolve()
            val superDecl = superType.declaration as? KSClassDeclaration ?: continue

            // Skip interfaces
            if (superDecl.classKind != ClassKind.CLASS) continue

            val superQualifiedName = superDecl.qualifiedName?.asString() ?: continue

            // Check if superclass has @GenerateEvent annotation (spec-to-spec inheritance)
            val hasGenerateEventAnnotation = superDecl.annotations.any {
                it.annotationType.resolve().declaration.qualifiedName?.asString() == GenerateEvent::class.qualifiedName
            }

            if (hasGenerateEventAnnotation) {
                // Get the generated event name for the parent spec
                val parentAnnotation = superDecl.annotations.first {
                    it.annotationType.resolve().declaration.qualifiedName?.asString() == GenerateEvent::class.qualifiedName
                }
                val parentExplicitName = parentAnnotation.arguments.firstOrNull { it.name?.asString() == "name" }?.value as? String ?: ""
                val parentSpecName = superDecl.simpleName.asString()
                val parentEventName = parentExplicitName.takeIf { it.isNotBlank() }
                    ?: parentSpecName.removeSuffix("Spec")

                val parentPkg = superDecl.packageName.asString()
                val parentParams = superDecl.primaryConstructor?.parameters?.toList() ?: emptyList()

                return ClassName(parentPkg, parentEventName) to parentParams
            }

            // Check if superclass is or extends org.bukkit.event.Event
            if (superQualifiedName == "org.bukkit.event.Event" || isSubclassOfBukkitEvent(superDecl)) {
                return superDecl.toClassName() to emptyList()
            }
        }

        return BUKKIT_EVENT to emptyList()
    }

    /**
     * Checks if a class is a subclass of org.bukkit.event.Event
     */
    private fun isSubclassOfBukkitEvent(classDecl: KSClassDeclaration): Boolean {
        val visited = mutableSetOf<String>()
        var current: KSClassDeclaration? = classDecl

        while (current != null) {
            val qualifiedName = current.qualifiedName?.asString() ?: break
            if (qualifiedName in visited) break
            visited.add(qualifiedName)

            if (qualifiedName == "org.bukkit.event.Event") return true

            // Get superclass
            val superTypes = current.superTypes.toList()
            current = null
            for (superTypeRef in superTypes) {
                val superDecl = superTypeRef.resolve().declaration as? KSClassDeclaration ?: continue
                if (superDecl.classKind == ClassKind.CLASS) {
                    current = superDecl
                    break
                }
            }
        }
        return false
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

        // Resolve superclass and get parent parameters
        val (superclass, parentParams) = resolveSuperclass(specDecl)
        val parentParamNames = parentParams.map { it.name?.asString() }.toSet()

        val ctorParams = primaryCtor.parameters

        // Get class modifiers (visibility, open, abstract, etc.)
        val classModifiers = specDecl.modifiers
            .mapNotNull { it.toKModifier() }
            .filter { it in listOf(KModifier.PUBLIC, KModifier.INTERNAL, KModifier.OPEN, KModifier.ABSTRACT, KModifier.SEALED) }
            .toMutableSet()

        // Ensure at least PUBLIC visibility
        if (classModifiers.none { it in listOf(KModifier.PUBLIC, KModifier.INTERNAL, KModifier.PRIVATE) }) {
            classModifiers.add(KModifier.PUBLIC)
        }

        // Get properties from spec class to extract their modifiers
        val specProperties = specDecl.getDeclaredProperties().associateBy { it.simpleName.asString() }

        // Build constructor with all parameters (own + parent's for super call)
        val ctorFun = FunSpec.constructorBuilder().apply {
            ctorParams.forEach { p ->
                val name = p.name?.asString()
                if (name == null) {
                    logger.error("@GenerateEvent: constructor parameter has no name.", p)
                    return@forEach
                }
                val type = p.type.resolve().toTypeName()
                val parameter = ParameterSpec.builder(name, type)

                // Add vararg modifier if present
                if (p.isVararg) {
                    parameter.addModifiers(KModifier.VARARG)
                }

                // Add crossinline modifier if present
                if (p.isCrossInline) {
                    parameter.addModifiers(KModifier.CROSSINLINE)
                }

                // Add noinline modifier if present
                if (p.isNoInline) {
                    parameter.addModifiers(KModifier.NOINLINE)
                }

                addParameter(parameter.build())
            }
        }.build()

        val eventTypeBuilder = TypeSpec.classBuilder(generatedName)
            .addModifiers(classModifiers)
            .primaryConstructor(ctorFun)

        // Set superclass with super() call if needed
        if (parentParams.isNotEmpty()) {
            // Build super call with parent parameters
            val superCallArgs = parentParams.mapNotNull { it.name?.asString() }.joinToString(", ")
            eventTypeBuilder.superclass(superclass)
            eventTypeBuilder.addSuperclassConstructorParameter(superCallArgs)
        } else {
            eventTypeBuilder.superclass(superclass)
        }

        // Add properties for own parameters (not inherited from parent)
        ctorParams.forEach { p ->
            val name = p.name!!.asString()

            // Skip parameters that come from parent - they are already inherited
            if (name in parentParamNames) return@forEach

            val resolvedType = p.type.resolve()
            val baseType = resolvedType.toTypeName()

            // For vararg parameters, the property type should be Array<out T>
            val type = if (p.isVararg) {
                ClassName("kotlin", "Array").parameterizedBy(WildcardTypeName.producerOf(baseType))
            } else {
                baseType
            }

            val isMutable = p.isVar

            // Get property modifiers from spec class (for visibility, etc.)
            val specProperty = specProperties[name]
            val propertyModifiers = specProperty?.modifiers
                ?.mapNotNull { it.toKModifier() }
                ?.filter { it in listOf(KModifier.PUBLIC, KModifier.INTERNAL, KModifier.PROTECTED, KModifier.PRIVATE, KModifier.OPEN) }
                ?.toMutableSet()
                ?: mutableSetOf()

            // Default to PUBLIC if no visibility modifier
            if (propertyModifiers.none { it in listOf(KModifier.PUBLIC, KModifier.INTERNAL, KModifier.PROTECTED, KModifier.PRIVATE) }) {
                propertyModifiers.add(KModifier.PUBLIC)
            }

            val propBuilder = PropertySpec.builder(name, type)
                .initializer(name)
                .addModifiers(propertyModifiers)

            if (isMutable) {
                propBuilder.mutable(true)
            }

            eventTypeBuilder.addProperty(propBuilder.build())
        }

        // Always add handlers for all events - each event type needs its own HandlerList
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

