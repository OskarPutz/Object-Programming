from plant import Plant
from animal import Animal

class Hogweed(Plant):
    def __init__(self, x, y, world):
        super().__init__(10, x, y, world)
    
    def action(self):
        # Kill all animals in adjacent cells
        directions = [(0,0), (0,1), (1,0), (0,-1), (-1,0)]
        
        for dx, dy in directions:
            nx, ny = self.x + dx, self.y + dy
            if 0 <= nx < self.WIDTH and 0 <= ny < self.HEIGHT:
                organism = self.world.grid[ny][nx]
                if organism is not None and isinstance(organism, Animal) and organism.draw() != 'C':
                    organism.alive = False
                    self.world.grid[ny][nx] = None
                    self.world.last_collision_message = f"Hogweed killed {organism.draw()} at ({nx}, {ny})"
        
        super().action()
    
    def draw(self):
        return 'h'
