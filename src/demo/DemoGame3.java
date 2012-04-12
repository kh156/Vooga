/**
 * @author Glenn Rivkees (grivkees)
 */

package demo;

import game.AdvancedPlayField;
import game.KeepLeftFirstPlayerGameScroller;
import items.CollectibleInstantItem;
import items.CollectibleInventoryItem;
import items.CollectibleItem;
import items.CollectibleTimelapseItem;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.List;

import tiles.BaseTile;
import tiles.BreakableDecorator;
import tiles.ItemDecorator;
import tiles.MovingDecorator;
import tiles.Tile;

import keyconfiguration.Key;
import keyconfiguration.KeyConfig;

import charactersprites.NPC;
import charactersprites.Player;
import charactersprites.ai.MoveState;
import collision.CharacterPlatformCollision;
import collision.PlayerCollectibleItemCollision;

import com.golden.gamedev.Game;
import com.golden.gamedev.GameLoader;
import com.golden.gamedev.object.background.ColorBackground;

public class DemoGame3 extends Game {

	private AdvancedPlayField myPlayfield;
	private List<Key> keyList;
	public void initResources() {
		// Playfield Init
		myPlayfield = new AdvancedPlayField(10000, 500);
		myPlayfield.setBackground(new ColorBackground(Color.gray));
		myPlayfield.setGameScroller(new KeepLeftFirstPlayerGameScroller());

		// Collisions
		myPlayfield.addCollisionGroup(myPlayfield.getPlayers(),
		        myPlayfield.getSetting(), new CharacterPlatformCollision());

		myPlayfield.addCollisionGroup(myPlayfield.getPlayers(),
		        myPlayfield.getItems(), new PlayerCollectibleItemCollision());

		// Sprite Init / Or load funcitonality
		// SpriteGroups already exist in AdvancedPlayfield
		// use addItem(sprite), addPlayer(), addCharacter(), or addSetting()

		Player temp = new Mario(this);
        keyList = new KeyConfig(temp, this).getKeyList();
		temp.setImages(this.getImages("resources/Mario1.png", 1, 1));
		temp.setLocation(25, 20);
		temp.setMyHP(10);
		myPlayfield.addPlayer(temp);

		Tile temp1 = new BaseTile(this);
		temp1.setImages(this.getImages("resources/Bar.png", 1, 1));
		temp1.setLocation(0, 440);
		myPlayfield.addSetting(temp1);

		Tile temp2 = new BaseTile(this);
		temp2.setImages(this.getImages("resources/Bar.png", 1, 1));
		temp2.setLocation(600, 440);
		myPlayfield.addSetting(temp2);

		Tile block2 = new BreakableDecorator(new BaseTile(this));
		block2.setMass(6);
		block2.setMovable(false);
		block2.setImages(this.getImages("resources/Block2Break.png", 8, 1));
		block2.setLocation(160, 200);
		myPlayfield.addSetting(block2);

		CollectibleInstantItem coin = new CollectibleInstantItem(this);
		coin.setImages(this.getImages("resources/Coin.png", 1, 1));
		coin.setActive(false);
		coin.setValue(3);
		System.out.println(coin.getAttackPower());
		myPlayfield.addItem(coin);

		CollectibleTimelapseItem poison = new CollectibleTimelapseItem(this);
		poison.setImages(this.getImages("resources/Poison.png", 1, 1));
		poison.setActive(true);
		poison.setMovable(false);
		poison.setLocation(300, 400);
		poison.setTimer(5);
		poison.setHitPoints(-1/5.0);
		myPlayfield.addItem(poison);
		
		ItemDecorator block1 = new ItemDecorator(new BaseTile(this));
		block1.setMass(6);
		block1.setMovable(false);
		block1.setImages(this.getImages("resources/Block1.png", 1, 1));
		block1.setLocation(100, 200);
		block1.addItem(coin);
		myPlayfield.addSetting(block1);
		
		Tile middleBar = new MovingDecorator(new BaseTile(this), 260,
		        240, 700, 60, 0.05);
		middleBar.setImages(getImages("resources/SmallBar.png", 1, 1));
		myPlayfield.addSetting(middleBar);

	}

	public void update(long arg0) {
		myPlayfield.update(arg0);
		checkKeyboardInput(arg0);
	}
	
	public void checkKeyboardInput(long milliSec) {
	        for(Key key : keyList){
	            if(key.isKeyDown(milliSec)){
	                key.notifyObserver();
	            }
	        }
	}
	
	public void render(Graphics2D arg0) {
		myPlayfield.render(arg0);
	}

	public static void main(String[] args) {
		GameLoader game = new GameLoader();
		game.setup(new DemoGame3(), new Dimension(640, 480), false);
		game.start();
	}

}
