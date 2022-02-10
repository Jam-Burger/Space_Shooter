class Item{
  int t=0;
  PVector pos;
  char type;
  Item(PVector p, char t){
    pos= p.copy();
    type= t;
  }
  void show(){
    t++;
    if(type=='f') fill(#C4A400);
    else if(type=='h') fill(0, 255, 255);
    else if(type=='b') fill(#FF1100);
    else if(type=='s') fill(#08DCFF);
    noStroke();
    if(type=='b') rect(pos.x, pos.y, 30, 30);
    else ellipse(pos.x, pos.y, 30, 30);
  }
}
