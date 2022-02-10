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
  color clr= #FFFFFF;
  Shooter() {
    pos= new PVector(width/2, height/2);
    vel= new PVector();
    acc= new PVector();
    dir= PVector.fromAngle(theta);
  }
  void fire() {
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
  void thrust() {
    if (int(fuel)>0 && vel.mag()<MAXVELOCITY) {
      fuel--;
      applyForce(dir.copy().setMag(THRUST/100));
    }
  }
  void takeItem(Item i) {
    if (i.type=='f') fuelStock+= FUEL/5;
    else if(i.type=='h') healthStock+= SHOOTERHEALTH/10;
    else if(i.type=='b') boostTime+= 500;
    else if(i.type=='s') shildTime+= 500;
  }
  void applyForce(PVector f) {
    acc.add(f.copy());
  }
  void update() {
    reloading++;
    if(boostTime>0) boostTime--;
    if(shildTime>0) shildTime--;
    if (reloading>LASERRELOADTIME*20 && ammos<AMMO) ammos+=.2;
    if (fuel<FUEL) fuel+=.03;

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
    vel.mult(.99);
    acc.mult(0);

    for (Laser l : lasers) {
      l.update();
    }
  }
  void show() {
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
    stroke(#08DCFF);
    if(shildTime>0) ellipse(0, 0, r*2.2, r*2.2);
    popMatrix();
  }
}
