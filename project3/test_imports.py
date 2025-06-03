print("Testing imports...")

try:
    from organism import Organism
    from world import World
    from animal import Animal
    from plant import Plant
    from wolf import Wolf
    from sheep import Sheep
    from fox import Fox
    from turtle import Turtle
    from antelope import Antelope
    from grass import Grass
    from sowthistle import SowThistle
    from guarana import Guarana
    from belladonna import Belladonna
    from hogweed import Hogweed
    from human import Human
    # Don't try to import world_frame here since it needs tkinter
    
    print("All modules imported successfully!")
except ImportError as e:
    print(f"Import error: {e}")
