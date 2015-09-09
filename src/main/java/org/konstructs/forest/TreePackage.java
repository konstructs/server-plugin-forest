package org.konstructs.forest;

import konstructs.api.Position;

public class TreePackage {

    Position position;
    String message;
    String production;
    int generation;

    public TreePackage(String message, Position position, String production, int generation) {
        this.position = position;
        this.message = message;
        this.production = production;
        this.generation = generation;
    }

    public TreePackage(String message, Position position) {
        this.position = position;
        this.message = message;
    }

    public Position pos() {
        return position;
    }

    public String getProduction() {
        return production;
    }

    public int getGeneration() {
        return generation;
    }

    public boolean is(String str) {
        return str.equals(message);
    }
}
