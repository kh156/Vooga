package demo.custom;

import java.util.List;
import com.golden.gamedev.GameObject;
import core.characters.Character;
import core.characters.GameElement;
import core.characters.ai.DeadState;
import core.characters.ai.State;
import core.physicsengine.physicsplugin.PhysicsAttributes;
import demo.custom.ShellState;

/**
 * @author Eric Mercer (JacenLakiir)
 */
@SuppressWarnings("serial")
public class Koopa extends Character {

    private static final String IMAGE_FILE = "resources/Koopa.png";
    private static final String SHELL_IMAGE_FILE = "resources/KoopaShell.png";

    private ShellState myShellState;

    public Koopa(GameObject game, PhysicsAttributes physicsAttribute) {
        super(game, physicsAttribute);
        setImages(game.getImages(IMAGE_FILE, 1, 1));
        setMovable(true);
        myShellState = new ShellState(this);
        myShellState.setActive(false);
        addPossibleState(myShellState);
        setTag("Koopa");
    }

    public Koopa(GameObject game, PhysicsAttributes physicsAttribute, List<State> possibleStates) {
        super(game, physicsAttribute);
        setTag("Koopa");
    }

    @Override
    public void afterHitFromLeftBy(GameElement e, String tag) {
        if (tag.equals("Mario")) {
            if (myShellState.isActive())
                handleMarioCollision((Mario)e, true);
        }
        else if (tag.equals("Goomba")) {
            setDirection(myShellState.isActive() ? getDirection() : -1
                    * getDirection());
        }
        else if (tag.equals("Koopa")) {
            handleKoopaCollision((Koopa)e, true);
        }
    }

    @Override
    public void afterHitFromRightBy(GameElement e, String tag) {
        if (tag.equals("Mario")) {
            if (myShellState.isActive())
                handleMarioCollision((Mario)e, false);
        }
        else if (tag.equals("Goomba")) {
            setDirection(myShellState.isActive() ? getDirection() : -1
                    * getDirection());
        }
        else if (tag.equals("Koopa")) {
            handleKoopaCollision((Koopa)e, false);
        }

    }

    @Override
    public void afterHitFromTopBy(GameElement e, String tag) {
        if (tag.equals("Mario")) {
            if (!myShellState.isActive()) {
                setImages(getGame().getImages(SHELL_IMAGE_FILE, 1, 1));
                deactivateAllOtherStates(myShellState);
                myShellState.setActive(true);
            }
            myShellState.setSpeed(0);
        }
    }



    public double getShellSpeed() {
        return myShellState.getSpeed();
    }

    public void setShellSpeed(double speed) {
        myShellState.setSpeed(speed);
    }

    public boolean isInShellState() {
        return myShellState.isActive();
    }

    private void handleMarioCollision(Mario m, boolean isHitOnLeft) {
        boolean isInShell = myShellState.isActive();
        if (isInShell && getShellSpeed() == 0) {
            myShellState.setSpeed(50 * Math.abs(m.getVelocity().getX()));
            setDirection(isHitOnLeft ? 1 : -1);
        } else if (isInShell && getShellSpeed() != 0) {
            m.updateAttributeValue("hitPoints",
                    -1 * m.getMyAttributeValue("hitPoints"));
        } else if (!isInShell) {
            m.updateAttributeValue("hitPoints",
                    -1 * m.getMyAttributeValue("hitPoints"));
        }
    }

    private void handleKoopaCollision(Koopa k, boolean isThisHitOnLeft) {
        if (myShellState.isActive()) {
            if (k.isInShellState() && k.getShellSpeed() != 0) {
                setShellSpeed(50 * Math.abs(k.getVelocity().getX()));
                setDirection(-1 * getDirection());
            } else if (k.isInShellState() && k.getShellSpeed() == 0)
                k.addPossibleState(new DeadState(this));
        } else {
            if (k.getShellSpeed() != 0)
                k.addPossibleState(new DeadState(this));
            else
                setDirection(isThisHitOnLeft ? 1 : -1);
        }
    }

}