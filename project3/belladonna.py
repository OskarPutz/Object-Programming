from plant import Plant

class Belladonna(Plant):
    def __init__(self, x, y, world):
        super().__init__(99, x, y, world)
    
    def collision(self, other):
        # Belladonna kills any animal that eats it
        other.alive = False
        self.world.grid[other.y][other.x] = None
        self.alive = False
        self.world.last_collision_message = f"{other.draw()} died by eating Belladonna!"
    
    def draw(self):
        return 'b'
