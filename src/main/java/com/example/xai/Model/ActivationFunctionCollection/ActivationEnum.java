package com.example.xai.Model.ActivationFunctionCollection;

/**
 * Enum für die verschiedenen Aktivierungsfunktionen. Alle AKtivierungsfunktionen für NEAT und HyperNEAT sind hier aufgelistet.
 * Die Logik für die Aktivierungsfunktionen ist in der Klasse ActivationFunctions zu finden.
 * 
 * @see ActivationFunctions
 * @version 1.0
 * @since 1.0
 * @autor Majid Moussa Adoyi
 * @date 01.07.2024
 * 
 * 
 */
public enum ActivationEnum {
    SIGMOID,
    TANH,
    GAUSSIAN,
    COS,
    SIN,
    IDENTITIY
}
