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
  boolean hit(Asteroid ast) {
    return PVector.dist(pos, ast.pos)<=ast.r;
  }
  boolean edges() {
    return (pos.x>0 && pos.x<width && pos.y>0 && pos.y<height);
  }
  void update() {
    pos.add(vel);
  }
  void show() {
    pushMatrix();
    translate(pos.x, pos.y);
    if(powered) strokeWeight(7);
    else strokeWeight(5);
    stroke(#8DFF0D);
    if(powered) stroke(#FF1100);
    rotate(theta);
    if(powered) line(-10, 0, 10, 0);
    else line(-5, 0, 5, 0);
    popMatrix();
  }
}
