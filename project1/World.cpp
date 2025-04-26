// Simplified virtual world simulator framework in C++
#include <iostream>
#include <vector>
#include <algorithm>
#include <ctime>
#include <cstdlib>
#include <termios.h>
#include <unistd.h>

using namespace std;

// Cross-platform alternative for _getch()
char getch() {
    struct termios oldt, newt;
    char ch;
    tcgetattr(STDIN_FILENO, &oldt);
    newt = oldt;
    newt.c_lflag &= ~(ICANON | ECHO);
    tcsetattr(STDIN_FILENO, TCSANOW, &newt);
    ch = getchar();
    tcsetattr(STDIN_FILENO, TCSANOW, &oldt);
    return ch;
}

#define WIDTH 40
#define HEIGHT 20

class World;

class Organism {
public:
    int strength, initiative, age;
    int x, y;
    World* world;
    bool alive = true;

    Organism(int str, int init, int x, int y, World* w) : strength(str), initiative(init), x(x), y(y), world(w), age(0) {}
    virtual ~Organism() = default;

    virtual void action() = 0;
    virtual void collision(Organism* other) = 0;
    virtual char draw() = 0;
    bool isAlive() const { return alive; }
};

// Abstract base class for all animals
class Animal : public Organism {
public:
    Animal(int str, int init, int x, int y, World* w) : Organism(str, init, x, y, w) {}
    virtual ~Animal() = default;

    void action() override;
    void collision(Organism* other) override;
    char draw() override { return 'A'; } // Default representation for Animal
};

// Abstract base class for all plants
class Plant : public Organism {
public:
    Plant(int str, int x, int y, World* w) : Organism(str, 0, x, y, w) {}
    virtual ~Plant() = default;

    void action() override {}
    void collision(Organism* other) override { alive = false; }
    char draw() override { return 'P'; } // Default representation for Plant
};

class Human : public Animal {
    int abilityCooldown = 0;
    int abilityDuration = 0;
    int dx = 0, dy = 0;

public:
    Human(int x, int y, World* w) : Animal(5, 4, x, y, w) {}

    void setDirection(int _dx, int _dy) { dx = _dx; dy = _dy; }
    void activateAbility() {
        if (abilityCooldown == 0 && abilityDuration == 0) abilityDuration = 5;
    }

    void action() override;
    char draw() override { return 'H'; }
};

class World {
    public:
        std::vector<Organism*> organisms;
        Organism* grid[HEIGHT][WIDTH] = {nullptr};
        std::string lastCollisionMessage = "No collisions yet."; // Store the last collision message
    
        void addOrganism(Organism* org) {
            organisms.push_back(org);
            grid[org->y][org->x] = org;
        }
    
        void makeTurn();
        void drawWorld();
    };

void Animal::action() {
    int dir = rand() % 4;
    int nx = x, ny = y;
    if (dir == 0 && y > 0) ny--;
    else if (dir == 1 && y < HEIGHT - 1) ny++;
    else if (dir == 2 && x > 0) nx--;
    else if (dir == 3 && x < WIDTH - 1) nx++;

    if (world->grid[ny][nx]) collision(world->grid[ny][nx]);
    else {
        world->grid[y][x] = nullptr;
        x = nx; y = ny;
        world->grid[y][x] = this;
    }
    age++;
}
void Animal::collision(Organism* other) {
    if (typeid(*this) == typeid(*other)) return; // No reproduction logic for now

    if (other->strength > strength) {
        world->lastCollisionMessage = other->draw() + std::string(" killed ") + draw() + 
                                      " at (" + std::to_string(x) + ", " + std::to_string(y) + ")";
        alive = false;
        world->grid[y][x] = nullptr; // Remove this organism from the grid
    } else {
        world->lastCollisionMessage = draw() + std::string(" killed ") + other->draw() + 
                                      " at (" + std::to_string(other->x) + ", " + std::to_string(other->y) + ")";
        other->alive = false;
        world->grid[other->y][other->x] = nullptr; // Remove the other organism from the grid
    }
}

class Sheep : public Animal {
    public:
        Sheep(int x, int y, World* w) : Animal(4, 4, x, y, w) {} // Example stats: strength=3, initiative=2
        char draw() override { return 'S'; } // Representation for Sheep
    };

class Wolf : public Animal {
    public:
        Wolf(int x, int y, World* w) : Animal(9, 5, x, y, w) {} // Example stats: strength=3, initiative=2
        char draw() override { return 'W'; } // Representation for Wolf
    };

    class Fox : public Animal {
        public:
            Fox(int x, int y, World* w) : Animal(3, 7, x, y, w) {} // Example stats: strength=7, initiative=7
        
            char draw() override { return 'F'; } // Representation for Fox
        
            void action() override {
                int directions[4][2] = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}}; // UP, DOWN, LEFT, RIGHT
                std::random_shuffle(std::begin(directions), std::end(directions)); // Randomize movement order
        
                for (auto& dir : directions) {
                    int nx = x + dir[0];
                    int ny = y + dir[1];
        
                    if (nx >= 0 && ny >= 0 && nx < WIDTH && ny < HEIGHT) {
                        Organism* target = world->grid[ny][nx];
                        if (!target || target->strength <= strength) { // Move if cell is empty or weaker organism
                            if (target) collision(target); // Handle collision if necessary
                            if (alive) {
                                world->grid[y][x] = nullptr;
                                x = nx; y = ny;
                                world->grid[y][x] = this;
                            }
                            break; // Stop after moving
                        }
                    }
                }
                age++;
            }
        };

        class Turtle : public Animal {
            public:
                Turtle(int x, int y, World* w) : Animal(2, 1, x, y, w) {} // Example stats: strength=3, initiative=2
                char draw() override { return 'T'; } // Representation for Sheep
                void action() override {
                    int chance = rand() % 4;
                    if (chance == 0) {
                        Animal::action(); // Move with 25% chance
                    }
                    age++;
                }
                void collision(Organism* other) override {
                    if (other->strength < 5) {
                        // Reflect the attack: Move the attacker back to its previous position
                        world->grid[other->y][other->x] = nullptr; // Clear the attacker's current position
                        other->x = x; // Move attacker back to the Turtle's position
                        other->y = y;
                        world->grid[other->y][other->x] = other; // Place the attacker back in its previous cell
                        world->lastCollisionMessage = other->draw() + std::string(" was reflected by ") + draw();
                    } else {
                        // Default collision behavior for stronger organisms
                        Animal::collision(other);
                    }
                }
            };

            class Antelope : public Animal {
                public:
                    Antelope(int x, int y, World* w) : Animal(4, 4, x, y, w) {} // Example stats: strength=3, initiative=2
                    char draw() override { return 'A'; } // Representation for Sheep
                    void action() override {
                        int directions[4][2] = {{0, -2}, {0, 2}, {-2, 0}, {2, 0}}; // UP, DOWN, LEFT, RIGHT
                        std::random_shuffle(std::begin(directions), std::end(directions)); // Randomize movement order

                        for (auto& dir : directions) {
                            int nx = x + dir[0];
                            int ny = y + dir[1];

                            if (nx >= 0 && ny >= 0 && nx < WIDTH && ny < HEIGHT) {
                                Organism* target = world->grid[ny][nx];
                                if (!target) { // Move if cell is empty
                                    world->grid[y][x] = nullptr;
                                    x = nx; y = ny;
                                    world->grid[y][x] = this;
                                    break; // Stop after moving
                                } else if (target->strength <= strength) { // Handle collision if weaker organism
                                    collision(target);
                                    if (alive) {
                                        world->grid[y][x] = nullptr;
                                        x = nx; y = ny;
                                        world->grid[y][x] = this;
                                    }
                                    break; // Stop after moving
                                }
                            }
                        }
                        age++;
                    }
                };
            


void Human::action() {
    int nx = x + dx, ny = y + dy;
    dx = dy = 0;
    if (nx >= 0 && ny >= 0 && nx < WIDTH && ny < HEIGHT) {
        if (world->grid[ny][nx]) collision(world->grid[ny][nx]);
        if (alive) {
            world->grid[y][x] = nullptr;
            x = nx; y = ny;
            world->grid[y][x] = this;
        }
    }
    if (abilityDuration > 0) abilityDuration--;
    else if (abilityCooldown > 0) abilityCooldown--;
    else if (abilityDuration == 0 && abilityCooldown == 0 && abilityDuration < 5) abilityCooldown = 5;
    age++;
}

void World::makeTurn() {
    std::sort(organisms.begin(), organisms.end(), [](Organism* a, Organism* b) {
        if (a->initiative != b->initiative) return a->initiative > b->initiative;
        return a->age > b->age;
    });

    for (auto org : organisms) {
        if (org->isAlive()) org->action();
    }

    organisms.erase(std::remove_if(organisms.begin(), organisms.end(), [](Organism* o) { return !o->isAlive(); }), organisms.end());
}

void World::drawWorld() {
    system("clear");
    std::cout << "Oskar Putz 203475\n\n";
    for (int y = 0; y < HEIGHT; y++) {
        for (int x = 0; x < WIDTH; x++) {
            if (grid[y][x]) std::cout << grid[y][x]->draw();
            else std::cout << '.';
        }
        std::cout << '\n';
    }
    std::cout << "\n" << lastCollisionMessage << "\n";
    char key = getch();
}


int main() {
    srand((unsigned)time(0));
    World world;

    Human* human = new Human(5, 5, &world);
    world.addOrganism(human);

    //for (int i = 0; i < 5; ++i) world.addOrganism(new Sheep(rand() % WIDTH, rand() % HEIGHT, &world));
    for (int i = 0; i < 5; ++i) world.addOrganism(new Wolf(rand() % WIDTH, rand() % HEIGHT, &world));
    for (int i = 0; i < 5; ++i) world.addOrganism(new Fox(rand() % WIDTH, rand() % HEIGHT, &world));
    for (int i = 0; i < 5; ++i) world.addOrganism(new Turtle(rand() % WIDTH, rand() % HEIGHT, &world));
    for (int i = 0; i < 5; ++i) world.addOrganism(new Antelope(rand() % WIDTH, rand() % HEIGHT, &world));

    while (true) {
        world.drawWorld();
        char key = getch();
        if (key == 'q') break;
        if (key == 'w') human->setDirection(0, -1); // UP
        else if (key == 's') human->setDirection(0, 1); // DOWN
        else if (key == 'a') human->setDirection(-1, 0); // LEFT
        else if (key == 'd') human->setDirection(1, 0); // RIGHT
        else if (key == 'p') human->activateAbility();

        world.makeTurn();
    }

    return 0;
}
