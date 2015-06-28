# server-plugin-forest
Manage trees in the world, grow them and make new ones.

This plugin listens for block placements and generates a small tree if a
"sapling block" was placed. It will generate the tree with a L-system based
algorithm, the current generations data is then saved and a random timer is
registered.

After the random amount of time one of these things happens:

* The tree grows, an additional generation is generated and the tree is once
  again put back on a random timer.
* The tree spawns one or more new trees and the tree is removed from the 
  database (the tree has stopped growing).
