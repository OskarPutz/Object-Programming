from animal import Animal
from sheep import Sheep
import math
import random

class CyberSheep(Animal):
    def __init__(self, x, y, world):
        super().__init__(11, 4, x, y, world)
    
    def action(self):
        # Find closest hogweed
        closest_hogweed = None
        min_distance = float('inf')
        
        for y in range(self.HEIGHT):
            for x in range(self.WIDTH):
                organism = self.world.grid[y][x]
                if organism is not None and organism.draw() == 'h':
                    distance = math.sqrt((self.x - x)**2 + (self.y - y)**2)
                    if distance < min_distance:
                        min_distance = distance
                        closest_hogweed = organism
        
        # If hogweed found, move towards it
        if closest_hogweed is not None:
            nx, ny = self.x, self.y
            
            # Decide whether to move horizontally or vertically
            if abs(self.x - closest_hogweed.x) > abs(self.y - closest_hogweed.y):
                # Move horizontally
                if self.x < closest_hogweed.x:
                    nx += 1
                elif self.x > closest_hogweed.x:
                    nx -= 1
            else:
                # Move vertically
                if self.y < closest_hogweed.y:
                    ny += 1
                elif self.y > closest_hogweed.y:
                    ny -= 1
            
            # Check if the target cell has an organism
            if self.world.grid[ny][nx] is not None:
                if self.world.grid[ny][nx].draw() == 'h':
                    # Special handling for hogweed - CyberSheep eats it without dying
                    hogweed = self.world.grid[ny][nx]
                    self.world.last_collision_message = f"{self.draw()} ate Hogweed at ({hogweed.x}, {hogweed.y})"
                    hogweed.alive = False
                    self.world.grid[ny][nx] = None
                    
                    # Move to the hogweed's position
                    self.world.grid[self.y][self.x] = None
                    self.x, self.y = nx, ny
                    self.world.grid[self.y][self.x] = self
                else:
                    # Handle collisions with other organisms
                    self.collision(self.world.grid[ny][nx])
            
            # Move if still alive and allowed to move (and didn't already move to eat hogweed)
            elif self.alive and self.move:
                self.world.grid[self.y][self.x] = None
                self.x, self.y = nx, ny
                self.world.grid[self.y][self.x] = self
            
            self.age += 1
            self.move = True
        else:
            # If no hogweed, behave like a normal sheep
            direction = random.randint(0, 3)
            nx, ny = self.x, self.y
            
            if direction == 0 and self.y > 0:
                ny -= 1
            elif direction == 1 and self.y < self.HEIGHT - 1:
                ny += 1
            elif direction == 2 and self.x > 0:
                nx -= 1
            elif direction == 3 and self.x < self.WIDTH - 1:
                nx += 1
            
            # Check if the target cell has an organism
            if self.world.grid[ny][nx] is not None:
                self.collision(self.world.grid[ny][nx])
            
            elif self.alive and self.move:
                self.world.grid[self.y][self.x] = None
                self.x, self.y = nx, ny
                self.world.grid[self.y][self.x] = self
            
            self.age += 1
            self.move = True
    
    
    def draw(self):
        return 'C'
    
    def clone(self, x, y):
        return CyberSheep(x, y, self.world)