class Asteroid {
  PVector pos, vel;
  float theta= random(0, PI/3);
  float omega;
  ArrayList<PVector> vertices= new ArrayList<PVector>();
  float r, HP;
  color clr= #FFFFFF;
  boolean carringItem;
  Asteroid(PVector pos_, PVector vel_, float r_) {
    pos= pos_;
    vel= vel_;
    r= r_;
    HP= r*r/10;
    omega= random(-3/HP, 3/HP);
    carringItem= random(1)<.1 || (random(1)<.5 && (r<ASTEROIDSIZE*.7 || r>ASTEROIDSIZE*1.3 || vel.mag()>ASTEROIDVELOCITY));
    for (float alpha= 0; alpha<TAU; alpha+=random(TAU/12, TAU/5)) {
      PVector v= PVector.fromAngle(alpha).setMag(random(r*.8, r*1.2));
      vertices.add(v);
    }
  }
  boolean edges() {
    return (pos.x>-width/2f && pos.x<width*1.5 && pos.y>-height/2f && pos.y<height*1.5);
  }
  void update() {
    pos.add(vel);
    theta+=omega;
    if (shooter.shildTime==0 && PVector.dist(shooter.pos, pos)<r+shooter.r) {
      shooter.HP--;
      shooter.clr= #FF0000;
    }
  }
  void show() {
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
