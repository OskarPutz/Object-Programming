from plant import Plant

class Guarana(Plant):
    def __init__(self, x, y, world):
        super().__init__(0, x, y, world)
    
    def draw(self):
        return 'g'
