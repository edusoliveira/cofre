package io.edusoliveira.meudia.data.db

enum class BillCategory(val label: String) {
    MORADIA("Moradia"),
    TRANSPORTE("Transporte"),
    ASSINATURA("Assinatura"),
    EMPRESTIMO("Empréstimo"),
    DOACAO("Doação"),
    EDUCACAO("Educação"),
    CARTAO("Cartão"),
    VIAGEM("Viagem"),
    OUTRO("Outro"),
}

enum class BillType {
    MENSAL,
    ANUAL,
}
