package de.skuolari.punylisp

import de.skuolari.punylisp.values.*

object Evaluator {
    private val letSymbol = Symbol("let*")
    private val defSymbol = Symbol("def!")
    private val doSymbol = Symbol("do")
    private val ifSymbol = Symbol("if")
    private val fnSymbol = Symbol("fn*")

    tailrec operator fun invoke(ast: PunyValue, env: Environment): PunyValue = when {
        ast !is PunyList -> eval(ast, env)
        ast.isEmpty() -> ast
        else -> {
            val first = ast[0]
            when (first) {
                letSymbol -> this(ast[2], evalLet(ast, env))
                defSymbol -> evalSet(ast, env)
                doSymbol -> {
                    evalList(ast.subList(1, ast.size - 1), env)
                    this(ast.last(), env)
                }
                ifSymbol -> this(evalIf(ast, env), env)
                fnSymbol -> evalFn(ast, env)
                else -> {
                    val list = evalList(ast, env)
                    val function = list[0]
                    val args = list.subList(1, list.size)
                    when (function) {
                        is Lambda -> function(args)
                        is Callable -> this(function.ast, MapEnvironment(function.env, function.parameters, args))
                        else -> throw IllegalStateException()
                    }
                }
            }
        }
    }


    fun evalLet(ast: PunyList, env: Environment): Environment {
        val inner = MapEnvironment(env)
        val bindings = this(ast[1], inner) as PunyList
        bindings.forEach { it ->
            val l = it as PunyList
            inner.set(l[0] as Symbol, this(l[1], inner))
        }
        return inner
    }

    fun evalSet(ast: PunyList, env: Environment): PunyValue =
            this(ast[2], env).apply {
                env.set(ast[1] as Symbol, this)
            }

    fun evalIf(ast: PunyList, env: Environment) =
            when (this(ast[1], env)) {
                Nil, false.wrap() -> if (ast.size != 4) SyntaxErrorException("Expected syntax: (if <condition> <then> <else>)") else ast[3]
                else -> ast[2]
            }

    fun evalFn(ast: PunyList, env: Environment) = Callable(ast[2], ast[1] as List<PunyValue>, env, Lambda { it ->
        this(ast[2], MapEnvironment(env, ast[1] as PunyList, it))
    })

    class Callable(val ast: PunyValue, val parameters: List<PunyValue>, val env: Environment, val lambda: Lambda) : PunyValue {
        override fun format(readably: Boolean) = lambda.format(readably)
    }

    fun evalList(l: List<PunyValue>, env: Environment) = PunyList().apply {
        for (s in l) {
            val r: PunyValue = this@Evaluator(s, env)
            this.add(r)
        }
    }

    fun eval(ast: PunyValue, env: Environment): PunyValue =
            when (ast) {
                is PunyList -> evalList(ast, env)
                is Symbol -> env.get(ast)
                else -> ast
            }
}
