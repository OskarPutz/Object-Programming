import tkinter as tk
from tkinter import filedialog, messagebox
import pickle
import random
from world import World
from wolf import Wolf
from sheep import Sheep
from fox import Fox
from turtler import Turtler
from antelope import Antelope
from grass import Grass
from sowthistle import SowThistle
from guarana import Guarana
from belladonna import Belladonna
from hogweed import Hogweed
from human import Human
from cybersheep import CyberSheep

class WorldFrame(tk.Tk):
    WIDTH = 40
    HEIGHT = 20
    CELL_SIZE = 30
    
    def __init__(self):
        super().__init__()
        self.title("World Simulation - Oskar Putz")
        self.protocol("WM_DELETE_WINDOW", self.on_close)
        
        self.world = World()
        self.initialize_world()
        
        # Main frame
        main_frame = tk.Frame(self)
        main_frame.pack(fill=tk.BOTH, expand=True)
        
        # Control panel
        control_panel = tk.Frame(main_frame)
        control_panel.pack(side=tk.TOP, fill=tk.X)
        
        up_button = tk.Button(control_panel, text="Up", command=lambda: self.move_human(0, -1))
        down_button = tk.Button(control_panel, text="Down", command=lambda: self.move_human(0, 1))
        left_button = tk.Button(control_panel, text="Left", command=lambda: self.move_human(-1, 0))
        right_button = tk.Button(control_panel, text="Right", command=lambda: self.move_human(1, 0))
        ability_button = tk.Button(control_panel, text="Activate Ability", command=self.activate_human_ability)
        next_turn_button = tk.Button(control_panel, text="Next Turn", command=self.next_turn)
        save_button = tk.Button(control_panel, text="Save", command=self.save_world)
        load_button = tk.Button(control_panel, text="Load", command=self.load_world)
        
        up_button.pack(side=tk.LEFT, padx=5, pady=5)
        down_button.pack(side=tk.LEFT, padx=5, pady=5)
        left_button.pack(side=tk.LEFT, padx=5, pady=5)
        right_button.pack(side=tk.LEFT, padx=5, pady=5)
        ability_button.pack(side=tk.LEFT, padx=5, pady=5)
        next_turn_button.pack(side=tk.LEFT, padx=5, pady=5)
        save_button.pack(side=tk.LEFT, padx=5, pady=5)
        load_button.pack(side=tk.LEFT, padx=5, pady=5)
        
        # Canvas for world display
        self.canvas = tk.Canvas(main_frame, width=self.WIDTH * self.CELL_SIZE, 
                              height=self.HEIGHT * self.CELL_SIZE, bg="white")
        self.canvas.pack(side=tk.TOP, fill=tk.BOTH, expand=True)
        
        # Status bar
        status_panel = tk.Frame(main_frame)
        status_panel.pack(side=tk.BOTTOM, fill=tk.X)
        
        self.human_status_label = tk.Label(status_panel, text="Ability status: OFF")
        self.strength_label = tk.Label(status_panel, text=f"Current strength: {self.world.human.strength}")
        self.status_label = tk.Label(status_panel, text="No collisions yet.")
        
        self.human_status_label.pack(side=tk.TOP, anchor=tk.W, padx=5)
        self.strength_label.pack(side=tk.TOP, anchor=tk.W, padx=5)
        self.status_label.pack(side=tk.TOP, anchor=tk.W, padx=5)
        
        # Key bindings
        self.bind("<Up>", lambda e: self.move_human(0, -1))
        self.bind("<Down>", lambda e: self.move_human(0, 1))
        self.bind("<Left>", lambda e: self.move_human(-1, 0))
        self.bind("<Right>", lambda e: self.move_human(1, 0))
        self.bind("p", lambda e: self.activate_human_ability())
        self.bind("<space>", lambda e: self.next_turn())
        self.bind("q", lambda e: self.on_close())
        
        # Draw the initial world
        self.draw_world()
        
        # Set window size and position
        window_width = self.WIDTH * self.CELL_SIZE + 16
        window_height = self.HEIGHT * self.CELL_SIZE + 150
        screen_width = self.winfo_screenwidth()
        screen_height = self.winfo_screenheight()
        center_x = int(screen_width/2 - window_width/2)
        center_y = int(screen_height/2 - window_height/2)
        self.geometry(f'{window_width}x{window_height}+{center_x}+{center_y}')
    
    def initialize_world(self):
        # Add human
        human = Human(0, 0, self.world)
        self.world.human = human
        self.world.add_organism(human)
        
        # Add animals
        self.add_organisms(Wolf, 2)
        self.add_organisms(Sheep, 4)
        self.add_organisms(Fox, 2)
        self.add_organisms(Turtler, 2)
        self.add_organisms(Antelope, 2)
        self.add_organisms(CyberSheep, 2)
        
        # Add plants
        self.add_organisms(Grass, 4)
        self.add_organisms(SowThistle, 2)
        self.add_organisms(Guarana, 2)
        self.add_organisms(Belladonna, 2)
        self.add_organisms(Hogweed, 1)
    
    def add_organisms(self, organism_class, count):
        for _ in range(count):
            while True:
                x = random.randint(0, self.WIDTH - 1)
                y = random.randint(0, self.HEIGHT - 1)
                if self.world.grid[y][x] is None:
                    self.world.add_organism(organism_class(x, y, self.world))
                    break
    
    def draw_world(self):
        self.canvas.delete("all")
        
        # Draw grid lines
        for i in range(self.WIDTH + 1):
            self.canvas.create_line(i * self.CELL_SIZE, 0, 
                                  i * self.CELL_SIZE, self.HEIGHT * self.CELL_SIZE, 
                                  fill="gray")
        
        for i in range(self.HEIGHT + 1):
            self.canvas.create_line(0, i * self.CELL_SIZE, 
                                  self.WIDTH * self.CELL_SIZE, i * self.CELL_SIZE, 
                                  fill="gray")
        
        # Draw organisms
        colors = {
            'H': "blue",     # Human
            'W': "gray",     # Wolf
            'S': "white",    # Sheep
            'C': "cyan",     # CyberSheep
            'F': "orange",   # Fox
            'T': "green",    # Turtle
            'A': "brown",    # Antelope
            'G': "light green",  # Grass
            's': "yellow",   # SowThistle
            'g': "red",      # Guarana
            'b': "purple",   # Belladonna
            'h': "black"     # Hogweed
        }
        
        for y in range(self.HEIGHT):
            for x in range(self.WIDTH):
                organism = self.world.grid[y][x]
                if organism is not None:
                    char = organism.draw()
                    color = colors.get(char, "black")
                    
                    # Draw filled rectangle
                    self.canvas.create_rectangle(
                        x * self.CELL_SIZE + 1, 
                        y * self.CELL_SIZE + 1, 
                        (x + 1) * self.CELL_SIZE - 1, 
                        (y + 1) * self.CELL_SIZE - 1, 
                        fill=color
                    )
                    
                    # Draw character
                    self.canvas.create_text(
                        x * self.CELL_SIZE + self.CELL_SIZE // 2,
                        y * self.CELL_SIZE + self.CELL_SIZE // 2,
                        text=char,
                        fill="white" if color in ["blue", "black", "purple"] else "black"
                    )
        
        # Update status labels
        ability_status = "ON" if self.world.human.ability_duration > 0 else "OFF"
        cooldown = self.world.human.ability_cooldown
        
        self.human_status_label.config(
            text=f"Ability status: {ability_status}" + 
                 (f" (Cooldown: {cooldown})" if cooldown > 0 else "")
        )
        self.strength_label.config(text=f"Current strength: {self.world.human.strength}")
        self.status_label.config(text=self.world.last_collision_message)
    
    def move_human(self, dx, dy):
        if self.world.human is not None and self.world.human.is_alive():
            self.world.human.set_direction(dx, dy)
            self.next_turn()
    
    def activate_human_ability(self):
        if self.world.human is not None and self.world.human.is_alive():
            self.world.human.activate_ability()
            self.draw_world()
    
    def next_turn(self):
        self.world.make_turn()
        self.draw_world()
    
    def save_world(self):
        file_path = filedialog.asksaveasfilename(
            defaultextension=".pkl",
            filetypes=[("Pickle files", "*.pkl"), ("All files", "*.*")]
        )
        if file_path:
            try:
                with open(file_path, 'wb') as file:
                    pickle.dump(self.world, file)
                self.status_label.config(text=f"World saved successfully to {file_path}")
            except Exception as e:
                messagebox.showerror("Save Error", f"Error saving world: {str(e)}")
    
    def load_world(self):
        file_path = filedialog.askopenfilename(
            defaultextension=".pkl",
            filetypes=[("Pickle files", "*.pkl"), ("All files", "*.*")]
        )
        if file_path:
            try:
                with open(file_path, 'rb') as file:
                    self.world = pickle.load(file)
                self.draw_world()
                self.status_label.config(text=f"World loaded successfully from {file_path}")
            except Exception as e:
                messagebox.showerror("Load Error", f"Error loading world: {str(e)}")
    
    def on_close(self):
        if messagebox.askokcancel("Quit", "Do you want to quit?"):
            self.destroy()
