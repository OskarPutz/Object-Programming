from organism import Organism
import random
from abc import abstractmethod

class Animal(Organism):
    WIDTH = 40
    HEIGHT = 20
    
    def __init__(self, strength, initiative, x, y, world):
        super().__init__(strength, initiative, x, y, world)
        self.move = True
    
    @abstractmethod
    def clone(self, x, y):
        pass
    
    def action(self):
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
        
        if self.world.grid[ny][nx] is not None:
            self.collision(self.world.grid[ny][nx])
            
        if self.alive and self.move:
            self.world.grid[self.y][self.x] = None
            self.x, self.y = nx, ny
            self.world.grid[self.y][self.x] = self
        
        self.age += 1
        self.move = True
    
    def collision(self, other):
        # Reproduction case - same species
        if other.__class__ == self.__class__:
            self.move = False
            other.move = False
            
            # Possible movement directions
            dirs = [(0,1), (1,0), (0,-1), (-1,0), (1,1), (1,-1), (-1,1), (-1,-1)]
            random.shuffle(dirs)
            
            for dx, dy in dirs:
                nx, ny = self.x + dx, self.y + dy
                if (0 <= nx < self.WIDTH and 0 <= ny < self.HEIGHT and 
                    self.world.grid[ny][nx] is None):
                    child = self.clone(nx, ny)
                    child.move = False
                    self.world.queue_organism(child)
                    self.world.last_collision_message = f"{self.draw()} reproduced at ({nx}, {ny})"
                    break
            return
        
        # Deadly Hogweed case
        if other.draw() == 'h':
            self.world.last_collision_message = f"{self.draw()} was killed by Hogweed at ({other.x}, {other.y})"
            self.alive = False
            self.world.grid[self.y][self.x] = None
            return
        
        # Antelope escape case
        if other.draw() == 'A':
            escape_chance = random.randint(0, 1)
            if escape_chance == 0:
                dirs = [(0,1), (1,0), (0,-1), (-1,0)]
                random.shuffle(dirs)
                
                for dx, dy in dirs:
                    nx, ny = other.x + dx, other.y + dy
                    if (0 <= nx < self.WIDTH and 0 <= ny < self.HEIGHT and 
                        self.world.grid[ny][nx] is None):
                        self.world.grid[other.y][other.x] = None
                        other.x, other.y = nx, ny
                        self.world.grid[ny][nx] = other
                        self.world.last_collision_message = f"{other.draw()} escaped from {self.draw()} at ({other.x}, {other.y})"
                        return
        
        # Turtle defense case
        if other.draw() == 'T':
            if self.strength < 5:
                self.move = False
                self.world.last_collision_message = f"{self.draw()} was deflected by Turtle at ({other.x}, {other.y})"
                return
        
        # Guarana case - strength boost
        if other.draw() == 'g':
            self.strength += 3
            self.world.last_collision_message = f"{self.draw()} ate Guarana and gained 3 strength!"
            other.alive = False
            self.world.grid[other.y][other.x] = None
            return
        
        # Regular combat case
        if other.strength > self.strength:
            self.world.last_collision_message = f"{other.draw()} killed {self.draw()} at ({self.x}, {self.y})"
            self.alive = False
            self.world.grid[self.y][self.x] = None
        else:
            self.world.last_collision_message = f"{self.draw()} killed {other.draw()} at ({other.x}, {other.y})"
            other.alive = False
            self.world.grid[other.y][other.x] = None
    
    def draw(self):
        return 'A'
