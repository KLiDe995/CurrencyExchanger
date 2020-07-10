package ru.ivglv.currencyexchanger

fun String.toFloatSafe() =
    if(this != "") this.replace(",", ".").toFloat()
    else 0f