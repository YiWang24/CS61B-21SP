# Project 3 Prep

**For tessellating hexagons, one of the hardest parts is figuring out where to place each hexagon/how to easily place hexagons on screen in an algorithmic way.
After looking at your own implementation, consider the implementation provided near the end of the lab.
How did your implementation differ from the given one? What lessons can be learned from it?**

Answer:My implementation of hexagon tessellation focused on computing the relative offsets for each hexagon based on its row and column index. I used helper methods to determine the starting position and width of each row. However, after reviewing the provided solution, I noticed that it used a more structured pattern for positioning hexagons, leveraging consistent vertical and diagonal offsets.

Key differences:

The provided solution may have been more efficient in determining hexagon positions by using a clear grid-based offset system.
My approach initially required manual adjustments to fine-tune positioning, while the provided solution likely used formula-based offsets that automatically adjusted.

-----

**Can you think of an analogy between the process of tessellating hexagons and randomly generating a world using rooms and hallways?
What is the hexagon and what is the tesselation on the Project 3 side?**

Answer:Tessellating hexagons is similar to generating a world using rooms and hallways because both involve placing multiple interconnected structures in a grid-like fashion while ensuring they fit together without gaps.

In the context of Project 3, the hexagon can be thought of as an individual room in a randomly generated world.
The tessellation process mirrors the placement of rooms and hallways, ensuring they connect properly without overlaps or unintended empty spaces.

-----
**If you were to start working on world generation, what kind of method would you think of writing first? 
Think back to the lab and the process used to eventually get to tessellating hexagons.**

Answer:If I were to start world generation, I would begin by writing a method to place individual rooms in a structured way, just like how we started by drawing a single hexagon before tackling tessellation.

-----
**What distinguishes a hallway from a room? How are they similar?**

Answer:
Differences:

A room is an enclosed space with a defined width and height, often with walls.
A hallway is a narrow passage that connects rooms and allows movement between them.
Similarities:

Both are physical structures in the generated world.
Both contribute to world connectivity, ensuring that all areas are accessible.
Both need to be placed without overlap and must fit within the constraints of the world.