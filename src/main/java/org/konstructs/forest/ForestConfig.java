package org.konstructs.forest;

import konstructs.api.BlockTypeId;

public class ForestConfig {
    private final BlockTypeId wood;
    private final BlockTypeId leaves;
    private final BlockTypeId thinLeaves;
    private final BlockTypeId sapling;
    private final BlockTypeId growsOn;
    private final BlockTypeId seedsOn;
    private final int seedHeightDifference;
    private final int maxGenerations;
    private final int minGenerations;
    private final int trunkRadi;
    private final int trunkHeight;
    private final int crownRadi;
    private final int crownHeight;
    private final String initialState;
    private final int minGrowthDelay;
    private final int randomGrowthDelay;
    private final int maxSeedsPerGeneration;
    private final int seedEveryGeneration;
    private final int randomGrowth;

    ForestConfig(String wood,
                 String leaves,
                 String thinLeaves,
                 String sapling,
                 String growsOn,
                 String seedsOn,
                 int seedHeightDifference,
                 int maxGenerations,
                 int minGenerations,
                 int trunkRadi,
                 int trunkHeight,
                 int crownRadi,
                 int crownHeight,
                 String initialState,
                 int minGrowthDelay,
                 int randomGrowthDelay,
                 int maxSeedsPerGeneration,
                 int seedEveryGeneration,
                 int randomGrowth) {
        this.wood = BlockTypeId.fromString(wood);
        this.leaves = BlockTypeId.fromString(leaves);
        this.thinLeaves = BlockTypeId.fromString(thinLeaves);
        this.sapling = BlockTypeId.fromString(sapling);
        this.growsOn = BlockTypeId.fromString(growsOn);
        this.seedsOn = BlockTypeId.fromString(seedsOn);
        this.seedHeightDifference = seedHeightDifference;
        this.maxGenerations = maxGenerations;
        this.minGenerations = minGenerations;
        this.trunkRadi = trunkRadi;
        this.trunkHeight = trunkHeight;
        this.crownRadi = crownRadi;
        this.crownHeight = crownHeight;
        this.initialState = initialState;
        this.minGrowthDelay = minGrowthDelay;
        this.randomGrowthDelay = randomGrowthDelay;
        this.maxSeedsPerGeneration = maxSeedsPerGeneration;
        this.seedEveryGeneration = seedEveryGeneration;
        this.randomGrowth = randomGrowth;
    }

    public BlockTypeId getWood() {
        return wood;
    }
    public BlockTypeId getLeaves() {
        return leaves;
    }
    public BlockTypeId getThinLeaves() {
        return thinLeaves;
    }
    public BlockTypeId getSapling() {
        return sapling;
    }
    public BlockTypeId getGrowsOn() {
        return growsOn;
    }
    public BlockTypeId getSeedsOn() {
        return seedsOn;
    }
    public int getSeedHeightDifference() {
        return seedHeightDifference;
    }
    public int getMaxGenerations() {
        return maxGenerations;
    }
    public int getMinGenerations() {
        return minGenerations;
    }
    public int getTrunkRadi() {
        return trunkRadi;
    }
    public int getTrunkHeight() {
        return trunkHeight;
    }
    public int getCrownRadi() {
        return crownRadi;
    }
    public int getCrownHeight() {
        return crownHeight;
    }
    public String getInitialState() {
        return initialState;
    }
    public int getMinGrowthDelay() {
        return minGrowthDelay;
    }
    public int getRandomGrowthDelay() {
        return randomGrowthDelay;
    }
    public int getMaxSeedsPerGeneration() {
        return maxSeedsPerGeneration;
    }
    public int getSeedEveryGeneration() {
        return seedEveryGeneration;
    }
    public int getRandomGrowth() {
        return randomGrowth;
    }

}
