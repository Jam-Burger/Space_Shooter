import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Space_Shooter extends PApplet {

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
public void setup() {
  
  textAlign(CENTER, CENTER);
  colorMode(HSB);
  cursor(1);
  init();
}
public void draw() {
  rectMode(CENTER);
  background(0);
  t++;
  if (t%ASTEROIDPERIOD==0) {
    t=0;
    addAsteroid();
  }
  
  shooter.clr= 0xffFFFFFF;
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
      ast.clr= 0xffFFFFFF;
      for (int i= shooter.lasers.size()-1; i>=0; i--) {
        Laser laser= shooter.lasers.get(i);
        if (laser.hit(ast)) {
          ast.HP-=laser.power;
          ast.clr= 0xffFF0000;
          if (!laser.powered) {
            shooter.lasers.remove(i);
            laser= null;
          }

          if (ast.r>ASTEROIDSIZE*.75f && ast.HP<80 && !ast.carringItem && random(1)<.5f) {
            asteroids.remove(n);
            score += round(ast.r/10)*10;
            for (int q=0; q<random(1, 3); q++) {
              asteroids.add(new Asteroid(ast.pos.copy(), PVector.random2D().mult(random(ASTEROIDVELOCITY*4/ast.r)).copy(), ast.r*random(.4f, .6f)));
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
              else if (prob>2.5f) type= 's'; //2.5-4 = shild
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
  fill(0xff1000FF);
  rect(width/8f, height-90, map(shooter.ammos, AMMO, 0, width/4, 0), 20);
  fill(0xffC4A400);
  rect(width*5/8f, height-50, map(shooter.fuel, FUEL, 0, width/4, 0), 20);
}
public void init() {
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
public void mousePressed() {
  if (gameOver) {
    init();
  }
}
public void addAsteroid() {
  float r= ASTEROIDSIZE*random(.5f, 1.5f);
  float x= random(1)<.5f ? random(-width/2+r, -r*2) : random(width+r*2, width*1.5f-r);
  float y= random(1)<.5f ? random(-height/2+r, -r*2) : random(height+r*2, height*1.5f-r);

  PVector target= new PVector(random(width), random(height));
  PVector pos= new PVector(x, y);
  PVector vel= PVector.sub(target, pos).setMag(random(ASTEROIDVELOCITY*2/r));
  asteroids.add(new Asteroid(pos, vel, r));
}
class Asteroid {
  PVector pos, vel;
  float theta= random(0, PI/3);
  float omega;
  ArrayList<PVector> vertices= new ArrayList<PVector>();
  float r, HP;
  int clr= 0xffFFFFFF;
  boolean carringItem;
  Asteroid(PVector pos_, PVector vel_, float r_) {
    pos= pos_;
    vel= vel_;
    r= r_;
    HP= r*r/10;
    omega= random(-3/HP, 3/HP);
    carringItem= random(1)<.1f || (random(1)<.5f && (r<ASTEROIDSIZE*.7f || r>ASTEROIDSIZE*1.3f || vel.mag()>ASTEROIDVELOCITY));
    for (float alpha= 0; alpha<TAU; alpha+=random(TAU/12, TAU/5)) {
      PVector v= PVector.fromAngle(alpha).setMag(random(r*.8f, r*1.2f));
      vertices.add(v);
    }
  }
  public boolean edges() {
    return (pos.x>-width/2f && pos.x<width*1.5f && pos.y>-height/2f && pos.y<height*1.5f);
  }
  public void update() {
    pos.add(vel);
    theta+=omega;
    if (shooter.shildTime==0 && PVector.dist(shooter.pos, pos)<r+shooter.r) {
      shooter.HP--;
      shooter.clr= 0xffFF0000;
    }
  }
  public void show() {
    pushMatrix();
    translate(pos.x, pos.y);
    rotate(theta);
    strokeWeight(2);
    noFill();
    stroke(clr);
    beginShape();
    for (PVector v : vertices) {
      vertex(v.x, v.y);
    }
    endShape(CLOSE);
    popMatrix();
  }
}
class Item{
  int t=0;
  PVector pos;
  char type;
  Item(PVector p, char t){
    pos= p.copy();
    type= t;
  }
  public void show(){
    t++;
    if(type=='f') fill(0xffC4A400);
    else if(type=='h') fill(0, 255, 255);
    else if(type=='b') fill(0xffFF1100);
    else if(type=='s') fill(0xff08DCFF);
    noStroke();
    if(type=='b') rect(pos.x, pos.y, 30, 30);
    else ellipse(pos.x, pos.y, 30, 30);
  }
}
class Laser {
  PVector pos, vel;
  float theta;
  float power;
  boolean powered= false;
  Laser(PVector pos, float theta) {
    this.pos= pos.copy();
    this.theta= theta;
    vel= PVector.fromAngle(theta).mult(LASERSPEED);
    power= powered ? LASERPOWER/2 : LASERPOWER;
  }
  public boolean hit(Asteroid ast) {
    return PVector.dist(pos, ast.pos)<=ast.r;
  }
  public boolean edges() {
    return (pos.x>0 && pos.x<width && pos.y>0 && pos.y<height);
  }
  public void update() {
    pos.add(vel);
  }
  public void show() {
    pushMatrix();
    translate(pos.x, pos.y);
    if(powered) strokeWeight(7);
    else strokeWeight(5);
    stroke(0xff8DFF0D);
    if(powered) stroke(0xffFF1100);
    rotate(theta);
    if(powered) line(-10, 0, 10, 0);
    else line(-5, 0, 5, 0);
    popMatrix();
  }
}
class Shooter {
  PVector pos;
  PVector vel, acc, dir;
  ArrayList<Laser> lasers= new ArrayList<Laser>();
  float ammos= AMMO;
  float HP= SHOOTERHEALTH;
  int reloading= 0;
  float theta= -PI/2;
  float r= 30;
  int art= 0, boostTime= 0, shildTime= 0;
  float fuel= FUEL;
  float fuelStock= 0;
  float healthStock= 0;
  int clr= 0xffFFFFFF;
  Shooter() {
    pos= new PVector(width/2, height/2);
    vel= new PVector();
    acc= new PVector();
    dir= PVector.fromAngle(theta);
  }
  public void fire() {
    art++;
    if (t%LASERRELOADTIME==0 && ammos>0) {
      Laser laser= new Laser(pos, theta);
      lasers.add(laser);
      laser.powered= boostTime>0;
      ammos--;
      art=0;
      reloading= 0;
    }
  }
  public void thrust() {
    if (PApplet.parseInt(fuel)>0 && vel.mag()<MAXVELOCITY) {
      fuel--;
      applyForce(dir.copy().setMag(THRUST/100));
    }
  }
  public void takeItem(Item i) {
    if (i.type=='f') fuelStock+= FUEL/5;
    else if(i.type=='h') healthStock+= SHOOTERHEALTH/10;
    else if(i.type=='b') boostTime+= 500;
    else if(i.type=='s') shildTime+= 500;
  }
  public void applyForce(PVector f) {
    acc.add(f.copy());
  }
  public void update() {
    reloading++;
    if(boostTime>0) boostTime--;
    if(shildTime>0) shildTime--;
    if (reloading>LASERRELOADTIME*20 && ammos<AMMO) ammos+=.2f;
    if (fuel<FUEL) fuel+=.03f;

    if (HP>=SHOOTERHEALTH) healthStock= 0;
    else if (healthStock>0) {
      HP++;
      healthStock--;
    }
    if (fuel>=FUEL) fuelStock= 0;
    else if (fuelStock>0) {
      fuel++;
      fuelStock--;
    }
    theta= PVector.sub(new PVector(mouseX, mouseY), pos).heading();
    dir= PVector.fromAngle(theta);

    if (pos.x<0) pos.x=width;
    else if (pos.x>width) pos.x=0;
    if (pos.y<0) pos.y=height;
    else if (pos.y>height) pos.y=0;

    vel.add(acc);
    pos.add(vel);
    vel.mult(.99f);
    acc.mult(0);

    for (Laser l : lasers) {
      l.update();
    }
  }
  public void show() {
    for (Laser l : lasers) {
      l.show();
    }
    pushMatrix();
    translate(pos.x, pos.y);
    strokeWeight(2);
    fill(0);
    stroke(clr);
    rotate(theta);
    triangle(r, 0, -r, -r/2, -r, r/2);
    noFill();
    strokeWeight(5);
    stroke(0xff08DCFF);
    if(shildTime>0) ellipse(0, 0, r*2.2f, r*2.2f);
    popMatrix();
  }
}
  public void settings() {  fullScreen(); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Space_Shooter" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
