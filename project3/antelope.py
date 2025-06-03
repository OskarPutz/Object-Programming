from animal import Animal
import random

class Antelope(Animal):
    def __init__(self, x, y, world):
        super().__init__(4, 4, x, y, world)
    
    def action(self):
        # Antelope moves by 2 cells at once
        direction = random.randint(0, 3)
        nx, ny = self.x, self.y
        
        if direction == 0 and self.y > 1:  # Up
            ny -= 2
        elif direction == 1 and self.y < self.HEIGHT - 2:  # Down
            ny += 2
        elif direction == 2 and self.x > 1:  # Left
            nx -= 2
        elif direction == 3 and self.x < self.WIDTH - 2:  # Right
            nx += 2
        
        if self.world.grid[ny][nx] is not None:
            self.collision(self.world.grid[ny][nx])
            
        if self.alive and self.move:
            self.world.grid[self.y][self.x] = None
            self.x, self.y = nx, ny
            self.world.grid[self.y][self.x] = self
        
        self.age += 1
        self.move = True
    
    def draw(self):
        return 'A'
    
    def clone(self, x, y):
        return Antelope(x, y, self.world)
