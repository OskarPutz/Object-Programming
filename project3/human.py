from animal import Animal

class Human(Animal):
    def __init__(self, x, y, world):
        super().__init__(5, 4, x, y, world)
        self.dx = 0
        self.dy = 0
        self.ability_cooldown = 0
        self.ability_duration = 0
    
    def clone(self, x, y):
        return Human(x, y, self.world)
    
    def set_direction(self, dx, dy):
        self.dx = dx
        self.dy = dy
    
    def activate_ability(self):
        if self.ability_cooldown == 0:
            self.ability_duration = 5
            self.ability_cooldown = 10
            self.world.last_collision_message = "Ability activated!"
            self.strength += 5
    
    def action(self):
        nx, ny = self.x + self.dx, self.y + self.dy
        self.dx, self.dy = 0, 0
        
        if 0 <= nx < self.WIDTH and 0 <= ny < self.HEIGHT:
            if self.world.grid[ny][nx] is not None and (nx != self.x or ny != self.y):
                self.collision(self.world.grid[ny][nx])
            
            if self.alive:
                self.world.grid[self.y][self.x] = None
                self.x, self.y = nx, ny
                self.world.grid[self.y][self.x] = self
        
        if self.ability_cooldown > 0:
            self.ability_cooldown -= 1
            
        if self.ability_duration > 0:
            self.ability_duration -= 1
            self.strength -= 1
        
        self.age += 1
    
    def draw(self):
        return 'H'
