package de.skuolari.punylisp

import de.skuolari.punylisp.values.*

object Evaluator {
    private val letSymbol = Symbol("let*")
    private val defSymbol = Symbol("def!")
    private val doSymbol = Symbol("do")
    private val ifSymbol = Symbol("if")
    private val fnSymbol = Symbol("fn*")

    operator fun invoke(ast: PunyValue, env: Environment): PunyValue =
            if (ast !is PunyList) eval(ast, env)
            else if (ast.isEmpty()) ast
            else ast[0].let { first ->
                when (first) {
                    letSymbol -> evalLet(ast, env)
                    defSymbol -> evalSet(ast, env)
                    doSymbol -> evalDo(ast, env)
                    ifSymbol -> evalIf(ast, env)
                    fnSymbol -> evalFn(ast, env)
                    else -> {
                        val list = evalList(ast, env)
                        val function = list[0] as Lambda
                        function(list.subList(1, list.size))
                    }
                }
            }

    fun evalLet(ast: PunyList, env: Environment): PunyValue {
        val inner = MapEnvironment(env)
        val bindings = this(ast[1], inner) as PunyList
        bindings.forEach { it ->
            val l = it as PunyList
            inner.set(l[0] as Symbol, this(l[1], inner))
        }
        return this(ast[2], inner)
    }

    fun evalSet(ast: PunyList, env: Environment): PunyValue =
            this(ast[2], env).apply {
                env.set(ast[1] as Symbol, this)
            }

    private fun evalDo(ast: PunyList, env: Environment): PunyValue =
            evalList(ast.subList(1, ast.size), env).last()

    fun evalIf(ast: PunyList, env: Environment) =
            when (this(ast[1], env)) {
                Nil, false.wrap() -> if (ast.size > 4) Nil else this(ast[3], env)
                else -> eval(ast[2], env)
            }

    fun evalFn(ast: PunyList, env: Environment): Lambda = Lambda { it ->
        this(ast[2], MapEnvironment(env, ast[1] as PunyList, it))
    }

    fun evalList(l: List<PunyValue>, env: Environment) = PunyList().apply {
        for (s in l) {
            val r: PunyValue = this@Evaluator(s, env)
            this.add(r)
        }
    }

    fun eval(ast: PunyValue, env: Environment): PunyValue =
            if (ast is PunyList) {
                evalList(ast, env)
            } else if (ast is Symbol) env.get(ast)
            else ast
}
