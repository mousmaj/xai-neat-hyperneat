package com.example.xai.Model.Genotype;

public enum Layer {

    INPUT, HIDDEN, OUTPUT;

    public static Layer getLayer(String layer) {
        switch (layer) {
            case "INPUT":
                return INPUT;
            case "HIDDEN":
                return HIDDEN;
            case "OUTPUT":
                return OUTPUT;
            default:
                return null;
        }
    }    
    
}
