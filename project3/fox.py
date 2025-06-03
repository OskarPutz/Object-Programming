from animal import Animal
import random

class Fox(Animal):
    def __init__(self, x, y, world):
        super().__init__(3, 7, x, y, world)
    
    def action(self):
        # Fox won't move to a cell with a stronger organism
        available_moves = []
        directions = [(0,-1), (0,1), (-1,0), (1,0)]  # up, down, left, right
        
        for dx, dy in directions:
            nx, ny = self.x + dx, self.y + dy
            if (0 <= nx < self.WIDTH and 0 <= ny < self.HEIGHT):
                if (self.world.grid[ny][nx] is None or 
                    self.world.grid[ny][nx].strength <= self.strength):
                    available_moves.append((nx, ny))
        
        # If there are safe moves, choose one randomly
        if available_moves:
            nx, ny = random.choice(available_moves)
            
            if self.world.grid[ny][nx] is not None:
                self.collision(self.world.grid[ny][nx])
            
            if self.alive:
                self.world.grid[self.y][self.x] = None
                self.x, self.y = nx, ny
                self.world.grid[self.y][self.x] = self
        
        self.age += 1
    
    def draw(self):
        return 'F'
    
    def clone(self, x, y):
        return Fox(x, y, self.world)
