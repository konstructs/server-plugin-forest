package org.konstructs.forest;

import konstructs.api.BlockTypeId;

public class ForestConfig {
    private final BlockTypeId wood;
    private final BlockTypeId leaves;
    private final BlockTypeId thinLeaves;
    private final BlockTypeId sapling;
    private final BlockTypeId growsOn;
    private final int seedHeightDifference;
    private final int maxSeedHeight;
    private final int minSeedHeight;
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
    private final int randomGrowth;

    ForestConfig(String wood,
                 String leaves,
                 String thinLeaves,
                 String sapling,
                 String growsOn,
                 int seedHeightDifference,
                 int maxSeedHeight,
                 int minSeedHeight,
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
                 int randomGrowth) {
        this.wood = BlockTypeId.fromString(wood);
        this.leaves = BlockTypeId.fromString(leaves);
        this.thinLeaves = BlockTypeId.fromString(thinLeaves);
        this.sapling = BlockTypeId.fromString(sapling);
        this.growsOn = BlockTypeId.fromString(growsOn);
        this.seedHeightDifference = seedHeightDifference;
        this.maxSeedHeight = maxSeedHeight;
        this.minSeedHeight = minSeedHeight;
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
    public int getSeedHeightDifference() {
        return seedHeightDifference;
    }
    public int getMaxSeedHeight() {
        return maxSeedHeight;
    }
    public int getMinSeedHeight() {
        return minSeedHeight;
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
    public int getRandomGrowth() {
        return randomGrowth;
    }

}
