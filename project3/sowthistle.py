from plant import Plant
import random

class SowThistle(Plant):
    def __init__(self, x, y, world):
        super().__init__(0, x, y, world)
    
    def action(self):
        # SowThistle attempts to spread 3 times per turn
        for _ in range(3):
            if random.randint(0, 9) == 0:  # 10% chance each attempt
                nx = self.x + (random.randint(0, 2) - 1)
                ny = self.y + (random.randint(0, 2) - 1)
                
                if (0 <= nx < self.WIDTH and 0 <= ny < self.HEIGHT and 
                    self.world.grid[ny][nx] is None):
                    # Create a new plant of the same type
                    new_plant = SowThistle(nx, ny, self.world)
                    self.world.queue_organism(new_plant)
        
        self.age += 1
    
    def draw(self):
        return 's'
