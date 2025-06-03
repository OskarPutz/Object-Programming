from abc import ABC, abstractmethod
import pickle

class Organism(ABC):
    def __init__(self, strength, initiative, x, y, world):
        self.strength = strength
        self.initiative = initiative 
        self.x = x
        self.y = y
        self.world = world
        self.age = 0
        self.alive = True
    
    @abstractmethod
    def action(self):
        pass
    
    @abstractmethod
    def collision(self, other):
        pass
    
    @abstractmethod
    def draw(self):
        pass
    
    def is_alive(self):
        return self.alive
