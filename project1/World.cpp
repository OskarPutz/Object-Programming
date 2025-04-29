#include <iostream>
#include <vector>
#include <algorithm>
#include <ctime>
#include <cstdlib>
#include <termios.h>
#include <unistd.h>

using namespace std;

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

class Animal : public Organism {
    bool move = true;
public:
    Animal(int str, int init, int x, int y, World* w) : Organism(str, init, x, y, w) {}
    virtual ~Animal() = default;
    virtual Animal* clone(int x, int y) const = 0;
    void action() override;
    void collision(Organism* other) override;

    char draw() override { return 'A'; } 
};

class Plant : public Organism {
public:
    Plant(int str, int x, int y, World* w) : Organism(str, 0, x, y, w) {}
    virtual ~Plant() = default;

    void action() override {}
    void collision(Organism* other) override { alive = false; }
    char draw() override { return 'P'; }
};

class Human : public Animal {
    int dx = 0, dy = 0;

public:
    int abilityCooldown = 0;
    int abilityDuration = 0;
    Human(int x, int y, World* w) : Animal(5, 4, x, y, w) {}
    Animal* clone(int x, int y) const {
        return new Human(x, y, world);
    }
    void setDirection(int _dx, int _dy) { dx = _dx; dy = _dy; }
    void activateAbility();
    void action() override;
    char draw() override { return 'H'; }
};

class World {
    public:
        std::vector<Organism*> organisms;
        std::vector<Organism*> newOrganisms;
        Organism* grid[HEIGHT][WIDTH] = {nullptr};
        std::string lastCollisionMessage = "No collisions yet."; 
        Human* human = nullptr; 
    
        void addOrganism(Organism* org) {
            organisms.push_back(org);
            grid[org->y][org->x] = org;
        }
        
        void queueOrganism(Organism* org) {
            newOrganisms.push_back(org); 
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
    if (alive && move){
        world->grid[y][x] = nullptr;
        x = nx; y = ny;
        world->grid[y][x] = this;
    }
    age++;
    move = true;
}
void Animal::collision(Organism* other) {
    if (typeid(*other) == typeid(*this)) {
        move = false;
        int dirs[8][2] = { {0,1}, {1,0}, {0,-1}, {-1,0}, {1,1}, {1,-1}, {-1,1}, {-1,-1} };
        std::random_shuffle(std::begin(dirs), std::end(dirs));
        for (auto& dir : dirs) {
            int nx = x + dir[0];
            int ny = y + dir[1];
            if (nx >= 0 && ny >= 0 && nx < WIDTH && ny < HEIGHT && world->grid[ny][nx] == nullptr) {
                Animal* thisAnimal = dynamic_cast<Animal*>(this);
                if (thisAnimal) {
                    Animal* child = thisAnimal->clone(nx, ny);
                    child->move = false;
                    if (child) {
                        world->queueOrganism(child);
                        world->lastCollisionMessage = draw() + std::string(" reproduced at (") + std::to_string(nx) + ", " + std::to_string(ny) + ")";
                    }
                }
                break;
            }
        }
        return;
    }
    
    if(other->draw() == 'h'){
        world->lastCollisionMessage = draw() + std::string(" was killed by Hogweed at (") +
                                      std::to_string(other->x) + ", " + std::to_string(other->y) + ")";
        alive = false;
        world->grid[y][x] = nullptr;
        return;
    }
    if (other->draw() == 'A'){
        int escapeChance = rand() % 2;
        if (escapeChance == 0) {
            int dirs[4][2] = { {0,1}, {1,0}, {0,-1}, {-1,0} };
            std::random_shuffle(std::begin(dirs), std::end(dirs));
            for (auto& dir : dirs) {
                int nx = other->x + dir[0];
                int ny = other->y + dir[1];
                if (nx >= 0 && ny >= 0 && nx < WIDTH && ny < HEIGHT && world->grid[ny][nx] == nullptr) {
                    world->grid[ny][nx] = other;
                    world->lastCollisionMessage = other->draw() + std::string(" escaped from ") + draw() + std::string("at (") +
                    std::to_string(other->x) + ", " + std::to_string(other->y) + ")";
                    break;
                }
            }
            return; 
        }
    }
    if (other->draw() == 'T'){
        if (strength < 5) {
            move = false;
            world->lastCollisionMessage = draw() + std::string(" was deflected by Turtle at (") + std::to_string(other->x) + ", " + std::to_string(other->y) + ")";
            return;
        }
    }
    if (other->draw() == 'g'){
        strength += 3;
        world->lastCollisionMessage = draw() + std::string(" ate Guarana and gained 3 strength! ");
        other->alive = false;
        return;
    }
    if (other->strength > strength) {
        world->lastCollisionMessage = other->draw() + std::string(" killed ") + draw() + 
                                      " at (" + std::to_string(x) + ", " + std::to_string(y) + ")";
        alive = false;
        world->grid[y][x] = nullptr; 
    } else {
        world->lastCollisionMessage = draw() + std::string(" killed ") + other->draw() + 
                                      " at (" + std::to_string(other->x) + ", " + std::to_string(other->y) + ")";
        other->alive = false;
        world->grid[other->y][other->x] = nullptr; 
    }
};

class Sheep : public Animal {
    public:
        Sheep(int x, int y, World* w) : Animal(4, 4, x, y, w) {}
        char draw() override { return 'S'; } 
        Animal* clone(int x, int y) const override{
            return new Sheep(x, y, world);
        }
    };

class Wolf : public Animal {
    public:
        Wolf(int x, int y, World* w) : Animal(9, 5, x, y, w) {}
        char draw() override { return 'W'; } 
        Animal* clone(int x, int y) const override{
            return new Wolf(x, y, world);
        }
    };

class Fox : public Animal {
    public:
        Fox(int x, int y, World* w) : Animal(3, 7, x, y, w) {}
        
        char draw() override { return 'F'; } 
        Animal* clone(int x, int y) const override{
            return new Fox(x, y, world);
        }
        
        void action() override {
            int directions[4][2] = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}}; 
            std::random_shuffle(std::begin(directions), std::end(directions));
        
            for (auto& dir : directions) {
                int nx = x + dir[0];
                int ny = y + dir[1];
        
                if (nx >= 0 && ny >= 0 && nx < WIDTH && ny < HEIGHT) {
                    Organism* target = world->grid[ny][nx];
                    if (!target || target->strength <= strength) { 
                        if (target) collision(target); 
                        if (alive) {
                            world->grid[y][x] = nullptr;
                            x = nx; y = ny;
                            world->grid[y][x] = this;
                        }
                        break;
                    }
                }
            }
            age++;
        }
};

class Turtle : public Animal {
    public:
        Turtle(int x, int y, World* w) : Animal(2, 1, x, y, w) {}
        char draw() override { return 'T'; } 
        Animal* clone(int x, int y) const override{
            return new Turtle(x, y, world);
        }
        void action() override {
            int chance = rand() % 4;
            if (chance == 0) {
                Animal::action(); 
            }
            age++;
        }
    };

class Antelope : public Animal {
    public:
        Antelope(int x, int y, World* w) : Animal(4, 4, x, y, w) {} 
        char draw() override { return 'A'; } 
        Animal* clone(int x, int y) const override{
            return new Antelope(x, y, world);
        }
        void action() override {
            int directions[4][2] = {{0, -2}, {0, 2}, {-2, 0}, {2, 0}}; 
            std::random_shuffle(std::begin(directions), std::end(directions));

            for (auto& dir : directions) {
                int nx = x + dir[0];
                int ny = y + dir[1];

                if (nx >= 0 && ny >= 0 && nx < WIDTH && ny < HEIGHT) {
                        if (alive) {
                            world->grid[y][x] = nullptr;
                            x = nx; y = ny;
                            world->grid[y][x] = this;
                                }
                            break; 
                    }
                }
        age++;
        }
    };
            

class Grass : public Plant {
    public:
        Grass(int x, int y, World* w) : Plant(0, x, y, w) {} 
        
        char draw() override { return 'G'; }
        void action() override {
            if (rand() % 10 == 0) { 
                int nx = x + (rand() % 3 - 1); 
                int ny = y + (rand() % 3 - 1);
                if (nx >= 0 && ny >= 0 && nx < WIDTH && ny < HEIGHT && world->grid[ny][nx] == nullptr) {
                    world->queueOrganism(new Grass(nx, ny, world));
                }
            }
        }
    };

class SowThistle : public Plant {
    public:
        SowThistle(int x, int y, World* w) : Plant(0, x, y, w) {} 
        
        char draw() override { return 's'; } 
        void action() override {
            for (int i = 0; i < 3; ++i) { 
                if (rand() % 10 == 0) { 
                    int nx = x + (rand() % 3 - 1);
                    int ny = y + (rand() % 3 - 1);
                    if (nx >= 0 && ny >= 0 && nx < WIDTH && ny < HEIGHT && world->grid[ny][nx] == nullptr) {
                        world->queueOrganism(new Grass(nx, ny, world));
                    }
                }
            }
        }
    };
    
class Guarana : public Plant {
    public:
        Guarana(int x, int y, World* w) : Plant(0, x, y, w) {} 
        char draw() override { return 'g'; }
        void action() override {
            if (rand() % 10 == 0) { 
                int nx = x + (rand() % 3 - 1); 
                int ny = y + (rand() % 3 - 1); 
                if (nx >= 0 && ny >= 0 && nx < WIDTH && ny < HEIGHT && world->grid[ny][nx] == nullptr) {
                    world->queueOrganism(new Guarana(nx, ny, world));
                }
            }
        }
    };

class Belladonna : public Plant {
    public:
        Belladonna(int x, int y, World* w) : Plant(99, x, y, w) {} 
                    
        char draw() override { return 'b'; } 
        void action() override {
            if (rand() % 10 == 0) { 
                int nx = x + (rand() % 3 - 1); 
                int ny = y + (rand() % 3 - 1); 
                if (nx >= 0 && ny >= 0 && nx < WIDTH && ny < HEIGHT && world->grid[ny][nx] == nullptr) {
                    world->queueOrganism(new Belladonna(nx, ny, world));
                }
            }
        }
    };

class Hogweed : public Plant {
    public:
        Hogweed(int x, int y, World* w) : Plant(10, x, y, w) {}
        
        char draw() override { return 'h'; }
        void action() override {
            for (int dx = -1; dx <= 1; ++dx) {
                for (int dy = -1; dy <= 1; ++dy) {
                    int nx = x + dx;
                    int ny = y + dy;
                    if (nx >= 0 && ny >= 0 && nx < WIDTH && ny < HEIGHT) {
                        Organism* target = world->grid[ny][nx];
                        if (target && dynamic_cast<Animal*>(target)) {
                            target->alive = false;
                            world->grid[ny][nx] = nullptr;
                            world->lastCollisionMessage = draw() + std::string(" killed ") + target->draw() +
                                                          " at (" + std::to_string(nx) + ", " + std::to_string(ny) + ")";
                        }
                    }
                }
            }
        }
    };
void Human::action() {
    int nx = x + dx, ny = y + dy;
    dx = dy = 0;
    if (nx >= 0 && ny >= 0 && nx < WIDTH && ny < HEIGHT) {
        if (world->grid[ny][nx] && (nx != x || ny != y)) collision(world->grid[ny][nx]);
        if (alive) {
            world->grid[y][x] = nullptr;
            x = nx; y = ny;
            world->grid[y][x] = this;
        }
    }
    if (abilityCooldown > 0) abilityCooldown--;
    if (abilityDuration > 0) {
        abilityDuration--;
        strength--;
    }

    age++;
}

void Human::activateAbility() {
    if (abilityCooldown == 0) {
        abilityDuration = 6; 
        abilityCooldown = 11; 
        world->lastCollisionMessage = "Ability activated!";
        strength += 6;
    } 
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
    for (auto org : newOrganisms) {
        addOrganism(org);
    }
    newOrganisms.clear();
}

void World::drawWorld() {
    system("clear");
    std::cout << "Oskar Putz ID: 203475 \n";
    if (human->abilityDuration > 0){
        std::cout << "Ability status: ON\n";
    } else {
    std::cout << "Ability status: OFF\n";
    }
    std::cout << "Current strength: " << human->strength << "\n";
    for (int y = 0; y < HEIGHT; y++) {
        for (int x = 0; x < WIDTH; x++) {
            if (grid[y][x]) std::cout << grid[y][x]->draw();
            else std::cout << '.';
        }
        std::cout << '\n';
    }
    std::cout << "\n" << lastCollisionMessage << "\n";
}


int main() {
    srand((unsigned)time(0));
    World world;
    world.human = new Human(0, 0, &world);
    world.addOrganism(world.human);

    for (int i = 0; i < 2; ++i) world.addOrganism(new Sheep(rand() % WIDTH, rand() % HEIGHT, &world));
    for (int i = 0; i < 2; ++i) world.addOrganism(new Wolf(rand() % WIDTH, rand() % HEIGHT, &world));
    for (int i = 0; i < 2; ++i) world.addOrganism(new Fox(rand() % WIDTH, rand() % HEIGHT, &world));
    for (int i = 0; i < 2; ++i) world.addOrganism(new Turtle(rand() % WIDTH, rand() % HEIGHT, &world));
    for (int i = 0; i < 2; ++i) world.addOrganism(new Antelope(rand() % WIDTH, rand() % HEIGHT, &world));
    for (int i = 0; i < 2; ++i) world.addOrganism(new Grass(rand() % WIDTH, rand() % HEIGHT, &world));
    for (int i = 0; i < 2; ++i) world.addOrganism(new SowThistle(rand() % WIDTH, rand() % HEIGHT, &world));
    for (int i = 0; i < 2; ++i) world.addOrganism(new Guarana(rand() % WIDTH, rand() % HEIGHT, &world));
    for (int i = 0; i < 2; ++i) world.addOrganism(new Belladonna(rand() % WIDTH, rand() % HEIGHT, &world));
    for (int i = 0; i < 2; ++i) world.addOrganism(new Hogweed(rand() % WIDTH, rand() % HEIGHT, &world));

    while (true) {
        world.drawWorld();
        char key = getch();
        if (key == 'q') break;
        if (key == '\033') {
            getch();
            switch (getch()) {
                case 'A': world.human->setDirection(0, -1); break; // UP
                case 'B': world.human->setDirection(0, 1); break;  // DOWN
                case 'C': world.human->setDirection(1, 0); break;  // RIGHT
                case 'D': world.human->setDirection(-1, 0); break; // LEFT
            }
        } else if (key == 'p') {
            world.human->activateAbility();
        }
        world.makeTurn();
    }

    return 0;
}
