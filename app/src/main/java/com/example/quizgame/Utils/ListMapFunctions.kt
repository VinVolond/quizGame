package com.example.quizgame.Utils

import kotlin.random.Random

class ListMapFunctions {
    private val letterProbabilities = makeTextToMap(NeededVariables().russianLetterProbabilityString)
    private fun makeTextToMap(text: String): MutableMap<String, Double>{

        // Split the text into lines
        val lines = text.trim().split('\n')


        // Initialize an empty mutable map to store the letter-to-percentage mapping
        val letterToPercentage = mutableMapOf<String, Double>()

        // Iterate through the lines and extract the letter and percentage
        for (line in lines) {
            val parts = line.split("-")
            println(parts.size)
            if (parts.size == 2) {
                val letter = parts[0].trim()
                val percentage = parts[1].trim()
                println(percentage.toDouble())
                letterToPercentage[letter] = percentage.toDouble()
            }
        }

        // Print the resulting map
        return letterToPercentage
    }
    fun weightedRandomLetter(): String? {

        println("letterProbabilities${letterProbabilities}")
        if (letterProbabilities.isEmpty()) return null

        val totalWeight = letterProbabilities.values.sum()
        println("TotalWeight${totalWeight}")
        var randomValue = Random.nextDouble(0.0, totalWeight)

        for ((letter, weight) in letterProbabilities) {
            if (randomValue < weight) {
                println("returned letter${letter}")
                return letter
            }
            randomValue -= weight
        }

        // In case of rounding errors, return the last letter
        return letterProbabilities.keys.last()
    }

}