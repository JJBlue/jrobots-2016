import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

import jrobots.simulation.simulationObjects.JRobot2015_3;
import jrobots.utils.Angle;
import jrobots.utils.ProximityScan;
import jrobots.utils.SonarTrace;
import jrobots.utils.Vector;

public class Error404 extends JRobot2015_3{	
  private static final long serialVersionUID = 1L;
  
  private SonarTrace[] st = new SonarTrace[3];
  
  private int inter = 0;
  private int scanAbstand = 20;
  
  private ProximityScan oldPS = null;
  private ProximityScan oldPS2 = oldPS;
  
  private int schiessenAbAbstand = 55;
  private double ImmerSchiessenAbEnergie = 1.2;
  
  private double abstand = 70;
  private double minimalabstand = 15;
  //private int newDrive = 32;
  
  private int haufigkeitRakete = 70; //100 ca. Energie 1;
  private boolean fire = true;
  private int BoosterAbstandRakete = 40;
  //private double BoosterAbEnergieBenutzen = 0.40;
  
  private double lastHealth = 100;
  
  private double time = 0;
  
  private Vector lastTarget = null;
  
  //DO NOT CHANGE
  //private boolean areWeFire = false;
  
  public Error404() {    
    c.add(Color.black);
    c.add(Color.BLUE);
    c.add(Color.cyan);
    c.add(Color.darkGray);
    c.add(Color.green);
    c.add(Color.orange);
    c.add(Color.magenta);
    c.add(Color.gray);
    c.add(Color.orange);
    c.add(Color.pink);
    c.add(Color.red);
    c.add(Color.white);
    c.add(Color.lightGray);
    c.add(Color.yellow);
  }
  public ArrayList<Color> c = new ArrayList<Color>();

  @Override
  protected void actions() {
	this.drive();
	  
	this.mustfire();
	
	this.scanning();
    this.fire();    
    
    LebenRetten();
    
    drawDebug();
    
    //areWeFire = false;
  }
  
  private void LebenRetten(){
	  if(this.getHealth() <= lastHealth - 10){
		  BoosterAbstandRakete += 1;
		  //BoosterAbEnergieBenutzen -= 0.02;
		  
		  lastHealth = this.getHealth();
	  }
  }
  
  private void scanning(){
	  inter++;
	  if (inter % scanAbstand == 0 && this.getEnergy() >= 0.1) {
	      this.setSonarEnergy(0.1);
	  }
	  
	  if(oldPS != null){
		  oldPS2 = oldPS;
	  }
	  
	  if(oldPS != null && this.oldPS.timeOfScan <= this.getTime() - 15){
		  this.oldPS = this.getProjectileRadar();
	  }else if(this.getProjectileRadar() != null){
		  this.oldPS = this.getProjectileRadar();
	  }
	  
	  if (st[0] != this.getLastSonarTrace()) {
		  st[2] = st[1];
		  st[1] = st[0];
		  st[0] = this.getLastSonarTrace();
	  }

  }
  
  private int fireaktuell = 0;
  @SuppressWarnings("static-access")
  private void fire() {
	  if (st[0] != null && st[1] != null && this.getLastSonarTrace() != null) {
		  double distance = st[0].location.distanceTo(this.getPosition());
		  
		  if((haufigkeitRakete <= fireaktuell || distance <= minimalabstand) && fire == true){
		      if(st[0] != null && st[1] != null && st[0].location.distanceTo(this.getPosition()) <= schiessenAbAbstand || this.getEnergy() >= ImmerSchiessenAbEnergie){	 
		    		double m = (double) (st[1].location.getY() - st[0].location.getY()) / (st[1].location.getX() - st[0].location.getX());
		    				 
		    		double t = st[0].location.getY() - m * st[0].location.getX();
		    		
		    		if(m < Integer.MAX_VALUE && t < Integer.MAX_VALUE && m > Integer.MIN_VALUE && t > Integer.MIN_VALUE){				    				
		    			  double d = Math.abs(st[0].location.getX() - st[1].location.getX());
		    			  double dTime = Math.abs(st[0].timestamp - st[1].timestamp);		    			   		    			  
		    			  double time = distance / this.getProjectileSpeed();		    			  
		    			  double x = (double) st[0].location.getX() + dTime * (time / dTime) * d;
		    			  double y = (double) m * x + t;
		    			  
		    			  Vector v = new Vector(x, y);
		    			  Vector scanPos = v.sub(this.getPosition());
		    			  
		    			  this.addDebugLine(scanPos, this.getPosition());
		    			  this.lastTarget = scanPos;
		    			
		    			  this.setLaunchProjectileCommand(scanPos.getAngle());
		    		}
		    	  
		    	  //areWeFire = true;
		      }else if(st[0].location.distanceTo(st[1].location) <= 10){
			      Vector scanPos = st[0].location.sub(this.getPosition());
		    	
		    	  this.setLaunchProjectileCommand(scanPos.getAngle());
		    	  
		    	  this.lastTarget = null;
		    	  
		    	  //areWeFire = true;
		      }
		      
		      fireaktuell = 0;
		  }else{
			  fireaktuell++;
		  }
	  }else{
		  fireaktuell++;
	  }
  }
  
  private void mustfire(){
	  if((oldPS == null || oldPS.timeOfScan <= this.getTime() - 20) && this.getEnergy() <= 1.5){
		  fire = false;
	  }else{
		  fire = true;
	  }
  }
  
  private void drive() {
	  this.Ausweichen();
  }
  
  private void DriveElse(){
	  Random r = new Random();
	  double d = r.nextInt(360);
	  
	  drive(new Angle(d, "d"));
  }
  
  private void drawDebug() {
	  if (st[0] != null && st[1] != null &&st[2] != null) {
		  this.addDebugArrow(new Vector(st[2].location.getX(),  st[2].location.getY()),
		  			 new Vector(st[1].location.getX(), st[1].location.getY()));
		  this.addDebugArrow(new Vector(st[1].location.getX(),  st[1].location.getY()),
		 			 new Vector(st[0].location.getX(), st[0].location.getY()));
	  }
	  
	  if (st[0] != null && this.lastTarget != null) {
		  this.addDebugArrow(new Vector(st[0].location.getX(),  st[0].location.getY()),
		 			 		 this.lastTarget);
	  }
  }
  
  private void Ausweichen(){
	  if(st[0] != null || (oldPS != null && oldPS2 != null)){		  
		if(aufDerLinie()){
			//oldPS.pos ist egal
			driveCircle(oldPS.pos, 1);
		}else if(mine()){
			driveCircle(oldPS.pos, 1);
		}else if(st[0] != null){
			driveCircle(st[0].location);
		}else{
			DriveElse();
		}
	  }else{
		  DriveElse();
	  }
	  
	  allok();
  }
  
  private int nah = 100;
  private int weg = 80;
  private int ok = 90;
  private void driveCircle(Vector v){
	  driveCircle(v, 1);
  }
  private void driveCircle(Vector v, int speed){	  
	  Angle a = v.sub(this.getPosition()).getAngle();
	  
	  this.addDebugArrow(this.getPosition(), v);
	  
	  Angle c = null;
	  if(zuNah()){ 
		  c = new Angle(nah, "d");
		  speed = 1;
	  }else if(zuWeitWeg()){
		  c = new Angle(weg, "d");
		  speed = 1; 
	  }else{
		  c = a.add(new Angle(ok, "d"));
	  }
	  
	  if(HabLinie() && aufDerLinie() == false){
			if(this.getPosition().getY() < FlugraketeGetY(this.getPosition().getX())){
				drive(oldPS.pos.getAngle().sub(new Angle(180 - ok, "d")), DriveSpeed(1));
			}else if(this.getPosition().getY() > FlugraketeGetY(this.getPosition().getX())){
				drive(oldPS.pos.getAngle().add(new Angle(180 - ok, "d")), DriveSpeed(1));
			}else{
				drive(a.add(c), DriveSpeed(speed));
			}
	  }else if(aufDerLinie()){
		  if(this.getPosition().getAngle().angle <= a.add(new Angle(5, "d")).angle || this.getPosition().getAngle().angle >= a.sub(new Angle(5, "d")).angle){
			  drive(a.add(new Angle(10, "d")), DriveSpeed(2));
		  }else if(this.getPosition().getAngle() != a){
			  drive(this.getPosition().getAngle(), DriveSpeed(1));
		  }
		  
	  }else{
		drive(a.add(c), DriveSpeed(speed));
	  }
  }
  private void drive(Angle a){
	  this.setAutopilot(a, DriveSpeed());
  }
  private void drive(Angle a, double speed){
	  this.setAutopilot(a, speed);
  }

  private double DriveSpeed(){
	  return DriveSpeed(-1);
  }
  private double DriveSpeed(double speed){
	//int i = (int)((Math.random()) * (maxspeed - minspeed + 1) + minspeed);
	 //double d = (double) i / 10;
	  
	  if(oldPS != null){
		  if(oldPS.pos.distanceTo(this.getPosition()) <= BoosterAbstandRakete){// || this.getEnergy() >= BoosterAbEnergieBenutzen){// && areWeFire == false){			 
			  if(aufDerLinie()){
				  this.setBoost();
				  
				  return 1;
			  }
		  }
	  }else if(speed == 2){
		  this.setBoost();
		  
		  return 1;
	  }
	  
	  if(aufDerLinie()  || zuNah()  || zuWeitWeg()){
		  return 1;
	  }else{
		  if(time < 3){
			  time = this.getTime();
			  return 1;
		  }else{
			  if(speed != -1){
				  return speed;
			  }
			  
			  return 0.5;
		  }
	  }
  }
  
  private double FlugraketeGetY(double x){
	  if(oldPS != null && oldPS2 != null){		 
			double m = (double) (oldPS2.pos.getY() - oldPS.pos.getY()) / (oldPS2.pos.getX() - oldPS.pos.getX());
				 
			double t = oldPS.pos.getY() - m * oldPS.pos.getX();
				 
			if(m < Integer.MAX_VALUE && t < Integer.MAX_VALUE && m > Integer.MIN_VALUE && t > Integer.MIN_VALUE){
				this.addDebugLine(new Vector(-1000, m*-1000 + t), new Vector(1000, m*1000 + t));
			
				return m*x + t;
			}
	  }
	  
	  return 0;
  }
  private boolean HabLinie(){
	  if(oldPS != null && oldPS2 != null){
		return true;
	  }
	  return false;
  }
  private boolean aufDerLinie(){
	  if(oldPS != null && oldPS2 != null){		 
		double m = (double) (oldPS2.pos.getY() - oldPS.pos.getY()) / (oldPS2.pos.getX() - oldPS.pos.getX());
		double t = oldPS.pos.getY() - m * oldPS.pos.getX();
			 
		double y = m * this.getPosition().getX() + t;
				
		if(m < Integer.MAX_VALUE && t < Integer.MAX_VALUE && m > Integer.MIN_VALUE && t > Integer.MIN_VALUE){		
			this.addDebugLine(new Vector(-1000, m*-1000 + t), new Vector(1000, m*1000 + t));
				 
			if(st[0] != null && oldPS.pos.getX() > st[0].location.getX() && oldPS.pos.getX() > this.getPosition().getX() && oldPS.pos.getY() > st[0].location.getY() && oldPS.pos.getY() > this.getPosition().getY()){
				return false;
			}else if(st[0] != null && oldPS.pos.getX() < st[0].location.getX() && oldPS.pos.getX() < this.getPosition().getX() && oldPS.pos.getY() < st[0].location.getY() && oldPS.pos.getY() < this.getPosition().getY()){
				return false;
			}else if(this.getPosition().distanceTo(new Vector(this.getPosition().getX(), y)) <= Error404.getJRobotLength() / 2){
				ChangeColor(Color.red);
				
				return true;
			}
		}
	  }
	  
	  return false;
  }
  private boolean mine(){
	  if(oldPS != null){
		  int y = 15;
		  
		  if(oldPS.pos.distanceTo(this.getPosition()) <= y){
				ChangeColor(Color.orange);
				return true;
		}
	 }
		  
	  return false;
  }
  private boolean zuNah(){
	  if(st[0] != null){
		  double a = st[0].location.distanceTo(this.getPosition());
		  
		  if(a < abstand){
			  ChangeColor(Color.yellow);
			  return true;
		  }
	  }
	  return false;
  }
  private boolean zuWeitWeg(){
	  if(st[0] != null){
		  double a = st[0].location.distanceTo(this.getPosition());
		  
		  if(a > abstand + 10){
			  ChangeColor(Color.blue);
			  return true;
		  }
	  }
	  return false;
  }
  private void allok(){
	  if(mine() == false && aufDerLinie() == false && zuNah() == false && zuWeitWeg() == false){
		  ChangeColor(Color.green);
	  }
  }
  private void ChangeColor(Color c){
	  this.setBodyColor(c);
	  this.setTurretColor(c);
	  this.setNameColor(c);
  }
  @SuppressWarnings("unused")
  private void ChangeColor(Color c1, Color c2, Color c3){
	  this.setBodyColor(c1);
	  this.setTurretColor(c2);
	  this.setNameColor(c3);
  }
}
