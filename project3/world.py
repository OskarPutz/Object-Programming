import pickle

class World:
    WIDTH = 40
    HEIGHT = 20
    
    def __init__(self):
        self.organisms = []
        self.new_organisms = []
        self.grid = [[None for _ in range(self.WIDTH)] for _ in range(self.HEIGHT)]
        self.last_collision_message = "No collisions yet."
        self.human = None
    
    def add_organism(self, org):
        if 0 <= org.y < self.HEIGHT and 0 <= org.x < self.WIDTH:
            if self.grid[org.y][org.x] is None:
                self.organisms.append(org)
                self.grid[org.y][org.x] = org
    
    def queue_organism(self, org):
        self.new_organisms.append(org)
    
    def make_turn(self):
        # Sort organisms by initiative and age
        self.organisms.sort(key=lambda x: (-x.initiative, -x.age))
        
        # Make each organism perform its action
        organisms_copy = self.organisms.copy()
        for org in organisms_copy:
            if org.is_alive():
                org.action()
        
        # Remove dead organisms
        self.organisms = [org for org in self.organisms if org.is_alive()]
        
        # Clear the grid
        self.grid = [[None for _ in range(self.WIDTH)] for _ in range(self.HEIGHT)]
        
        # Place living organisms back on the grid
        for org in self.organisms:
            if 0 <= org.y < self.HEIGHT and 0 <= org.x < self.WIDTH:
                self.grid[org.y][org.x] = org
        
        # Add new organisms
        for org in self.new_organisms:
            self.add_organism(org)
        self.new_organisms.clear()
