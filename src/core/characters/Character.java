/**
 * @author Kuang Han
 */

package core.characters;

import java.util.HashMap;
import java.util.Map;
import levelio.annotations.Modifiable;

import com.golden.gamedev.GameObject;

import core.physicsengine.physicsplugin.PhysicsAttributes;

@SuppressWarnings("serial")
public abstract class Character extends GameElement {

    @Modifiable(classification = "Gameplay")
    protected double myHitPoints;

    protected transient Map<String, Double> myStateValues, myBaseValues;

    public Character(GameObject game, PhysicsAttributes physicsAttribute) {
	super(game, physicsAttribute);
	myBaseValues = new HashMap<String, Double>();
	myStateValues = new HashMap<String, Double>();
    }

    public Character() {
	super();
    }

    public void addState(String attribute, double defaultValue) {
	myBaseValues.put(attribute, defaultValue);
	myStateValues.put(attribute, myBaseValues.get(attribute));

    }

    public void updateBaseValues(String attribute, double newValue) {
	if (!myBaseValues.containsKey(attribute)) {
	    myBaseValues.put(attribute, (double) 0);
	}
	myBaseValues.put(attribute, myBaseValues.get(attribute) + newValue);
	myStateValues.put(attribute, myBaseValues.get(attribute));
    }

    public double getMyBaseValue(String attribute) {
	return myBaseValues.get(attribute);
    }

    public void updateStateValues(String attribute, double newValue) {
	if (!myBaseValues.containsKey(attribute)) {
	    myBaseValues.put(attribute, (double) 0);
	}
	myStateValues.put(attribute, myBaseValues.get(attribute) + newValue);
    }

    public double getMyStateValue(String attribute) {
	return myStateValues.get(attribute);
    }

    public double getMyHP() {
	return myHitPoints;
    }

    public void setMyHP(double hp) {
	myHitPoints += hp;
    }

}
