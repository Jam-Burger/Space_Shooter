final float LASERSPEED= 30;
final float THRUST= 30;
final float MAXVELOCITY= 30;
final int LASERRELOADTIME= 7;
final float ASTEROIDPERIOD= 50;
final float ASTEROIDSIZE= 50;
final float ASTEROIDVELOCITY= 100;
final float SHOOTERHEALTH= 500;
final float AMMO= 500;
final float FUEL= 500;
final int FUELRELOADINGTIME= 100;
final int ITEMDELAY= 200;
final float LASERPOWER= 30;

boolean gameOver;
int t;
int score;
Shooter shooter;
ArrayList<Asteroid> asteroids;
ArrayList<Item> items;
void setup() {
  fullScreen();
  textAlign(CENTER, CENTER);
  colorMode(HSB);
  cursor(1);
  init();
}
void draw() {
  rectMode(CENTER);
  background(0);
  t++;
  if (t%ASTEROIDPERIOD==0) {
    t=0;
    addAsteroid();
  }
  
  shooter.clr= #FFFFFF;
  fill(255);
  stroke(255);
  textSize(height/30f);
  text("Score  :  "+score, width/15, height/20);
  if (!gameOver) {
    if (mousePressed) shooter.fire();
    if (keyPressed) {
      if (key==' ') shooter.thrust();
      else if (!mousePressed && keyCode==SHIFT) shooter.fire();
    }
    for (int n= asteroids.size()-1; n>=0; n--) {
      Asteroid ast= asteroids.get(n);
      if (!ast.edges()) {
        asteroids.remove(n);
        ast= null;
        continue;
      }
      ast.clr= #FFFFFF;
      for (int i= shooter.lasers.size()-1; i>=0; i--) {
        Laser laser= shooter.lasers.get(i);
        if (laser.hit(ast)) {
          ast.HP-=laser.power;
          ast.clr= #FF0000;
          if (!laser.powered) {
            shooter.lasers.remove(i);
            laser= null;
          }

          if (ast.r>ASTEROIDSIZE*.75 && ast.HP<80 && !ast.carringItem && random(1)<.5) {
            asteroids.remove(n);
            score += round(ast.r/10)*10;
            for (int q=0; q<random(1, 3); q++) {
              asteroids.add(new Asteroid(ast.pos.copy(), PVector.random2D().mult(random(ASTEROIDVELOCITY*4/ast.r)).copy(), ast.r*random(.4, .6)));
            }
            ast= null;
            break;
          }
          if (ast.HP <= 0) {
            asteroids.remove(n);
            score += round(ast.r/10)*10;
            if (ast.carringItem) {
              char type;
              float prob= random(10);
              if (prob>4) type= 'f'; //4-10 = fuel
              else if (prob>2.5) type= 's'; //2.5-4 = shild
              else if (prob>1) type= 'h'; //1-2.5 = health
              else type= 'b'; //0-1 = boost
              items.add(new Item(ast.pos, type));
            }
            ast= null;
            break;
          }
        }
      }
    }
  }

  for (int i= items.size()-1; i>=0; i--) {
    Item item= items.get(i);
    if (item.t==ITEMDELAY) {
      items.remove(i);
      item= null;
    } else if (PVector.dist(item.pos, shooter.pos)<shooter.r) {
      items.remove(i);
      shooter.takeItem(item);
      item= null;
    }
  }

  for (int i= shooter.lasers.size()-1; i>=0; i--) {
    Laser laser= shooter.lasers.get(i);
    if (!laser.edges()) {
      shooter.lasers.remove(i);
      laser= null;
    }
  }
  
  for (Asteroid ast : asteroids) {
    ast.update();
    ast.show();
  }
  for (Item item : items) {
    item.show();
  }
  if (!gameOver) shooter.update();
  shooter.show();

  if (shooter.HP<=0) {
    shooter.HP=0;
    noStroke();
    textSize(80);
    fill(0, 100);
    rect(width/2, height/2, width, height);
    fill(255);
    gameOver= true;
    text("GAME OVER", width/2, height/2, width/4, 100);
  }

  textSize(25);
  fill(255);
  text("Ammos", width/4f-width/6f, height-80, 100, 30);
  text("Health", width/4f-width/6f, height-40, 100, 30);
  text("Fuel", width*3/4f-width/6f, height-40, 100, 30);
  
  stroke(255);
  strokeWeight(2);
  fill(0);
  rect(width/4f, height-80, width/4, 20);
  rect(width/4f, height-40, width/4, 20);
  rect(width*3/4f, height-40, width/4, 20);
  fill(map(shooter.HP, SHOOTERHEALTH, 0, 90, 0), 255, 255);
  noStroke();
  rectMode(CORNER);
  rect(width/8f, height-50, map(shooter.HP, SHOOTERHEALTH, 0, width/4, 0), 20);
  fill(#1000FF);
  rect(width/8f, height-90, map(shooter.ammos, AMMO, 0, width/4, 0), 20);
  fill(#C4A400);
  rect(width*5/8f, height-50, map(shooter.fuel, FUEL, 0, width/4, 0), 20);
}
void init() {
  shooter= new Shooter();
  asteroids= new ArrayList<Asteroid>();
  items= new ArrayList<Item>();
  score= 0;
  t=0;
  gameOver= false;
  for (int n=0; n<10; n++) {
    addAsteroid();
  }
}
void mousePressed() {
  if (gameOver) {
    init();
  }
}
void addAsteroid() {
  float r= ASTEROIDSIZE*random(.5, 1.5);
  float x= random(1)<.5 ? random(-width/2+r, -r*2) : random(width+r*2, width*1.5-r);
  float y= random(1)<.5 ? random(-height/2+r, -r*2) : random(height+r*2, height*1.5-r);

  PVector target= new PVector(random(width), random(height));
  PVector pos= new PVector(x, y);
  PVector vel= PVector.sub(target, pos).setMag(random(ASTEROIDVELOCITY*2/r));
  asteroids.add(new Asteroid(pos, vel, r));
}
